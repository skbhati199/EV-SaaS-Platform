# Cloudflare Tunnel Setup for EV SaaS Platform

This directory contains configuration files for the Cloudflare tunnel that securely exposes your EV SaaS Platform services to the internet without opening ports in your firewall.

## Setup Instructions

### 1. Create a Cloudflare Tunnel

1. Log in to the [Cloudflare Zero Trust Dashboard](https://dash.teams.cloudflare.com/)
2. Navigate to **Access > Tunnels**
3. Click **Create a tunnel**
4. Give your tunnel a name (e.g., `ev-saas-platform`)
5. After creating the tunnel, you'll be shown a token - copy this token

brew install cloudflared && 

sudo cloudflared service install eyJhIjoiYmY1NDk1MzI3M2U5ZTJmOGE3ZDFiNzQxYzk1Y2I3YjciLCJ0IjoiODE4MTBmOWItMTVlOS00NTc5LThhYjgtOGY0ZTdiN2M5YzY4IiwicyI6Ik5qUXhNR05sTnpjdE1EWmhOeTAwTURFMUxUZzJPV1F0TmpNME1EQXhPVGcwTXprNCJ9

### 2. Configure Environment Variables

1. Add the following variables to your main `.env` file:
   ```
   CLOUDFLARE_TUNNEL_TOKEN=your_tunnel_token_here
   TUNNEL_ID=your_tunnel_id_here
   ```

   Replace `your_tunnel_token_here` with the token from step 1.
   Replace `your_tunnel_id_here` with the tunnel ID from the Cloudflare dashboard.

### 3. Configure DNS Records

For each service you want to expose, create a CNAME record in your Cloudflare DNS dashboard:

1. Go to your domain's DNS settings in the [Cloudflare Dashboard](https://dash.cloudflare.com/)
2. Add the following CNAME records pointing to your tunnel:

| Type  | Name                        | Content                             |
|-------|-----------------------------|------------------------------------|
| CNAME | ev-platform                 | your-tunnel-id.cfargotunnel.com    |
| CNAME | admin.ev-platform           | your-tunnel-id.cfargotunnel.com    |
| CNAME | api.ev-platform             | your-tunnel-id.cfargotunnel.com    |
| CNAME | auth.ev-platform            | your-tunnel-id.cfargotunnel.com    |
| CNAME | keycloak.ev-platform        | your-tunnel-id.cfargotunnel.com    |
| CNAME | prometheus.ev-platform      | your-tunnel-id.cfargotunnel.com    |
| CNAME | grafana.ev-platform         | your-tunnel-id.cfargotunnel.com    |
| CNAME | mailhog.ev-platform         | your-tunnel-id.cfargotunnel.com    |

### 4. Modify Configuration

1. Edit `config.yml` to replace `nbevc.com` with your actual domain name.
2. Customize ingress rules as needed for your specific services.

### 5. Start the Tunnel

Start the tunnel along with your other services:

```bash
docker-compose -f docker-compose.test.yml up -d
```

## Troubleshooting

### Tunnel Not Connecting

1. Check logs: `docker logs ev-test-cloudflared`
2. Verify your token is correct in the `.env` file
3. Ensure the Cloudflare tunnel container has network access

### Services Not Accessible

1. Verify DNS records are correctly set up in Cloudflare
2. Check ingress rules in `config.yml` to ensure services are properly mapped
3. Make sure services are running and accessible from the Cloudflare tunnel container

## Security Considerations

- The Cloudflare tunnel token grants access to create new tunnels - keep it secure
- Consider using Cloudflare Access to add authentication to your exposed services
- Regularly audit your tunnel configuration and exposed services 