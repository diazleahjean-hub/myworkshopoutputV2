package com.myworkshopoutput.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class EnvLoader {

    private static final Map<String, String> env = new HashMap<>();
    private static boolean loaded = false;

    private static void load() {
        if (loaded) return;
        loaded = true;

        File envFile = new File(".env");
        if (!envFile.exists()) {
            envFile = new File("../.env");
        }

        if (envFile.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(envFile))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    line = line.trim();
                    if (line.isEmpty() || line.startsWith("#")) continue;
                    int eq = line.indexOf('=');
                    if (eq > 0) {
                        String key   = line.substring(0, eq).trim();
                        String value = line.substring(eq + 1).trim();
                        env.put(key, value);
                    }
                }
            } catch (IOException e) {
                System.err.println("[EnvLoader] Could not read .env file: " + e.getMessage());
            }
        } else {
            System.out.println("[EnvLoader] No .env file found — falling back to system environment variables.");
        }
    }

    public static String get(String key) {
        load();
        String val = env.getOrDefault(key, System.getenv(key));
        if (val == null || val.isBlank()) {
            throw new RuntimeException(
                    "Missing required config key: " + key +
                            "\nMake sure your .env file exists and contains this key."
            );
        }
        return val;
    }

    public static String getOptional(String key) {
        load();
        String val = env.getOrDefault(key, System.getenv(key));
        return (val == null || val.isBlank()) ? null : val;
    }
}