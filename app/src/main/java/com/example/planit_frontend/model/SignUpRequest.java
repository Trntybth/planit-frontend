package com.example.planit_frontend.model;

public class SignUpRequest {
    private boolean signup;

    // Constructor, getters and setters
    public SignUpRequest(boolean signup) {
        this.signup = signup;
    }

    public boolean isSignup() {
        return signup;
    }

    public void setSignup(boolean signup) {
        this.signup = signup;
    }
}
