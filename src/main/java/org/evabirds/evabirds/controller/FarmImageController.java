package org.evabirds.evabirds.controller;

import jakarta.validation.Valid;
import org.evabirds.evabirds.dto.AddFarmImageRequest;
import org.evabirds.evabirds.dto.FarmImageResponse;
import org.evabirds.evabirds.service.FarmImageService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/farms/{farmId}/images")
public class FarmImageController {

    private final FarmImageService farmImageService;

    public FarmImageController(FarmImageService farmImageService) {
        this.farmImageService = farmImageService;
    }

    @PostMapping
    public ResponseEntity<FarmImageResponse> addImage(
            @PathVariable Long farmId,
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody AddFarmImageRequest request
    ) {
        FarmImageResponse response = farmImageService.addImage(farmId, userDetails.getUsername(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<FarmImageResponse>> getImages(@PathVariable Long farmId) {
        List<FarmImageResponse> images = farmImageService.getImagesByFarmId(farmId);
        return ResponseEntity.ok(images);
    }

    @PatchMapping("/{imageId}/primary")
    public ResponseEntity<FarmImageResponse> setPrimaryImage(
            @PathVariable Long farmId,
            @PathVariable Long imageId,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        FarmImageResponse response = farmImageService.setPrimaryImage(farmId, imageId, userDetails.getUsername());
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{imageId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteImage(
            @PathVariable Long farmId,
            @PathVariable Long imageId,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        farmImageService.deleteImage(farmId, imageId, userDetails.getUsername());
    }
}
