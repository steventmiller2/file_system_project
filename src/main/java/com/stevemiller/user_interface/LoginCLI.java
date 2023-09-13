package com.stevemiller.user_interface;

import java.util.Scanner;
import com.stevemiller.authentication.User;
import com.stevemiller.data_access.UserDAO;

public class LoginCLI {
    private Scanner scanner;

    public LoginCLI() {
        this.scanner = new Scanner(System.in);
    }

    public User loginPrompt(UserDAO userDAO) {
        while (true) {
            System.out.println("Please log in:");
            System.out.print("Username: ");
            String username = scanner.nextLine().trim();
            System.out.print("Password: "); // You might want to mask the password for security reasons
            String password = scanner.nextLine().trim();

            User user = userDAO.authenticateUser(username, password);
            if (user != null) {
                System.out.println("Login successful!");
                return user;
            } else {
                System.out.println("Invalid username or password. Please try again.");
            }
        }
    }
}
