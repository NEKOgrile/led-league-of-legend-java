package com.Game;

import java.net.http.HttpClient;
import java.util.Optional;
import java.util.concurrent.*;
import java.util.logging.*;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;

public class Main {

    private static final Logger LOGGER = Logger.getLogger(Main.class.getName());

    public static void main(String[] args) {

        // Étape 1 : Configuration du système de logs
        configureLogging();
        LOGGER.info("Démarrage de l'application");

        // Étape 2 : Création du client HTTP pour interroger l'API LoL Live Client Data
        HttpClient client = HttpClientConfig.createHttpClient();
        if (client == null) {
            LOGGER.severe("Erreur de création du HttpClient.");
            return;
        }
        testingGame(client);  
    }

private static void testingGame(HttpClient client) {
    ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    scheduler.scheduleAtFixedRate(() -> {
        if (GameStatusChecker.isGameRunning(client)) {
            LOGGER.info("Une partie est en cours !");
            runGameLogic(client , true);
        } else {
            LOGGER.info("Aucune partie en cours.");
            try {
                // Attendre 60 secondes si aucune partie n'est en cours
                Thread.sleep(60_000);
                runGameLogic(client , false);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                LOGGER.warning("Le thread a été interrompu pendant l'attente.");
            }
        }
    }, 0, 60, TimeUnit.SECONDS);
}


    /**
     * Configuration du système de logs : console et fichier
     */
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
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, "Impossible de créer le fichier de log : " + e.getMessage(), e);
        }

        LOGGER.setLevel(Level.FINE);
    }

    /**
     * Logique principale exécutée pendant une partie active
     */
    private static void runGameLogic(HttpClient client , boolean partie) {

        // Étape 4 : Démarrage du serveur Python pour l'écoute des touches
        PythonServerManager.startPythonServerOnce();

        // Étape 5 : Récupération et journalisation du champion actif
        Optional<String> activeChamp = GetNameChampion.getChampionName(client);
        activeChamp.ifPresentOrElse(
                champ -> LOGGER.info("Champion actif détecté : " + champ),
                () -> LOGGER.warning("Impossible de détecter le champion actif"));

        // Étape 6 : Préparation de la file pour recevoir les touches via socket
        BlockingQueue<String> keyQueue = new LinkedBlockingQueue<>();
        KeyPressReader keyReader = new KeyPressReader(keyQueue);
        LOGGER.fine("Démarrage du listener de touches (socket)");
        keyReader.startListening();

        List<TimedKey> allKeys = new ArrayList<>();
        int previousMana = -1;
        // Étape 7 : Boucle de détection continue (touches + mana)
        while (partie == true) {
            // 7.1 : Lecture non bloquante d'une touche reçue
            String keyPressed = keyQueue.poll();
            if (keyPressed != null) {
                LOGGER.finer("Touche reçue via socket : " + keyPressed);
                allKeys.add(new TimedKey(keyPressed));

            }

            // 7.2 : Lecture du mana via l'API
            int manaOpt = GetManaPlayer.getMana(client);
            if (manaOpt != -1) {
                try {
                    int currentMana = manaOpt;
                    LOGGER.finest("Mana actuel : " + currentMana);

                    // Détection de la perte de mana -> sort lancé
                    if (previousMana != -1 && currentMana < previousMana) {
                        long now = System.currentTimeMillis();
                        long deltaTime = 500;
                        TimedKey matched = null;


                        for (int i = allKeys.size() - 1; i >= 0; i--) {
                            if (now - allKeys.get(i).timestamp <= deltaTime) {
                                matched = allKeys.get(i);

                                break;
                            }
                        }
                        String sortKey = matched != null ? matched.key.toUpperCase() : "?";
                        int manaUsed = previousMana - currentMana;

                        LOGGER.info("Sort détecté : key=" + sortKey + " manaUsed=" + manaUsed);
                        String champ = activeChamp.orElse("Inconnu");
                        System.out.println(champ + " sort " + sortKey + " lancé " + manaUsed + " mana");

                    }
                    previousMana = currentMana;
                } catch (NumberFormatException e) {
                    LOGGER.log(Level.WARNING, "Erreur parsing mana", e);
                }
            } else {
                LOGGER.warning("Impossible de récupérer le mana.");
                partie = false;
                
            }

            // 7.3 : Pause courte pour limiter l'utilisation CPU
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                LOGGER.warning("Thread interrompu pendant sleep, arrêt de la boucle.");
                break;
            }
        }
        // Étape 8 : Nettoyage à la sortie de boucle
        keyReader.stopListening();
        PythonServerManager.stopPythonServer();


        //teste de l etat de la game : 
        testingGame(client);
    }

    /**
     * Classe interne stockant une touche avec son timestamp
     */
    static class TimedKey {
        String key;
        long timestamp;

        TimedKey(String key) {
            this.key = key;
            this.timestamp = System.currentTimeMillis();
        }
    }
}
