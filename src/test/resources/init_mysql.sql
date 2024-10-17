-- Replace 'your_database', 'your_user', and 'your_password' with your desired names and password.

-- Create the database
CREATE DATABASE IF NOT EXISTS testdb;

-- Create a new user and grant access to the new database
CREATE USER 'db-user'@'%' IDENTIFIED BY 'Password1';

-- Grant all privileges on the new database to the new user
GRANT ALL PRIVILEGES ON testdb.* TO 'db-user'@'%';

-- Apply changes
FLUSH PRIVILEGES;


-- Switch to the user_transactions database
USE testdb;

-- Create the Users table
CREATE TABLE IF NOT EXISTS Users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL,
    email VARCHAR(100) NOT NULL,
    password VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

--- Insert sample data into the Users table
INSERT INTO Users (username, email, password) VALUES
('john_doe', 'john@example.com', 'password123'),
('jane_smith', 'jane@example.com', 'password456'),
('alice_jones', 'alice@example.com', 'password789');

COMMIT;