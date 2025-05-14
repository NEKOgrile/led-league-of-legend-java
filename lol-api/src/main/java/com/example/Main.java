package com.example;

import java.net.http.HttpClient;
import java.util.*;

public class Main {

    static class TimedKey {
        String key;
        long timestamp;

        TimedKey(String key) {
            this.key = key;
            this.timestamp = System.currentTimeMillis();
        }
    }

    public static void main(String[] args) throws Exception {
        HttpClient client = HttpClientConfig.createHttpClient();

        if (client == null) {
            System.err.println("Erreur de création du HttpClient.");
            return;
        }

        KeyPressReader keyReader = new KeyPressReader();
        keyReader.startListening();

        List<TimedKey> allKeys = new ArrayList<>();
        int previousMana = -1;

        while (true) {
            // 1. Lire et stocker chaque touche avec le timestamp
            String keyPressed = keyReader.getLastKeyPressed();
            if (keyPressed != null) {
                allKeys.add(new TimedKey(keyPressed));

                // ✅ Ici on peut vider le fichier : la touche est stockée dans allKeys
                keyReader.clearKeyFile();
            }

            // 2. Lire le mana
            Optional<String> manaOpt = GetManaPlayer.getMana(client);
            if (manaOpt.isPresent()) {
                try {
                    int currentMana = Integer.parseInt(manaOpt.get().split("\\.")[0]);

                    if (previousMana != -1 && currentMana < previousMana) {
                        long now = System.currentTimeMillis();
                        long deltaTime = 500; // on cherche une touche pressée dans les 500 ms avant

                        // Cherche la touche la plus proche juste avant la perte de mana
                        TimedKey matchedKey = null;
                        for (int i = allKeys.size() - 1; i >= 0; i--) {
                            TimedKey tk = allKeys.get(i);
                            if (now - tk.timestamp <= deltaTime) {
                                matchedKey = tk;
                                break;
                            }
                        }

                        String sortKey = (matchedKey != null) ? matchedKey.key.toUpperCase() : "?";
                        int manaUsed = previousMana - currentMana;

                        System.out.println("sort " + sortKey + " lancé " + manaUsed + " mana");

                        // Ne vide plus la liste ici
                    }

                    previousMana = currentMana;
                } catch (NumberFormatException e) {
                    System.err.println("Erreur de parsing du mana : " + e.getMessage());
                }
            } else {
                System.err.println("Impossible de récupérer le mana.");
            }

            Thread.sleep(50); // ajustable
        }
    }
}
