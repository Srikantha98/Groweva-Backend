package org.evabirds.evabirds.service;

import org.evabirds.evabirds.dto.FarmResponse;
import org.evabirds.evabirds.entity.Farm;
import org.evabirds.evabirds.entity.Favorite;
import org.evabirds.evabirds.entity.User;
import org.evabirds.evabirds.exception.ResourceNotFoundException;
import org.evabirds.evabirds.repository.FarmRepository;
import org.evabirds.evabirds.repository.FavoriteRepository;
import org.evabirds.evabirds.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class FavoriteService {

    private final FavoriteRepository favoriteRepository;
    private final FarmRepository farmRepository;
    private final UserRepository userRepository;

    public FavoriteService(
            FavoriteRepository favoriteRepository,
            FarmRepository farmRepository,
            UserRepository userRepository
    ) {
        this.favoriteRepository = favoriteRepository;
        this.farmRepository = farmRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public boolean toggleFavorite(String userEmail, Long farmId) {
        User user = userRepository.findByEmailIgnoreCase(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + userEmail));

        Farm farm = farmRepository.findById(farmId)
                .orElseThrow(() -> new ResourceNotFoundException("Farm not found with id: " + farmId));

        if (favoriteRepository.existsByUserIdAndFarmId(user.getId(), farmId)) {
            favoriteRepository.deleteByUserIdAndFarmId(user.getId(), farmId);
            return false; // Removed from favorites
        } else {
            Favorite favorite = new Favorite(user, farm);
            favoriteRepository.save(favorite);
            return true; // Added to favorites
        }
    }

    @Transactional(readOnly = true)
    public boolean isFavorite(String userEmail, Long farmId) {
        User user = userRepository.findByEmailIgnoreCase(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + userEmail));

        return favoriteRepository.existsByUserIdAndFarmId(user.getId(), farmId);
    }

    @Transactional(readOnly = true)
    public List<FarmResponse> getMyFavorites(String userEmail) {
        User user = userRepository.findByEmailIgnoreCase(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + userEmail));

        return favoriteRepository.findByUserIdOrderByCreatedAtDesc(user.getId())
                .stream()
                .map(favorite -> FarmResponse.fromEntity(favorite.getFarm()))
                .toList();
    }
}
