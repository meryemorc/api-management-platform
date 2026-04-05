CREATE TABLE organizations (
                               id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                               name VARCHAR(255) NOT NULL,
                               slug VARCHAR(255) NOT NULL UNIQUE,
                               plan VARCHAR(50) NOT NULL,
                               is_active BOOLEAN DEFAULT true,
                               created_at TIMESTAMP,
                               updated_at TIMESTAMP
);

CREATE TABLE organization_members (
                                      id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                                      user_id UUID NOT NULL,
                                      organization_id UUID NOT NULL REFERENCES organizations(id),
                                      role VARCHAR(50) NOT NULL,
                                      joined_at TIMESTAMP,
                                      UNIQUE(user_id, organization_id)
);