#!/bin/bash
# =============================================================================
# nginx-setup.sh — Configure nginx vhosts + certbot SSL for spareparts
# Domains:
#   spare.symmetry.sn        → frontend  (localhost:3000)
#   spare-api.symmetry.sn    → backend   (localhost:8080)
#   storage-spare.symmetry.sn→ minio API (localhost:9001)
# =============================================================================

set -euo pipefail

# ---------------------------------------------------------------------------
# Config
# ---------------------------------------------------------------------------
FRONTEND_DOMAIN="spare.symmetry.sn"
API_DOMAIN="spare-api.symmetry.sn"
MINIO_DOMAIN="storage-spare.symmetry.sn"

FRONTEND_PORT=3000
BACKEND_PORT=8080
MINIO_PORT=9000
MINIO_CONSOLE_PORT=9001

NGINX_CONF_DIR="/etc/nginx/conf.d"
CERTBOT_EMAIL="${CERTBOT_EMAIL:-admin@symmetry.sn}"
WEBROOT="/var/www/certbot"

# ---------------------------------------------------------------------------
# Colors
# ---------------------------------------------------------------------------
RED='\033[0;31m'; GREEN='\033[0;32m'; YELLOW='\033[1;33m'; NC='\033[0m'
info()    { echo -e "${GREEN}[INFO]${NC}  $*"; }
warn()    { echo -e "${YELLOW}[WARN]${NC}  $*"; }
error()   { echo -e "${RED}[ERROR]${NC} $*" >&2; }

# ---------------------------------------------------------------------------
# Guards
# ---------------------------------------------------------------------------
if [[ $EUID -ne 0 ]]; then
  error "Run as root or with sudo."
  exit 1
fi

for cmd in nginx certbot; do
  if ! command -v "$cmd" &>/dev/null; then
    error "'$cmd' is not installed. Install it and re-run."
    exit 1
  fi
done

# ---------------------------------------------------------------------------
# Helpers
# ---------------------------------------------------------------------------
cert_exists() {
  local domain="$1"
  [[ -d "/etc/letsencrypt/live/${domain}" ]]
}

nginx_reload() {
  info "Testing nginx configuration..."
  nginx -t
  info "Reloading nginx..."
  systemctl reload nginx
}

# Create the SSL options file that certbot --nginx normally provides
ensure_ssl_options() {
  local ssl_options="/etc/letsencrypt/options-ssl-nginx.conf"
  local dhparam="/etc/letsencrypt/ssl-dhparams.pem"

  mkdir -p /etc/letsencrypt

  if [[ ! -f "$ssl_options" ]]; then
    info "Creating ${ssl_options}..."
    cat > "$ssl_options" <<'EOF'
ssl_session_cache    shared:le_nginx_SSL:10m;
ssl_session_timeout  1440m;
ssl_session_tickets  off;

ssl_protocols TLSv1.2 TLSv1.3;
ssl_prefer_server_ciphers off;
ssl_ciphers "ECDHE-ECDSA-AES128-GCM-SHA256:ECDHE-RSA-AES128-GCM-SHA256:ECDHE-ECDSA-AES256-GCM-SHA384:ECDHE-RSA-AES256-GCM-SHA384:ECDHE-ECDSA-CHACHA20-POLY1305:ECDHE-RSA-CHACHA20-POLY1305:DHE-RSA-AES128-GCM-SHA256:DHE-RSA-AES256-GCM-SHA384";
EOF
  else
    info "${ssl_options} already exists — skipping."
  fi

  if [[ ! -f "$dhparam" ]]; then
    info "Generating DH parameters (2048-bit) — this may take a moment..."
    openssl dhparam -out "$dhparam" 2048 2>/dev/null
  else
    info "${dhparam} already exists — skipping."
  fi
}

# ---------------------------------------------------------------------------
# Phase 1 — HTTP-only configs (needed for ACME challenge)
# ---------------------------------------------------------------------------
write_http_conf() {
  local domain="$1"
  local upstream_port="$2"
  local conf_file="${NGINX_CONF_DIR}/${domain}.conf"

  if [[ -f "$conf_file" ]]; then
    warn "Config already exists: ${conf_file} — replacing."
  fi

  info "Writing HTTP config for ${domain}..."
  cat > "$conf_file" <<EOF
server {
    listen 80;
    server_name ${domain};

    # ACME challenge (certbot webroot)
    location /.well-known/acme-challenge/ {
        root ${WEBROOT};
    }

    location / {
        return 301 https://\$host\$request_uri;
    }
}
EOF
}

