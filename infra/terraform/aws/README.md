# EV SaaS Platform Terraform Configuration

This Terraform setup manages the infrastructure for the EV SaaS Platform, including AWS resources and GitHub Container Registry integration.

## Prerequisites

1. [Terraform](https://www.terraform.io/downloads.html) (v1.0+)
2. [AWS CLI](https://aws.amazon.com/cli/) configured with appropriate credentials
3. [Docker](https://www.docker.com/get-started) installed and running
4. GitHub Personal Access Token with `packages:write` and `repo` permissions
5. `kubectl` command-line tool installed for Kubernetes management

## Features

- AWS infrastructure provisioning:
  - VPC with public and private subnets
  - EKS (Kubernetes) cluster
  - RDS PostgreSQL database
  - ElastiCache Redis instance (using native AWS resources)
- GitHub Container Registry setup and integration
- Automated Docker image building and pushing for all microservices
- Versioned container images with timestamped tags
- EKS integration with GitHub Container Registry for image pulling
- Automatic Kubernetes secret configuration for registry authentication

## Setup Instructions

### 1. Configure Variables

Copy the example variables file and update it with your settings:

```bash
cp terraform.tfvars.example terraform.tfvars
```

Edit `terraform.tfvars` to include your:
- AWS configuration
- Database credentials (keep these secure)
- GitHub owner (username or organization)
- GitHub Personal Access Token
- List of services to build and push

### 2. Initialize Terraform

```bash
terraform init
```

### 3. Plan the Deployment

```bash
terraform plan
```

### 4. Apply the Configuration

```bash
terraform apply
```

### 5. Push Images to GitHub Container Registry

The Terraform configuration will automatically:
1. Set up GitHub Container Registry
2. Build Docker images for all specified services
3. Push the images to the registry with both `latest` and timestamped version tags
4. Configure EKS to access the GitHub Container Registry
5. Create Kubernetes secrets for authentication with the registry

## Managing Service Images

### Adding a New Service

1. Add the service name to the `service_list` variable in `terraform.tfvars`
2. Ensure the service has a valid `Dockerfile` in its directory
3. Run `terraform apply` to build and push the new service image

### Updating Existing Images

When you make changes to a service:

1. Commit your changes to the service code
2. Run `terraform apply` - the build triggers will detect changes and rebuild+push the updated image

## Container Image URLs

After applying the Terraform configuration, the container image URLs will be output in the format:

```
ghcr.io/<github-owner>/ev-saas-platform/<service-name>:latest
ghcr.io/<github-owner>/ev-saas-platform/<service-name>:<timestamp>
```

You can use these URLs in your Kubernetes deployments or Docker Compose files.

## Using Images in Kubernetes

The Terraform configuration automatically creates:

1. An IAM policy allowing EKS nodes to pull from container registries
2. A Kubernetes secret named `github-registry-secret` in the `ev-saas` namespace
3. Default service account configuration to use this secret

In your Kubernetes deployment manifests, you can reference the images:

```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: billing-service
  namespace: ev-saas
spec:
  template:
    spec:
      containers:
      - name: billing-service
        image: ghcr.io/<github-owner>/ev-saas-platform/billing-service:latest
      imagePullSecrets:
      - name: github-registry-secret
```

## Infrastructure Outputs

After deployment, you can access:

- EKS Cluster Endpoint: For Kubernetes API access
- RDS Database Endpoint: For database connections
- Redis Endpoint: For caching and session management
- Container Registry URLs: For referencing in deployments

## Provider Compatibility Notes

This configuration has been tested and works with:
- AWS Provider: >= 3.73.0, < 4.0.0
- GitHub Provider: ~> 5.0
- Null Provider: ~> 3.0

To avoid version constraint issues, we've used native AWS resources instead of the Terraform AWS ElastiCache module.

## Security Notes

- Store sensitive variables (GitHub token, DB passwords) securely - consider using Terraform Cloud, AWS Secrets Manager, or environment variables
- Never commit the `terraform.tfvars` file to version control
- Rotate your GitHub token periodically
- The EKS IAM role has permissions to pull from container registries 