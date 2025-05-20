package com.Game;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

public class GameStatusChecker {

    private static final String LIVE_CLIENT_URL = "https://127.0.0.1:2999/liveclientdata/activeplayer";

    public static boolean isGameRunning(HttpClient client) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(LIVE_CLIENT_URL))
                    .timeout(Duration.ofSeconds(1))
                    .header("Accept", "application/json")
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            // Si la réponse est 200 OK, une partie est en cours
            return response.statusCode() == 200;
        } catch (Exception e) {
            // En cas d'exception (par exemple, connexion refusée), aucune partie n'est en cours
            return false;
        }
    }
}