# ---------------------------------------------------------------------------
# Phase 2 — Full HTTPS configs
# ---------------------------------------------------------------------------
write_https_conf_frontend() {
  local domain="$FRONTEND_DOMAIN"
  local port="$FRONTEND_PORT"
  info "Writing HTTPS config for ${domain} (frontend)..."
  cat > "${NGINX_CONF_DIR}/${domain}.conf" <<EOF
server {
    listen 80;
    server_name ${domain};

    location /.well-known/acme-challenge/ {
        root ${WEBROOT};
    }

    location / {
        return 301 https://\$host\$request_uri;
    }
}

server {
    listen 443 ssl http2;
    server_name ${domain};

    ssl_certificate     /etc/letsencrypt/live/${domain}/fullchain.pem;
    ssl_certificate_key /etc/letsencrypt/live/${domain}/privkey.pem;
    include             /etc/letsencrypt/options-ssl-nginx.conf;
    ssl_dhparam         /etc/letsencrypt/ssl-dhparams.pem;

    # Security headers
    add_header Strict-Transport-Security "max-age=31536000; includeSubDomains" always;
    add_header X-Frame-Options           SAMEORIGIN always;
    add_header X-Content-Type-Options    nosniff always;
    add_header Referrer-Policy           "strict-origin-when-cross-origin" always;

    location / {
        proxy_pass         http://127.0.0.1:${port};
        proxy_http_version 1.1;
        proxy_set_header   Host              \$host;
        proxy_set_header   X-Real-IP         \$remote_addr;
        proxy_set_header   X-Forwarded-For   \$proxy_add_x_forwarded_for;
        proxy_set_header   X-Forwarded-Proto \$scheme;
        proxy_set_header   Upgrade           \$http_upgrade;
        proxy_set_header   Connection        "upgrade";
    }
}
EOF
}

write_https_conf_api() {
  local domain="$API_DOMAIN"
  local port="$BACKEND_PORT"
  info "Writing HTTPS config for ${domain} (backend API)..."
  cat > "${NGINX_CONF_DIR}/${domain}.conf" <<EOF
server {
    listen 80;
    server_name ${domain};

    location /.well-known/acme-challenge/ {
        root ${WEBROOT};
    }

    location / {
        return 301 https://\$host\$request_uri;
    }
}

server {
    listen 443 ssl http2;
    server_name ${domain};

    ssl_certificate     /etc/letsencrypt/live/${domain}/fullchain.pem;
    ssl_certificate_key /etc/letsencrypt/live/${domain}/privkey.pem;
    include             /etc/letsencrypt/options-ssl-nginx.conf;
    ssl_dhparam         /etc/letsencrypt/ssl-dhparams.pem;

    add_header Strict-Transport-Security "max-age=31536000; includeSubDomains" always;
    add_header X-Content-Type-Options    nosniff always;

    # Larger body for file uploads
    client_max_body_size 50M;

    location / {
        proxy_pass         http://127.0.0.1:${port};
        proxy_http_version 1.1;
        proxy_set_header   Host              \$host;
        proxy_set_header   X-Real-IP         \$remote_addr;
        proxy_set_header   X-Forwarded-For   \$proxy_add_x_forwarded_for;
        proxy_set_header   X-Forwarded-Proto \$scheme;
        proxy_read_timeout 120s;
        proxy_send_timeout 120s;
    }
}
EOF
}

