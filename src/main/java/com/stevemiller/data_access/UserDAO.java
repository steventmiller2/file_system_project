package com.stevemiller.data_access;

import com.stevemiller.authentication.User;

public interface UserDAO {
  User authenticateUser(String username, String password);
}
