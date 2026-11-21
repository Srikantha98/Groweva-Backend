package org.evabirds.evabirds.controller;

import jakarta.validation.Valid;
import org.evabirds.evabirds.dto.CreateFarmRequest;
import org.evabirds.evabirds.dto.FarmResponse;
import org.evabirds.evabirds.dto.UpdateFarmRequest;
import org.evabirds.evabirds.entity.FarmStatus;
import org.evabirds.evabirds.entity.FarmType;
import org.evabirds.evabirds.entity.ListingType;
import org.evabirds.evabirds.service.FarmService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/farms")
public class FarmController {

    private final FarmService farmService;

    public FarmController(FarmService farmService) {
        this.farmService = farmService;
    }

    @PostMapping
    public ResponseEntity<FarmResponse> createFarm(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody CreateFarmRequest request
    ) {
        FarmResponse response = farmService.createFarm(userDetails.getUsername(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<FarmResponse>> getAllFarms(
            @RequestParam(required = false) ListingType listingType,
            @RequestParam(required = false) FarmType farmType,
            @RequestParam(required = false) FarmStatus status
    ) {
        List<FarmResponse> farms = farmService.getAllFarms(listingType, farmType, status);
        return ResponseEntity.ok(farms);
    }

    @GetMapping("/my-farms")
    public ResponseEntity<List<FarmResponse>> getMyFarms(
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        List<FarmResponse> myFarms = farmService.getMyFarms(userDetails.getUsername());
        return ResponseEntity.ok(myFarms);
    }

    @GetMapping("/{id}")
    public ResponseEntity<FarmResponse> getFarmById(@PathVariable Long id) {
        FarmResponse farm = farmService.getFarmById(id);
        return ResponseEntity.ok(farm);
    }

    @PutMapping("/{id}")
    public ResponseEntity<FarmResponse> updateFarm(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody UpdateFarmRequest request
    ) {
        FarmResponse updated = farmService.updateFarm(id, userDetails.getUsername(), request);
        return ResponseEntity.ok(updated);
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<FarmResponse> updateFarmStatus(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam FarmStatus status
    ) {
        FarmResponse updated = farmService.updateStatus(id, userDetails.getUsername(), status);
        return ResponseEntity.ok(updated);
    }

    @PostMapping("/{id}/view")
    public ResponseEntity<FarmResponse> incrementViews(@PathVariable Long id) {
        FarmResponse updated = farmService.incrementViews(id);
        return ResponseEntity.ok(updated);
    }

    @PatchMapping("/{id}/verify")
    public ResponseEntity<FarmResponse> verifyFarm(
            @PathVariable Long id,
            @RequestParam(defaultValue = "true") boolean isVerified,
            @RequestParam(required = false) String verifiedBy
    ) {
        FarmResponse updated = farmService.verifyFarm(id, isVerified, verifiedBy);
        return ResponseEntity.ok(updated);
    }

    @PatchMapping("/{id}/admin-status")
    public ResponseEntity<FarmResponse> adminUpdateFarmStatus(
            @PathVariable Long id,
            @RequestParam FarmStatus status
    ) {
        FarmResponse updated = farmService.adminUpdateStatus(id, status);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteFarm(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        farmService.deleteFarm(id, userDetails.getUsername());
    }
}
