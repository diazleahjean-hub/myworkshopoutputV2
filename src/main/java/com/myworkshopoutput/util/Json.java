package com.myworkshopoutput.util;

import java.util.*;

public class Json {

    public static JsonObject parseObject(String json) {
        return (JsonObject) new Parser(json.trim()).parseValue();
    }

    public static JsonArray parseArray(String json) {
        return (JsonArray) new Parser(json.trim()).parseValue();
    }

    public static class JsonObject {
        private final Map<String, Object> map = new LinkedHashMap<>();

        void put(String k, Object v) { map.put(k, v); }

        public String getString(String key) {
            Object v = map.get(key);
            if (v == null) throw new RuntimeException("Missing key: " + key);
            return v.toString();
        }

        public String optString(String key, String def) {
            Object v = map.get(key);
            return (v == null || v == NULL_SENTINEL) ? def : v.toString();
        }

        public long optLong(String key, long def) {
            Object v = map.get(key);
            if (v == null || v == NULL_SENTINEL) return def;
            return ((Number) v).longValue();
        }

        public boolean isNull(String key) {
            return !map.containsKey(key) || map.get(key) == NULL_SENTINEL;
        }

        public boolean has(String key) { return map.containsKey(key); }

        public JsonObject getJSONObject(String key) {
            return (JsonObject) map.get(key);
        }

        public JsonArray getJSONArray(String key) {
            return (JsonArray) map.get(key);
        }

        @Override public String toString() { return map.toString(); }
    }

    public static class JsonArray implements Iterable<Object> {
        private final List<Object> list = new ArrayList<>();

        void add(Object v) { list.add(v); }

        public int length() { return list.size(); }

        public JsonObject getJSONObject(int i) { return (JsonObject) list.get(i); }

        public String getString(int i) { return list.get(i).toString(); }

        @Override public Iterator<Object> iterator() { return list.iterator(); }

        @Override public String toString() { return list.toString(); }
    }

    private static final Object NULL_SENTINEL = new Object() {
        @Override public String toString() { return "null"; }
    };

    private static class Parser {
        private final String s;
        private int pos;

        Parser(String s) { this.s = s; }

        Object parseValue() {
            skipWs();
            if (pos >= s.length()) return null;
            char c = s.charAt(pos);
            if (c == '{') return parseObject();
            if (c == '[') return parseArray();
            if (c == '"') return parseString();
            if (c == 't') { pos += 4; return Boolean.TRUE; }
            if (c == 'f') { pos += 5; return Boolean.FALSE; }
            if (c == 'n') { pos += 4; return NULL_SENTINEL; }
            return parseNumber();
        }

        JsonObject parseObject() {
            JsonObject obj = new JsonObject();
            pos++;
            skipWs();
            if (peek() == '}') { pos++; return obj; }
            while (true) {
                skipWs();
                String key = parseString();
                skipWs();
                pos++;
                skipWs();
                Object val = parseValue();
                obj.put(key, val);
                skipWs();
                if (peek() == '}') { pos++; break; }
                pos++;
            }
            return obj;
        }

        JsonArray parseArray() {
            JsonArray arr = new JsonArray();
            pos++;
            skipWs();
            if (peek() == ']') { pos++; return arr; }
            while (true) {
                skipWs();
                arr.add(parseValue());
                skipWs();
                if (peek() == ']') { pos++; break; }
                pos++;
            }
            return arr;
        }

        String parseString() {
            pos++;
            StringBuilder sb = new StringBuilder();
            while (pos < s.length()) {
                char c = s.charAt(pos++);
                if (c == '"') break;
                if (c == '\\') {
                    char esc = s.charAt(pos++);
                    switch (esc) {
                        case '"'  -> sb.append('"');
                        case '\\' -> sb.append('\\');
                        case '/'  -> sb.append('/');
                        case 'n'  -> sb.append('\n');
                        case 'r'  -> sb.append('\r');
                        case 't'  -> sb.append('\t');
                        case 'u'  -> {
                            String hex = s.substring(pos, pos + 4);
                            sb.append((char) Integer.parseInt(hex, 16));
                            pos += 4;
                        }
                        default   -> sb.append(esc);
                    }
                } else {
                    sb.append(c);
                }
            }
            return sb.toString();
        }

        Number parseNumber() {
            int start = pos;
            boolean isFloat = false;
            if (peek() == '-') pos++;
            while (pos < s.length() && (Character.isDigit(s.charAt(pos))
                    || s.charAt(pos) == '.' || s.charAt(pos) == 'e'
                    || s.charAt(pos) == 'E' || s.charAt(pos) == '+'
                    || s.charAt(pos) == '-')) {
                if (s.charAt(pos) == '.' || s.charAt(pos) == 'e' || s.charAt(pos) == 'E')
                    isFloat = true;
                pos++;
            }
            String numStr = s.substring(start, pos);
            if (isFloat) return Double.parseDouble(numStr);
            try { return Long.parseLong(numStr); }
            catch (NumberFormatException e) { return Double.parseDouble(numStr); }
        }

        void skipWs() {
            while (pos < s.length() && Character.isWhitespace(s.charAt(pos))) pos++;
        }

        char peek() { return pos < s.length() ? s.charAt(pos) : 0; }
    }
}
