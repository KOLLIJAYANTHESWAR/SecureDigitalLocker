# 🔐 Secure Digital Locker

A Role-Based Secure Digital Document Management System built using **Spring Boot, MySQL, JWT, and GCP-ready storage architecture**.

This system allows hierarchical user creation and approval workflows with secure document upload and audit tracking.

---

## 📌 Project Overview

Secure Digital Locker is a backend system designed to:

- Manage hierarchical users (ADMIN → HR → MANAGER → USER)
- Enforce approval workflows before activation
- Secure document upload & access control
- Maintain detailed audit logs
- Support both Local and GCP Cloud Storage

---

## 🏗️ Architecture

```
Client → REST API → Service Layer → Repository Layer → MySQL
                         ↓
                  Storage Layer (Local / GCP)
```

### Tech Stack

- **Backend:** Spring Boot
- **Security:** Spring Security + JWT
- **Database:** MySQL 8
- **ORM:** Hibernate (JPA)
- **Storage:** Local File System / Google Cloud Storage
- **Build Tool:** Maven
- **Testing:** Shell API Test Script (curl-based)

---

## 🔐 Role Hierarchy

| Role     | Can Create | Can Approve |
|----------|------------|------------|
| ADMIN    | HR         | HR         |
| HR       | MANAGER    | MANAGER    |
| MANAGER  | USER       | USER       |
| USER     | —          | —          |

Each created user must be **approved by their superior** before login access.

---

## 📦 Features

### ✅ Authentication
- JWT-based login
- Role-based authorization
- Secure password hashing (BCrypt)

### ✅ User Management
- Hierarchical user creation
- Approval workflow
- Status tracking (PENDING / APPROVED / REJECTED)
- Soft deactivation support

### ✅ Profile Management
- View & update profile
- Upload profile picture

### ✅ Document Management
- Upload documents
- Secure document retrieval
- Document visibility control

### ✅ Audit Logging
Tracks:
- LOGIN
- USER_CREATED
- APPROVAL
- UPLOAD
- DOWNLOAD
- DELETE

---

## 🗄️ Database Structure

Main Tables:

- `users`
- `profiles`
- `documents`
- `document_visibility`
- `approval_requests`
- `audit_logs`

Foreign key constraints enforce data integrity.

---

## ⚙️ Configuration

Application uses environment-based configuration.

Example config file:

```
src/main/resources/application-example.properties
```

You must create:

```
application.properties
```

With your own:

- Database credentials
- JWT secret
- Storage configuration

---

## 🚀 Running Locally

### 1️⃣ Clone Repository

```bash
git clone https://github.com/YOUR_USERNAME/SecureDigitalLocker.git
cd SecureDigitalLocker
```

### 2️⃣ Setup MySQL

Create database:

```sql
CREATE DATABASE dgll;
```

### 3️⃣ Configure application.properties

Add your MySQL credentials.

### 4️⃣ Run Application

```bash
./mvnw spring-boot:run
```

Server runs on:

```
http://localhost:8080
```

---

## 🧪 API Test Script

Automated test script:

```
api_test.sh
```

It performs:

- Admin login
- HR creation & approval
- Manager creation & approval
- User creation & approval
- Document upload

Run:

```bash
chmod +x api_test.sh
./api_test.sh
```



## 🌩️ GCP Ready

Project includes:

- `GcpStorageService`
- Configurable storage type:
  ```
  storage.type=local
  storage.type=gcp
  ```

Can be deployed to:

- Google Cloud Run
- Google Cloud SQL
- Google Cloud Storage

---

## 🔒 Security Highlights

- BCrypt password hashing
- JWT token expiration
- Role-based endpoint protection
- Foreign key enforcement
- Centralized exception handling

---

## 📈 Future Improvements

- Docker support
- CI/CD pipeline
- Swagger documentation
- Pagination support
- Unit & integration tests
- Production environment profiles

---

## 👨‍💻 Author

**Kolli Jayanth Eswar**

Backend Developer | Spring Boot | Cloud Ready Systems

---

## 📜 License

This project is licensed under the MIT License.