write_https_conf_minio() {
  local domain="$MINIO_DOMAIN"
  local port="$MINIO_PORT"
  local console_port="$MINIO_CONSOLE_PORT"
  info "Writing HTTPS config for ${domain} (MinIO)..."
  cat > "${NGINX_CONF_DIR}/${domain}.conf" <<EOF
server {
    listen 80;
    server_name ${domain};

    location /.well-known/acme-challenge/ {
        root ${WEBROOT};
    }

    location / {
        return 301 https://\$host\$request_uri;
    }
}

server {
    listen 443 ssl http2;
    server_name ${domain};

    ssl_certificate     /etc/letsencrypt/live/${domain}/fullchain.pem;
    ssl_certificate_key /etc/letsencrypt/live/${domain}/privkey.pem;
    include             /etc/letsencrypt/options-ssl-nginx.conf;
    ssl_dhparam         /etc/letsencrypt/ssl-dhparams.pem;

    add_header Strict-Transport-Security "max-age=31536000; includeSubDomains" always;

    # Allow large object uploads
    client_max_body_size 500M;

    ignore_invalid_headers off;

    # WebSocket endpoint for MinIO console (port ${console_port})
    location /ws/ {
        proxy_pass              http://127.0.0.1:${console_port};
        proxy_http_version      1.1;
        proxy_set_header        Host              \$http_host;
        proxy_set_header        X-Real-IP         \$remote_addr;
        proxy_set_header        X-Forwarded-For   \$proxy_add_x_forwarded_for;
        proxy_set_header        X-Forwarded-Proto \$scheme;
        proxy_set_header        Upgrade           \$http_upgrade;
        proxy_set_header        Connection        "upgrade";
        proxy_read_timeout      86400s;
    }

    # Object storage API (port ${port})
    location / {
        proxy_pass              http://127.0.0.1:${port};
        proxy_http_version      1.1;
        proxy_set_header        Host              \$http_host;
        proxy_set_header        X-Real-IP         \$remote_addr;
        proxy_set_header        X-Forwarded-For   \$proxy_add_x_forwarded_for;
        proxy_set_header        X-Forwarded-Proto \$scheme;
        proxy_set_header        Connection        "";
        proxy_connect_timeout   300s;
        proxy_send_timeout      300s;
        proxy_read_timeout      300s;
        proxy_buffering         off;
        proxy_request_buffering off;
        chunked_transfer_encoding on;
    }
}
EOF
}

# ---------------------------------------------------------------------------
# Phase 3 — Obtain certificate if missing
# ---------------------------------------------------------------------------
obtain_cert() {
  local domain="$1"

  if cert_exists "$domain"; then
    info "Certificate already exists for ${domain} — skipping."
    return
  fi

  info "Obtaining certificate for ${domain}..."
  certbot certonly \
    --webroot \
    --webroot-path "$WEBROOT" \
    --email "$CERTBOT_EMAIL" \
    --agree-tos \
    --no-eff-email \
    --non-interactive \
    -d "$domain"
}

# ---------------------------------------------------------------------------
# Main
# ---------------------------------------------------------------------------
info "Creating certbot webroot directory..."
mkdir -p "$WEBROOT"

# Phase 1 — write minimal HTTP configs so nginx can serve ACME challenge
for domain in "$FRONTEND_DOMAIN" "$API_DOMAIN" "$MINIO_DOMAIN"; do
  write_http_conf "$domain" 0   # port unused in HTTP-only conf
done

nginx_reload

# Phase 2 — obtain certs
for domain in "$FRONTEND_DOMAIN" "$API_DOMAIN" "$MINIO_DOMAIN"; do
  obtain_cert "$domain"
done

# Phase 3 — ensure SSL options file and dhparam exist, then write HTTPS configs
ensure_ssl_options
write_https_conf_frontend
write_https_conf_api
write_https_conf_minio

nginx_reload

# ---------------------------------------------------------------------------
# Certbot auto-renew (add cron if not already present)
# ---------------------------------------------------------------------------
CRON_JOB="0 3 * * * certbot renew --quiet --post-hook 'systemctl reload nginx'"
if ! crontab -l 2>/dev/null | grep -qF "certbot renew"; then
  info "Adding certbot auto-renew cron job (daily at 03:00)..."
  (crontab -l 2>/dev/null; echo "$CRON_JOB") | crontab -
else
  info "Certbot auto-renew cron job already present."
fi

info "======================================================"
info " Setup complete!"
info "  https://${FRONTEND_DOMAIN}   → frontend"
info "  https://${API_DOMAIN}        → backend API"
info "  https://${MINIO_DOMAIN}      → MinIO storage"
info "======================================================"
