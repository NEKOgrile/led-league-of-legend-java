package com.example;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import javax.net.ssl.*;
import java.security.SecureRandom;
import java.time.Duration;

public class ActivePlayerFetcher {
    public static void main(String[] args) throws Exception {
        // 1) Créer un SSLContext "trust all" pour accepter le certificat local de LoL
        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(null, new TrustManager[]{ new X509TrustManager() {
            public java.security.cert.X509Certificate[] getAcceptedIssuers() { return null; }
            public void checkClientTrusted(java.security.cert.X509Certificate[] certs, String authType) { }
            public void checkServerTrusted(java.security.cert.X509Certificate[] certs, String authType) { }
        } }, new SecureRandom());

        // 2) Construire l’HttpClient avec ce SSLContext
        HttpClient client = HttpClient.newBuilder()
                .sslContext(sslContext)
                .connectTimeout(Duration.ofSeconds(1))
                .build();

        // 3) Construire la requête GET
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI("https://127.0.0.1:2999/liveclientdata/activeplayer"))
                .timeout(Duration.ofSeconds(1))
                .header("Accept", "application/json")
                .GET()
                .build();

        // 4) Boucle infinie : interroger, parser le mana, afficher, puis dormir 1 s
        while (true) {
            try {
                HttpResponse<String> response =
                        client.send(request, HttpResponse.BodyHandlers.ofString());
                String json = response.body();

                // 5) Extraire la valeur de mana (resourceValue) via substring
                String key = "\"resourceValue\":";
                int idx = json.indexOf(key);
                if (idx != -1) {
                    int start = idx + key.length();
                    int end = json.indexOf(',', start);
                    if (end == -1) end = json.indexOf('}', start);
                    String manaStr = json.substring(start, end).trim();
                    System.out.println("Mana actuel : " + manaStr);
                } else {
                    System.out.println("Champ resourceValue non trouvé dans la réponse.");
                }
            } catch (Exception e) {
                System.err.println("Erreur lors de la requête : " + e.getMessage());
            }

            // 6) Pause de 1 seconde
            Thread.sleep(1000);
        }
    }
}
 