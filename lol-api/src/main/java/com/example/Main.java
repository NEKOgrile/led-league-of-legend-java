package com.example;

import java.net.http.HttpClient;
import javax.net.ssl.*;
import java.security.SecureRandom;
import java.time.Duration;

public class Main {
    public static void main(String[] args) throws Exception {
        // Construit le SSLContext
        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(null, new TrustManager[]{ new X509TrustManager() {
            public java.security.cert.X509Certificate[] getAcceptedIssuers() { return null; }
            public void checkClientTrusted(java.security.cert.X509Certificate[] certs, String authType) {}
            public void checkServerTrusted(java.security.cert.X509Certificate[] certs, String authType) {}
        } }, new SecureRandom());

        HttpClient client = HttpClient.newBuilder()
                .sslContext(sslContext)
                .connectTimeout(Duration.ofSeconds(1))
                .build();

        // Détecteur de touches
        KeyPressReader keyReader = new KeyPressReader();
        keyReader.startListening();

        while (true) {
            String key = keyReader.getLastKeyPressed();
            if (key != null) {
                System.out.println("Touche pressée détectée : " + key);
                // Ici tu peux appeler SortDetector ou d'autres actions en fonction de la touche
            }
            Thread.sleep(100);  // rafraîchissement
        }

        // keyReader.stopListening(); ← À utiliser si tu veux arrêter le script Python
    }
}
