package com.apperian.api.publishing;

import com.apperian.api.EASEResponse;

public class AuthenticateUserResponse extends EASEResponse {
    public Result result;

    public static class Result {
        public String token;
    }

    @Override
    public String getErrorMessage() {
        if (result != null && result.token == null) {
            return "No access";
        }
        return super.getErrorMessage();
    }

    @Override
    public String toString() {
        return "AuthenticateUserResponse{" +
                "token='" + result.token + '\'' +
                (hasError() ? ", error='" + getError() + '\'' : "") +
                '}';
    }
}
