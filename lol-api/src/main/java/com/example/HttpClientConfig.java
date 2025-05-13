package com.example;

import javax.net.ssl.*;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.security.SecureRandom;
import java.net.http.HttpClient;
import java.time.Duration;
import java.security.KeyManagementException;

public class HttpClientConfig {
    public static HttpClient createHttpClient() {
        try {
            // Créer un SSLContext qui accepte tous les certificats
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, new TrustManager[]{
                new X509TrustManager() {
                    public X509Certificate[] getAcceptedIssuers() {
                        return null;
                    }
                    public void checkClientTrusted(X509Certificate[] certs, String authType) {}
                    public void checkServerTrusted(X509Certificate[] certs, String authType) {}
                }
            }, new SecureRandom());

            // Retourner un HttpClient configuré avec ce SSLContext
            return HttpClient.newBuilder()
                    .sslContext(sslContext)
                    .connectTimeout(Duration.ofSeconds(1))
                    .build();
        } catch (NoSuchAlgorithmException | KeyManagementException e) {
            // Gestion des exceptions possibles
            System.err.println("Erreur lors de la configuration du SSLContext : " + e.getMessage());
            return null;
        }
    }
}
