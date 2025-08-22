# Factory Control System

Welcome to the **Factory Control System**, a powerful and intuitive JavaFX application designed to streamline your factory operations. This system offers seamless integration with a MySQL database, enabling real-time data interaction, efficient query execution, and insightful visual data representation through dynamic charts.

---

## ✨ Key Features

Our Factory Control System is packed with functionalities to give you complete oversight and control:

* **Database Connection**: Easily establish a secure and robust connection to your MySQL database. Simply input your essential credentials, including username, password, the relevant schema name, and the specific table you wish to interact with.

* **Single Process Queries**: Execute a diverse range of SQL queries individually. The results are presented clearly and concisely in a dedicated table view. Available queries include:
    * Displaying all available logs for a comprehensive overview.
    * Identifying and showcasing the **maximum `LoggedValue`** recorded.
    * Calculating and presenting the **sum of `LoggedValue`** for any user-specified `LogID`.
    * Pinpointing the `LineID` associated with the **lowest `LoggedValue`**, visualized effectively in both a table and an interactive chart.
    * Discovering the `LineID` exhibiting the **highest `LoggedValue`** within a custom date range, also presented in a table and a dynamic chart.
    * Efficiently filtering and displaying all data entries where the `LoggedValue` is exactly zero.

* **Multi-threaded Query Execution**: Boost your analytical capabilities with the ability to run multiple selected queries concurrently. Each query operates in its own thread, displaying results in separate tables, which is perfect for parallel data analysis and enhanced responsiveness.

* **Intuitive Graphical User Interface (GUI)**: Crafted with JavaFX, the application boasts a user-friendly interface structured with distinct tabs for effortless navigation:
    * **Connect**: Your gateway to managing all database connection parameters.
    * **One Process**: The hub for executing single, focused queries.
    * **MultiThread**: Where you can unleash the power of parallel query execution.

* **Interactive Tutorial**: A comprehensive, built-in tutorial is integrated directly into the application. It provides step-by-step guidance, ensuring a smooth and efficient onboarding experience for all users.

---

## 🚀 Technologies Powering This System

This project leverages a robust stack of modern technologies to deliver its capabilities:

* **Frontend & GUI**: JavaFX (for rich, desktop-grade user interfaces)
* **Database Connectivity**: MySQL Connector/J (for reliable interaction with MySQL databases)
* **Build Automation**: Apache Maven (for streamlined project build, dependency management, and lifecycle control)
* **Language**: Java (JDK 21 or higher)

---

## 🛠️ Setup and Installation

Getting the Factory Control System up and running on your local machine is straightforward. Follow these instructions carefully:

### Prerequisites

Before you proceed with the installation, please ensure you have the following software installed on your system:

* **Java Development Kit (JDK)**: You'll need **Version 21 or higher**.
* **Apache Maven**: This is essential for building the project and managing its dependencies.
* **MySQL Database**: A functional MySQL database instance is required. Ensure you have a schema and a table (for example, named `mine`) populated with relevant data that the application can interact with.

### Running the Application

Once your prerequisites are met, follow these steps to launch the application:

1.  **Clone the Repository**:
    Begin by cloning the project's source code from its Git repository to your local machine:

    ```bash
    git clone <repository-url>
    ```

2.  **Navigate to the Project Directory**:
    Change your current working directory to the newly cloned project folder:

    ```bash
    cd FactoryControlSystem-cf1759fb5b1419325342229daffd5006913f2f74
    ```

3.  **Build and Launch the Application**:
    Utilize the Maven wrapper script to compile the project, resolve its dependencies, and launch the JavaFX graphical user interface:

    ```bash
    ./mvnw javafx:run
    ```

    *(\*\*Note for Windows users\*\*: You might need to use `mvnw.cmd javafx:run` instead.)*

After successfully launching the application, navigate to the "Connect" tab within the GUI. Here, you can enter your database credentials and begin exploring all the powerful features of the Factory Control System!

---

## 🤝 Project Structure & Contribution

#### Folder Structure
The project follows a standard Maven project structure:
* `src/main/java/sql/demo/`: Contains the main Java source code for the application, including `HelloApplication.java`, `HelloController.java`, `TutorialController.java`, `DatePickerController.java`, and `CustomDatePicker.java`.
* `src/main/resources/sql/demo/`: Contains the FXML files for the JavaFX UI (`hello-view.fxml`, `Tutorial.fxml`, `DatePickerDialog.fxml`).
* `pom.xml`: The Maven Project Object Model file, managing dependencies and build configurations.
* `lib/`: Contains the `mysql-connector-j-8.3.0.jar` for MySQL database connectivity.
* `.mvn/`: Maven wrapper files (`mvnw`, `mvnw.cmd`).

#### Contribution
The current project does not explicitly define contribution guidelines. If you wish to allow contributions, consider adding a section detailing how others can contribute (e.g., by opening issues, submitting pull requests, or reporting bugs).

#### Testing
The `pom.xml` includes JUnit Jupiter API and Engine dependencies for testing. However, the provided files do not contain explicit test instructions or test files. You might consider adding a section on how to run tests if they are implemented.

---

## 📜 Metadata

#### License
The provided `pom.xml` does not explicitly state a license. Common open-source licenses include MIT, Apache 2.0, or GPL. You should choose a license that best suits your project's distribution and usage intentions.

#### Credits
The current README does not include a specific "Credits" section beyond listing the technologies used. You might consider adding one to thank specific contributors, acknowledge inspiration, or list other important dependencies.

#### Badges
The current README does not include GitHub badges. Badges are small, graphical status indicators often placed at the top of a README. You could add badges for:
* **Build Status**: (e.g., using GitHub Actions)
* **License**: To clearly display the chosen license.
* **Stars/Forks**: To show project popularity.
* **Java Version**: To indicate the required JDK version.
