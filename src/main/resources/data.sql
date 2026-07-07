INSERT INTO system_accounts (email, password_hash, full_name, role, is_active, created_at)
SELECT 'manager@insureflow.com', '$2b$10$9MgjifbzABsPw5jlba0LUODsFxfaIwVLBB3FqXZXfFiFeIeMdmXo2', 'Admin Manager', 'INSURANCE_MANAGER', true, NOW()
WHERE NOT EXISTS (
    SELECT 1 FROM system_accounts WHERE email = 'manager@insureflow.com'
);
