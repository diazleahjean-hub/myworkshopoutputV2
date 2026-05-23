package com.myworkshopoutput.service;

import com.myworkshopoutput.model.User;
import com.myworkshopoutput.util.Json;

import java.net.http.HttpResponse;

public class AuthService {

    public static User signIn(String email, String password) throws Exception {
        HttpResponse<String> res = SupabaseClient.signIn(email, password);

        if (res.statusCode() == 200) {
            return parseSession(res.body());
        }

        String msg = extractError(res.body());
        throw new Exception(msg);
    }

    public static User signUp(String email, String password) throws Exception {
        if (password.length() < 6) {
            throw new Exception("Password must be at least 6 characters.");
        }

        HttpResponse<String> res = SupabaseClient.signUp(email, password);

        if (res.statusCode() == 200 || res.statusCode() == 201) {
            Json.JsonObject json = Json.parseObject(res.body());

            if (json.isNull("access_token")) {
                throw new Exception("Check your email to confirm your account, then log in.");
            }
            return parseSession(res.body());
        }

        throw new Exception(extractError(res.body()));
    }

    public static void signOut(String accessToken) {
        try {
            SupabaseClient.signOut(accessToken);
        } catch (Exception ignored) {
        }
    }

    private static User parseSession(String body) {
        Json.JsonObject json     = Json.parseObject(body);
        Json.JsonObject userJson = json.getJSONObject("user");

        String id           = userJson.getString("id");
        String email        = userJson.getString("email");
        String accessToken  = json.getString("access_token");
        String refreshToken = json.optString("refresh_token", "");
        long   expiresIn    = json.optLong("expires_in", 3600);
        long   expiresAt    = System.currentTimeMillis() / 1000 + expiresIn;

        return new User(id, email, accessToken, refreshToken, expiresAt);
    }

    private static String extractError(String body) {
        try {
            Json.JsonObject json = Json.parseObject(body);
            if (json.has("error_description")) return json.getString("error_description");
            if (json.has("message"))           return json.getString("message");
            if (json.has("msg"))               return json.getString("msg");
        } catch (Exception ignored) {}
        return "An unexpected error occurred. Please try again.";
    }
}