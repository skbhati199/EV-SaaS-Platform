CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE IF NOT EXISTS notifications (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id UUID NOT NULL,
    type VARCHAR(100) NOT NULL,
    subject VARCHAR(255) NOT NULL,
    content TEXT,
    channel VARCHAR(50),
    recipient VARCHAR(255),
    sent BOOLEAN DEFAULT FALSE,
    read BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT NOW(),
    sent_at TIMESTAMP,
    read_at TIMESTAMP,
    related_entity_id UUID,
    related_entity_type VARCHAR(100)
);

CREATE INDEX idx_notifications_user_id ON notifications(user_id);
CREATE INDEX idx_notifications_type ON notifications(type);
CREATE INDEX idx_notifications_sent ON notifications(sent);
CREATE INDEX idx_notifications_read ON notifications(read);
CREATE INDEX idx_notifications_created_at ON notifications(created_at);
CREATE INDEX idx_notifications_related_entity ON notifications(related_entity_id, related_entity_type); 