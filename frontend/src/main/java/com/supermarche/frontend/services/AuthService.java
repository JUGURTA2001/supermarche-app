package com.supermarche.frontend.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.supermarche.frontend.models.LoginResponse;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class AuthService {

    private static final String API_URL = "http://localhost:8080/api/auth/login";
    private static final HttpClient client = HttpClient.newHttpClient();
    private static final ObjectMapper mapper = new ObjectMapper();

    public static LoginResponse login(String email, String password) {
        try {
            // Création du JSON manuellement (simple)
            String jsonBody = String.format("{\"email\":\"%s\",\"password\":\"%s\"}", email, password);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(API_URL))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            // Si le code HTTP est 200 (OK)
            if (response.statusCode() == 200) {
                return mapper.readValue(response.body(), LoginResponse.class);
            } else {
                System.out.println("Erreur HTTP : " + response.statusCode());
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}