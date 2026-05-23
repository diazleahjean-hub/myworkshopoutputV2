package com.myworkshopoutput.service;

import com.myworkshopoutput.model.Note;
import com.myworkshopoutput.util.Json;

import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

/**
 * CRUD operations for the notes table.
 * Requires a valid access token (pass it from the current User session).
 */
public class NoteService {

    public static List<Note> getNotes(String accessToken) throws Exception {
        HttpResponse<String> res = SupabaseClient.getNotes(accessToken);

        if (res.statusCode() == 200) {
            List<Note> notes = new ArrayList<>();
            Json.JsonArray arr = Json.parseArray(res.body());
            for (int i = 0; i < arr.length(); i++) {
                Json.JsonObject obj = arr.getJSONObject(i);
                notes.add(new Note(
                        obj.getString("id"),
                        obj.optString("title", "(no title)"),
                        obj.optString("content", ""),
                        obj.optString("created_at", "")
                ));
            }
            return notes;
        }

        throw new Exception("Failed to load notes: " + res.body());
    }

    public static Note insertNote(String title, String content, String accessToken) throws Exception {
        if (title.isBlank()) throw new Exception("Title cannot be empty.");

        HttpResponse<String> res = SupabaseClient.insertNote(title, content, accessToken);

        if (res.statusCode() == 200 || res.statusCode() == 201) {
            // Supabase returns an array even for single inserts with Prefer: return=representation
            Json.JsonArray arr = Json.parseArray(res.body());
            Json.JsonObject obj = arr.getJSONObject(0);
            return new Note(
                    obj.getString("id"),
                    obj.optString("title", title),
                    obj.optString("content", content),
                    obj.optString("created_at", "")
            );
        }

        throw new Exception("Failed to save note: " + res.body());
    }

    public static void deleteNote(String noteId, String accessToken) throws Exception {
        HttpResponse<String> res = SupabaseClient.deleteNote(noteId, accessToken);
        if (res.statusCode() != 200 && res.statusCode() != 204) {
            throw new Exception("Failed to delete note: " + res.body());
        }
    }
}