#!/bin/bash

# Script to remove unused script files after reorganizing into the scripts directory

K8S_DIR="/Users/sonukumar/IdeaProjects/EV SaaS Platform/infra/k8s"
echo "Removing unused script files from $K8S_DIR..."

# List of script files to remove
DELETE_SCRIPTS=(
  "$K8S_DIR/apply-k8s-configs.sh"
  "$K8S_DIR/apply-notification-updates.sh"
  "$K8S_DIR/build-all-services.sh"
  "$K8S_DIR/build-dummy-images.sh"
  "$K8S_DIR/deploy-ev-services.sh"
  "$K8S_DIR/deploy-local-environment.sh"
  "$K8S_DIR/setup-cloudflare-tunnel.sh"
  "$K8S_DIR/stop-all-services.sh"
  "$K8S_DIR/verify-service-connectivity.sh"
)

# Remove each script if it exists
for script in "${DELETE_SCRIPTS[@]}"; do
  if [ -f "$script" ]; then
    rm "$script"
    echo "Deleted: $script"
  else
    echo "Script not found: $script"
  fi
done

echo "Script cleanup complete!"
