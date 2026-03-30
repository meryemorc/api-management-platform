CREATE TABLE permissions (
                             id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                             name VARCHAR(100) NOT NULL UNIQUE,
                             description VARCHAR(255)
);

CREATE TABLE roles (
                       id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                       name VARCHAR(100) NOT NULL UNIQUE,
                       description VARCHAR(255)
);

CREATE TABLE users (
                       id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                       email VARCHAR(255) NOT NULL UNIQUE,
                       username VARCHAR(100) NOT NULL,
                       password VARCHAR(255),
                       is_active BOOLEAN DEFAULT true,
                       is_email_verified BOOLEAN DEFAULT false,
                       created_at TIMESTAMP,
                       last_login_at TIMESTAMP,
                       provider_id VARCHAR(255),
                       auth_provider VARCHAR(50),
                       CONSTRAINT users_email_unique UNIQUE (email)
);

CREATE TABLE user_roles (
                            user_id UUID REFERENCES users(id),
                            role_id UUID REFERENCES roles(id),
                            PRIMARY KEY (user_id, role_id)
);

CREATE TABLE role_permissions (
                                  role_id UUID REFERENCES roles(id),
                                  permission_id UUID REFERENCES permissions(id),
                                  PRIMARY KEY (role_id, permission_id)
);