package com.example;

import java.io.*;

public class KeyPressReader {

    private Process pythonProcess;
    private final File workingDir;
    private final File scriptPath;
    private final File keyFile;

    public KeyPressReader() {
        // Chemin absolu du dossier où se trouvent le script Python et le fichier texte
        workingDir = new File("C:/Users/thebe/Desktop/code/java/lol-api/src");

        // Script Python
        scriptPath = new File(workingDir, "key_listener.py");

        // Fichier généré par le script
        keyFile = new File(workingDir, "key_pressed.txt");
    }

    public void startListening() {
        try {
            if (!scriptPath.exists()) {
                System.err.println("Script Python introuvable à : " + scriptPath.getAbsolutePath());
                return;
            }

            ProcessBuilder processBuilder = new ProcessBuilder("python", scriptPath.getAbsolutePath());
            processBuilder.directory(workingDir);
            processBuilder.redirectErrorStream(true); // utile pour debug
            pythonProcess = processBuilder.start();

            // (optionnel) lire les logs du script Python
            new Thread(() -> {
                try (BufferedReader reader = new BufferedReader(
                        new InputStreamReader(pythonProcess.getInputStream()))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        System.out.println("[Python] " + line);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getLastKeyPressed() {
        if (keyFile.exists() && keyFile.length() > 0) {
            try {
                BufferedReader reader = new BufferedReader(new FileReader(keyFile));
                String key = reader.readLine();
                reader.close();

                // Vider le fichier pour ne pas relire la même touche
                new PrintWriter(keyFile).close();
                return key;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public void stopListening() {
        if (pythonProcess != null) {
            pythonProcess.destroy();
        }
    }
}
