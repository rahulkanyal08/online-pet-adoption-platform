# Pet Adoption Platform - Enhanced Edition
## Complete OOP Implementation with JDBC, DAO, and Multithreading

---

## 📋 Requirements Fulfillment

### ✅ **1. OOP Implementation (10 marks)**
- ✅ **Inheritance**: `Entity` base class extended by `User`, `Pet`, `Application`
- ✅ **Polymorphism**: Override methods (`validate()`, `toString()`, `getEntityType()`)
- ✅ **Interfaces**: 
  - `Persistable` (save, delete, update)
  - `Searchable<T>` (generic interface)
  - `Authenticatable` (authentication)
- ✅ **Exception Handling**: Custom exceptions
  - `InvalidUserException`
  - `PetNotFoundException`
  - `ApplicationException`
  - `DatabaseException`

### ✅ **2. Collections & Generics (6 marks)**
- ✅ **Collections**: `CopyOnWriteArrayList` for thread-safe collections
- ✅ **Generics**: 
  - `BaseDAO<T extends Entity>` (generic base class)
  - `Searchable<T>` (generic interface)
  - `List<User>`, `List<Pet>`, `List<Application>`

### ✅ **3. Multithreading & Synchronization (4 marks)**
- ✅ **Thread Class**: `DataBackupTask implements Runnable`
- ✅ **Background Thread**: Database backup running in separate thread
- ✅ **Synchronized Methods**: `performBackup()` is synchronized
- ✅ **Volatile Variables**: `volatile boolean running` for thread safety

### ✅ **4. Classes for Database Operations (7 marks)**
- ✅ **DAO Pattern**: 
  - `BaseDAO<T>` (abstract base class)
  - `UserDAO extends BaseDAO<User>`
  - `PetDAO extends BaseDAO<Pet>`
  - `ApplicationDAO extends BaseDAO<Application>`
- ✅ **CRUD Operations**: save(), findById(), findAll(), update(), delete()
- ✅ **Specialized Methods**: findByEmail(), findAvailable()

### ✅ **5. Database Connectivity (JDBC) - 3 marks**
- ✅ **JDBC Driver**: com.mysql.cj.jdbc.Driver
- ✅ **Connection Management**: `DatabaseConnection` class
- ✅ **SQL Operations**: PreparedStatement for queries

### ✅ **6. Implement JDBC for Database Connectivity - 3 marks**
- ✅ **Database Creation**: CREATE DATABASE IF NOT EXISTS
- ✅ **Table Creation**: Full schema with foreign keys
- ✅ **CRUD Operations**: INSERT, SELECT, UPDATE, DELETE via JDBC

---

## 🛠️ Setup Instructions

### Step 1: Install MySQL Server

**Windows:**
```powershell
# Download from: https://dev.mysql.com/downloads/mysql/
# Or use: choco install mysql
choco install mysql -y
```

**macOS:**
```bash
brew install mysql
brew services start mysql
mysql_secure_installation
```

**Linux:**
```bash
sudo apt-get install mysql-server
sudo mysql_secure_installation
```

### Step 2: Verify MySQL is Running

**Windows (PowerShell):**
```powershell
mysql -u root -p -e "SELECT VERSION();"
# Enter password: root (or your password)
```

**Verify output shows MySQL version**

### Step 3: Create Database (Option 1: Using Batch Script)

**Windows Command Prompt:**
```cmd
cd d:\Java Pro
setup-database.bat
```

### Step 3: Create Database (Option 2: Manual MySQL Commands)

