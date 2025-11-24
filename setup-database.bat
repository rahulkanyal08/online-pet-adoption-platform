@echo off
REM =====================================================
REM MySQL Database Setup Script for Pet Adoption Platform
REM =====================================================
REM This script creates the database and tables

echo.
echo ╔════════════════════════════════════════════════════╗
echo ║  Pet Adoption Platform - MySQL Setup              ║
echo ╚════════════════════════════════════════════════════╝
echo.

REM Check if MySQL is installed
where mysql.exe >nul 2>nul
if %errorlevel% neq 0 (
    echo ❌ MySQL is not installed or not in PATH
    echo Please install MySQL and add it to system PATH
    pause
    exit /b 1
)

echo ✓ MySQL found!
echo.

REM Connect to MySQL and create database
echo 📊 Creating database...
mysql -u root -proot -e "CREATE DATABASE IF NOT EXISTS pet_adoption;"
if %errorlevel% equ 0 (
    echo ✓ Database created successfully!
) else (
    echo ❌ Failed to create database
    pause
    exit /b 1
)

echo.
echo 📋 Creating tables...

REM Create users table
mysql -u root -proot pet_adoption -e "CREATE TABLE IF NOT EXISTS users (id INT PRIMARY KEY AUTO_INCREMENT, name VARCHAR(100) NOT NULL, email VARCHAR(100) UNIQUE NOT NULL, role VARCHAR(20) NOT NULL, password VARCHAR(100) NOT NULL, created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP);"
echo ✓ Users table created

REM Create pets table
mysql -u root -proot pet_adoption -e "CREATE TABLE IF NOT EXISTS pets (id INT PRIMARY KEY AUTO_INCREMENT, shelter_id INT NOT NULL, name VARCHAR(100) NOT NULL, type VARCHAR(50) NOT NULL, breed VARCHAR(100) NOT NULL, age INT NOT NULL, description TEXT, adoption_status VARCHAR(20) DEFAULT 'available', approval_status VARCHAR(20) DEFAULT 'pending', created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP);"
echo ✓ Pets table created

REM Create applications table
mysql -u root -proot pet_adoption -e "CREATE TABLE IF NOT EXISTS applications (id INT PRIMARY KEY AUTO_INCREMENT, adopter_id INT NOT NULL, pet_id INT NOT NULL, status VARCHAR(20) DEFAULT 'submitted', application_notes TEXT, created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP, FOREIGN KEY (adopter_id) REFERENCES users(id), FOREIGN KEY (pet_id) REFERENCES pets(id));"
echo ✓ Applications table created

REM Create messages table
mysql -u root -proot pet_adoption -e "CREATE TABLE IF NOT EXISTS messages (id INT PRIMARY KEY AUTO_INCREMENT, sender_id INT NOT NULL, recipient_id INT NOT NULL, content TEXT NOT NULL, sent_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, FOREIGN KEY (sender_id) REFERENCES users(id), FOREIGN KEY (recipient_id) REFERENCES users(id));"
echo ✓ Messages table created

echo.
echo ╔════════════════════════════════════════════════════╗
echo ║  ✓ Database Setup Complete!                       ║
echo ║  Database: pet_adoption                           ║
echo ║  User: root                                       ║
echo ║  Password: root                                   ║
echo ╚════════════════════════════════════════════════════╝
echo.

pause
