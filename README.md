Library Management System

A console-based Library Management System developed using Java, MySQL and JDBC. The application helps manage books, members, user accounts, book issuing, book returns and fine calculations.

Features

1. Member

- Register and log in.
- View available books.
- Search books by name or author.
- View issued books.
- View fine details.
- Issue and return books.

2. Admin

- Admin login.
- Add, view, search, update and delete books.
- View all members.
- Search members by name.
- Update and delete member details.
- Create librarian accounts.

3. Librarian

- Librarian login.
- View available books.
- Search books by name or author.
- View all members.
- Issue books to members.
- Return books for members.

Technologies Used

- Java
- MySQL
- JDBC
- BCrypt (password hashing)
- Git
- GitHub

Database

Database name: "library_management"

Tables:

- "book"
- "member"
- "user"
- "book_issue"

Business Rules

- A member can have a maximum of 4 active books.
- The borrowing period is 7 days.
- A fine is charged from the 8th day onward.
- The fine is 5% of the book price per late day.
- A member cannot issue the same book again while it is already issued.
- Available book copies are updated during issue and return.

Project Structure

LibraryManagementSystem/
├── src/
│   ├── app/
│   ├── model/
│   ├── dao/
│   ├── service/
│   └── util/
├── lib/
├── out/
├── .gitignore
└── README.md

Required Software

- JDK
- MySQL Server
- MySQL Workbench
- MySQL Connector/J
- jbcrypt library

Setup Instructions

1. Clone this repository:

git clone YOUR_GITHUB_REPOSITORY_URL

2. Open MySQL Workbench.

3. Create the database:

CREATE DATABASE library_management;

4. Create the required tables using the database schema for this project.

5. Configure your database username and password using environment variables:

DB_USERNAME
DB_PASSWORD

6. Make sure the required JAR files are available inside the "lib" folder.

Compile and Run

Compile:

javac -cp "lib\*;out" -d out src\app\main.java

Run:

java -cp "lib\*;out" app.main

Security

- Passwords are hashed using BCrypt.
- Database credentials can be supplied through environment variables.
- Git ignores compiled files and local configuration files.

Project Status

Core Member, Admin and Librarian features have been implemented and tested.

Author

Developed as a Java and MySQL project.