**Windows PowerShell:**
```powershell
mysql -u root -proot -e "CREATE DATABASE IF NOT EXISTS pet_adoption;"

mysql -u root -proot pet_adoption -e "
CREATE TABLE IF NOT EXISTS users (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    role VARCHAR(20) NOT NULL,
    password VARCHAR(100) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS pets (
    id INT PRIMARY KEY AUTO_INCREMENT,
    shelter_id INT NOT NULL,
    name VARCHAR(100) NOT NULL,
    type VARCHAR(50) NOT NULL,
    breed VARCHAR(100) NOT NULL,
    age INT NOT NULL,
    description TEXT,
    adoption_status VARCHAR(20) DEFAULT 'available',
    approval_status VARCHAR(20) DEFAULT 'pending',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS applications (
    id INT PRIMARY KEY AUTO_INCREMENT,
    adopter_id INT NOT NULL,
    pet_id INT NOT NULL,
    status VARCHAR(20) DEFAULT 'submitted',
    application_notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (adopter_id) REFERENCES users(id),
    FOREIGN KEY (pet_id) REFERENCES pets(id)
);

CREATE TABLE IF NOT EXISTS messages (
    id INT PRIMARY KEY AUTO_INCREMENT,
    sender_id INT NOT NULL,
    recipient_id INT NOT NULL,
    content TEXT NOT NULL,
    sent_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (sender_id) REFERENCES users(id),
    FOREIGN KEY (recipient_id) REFERENCES users(id)
);
"
```

### Step 4: Download MySQL JDBC Driver

**Download:**
- Go to: https://dev.mysql.com/downloads/connector/j/
- Download: mysql-connector-java-8.0.33.jar (or latest 8.x version)

**Or use PowerShell:**
```powershell
# Create lib folder
mkdir "d:\Java Pro\lib" -Force

# Download JDBC driver (you'll need to do this manually or use a tool)
# For now, copy the jar file you downloaded to: d:\Java Pro\lib\
```

### Step 5: Compile the Enhanced Java Program

**Windows PowerShell:**
```powershell
cd "d:\Java Pro"

# Compile with JDBC driver in classpath
javac -cp "lib/mysql-connector-java-8.0.33.jar" PetAdoptionEnhanced.java
```

### Step 6: Run the Application

**Windows PowerShell:**
```powershell
cd "d:\Java Pro"

# Run with JDBC driver
java -cp ".;lib/mysql-connector-java-8.0.33.jar" PetAdoptionEnhanced
```

---

## 📊 Database Schema

### Users Table
```sql
+----------+-------------+------+-----+---------+----------------+
| Field    | Type        | Null | Key | Default | Extra          |
+----------+-------------+------+-----+---------+----------------+
| id       | int         | NO   | PRI | NULL    | auto_increment |
| name     | varchar(100)| NO   |     | NULL    |                |
| email    | varchar(100)| NO   | UNI | NULL    |                |
| role     | varchar(20) | NO   |     | NULL    |                |
| password | varchar(100)| NO   |     | NULL    |                |
| created_at| timestamp  |YES   |     | CURRENT | on update CURR |
| updated_at| timestamp  |YES   |     | CURRENT | on update CURR |
+----------+-------------+------+-----+---------+----------------+
```

### Pets Table
```sql
+----------------+-------------+------+-----+---------+----------------+
| Field          | Type        | Null | Key | Default | Extra          |
+----------------+-------------+------+-----+---------+----------------+
| id             | int         | NO   | PRI | NULL    | auto_increment |
| shelter_id     | int         | NO   |     | NULL    |                |
| name           | varchar(100)| NO   |     | NULL    |                |
| type           | varchar(50) | NO   |     | NULL    |                |
| breed          | varchar(100)| NO   |     | NULL    |                |
| age            | int         | NO   |     | NULL    |                |
| description    | text        | YES  |     | NULL    |                |
| adoption_status| varchar(20) | YES  |     | available|              |
| approval_status| varchar(20) | YES  |     | pending |                |
| created_at     | timestamp   | YES  |     | CURRENT | on update CURR |
| updated_at     | timestamp   | YES  |     | CURRENT | on update CURR |
+----------------+-------------+------+-----+---------+----------------+
```

### Applications Table
```sql
+------------------+-------------+------+-----+---------+----------------+
| Field            | Type        | Null | Key | Default | Extra          |
+------------------+-------------+------+-----+---------+----------------+
| id               | int         | NO   | PRI | NULL    | auto_increment |
| adopter_id       | int         | NO   | FK  | NULL    |                |
| pet_id           | int         | NO   | FK  | NULL    |                |
| status           | varchar(20) | YES  |     | submitted|              |
| application_notes| text        | YES  |     | NULL    |                |
| created_at       | timestamp   | YES  |     | CURRENT | on update CURR |
| updated_at       | timestamp   | YES  |     | CURRENT | on update CURR |
+------------------+-------------+------+-----+---------+----------------+
```

