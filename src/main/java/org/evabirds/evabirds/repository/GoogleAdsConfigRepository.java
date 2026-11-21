package org.evabirds.evabirds.repository;

import org.evabirds.evabirds.entity.GoogleAdsConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GoogleAdsConfigRepository extends JpaRepository<GoogleAdsConfig, Long> {
}
