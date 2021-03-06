package com.apperian.api.signing;

import com.apperian.api.ApperianResponse;

import java.util.List;

public class ListAllSigningCredentialsResponse extends ApperianResponse {
    List<SigningCredential> credentials;

    public List<SigningCredential> getCredentials() {
        return credentials;
    }

    @Override
    public String toString() {
        return "ListAllSigningCredentialsResponse{" +
                "credentials=" + credentials +
                '}';
    }
}
