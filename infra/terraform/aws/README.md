# EV SaaS Platform Terraform Configuration

This Terraform setup manages the infrastructure for the EV SaaS Platform, including AWS resources and GitHub Container Registry integration.

## Prerequisites

1. [Terraform](https://www.terraform.io/downloads.html) (v1.0+)
2. [AWS CLI](https://aws.amazon.com/cli/) configured with appropriate credentials
3. [Docker](https://www.docker.com/get-started) installed and running
4. GitHub Personal Access Token with `packages:write` and `repo` permissions

## Features

- AWS infrastructure provisioning (VPC, EKS, RDS, ElastiCache)
- GitHub Container Registry setup and integration
- Automated Docker image building and pushing for all microservices
- Versioned container images with timestamped tags

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

## Security Notes

- Store sensitive variables (GitHub token, DB passwords) securely - consider using Terraform Cloud, AWS Secrets Manager, or environment variables
- Never commit the `terraform.tfvars` file to version control
- Rotate your GitHub token periodically 