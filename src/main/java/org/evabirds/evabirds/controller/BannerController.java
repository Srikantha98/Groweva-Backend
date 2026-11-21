package org.evabirds.evabirds.controller;

import org.evabirds.evabirds.entity.Banner;
import org.evabirds.evabirds.service.BannerService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/banners")
public class BannerController {

    private final BannerService bannerService;

    public BannerController(BannerService bannerService) {
        this.bannerService = bannerService;
    }

    @GetMapping("/sponsorship-limit")
    public ResponseEntity<Map<String, Object>> getSponsorshipLimit() {
        return ResponseEntity.ok(bannerService.getSponsorshipSlotStatus());
    }

    @GetMapping
    public ResponseEntity<List<Banner>> getAllBanners(
            @RequestParam(required = false) Boolean active
    ) {
        return ResponseEntity.ok(bannerService.getAllBanners(active));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Banner> getBannerById(@PathVariable String id) {
        return bannerService.getBannerById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Banner> createBanner(@RequestBody Banner banner) {
        Banner saved = bannerService.saveBanner(banner);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Banner> updateBanner(@PathVariable String id, @RequestBody Banner banner) {
        banner.setId(id);
        Banner saved = bannerService.saveBanner(banner);
        return ResponseEntity.ok(saved);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteBanner(@PathVariable String id) {
        bannerService.deleteBanner(id);
    }

    @PostMapping("/{id}/click")
    public ResponseEntity<Banner> recordClick(@PathVariable String id) {
        Banner updated = bannerService.recordClick(id);
        return updated != null ? ResponseEntity.ok(updated) : ResponseEntity.notFound().build();
    }

    @PostMapping("/{id}/impression")
    public ResponseEntity<Banner> recordImpression(@PathVariable String id) {
        Banner updated = bannerService.recordImpression(id);
        return updated != null ? ResponseEntity.ok(updated) : ResponseEntity.notFound().build();
    }
}
