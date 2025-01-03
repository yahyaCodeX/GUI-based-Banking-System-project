import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

//user class
class User {
    private Connection connection;

    public User(Connection connection) {
        this.connection = connection;
    }
//Register new account method
    public void Register(String full_name, String email, String password) {
        if (user_exist(email)) {
            JOptionPane.showMessageDialog(null, "Email already exists");
            return;
        }

        String registerquery = "INSERT INTO user (full_name, email, password) VALUES (?, ?, ?);";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(registerquery);
            preparedStatement.setString(1, full_name);
            preparedStatement.setString(2, email);
            preparedStatement.setString(3, password);
            int rowsaffected = preparedStatement.executeUpdate();
            if (rowsaffected > 0) {
                JOptionPane.showMessageDialog(null, "Registration successful");
            } else {
                JOptionPane.showMessageDialog(null, "Registration failed");
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e.getMessage());
        }
    }
//login method
    public String login(String email, String password) {
        String loginquery = "SELECT * FROM user WHERE email=? AND password=?;";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(loginquery);
            preparedStatement.setString(1, email);
            preparedStatement.setString(2, password);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return email;
            } else {
                return null;
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e.getMessage());
        }
        return null;
    }
//user_exist method checks weather a user exist or not
    public boolean user_exist(String email) {
        String user_existquery = "SELECT * FROM user WHERE email=?;";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(user_existquery);
            preparedStatement.setString(1, email);
            ResultSet resultSet = preparedStatement.executeQuery();
            return resultSet.next();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e.getMessage());
        }
        return false;
    }
}
//Accounts class
class Accounts {
    private Connection connection;

    public Accounts(Connection connection) {
        this.connection = connection;
    }

   //open account method
    public int open_account(String full_name, String email, double initialBalance, String security_pin) {
        if (!account_exist(email)) {
            String open_account_query = "INSERT INTO accounts (account_number,full_name, email, balance, security_pin) VALUES (?,?, ?, ?, ?);";
    
            try {
                int account_number = generate_accountnumber();
                PreparedStatement preparedStatement = connection.prepareStatement(open_account_query);
                preparedStatement.setInt(1, account_number);
                preparedStatement.setString(2, full_name);
                preparedStatement.setString(3, email);
                preparedStatement.setDouble(4, initialBalance);
                preparedStatement.setString(5, security_pin);
                int rowsAffected = preparedStatement.executeUpdate();
                if (rowsAffected > 0) {
                    return account_number;
                } else {
                    JOptionPane.showMessageDialog(null, "Account Creation Failed");
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(null, e.getMessage());
            }
        }
        throw new RuntimeException("Account Already Exists");
    }
    
//getaccountnumber method returns account number of particular email
    public int getAccount_number(String email) {
        String get_Accountnumber_query = "SELECT account_number FROM accounts WHERE email=?;";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(get_Accountnumber_query);
            preparedStatement.setString(1, email);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt("account_number");
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e.getMessage());
        }
        throw new RuntimeException("Account Number Does not exist");
    }
//generateaccountnumber methhod creates a new account number
    public int generate_accountnumber() {
        String last_account_query = "SELECT account_number FROM accounts ORDER BY account_number DESC LIMIT 1;";
        int last_account_number = 10000; // Default account number if none exists
        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(last_account_query);
            if (resultSet.next()) {
                last_account_number = resultSet.getInt("account_number");
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e.getMessage());
        }
        return last_account_number + 1;
    }
//accountexist checks account exist or not
    public boolean account_exist(String email) {
        String account_exist_query = "SELECT * FROM accounts WHERE email=?;";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(account_exist_query);
            preparedStatement.setString(1, email);
            ResultSet resultSet = preparedStatement.executeQuery();
            return resultSet.next();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e.getMessage());
        }
        return false;
    }
//check balance method
    public double check_balance(int account_number) {
        String balance_query = "SELECT balance FROM accounts WHERE account_number=?;";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(balance_query);
            preparedStatement.setInt(1, account_number);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getDouble("balance");
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e.getMessage());
        }
        return 0;
    }
}
//Accountmanager class 
class AccountManager {
    private Connection connection;

