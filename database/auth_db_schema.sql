-- Auth Database Schema
-- This database stores only authentication-related information

DROP TABLE IF EXISTS users_auth CASCADE;

CREATE TABLE users_auth (
    user_id SERIAL PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    role VARCHAR(20) NOT NULL CHECK (role IN ('STUDENT', 'INSTRUCTOR', 'ADMIN')),
    password_hash VARCHAR(255) NOT NULL,
    status VARCHAR(20) DEFAULT 'ACTIVE' CHECK (status IN ('ACTIVE', 'INACTIVE', 'LOCKED')),
    last_login TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Insert default users with hashed passwords
-- Password for all users: admin123, inst123, stu123 respectively
INSERT INTO users_auth (username, role, password_hash, status) VALUES
('admin1', 'ADMIN', '$2a$12$v03/8A0lewTceJbV3ow7cOsygxujKapftME70yyjnMt7/AftV.OJW', 'ACTIVE'),
('inst1', 'INSTRUCTOR', '$2a$12$zY3ROl2rYOCpeM7m/Uxd..bh3nVJ2I18LPWe7gkoHGI9mcNvNmw/S', 'ACTIVE'),
('stu1', 'STUDENT', '$2a$12$Htx.KfNJG4WYZzarhw0dX.yWciZwByBsBDuyuW4y3tSdTqrp.Kjmm', 'ACTIVE'),
('stu2', 'STUDENT', '$2a$12$Htx.KfNJG4WYZzarhw0dX.yWciZwByBsBDuyuW4y3tSdTqrp.Kjmm', 'ACTIVE');
