package com.supermarche.backend.service;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.UUID;

@Service
public class FileStorageService {

    @Value("${app.upload.dir}")
    private String uploadDir;

    @Value("${app.upload.images-subdir}")
    private String imagesSubdir;

    private Path imagesPath;

    @PostConstruct
    public void init() throws IOException {
        this.imagesPath = Paths.get(uploadDir, imagesSubdir).toAbsolutePath().normalize();
        Files.createDirectories(imagesPath);
        System.out.println("📁 Dossier d'images prêt : " + imagesPath);
    }

    /**
     * Sauvegarde une image et retourne son chemin relatif (ex: "images/uuid.png")
     */
    public String saveImage(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) return null;

        // Vérifie le type MIME
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new IllegalArgumentException("Le fichier doit être une image.");
        }

        // Extension
        String original = file.getOriginalFilename();
        String ext = "";
        if (original != null && original.contains(".")) {
            ext = original.substring(original.lastIndexOf(".")).toLowerCase();
        }

        // Nom unique
        String filename = UUID.randomUUID().toString() + ext;
        Path target = imagesPath.resolve(filename);
        Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);

        // Chemin relatif stocké en BDD
        return imagesSubdir + "/" + filename;
    }

    /**
     * Supprime une image à partir de son chemin relatif ("images/xxx.png")
     */
    public void deleteImage(String relativePath) {
        if (relativePath == null || relativePath.isBlank()) return;
        try {
            Path target = Paths.get(uploadDir).resolve(relativePath).toAbsolutePath().normalize();
            Files.deleteIfExists(target);
        } catch (IOException e) {
            System.err.println("⚠️ Impossible de supprimer : " + relativePath);
        }
    }
}