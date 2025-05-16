package com.example;

import java.net.http.HttpClient;
import java.util.*;
import java.util.concurrent.*;
import java.util.logging.*;

public class Main {

    private static final Logger LOGGER = Logger.getLogger(Main.class.getName());

    static class TimedKey {
        String key;
        long timestamp;

        TimedKey(String key) {
            this.key = key;
            this.timestamp = System.currentTimeMillis();
        }
    }

    public static void main(String[] args) {
        // Configuration du logging
        configureLogging();

        LOGGER.info("Démarrage de l'application");

        HttpClient client = HttpClientConfig.createHttpClient();
        if (client == null) {
            LOGGER.severe("Erreur de création du HttpClient.");
            return;
        }

        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

        scheduler.scheduleAtFixedRate(() -> {
            if (GameStatusChecker.isGameRunning(client)) {
                LOGGER.info("Une partie est en cours !");
                // Exécutez ici votre logique principale
                runGameLogic(client);
            } else {
                LOGGER.info("Aucune partie en cours.");
            }
        }, 0, 60, TimeUnit.SECONDS);
    }

    private static void configureLogging() {
        LogManager.getLogManager().reset();
        ConsoleHandler consoleHandler = new ConsoleHandler();
        consoleHandler.setLevel(Level.FINE);
        LOGGER.addHandler(consoleHandler);

        try {
            FileHandler fileHandler = new FileHandler("led-lol.log", true);
            fileHandler.setLevel(Level.FINE);
            fileHandler.setFormatter(new SimpleFormatter());
            LOGGER.addHandler(fileHandler);
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Impossible de créer le fichier de log : " + e.getMessage(), e);
        }

        LOGGER.setLevel(Level.FINE);
    }

    private static void runGameLogic(HttpClient client) {
        Optional<String> activeChamp = GetNameChampion.getChampionName(client);
        activeChamp.ifPresentOrElse(
            champ -> LOGGER.info("Champion actif détecté : " + champ),
            ()     -> LOGGER.warning("Impossible de détecter le champion actif")
        );

        KeyPressReader keyReader = new KeyPressReader();
        LOGGER.fine("Démarrage du listener de touches");
        keyReader.startListening();

        List<TimedKey> allKeys = new ArrayList<>();
        int previousMana = -1;

        while (true) {
            // 1. Lire et stocker chaque touche avec le timestamp
            String keyPressed = keyReader.getLastKeyPressed();
            if (keyPressed != null) {
                LOGGER.finer("Touche lue : " + keyPressed);
                allKeys.add(new TimedKey(keyPressed));
                keyReader.clearKeyFile();
            }

            // 2. Lire le mana
            Optional<String> manaOpt = GetManaPlayer.getMana(client);
            if (manaOpt.isPresent()) {
                try {
                    int currentMana = Integer.parseInt(manaOpt.get().split("\\.")[0]);
                    LOGGER.finest("Mana actuel : " + currentMana);

                    if (previousMana != -1 && currentMana < previousMana) {
                        long now = System.currentTimeMillis();
                        long deltaTime = 500; // fenêtre pour les frappes récentes

                        // Recherche de la touche associée
                        TimedKey matchedKey = null;
                        for (int i = allKeys.size() - 1; i >= 0; i--) {
                            TimedKey tk = allKeys.get(i);
                            if (now - tk.timestamp <= deltaTime) {
                                matchedKey = tk;
                                break;
                            }
                        }
                        String sortKey = matchedKey != null ? matchedKey.key.toUpperCase() : "?";
                        int manaUsed = previousMana - currentMana;

                        LOGGER.info("Sort détecté : key=" + sortKey + "  manaUsed=" + manaUsed);
                        if (activeChamp.isPresent()) {
                            String champion = activeChamp.get();
                            System.out.println(champion + " sort " + sortKey + " lancé " + manaUsed + " mana");
                        } else {
                            System.out.println("Champion inconnu sort " + sortKey + " lancé " + manaUsed + " mana");
                        }
                    }
                    previousMana = currentMana;
                } catch (NumberFormatException e) {
                    LOGGER.log(Level.WARNING, "Erreur de parsing du mana : " + e.getMessage(), e);
                }
            } else {
                LOGGER.warning("Impossible de récupérer le mana.");
            }

            try {
                Thread.sleep(10); // intervalle ajusté à 10 ms
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt(); // Réinitialise le statut d'interruption
                LOGGER.warning("Le thread a été interrompu pendant le sommeil.");
                break; // Sort de la boucle si nécessaire
            }
        }
    }
}
