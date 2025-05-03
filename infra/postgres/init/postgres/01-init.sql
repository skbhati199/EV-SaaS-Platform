-- Create a separate user schema and assign necessary permissions
CREATE SCHEMA IF NOT EXISTS users;
CREATE SCHEMA IF NOT EXISTS auth;
CREATE SCHEMA IF NOT EXISTS billing;
CREATE SCHEMA IF NOT EXISTS notification;
CREATE SCHEMA IF NOT EXISTS scheduler;

-- Grant permissions
GRANT ALL PRIVILEGES ON SCHEMA users TO evsaas;
GRANT ALL PRIVILEGES ON SCHEMA auth TO evsaas;
GRANT ALL PRIVILEGES ON SCHEMA billing TO evsaas;
GRANT ALL PRIVILEGES ON SCHEMA notification TO evsaas;
GRANT ALL PRIVILEGES ON SCHEMA scheduler TO evsaas;

-- Set search path
ALTER ROLE evsaas SET search_path TO users, auth, billing, notification, scheduler, public;

-- Extensions
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
CREATE EXTENSION IF NOT EXISTS "pgcrypto"; 