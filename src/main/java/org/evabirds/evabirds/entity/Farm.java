package org.evabirds.evabirds.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "farms", indexes = {
        @Index(name = "idx_farm_status", columnList = "status"),
        @Index(name = "idx_farm_owner", columnList = "owner_id"),
        @Index(name = "idx_farm_listing_type", columnList = "listing_type"),
        @Index(name = "idx_farm_type", columnList = "farm_type"),
        @Index(name = "idx_farm_state_district", columnList = "state, district"),
        @Index(name = "idx_farm_created_at", columnList = "created_at")
})
public class Farm {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    @Column(name = "farm_name", nullable = false)
    private String farmName;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private String location;

    // Detailed Location Fields (STEP 1)
    @Column(name = "country")
    private String country = "India";

    @Column(name = "state")
    private String state;

    @Column(name = "district")
    private String district;

    @Column(name = "taluk")
    private String taluk;

    @Column(name = "village_city")
    private String villageCity;

    @Column(name = "locality")
    private String locality;

    @Column(name = "pincode")
    private String pincode;

    @Column(name = "address_landmark", columnDefinition = "TEXT")
    private String addressLandmark;

    // Listing Scope (Entire Farm, Individual Shed, Multiple Sheds)
    @Column(name = "listing_scope")
    private String listingScope;

    @Enumerated(EnumType.STRING)
    @Column(name = "listing_type", nullable = false)
    private ListingType listingType;

    @Enumerated(EnumType.STRING)
    @Column(name = "farm_type")
    private FarmType farmType;

    @Enumerated(EnumType.STRING)
    @Column(name = "housing_system")
    private HousingSystem housingSystem;

    @Enumerated(EnumType.STRING)
    @Column(name = "environment_type")
    private EnvironmentType environmentType;

    @Column(name = "has_cages")
    private Boolean hasCages;

    @Column(name = "cage_shed_count")
    private Integer cageShedCount;

    @Column(name = "cage_details", columnDefinition = "TEXT")
    private String cageDetails;

    // Land Area (STEP 2)
    @Column(name = "total_land_area")
    private Double totalLandArea;

    @Column(name = "land_area_unit")
    private String landAreaUnit;

    @Column(name = "capacity_birds", nullable = false)
    private Integer capacityBirds;

    @Column(name = "total_max_capacity")
    private Integer totalMaxCapacity;

    @Column(name = "total_available_capacity")
    private Integer totalAvailableCapacity;

    @Column(name = "shed_count")
    private Integer shedCount;

    @Column(name = "shed_area_sqft")
    private Double shedAreaSqft;

    @Column(name = "total_built_up_area_sqft")
    private Double totalBuiltUpAreaSqft;

    // Financial & Rental Terms (STEP 3)
    @Column(name = "pricing_model")
    private String pricingModel;

    @Column(name = "billing_period")
    private String billingPeriod;

    @Column(name = "entered_pricing_type")
    private String enteredPricingType;

    @Column(name = "rate_per_bird", precision = 12, scale = 2)
    private BigDecimal ratePerBird;

    @Column(name = "rent_per_batch", precision = 14, scale = 2)
    private BigDecimal rentPerBatch;

    @Column(name = "batch_duration_days")
    private Integer batchDurationDays;

    @Column(name = "min_batches")
    private Integer minBatches;

    @Column(name = "applicable_bird_count")
    private Integer applicableBirdCount;

    @Column(name = "billing_count_basis")
    private String billingCountBasis;

    @Column(name = "monthly_rent", precision = 14, scale = 2)
    private BigDecimal monthlyRent;

    @Column(name = "lease_amount", precision = 14, scale = 2)
    private BigDecimal leaseAmount;

    @Column(name = "lease_duration_months")
    private Integer leaseDurationMonths;

    @Column(name = "payment_frequency")
    private String paymentFrequency;

    @Column(name = "security_deposit", precision = 14, scale = 2)
    private BigDecimal securityDeposit;

    @Column(name = "advance_payment", precision = 14, scale = 2)
    private BigDecimal advancePayment;

    @Column(name = "electricity_charges")
    private String electricityCharges;

    @Column(name = "water_charges")
    private String waterCharges;

    @Column(name = "maintenance_responsibility")
    private String maintenanceResponsibility;

    @Column(name = "cleaning_responsibility")
    private String cleaningResponsibility;

    @Column(name = "downtime_terms")
    private String downtimeTerms;

    @Column(name = "equipment_included", columnDefinition = "TEXT")
    private String equipmentIncluded;

    @Column(name = "notice_period")
    private String noticePeriod;

