package org.evabirds.evabirds.service;

import org.evabirds.evabirds.dto.AddFarmImageRequest;
import org.evabirds.evabirds.dto.FarmImageResponse;
import org.evabirds.evabirds.entity.Farm;
import org.evabirds.evabirds.entity.FarmImage;
import org.evabirds.evabirds.exception.ResourceNotFoundException;
import org.evabirds.evabirds.repository.FarmImageRepository;
import org.evabirds.evabirds.repository.FarmRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class FarmImageService {

    private final FarmImageRepository farmImageRepository;
    private final FarmRepository farmRepository;
    private final CloudinaryService cloudinaryService;

    public FarmImageService(FarmImageRepository farmImageRepository, FarmRepository farmRepository, CloudinaryService cloudinaryService) {
        this.farmImageRepository = farmImageRepository;
        this.farmRepository = farmRepository;
        this.cloudinaryService = cloudinaryService;
    }

    @Transactional
    public FarmImageResponse addImage(Long farmId, String userEmail, AddFarmImageRequest request) {
        Farm farm = farmRepository.findById(farmId)
                .orElseThrow(() -> new ResourceNotFoundException("Farm not found with id: " + farmId));

        if (!farm.getOwner().getEmail().equalsIgnoreCase(userEmail)) {
            throw new AccessDeniedException("You are not authorized to add images to this farm");
        }

        long existingCount = farmImageRepository.countByFarmId(farmId);
        boolean shouldBePrimary = Boolean.TRUE.equals(request.getIsPrimary()) || existingCount == 0;

        if (shouldBePrimary) {
            List<FarmImage> existingImages = farmImageRepository.findByFarmIdOrderByDisplayOrderAscIdAsc(farmId);
            for (FarmImage img : existingImages) {
                if (img.isPrimary()) {
                    img.setPrimary(false);
                    farmImageRepository.save(img);
                }
            }
        }

        int order = request.getDisplayOrder() != null ? request.getDisplayOrder() : (int) existingCount;

        String rawUrl = request.getImageUrl();
        String finalUrl = rawUrl;
        String thumbUrl = request.getThumbnailUrl();
        String origUrl = request.getOriginalUrl();
        String pubId = request.getPublicId();

        if (rawUrl != null && rawUrl.startsWith("data:image/")) {
            try {
                var res = cloudinaryService.processBase64Upload(rawUrl, "evabirds/farms", true);
                finalUrl = (String) res.get("url");
                thumbUrl = (String) res.get("thumbnailUrl");
                origUrl = (String) res.get("originalUrl");
                pubId = (String) res.get("publicId");
            } catch (Exception ignored) {
            }
        } else if (rawUrl != null) {
            finalUrl = cloudinaryService.ensureOptimizedAndWatermarked(rawUrl, true);
            if (thumbUrl == null || thumbUrl.isBlank()) {
                thumbUrl = finalUrl;
            }
        }

        FarmImage farmImage = new FarmImage(
                farm,
                finalUrl,
                thumbUrl,
                origUrl != null ? origUrl : finalUrl,
                pubId,
                request.getCategory(),
                request.getCaption(),
                shouldBePrimary,
                order
        );

        FarmImage savedImage = farmImageRepository.save(farmImage);
        return FarmImageResponse.fromEntity(savedImage);
    }

    @Transactional(readOnly = true)
    public List<FarmImageResponse> getImagesByFarmId(Long farmId) {
        if (!farmRepository.existsById(farmId)) {
            throw new ResourceNotFoundException("Farm not found with id: " + farmId);
        }

        return farmImageRepository.findByFarmIdOrderByDisplayOrderAscIdAsc(farmId)
                .stream()
                .map(FarmImageResponse::fromEntity)
                .toList();
    }

    @Transactional
    public FarmImageResponse setPrimaryImage(Long farmId, Long imageId, String userEmail) {
        Farm farm = farmRepository.findById(farmId)
                .orElseThrow(() -> new ResourceNotFoundException("Farm not found with id: " + farmId));

        if (!farm.getOwner().getEmail().equalsIgnoreCase(userEmail)) {
            throw new AccessDeniedException("You are not authorized to modify images for this farm");
        }

        List<FarmImage> images = farmImageRepository.findByFarmIdOrderByDisplayOrderAscIdAsc(farmId);
        FarmImage targetImage = null;

        for (FarmImage img : images) {
            if (img.getId().equals(imageId)) {
                img.setPrimary(true);
                targetImage = img;
            } else {
                img.setPrimary(false);
            }
            farmImageRepository.save(img);
        }

        if (targetImage == null) {
            throw new ResourceNotFoundException("Image not found with id: " + imageId);
        }

        return FarmImageResponse.fromEntity(targetImage);
    }

    @Transactional
    public void deleteImage(Long farmId, Long imageId, String userEmail) {
        Farm farm = farmRepository.findById(farmId)
                .orElseThrow(() -> new ResourceNotFoundException("Farm not found with id: " + farmId));

        if (!farm.getOwner().getEmail().equalsIgnoreCase(userEmail)) {
            throw new AccessDeniedException("You are not authorized to delete images from this farm");
        }

        FarmImage targetImage = farmImageRepository.findById(imageId)
                .orElseThrow(() -> new ResourceNotFoundException("Image not found with id: " + imageId));

        if (!targetImage.getFarm().getId().equals(farmId)) {
            throw new IllegalArgumentException("Image does not belong to this farm");
        }

        boolean wasPrimary = targetImage.isPrimary();
        farmImageRepository.delete(targetImage);

        if (wasPrimary) {
            List<FarmImage> remainingImages = farmImageRepository.findByFarmIdOrderByDisplayOrderAscIdAsc(farmId);
            if (!remainingImages.isEmpty()) {
                FarmImage newPrimary = remainingImages.get(0);
                newPrimary.setPrimary(true);
                farmImageRepository.save(newPrimary);
            }
        }
    }
}
