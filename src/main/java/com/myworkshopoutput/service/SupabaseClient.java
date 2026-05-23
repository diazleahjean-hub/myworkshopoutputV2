package com.myworkshopoutput.service;

import com.myworkshopoutput.util.EnvLoader;

import java.net.URI;
import java.net.http.*;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse.BodyHandlers;

public class SupabaseClient {

    private static final String URL     = EnvLoader.get("SUPABASE_URL");
    private static final String API_KEY = EnvLoader.get("SUPABASE_ANON_KEY");

    private static final HttpClient HTTP = HttpClient.newHttpClient();

    public static HttpResponse<String> signIn(String email, String password) throws Exception {
        String body = String.format(
                "{\"email\":\"%s\",\"password\":\"%s\"}",
                escape(email), escape(password)
        );
        return post("/auth/v1/token?grant_type=password", body, null);
    }

    public static HttpResponse<String> signUp(String email, String password) throws Exception {
        String body = String.format(
                "{\"email\":\"%s\",\"password\":\"%s\"}",
                escape(email), escape(password)
        );
        return post("/auth/v1/signup", body, null);
    }

    public static HttpResponse<String> signOut(String accessToken) throws Exception {
        HttpRequest request = baseBuilder("/auth/v1/logout", accessToken)
                .POST(BodyPublishers.noBody())
                .build();
        return HTTP.send(request, BodyHandlers.ofString());
    }

    public static HttpResponse<String> getNotes(String accessToken) throws Exception {
        HttpRequest request = baseBuilder("/rest/v1/notes?select=*&order=created_at.desc", accessToken)
                .GET()
                .build();
        return HTTP.send(request, BodyHandlers.ofString());
    }

    public static HttpResponse<String> insertNote(String title, String content, String accessToken) throws Exception {
        String body = String.format(
                "{\"title\":\"%s\",\"content\":\"%s\"}",
                escape(title), escape(content)
        );
        return post("/rest/v1/notes", body, accessToken);
    }

    public static HttpResponse<String> deleteNote(String noteId, String accessToken) throws Exception {
        HttpRequest request = baseBuilder("/rest/v1/notes?id=eq." + noteId, accessToken)
                .method("DELETE", BodyPublishers.noBody())
                .build();
        return HTTP.send(request, BodyHandlers.ofString());
    }

    private static HttpResponse<String> post(String path, String jsonBody, String accessToken) throws Exception {
        HttpRequest request = baseBuilder(path, accessToken)
                .header("Content-Type", "application/json")
                .POST(BodyPublishers.ofString(jsonBody))
                .build();
        return HTTP.send(request, BodyHandlers.ofString());
    }

    private static HttpRequest.Builder baseBuilder(String path, String accessToken) {
        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(URI.create(URL + path))
                .header("apikey",  API_KEY)
                .header("Prefer",  "return=representation");

        if (accessToken != null) {
            builder.header("Authorization", "Bearer " + accessToken);
        }
        return builder;
    }

    private static String escape(String s) {
        return s.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r");
    }
}