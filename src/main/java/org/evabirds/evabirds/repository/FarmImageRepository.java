package org.evabirds.evabirds.repository;

import org.evabirds.evabirds.entity.FarmImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FarmImageRepository extends JpaRepository<FarmImage, Long> {

    List<FarmImage> findByFarmIdOrderByDisplayOrderAscIdAsc(Long farmId);

    Optional<FarmImage> findFirstByFarmIdAndIsPrimaryTrue(Long farmId);

    long countByFarmId(Long farmId);
}
