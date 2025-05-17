package com.example;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;

/**
 * Client TCP qui lit les touches envoyées par le serveur Python
 * et les place dans une BlockingQueue partagée.
 */
public class KeyPressReader {
    private Socket socket;
    private BufferedReader reader;
    private final BlockingQueue<String> queue;

    /**
     * @param queue File thread-safe pour stocker les touches reçues.
     */
    public KeyPressReader(BlockingQueue<String> queue) {
        this.queue = queue;  // on conserve la référence à la queue partagée
    }

    /**
     * Démarre la connexion au serveur Python et lance un thread de lecture.
     */
    public void startListening() {
        try {
            // 1. Création du socket client sur localhost:4000
            socket = new Socket("127.0.0.1", 4000);

            // 2. On récupère le flux d'entrée pour lire les caractères envoyés
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            // 3. Thread dédié pour ne pas bloquer le Main Thread
            new Thread(() -> {
                String line;
                try {
                    // 4. Lire en boucle chaque ligne (une touche) puis la stocker dans la queue
                    while ((line = reader.readLine()) != null) {
                        queue.offer(line);  // non bloquant, ajoute à la queue
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Ferme proprement le socket et le reader.
     */
    public void stopListening() {
        try {
            if (socket != null) socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
