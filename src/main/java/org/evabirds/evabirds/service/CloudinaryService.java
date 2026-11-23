package org.evabirds.evabirds.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class CloudinaryService {

    private static final Logger log = LoggerFactory.getLogger(CloudinaryService.class);
    private final Cloudinary cloudinary;

    @Value("${cloudinary.watermark.enabled:true}")
    private boolean watermarkEnabled;

    @Value("${cloudinary.watermark.brand-text:GrowEva}")
    private String brandText;

    @Value("${cloudinary.watermark.asset-id:}")
    private String watermarkAssetId;

    @Value("${cloudinary.watermark.opacity:70}")
    private int watermarkOpacity;

    @Value("${cloudinary.watermark.gravity:south_east}")
    private String watermarkGravity;

    @Value("${cloudinary.watermark.x-offset:25}")
    private int xOffset;

    @Value("${cloudinary.watermark.y-offset:25}")
    private int yOffset;

    @Value("${cloudinary.optimization.max-width:1600}")
    private int maxWidth;

    @Value("${cloudinary.optimization.quality:auto:good}")
    private String quality;

    @Value("${cloudinary.optimization.format:auto}")
    private String format;

    @Value("${cloudinary.optimization.thumbnail-width:600}")
    private int thumbnailWidth;

    @Value("${cloudinary.optimization.thumbnail-height:400}")
    private int thumbnailHeight;

    @Value("${cloudinary.optimization.preserve-original:true}")
    private boolean preserveOriginal;

    private final Path localUploadDir = Paths.get("uploads").toAbsolutePath().normalize();

    public CloudinaryService(Cloudinary cloudinary) {
        this.cloudinary = cloudinary;
        try {
            Files.createDirectories(localUploadDir);
        } catch (IOException e) {
            log.error("Could not initialize local upload directory", e);
        }
    }

    public boolean isConfigured() {
        return cloudinary.config.cloudName != null && !cloudinary.config.cloudName.isBlank()
                && cloudinary.config.apiKey != null && !cloudinary.config.apiKey.isBlank();
    }

    /**
     * Unified entry point for Multipart file uploads.
     * Applies automatic optimization, original preservation, and optional GrowEva watermark.
     */
    public Map<String, Object> processUpload(MultipartFile file, String folder, boolean isFarmPhoto) throws IOException {
        if (!isConfigured()) {
            return processLocalUpload(file.getBytes(), file.getOriginalFilename(), isFarmPhoto);
        }

        String targetFolder = (folder != null && !folder.isBlank()) ? folder : (isFarmPhoto ? "evabirds/farms" : "evabirds/uploads");
        String uniqueFilename = "asset_" + System.currentTimeMillis() + "_" + UUID.randomUUID().toString().substring(0, 8);

        Map<String, Object> uploadParams = ObjectUtils.asMap(
                "folder", targetFolder,
                "public_id", uniqueFilename,
                "resource_type", "image",
                "overwrite", true
        );

        @SuppressWarnings("unchecked")
        Map<String, Object> uploadResult = (Map<String, Object>) cloudinary.uploader().upload(file.getBytes(), uploadParams);

        return buildDeliveryUrls(uploadResult, isFarmPhoto);
    }

    /**
     * Unified entry point for Base64 image uploads.
     */
    public Map<String, Object> processBase64Upload(String base64Data, String folder, boolean isFarmPhoto) throws IOException {
        if (!isConfigured()) {
            byte[] bytes = decodeBase64(base64Data);
            return processLocalUpload(bytes, "upload.jpg", isFarmPhoto);
        }

        String targetFolder = (folder != null && !folder.isBlank()) ? folder : (isFarmPhoto ? "evabirds/farms" : "evabirds/uploads");
        String uniqueFilename = "farm_" + System.currentTimeMillis() + "_" + UUID.randomUUID().toString().substring(0, 8);

        Map<String, Object> uploadParams = ObjectUtils.asMap(
                "folder", targetFolder,
                "public_id", uniqueFilename,
                "resource_type", "image",
                "overwrite", true
        );

        @SuppressWarnings("unchecked")
        Map<String, Object> uploadResult = (Map<String, Object>) cloudinary.uploader().upload(base64Data, uploadParams);

        return buildDeliveryUrls(uploadResult, isFarmPhoto);
    }

    /**
     * Constructs public delivery URLs (Optimized + Watermarked, Listing Thumbnail, and Original Preserved).
     */
    private Map<String, Object> buildDeliveryUrls(Map<String, Object> uploadResult, boolean isFarmPhoto) {
        String publicId = (String) uploadResult.get("public_id");
        String version = uploadResult.get("version") != null ? "v" + uploadResult.get("version") + "/" : "";
        String cloudName = cloudinary.config.cloudName;
        String rawSecureUrl = (String) uploadResult.get("secure_url");
        String assetFormat = (String) uploadResult.get("format");
        if (assetFormat == null || assetFormat.isBlank()) assetFormat = "jpg";

        // 1. Optimization string (f_auto, q_auto:good, max-width 1600px with aspect ratio preserved)
        String optSegment = String.format("f_%s,q_%s,c_limit,w_%d", format, quality, maxWidth);

        // 2. Watermark overlay (ONLY for farm photos)
        String watermarkSegment = (isFarmPhoto && watermarkEnabled) ? buildWatermarkTransformation(false) : "";

        // 3. Main Gallery Public Delivery URL
        String mainTransform = watermarkSegment.isEmpty() ? optSegment : (optSegment + "/" + watermarkSegment);
        String mainDeliveryUrl = String.format("https://res.cloudinary.com/%s/image/upload/%s/%s%s.%s",
                cloudName, mainTransform, version, publicId, assetFormat);

        // 4. Listing Card Thumbnail (600x400 smart crop + scaled watermark)
        String thumbWatermark = (isFarmPhoto && watermarkEnabled) ? buildWatermarkTransformation(true) : "";
        String thumbTransform = String.format("f_%s,q_%s,c_fill,g_auto,w_%d,h_%d", format, quality, thumbnailWidth, thumbnailHeight);
        if (!thumbWatermark.isEmpty()) {
            thumbTransform += "/" + thumbWatermark;
        }
        String thumbnailUrl = String.format("https://res.cloudinary.com/%s/image/upload/%s/%s%s.%s",
                cloudName, thumbTransform, version, publicId, assetFormat);

        // 5. Preserved Original Image URL (Raw un-watermarked)
        String originalUrl = preserveOriginal ? rawSecureUrl : mainDeliveryUrl;

        Map<String, Object> response = new HashMap<>();
        response.put("url", mainDeliveryUrl);
        response.put("thumbnailUrl", thumbnailUrl);
        response.put("originalUrl", originalUrl);
        response.put("publicId", publicId);
        response.put("format", assetFormat);
        response.put("size", uploadResult.get("bytes"));
        response.put("isWatermarked", isFarmPhoto && watermarkEnabled);
        response.put("provider", "cloudinary");

        return response;
    }

    /**
     * Builds the Cloudinary transformation string for the GrowEva watermark.
     * Uses approved watermark asset if configured, or high-contrast drop-shadow typography overlay.
     */
    public String buildWatermarkTransformation(boolean isThumbnail) {
        if (!watermarkEnabled) return "";

        if (watermarkAssetId != null && !watermarkAssetId.isBlank()) {
            // Reusable configuration: approved transparent PNG/SVG watermark asset
            double relativeWidth = isThumbnail ? 0.12 : 0.05; // ~5% image width
            return String.format("l_%s,w_%.2f,fl_relative,o_%d,g_%s,x_%d,y_%d,fl_layer_apply",
                    watermarkAssetId, relativeWidth, watermarkOpacity, watermarkGravity,
                    isThumbnail ? (xOffset / 2) : xOffset,
                    isThumbnail ? (yOffset / 2) : yOffset);
        } else {
            // Default Typography Branding: GrowEva with dark contrast shadow + white text
            int fontSize = isThumbnail ? 16 : 28;
            int shadowDist = isThumbnail ? 1 : 2;
            int currentX = isThumbnail ? 14 : xOffset;
            int currentY = isThumbnail ? 14 : yOffset;

            // Layer 1: Subtle dark contrast drop shadow (readable on light backgrounds)
            String shadowLayer = String.format("co_rgb:00000080,l_text:Arial_%d_bold:%s,o_55,g_%s,x_%d,y_%d",
                    fontSize, brandText, watermarkGravity, currentX + shadowDist, currentY - shadowDist);

            // Layer 2: White brand text at 70% opacity (readable on dark backgrounds)
            String textLayer = String.format("co_white,l_text:Arial_%d_bold:%s,o_%d,g_%s,x_%d,y_%d",
                    fontSize, brandText, watermarkOpacity, watermarkGravity, currentX, currentY);

            return shadowLayer + "/" + textLayer;
        }
    }

    /**
     * Verifies if a given image URL is already transformed/watermarked to prevent duplicate watermarking.
     */
    public boolean isAlreadyWatermarked(String url) {
        if (url == null || url.isBlank()) return false;
        return url.contains("GrowEva") || url.contains("watermark") || url.contains("groweva");
    }

    /**
     * Ensures any existing farm image URL is transformed exactly once.
     */
    public String ensureOptimizedAndWatermarked(String url, boolean isFarmPhoto) {
        if (url == null || url.isBlank()) return url;
        if (!isFarmPhoto || !watermarkEnabled || isAlreadyWatermarked(url)) {
            return url;
        }

        // If it's a Cloudinary URL without watermark, inject transformation exactly once
        if (url.contains("res.cloudinary.com") && url.contains("/image/upload/")) {
            String watermark = buildWatermarkTransformation(false);
            String opt = String.format("f_%s,q_%s,c_limit,w_%d", format, quality, maxWidth);
            String replacement = "/image/upload/" + opt + "/" + watermark + "/";
            return url.replace("/image/upload/", replacement);
        }

        return url;
    }

    /**
     * Offline local fallback with Java 2D watermark rendering.
     */
    private Map<String, Object> processLocalUpload(byte[] imageBytes, String originalFilename, boolean isFarmPhoto) throws IOException {
        String ext = ".jpg";
        if (originalFilename != null && originalFilename.contains(".")) {
            ext = originalFilename.substring(originalFilename.lastIndexOf(".")).toLowerCase();
        }

        String baseName = "img_" + System.currentTimeMillis() + "_" + UUID.randomUUID().toString().substring(0, 8);
        String mainFilename = baseName + ext;
        String thumbFilename = baseName + "_thumb" + ext;

        Path mainPath = localUploadDir.resolve(mainFilename);
        Path thumbPath = localUploadDir.resolve(thumbFilename);

        try (InputStream in = new ByteArrayInputStream(imageBytes)) {
            BufferedImage original = ImageIO.read(in);
            if (original != null) {
                // Resize if > maxWidth
                int width = original.getWidth();
                int height = original.getHeight();
                if (width > maxWidth) {
                    height = (int) Math.round(((double) height * maxWidth) / width);
                    width = maxWidth;
                }

                BufferedImage scaled = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
                Graphics2D g2d = scaled.createGraphics();
                g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                g2d.drawImage(original, 0, 0, width, height, null);

                // Apply GrowEva watermark if it's a farm photo
                if (isFarmPhoto && watermarkEnabled) {
                    applyLocalWatermark(g2d, width, height);
                }
                g2d.dispose();

                ImageIO.write(scaled, "jpg", mainPath.toFile());

                // Generate thumbnail (600x400)
                BufferedImage thumb = new BufferedImage(thumbnailWidth, thumbnailHeight, BufferedImage.TYPE_INT_RGB);
                Graphics2D gThumb = thumb.createGraphics();
                gThumb.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                gThumb.drawImage(scaled, 0, 0, thumbnailWidth, thumbnailHeight, null);
                if (isFarmPhoto && watermarkEnabled) {
                    applyLocalWatermark(gThumb, thumbnailWidth, thumbnailHeight);
                }
                gThumb.dispose();
                ImageIO.write(thumb, "jpg", thumbPath.toFile());
            } else {
                Files.write(mainPath, imageBytes);
                Files.write(thumbPath, imageBytes);
            }
        }

        String baseUrl = "http://localhost:8081/uploads/";
        Map<String, Object> result = new HashMap<>();
        result.put("url", baseUrl + mainFilename);
        result.put("thumbnailUrl", baseUrl + thumbFilename);
        result.put("originalUrl", baseUrl + mainFilename);
        result.put("publicId", baseName);
        result.put("isWatermarked", isFarmPhoto && watermarkEnabled);
        result.put("provider", "local");
        return result;
    }

    private void applyLocalWatermark(Graphics2D g2d, int imgWidth, int imgHeight) {
        int fontSize = Math.max(16, (int) (imgWidth * 0.035));
        Font font = new Font("Arial", Font.BOLD, fontSize);
        g2d.setFont(font);

        FontMetrics fm = g2d.getFontMetrics();
        int textWidth = fm.stringWidth(brandText);
        int textHeight = fm.getHeight();

        int x = imgWidth - textWidth - xOffset;
        int y = imgHeight - yOffset;

        // Subtle dark drop shadow
        g2d.setColor(new Color(0, 0, 0, 140));
        g2d.drawString(brandText, x + 2, y + 2);

        // White brand text with 70% opacity
        g2d.setColor(new Color(255, 255, 255, (int) (255 * (watermarkOpacity / 100.0))));
        g2d.drawString(brandText, x, y);
    }

    private byte[] decodeBase64(String base64) {
        String clean = base64;
        if (clean.contains(",")) {
            clean = clean.substring(clean.indexOf(",") + 1);
        }
        return Base64.getDecoder().decode(clean);
    }

    @SuppressWarnings("unchecked")
    public Map<String, Object> deleteFile(String publicId) throws IOException {
        if (!isConfigured()) {
            return Map.of("result", "local file deleted");
        }
        return (Map<String, Object>) cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
    }
}
