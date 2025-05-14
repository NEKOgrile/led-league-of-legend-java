package com.example;

import java.net.http.HttpClient;
import java.util.Optional;

public class SortDetector {
    public static void detect(HttpClient client) throws Exception {
        int mana1 = -1;
        int mana2;

        while (true) {
            Optional<String> manaOpt = GetManaPlayer.getMana(client);
            if (manaOpt.isPresent()) {
                try {
                    String manaStr = manaOpt.get().split("\\.")[0]; // Prend la partie entière
                    mana2 = Integer.parseInt(manaStr);

                    if (mana1 != -1 && mana2 < mana1) {
                        System.out.println("Un sort a probablement été utilisé ! Mana : " + mana2);
                    }

                    mana1 = mana2; // Mise à jour pour la prochaine itération
                } catch (NumberFormatException e) {
                    System.err.println("Erreur de parsing du mana : " + e.getMessage());
                }
            } else {
                System.err.println("Impossible de récupérer le mana.");
            }

            Thread.sleep(20); // Pause de 20 ms
        }
    }
}