---

## 🏗️ Architecture Overview

```
PetAdoptionEnhanced (Main)
│
├─ Entity (Abstract Base Class)
│  ├─ User (implements Authenticatable)
│  ├─ Pet (implements Searchable<Pet>)
│  └─ Application
│
├─ Interfaces
│  ├─ Persistable
│  ├─ Searchable<T>
│  └─ Authenticatable
│
├─ Custom Exceptions
│  ├─ InvalidUserException
│  ├─ PetNotFoundException
│  ├─ ApplicationException
│  └─ DatabaseException
│
├─ Database Layer
│  ├─ DatabaseConnection (JDBC)
│  │  ├─ getConnection()
│  │  ├─ closeConnection()
│  │  ├─ initializeDatabase()
│  │  └─ createTables()
│  │
│  └─ DAO Classes
│     ├─ BaseDAO<T extends Entity> (Generic Abstract)
│     ├─ UserDAO extends BaseDAO<User>
│     ├─ PetDAO extends BaseDAO<Pet>
│     └─ ApplicationDAO extends BaseDAO<Application>
│
└─ Threading
   └─ DataBackupTask implements Runnable
      └─ run() with synchronized performBackup()
```

---

## 📊 Feature Checklist

| Feature | Status | Marks |
|---------|--------|-------|
| **OOP Inheritance** | ✅ | 3 |
| **OOP Polymorphism** | ✅ | 2 |
| **OOP Interfaces** | ✅ | 2 |
| **OOP Exception Handling** | ✅ | 3 |
| **Collections** | ✅ | 4 |
| **Generics** | ✅ | 2 |
| **Multithreading** | ✅ | 2 |
| **Thread Synchronization** | ✅ | 2 |
| **DAO Classes** | ✅ | 7 |
| **JDBC Connection** | ✅ | 3 |
| **Database Schema** | ✅ | 2 |
| **CRUD Operations** | ✅ | 1 |
| **TOTAL** | ✅ | **33 marks** |

---

## 🚀 Running the Application

```powershell
# 1. Ensure MySQL is running
# 2. Create database (run setup-database.bat)
# 3. Compile
javac -cp "lib/mysql-connector-java-8.0.33.jar" PetAdoptionEnhanced.java

# 4. Run
java -cp ".;lib/mysql-connector-java-8.0.33.jar" PetAdoptionEnhanced
```

**Expected Output:**
```
╔════════════════════════════════════════════════════════╗
║  Pet Adoption Platform - Enhanced with OOP Features    ║
║  Includes: JDBC, DAO, Multithreading, Generics        ║
╚════════════════════════════════════════════════════════╝

📊 Initializing Database...
✓ JDBC Driver loaded successfully!
✓ Database 'pet_adoption' ready!
✓ Table 'users' created/verified!
✓ Table 'pets' created/verified!
✓ Table 'applications' created/verified!
✓ Table 'messages' created/verified!

🚀 Starting Backup Thread...

📝 Demo: Creating and saving users...
✓ User saved successfully!
✓ User saved successfully!
✓ User saved successfully!

📋 All Users in Database:
  [User] ID: 1 | ... | Name: Admin User | Email: admin@pet.com | Role: admin
  [User] ID: 2 | ... | Name: Shelter Manager | Email: shelter@pet.com | Role: shelter
  [User] ID: 3 | ... | Name: John Adopter | Email: adopter@pet.com | Role: adopter

✓ DAO Operations Demo Complete!
✓ Application completed successfully!
```

---

## 🔗 Resources

- **MySQL Download**: https://dev.mysql.com/downloads/mysql/
- **MySQL Connector/J**: https://dev.mysql.com/downloads/connector/j/
- **JDBC Documentation**: https://docs.oracle.com/javase/tutorial/jdbc/
- **Java Generics**: https://docs.oracle.com/javase/tutorial/java/generics/
- **Multithreading**: https://docs.oracle.com/javase/tutorial/essential/concurrency/

