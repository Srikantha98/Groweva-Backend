package org.evabirds.evabirds.repository;

import org.evabirds.evabirds.entity.Banner;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BannerRepository extends JpaRepository<Banner, String> {
    List<Banner> findByIsActiveTrue();
    List<Banner> findByPlacementAndIsActiveTrue(String placement);
    long countByAdType(String adType);
    long countByAdTypeNot(String adType);
}
