CREATE TABLE api_keys (
                          id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                          key_value VARCHAR(255) NOT NULL UNIQUE,
                          name VARCHAR(255) NOT NULL,
                          organization_id UUID NOT NULL,
                          is_active BOOLEAN DEFAULT true,
                          daily_request_limit INTEGER NOT NULL DEFAULT 1000,
                          monthly_request_limit INTEGER NOT NULL DEFAULT 30000,
                          created_at TIMESTAMP,
                          expires_at TIMESTAMP
);