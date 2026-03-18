![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)
🚀 Insure-AI – Insurance Management System

A full-stack insurance management platform built using Spring Boot and modern frontend technologies to streamline policy handling, user management, and agent operations with secure and scalable architecture.

📌 Overview

Insure-AI is designed to simplify and automate insurance workflows by providing a centralized system for managing users, policies, and administrative operations with role-based access.

🚀 Features

Customer authentication and registration, Admin dashboard and access control, Agent availability and scheduling management, Customer appointment scheduling system, Policy management with CRUD and soft delete, Role-based workflows for Admin, Agent, and Customer, Location-based services using pincode mapping, RESTful API integration, Secure backend with Spring Security, MySQL database integration

🛠️ Tech Stack

Frontend: React / Angular (update if needed)
Backend: Java, Spring Boot, Spring Security
Database: MySQL
Build Tool: Maven
Version Control: Git & GitHub

📁 Project Structure
Insure-AI
 ├── Insurance_backend
 └── Insurance_frontend
🔄 System Architecture
User (Frontend)
   ↓
API Calls (Axios / Fetch)
   ↓
Spring Boot Controller
   ↓
Service Layer
   ↓
Repository Layer
   ↓
MySQL Database
   ↓
Response → Frontend
⚙️ Setup & Run
🔹 Clone Repository
git clone https://github.com/kollasumanth2357/Insure-AI
cd Insure-AI
🔹 Run Backend
cd Insurance_backend
mvn spring-boot:run
🔹 Run Frontend
cd Insurance_frontend
npm install
npm start
🔐 Security

Role-based access control

Secure authentication using Spring Security

Protected REST APIs

📄 License

This project is licensed under the MIT License.

👨‍💻 Author

Kolla Sumanth

⭐ Support

If you like this project, give it a ⭐ on GitHub!
