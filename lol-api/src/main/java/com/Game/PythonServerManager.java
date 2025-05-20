package com.Game;

import java.io.*;
import java.util.logging.*;

public class PythonServerManager {

    private static final Logger LOGGER = Logger.getLogger(PythonServerManager.class.getName());
    private static Process pythonProcess;
    private static boolean pythonStarted = false;

    /**
     * Démarre le serveur Python (key_socket_server.py) une seule fois.
     */
    public static void startPythonServerOnce() {
        if (!pythonStarted) {
            try {
                String baseDir = System.getProperty("user.dir");
                File srcDir = new File(baseDir, "lol-api/src");
                File scriptFile = new File(srcDir, "key_socket_server.py");

                if (!scriptFile.exists()) {
                    LOGGER.severe("Script Python introuvable à : " + scriptFile.getAbsolutePath());
                    return;
                }

                ProcessBuilder pb = new ProcessBuilder("python", scriptFile.getAbsolutePath());
                pb.directory(new File(baseDir));
                pb.redirectErrorStream(true);
                pythonProcess = pb.start();
                pythonStarted = true;
                LOGGER.info("Serveur Python démarré avec : " + scriptFile.getName());

                // Thread de log de la sortie Python
                new Thread(() -> {
                    try (BufferedReader in = new BufferedReader(
                             new InputStreamReader(pythonProcess.getInputStream()))) {
                        String line;
                        while ((line = in.readLine()) != null) {
                            LOGGER.fine("[Python] " + line);
                        }
                    } catch (IOException e) {
                        LOGGER.log(Level.WARNING, "Erreur lecture Python : " + e.getMessage(), e);
                    }
                }).start();

            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Impossible de démarrer le serveur Python", e);
            }
        }
    }

    /**
     * Arrête le serveur Python si il est en cours d'exécution.
     */
    public static void stopPythonServer() {
        if (pythonProcess != null && pythonProcess.isAlive()) {
            pythonProcess.destroy();
            LOGGER.info("Serveur Python arrêté.");
            pythonStarted = false;
        }
    }
}