    @Column(name = "late_payment_terms")
    private String latePaymentTerms;

    @Column(name = "other_rental_conditions", columnDefinition = "TEXT")
    private String otherRentalConditions;

    @Column(name = "availability")
    private String availability;

    @Column(name = "available_from_date")
    private String availableFromDate;

    @Column(name = "min_commitment")
    private String minCommitment;

    @Column(name = "renewal_terms", columnDefinition = "TEXT")
    private String renewalTerms;

    @Column(name = "is_negotiable")
    private Boolean isNegotiable = false;

    // Infrastructure & Facilities (STEP 4)
    @Column(columnDefinition = "TEXT")
    private String facilities;

    @Column(name = "confirmed_by_owner")
    private Boolean confirmedByOwner = false;

    // Structured JSON fallbacks for complete multi-shed & distance state preservation
    @Column(name = "sheds_data_json", columnDefinition = "TEXT")
    private String shedsDataJson;

    @Column(name = "shed_distances_json", columnDefinition = "TEXT")
    private String shedDistancesJson;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FarmStatus status = FarmStatus.ACTIVE;

    @Column(name = "is_verified")
    private Boolean isVerified = false;

    @Column(name = "verified_at")
    private LocalDateTime verifiedAt;

    @Column(name = "verified_by")
    private String verifiedBy;

    @Column(name = "views_count")
    private Integer viewsCount = 0;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "farm", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @OrderBy("displayOrder ASC, id ASC")
    private List<FarmImage> images = new ArrayList<>();

