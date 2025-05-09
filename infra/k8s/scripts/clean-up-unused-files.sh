#!/bin/bash

# Script to clean up unused YAML files after reorganizing the directory structure

echo "Cleaning up unused YAML files..."

# List of files to remove (these are now consolidated into the structured directories)
DELETE_FILES=(
  "../additional-services.yaml"
  "../all-services-fix.yaml"
  "../auth-service-fix.yaml"
  "../auth-service-fixed.yaml"
  "../auth-service-nginx-fix.yaml"
  "../auth-service-with-migrations.yaml"
  "../billing-service-fix-updated.yaml"
  "../billing-service-fix.yaml"
  "../fix-all-services.yaml"
  "../infrastructure-components.yaml"
  "../kafka-fixed.yaml"
  "../kafka-simple-fix.yaml"
  "../nginx-gateway-fixed.yaml"
  "../nginx-gateway-updated.yaml"
  "../nginx-local-complete.yaml"
  "../nginx-local-fixed-final.yaml"
  "../nginx-local-fixed.yaml"
  "../nginx-local.yaml"
  "../nginx-simple.yaml"
  "../nginx-with-swagger.yaml"
  "../notification-service-fixed.yaml"
  "../postgres-config-fixed.yaml"
  "../roaming-service-fixed.yaml"
  "../service-proxy-update.yaml"
  "../service-proxy.yaml"
  "../simplified-deployment.yaml"
  "../swagger-ui.yaml"
  "../cloudflare-tunnel.yaml.bak"
)

# Remove each file if it exists
for file in "${DELETE_FILES[@]}"; do
  if [ -f "$file" ]; then
    rm "$file"
    echo "Deleted: $file"
  else
    echo "File not found: $file"
  fi
done

echo "Cleanup complete. The Kubernetes configuration is now well-structured."
