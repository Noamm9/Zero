package com.ricedotwho.mcprotocol.protocol.util;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.experimental.UtilityClass;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.UUID;

@UtilityClass
public class Util {
    public String getUsername(UUID uuid) {
        return getUsername(uuid.toString());
    }

    public String getUsername(String uuid) {
        try {
            String url = "https://sessionserver.mojang.com/session/minecraft/profile/" + uuid.toString();
            HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
            conn.setRequestMethod("GET");
            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            JsonParser parser = new JsonParser();
            JsonObject json = parser.parse(reader).getAsJsonObject();
            return json.get("name").getAsString();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public UUID getUUID(String username) {
        try {
            String url = "https://api.mojang.com/users/profiles/minecraft/" + username;
            HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
            conn.setRequestMethod("GET");

            if (conn.getResponseCode() != 200) {
                return null;
            }

            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            JsonObject json = JsonParser.parseReader(reader).getAsJsonObject();

            String rawUUID = json.get("id").getAsString();
            return UUID.fromString(dashUUID(rawUUID));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String dashUUID(String raw) {
        return raw.replaceFirst(
                "(\\p{XDigit}{8})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}{12})",
                "$1-$2-$3-$4-$5"
        );
    }
}
