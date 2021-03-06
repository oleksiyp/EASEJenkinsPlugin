package com.apperian.api.users;

/**
 * API described at:
 * https://help.apperian.com/display/pub/Users+API
 */
public class Users {
    public Users() {
    }

    public AuthenticateUserRequest authenticateUser(String userId, String password) {
        return new AuthenticateUserRequest(userId, password);
    }
}
