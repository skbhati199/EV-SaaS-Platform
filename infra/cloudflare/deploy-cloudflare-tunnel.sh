#!/bin/bash

# Deploy Cloudflare tunnel for EV SaaS Platform services
# This script deploys the Cloudflare tunnel configuration for nbevc.com domain

set -e

ECHO_PREFIX="\033[1;36m[Cloudflare Tunnel]\033[0m"

echo -e "$ECHO_PREFIX Starting Cloudflare tunnel deployment..."

# Check if cloudflared is installed
if ! command -v cloudflared &> /dev/null; then
    echo -e "$ECHO_PREFIX Error: cloudflared is not installed. Please install it first."
    echo -e "$ECHO_PREFIX You can install it using: brew install cloudflared"
    exit 1
fi

# Deploy tunnel configuration
echo -e "$ECHO_PREFIX Deploying tunnel configuration..."
cloudflared tunnel create ev-saas

# Copy credentials to appropriate location
CREDENTIALS_DIR="/etc/cloudflared"
CREDENTIALS_FILE="${CREDENTIALS_DIR}/credentials.json"

if [ ! -d "$CREDENTIALS_DIR" ]; then
    echo -e "$ECHO_PREFIX Creating credentials directory: $CREDENTIALS_DIR"
    sudo mkdir -p "$CREDENTIALS_DIR"
fi

echo -e "$ECHO_PREFIX Copying credentials to $CREDENTIALS_FILE"
sudo cp ./credentials.json "$CREDENTIALS_FILE"

# Update DNS records
echo -e "$ECHO_PREFIX Creating DNS records for nbevc.com subdomains..."

# Extract tunnel ID from config
TUNNEL_ID=$(grep -o 'tunnel: [^ ]*' config.yml | cut -d ' ' -f 2)

# Create DNS records for all services
DOMAINS=(
    "api"
    "console"
    "auth"
    "billing"
    "notifications"
    "roaming"
    "stations"
    "scheduler"
    "smart-charging"
    "users"
    "discovery"
    "docs"
    "keycloak"
)

for domain in "${DOMAINS[@]}"; do
    echo -e "$ECHO_PREFIX Creating DNS record for $domain.nbevc.com"
    cloudflared tunnel route dns "$TUNNEL_ID" "$domain.nbevc.com"
done

# Run the tunnel
echo -e "$ECHO_PREFIX Starting Cloudflare tunnel..."
cloudflared tunnel run --config ./config.yml

echo -e "$ECHO_PREFIX Cloudflare tunnel deployment complete!"
echo -e "$ECHO_PREFIX The following domains are now accessible:"
for domain in "${DOMAINS[@]}"; do
    echo -e "$ECHO_PREFIX - https://$domain.nbevc.com"
done