    public AccountManager(Connection connection) {
        this.connection = connection;
    }

    public void credit_money(int account_number) {
        String credit_query = "UPDATE accounts SET balance=balance+? WHERE account_number=?;";
        String pinQuery = "SELECT security_pin FROM accounts WHERE account_number=?";
        String pin = JOptionPane.showInputDialog("Enter Security PIN:");

        try {
            PreparedStatement preparedStatement = connection.prepareStatement(pinQuery);
            preparedStatement.setInt(1, account_number);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next() && resultSet.getString("security_pin").equals(pin)) {
                String amount = JOptionPane.showInputDialog("Enter amount to credit:");
                preparedStatement = connection.prepareStatement(credit_query);
                preparedStatement.setDouble(1, Double.parseDouble(amount));
                preparedStatement.setInt(2, account_number);
                preparedStatement.executeUpdate();
                JOptionPane.showMessageDialog(null, "Amount Credited Successfully");
            } else {
                JOptionPane.showMessageDialog(null, "Incorrect Security PIN", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e.getMessage());
        }
    }
//debit money method
    public void debit_money(int account_number) {
        String debit_query = "UPDATE accounts SET balance=balance-? WHERE account_number=?;";
        String pinQuery = "SELECT security_pin FROM accounts WHERE account_number=?";
        String pin = JOptionPane.showInputDialog("Enter Security PIN:");

        try {
            PreparedStatement preparedStatement = connection.prepareStatement(pinQuery);
            preparedStatement.setInt(1, account_number);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next() && resultSet.getString("security_pin").equals(pin)) {
                String amount = JOptionPane.showInputDialog("Enter amount to debit:");
                double debitAmount = Double.parseDouble(amount);
                String balanceQuery = "SELECT balance FROM accounts WHERE account_number=?";
                preparedStatement = connection.prepareStatement(balanceQuery);
                preparedStatement.setInt(1, account_number);
                resultSet = preparedStatement.executeQuery();
                if (resultSet.next() && resultSet.getDouble("balance") >= debitAmount) {
                    preparedStatement = connection.prepareStatement(debit_query);
                    preparedStatement.setDouble(1, debitAmount);
                    preparedStatement.setInt(2, account_number);
                    preparedStatement.executeUpdate();
                    JOptionPane.showMessageDialog(null, "Amount Debited Successfully");
                } else {
                    JOptionPane.showMessageDialog(null, "Insufficient Balance", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(null, "Incorrect Security PIN", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e.getMessage());
        }
    }
//transfer money method
    public void transfer_money(int sender_account_number) {
        String transfer_query = "UPDATE accounts SET balance=balance-? WHERE account_number=?;";
        String receive_query = "UPDATE accounts SET balance=balance+? WHERE account_number=?;";
        String senderPinQuery = "SELECT security_pin, balance FROM accounts WHERE account_number=?";
        
        JPanel transferPanel = new JPanel(new GridLayout(3, 2));
        JTextField amountField = new JTextField();
        JTextField receiverAccountField = new JTextField();
        JTextField pinField = new JTextField();
    
        transferPanel.add(new JLabel("Amount:"));
        transferPanel.add(amountField);
        transferPanel.add(new JLabel("Receiver Account Number:"));
        transferPanel.add(receiverAccountField);
        transferPanel.add(new JLabel("Your Security PIN:"));
        transferPanel.add(pinField);
    
        int result = JOptionPane.showConfirmDialog(null, transferPanel, "Transfer Money", JOptionPane.OK_CANCEL_OPTION);
        
        if (result == JOptionPane.OK_OPTION) {
            String pin = pinField.getText().trim(); // Trim pin input
            try {
                PreparedStatement preparedStatement = connection.prepareStatement(senderPinQuery);
                preparedStatement.setInt(1, sender_account_number);
                ResultSet resultSet = preparedStatement.executeQuery();
    
                if (resultSet.next() && resultSet.getString("security_pin").equals(pin)) {
                    double senderBalance = resultSet.getDouble("balance"); // Get the sender's current balance
                    int receiverAccountNumber = Integer.parseInt(receiverAccountField.getText().trim());
                    double amount = Double.parseDouble(amountField.getText().trim());
    
                    if (amount > senderBalance) {
                        JOptionPane.showMessageDialog(null, "Insufficient balance for this transfer.", "Error", JOptionPane.ERROR_MESSAGE);
                    } else {
                        // Check if receiver account exists
                        String balanceQuery = "SELECT balance FROM accounts WHERE account_number=?";
                        preparedStatement = connection.prepareStatement(balanceQuery);
                        preparedStatement.setInt(1, receiverAccountNumber);
                        resultSet = preparedStatement.executeQuery();
    
                        if (resultSet.next()) {
                            preparedStatement = connection.prepareStatement(transfer_query);
                            preparedStatement.setDouble(1, amount);
                            preparedStatement.setInt(2, sender_account_number);
                            preparedStatement.executeUpdate();
    
                            preparedStatement = connection.prepareStatement(receive_query);
                            preparedStatement.setDouble(1, amount);
                            preparedStatement.setInt(2, receiverAccountNumber);
                            preparedStatement.executeUpdate();
    
                            JOptionPane.showMessageDialog(null, "Transfer Successful");
                        } else {
                            JOptionPane.showMessageDialog(null, "Receiver account does not exist", "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "Incorrect Security PIN", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(null, "Invalid number format. Please check your inputs.", "Error", JOptionPane.ERROR_MESSAGE);
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(null, e.getMessage());
            }
        }
    }
    
}
//Banking System class that manages all classes
public class BankingSystemGUI {
    private Connection connection;
    private User user;
    private Accounts accounts;
    private AccountManager accountManager;
    private JFrame frame;
    // Declare buttons as class-level fields
    private JTextField emailField;
    private JPasswordField passwordField;
    private JButton registerButton;
    private JButton loginButton;
    private JButton openAccountButton;
    private JButton checkBalanceButton;
    private JButton transferMoneyButton;
    private JButton creditMoneyButton;
    private JButton debitMoneyButton;
    private JButton checkaccountButton;

    private String email;
    private int loggedInAccountNumber;


    public BankingSystemGUI() {
        try {
            //connect a database like banking_system from mysql
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/banking_system", "root", "12345");
            user = new User(connection);
            accounts = new Accounts(connection);
            accountManager = new AccountManager(connection);
            initialize();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e.getMessage());
        }
    }
   //reset session method reset all details for new entry
    private void resetSession() {
        user = new User(connection);  // Reset the user object
        accounts = new Accounts(connection);  // Reset the accounts object
    
        // Disable account-related buttons until new login
        openAccountButton.setEnabled(false);
        creditMoneyButton.setEnabled(false);
        debitMoneyButton.setEnabled(false);
        checkBalanceButton.setEnabled(false);
        transferMoneyButton.setEnabled(false);
        checkaccountButton.setEnabled(false);
    }
    //logout method
    private void logout() {
        // Clear all session data
        email = null;
        loggedInAccountNumber = 0;
        resetSession();
    }
    

    
    private void initialize() {
        frame = new JFrame("Banking System");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 400);
        frame.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 10, 10);

        JTextField emailField = new JTextField();
        JPasswordField passwordField = new JPasswordField();
         registerButton = new JButton("Register");
         loginButton = new JButton("Login");
         openAccountButton = new JButton("Open Account");
         checkBalanceButton = new JButton("Check Balance");
         transferMoneyButton = new JButton("Transfer Money");
         creditMoneyButton = new JButton("Credit Money");
         debitMoneyButton = new JButton("Debit Money");
         checkaccountButton=new JButton("check account number");
         JButton logoutButton = new JButton("Logout");

        gbc.gridx = 0;
        gbc.gridy = 0;
        frame.add(new JLabel("Email:"), gbc);
        
        gbc.gridx = 1;
        frame.add(emailField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        frame.add(new JLabel("Password:"), gbc);
        
        gbc.gridx = 1;
        frame.add(passwordField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2; // Make buttons span two columns
        gbc.anchor = GridBagConstraints.CENTER; // Center the buttons
        frame.add(loginButton, gbc);

        gbc.gridy = 3;
        frame.add(registerButton, gbc);

        

   

        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                resetSession();  // Clear previous session data
        
                 email = emailField.getText();
                String password = String.valueOf(passwordField.getPassword());
                String loginEmail = user.login(email, password);
        
                if (loginEmail != null) {
                    JOptionPane.showMessageDialog(null, "Login successful!");
                    boolean hasAccount = accounts.account_exist(email);
                    //condiiton
                    if(hasAccount){
                        loggedInAccountNumber=accounts.getAccount_number(email);
                    }
                    openAccountButton.setEnabled(!hasAccount);
                    checkaccountButton.setEnabled(hasAccount);
        
                    if (hasAccount) {
                        int accountNumber = accounts.getAccount_number(email);
                        checkBalanceButton.setEnabled(true);
                        transferMoneyButton.setEnabled(true);
                        creditMoneyButton.setEnabled(true);
                        debitMoneyButton.setEnabled(true);
                        logoutButton.setEnabled(true);
                    }
                    
                    // Clear email and password fields
                    emailField.setText("");
                    passwordField.setText("");
                } else {
                    JOptionPane.showMessageDialog(null, "Login failed!");
                }
            }
        });

        logoutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Call the clearSession method to reset all session data
                resetSession();
                email=null;
                loggedInAccountNumber=0;
                
                // Optionally, show a logout confirmation message
                JOptionPane.showMessageDialog(frame, "You have been logged out.");
                
                // Reset the GUI state to login screen
                emailField.setEnabled(true);
                passwordField.setEnabled(true);
                loginButton.setEnabled(true);
                registerButton.setEnabled(true);
                logoutButton.setEnabled(false); // Disable logout until next login
                
                // Reset other buttons
                openAccountButton.setEnabled(false);
                checkBalanceButton.setEnabled(false);
                transferMoneyButton.setEnabled(false);
                creditMoneyButton.setEnabled(false);
                debitMoneyButton.setEnabled(false);
                checkaccountButton.setEnabled(false);
            }
        });
        
        
        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Create a new dialog for registration
                JDialog registerDialog = new JDialog(frame, "Register", true);
                registerDialog.setLayout(new GridBagLayout());
                registerDialog.setSize(400, 400);
        
