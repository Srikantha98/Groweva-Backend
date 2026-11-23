package org.evabirds.evabirds.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
public class CloudinaryService {

    private static final Logger log = LoggerFactory.getLogger(CloudinaryService.class);
    private final Cloudinary cloudinary;

    public CloudinaryService(Cloudinary cloudinary) {
        this.cloudinary = cloudinary;
    }

    public boolean isConfigured() {
        return cloudinary.config.cloudName != null && !cloudinary.config.cloudName.isBlank()
                && cloudinary.config.apiKey != null && !cloudinary.config.apiKey.isBlank();
    }

    @SuppressWarnings("unchecked")
    public Map<String, Object> uploadFile(MultipartFile file, String folder) throws IOException {
        if (!isConfigured()) {
            throw new IllegalStateException("Cloudinary is not configured. Supply credentials in .env");
        }

        String targetFolder = (folder != null && !folder.isBlank()) ? folder : "evabirds/uploads";
        Map<String, Object> params = ObjectUtils.asMap(
                "folder", targetFolder,
                "resource_type", "auto"
        );

        return (Map<String, Object>) cloudinary.uploader().upload(file.getBytes(), params);
    }

    @SuppressWarnings("unchecked")
    public Map<String, Object> uploadBase64(String base64Image, String folder) throws IOException {
        if (!isConfigured()) {
            throw new IllegalStateException("Cloudinary is not configured. Supply credentials in .env");
        }

        String targetFolder = (folder != null && !folder.isBlank()) ? folder : "evabirds/uploads";
        Map<String, Object> params = ObjectUtils.asMap(
                "folder", targetFolder,
                "resource_type", "auto"
        );

        return (Map<String, Object>) cloudinary.uploader().upload(base64Image, params);
    }

    @SuppressWarnings("unchecked")
    public Map<String, Object> deleteFile(String publicId) throws IOException {
        if (!isConfigured()) {
            throw new IllegalStateException("Cloudinary is not configured.");
        }

        return (Map<String, Object>) cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
    }
}
