terraform {
  required_providers {
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

# Configure the GitHub Provider
provider "github" {
  token = var.github_token
  owner = var.github_owner
}

# Create GitHub Container Registry Package
resource "github_repository" "ev_saas_repo" {
  name        = "ev-saas-platform"
  description = "EV SaaS Platform Repository"
  visibility  = "private"

  has_issues           = true
  has_projects         = true
  has_wiki             = true
  has_downloads        = true
  vulnerability_alerts = true
}

# Enable GitHub Container Registry
resource "null_resource" "enable_container_registry" {
  triggers = {
    github_repo = github_repository.ev_saas_repo.name
  }

  provisioner "local-exec" {
    command = <<-EOT
      curl -X POST \
        -H "Authorization: token ${var.github_token}" \
        -H "Accept: application/vnd.github.v3+json" \
        https://api.github.com/user/packages/container/${github_repository.ev_saas_repo.name}/visibility \
        -d '{"visibility":"private"}'
    EOT
  }

  depends_on = [github_repository.ev_saas_repo]
}

# Login to GitHub Container Registry
resource "null_resource" "github_login" {
  provisioner "local-exec" {
    command = "echo ${var.github_token} | docker login ghcr.io -u ${var.github_owner} --password-stdin"
  }
}

# Dynamic loop for building and pushing all services
resource "null_resource" "build_push_services" {
  for_each = toset(var.service_list)

  triggers = {
    # Using path interpolation to reference the Dockerfile and source code of each service
    dockerfile_hash = fileexists("${path.module}/../../../${each.value}/Dockerfile") ? filemd5("${path.module}/../../../${each.value}/Dockerfile") : timestamp()
    # Calculate hash of source files (if available) or use timestamp if not available
    source_hash = fileexists("${path.module}/../../../${each.value}/src") ? sha256(join("", [for f in fileset("${path.module}/../../../${each.value}/src", "**/*.java") : filesha256("${path.module}/../../../${each.value}/src/${f}")])) : timestamp()
    # Trigger rebuild on git commit or on deploy
    timestamp = timestamp()
  }

  provisioner "local-exec" {
    command = <<-EOT
      # Set service name
      SERVICE_NAME="${each.value}"
      
      # Check if directory exists
      if [ -d "${path.module}/../../../$SERVICE_NAME" ]; then
        echo "Building and pushing $SERVICE_NAME..."
        
        # Build the Docker image with versioning
        SERVICE_VERSION=$(date '+%Y%m%d%H%M%S')
        
        # Build the Docker image
        docker build -t ghcr.io/${var.github_owner}/ev-saas-platform/$SERVICE_NAME:latest \
                     -t ghcr.io/${var.github_owner}/ev-saas-platform/$SERVICE_NAME:$SERVICE_VERSION \
                     ${path.module}/../../../$SERVICE_NAME/
        
        # Push the images
        docker push ghcr.io/${var.github_owner}/ev-saas-platform/$SERVICE_NAME:latest
        docker push ghcr.io/${var.github_owner}/ev-saas-platform/$SERVICE_NAME:$SERVICE_VERSION
        
        echo "Successfully built and pushed $SERVICE_NAME"
      else
        echo "Service directory $SERVICE_NAME not found. Skipping..."
      fi
    EOT
  }

  depends_on = [null_resource.github_login, null_resource.enable_container_registry]
}

# Output the image URLs
output "container_registry_urls" {
  description = "URLs of the pushed container images"
  value = {
    for service in var.service_list :
    service => "ghcr.io/${var.github_owner}/ev-saas-platform/${service}:latest"
  }
}
