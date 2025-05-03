-- Create billing plans table
CREATE TABLE billing_plans (
    id UUID PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    price_monthly DECIMAL(10, 2) NOT NULL,
    price_yearly DECIMAL(10, 2) NOT NULL,
    features JSONB,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);

-- Create subscriptions table
CREATE TABLE subscriptions (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL,
    organization_id UUID NOT NULL,
    plan_id UUID NOT NULL,
    status VARCHAR(50) NOT NULL,
    start_date TIMESTAMP NOT NULL,
    end_date TIMESTAMP,
    billing_cycle VARCHAR(20) NOT NULL,
    auto_renew BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),
    CONSTRAINT fk_subscription_plan
        FOREIGN KEY(plan_id)
        REFERENCES billing_plans(id)
);

-- Create invoices table
CREATE TABLE invoices (
    id UUID PRIMARY KEY,
    subscription_id UUID NOT NULL,
    user_id UUID NOT NULL,
    organization_id UUID NOT NULL,
    invoice_number VARCHAR(50) NOT NULL UNIQUE,
    amount DECIMAL(10, 2) NOT NULL,
    tax_amount DECIMAL(10, 2) NOT NULL,
    total_amount DECIMAL(10, 2) NOT NULL,
    status VARCHAR(50) NOT NULL,
    due_date TIMESTAMP NOT NULL,
    payment_date TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),
    CONSTRAINT fk_invoice_subscription
        FOREIGN KEY(subscription_id)
        REFERENCES subscriptions(id)
);

-- Create invoice items table
CREATE TABLE invoice_items (
    id UUID PRIMARY KEY,
    invoice_id UUID NOT NULL,
    description VARCHAR(255) NOT NULL,
    quantity INTEGER NOT NULL,
    unit_price DECIMAL(10, 2) NOT NULL,
    total_price DECIMAL(10, 2) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    CONSTRAINT fk_invoice_item_invoice
        FOREIGN KEY(invoice_id)
        REFERENCES invoices(id)
        ON DELETE CASCADE
);

-- Create payments table
CREATE TABLE payments (
    id UUID PRIMARY KEY,
    invoice_id UUID NOT NULL,
    amount DECIMAL(10, 2) NOT NULL,
    payment_method VARCHAR(50) NOT NULL,
    transaction_id VARCHAR(100),
    status VARCHAR(50) NOT NULL,
    payment_date TIMESTAMP NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),
    CONSTRAINT fk_payment_invoice
        FOREIGN KEY(invoice_id)
        REFERENCES invoices(id)
);

-- Create usage records table for metered billing
CREATE TABLE usage_records (
    id UUID PRIMARY KEY,
    subscription_id UUID NOT NULL,
    meter_type VARCHAR(50) NOT NULL,
    quantity DECIMAL(10, 2) NOT NULL,
    timestamp TIMESTAMP NOT NULL,
    processed BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    CONSTRAINT fk_usage_subscription
        FOREIGN KEY(subscription_id)
        REFERENCES subscriptions(id)
);

-- Create payment methods table
CREATE TABLE payment_methods (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL,
    type VARCHAR(50) NOT NULL,
    provider VARCHAR(50) NOT NULL,
    token_id VARCHAR(255),
    last_four VARCHAR(4),
    expiry_month VARCHAR(2),
    expiry_year VARCHAR(4),
    is_default BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);

-- Create billing settings table
CREATE TABLE billing_settings (
    id UUID PRIMARY KEY,
    organization_id UUID NOT NULL UNIQUE,
    billing_email VARCHAR(255),
    tax_id VARCHAR(100),
    billing_address JSONB,
    currency VARCHAR(3) DEFAULT 'USD',
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);

-- Create billing audit log table
CREATE TABLE billing_audit_logs (
    id UUID PRIMARY KEY,
    entity_type VARCHAR(50) NOT NULL,
    entity_id UUID NOT NULL,
    action VARCHAR(50) NOT NULL,
    user_id UUID,
    details JSONB,
    created_at TIMESTAMP NOT NULL DEFAULT NOW()
);

-- Create indexes
CREATE INDEX idx_billing_plans_active ON billing_plans(is_active);
CREATE INDEX idx_subscriptions_user_id ON subscriptions(user_id);
CREATE INDEX idx_subscriptions_organization_id ON subscriptions(organization_id);
CREATE INDEX idx_subscriptions_plan_id ON subscriptions(plan_id);
CREATE INDEX idx_subscriptions_status ON subscriptions(status);
CREATE INDEX idx_invoices_subscription_id ON invoices(subscription_id);
CREATE INDEX idx_invoices_user_id ON invoices(user_id);
CREATE INDEX idx_invoices_organization_id ON invoices(organization_id);
CREATE INDEX idx_invoices_status ON invoices(status);
CREATE INDEX idx_invoices_due_date ON invoices(due_date);
CREATE INDEX idx_invoice_items_invoice_id ON invoice_items(invoice_id);
CREATE INDEX idx_payments_invoice_id ON payments(invoice_id);
CREATE INDEX idx_payments_status ON payments(status);
CREATE INDEX idx_usage_records_subscription_id ON usage_records(subscription_id);
CREATE INDEX idx_usage_records_meter_type ON usage_records(meter_type);
CREATE INDEX idx_usage_records_processed ON usage_records(processed);
CREATE INDEX idx_payment_methods_user_id ON payment_methods(user_id);
CREATE INDEX idx_billing_settings_organization_id ON billing_settings(organization_id);
CREATE INDEX idx_billing_audit_logs_entity_type_id ON billing_audit_logs(entity_type, entity_id);
CREATE INDEX idx_billing_audit_logs_created_at ON billing_audit_logs(created_at); 