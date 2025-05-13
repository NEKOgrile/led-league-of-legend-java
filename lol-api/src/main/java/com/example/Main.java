package com.example;

import java.net.http.HttpClient;
import java.util.Optional;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        HttpClient client = HttpClientConfig.createHttpClient();
        
        if (client == null) {
            System.err.println("Erreur de création du HttpClient.");
            return;
        }

        while (true) {
            Optional<String> mana = GetManaPlayer.getMana(client);
            mana.ifPresent(m -> System.out.println("Mana actuel : " + m));
            Thread.sleep(1000); // Attendre 1 seconde
        }
    }
}