    @OneToMany(mappedBy = "farm", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<FarmShed> sheds = new ArrayList<>();

    @OneToMany(mappedBy = "farm", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<ShedDistance> distances = new ArrayList<>();

    public Farm() {
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        if (this.status == null) {
            this.status = FarmStatus.ACTIVE;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public String getFarmName() {
        return farmName;
    }

    public void setFarmName(String farmName) {
        this.farmName = farmName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getTaluk() {
        return taluk;
    }

    public void setTaluk(String taluk) {
        this.taluk = taluk;
    }

    public String getVillageCity() {
        return villageCity;
    }

    public void setVillageCity(String villageCity) {
        this.villageCity = villageCity;
    }

    public String getLocality() {
        return locality;
    }

    public void setLocality(String locality) {
        this.locality = locality;
    }

    public String getPincode() {
        return pincode;
    }

    public void setPincode(String pincode) {
        this.pincode = pincode;
    }

    public String getAddressLandmark() {
        return addressLandmark;
    }

    public void setAddressLandmark(String addressLandmark) {
        this.addressLandmark = addressLandmark;
    }

    public String getListingScope() {
        return listingScope;
    }

    public void setListingScope(String listingScope) {
        this.listingScope = listingScope;
    }

    public ListingType getListingType() {
        return listingType;
    }

    public void setListingType(ListingType listingType) {
        this.listingType = listingType;
    }

    public FarmType getFarmType() {
        return farmType;
    }

    public void setFarmType(FarmType farmType) {
        this.farmType = farmType;
    }

    public HousingSystem getHousingSystem() {
        return housingSystem;
    }

    public void setHousingSystem(HousingSystem housingSystem) {
        this.housingSystem = housingSystem;
    }

    public EnvironmentType getEnvironmentType() {
        return environmentType;
    }

    public void setEnvironmentType(EnvironmentType environmentType) {
        this.environmentType = environmentType;
    }

    public Boolean getHasCages() {
        return hasCages;
    }

    public void setHasCages(Boolean hasCages) {
        this.hasCages = hasCages;
    }

    public Integer getCageShedCount() {
        return cageShedCount;
    }

    public void setCageShedCount(Integer cageShedCount) {
        this.cageShedCount = cageShedCount;
    }

    public String getCageDetails() {
        return cageDetails;
    }

    public void setCageDetails(String cageDetails) {
        this.cageDetails = cageDetails;
    }

    public Double getTotalLandArea() {
        return totalLandArea;
    }

    public void setTotalLandArea(Double totalLandArea) {
        this.totalLandArea = totalLandArea;
    }

    public String getLandAreaUnit() {
        return landAreaUnit;
    }

    public void setLandAreaUnit(String landAreaUnit) {
        this.landAreaUnit = landAreaUnit;
    }

    public Integer getCapacityBirds() {
        return capacityBirds;
    }

    public void setCapacityBirds(Integer capacityBirds) {
        this.capacityBirds = capacityBirds;
    }

    public Integer getTotalMaxCapacity() {
        return totalMaxCapacity;
    }

    public void setTotalMaxCapacity(Integer totalMaxCapacity) {
        this.totalMaxCapacity = totalMaxCapacity;
    }

    public Integer getTotalAvailableCapacity() {
        return totalAvailableCapacity;
    }

    public void setTotalAvailableCapacity(Integer totalAvailableCapacity) {
        this.totalAvailableCapacity = totalAvailableCapacity;
    }

    public Integer getShedCount() {
        return shedCount;
    }

    public void setShedCount(Integer shedCount) {
        this.shedCount = shedCount;
    }

    public Double getShedAreaSqft() {
        return shedAreaSqft;
    }

    public void setShedAreaSqft(Double shedAreaSqft) {
        this.shedAreaSqft = shedAreaSqft;
    }

    public Double getTotalBuiltUpAreaSqft() {
        return totalBuiltUpAreaSqft;
    }

    public void setTotalBuiltUpAreaSqft(Double totalBuiltUpAreaSqft) {
        this.totalBuiltUpAreaSqft = totalBuiltUpAreaSqft;
    }

    public String getPricingModel() {
        return pricingModel;
    }

    public void setPricingModel(String pricingModel) {
        this.pricingModel = pricingModel;
    }

    public String getBillingPeriod() {
        return billingPeriod;
    }

    public void setBillingPeriod(String billingPeriod) {
        this.billingPeriod = billingPeriod;
    }

    public String getEnteredPricingType() {
        return enteredPricingType;
    }

    public void setEnteredPricingType(String enteredPricingType) {
        this.enteredPricingType = enteredPricingType;
    }

    public BigDecimal getRatePerBird() {
        return ratePerBird;
    }

    public void setRatePerBird(BigDecimal ratePerBird) {
        this.ratePerBird = ratePerBird;
    }

    public BigDecimal getRentPerBatch() {
        return rentPerBatch;
    }

    public void setRentPerBatch(BigDecimal rentPerBatch) {
        this.rentPerBatch = rentPerBatch;
    }

    public Integer getBatchDurationDays() {
        return batchDurationDays;
    }

    public void setBatchDurationDays(Integer batchDurationDays) {
        this.batchDurationDays = batchDurationDays;
    }

    public Integer getMinBatches() {
        return minBatches;
    }

    public void setMinBatches(Integer minBatches) {
        this.minBatches = minBatches;
    }

    public Integer getApplicableBirdCount() {
        return applicableBirdCount;
    }

    public void setApplicableBirdCount(Integer applicableBirdCount) {
        this.applicableBirdCount = applicableBirdCount;
    }

    public String getBillingCountBasis() {
        return billingCountBasis;
    }

    public void setBillingCountBasis(String billingCountBasis) {
        this.billingCountBasis = billingCountBasis;
    }

    public BigDecimal getMonthlyRent() {
        return monthlyRent;
    }

    public void setMonthlyRent(BigDecimal monthlyRent) {
        this.monthlyRent = monthlyRent;
    }

    public BigDecimal getLeaseAmount() {
        return leaseAmount;
    }

    public void setLeaseAmount(BigDecimal leaseAmount) {
        this.leaseAmount = leaseAmount;
    }

    public Integer getLeaseDurationMonths() {
        return leaseDurationMonths;
    }

    public void setLeaseDurationMonths(Integer leaseDurationMonths) {
        this.leaseDurationMonths = leaseDurationMonths;
    }

    public String getPaymentFrequency() {
        return paymentFrequency;
    }

    public void setPaymentFrequency(String paymentFrequency) {
        this.paymentFrequency = paymentFrequency;
    }

    public BigDecimal getSecurityDeposit() {
        return securityDeposit;
    }

    public void setSecurityDeposit(BigDecimal securityDeposit) {
        this.securityDeposit = securityDeposit;
    }

    public BigDecimal getAdvancePayment() {
        return advancePayment;
    }

    public void setAdvancePayment(BigDecimal advancePayment) {
        this.advancePayment = advancePayment;
    }

    public String getElectricityCharges() {
        return electricityCharges;
    }

    public void setElectricityCharges(String electricityCharges) {
        this.electricityCharges = electricityCharges;
    }

    public String getWaterCharges() {
        return waterCharges;
    }

    public void setWaterCharges(String waterCharges) {
        this.waterCharges = waterCharges;
    }

    public String getMaintenanceResponsibility() {
        return maintenanceResponsibility;
    }

    public void setMaintenanceResponsibility(String maintenanceResponsibility) {
        this.maintenanceResponsibility = maintenanceResponsibility;
    }

    public String getCleaningResponsibility() {
        return cleaningResponsibility;
    }

    public void setCleaningResponsibility(String cleaningResponsibility) {
        this.cleaningResponsibility = cleaningResponsibility;
    }

    public String getDowntimeTerms() {
        return downtimeTerms;
    }

    public void setDowntimeTerms(String downtimeTerms) {
        this.downtimeTerms = downtimeTerms;
    }

    public String getEquipmentIncluded() {
        return equipmentIncluded;
    }

    public void setEquipmentIncluded(String equipmentIncluded) {
        this.equipmentIncluded = equipmentIncluded;
    }

    public String getNoticePeriod() {
        return noticePeriod;
    }

    public void setNoticePeriod(String noticePeriod) {
        this.noticePeriod = noticePeriod;
    }

    public String getLatePaymentTerms() {
        return latePaymentTerms;
    }

    public void setLatePaymentTerms(String latePaymentTerms) {
        this.latePaymentTerms = latePaymentTerms;
    }

    public String getOtherRentalConditions() {
        return otherRentalConditions;
    }

    public void setOtherRentalConditions(String otherRentalConditions) {
        this.otherRentalConditions = otherRentalConditions;
    }

    public String getAvailability() {
        return availability;
    }

    public void setAvailability(String availability) {
        this.availability = availability;
    }

    public String getAvailableFromDate() {
        return availableFromDate;
    }

    public void setAvailableFromDate(String availableFromDate) {
        this.availableFromDate = availableFromDate;
    }

    public String getMinCommitment() {
        return minCommitment;
    }

    public void setMinCommitment(String minCommitment) {
        this.minCommitment = minCommitment;
    }

    public String getRenewalTerms() {
        return renewalTerms;
    }

    public void setRenewalTerms(String renewalTerms) {
        this.renewalTerms = renewalTerms;
    }

    public Boolean getIsNegotiable() {
        return isNegotiable;
    }

    public void setIsNegotiable(Boolean isNegotiable) {
        this.isNegotiable = isNegotiable != null ? isNegotiable : false;
    }

    public String getFacilities() {
        return facilities;
    }

    public void setFacilities(String facilities) {
        this.facilities = facilities;
    }

    public Boolean getConfirmedByOwner() {
        return confirmedByOwner;
    }

    public void setConfirmedByOwner(Boolean confirmedByOwner) {
        this.confirmedByOwner = confirmedByOwner;
    }

    public String getShedsDataJson() {
        return shedsDataJson;
    }

    public void setShedsDataJson(String shedsDataJson) {
        this.shedsDataJson = shedsDataJson;
    }

    public String getShedDistancesJson() {
        return shedDistancesJson;
    }

    public void setShedDistancesJson(String shedDistancesJson) {
        this.shedDistancesJson = shedDistancesJson;
    }

    public FarmStatus getStatus() {
        return status;
    }

    public void setStatus(FarmStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public List<FarmImage> getImages() {
        return images;
    }

    public void setImages(List<FarmImage> images) {
        this.images = images;
    }

    public void addImage(FarmImage image) {
        images.add(image);
        image.setFarm(this);
    }

    public void removeImage(FarmImage image) {
        images.remove(image);
        image.setFarm(null);
    }

    public List<FarmShed> getSheds() {
        return sheds;
    }

    public void setSheds(List<FarmShed> sheds) {
        this.sheds = sheds;
    }

    public void addShed(FarmShed shed) {
        sheds.add(shed);
        shed.setFarm(this);
    }

    public List<ShedDistance> getDistances() {
        return distances;
    }

    public void setDistances(List<ShedDistance> distances) {
        this.distances = distances;
    }

    public void addDistance(ShedDistance distance) {
        distances.add(distance);
        distance.setFarm(this);
    }

    public Boolean getIsVerified() {
        return isVerified != null ? isVerified : false;
    }

    public void setIsVerified(Boolean isVerified) {
        this.isVerified = isVerified;
    }

    public LocalDateTime getVerifiedAt() {
        return verifiedAt;
    }

    public void setVerifiedAt(LocalDateTime verifiedAt) {
        this.verifiedAt = verifiedAt;
    }

    public String getVerifiedBy() {
        return verifiedBy;
    }

    public void setVerifiedBy(String verifiedBy) {
        this.verifiedBy = verifiedBy;
    }

    public Integer getViewsCount() {
        return viewsCount != null ? viewsCount : 0;
    }

    public void setViewsCount(Integer viewsCount) {
        this.viewsCount = viewsCount;
    }
}
