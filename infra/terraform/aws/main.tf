provider "aws" {
  region = var.region
}

# Include GitHub registry configuration
terraform {
  required_version = ">= 1.0.0"
  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = ">= 3.73.0, < 4.0.0"
    }
    github = {
      source  = "integrations/github"
      version = "~> 5.0"
    }
    null = {
      source  = "hashicorp/null"
      version = "~> 3.0"
    }
  }
}

module "vpc" {
  source  = "terraform-aws-modules/vpc/aws"
  version = "~> 3.0"

  name = "ev-saas-vpc"
  cidr = "10.0.0.0/16"

  azs             = ["${var.region}a", "${var.region}b", "${var.region}c"]
  private_subnets = ["10.0.1.0/24", "10.0.2.0/24", "10.0.3.0/24"]
  public_subnets  = ["10.0.101.0/24", "10.0.102.0/24", "10.0.103.0/24"]

  enable_nat_gateway   = true
  single_nat_gateway   = true
  enable_dns_hostnames = true

  tags = {
    "kubernetes.io/cluster/${var.cluster_name}" = "shared"
    Environment                                 = var.environment
    Project                                     = "ev-saas"
  }

  public_subnet_tags = {
    "kubernetes.io/cluster/${var.cluster_name}" = "shared"
    "kubernetes.io/role/elb"                    = "1"
  }

  private_subnet_tags = {
    "kubernetes.io/cluster/${var.cluster_name}" = "shared"
    "kubernetes.io/role/internal-elb"           = "1"
  }
}

# Create IAM policy for EKS to pull from GitHub Container Registry
resource "aws_iam_policy" "github_container_registry_access" {
  name        = "EKS-GitHub-Container-Registry-Access"
  description = "Policy to allow EKS to pull images from GitHub Container Registry"

  policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Effect = "Allow"
        Action = [
          "ecr:GetDownloadUrlForLayer",
          "ecr:BatchGetImage",
          "ecr:BatchCheckLayerAvailability",
          "ecr:PutImage",
          "ecr:InitiateLayerUpload",
          "ecr:UploadLayerPart",
          "ecr:CompleteLayerUpload",
          "ecr:GetAuthorizationToken"
        ]
        Resource = "*"
      }
    ]
  })
}

module "eks" {
  source  = "terraform-aws-modules/eks/aws"
  version = "~> 18.0"

  cluster_name    = var.cluster_name
  cluster_version = "1.23"

  vpc_id     = module.vpc.vpc_id
  subnet_ids = module.vpc.private_subnets

  cluster_endpoint_private_access = true
  cluster_endpoint_public_access  = true

  eks_managed_node_groups = {
    default_node_group = {
      min_size     = 2
      max_size     = 5
      desired_size = 3

      instance_types = ["t3.medium"]
      capacity_type  = "ON_DEMAND"

      # Attach the GitHub Container Registry access policy to the node group
      iam_role_additional_policies = {
        github_container_registry_access = aws_iam_policy.github_container_registry_access.arn
      }

      tags = {
        Environment = var.environment
        Project     = "ev-saas"
      }
    }
  }

  node_security_group_additional_rules = {
    ingress_self_all = {
      description = "Node to node all ports/protocols"
      protocol    = "-1"
      from_port   = 0
      to_port     = 0
      type        = "ingress"
      self        = true
    }
    egress_all = {
      description      = "Node all egress"
      protocol         = "-1"
      from_port        = 0
      to_port          = 0
      type             = "egress"
      cidr_blocks      = ["0.0.0.0/0"]
      ipv6_cidr_blocks = ["::/0"]
    }
  }

  tags = {
    Environment = var.environment
    Project     = "ev-saas"
  }
}

