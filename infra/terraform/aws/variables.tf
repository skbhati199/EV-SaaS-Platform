variable "region" {
  description = "AWS region to deploy resources"
  type        = string
  default     = "us-east-1"
}

variable "cluster_name" {
  description = "Name of the EKS cluster"
  type        = string
  default     = "ev-saas-cluster"
}

variable "environment" {
  description = "Environment name"
  type        = string
  default     = "production"
}

variable "db_username" {
  description = "Username for RDS PostgreSQL instance"
  type        = string
  sensitive   = true
}

variable "db_password" {
  description = "Password for RDS PostgreSQL instance"
  type        = string
  sensitive   = true
}

# GitHub variables for container registry
variable "github_token" {
  description = "GitHub Personal Access Token with packages:write and repo permissions"
  type        = string
  sensitive   = true
}

variable "github_owner" {
  description = "GitHub username or organization name"
  type        = string
}

variable "service_list" {
  description = "List of services to build and push to GitHub Container Registry"
  type        = list(string)
  default     = ["billing-service", "auth-service", "api-gateway", "station-service", "user-service"]
} 
