package com.myworkshopoutput.model;

public class User {

    private final String id;
    private final String email;
    private final String accessToken;
    private final String refreshToken;
    private final long   expiresAt;

    public User(String id, String email, String accessToken, String refreshToken, long expiresAt) {
        this.id           = id;
        this.email        = email;
        this.accessToken  = accessToken;
        this.refreshToken = refreshToken;
        this.expiresAt    = expiresAt;
    }

    public String getId()           { return id; }
    public String getEmail()        { return email; }
    public String getAccessToken()  { return accessToken; }
    public String getRefreshToken() { return refreshToken; }
    public long   getExpiresAt()    { return expiresAt; }

    public boolean isTokenValid() {
        return System.currentTimeMillis() / 1000 < expiresAt;
    }

    @Override
    public String toString() {
        return "User{id='" + id + "', email='" + email + "'}";
    }
}