                GridBagConstraints gbc = new GridBagConstraints();
                gbc.fill = GridBagConstraints.HORIZONTAL;
                gbc.insets = new Insets(5, 5, 5, 5);
        
                // Input fields for registration
                JTextField fullNameField = new JTextField();
                JTextField emailField = new JTextField();
                JPasswordField passwordField = new JPasswordField();
                JButton submitButton = new JButton("Submit");

                Dimension fieldSize = new Dimension(200, 25);
                fullNameField.setPreferredSize(fieldSize);
                emailField.setPreferredSize(fieldSize);
                passwordField.setPreferredSize(fieldSize);
        
                gbc.gridx = 0;
                gbc.gridy = 0;
                registerDialog.add(new JLabel("Full Name:"), gbc);
                
                gbc.gridx = 1;
                registerDialog.add(fullNameField, gbc);
        
                gbc.gridx = 0;
                gbc.gridy = 1;
                registerDialog.add(new JLabel("Email:"), gbc);
                
                gbc.gridx = 1;
                registerDialog.add(emailField, gbc);
        
                gbc.gridx = 0;
                gbc.gridy = 2;
                registerDialog.add(new JLabel("Password:"), gbc);
                
                gbc.gridx = 1;
                registerDialog.add(passwordField, gbc);
        
