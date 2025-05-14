package com.example;

import java.io.*;

public class KeyPressReader {

    private Process pythonProcess;
    private final File workingDir;
    private final File scriptPath;
    private final File keyFile;

    public KeyPressReader() {
        workingDir = new File("C:/Users/thebe/Desktop/code/java/lol-api/src");
        scriptPath = new File(workingDir, "key_listener.py");
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
            processBuilder.redirectErrorStream(true);
            pythonProcess = processBuilder.start();

            // Log Python output
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

                // ❌ NE SUPPRIME PLUS ICI
                return key;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public void clearKeyFile() {
        try {
            new PrintWriter(keyFile).close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void stopListening() {
        if (pythonProcess != null) {
            pythonProcess.destroy();
        }
    }
}