# Create Kubernetes Secret for GitHub Container Registry credentials using null_resource
resource "null_resource" "github_registry_credentials" {
  depends_on = [module.eks]

  provisioner "local-exec" {
    command = <<-EOT
      # Update kubeconfig to interact with the EKS cluster
      aws eks update-kubeconfig --region ${var.region} --name ${var.cluster_name}
      
      # Create Kubernetes secret with GitHub credentials
      kubectl create namespace ev-saas || true
      
      # Create a secret for GitHub container registry access
      kubectl create secret docker-registry github-registry-secret \
        --namespace=ev-saas \
        --docker-server=ghcr.io \
        --docker-username=${var.github_owner} \
        --docker-password=${var.github_token} \
        --docker-email=null || true
        
      # Add annotation to use this secret for pulling images
      kubectl patch serviceaccount default \
        --namespace=ev-saas \
        -p '{"imagePullSecrets": [{"name": "github-registry-secret"}]}' || true
    EOT
  }
}

module "rds" {
  source  = "terraform-aws-modules/rds/aws"
  version = "~> 3.0"

  identifier = "ev-saas-postgres"

  engine               = "postgres"
  engine_version       = "13.4"
  family               = "postgres13"
  major_engine_version = "13"
  instance_class       = "db.t3.medium"

  allocated_storage     = 20
  max_allocated_storage = 100

  name     = "evsaas_db"
  username = var.db_username
  password = var.db_password
  port     = 5432

  multi_az               = true
  subnet_ids             = module.vpc.private_subnets
  vpc_security_group_ids = [aws_security_group.rds.id]

  maintenance_window = "Mon:00:00-Mon:03:00"
  backup_window      = "03:00-06:00"

  backup_retention_period = 7

  tags = {
    Environment = var.environment
    Project     = "ev-saas"
  }
}

resource "aws_security_group" "rds" {
  name        = "ev-saas-rds-sg"
  description = "Allow traffic from EKS to RDS"
  vpc_id      = module.vpc.vpc_id

  ingress {
    description     = "PostgreSQL from EKS"
    from_port       = 5432
    to_port         = 5432
    protocol        = "tcp"
    security_groups = [module.eks.node_security_group_id]
  }

  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }

  tags = {
    Environment = var.environment
    Project     = "ev-saas"
  }
}

# Create a simple Redis instance directly with AWS resources instead of using the module
# that has version compatibility issues
resource "aws_elasticache_subnet_group" "redis" {
  name       = "ev-saas-cache-subnet"
  subnet_ids = module.vpc.private_subnets
}

resource "aws_elasticache_cluster" "redis" {
  cluster_id           = "ev-saas-redis"
  engine               = "redis"
  engine_version       = "6.x"
  node_type            = "cache.t3.small"
  num_cache_nodes      = 1
  parameter_group_name = "default.redis6.x"
  port                 = 6379

  subnet_group_name  = aws_elasticache_subnet_group.redis.name
  security_group_ids = [aws_security_group.redis.id]

  maintenance_window = "mon:03:00-mon:04:00"
  apply_immediately  = true

  tags = {
    Environment = var.environment
    Project     = "ev-saas"
  }
}

resource "aws_security_group" "redis" {
  name        = "ev-saas-redis-sg"
  description = "Allow traffic from EKS to Redis"
  vpc_id      = module.vpc.vpc_id

  ingress {
    description     = "Redis from EKS"
    from_port       = 6379
    to_port         = 6379
    protocol        = "tcp"
    security_groups = [module.eks.node_security_group_id]
  }

  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }

  tags = {
    Environment = var.environment
    Project     = "ev-saas"
  }
}

# Output the endpoint details
output "eks_cluster_endpoint" {
  description = "Endpoint for EKS control plane"
  value       = module.eks.cluster_endpoint
}

output "eks_cluster_name" {
  description = "The name of the EKS cluster"
  value       = module.eks.cluster_name
}

output "rds_endpoint" {
  description = "The RDS instance endpoint"
  value       = module.rds.db_instance_address
}

output "redis_endpoint" {
  description = "The Redis endpoint"
  value       = aws_elasticache_cluster.redis.cache_nodes[0].address
}
