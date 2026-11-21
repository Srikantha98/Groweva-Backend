package org.evabirds.evabirds.controller;

import org.evabirds.evabirds.entity.GoogleAdsConfig;
import org.evabirds.evabirds.service.BannerService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/google-ads")
public class GoogleAdsController {

    private final BannerService bannerService;

    public GoogleAdsController(BannerService bannerService) {
        this.bannerService = bannerService;
    }

    @GetMapping
    public ResponseEntity<GoogleAdsConfig> getGoogleAdsConfig() {
        return ResponseEntity.ok(bannerService.getGoogleAdsConfig());
    }

    @PutMapping
    public ResponseEntity<GoogleAdsConfig> updateGoogleAdsConfig(@RequestBody GoogleAdsConfig incoming) {
        GoogleAdsConfig updated = bannerService.updateGoogleAdsConfig(incoming);
        return ResponseEntity.ok(updated);
    }
}
