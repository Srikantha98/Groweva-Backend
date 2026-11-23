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
import java.util.Map;

@RestController
@RequestMapping("/api/upload")
public class UploadController {

    private static final Logger log = LoggerFactory.getLogger(UploadController.class);
    private final CloudinaryService cloudinaryService;

    public UploadController(CloudinaryService cloudinaryService) {
        this.cloudinaryService = cloudinaryService;
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "folder", required = false, defaultValue = "evabirds/farms") String folder,
            @RequestParam(value = "isFarmPhoto", required = false) Boolean isFarmPhoto
    ) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "File is empty"));
        }

        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            return ResponseEntity.badRequest().body(Map.of("error", "Only image files (PNG, JPEG, WebP) are allowed"));
        }

        // Determine if this is a farm photo (watermark strictly for farm photos)
        boolean isFarm = Boolean.TRUE.equals(isFarmPhoto) || (folder != null && folder.toLowerCase().contains("farm"));

        try {
            Map<String, Object> response = cloudinaryService.processUpload(file, folder, isFarm);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IOException e) {
            log.error("Failed to process image upload: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to upload image: " + e.getMessage()));
        }
    }

    @PostMapping("/base64")
    public ResponseEntity<?> uploadBase64(@RequestBody Map<String, Object> request) {
        String base64Image = (String) request.get("image");
        String folder = (String) request.getOrDefault("folder", "evabirds/farms");
        Object farmParam = request.get("isFarmPhoto");
        boolean isFarm = farmParam instanceof Boolean ? (Boolean) farmParam : (folder != null && folder.toLowerCase().contains("farm"));

        if (base64Image == null || base64Image.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("error", "image parameter is required"));
        }

        try {
            Map<String, Object> response = cloudinaryService.processBase64Upload(base64Image, folder, isFarm);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IOException e) {
            log.error("Failed to process base64 image upload: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to upload base64 image: " + e.getMessage()));
        }
    }

    @DeleteMapping
    public ResponseEntity<?> deleteFile(@RequestParam("publicId") String publicId) {
        try {
            Map<String, Object> result = cloudinaryService.deleteFile(publicId);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("Failed to delete asset {}: {}", publicId, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to delete asset: " + e.getMessage()));
        }
    }
}
