package org.evabirds.evabirds.service;

import org.evabirds.evabirds.dto.AddFarmImageRequest;
import org.evabirds.evabirds.dto.CreateFarmRequest;
import org.evabirds.evabirds.dto.FarmResponse;
import org.evabirds.evabirds.dto.UpdateFarmRequest;
import org.evabirds.evabirds.entity.Farm;
import org.evabirds.evabirds.entity.FarmImage;
import org.evabirds.evabirds.entity.FarmStatus;
import org.evabirds.evabirds.entity.FarmType;
import org.evabirds.evabirds.entity.ListingType;
import org.evabirds.evabirds.entity.User;
import org.evabirds.evabirds.exception.ResourceNotFoundException;
import org.evabirds.evabirds.repository.FarmRepository;
import org.evabirds.evabirds.repository.UserRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class FarmService {

    private final FarmRepository farmRepository;
    private final UserRepository userRepository;
    private final org.evabirds.evabirds.repository.EmployeeRepository employeeRepository;
    private final org.evabirds.evabirds.repository.FavoriteRepository favoriteRepository;
    private final org.evabirds.evabirds.repository.MessageRepository messageRepository;
    private final NotificationService notificationService;

    public FarmService(
            FarmRepository farmRepository,
            UserRepository userRepository,
            org.evabirds.evabirds.repository.EmployeeRepository employeeRepository,
            org.evabirds.evabirds.repository.FavoriteRepository favoriteRepository,
            org.evabirds.evabirds.repository.MessageRepository messageRepository,
            NotificationService notificationService
    ) {
        this.farmRepository = farmRepository;
        this.userRepository = userRepository;
        this.employeeRepository = employeeRepository;
        this.favoriteRepository = favoriteRepository;
        this.messageRepository = messageRepository;
        this.notificationService = notificationService;
    }

    @Transactional
    public FarmResponse createFarm(String ownerEmail, CreateFarmRequest request) {
        User owner = userRepository.findByEmailIgnoreCase(ownerEmail)
                .orElseGet(() -> {
                    var empOpt = employeeRepository.findByEmailIgnoreCase(ownerEmail);
                    if (empOpt.isPresent()) {
                        org.evabirds.evabirds.entity.Employee emp = empOpt.get();
                        User u = new User(emp.getEmail(), emp.getPasswordHash(), emp.getRole());
                        return userRepository.save(u);
                    }
                    throw new ResourceNotFoundException("User not found with email: " + ownerEmail);
                });

        FarmStatus targetStatus = request.getStatus() != null ? request.getStatus() : FarmStatus.ACTIVE;

        if (targetStatus != FarmStatus.DRAFT) {
            validatePaymentTerms(
                    request.getListingType(),
                    request.getPricingModel(),
                    request.getIsNegotiable(),
                    request.getMonthlyRent(),
                    request.getRentPerBatch(),
                    request.getRatePerBird(),
                    request.getLeaseAmount(),
                    request.getLeaseDurationMonths()
            );
        }

        Farm farm = new Farm();
        farm.setOwner(owner);
        farm.setFarmName(request.getFarmName());
        farm.setDescription(request.getDescription());
        farm.setLocation(request.getLocation());

        // Location fields
        farm.setCountry(request.getCountry() != null ? request.getCountry() : "India");
        farm.setState(request.getState());
        farm.setDistrict(request.getDistrict());
        farm.setTaluk(request.getTaluk());
        farm.setVillageCity(request.getVillageCity());
        farm.setLocality(request.getLocality());
        farm.setPincode(request.getPincode());
        farm.setAddressLandmark(request.getAddressLandmark());

        // Scope & Category
        farm.setListingScope(request.getListingScope());
        farm.setListingType(request.getListingType() != null ? request.getListingType() : ListingType.RENT);
        farm.setFarmType(request.getFarmType());
        farm.setHousingSystem(request.getHousingSystem());
        farm.setEnvironmentType(request.getEnvironmentType());
        farm.setHasCages(request.getHasCages());
        farm.setCageShedCount(request.getCageShedCount());
        farm.setCageDetails(request.getCageDetails());

        // Land & Capacity
        farm.setTotalLandArea(request.getTotalLandArea());
        farm.setLandAreaUnit(request.getLandAreaUnit());
        farm.setCapacityBirds(request.getCapacityBirds() != null ? request.getCapacityBirds() : request.getCapacity());
        farm.setTotalMaxCapacity(request.getTotalMaxCapacity());
        farm.setTotalAvailableCapacity(request.getTotalAvailableCapacity());
        farm.setShedCount(request.getShedCount());
        farm.setShedAreaSqft(request.getShedAreaSqft());
        farm.setTotalBuiltUpAreaSqft(request.getTotalBuiltUpAreaSqft());

        // Pricing & Terms
        farm.setPricingModel(request.getPricingModel());
        farm.setBillingPeriod(request.getBillingPeriod());
        farm.setEnteredPricingType(request.getEnteredPricingType());
        farm.setRatePerBird(request.getRatePerBird());
        farm.setRentPerBatch(request.getRentPerBatch());
        farm.setBatchDurationDays(request.getBatchDurationDays());
        farm.setMinBatches(request.getMinBatches());
        farm.setApplicableBirdCount(request.getApplicableBirdCount());
        farm.setBillingCountBasis(request.getBillingCountBasis());

        farm.setMonthlyRent(request.getMonthlyRent() != null ? request.getMonthlyRent() : request.getPrice());
        farm.setLeaseAmount(request.getLeaseAmount());
        farm.setLeaseDurationMonths(request.getLeaseDurationMonths());
        farm.setPaymentFrequency(request.getPaymentFrequency());
        BigDecimal deposit = request.getSecurityDeposit() != null ? request.getSecurityDeposit() : request.getAdvancePayment();
        farm.setSecurityDeposit(deposit);
        farm.setAdvancePayment(request.getAdvancePayment() != null ? request.getAdvancePayment() : deposit);
        farm.setElectricityCharges(request.getElectricityCharges());
        farm.setWaterCharges(request.getWaterCharges());
        farm.setMaintenanceResponsibility(request.getMaintenanceResponsibility());
        farm.setCleaningResponsibility(request.getCleaningResponsibility());
        farm.setDowntimeTerms(request.getDowntimeTerms());
        farm.setEquipmentIncluded(request.getEquipmentIncluded());
        farm.setNoticePeriod(request.getNoticePeriod());
        farm.setLatePaymentTerms(request.getLatePaymentTerms());
        farm.setOtherRentalConditions(request.getOtherRentalConditions());
        farm.setAvailability(request.getAvailability());
        farm.setAvailableFromDate(request.getAvailableFromDate());
        farm.setMinCommitment(request.getMinCommitment());
        farm.setRenewalTerms(request.getRenewalTerms());
        farm.setIsNegotiable(request.getIsNegotiable() != null ? request.getIsNegotiable() : false);

        // Facilities & Metadata
        farm.setFacilities(request.getFacilities());
        farm.setConfirmedByOwner(request.getConfirmedByOwner());
        farm.setShedsDataJson(request.getShedsDataJson());
        farm.setShedDistancesJson(request.getShedDistancesJson());
        farm.setStatus(targetStatus);

        // Process Farm Images
        if (request.getImages() != null && !request.getImages().isEmpty()) {
            int order = 0;
            boolean hasPrimary = false;
            for (AddFarmImageRequest imgReq : request.getImages()) {
                if (imgReq.getImageUrl() != null && !imgReq.getImageUrl().isBlank()) {
                    boolean isPrim = Boolean.TRUE.equals(imgReq.getIsPrimary()) && !hasPrimary;
                    if (isPrim) hasPrimary = true;
                    FarmImage img = new FarmImage(
                            farm,
                            imgReq.getImageUrl(),
                            imgReq.getCategory(),
                            imgReq.getCaption() != null ? imgReq.getCaption() : imgReq.getCategory(),
                            isPrim,
                            imgReq.getDisplayOrder() != null ? imgReq.getDisplayOrder() : order++
                    );
                    farm.addImage(img);
                }
            }
            if (!hasPrimary && !farm.getImages().isEmpty()) {
                farm.getImages().get(0).setPrimary(true);
            }
        } else if (request.getPrimaryImageUrl() != null && !request.getPrimaryImageUrl().isBlank()) {
            FarmImage img = new FarmImage(
                    farm,
                    request.getPrimaryImageUrl(),
                    "Hero",
                    "Primary Farm Image",
                    true,
                    0
            );
            farm.addImage(img);
        }

        Farm savedFarm = farmRepository.save(farm);

        // Generate notification for owner
        try {
            notificationService.createNotification(
                    owner,
                    "Shed Listed Successfully",
                    "Your poultry shed '" + savedFarm.getFarmName() + "' is published and now available for rent.",
                    "verified",
                    "/my-sheds"
            );
        } catch (Exception ignored) {
        }

        return FarmResponse.fromEntity(savedFarm);
    }

    @Transactional(readOnly = true)
    public FarmResponse getFarmById(Long id) {
        Farm farm = farmRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Farm not found with id: " + id));
        return FarmResponse.fromEntity(farm);
    }

    @Transactional(readOnly = true)
    public List<FarmResponse> getAllFarms(ListingType listingType, FarmType farmType, FarmStatus status) {
        List<Farm> farms;
        FarmStatus filterStatus = status != null ? status : FarmStatus.ACTIVE;

        if (listingType != null && farmType != null) {
            farms = farmRepository.findByListingTypeAndFarmTypeAndStatus(listingType, farmType, filterStatus);
        } else if (listingType != null) {
            farms = farmRepository.findByListingTypeAndStatus(listingType, filterStatus);
        } else if (farmType != null) {
            farms = farmRepository.findByFarmTypeAndStatus(farmType, filterStatus);
        } else {
            farms = farmRepository.findByStatus(filterStatus);
        }

        return farms.stream()
                .map(FarmResponse::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<FarmResponse> getMyFarms(String ownerEmail) {
        var ownerOpt = userRepository.findByEmailIgnoreCase(ownerEmail);
        if (ownerOpt.isPresent()) {
            List<FarmResponse> userFarms = farmRepository.findByOwnerId(ownerOpt.get().getId())
                    .stream()
                    .map(FarmResponse::fromEntity)
                    .toList();
            if (!userFarms.isEmpty()) {
                return userFarms;
            }
        }

        // Check if caller is platform administrator or staff
        var empOpt = employeeRepository.findByEmailIgnoreCase(ownerEmail);
        if (empOpt.isPresent()) {
            return farmRepository.findAll()
                    .stream()
                    .map(FarmResponse::fromEntity)
                    .toList();
        }

        if (ownerOpt.isPresent()) {
            return List.of();
        }

        throw new ResourceNotFoundException("User not found with email: " + ownerEmail);
    }

    @Transactional
    public FarmResponse updateFarm(Long farmId, String userEmail, UpdateFarmRequest request) {
        Farm farm = farmRepository.findById(farmId)
                .orElseThrow(() -> new ResourceNotFoundException("Farm not found with id: " + farmId));

        boolean isOwner = farm.getOwner() != null && farm.getOwner().getEmail().equalsIgnoreCase(userEmail);
        boolean isAdmin = employeeRepository.existsByEmailIgnoreCase(userEmail);
        if (!isOwner && !isAdmin) {
            throw new AccessDeniedException("You are not authorized to update this farm listing");
        }

        FarmStatus targetStatus = request.getStatus() != null ? request.getStatus() : farm.getStatus();

        if (targetStatus != FarmStatus.DRAFT) {
            ListingType targetListingType = request.getListingType() != null ? request.getListingType() : farm.getListingType();
            String targetPricingModel = request.getPricingModel() != null ? request.getPricingModel() : farm.getPricingModel();
            Boolean targetIsNegotiable = request.getIsNegotiable() != null ? request.getIsNegotiable() : farm.getIsNegotiable();
            BigDecimal targetMonthlyRent = request.getMonthlyRent() != null ? request.getMonthlyRent() : farm.getMonthlyRent();
            BigDecimal targetRentPerBatch = request.getRentPerBatch() != null ? request.getRentPerBatch() : farm.getRentPerBatch();
            BigDecimal targetRatePerBird = request.getRatePerBird() != null ? request.getRatePerBird() : farm.getRatePerBird();
            BigDecimal targetLeaseAmount = request.getLeaseAmount() != null ? request.getLeaseAmount() : farm.getLeaseAmount();
            Integer targetDuration = request.getLeaseDurationMonths() != null ? request.getLeaseDurationMonths() : farm.getLeaseDurationMonths();

            validatePaymentTerms(targetListingType, targetPricingModel, targetIsNegotiable, targetMonthlyRent, targetRentPerBatch, targetRatePerBird, targetLeaseAmount, targetDuration);
        }

        if (request.getFarmName() != null) farm.setFarmName(request.getFarmName());
        if (request.getDescription() != null) farm.setDescription(request.getDescription());
        if (request.getLocation() != null) farm.setLocation(request.getLocation());

        if (request.getCountry() != null) farm.setCountry(request.getCountry());
        if (request.getState() != null) farm.setState(request.getState());
        if (request.getDistrict() != null) farm.setDistrict(request.getDistrict());
        if (request.getTaluk() != null) farm.setTaluk(request.getTaluk());
        if (request.getVillageCity() != null) farm.setVillageCity(request.getVillageCity());
        if (request.getLocality() != null) farm.setLocality(request.getLocality());
        if (request.getPincode() != null) farm.setPincode(request.getPincode());
        if (request.getAddressLandmark() != null) farm.setAddressLandmark(request.getAddressLandmark());

        if (request.getListingScope() != null) farm.setListingScope(request.getListingScope());
        if (request.getListingType() != null) farm.setListingType(request.getListingType());
        if (request.getFarmType() != null) farm.setFarmType(request.getFarmType());
        if (request.getHousingSystem() != null) farm.setHousingSystem(request.getHousingSystem());
        if (request.getEnvironmentType() != null) farm.setEnvironmentType(request.getEnvironmentType());
        if (request.getHasCages() != null) farm.setHasCages(request.getHasCages());
        if (request.getCageShedCount() != null) farm.setCageShedCount(request.getCageShedCount());
        if (request.getCageDetails() != null) farm.setCageDetails(request.getCageDetails());

        if (request.getTotalLandArea() != null) farm.setTotalLandArea(request.getTotalLandArea());
        if (request.getLandAreaUnit() != null) farm.setLandAreaUnit(request.getLandAreaUnit());
        if (request.getCapacityBirds() != null) {
            farm.setCapacityBirds(request.getCapacityBirds());
        } else if (request.getCapacity() != null) {
            farm.setCapacityBirds(request.getCapacity());
        }
        if (request.getTotalMaxCapacity() != null) farm.setTotalMaxCapacity(request.getTotalMaxCapacity());
        if (request.getTotalAvailableCapacity() != null) farm.setTotalAvailableCapacity(request.getTotalAvailableCapacity());
        if (request.getShedCount() != null) farm.setShedCount(request.getShedCount());
        if (request.getShedAreaSqft() != null) farm.setShedAreaSqft(request.getShedAreaSqft());
        if (request.getTotalBuiltUpAreaSqft() != null) farm.setTotalBuiltUpAreaSqft(request.getTotalBuiltUpAreaSqft());

        if (request.getPricingModel() != null) farm.setPricingModel(request.getPricingModel());
        if (request.getBillingPeriod() != null) farm.setBillingPeriod(request.getBillingPeriod());
        if (request.getEnteredPricingType() != null) farm.setEnteredPricingType(request.getEnteredPricingType());
        if (request.getRatePerBird() != null) farm.setRatePerBird(request.getRatePerBird());
        if (request.getRentPerBatch() != null) farm.setRentPerBatch(request.getRentPerBatch());
        if (request.getBatchDurationDays() != null) farm.setBatchDurationDays(request.getBatchDurationDays());
        if (request.getMinBatches() != null) farm.setMinBatches(request.getMinBatches());
        if (request.getApplicableBirdCount() != null) farm.setApplicableBirdCount(request.getApplicableBirdCount());
        if (request.getBillingCountBasis() != null) farm.setBillingCountBasis(request.getBillingCountBasis());

        if (request.getMonthlyRent() != null) {
            farm.setMonthlyRent(request.getMonthlyRent());
        } else if (request.getPrice() != null) {
            farm.setMonthlyRent(request.getPrice());
        }
        if (request.getLeaseAmount() != null) farm.setLeaseAmount(request.getLeaseAmount());
        if (request.getLeaseDurationMonths() != null) farm.setLeaseDurationMonths(request.getLeaseDurationMonths());
        if (request.getPaymentFrequency() != null) farm.setPaymentFrequency(request.getPaymentFrequency());
        if (request.getSecurityDeposit() != null) {
            farm.setSecurityDeposit(request.getSecurityDeposit());
            if (farm.getAdvancePayment() == null || request.getAdvancePayment() != null) {
                farm.setAdvancePayment(request.getAdvancePayment() != null ? request.getAdvancePayment() : request.getSecurityDeposit());
            }
        } else if (request.getAdvancePayment() != null) {
            farm.setAdvancePayment(request.getAdvancePayment());
        }
        if (request.getElectricityCharges() != null) farm.setElectricityCharges(request.getElectricityCharges());
        if (request.getWaterCharges() != null) farm.setWaterCharges(request.getWaterCharges());
        if (request.getMaintenanceResponsibility() != null) farm.setMaintenanceResponsibility(request.getMaintenanceResponsibility());
        if (request.getCleaningResponsibility() != null) farm.setCleaningResponsibility(request.getCleaningResponsibility());
        if (request.getDowntimeTerms() != null) farm.setDowntimeTerms(request.getDowntimeTerms());
        if (request.getEquipmentIncluded() != null) farm.setEquipmentIncluded(request.getEquipmentIncluded());
        if (request.getNoticePeriod() != null) farm.setNoticePeriod(request.getNoticePeriod());
        if (request.getLatePaymentTerms() != null) farm.setLatePaymentTerms(request.getLatePaymentTerms());
        if (request.getOtherRentalConditions() != null) farm.setOtherRentalConditions(request.getOtherRentalConditions());
        if (request.getAvailability() != null) farm.setAvailability(request.getAvailability());
        if (request.getAvailableFromDate() != null) farm.setAvailableFromDate(request.getAvailableFromDate());
        if (request.getMinCommitment() != null) farm.setMinCommitment(request.getMinCommitment());
        if (request.getRenewalTerms() != null) farm.setRenewalTerms(request.getRenewalTerms());

        if (request.getFacilities() != null) farm.setFacilities(request.getFacilities());
        if (request.getConfirmedByOwner() != null) farm.setConfirmedByOwner(request.getConfirmedByOwner());
        if (request.getShedsDataJson() != null) farm.setShedsDataJson(request.getShedsDataJson());
        if (request.getShedDistancesJson() != null) farm.setShedDistancesJson(request.getShedDistancesJson());
        if (request.getIsNegotiable() != null) farm.setIsNegotiable(request.getIsNegotiable());
        if (request.getStatus() != null) farm.setStatus(request.getStatus());

        // Process images update if provided
        if (request.getImages() != null) {
            farm.getImages().clear();
            int order = 0;
            boolean hasPrimary = false;
            for (AddFarmImageRequest imgReq : request.getImages()) {
                if (imgReq.getImageUrl() != null && !imgReq.getImageUrl().isBlank()) {
                    boolean isPrim = Boolean.TRUE.equals(imgReq.getIsPrimary()) && !hasPrimary;
                    if (isPrim) hasPrimary = true;
                    FarmImage img = new FarmImage(
                            farm,
                            imgReq.getImageUrl(),
                            imgReq.getCategory(),
                            imgReq.getCaption() != null ? imgReq.getCaption() : imgReq.getCategory(),
                            isPrim,
                            imgReq.getDisplayOrder() != null ? imgReq.getDisplayOrder() : order++
                    );
                    farm.addImage(img);
                }
            }
            if (!hasPrimary && !farm.getImages().isEmpty()) {
                farm.getImages().get(0).setPrimary(true);
            }
        }

        Farm updatedFarm = farmRepository.save(farm);
        return FarmResponse.fromEntity(updatedFarm);
    }

    @Transactional
    public FarmResponse updateStatus(Long farmId, String userEmail, FarmStatus newStatus) {
        Farm farm = farmRepository.findById(farmId)
                .orElseThrow(() -> new ResourceNotFoundException("Farm not found with id: " + farmId));

        boolean isOwner = farm.getOwner() != null && farm.getOwner().getEmail().equalsIgnoreCase(userEmail);
        boolean isAdmin = employeeRepository.existsByEmailIgnoreCase(userEmail);
        if (!isOwner && !isAdmin) {
            throw new AccessDeniedException("You are not authorized to modify this farm listing");
        }

        farm.setStatus(newStatus);
        Farm updatedFarm = farmRepository.save(farm);

        // Generate status update notification for owner
        try {
            String statusText = newStatus == FarmStatus.RENTED ? "marked as Rented." : newStatus == FarmStatus.ACTIVE ? "available for rent." : "saved as draft.";
            notificationService.createNotification(
                    farm.getOwner(),
                    "Shed Status Updated",
                    "'" + farm.getFarmName() + "' is now " + statusText,
                    "alert",
                    "/my-sheds"
            );
        } catch (Exception ignored) {
        }

        return FarmResponse.fromEntity(updatedFarm);
    }

    @Transactional
    public void deleteFarm(Long farmId, String userEmail) {
        Farm farm = farmRepository.findById(farmId)
                .orElseThrow(() -> new ResourceNotFoundException("Farm not found with id: " + farmId));

        boolean isOwner = farm.getOwner() != null && farm.getOwner().getEmail().equalsIgnoreCase(userEmail);
        boolean isAdmin = employeeRepository.existsByEmailIgnoreCase(userEmail);
        if (!isOwner && !isAdmin) {
            throw new AccessDeniedException("You are not authorized to delete this farm listing");
        }

        favoriteRepository.deleteByFarmId(farmId);
        messageRepository.deleteByFarmId(farmId);
        farmRepository.delete(farm);
    }

    @Transactional
    public FarmResponse incrementViews(Long farmId) {
        Farm farm = farmRepository.findById(farmId)
                .orElseThrow(() -> new ResourceNotFoundException("Farm not found with id: " + farmId));

        int current = farm.getViewsCount() != null ? farm.getViewsCount() : 0;
        farm.setViewsCount(current + 1);
        Farm saved = farmRepository.save(farm);
        return FarmResponse.fromEntity(saved);
    }

    @Transactional
    public FarmResponse verifyFarm(Long farmId, boolean isVerified, String verifiedBy) {
        Farm farm = farmRepository.findById(farmId)
                .orElseThrow(() -> new ResourceNotFoundException("Farm not found with id: " + farmId));

        farm.setIsVerified(isVerified);
        if (isVerified) {
            farm.setVerifiedAt(java.time.LocalDateTime.now());
            farm.setVerifiedBy(verifiedBy != null && !verifiedBy.isBlank() ? verifiedBy : "Evabirds Platform Admin");
        } else {
            farm.setVerifiedAt(null);
            farm.setVerifiedBy(null);
        }

        Farm saved = farmRepository.save(farm);

        // Notify owner about verification
        try {
            String title = isVerified ? "Farm Verified by Admin" : "Farm Verification Status Changed";
            String msg = isVerified
                    ? "Congratulations! Your farm '" + farm.getFarmName() + "' has been approved and granted the official Verified Symbol."
                    : "The verification status for '" + farm.getFarmName() + "' has been updated.";
            notificationService.createNotification(farm.getOwner(), title, msg, "verified", "/my-sheds");
        } catch (Exception ignored) {
        }

        return FarmResponse.fromEntity(saved);
    }

    @Transactional
    public FarmResponse adminUpdateStatus(Long farmId, FarmStatus status) {
        Farm farm = farmRepository.findById(farmId)
                .orElseThrow(() -> new ResourceNotFoundException("Farm not found with id: " + farmId));

        farm.setStatus(status);
        if (status == FarmStatus.ACTIVE && !Boolean.TRUE.equals(farm.getIsVerified())) {
            farm.setIsVerified(true);
            farm.setVerifiedAt(java.time.LocalDateTime.now());
            farm.setVerifiedBy("Evabirds Platform Admin");
        }

        Farm saved = farmRepository.save(farm);

        // Notify farm owner about status update
        try {
            String title = status == FarmStatus.ACTIVE ? "Farm Approved & Published" : "Farm Status Updated by Admin";
            String msg = status == FarmStatus.ACTIVE
                    ? "Your poultry farm '" + farm.getFarmName() + "' has been approved by admin and is now active for seekers."
                    : "The status of your farm '" + farm.getFarmName() + "' has been changed to " + status + " by admin.";
            notificationService.createNotification(farm.getOwner(), title, msg, "verified", "/my-sheds");
        } catch (Exception e) {
            // best-effort
        }

        return FarmResponse.fromEntity(saved);
    }

    private void validatePaymentTerms(
            ListingType listingType,
            String pricingModel,
            Boolean isNegotiable,
            BigDecimal monthlyRent,
            BigDecimal rentPerBatch,
            BigDecimal ratePerBird,
            BigDecimal leaseAmount,
            Integer leaseDurationMonths
    ) {
        if (listingType == null) {
            throw new IllegalArgumentException("Listing type (RENT or LEASE) is required");
        }

        // If negotiable pricing model or isNegotiable is true, price is optional
        if ("NEGOTIABLE".equalsIgnoreCase(pricingModel) || Boolean.TRUE.equals(isNegotiable)) {
            return;
        }

        if (listingType == ListingType.RENT) {
            if ("PER_BATCH".equalsIgnoreCase(pricingModel)) {
                if ((rentPerBatch == null || rentPerBatch.compareTo(BigDecimal.ZERO) <= 0) &&
                    (monthlyRent == null || monthlyRent.compareTo(BigDecimal.ZERO) <= 0)) {
                    throw new IllegalArgumentException("Please specify the rent per batch or estimated rate");
                }
            } else if ("PER_BIRD".equalsIgnoreCase(pricingModel)) {
                if ((ratePerBird == null || ratePerBird.compareTo(BigDecimal.ZERO) <= 0) &&
                    (monthlyRent == null || monthlyRent.compareTo(BigDecimal.ZERO) <= 0) &&
                    (rentPerBatch == null || rentPerBatch.compareTo(BigDecimal.ZERO) <= 0)) {
                    throw new IllegalArgumentException("Please specify rate per bird or total amount");
                }
            } else {
                if (monthlyRent == null || monthlyRent.compareTo(BigDecimal.ZERO) <= 0) {
                    throw new IllegalArgumentException("Monthly rent must be specified and greater than 0 for RENT listings");
                }
            }
        } else if (listingType == ListingType.LEASE) {
            boolean hasLeaseAmount = leaseAmount != null && leaseAmount.compareTo(BigDecimal.ZERO) > 0;
            boolean hasMonthlyRent = monthlyRent != null && monthlyRent.compareTo(BigDecimal.ZERO) > 0;

            if (!hasLeaseAmount && !hasMonthlyRent) {
                throw new IllegalArgumentException("For LEASE listings, please provide either an upfront lease amount or periodic rent");
            }

            if (leaseDurationMonths == null || leaseDurationMonths <= 0) {
                throw new IllegalArgumentException("Lease duration in months must be provided and greater than 0 for LEASE listings");
            }
        }
    }
}
