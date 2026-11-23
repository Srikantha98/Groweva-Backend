package org.evabirds.evabirds.controller;

import org.evabirds.evabirds.service.CloudinaryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/upload")
public class UploadController {

    private static final Logger log = LoggerFactory.getLogger(UploadController.class);
    private final CloudinaryService cloudinaryService;
    private final Path uploadDir = Paths.get("uploads").toAbsolutePath().normalize();

    public UploadController(CloudinaryService cloudinaryService) {
        this.cloudinaryService = cloudinaryService;
        try {
            Files.createDirectories(uploadDir);
        } catch (IOException e) {
            log.error("Could not initialize local upload directory", e);
        }
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "folder", required = false, defaultValue = "evabirds/uploads") String folder
    ) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "File is empty"));
        }

        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            return ResponseEntity.badRequest().body(Map.of("error", "Only image files (PNG, JPEG, WebP) are allowed"));
        }

        // 1. Cloudinary upload path (Primary)
        if (cloudinaryService.isConfigured()) {
            try {
                Map<String, Object> result = cloudinaryService.uploadFile(file, folder);
                Map<String, Object> response = new HashMap<>();
                response.put("url", result.get("secure_url"));
                response.put("publicId", result.get("public_id"));
                response.put("format", result.get("format"));
                response.put("size", result.get("bytes"));
                response.put("provider", "cloudinary");
                return ResponseEntity.status(HttpStatus.CREATED).body(response);
            } catch (Exception e) {
                log.error("Cloudinary upload failed, falling back to local storage: {}", e.getMessage());
            }
        }

        // 2. Local disk fallback
        String originalFilename = file.getOriginalFilename();
        String extension = ".jpg";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf(".")).toLowerCase();
        }

        String generatedFilename = "img_" + System.currentTimeMillis() + "_" + UUID.randomUUID().toString().substring(0, 8) + extension;
        Path targetLocation = uploadDir.resolve(generatedFilename);

        try (InputStream inputStream = file.getInputStream()) {
            Files.copy(inputStream, targetLocation, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to store file: " + e.getMessage()));
        }

        String fileUrl = "http://localhost:8081/uploads/" + generatedFilename;
        Map<String, Object> response = new HashMap<>();
        response.put("url", fileUrl);
        response.put("filename", generatedFilename);
        response.put("size", file.getSize());
        response.put("contentType", contentType);
        response.put("provider", "local");

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/base64")
    public ResponseEntity<?> uploadBase64(@RequestBody Map<String, String> request) {
        String base64Image = request.get("image");
        String folder = request.getOrDefault("folder", "evabirds/uploads");

        if (base64Image == null || base64Image.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("error", "image parameter is required"));
        }

        if (!cloudinaryService.isConfigured()) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body(Map.of("error", "Cloudinary credentials not configured on backend."));
        }

        try {
            Map<String, Object> result = cloudinaryService.uploadBase64(base64Image, folder);
            Map<String, Object> response = new HashMap<>();
            response.put("url", result.get("secure_url"));
            response.put("publicId", result.get("public_id"));
            response.put("format", result.get("format"));
            response.put("size", result.get("bytes"));
            response.put("provider", "cloudinary");
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            log.error("Failed to upload base64 image to Cloudinary: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Cloudinary upload failed: " + e.getMessage()));
        }
    }

    @DeleteMapping
    public ResponseEntity<?> deleteFile(@RequestParam("publicId") String publicId) {
        if (!cloudinaryService.isConfigured()) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body(Map.of("error", "Cloudinary credentials not configured."));
        }

        try {
            Map<String, Object> result = cloudinaryService.deleteFile(publicId);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("Failed to delete Cloudinary asset {}: {}", publicId, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to delete asset: " + e.getMessage()));
        }
    }
}
