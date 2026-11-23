package org.evabirds.evabirds.service;

import jakarta.annotation.PostConstruct;
import org.evabirds.evabirds.constant.CacheNames;
import org.evabirds.evabirds.entity.Banner;
import org.evabirds.evabirds.entity.GoogleAdsConfig;
import org.evabirds.evabirds.repository.BannerRepository;
import org.evabirds.evabirds.repository.GoogleAdsConfigRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class BannerService {

    public static final int MAX_SPONSORSHIP_ADS = 5;

    private final BannerRepository bannerRepository;
    private final GoogleAdsConfigRepository googleAdsConfigRepository;

    public BannerService(BannerRepository bannerRepository, GoogleAdsConfigRepository googleAdsConfigRepository) {
        this.bannerRepository = bannerRepository;
        this.googleAdsConfigRepository = googleAdsConfigRepository;
    }

    @PostConstruct
    public void initSeedData() {
        // Zero banners auto-seeded. Banners and images must be published explicitly via Admin portal.

        if (googleAdsConfigRepository.count() == 0) {
            GoogleAdsConfig config = new GoogleAdsConfig();
            config.setId(1L);
            config.setEnabled(false);
            config.setPublisherId("");
            config.setHeaderSlotId("");
            config.setSidebarSlotId("");
            config.setAutoAdsEnabled(false);
            config.setTestMode(false);
            config.setLastUpdated(LocalDateTime.now());
            googleAdsConfigRepository.save(config);
        }
    }

    @Cacheable(value = CacheNames.BANNERS, key = "#activeOnly != null ? #activeOnly : false")
    public List<Banner> getAllBanners(Boolean activeOnly) {
        if (Boolean.TRUE.equals(activeOnly)) {
            return bannerRepository.findByIsActiveTrue();
        }
        return bannerRepository.findAll();
    }

    public Optional<Banner> getBannerById(String id) {
        return bannerRepository.findById(id);
    }

    public Map<String, Object> getSponsorshipSlotStatus() {
        long currentCount = bannerRepository.countByAdTypeNot("GOOGLE_ADS");
        Map<String, Object> status = new HashMap<>();
        status.put("maxAllowed", MAX_SPONSORSHIP_ADS);
        status.put("currentCount", currentCount);
        status.put("availableSlots", Math.max(0, MAX_SPONSORSHIP_ADS - currentCount));
        status.put("isLimitReached", currentCount >= MAX_SPONSORSHIP_ADS);
        return status;
    }

    @Transactional
    @CacheEvict(value = CacheNames.BANNERS, allEntries = true)
    public Banner saveBanner(Banner banner) {
        boolean isSponsorship = banner.getAdType() == null || !"GOOGLE_ADS".equalsIgnoreCase(banner.getAdType());

        if (banner.getId() == null || banner.getId().isBlank()) {
            banner.setId("banner-" + System.currentTimeMillis());
        }

        boolean isNew = !bannerRepository.existsById(banner.getId());

        if (isSponsorship) {
            long currentCount = bannerRepository.countByAdTypeNot("GOOGLE_ADS");
            if (isNew) {
                if (currentCount >= MAX_SPONSORSHIP_ADS) {
                    throw new ResponseStatusException(
                            HttpStatus.BAD_REQUEST,
                            "Maximum limit of 5 sponsorship ads reached (" + currentCount + "/5 slots occupied). Please remove an existing sponsorship ad before creating a new one."
                    );
                }
            } else {
                Banner existing = bannerRepository.findById(banner.getId()).orElse(null);
                if (existing != null && "GOOGLE_ADS".equalsIgnoreCase(existing.getAdType())) {
                    if (currentCount >= MAX_SPONSORSHIP_ADS) {
                        throw new ResponseStatusException(
                                HttpStatus.BAD_REQUEST,
                                "Maximum limit of 5 sponsorship ads reached. Cannot convert Google Ad to Sponsorship Ad."
                        );
                    }
                }
            }
        }

        if (banner.getCreatedAt() == null) {
            banner.setCreatedAt(LocalDateTime.now());
        }
        banner.setUpdatedAt(LocalDateTime.now());
        return bannerRepository.save(banner);
    }

    @Transactional
    @CacheEvict(value = CacheNames.BANNERS, allEntries = true)
    public void deleteBanner(String id) {
        bannerRepository.deleteById(id);
    }

    @Transactional
    public Banner recordClick(String id) {
        Banner banner = bannerRepository.findById(id).orElse(null);
        if (banner != null) {
            banner.setClicks((banner.getClicks() != null ? banner.getClicks() : 0) + 1);
            return bannerRepository.save(banner);
        }
        return null;
    }

    @Transactional
    public Banner recordImpression(String id) {
        Banner banner = bannerRepository.findById(id).orElse(null);
        if (banner != null) {
            banner.setImpressions((banner.getImpressions() != null ? banner.getImpressions() : 0) + 1);
            return bannerRepository.save(banner);
        }
        return null;
    }

    @Cacheable(value = CacheNames.GOOGLE_ADS, key = "'default'")
    public GoogleAdsConfig getGoogleAdsConfig() {
        return googleAdsConfigRepository.findById(1L).orElseGet(() -> {
            GoogleAdsConfig c = new GoogleAdsConfig();
            c.setId(1L);
            return googleAdsConfigRepository.save(c);
        });
    }

    @Transactional
    @CacheEvict(value = CacheNames.GOOGLE_ADS, allEntries = true)
    public GoogleAdsConfig updateGoogleAdsConfig(GoogleAdsConfig incoming) {
        GoogleAdsConfig config = getGoogleAdsConfig();
        if (incoming.getEnabled() != null) config.setEnabled(incoming.getEnabled());
        if (incoming.getPublisherId() != null) config.setPublisherId(incoming.getPublisherId());
        if (incoming.getHeaderSlotId() != null) config.setHeaderSlotId(incoming.getHeaderSlotId());
        if (incoming.getSidebarSlotId() != null) config.setSidebarSlotId(incoming.getSidebarSlotId());
        if (incoming.getAutoAdsEnabled() != null) config.setAutoAdsEnabled(incoming.getAutoAdsEnabled());
        if (incoming.getTestMode() != null) config.setTestMode(incoming.getTestMode());
        config.setLastUpdated(LocalDateTime.now());
        return googleAdsConfigRepository.save(config);
    }
}
