-- Billing plans tablosu
CREATE TABLE billing_plans (
                               id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                               name VARCHAR(50) NOT NULL UNIQUE,
                               display_name VARCHAR(100) NOT NULL,
                               monthly_price DECIMAL(10,2) NOT NULL DEFAULT 0.00,
                               yearly_price DECIMAL(10,2) NOT NULL DEFAULT 0.00,
                               request_limit INTEGER NOT NULL,
                               overage_price_per_request DECIMAL(10,6) NOT NULL DEFAULT 0.000000,
                               features TEXT,
                               is_active BOOLEAN DEFAULT TRUE,
                               created_at TIMESTAMP NOT NULL DEFAULT NOW(),
                               updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);

-- Organization subscriptions tablosu
CREATE TABLE subscriptions (
                               id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                               organization_id UUID NOT NULL UNIQUE,
                               plan_id UUID NOT NULL REFERENCES billing_plans(id),
                               status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
                               billing_cycle VARCHAR(20) NOT NULL DEFAULT 'MONTHLY',
                               current_period_start TIMESTAMP NOT NULL,
                               current_period_end TIMESTAMP NOT NULL,
                               cancel_at_period_end BOOLEAN DEFAULT FALSE,
                               stripe_customer_id VARCHAR(255),
                               stripe_subscription_id VARCHAR(255),
                               created_at TIMESTAMP NOT NULL DEFAULT NOW(),
                               updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);

-- Usage records tablosu
CREATE TABLE usage_records (
                               id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                               organization_id UUID NOT NULL,
                               subscription_id UUID NOT NULL REFERENCES subscriptions(id),
                               period_start TIMESTAMP NOT NULL,
                               period_end TIMESTAMP NOT NULL,
                               total_requests BIGINT NOT NULL DEFAULT 0,
                               included_requests INTEGER NOT NULL DEFAULT 0,
                               overage_requests BIGINT NOT NULL DEFAULT 0,
                               overage_amount DECIMAL(10,2) NOT NULL DEFAULT 0.00,
                               created_at TIMESTAMP NOT NULL DEFAULT NOW(),
                               updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);

-- Invoices tablosu
CREATE TABLE invoices (
                          id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                          organization_id UUID NOT NULL,
                          subscription_id UUID NOT NULL REFERENCES subscriptions(id),
                          invoice_number VARCHAR(50) NOT NULL UNIQUE,
                          status VARCHAR(20) NOT NULL DEFAULT 'DRAFT',
                          subtotal DECIMAL(10,2) NOT NULL DEFAULT 0.00,
                          tax DECIMAL(10,2) NOT NULL DEFAULT 0.00,
                          total DECIMAL(10,2) NOT NULL DEFAULT 0.00,
                          currency VARCHAR(3) NOT NULL DEFAULT 'USD',
                          period_start TIMESTAMP NOT NULL,
                          period_end TIMESTAMP NOT NULL,
                          due_date TIMESTAMP,
                          paid_at TIMESTAMP,
                          stripe_invoice_id VARCHAR(255),
                          stripe_payment_intent_id VARCHAR(255),
                          created_at TIMESTAMP NOT NULL DEFAULT NOW(),
                          updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);

-- Invoice items tablosu
CREATE TABLE invoice_items (
                               id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                               invoice_id UUID NOT NULL REFERENCES invoices(id),
                               description VARCHAR(255) NOT NULL,
                               quantity BIGINT NOT NULL DEFAULT 1,
                               unit_price DECIMAL(10,6) NOT NULL DEFAULT 0.000000,
                               amount DECIMAL(10,2) NOT NULL DEFAULT 0.00,
                               type VARCHAR(50) NOT NULL,
                               created_at TIMESTAMP NOT NULL DEFAULT NOW()
);

-- Payment attempts tablosu
CREATE TABLE payment_attempts (
                                  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                                  invoice_id UUID NOT NULL REFERENCES invoices(id),
                                  organization_id UUID NOT NULL,
                                  amount DECIMAL(10,2) NOT NULL,
                                  currency VARCHAR(3) NOT NULL DEFAULT 'USD',
                                  status VARCHAR(20) NOT NULL,
                                  stripe_payment_intent_id VARCHAR(255),
                                  failure_message TEXT,
                                  attempted_at TIMESTAMP NOT NULL DEFAULT NOW()
);

-- Index'ler
CREATE INDEX idx_subscriptions_org_id ON subscriptions(organization_id);
CREATE INDEX idx_usage_records_org_id ON usage_records(organization_id);
CREATE INDEX idx_usage_records_period ON usage_records(period_start, period_end);
CREATE INDEX idx_invoices_org_id ON invoices(organization_id);
CREATE INDEX idx_invoices_status ON invoices(status);
CREATE INDEX idx_payment_attempts_invoice_id ON payment_attempts(invoice_id);

-- Default billing plans
INSERT INTO billing_plans (name, display_name, monthly_price, yearly_price, request_limit, overage_price_per_request, features) VALUES
                                                                                                                                    ('FREE', 'Free Plan', 0.00, 0.00, 1000, 0.000000, 'Basic API access, 1000 requests/month'),
                                                                                                                                    ('STARTER', 'Starter Plan', 29.00, 290.00, 10000, 0.001000, 'Analytics, 10000 requests/month, Email support'),
                                                                                                                                    ('PRO', 'Pro Plan', 99.00, 990.00, 100000, 0.000800, 'Advanced analytics, 100000 requests/month, Priority support'),
                                                                                                                                    ('ENTERPRISE', 'Enterprise Plan', 299.00, 2990.00, 1000000, 0.000500, 'Custom limits, dedicated support, SLA guarantee');