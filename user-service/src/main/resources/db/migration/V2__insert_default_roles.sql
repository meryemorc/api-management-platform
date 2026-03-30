INSERT INTO roles (id, name, description) VALUES
                                              (gen_random_uuid(), 'SUPER_ADMIN', 'Platform sahibi, her şeye erişebilir'),
                                              (gen_random_uuid(), 'ORG_ADMIN', 'Organizasyon yöneticisi'),
                                              (gen_random_uuid(), 'ORG_DEVELOPER', 'API key oluşturabilir, limit ayarlayabilir'),
                                              (gen_random_uuid(), 'ORG_VIEWER', 'Sadece görüntüleyebilir'),
                                              (gen_random_uuid(), 'ORG_MEMBER', 'Organizasyona dahil, yetki verilmemiş');

INSERT INTO permissions (id, name, description) VALUES
                                                    (gen_random_uuid(), 'USER_READ', 'Kullanıcı bilgilerini görüntüle'),
                                                    (gen_random_uuid(), 'USER_CREATE', 'Yeni kullanıcı oluştur'),
                                                    (gen_random_uuid(), 'USER_UPDATE', 'Kullanıcı bilgilerini güncelle'),
                                                    (gen_random_uuid(), 'USER_DELETE', 'Kullanıcı sil'),
                                                    (gen_random_uuid(), 'ROLE_READ', 'Rolleri görüntüle'),
                                                    (gen_random_uuid(), 'ROLE_ASSIGN', 'Kullanıcıya rol ata'),
                                                    (gen_random_uuid(), 'ORG_READ', 'Organizasyon bilgilerini görüntüle'),
                                                    (gen_random_uuid(), 'ORG_CREATE', 'Yeni organizasyon oluştur'),
                                                    (gen_random_uuid(), 'ORG_UPDATE', 'Organizasyon bilgilerini güncelle'),
                                                    (gen_random_uuid(), 'ORG_DELETE', 'Organizasyon sil'),
                                                    (gen_random_uuid(), 'API_KEY_READ', 'API key''leri görüntüle'),
                                                    (gen_random_uuid(), 'API_KEY_CREATE', 'Yeni API key oluştur'),
                                                    (gen_random_uuid(), 'API_KEY_UPDATE', 'API key güncelle'),
                                                    (gen_random_uuid(), 'API_KEY_DELETE', 'API key sil'),
                                                    (gen_random_uuid(), 'API_KEY_ROTATE', 'API key yenile'),
                                                    (gen_random_uuid(), 'LIMIT_READ', 'Rate limit bilgilerini görüntüle'),
                                                    (gen_random_uuid(), 'LIMIT_UPDATE', 'Rate limit güncelle'),
                                                    (gen_random_uuid(), 'ANALYTICS_READ', 'Analytics verilerini görüntüle'),
                                                    (gen_random_uuid(), 'BILLING_READ', 'Fatura bilgilerini görüntüle'),
                                                    (gen_random_uuid(), 'BILLING_MANAGE', 'Fatura yönet'),
                                                    (gen_random_uuid(), 'WEBHOOK_READ', 'Webhook bilgilerini görüntüle'),
                                                    (gen_random_uuid(), 'WEBHOOK_MANAGE', 'Webhook yönet');

INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id FROM roles r, permissions p
WHERE r.name = 'SUPER_ADMIN';

INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id FROM roles r, permissions p
WHERE r.name = 'ORG_ADMIN'
  AND p.name IN ('ORG_READ', 'ORG_UPDATE', 'API_KEY_READ', 'API_KEY_CREATE',
                 'API_KEY_UPDATE', 'API_KEY_DELETE', 'API_KEY_ROTATE',
                 'LIMIT_READ', 'LIMIT_UPDATE', 'ANALYTICS_READ',
                 'BILLING_READ', 'USER_READ', 'ROLE_READ', 'ROLE_ASSIGN',
                 'WEBHOOK_READ', 'WEBHOOK_MANAGE');

INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id FROM roles r, permissions p
WHERE r.name = 'ORG_DEVELOPER'
  AND p.name IN ('API_KEY_READ', 'API_KEY_CREATE', 'API_KEY_UPDATE',
                 'API_KEY_ROTATE', 'LIMIT_READ', 'ANALYTICS_READ',
                 'WEBHOOK_READ', 'WEBHOOK_MANAGE');

INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id FROM roles r, permissions p
WHERE r.name = 'ORG_VIEWER'
  AND p.name IN ('API_KEY_READ', 'LIMIT_READ', 'ANALYTICS_READ',
                 'BILLING_READ', 'WEBHOOK_READ');

INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id FROM roles r, permissions p
WHERE r.name = 'ORG_MEMBER'
  AND p.name IN ('ORG_READ', 'API_KEY_READ');