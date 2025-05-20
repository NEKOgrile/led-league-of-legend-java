package com.Game;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Optional;

/**
 * Classe pour récupérer le nom du champion actif depuis l'API Live Client Data.
 * Utilisation d'un parsing manuel du JSON pour extraire le premier "id" de abilities.
 */
public class GetNameChampion {
    private static final String URL = "https://127.0.0.1:2999/liveclientdata/activeplayer";

    /**
     * Envoie une requête HTTP et retourne le nom du champion actif si trouvé.
     * @param client HttpClient configuré
     * @return Optional contenant le nom du champion, ou empty sinon
     */
    public static Optional<String> getChampionName(HttpClient client) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(URL))
                    .timeout(Duration.ofSeconds(1))
                    .header("Accept", "application/json")
                    .build();

            
            

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            String json = response.body();


          // Extraire la valeur de l'identifiant du champion
            String key = "\"id\":";
            int start = json.indexOf(key);
            if (start != -1) {
                start += key.length();
                int end = json.indexOf(',', start);
                if (end == -1) end = json.indexOf('}', start);
                String rawId = json.substring(start, end).trim();

                // Supprimer les guillemets entourant la valeur
                if (rawId.startsWith("\"") && rawId.endsWith("\"")) {
                    rawId = rawId.substring(1, rawId.length() - 1);
                }

                // Supprimer le dernier caractère pour obtenir le nom du champion
                if (rawId.length() > 1) {
                    String championName = rawId.substring(0, rawId.length() - 1);
                    return Optional.of(championName);
                }
            }







            
        } catch (Exception e) {
            System.err.println("Erreur lors de la récupération du nom du champion : " + e.getMessage());
        }
        return Optional.empty();
    }

    public static void main(String[] args) {
        HttpClient client = HttpClientConfig.createHttpClient();
        Optional<String> champion = getChampionName(client);
        champion.ifPresentOrElse(
            name -> System.out.println("Champion actif: " + name),
            () -> System.err.println("Impossible de détecter le champion actif."));
    }
}