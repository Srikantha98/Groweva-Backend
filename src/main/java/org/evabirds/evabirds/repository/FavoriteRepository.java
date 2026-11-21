package org.evabirds.evabirds.repository;

import org.evabirds.evabirds.entity.Favorite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FavoriteRepository extends JpaRepository<Favorite, Long> {

    Optional<Favorite> findByUserIdAndFarmId(Long userId, Long farmId);

    boolean existsByUserIdAndFarmId(Long userId, Long farmId);

    List<Favorite> findByUserIdOrderByCreatedAtDesc(Long userId);

    void deleteByUserIdAndFarmId(Long userId, Long farmId);

    @Modifying
    @Query("DELETE FROM Favorite f WHERE f.user.id = :userId")
    void deleteByUserId(@Param("userId") Long userId);

    @Modifying
    @Query("DELETE FROM Favorite f WHERE f.farm.id = :farmId")
    void deleteByFarmId(@Param("farmId") Long farmId);

    @Modifying
    @Query("DELETE FROM Favorite f WHERE f.farm.id IN :farmIds")
    void deleteByFarmIdIn(@Param("farmIds") List<Long> farmIds);
}

