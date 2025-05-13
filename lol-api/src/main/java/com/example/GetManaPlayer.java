package com.example;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Optional;
import java.time.Duration;

public class GetManaPlayer {
    private static final String URL = "https://127.0.0.1:2999/liveclientdata/activeplayer";

    public static Optional<String> getMana(HttpClient client) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(URL))
                    .timeout(Duration.ofSeconds(1))
                    .header("Accept", "application/json")
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            String json = response.body();

            // Extraire la valeur du mana
            String key = "\"resourceValue\":";
            int start = json.indexOf(key);
            if (start != -1) {
                start += key.length();
                int end = json.indexOf(',', start);
                if (end == -1) end = json.indexOf('}', start);
                String mana = json.substring(start, end).trim();
                return Optional.of(mana);
            }
        } catch (Exception e) {
            System.err.println("Erreur lors de la récupération du mana : " + e.getMessage());
        }
        return Optional.empty();
    }
}
