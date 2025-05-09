-- Create refresh tokens table
CREATE TABLE refresh_tokens (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL,
    token VARCHAR(255) NOT NULL UNIQUE,
    expiry_date TIMESTAMP NOT NULL,
    revoked BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    CONSTRAINT fk_user_refresh_token
        FOREIGN KEY(user_id)
        REFERENCES users(id)
        ON DELETE CASCADE
);

-- Create two-factor auth table
CREATE TABLE two_factor_auth (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL UNIQUE,
    secret_key VARCHAR(255) NOT NULL,
    enabled BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),
    CONSTRAINT fk_user_2fa
        FOREIGN KEY(user_id)
        REFERENCES users(id)
        ON DELETE CASCADE
);

-- Create passwordless auth table
CREATE TABLE passwordless_auth (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL,
    token VARCHAR(255) NOT NULL UNIQUE,
    valid_until TIMESTAMP NOT NULL,
    used BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    CONSTRAINT fk_user_passwordless
        FOREIGN KEY(user_id)
        REFERENCES users(id)
        ON DELETE CASCADE
);

-- Create auth audit log table
CREATE TABLE auth_audit_logs (
    id UUID PRIMARY KEY,
    user_id UUID,
    event_type VARCHAR(50) NOT NULL,
    ip_address VARCHAR(50),
    user_agent VARCHAR(255),
    status VARCHAR(50) NOT NULL,
    details VARCHAR(1000),
    created_at TIMESTAMP NOT NULL DEFAULT NOW()
);

-- Create indexes
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_refresh_token_user_id ON refresh_tokens(user_id);
CREATE INDEX idx_refresh_token_token ON refresh_tokens(token);
CREATE INDEX idx_2fa_user_id ON two_factor_auth(user_id);
CREATE INDEX idx_passwordless_token ON passwordless_auth(token);
CREATE INDEX idx_passwordless_user_id ON passwordless_auth(user_id);
CREATE INDEX idx_audit_user_id ON auth_audit_logs(user_id);
CREATE INDEX idx_audit_event_type ON auth_audit_logs(event_type);
CREATE INDEX idx_audit_created_at ON auth_audit_logs(created_at);
