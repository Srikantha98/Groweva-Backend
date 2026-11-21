package org.evabirds.evabirds.repository;

import org.evabirds.evabirds.entity.Farm;
import org.evabirds.evabirds.entity.FarmStatus;
import org.evabirds.evabirds.entity.FarmType;
import org.evabirds.evabirds.entity.ListingType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FarmRepository extends JpaRepository<Farm, Long>, JpaSpecificationExecutor<Farm> {

    List<Farm> findByOwnerId(Long ownerId);

    List<Farm> findByStatus(FarmStatus status);

    long countByStatus(FarmStatus status);

    @Query("SELECT COALESCE(SUM(f.capacityBirds), 0) FROM Farm f WHERE f.status = 'ACTIVE'")
    long sumActiveBirdCapacity();

    @Query("SELECT COALESCE(SUM(f.viewsCount), 0) FROM Farm f")
    long sumTotalViews();

    List<Farm> findByListingTypeAndStatus(ListingType listingType, FarmStatus status);

    List<Farm> findByFarmTypeAndStatus(FarmType farmType, FarmStatus status);

    List<Farm> findByListingTypeAndFarmTypeAndStatus(ListingType listingType, FarmType farmType, FarmStatus status);
}

