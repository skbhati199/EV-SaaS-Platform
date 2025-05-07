#!/bin/bash

# Check if the script is run with the token argument
if [ "$#" -ne 1 ]; then
    echo "Usage: $0 <cloudflare_tunnel_token>"
    exit 1
fi

TUNNEL_TOKEN=$1

# Extract the TUNNEL_ID from the token (first part before the first dot)
TUNNEL_ID=$(echo $TUNNEL_TOKEN | cut -d. -f1)

# Create the credentials file
mkdir -p "$(dirname "$0")"
echo "Creating credentials.json..."
cat > "$(dirname "$0")/credentials.json" << EOF
{"AccountTag":"","TunnelID":"$TUNNEL_ID","TunnelName":"ev-saas-platform","TunnelSecret":""}
EOF

# Update the config file with the correct tunnel ID
echo "Updating config.yml with tunnel ID..."
sed -i.bak "s/\${TUNNEL_ID}/$TUNNEL_ID/g" "$(dirname "$0")/config.yml"

# Add the tunnel token to the .env file
echo "Adding CLOUDFLARE_TUNNEL_TOKEN to .env file..."
if grep -q "CLOUDFLARE_TUNNEL_TOKEN" ../../.env; then
    sed -i.bak "s/CLOUDFLARE_TUNNEL_TOKEN=.*/CLOUDFLARE_TUNNEL_TOKEN=$TUNNEL_TOKEN/g" ../../.env
else
    echo -e "\n# Cloudflare Tunnel Configuration" >> ../../.env
    echo "CLOUDFLARE_TUNNEL_TOKEN=$TUNNEL_TOKEN" >> ../../.env
    echo "TUNNEL_ID=$TUNNEL_ID" >> ../../.env
fi

echo "Done! Now update your DNS records in Cloudflare dashboard"
echo "Then update the hostnames in config.yml with your actual domain"
echo "Finally, start your services with: docker-compose -f docker-compose.test.yml up -d" 