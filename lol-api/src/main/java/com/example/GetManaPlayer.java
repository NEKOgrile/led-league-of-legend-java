package com.example;

import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Optional;
import java.time.Duration;
import java.io.StringReader;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;

public class GetManaPlayer {
    private static final String URL = "https://127.0.0.1:2999/liveclientdata/activeplayer";

    public static Integer getMana(HttpClient client) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(URL))
                    .timeout(Duration.ofSeconds(1))
                    .header("Accept", "application/json")
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            String json = response.body();
            
            //create JsonReader object
            JsonReader jsonReader = Json.createReader(new StringReader(json));
            
            //get JsonObject from JsonReader
            JsonObject jsonObject = jsonReader.readObject();
            
            //we can close IO resource and JsonReader now
            jsonReader.close();
            
            //reading inner object from json object
            JsonObject innerJsonObject = jsonObject.getJsonObject("championStats");
            Integer mana = innerJsonObject.getInt("resourceValue");
            return mana;

        } catch (Exception e) {
            System.err.println("Erreur lors de la récupération du mana : " + e.getMessage());
        }
        return -1;
    }
}
