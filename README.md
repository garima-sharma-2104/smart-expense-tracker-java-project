# Smart Expense Tracker – Java Desktop Application

A feature-rich personal finance management application built using **Java Swing and SQLite**. Smart Expense Tracker helps users manage their income and expenses, monitor financial activity, visualize spending patterns, and split shared expenses through its integrated group expense management system, SplitSphere.

The application provides a clean desktop interface with authentication, interactive dashboards, transaction management, graphical analytics, and customizable Light and Dark themes.

## ✨ Features

### 1. User Authentication
- User registration and login functionality.
- Individual user data management.
- Separate financial records for application users.

### 2. Dashboard Overview
- Overview of income, expenses, and net balance.
- Monthly financial summaries.
- Quick access to important financial information.

### 3. Expense Management
- Add and manage daily expenses.
- View transaction records in organized tables.
- Categorize and monitor spending.
- Track individual expense details.

### 4. Income Management
- Record and manage income transactions.
- View income records.
- Monitor overall financial activity.

### 5. Graphical Analytics
- Visual representation of financial activity.
- Monthly spending charts.
- Category-wise expense summaries.
- Custom Java 2D-based graphical components.

### 6. SplitSphere – Group Expense Management
- Create independent expense-sharing groups.
- Add members to groups.
- Record shared group expenses.
- Calculate individual contributions and outstanding balances.
- Simplify the process of splitting bills among members.

### 7. Light and Dark Mode
- Switch between Light and Dark themes.
- Consistent styling across application components.
- Customized Swing interface elements.

## 🛠️ Technology Stack

| Technology | Purpose |
|---|---|
| Java 17+ | Core programming language |
| Java Swing | Desktop graphical user interface |
| SQLite | Local database management |
| JDBC | Database connectivity |
| Maven | Dependency and build management |
| Java 2D | Graphical charts and visual components |
| Eclipse IDE | Development environment |

## 🏗️ Project Structure

```text
Smart-Expense-Tracker/
│
├── src/
│   └── main/
│       └── java/
│           └── com/
│               └── expensetracker/
│                   ├── db/
│                   ├── model/
│                   ├── service/
│                   ├── ui/
│                   └── App.java
│
├── lib/
│   └── External JAR Libraries
│
├── pom.xml
├── README.md
├── build.bat
├── run.bat
├── test.bat
└── .gitignore
```

**Package Overview:**

- `db/` – Database connectivity and database operations.
- `model/` – Application data models.
- `service/` – Business logic and application services.
- `ui/` – Swing-based user interface components.
- `App.java` – Application entry point.

## ⚙️ Installation and Setup

### Prerequisites

Make sure the following are installed:

- Java Development Kit (JDK 17 or later).
- Apache Maven.
- Git (optional, for cloning the repository).

### Step 1: Clone the Repository

```bash
git clone https://github.com/garima-sharma-2104/smart-expense-tracker-java-project.git
```

### Step 2: Navigate to the Project Directory

```bash
cd smart-expense-tracker-java-project
```

### Step 3: Build the Application

Using Maven:

```bash
mvn clean package
```

Alternatively, on Windows, use the provided batch script:

```cmd
build.bat
```

### Step 4: Run the Application

Using the provided Windows batch script:

```cmd
run.bat
```

### Step 5: Run Diagnostic Tests

```cmd
test.bat
```

**Note:** The batch scripts provide an alternative execution workflow using the project's existing library configuration.

## 🗄️ Database

The application uses **SQLite** as its local database.

The database manages information related to:

- Users
- Expenses
- Income
- Groups
- Group Members
- Group Expenses

Database operations are handled through JDBC.

The application uses local database storage, so financial records are maintained on the machine where the application runs.

## 🔮 Future Enhancements

- Export financial reports in PDF and CSV formats.
- Advanced date-based transaction filtering.
- More detailed financial analytics and visualizations.
- Configurable storage and application settings.
- Improved financial insights and spending recommendations.
- Enhanced security and data protection.

## 🔒 Security and Privacy

- The application uses local SQLite storage.
- The repository does not include the local application database.
- Users should avoid committing personal financial records, credentials, or sensitive configuration files.
- Additional password hashing and security improvements are recommended before production use.

## 👨‍💻 Author

**Smart Expense Tracker**

A Java-based desktop application developed to simplify personal financial management and group expense tracking.

---

⭐ If you find this project useful, consider giving the repository a star!

**Repository:** [Smart Expense Tracker – Java Project](https://github.com/garima-sharma-2104/smart-expense-tracker-java-project)
