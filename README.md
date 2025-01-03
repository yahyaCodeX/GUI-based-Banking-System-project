### **Banking System GUI-Based Java Application**
<br>
This is a **Java-based GUI banking system** designed to simulate core banking operations such as user registration, login, money credit (deposit), money debit (withdrawal), transferring funds between accounts, checking account balance, and logging out. The application uses a **Graphical User Interface (GUI)** built with **Java Swing** and interacts with a **MySQL database** for storing user and account information.

---
<br>
### **Key Features**:
<br>
1. **User Registration and Account Creation**:  
   - Allows new users to register by entering their **full name**, **email**, and **password**.
   - Once registered, a new user account is created in the **user** table of the database with an initial zero balance.
   - Users are then redirected to the **login page** to access their account.
<br>
2. **Login**:  
   - Registered users can log in by entering their **email** and **password**.
   - The system authenticates the login credentials by checking the **user** table in the database.
   - Upon successful login, users gain access to their account menu, where they can perform banking operations.
<br>
3. **Money Credit (Deposit)**:  
   - Users can add money to their account by specifying an amount to credit.
   - The system ensures that only positive amounts are accepted and updates the balance in the **accounts** table.
   - A confirmation message is shown with the updated balance.
<br>
4. **Money Debit (Withdraw)**:  
   - Users can withdraw money from their account.
   - The system checks if the user has sufficient balance before allowing the withdrawal.
   - The **balance** is updated accordingly after the withdrawal, and the new balance is displayed.
<br>
5. **Money Transfer**:  
   - Users can transfer money to another account by specifying the **receiver's account number**, **amount**, and their **password** for authentication.
   - The system checks for sufficient funds and updates the balances of both the sender and receiver accounts.
   - A confirmation message is shown for the successful transfer.
<br>
6. **Check Balance**:  
   - Users can check their current account balance at any time.
   - The system fetches the balance from the **accounts** table and displays it to the user.
<br>
7. **Check Account Number**:  
   - Users can view their account number after successful login.
   - This unique identifier is displayed for reference during operations like money transfer.
<br>
8. **Logout**:  
   - The application provides a **logout** option to terminate the session securely.
   - Upon logging out, all session data (like account details) is cleared, and users are redirected to the login screen.

---
<br>
### **Technologies Used**:
- **Java Swing**: For building the GUI of the banking system, making the application interactive and user-friendly.
- **MySQL**: For storing user and account data, including login credentials, account balance, and transaction history.
- **JDBC**: For connecting the Java application to the MySQL database and performing operations like inserting, updating, and querying data.
  
---
<br>
### **Database Structure**:
The database consists of two main tables:
1. **user** table:
   - `full_name` (varchar): Stores the user's full name.
   - `email` (varchar, primary key): Stores the user's email address (unique identifier).
   - `password` (varchar): Stores the user's password.
<br>  
2. **accounts** table:
   - `account_number` (int, primary key): Unique identifier for each user’s account.
   - `full_name` (varchar): Stores the user's name (linked to the `user` table).
   - `email` (varchar, unique): Stores the user's email (linked to the `user` table).
   - `balance` (decimal): Stores the current balance of the user.
   - `security_pin` (char): Stores a unique pin used for transaction authentication.

---
<br>
### **How to Use**:
1. **Clone or Download the Repository**:
   - Clone the repository using `git clone` or download the ZIP file from GitHub.

2. **Set Up the MySQL Database**:
   - Create a MySQL database and run the provided SQL queries to create the **user** and **accounts** tables.

3. **Run the Java Application**:
   - Open the project in an IDE (like IntelliJ IDEA or Eclipse).
   - Make sure the MySQL server is running, and the database is set up.
   - Run the Java application, and the GUI will appear for user interaction.

4. **Perform Banking Operations**:
   - Register a new user, log in with their credentials, and perform actions like depositing money, transferring funds, checking balance, etc.

---

### **Future Enhancements**:
- Adding **transaction history** for users to view their past transactions.
- Implementing **multi-user authentication** with different user roles (e.g., admin and customer).
- Integrating **security features** such as password encryption and secure money transfers.

---

Feel free to explore, modify, or contribute to this project!
