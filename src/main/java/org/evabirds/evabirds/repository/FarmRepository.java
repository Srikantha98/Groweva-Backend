package org.evabirds.evabirds.repository;

import org.evabirds.evabirds.entity.Farm;
import org.evabirds.evabirds.entity.FarmStatus;
import org.evabirds.evabirds.entity.FarmType;
import org.evabirds.evabirds.entity.ListingType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FarmRepository extends JpaRepository<Farm, Long>, JpaSpecificationExecutor<Farm> {

    @EntityGraph(attributePaths = {"images", "owner", "owner.profile"})
    Optional<Farm> findById(Long id);

    @EntityGraph(attributePaths = {"images", "owner", "owner.profile"})
    List<Farm> findByOwnerId(Long ownerId);

    @EntityGraph(attributePaths = {"images", "owner", "owner.profile"})
    List<Farm> findByStatus(FarmStatus status);

    @EntityGraph(attributePaths = {"images", "owner", "owner.profile"})
    Page<Farm> findByStatus(FarmStatus status, Pageable pageable);

    long countByStatus(FarmStatus status);

    @Query("SELECT COALESCE(SUM(f.capacityBirds), 0) FROM Farm f WHERE f.status = 'ACTIVE'")
    long sumActiveBirdCapacity();

    @Query("SELECT COALESCE(SUM(f.viewsCount), 0) FROM Farm f")
    long sumTotalViews();

    @EntityGraph(attributePaths = {"images", "owner", "owner.profile"})
    List<Farm> findByListingTypeAndStatus(ListingType listingType, FarmStatus status);

    @EntityGraph(attributePaths = {"images", "owner", "owner.profile"})
    List<Farm> findByFarmTypeAndStatus(FarmType farmType, FarmStatus status);

    @EntityGraph(attributePaths = {"images", "owner", "owner.profile"})
    List<Farm> findByListingTypeAndFarmTypeAndStatus(ListingType listingType, FarmType farmType, FarmStatus status);
}
