package org.evabirds.evabirds.service;

import org.evabirds.evabirds.dto.ProfileResponse;
import org.evabirds.evabirds.dto.UpdateProfileRequest;
import org.evabirds.evabirds.entity.Profile;
import org.evabirds.evabirds.entity.User;
import org.evabirds.evabirds.exception.ResourceNotFoundException;
import org.evabirds.evabirds.repository.ProfileRepository;
import org.evabirds.evabirds.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProfileService {

    private final ProfileRepository profileRepository;
    private final UserRepository userRepository;

    public ProfileService(ProfileRepository profileRepository, UserRepository userRepository) {
        this.profileRepository = profileRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public ProfileResponse getProfileByEmail(String email) {
        Profile profile = profileRepository.findByUserEmail(email)
                .orElseGet(() -> {
                    User user = userRepository.findByEmailIgnoreCase(email)
                            .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
                    Profile newProfile = new Profile(user);
                    return profileRepository.save(newProfile);
                });

        return ProfileResponse.fromEntity(profile);
    }

    @Transactional(readOnly = true)
    public ProfileResponse getProfileByUserId(Long userId) {
        Profile profile = profileRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Profile not found for user ID: " + userId));

        return ProfileResponse.fromEntity(profile);
    }

    @Transactional(readOnly = true)
    public List<ProfileResponse> getAllProfiles() {
        return profileRepository.findAll()
                .stream()
                .map(ProfileResponse::fromEntity)
                .toList();
    }

    @Transactional
    public ProfileResponse updateProfile(String email, UpdateProfileRequest request) {
        Profile profile = profileRepository.findByUserEmail(email)
                .orElseGet(() -> {
                    User user = userRepository.findByEmailIgnoreCase(email)
                            .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
                    Profile newProfile = new Profile(user);
                    return profileRepository.save(newProfile);
                });

        if (request.getFullName() != null) {
            profile.setFullName(request.getFullName());
        }
        if (request.getPhone() != null) {
            profile.setPhone(request.getPhone());
        }
        if (request.getProfileImageUrl() != null) {
            profile.setProfileImageUrl(request.getProfileImageUrl());
        }
        if (request.getAddress() != null) {
            profile.setAddress(request.getAddress());
        }
        if (request.getCity() != null) {
            profile.setCity(request.getCity());
        }
        if (request.getState() != null) {
            profile.setState(request.getState());
        }
        if (request.getPincode() != null) {
            profile.setPincode(request.getPincode());
        }

        Profile updated = profileRepository.save(profile);
        return ProfileResponse.fromEntity(updated);
    }
}