                // Add Submit button to the dialog
                gbc.gridx = 0;
                gbc.gridy = 3;
                gbc.gridwidth = 2;
                gbc.anchor = GridBagConstraints.CENTER;
                registerDialog.add(submitButton, gbc);
        
                // Action for Submit button to register the user
                submitButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        String fullName = fullNameField.getText();
                        String email = emailField.getText();
                        String password = new String(passwordField.getPassword());
        
                        user.Register(fullName, email, password); // Call the registration method
                        registerDialog.dispose(); // Close the dialog after submission
                    }
                });
        
                registerDialog.setVisible(true); // Show the dialog
            }
        });
                
        openAccountButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JPanel inputPanel = new JPanel(new GridLayout(4, 2));
                JTextField fullNameField = new JTextField();
                JTextField emailField = new JTextField();
                JTextField initialBalanceField = new JTextField();
                JPasswordField securityPinField = new JPasswordField();
        
                inputPanel.add(new JLabel("Full Name:"));
                inputPanel.add(fullNameField);
                inputPanel.add(new JLabel("Email:"));
                inputPanel.add(emailField);
                inputPanel.add(new JLabel("Initial Balance:"));
                inputPanel.add(initialBalanceField);
                inputPanel.add(new JLabel("Security PIN:"));
                inputPanel.add(securityPinField);
        
                int result = JOptionPane.showConfirmDialog(null, inputPanel, "Open Account", JOptionPane.OK_CANCEL_OPTION);
                
                if (result == JOptionPane.OK_OPTION) {
                    String fullName = fullNameField.getText();
                    String email = emailField.getText();
                    double initialBalance;
                    String securityPin = String.valueOf(securityPinField.getPassword());
                    try {
                        initialBalance = Double.parseDouble(initialBalanceField.getText());
                        int accountNumber = accounts.open_account(fullName, email, initialBalance, securityPin);
                        JOptionPane.showMessageDialog(null, "Account created! Your account number is: " + accountNumber);
                        loggedInAccountNumber=accountNumber;
                    } catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(null, "Invalid balance format.");
                    } catch (RuntimeException ex) {
                        JOptionPane.showMessageDialog(null, ex.getMessage());
                    }
                }
            }
        });
        

        checkBalanceButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                double balance = accounts.check_balance(loggedInAccountNumber);
                JOptionPane.showMessageDialog(null, "Your balance is: " + balance);
            }
        });

        transferMoneyButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                accountManager.transfer_money(loggedInAccountNumber);
            }
        });
         ///credit money
        creditMoneyButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                accountManager.credit_money(loggedInAccountNumber);
            }
        });
     /////debit money
        debitMoneyButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                accountManager.debit_money(loggedInAccountNumber);
            }
        });
        checkaccountButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    int accountNumber = accounts.getAccount_number(email);
                    JOptionPane.showMessageDialog(null, "Your account number is: " + accountNumber);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(null, ex.getMessage());
                }
            }
        });

        gbc.gridy = 4;
        gbc.gridwidth = 1; // Reset to one column
        gbc.anchor = GridBagConstraints.CENTER; // Center the buttons
        frame.add(openAccountButton, gbc);

        gbc.gridx = 1;
        frame.add(checkBalanceButton, gbc);

        gbc.gridy = 5;
        gbc.gridx = 0;
        frame.add(transferMoneyButton, gbc);

        gbc.gridx = 1; // Column position
         // Row position
        frame.add(checkaccountButton, gbc);

        ////////////////////////////
        gbc.gridy = 6;
        gbc.gridx = 0;
        frame.add(creditMoneyButton, gbc);
    
        gbc.gridx = 1;
        frame.add(debitMoneyButton, gbc);

        gbc.gridx = 1; // Position it as needed
       gbc.gridy = 7; // Position it below other buttons
       frame.add(logoutButton, gbc);

        

        openAccountButton.setEnabled(false);
        checkBalanceButton.setEnabled(false);
        transferMoneyButton.setEnabled(false);
        creditMoneyButton.setEnabled(false);
        debitMoneyButton.setEnabled(false);
        checkaccountButton.setEnabled(false);
        logoutButton.setEnabled(false);

        frame.setVisible(true);
    }

    public static void main(String[] args) {
        new BankingSystemGUI();
    }
}
