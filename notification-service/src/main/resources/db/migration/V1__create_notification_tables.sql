-- Notification logs tablosu
CREATE TABLE notification_logs (
                                   id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                                   organization_id UUID NOT NULL,
                                   recipient_email VARCHAR(255) NOT NULL,
                                   notification_type VARCHAR(50) NOT NULL,
                                   channel VARCHAR(20) NOT NULL,
                                   status VARCHAR(20) NOT NULL,
                                   subject VARCHAR(255),
                                   error_message TEXT,
                                   retry_count INTEGER DEFAULT 0,
                                   sent_at TIMESTAMP,
                                   created_at TIMESTAMP NOT NULL DEFAULT NOW()
);

-- Webhook subscriptions tablosu
CREATE TABLE webhook_subscriptions (
                                       id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                                       organization_id UUID NOT NULL,
                                       url VARCHAR(500) NOT NULL,
                                       secret VARCHAR(255),
                                       is_active BOOLEAN DEFAULT TRUE,
                                       event_types VARCHAR(500) NOT NULL,
                                       created_at TIMESTAMP NOT NULL DEFAULT NOW(),
                                       updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);

-- Notification preferences tablosu
CREATE TABLE notification_preferences (
                                          id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                                          organization_id UUID NOT NULL UNIQUE,
                                          email_enabled BOOLEAN DEFAULT TRUE,
                                          webhook_enabled BOOLEAN DEFAULT FALSE,
                                          rate_limit_warning_enabled BOOLEAN DEFAULT TRUE,
                                          rate_limit_exceeded_enabled BOOLEAN DEFAULT TRUE,
                                          api_key_expiring_enabled BOOLEAN DEFAULT TRUE,
                                          daily_report_enabled BOOLEAN DEFAULT FALSE,
                                          high_error_rate_enabled BOOLEAN DEFAULT TRUE,
                                          created_at TIMESTAMP NOT NULL DEFAULT NOW(),
                                          updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);

-- Index'ler
CREATE INDEX idx_notification_logs_org_id ON notification_logs(organization_id);
CREATE INDEX idx_notification_logs_created_at ON notification_logs(created_at);
CREATE INDEX idx_webhook_subscriptions_org_id ON webhook_subscriptions(organization_id);
CREATE INDEX idx_notification_preferences_org_id ON notification_preferences(organization_id);