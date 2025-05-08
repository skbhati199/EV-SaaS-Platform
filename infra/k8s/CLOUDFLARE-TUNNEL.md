# Exposing Kubernetes Services with Cloudflare Tunnel

This guide explains how to expose your EV SaaS Platform microservices running in Kubernetes to the internet using Cloudflare Tunnel.

## Overview

Cloudflare Tunnel provides a secure way to connect your Kubernetes services to Cloudflare's network without exposing any public IP addresses. This approach has several advantages:

- No need for public IP addresses or open inbound ports
- TLS encryption between Cloudflare and your services
- DDoS protection via Cloudflare
- Easy DNS management through Cloudflare

## Prerequisites

1. A Cloudflare account with a domain
2. Access to Cloudflare Zero Trust dashboard
3. `kubectl` configured to access your Kubernetes cluster
4. `cloudflared` CLI tool installed locally

## Setup Instructions

### 1. Create a Cloudflare Tunnel

1. Log in to the [Cloudflare Zero Trust dashboard](https://dash.teams.cloudflare.com)
2. Navigate to "Access" → "Tunnels"
3. Click "Create a tunnel"
4. Give your tunnel a name (e.g., "ev-saas-k8s")
5. Copy the provided token to use in the next step

### 2. Set Up Tunnel Credentials in Kubernetes

Run the provided setup script with your tunnel token:

```bash
chmod +x setup-cloudflare-tunnel.sh
./setup-cloudflare-tunnel.sh <your-tunnel-token>
```

This script will:
- Extract the tunnel ID from the token
- Create the necessary credentials file
- Update the Kubernetes manifest with the proper values

### 3. Deploy Cloudflare Tunnel to Kubernetes

Apply the generated configuration:

```bash
kubectl apply -f cloudflare-tunnel.yaml
```

### 4. Configure DNS Records

For each hostname defined in your ingress configuration, create a CNAME record in Cloudflare DNS:

1. Log in to your Cloudflare dashboard
2. Go to your domain's DNS settings
3. Create CNAME records for each service:
   - `api.nbevc.com` → `<tunnel-id>.cfargotunnel.com`
   - `console.nbevc.com` → `<tunnel-id>.cfargotunnel.com`
   - `auth.nbevc.com` → `<tunnel-id>.cfargotunnel.com`
   - Repeat for each service hostname

## Configuration Details

### Ingress Rules

The Cloudflare Tunnel is configured to route traffic to different services based on the hostname:

- `api.nbevc.com` → NGINX Gateway (for API routing)
- `console.nbevc.com` → Admin Portal
- `auth.nbevc.com` → Auth Service
- `billing.nbevc.com` → Billing Service
- `stations.nbevc.com` → Station Service
- And so on...

### High Availability

The tunnel is deployed with 2 replicas for high availability. Cloudflare will load balance requests across these replicas automatically.

## Managing Your Tunnel

### Updating Configuration

To update your tunnel configuration:

1. Edit the `config.yaml` in the `cloudflare-tunnel-config` ConfigMap:

```bash
kubectl edit configmap cloudflare-tunnel-config -n ev-saas
```

2. After saving, restart the tunnel deployment:

```bash
kubectl rollout restart deployment cloudflare-tunnel -n ev-saas
```

### Monitoring Tunnel Status

Check the logs of your tunnel to verify it's working correctly:

```bash
kubectl logs -l app=cloudflare-tunnel -n ev-saas -f
```

### Updating Tunnel Credentials

If you need to recreate your tunnel or update credentials:

1. Create a new tunnel in Cloudflare dashboard
2. Run the setup script with the new token
3. Apply the updated manifest:

```bash
kubectl apply -f cloudflare-tunnel.yaml
```

## Troubleshooting

- **Connection issues**: Check the tunnel logs for errors
- **DNS not resolving**: Verify CNAME records are properly set up in Cloudflare
- **Services unreachable**: Make sure service names and ports are correct in the ingress rules

## Security Considerations

- Credentials for the tunnel are stored as a Kubernetes Secret
- Only authorized services are exposed through the tunnel
- Make sure to restrict access to your Cloudflare account

## Additional Resources

- [Cloudflare Tunnel documentation](https://developers.cloudflare.com/cloudflare-one/connections/connect-apps/)
- [Kubernetes Gateway API](https://gateway-api.sigs.k8s.io/) 