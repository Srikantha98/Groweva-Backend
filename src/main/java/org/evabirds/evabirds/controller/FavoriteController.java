package org.evabirds.evabirds.controller;

import org.evabirds.evabirds.dto.FarmResponse;
import org.evabirds.evabirds.service.FavoriteService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/favorites")
public class FavoriteController {

    private final FavoriteService favoriteService;

    public FavoriteController(FavoriteService favoriteService) {
        this.favoriteService = favoriteService;
    }

    @PostMapping("/{farmId}")
    public ResponseEntity<Map<String, Object>> toggleFavorite(
            @PathVariable Long farmId,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        boolean isFavorited = favoriteService.toggleFavorite(userDetails.getUsername(), farmId);
        return ResponseEntity.ok(Map.of(
                "farmId", farmId,
                "isFavorite", isFavorited,
                "message", isFavorited ? "Farm added to favorites" : "Farm removed from favorites"
        ));
    }

    @GetMapping
    public ResponseEntity<List<FarmResponse>> getMyFavorites(
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        List<FarmResponse> favorites = favoriteService.getMyFavorites(userDetails.getUsername());
        return ResponseEntity.ok(favorites);
    }

    @GetMapping("/{farmId}/check")
    public ResponseEntity<Map<String, Boolean>> checkFavorite(
            @PathVariable Long farmId,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        boolean isFavorited = favoriteService.isFavorite(userDetails.getUsername(), farmId);
        return ResponseEntity.ok(Collections.singletonMap("isFavorite", isFavorited));
    }
}
