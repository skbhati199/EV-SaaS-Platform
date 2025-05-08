#!/bin/bash

# Script to set up Cloudflare Tunnel for Kubernetes
set -e

# Check if the script is run with the token argument
if [ "$#" -ne 1 ]; then
    echo "Usage: $0 <cloudflare_tunnel_token>"
    exit 1
fi

TUNNEL_TOKEN=$1

# Extract the TUNNEL_ID from the token (first part before the first dot)
TUNNEL_ID=$(echo $TUNNEL_TOKEN | cut -d. -f1)
echo "Tunnel ID: $TUNNEL_ID"

# Create a temporary directory
TEMP_DIR=$(mktemp -d)
trap 'rm -rf "$TEMP_DIR"' EXIT

# Create the credentials file
echo "Creating credentials.json..."
cloudflared tunnel token $TUNNEL_TOKEN > $TEMP_DIR/credentials.json

if [ ! -s "$TEMP_DIR/credentials.json" ]; then
    echo "Failed to create credentials.json. Please make sure cloudflared is installed and you have valid token."
    exit 1
fi

# Base64 encode the credentials file for Kubernetes Secret
CREDENTIALS_B64=$(cat $TEMP_DIR/credentials.json | base64 | tr -d '\n')

# Update the Kubernetes YAML files with actual values
echo "Updating Kubernetes manifests..."
sed -i.bak "s/\${BASE64_ENCODED_CREDENTIALS}/$CREDENTIALS_B64/g" cloudflare-tunnel.yaml
sed -i.bak "s/\${TUNNEL_ID}/$TUNNEL_ID/g" cloudflare-tunnel.yaml

echo "Creation complete! Now you can apply the Kubernetes manifests:"
echo "kubectl apply -f cloudflare-tunnel.yaml"
echo ""
echo "Don't forget to update your DNS records in the Cloudflare dashboard to point to your tunnel."
echo "Each hostname in the config.yaml needs a corresponding CNAME record targeting:"
echo "${TUNNEL_ID}.cfargotunnel.com" 