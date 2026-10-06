package com.supermarche.frontend.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.File;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.time.Duration;

public class ApiClient {

    public static final String BASE_URL = "http://localhost:8080";

    public static final HttpClient HTTP = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    public static final ObjectMapper MAPPER = new ObjectMapper()
            .registerModule(new JavaTimeModule());

    // ============================================================
    // GET
    // ============================================================
    public static String get(String path) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + path))
                .header("Accept", "application/json")
                .GET()
                .build();
        HttpResponse<String> response = HTTP.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() >= 200 && response.statusCode() < 300) return response.body();
        throw new RuntimeException("Erreur HTTP " + response.statusCode() + " : " + response.body());
    }

    // ============================================================
    // POST (JSON simple)
    // ============================================================
    public static String post(String path, String jsonBody) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + path))
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();
        HttpResponse<String> response = HTTP.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() >= 200 && response.statusCode() < 300) return response.body();
        throw new RuntimeException("Erreur HTTP " + response.statusCode() + " : " + response.body());
    }

    // ============================================================
    // PUT (JSON simple)
    // ============================================================
    public static String put(String path, String jsonBody) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + path))
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();
        HttpResponse<String> response = HTTP.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() >= 200 && response.statusCode() < 300) return response.body();
        throw new RuntimeException("Erreur HTTP " + response.statusCode() + " : " + response.body());
    }

    // ============================================================
    // ⭐ DELETE — utilisée à la ligne 100 de ProduitService
    // ============================================================
    public static String delete(String path) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + path))
                .header("Accept", "application/json")
                .DELETE()
                .build();
        HttpResponse<String> response = HTTP.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() >= 200 && response.statusCode() < 300) return response.body();
        throw new RuntimeException("Erreur HTTP " + response.statusCode() + " : " + response.body());
    }

    // ============================================================
    // ⭐⭐ DELETE RAW — retourne la réponse brute (code + body)
    // Utilisée par BonCommandeService.delete() pour lire le message
    // d'erreur renvoyé par le backend (409 Conflict, etc.)
    // ============================================================
    public static HttpResponse<String> deleteRaw(String path) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + path))
                .header("Accept", "application/json")
                .DELETE()
                .build();
        return HTTP.send(request, HttpResponse.BodyHandlers.ofString());
    }

    // ============================================================
    // ⭐ POST MULTIPART — utilisée à la ligne 87 de ProduitService
    // ============================================================
    public static String postProduitMultipart(String produitJson, File photo) throws Exception {
        String boundary = "----Boundary" + System.currentTimeMillis();
        byte[] boundaryBytes = ("--" + boundary + "\r\n").getBytes();

        java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();

        // Partie "produit" (JSON)
        baos.write(boundaryBytes);
        baos.write("Content-Disposition: form-data; name=\"produit\"\r\n\r\n".getBytes());
        baos.write(produitJson.getBytes("UTF-8"));
        baos.write("\r\n".getBytes());

        // Partie "photo" (optionnelle)
        if (photo != null && photo.exists()) {
            baos.write(boundaryBytes);
            baos.write(("Content-Disposition: form-data; name=\"photo\"; filename=\""
                    + photo.getName() + "\"\r\n").getBytes());
            baos.write("Content-Type: image/png\r\n\r\n".getBytes());
            baos.write(Files.readAllBytes(photo.toPath()));
            baos.write("\r\n".getBytes());
        }

        baos.write(("--" + boundary + "--\r\n").getBytes());

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/api/produits"))
                .header("Content-Type", "multipart/form-data; boundary=" + boundary)
                .header("Accept", "application/json")
                .POST(HttpRequest.BodyPublishers.ofByteArray(baos.toByteArray()))
                .build();

        HttpResponse<String> response = HTTP.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() >= 200 && response.statusCode() < 300) return response.body();
        throw new RuntimeException("Erreur HTTP " + response.statusCode() + " : " + response.body());
    }

    // ============================================================
    // ⭐ PUT MULTIPART — utilisée à la ligne 101 de ProduitService
    // ============================================================
    public static String putProduitMultipart(Integer id, String produitJson, File photo) throws Exception {
        String boundary = "----Boundary" + System.currentTimeMillis();
        byte[] boundaryBytes = ("--" + boundary + "\r\n").getBytes();

        java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();

        baos.write(boundaryBytes);
        baos.write("Content-Disposition: form-data; name=\"produit\"\r\n\r\n".getBytes());
        baos.write(produitJson.getBytes("UTF-8"));
        baos.write("\r\n".getBytes());

        if (photo != null && photo.exists()) {
            baos.write(boundaryBytes);
            baos.write(("Content-Disposition: form-data; name=\"photo\"; filename=\""
                    + photo.getName() + "\"\r\n").getBytes());
            baos.write("Content-Type: image/png\r\n\r\n".getBytes());
            baos.write(Files.readAllBytes(photo.toPath()));
            baos.write("\r\n".getBytes());
        }

        baos.write(("--" + boundary + "--\r\n").getBytes());

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/api/produits/" + id))
                .header("Content-Type", "multipart/form-data; boundary=" + boundary)
                .header("Accept", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofByteArray(baos.toByteArray()))
                .build();

        HttpResponse<String> response = HTTP.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() >= 200 && response.statusCode() < 300) return response.body();
        throw new RuntimeException("Erreur HTTP " + response.statusCode() + " : " + response.body());
    }
}