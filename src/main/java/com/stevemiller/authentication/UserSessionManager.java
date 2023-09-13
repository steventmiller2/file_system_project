package com.stevemiller.authentication;

import com.stevemiller.data_access.UserDAO;
import com.stevemiller.user_interface.LoginCLI;

public class UserSessionManager {
    private User loggedInUser;
    private UserDAO userDAO;

    public UserSessionManager(UserDAO userDAO) {
        this.loggedInUser = null;
        this.userDAO = userDAO;
    }

    public User getLoggedInUser() {
        return loggedInUser;
    }

    public void authenticateUser() {
        LoginCLI loginCLI = new LoginCLI();
        //Set the loggedInUser to the user returned by the loginPrompt method
        loggedInUser = loginCLI.loginPrompt(userDAO);
    }
}
