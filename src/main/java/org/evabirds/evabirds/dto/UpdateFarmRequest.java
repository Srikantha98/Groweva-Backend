package org.evabirds.evabirds.dto;

import org.evabirds.evabirds.entity.EnvironmentType;
import org.evabirds.evabirds.entity.FarmStatus;
import org.evabirds.evabirds.entity.FarmType;
import org.evabirds.evabirds.entity.HousingSystem;
import org.evabirds.evabirds.entity.ListingType;

import java.math.BigDecimal;
import java.util.List;

public class UpdateFarmRequest {

    private String farmName;
    private String title;
    private String description;
    private String location;

    private String country;
    private String state;
    private String district;
    private String taluk;
    private String villageCity;
    private String locality;
    private String pincode;
    private String addressLandmark;

    private String listingScope;
    private ListingType listingType;
    private FarmType farmType;
    private HousingSystem housingSystem;
    private EnvironmentType environmentType;
    private Boolean hasCages;
    private Integer cageShedCount;
    private String cageDetails;

    private Double totalLandArea;
    private String landAreaUnit;

    private Integer capacityBirds;
    private Integer capacity;
    private Integer totalMaxCapacity;
    private Integer totalAvailableCapacity;
    private Integer shedCount;
    private Double shedAreaSqft;
    private Double totalBuiltUpAreaSqft;

    private String pricingModel;
    private String billingPeriod;
    private String enteredPricingType;
    private BigDecimal ratePerBird;
    private BigDecimal rentPerBatch;
    private Integer batchDurationDays;
    private Integer minBatches;
    private Integer applicableBirdCount;
    private String billingCountBasis;

    private BigDecimal monthlyRent;
    private BigDecimal price;
    private BigDecimal leaseAmount;
    private Integer leaseDurationMonths;
    private String paymentFrequency;
    private BigDecimal securityDeposit;
    private BigDecimal advancePayment;
    private String electricityCharges;
    private String waterCharges;
    private String maintenanceResponsibility;
    private String cleaningResponsibility;
    private String downtimeTerms;
    private String equipmentIncluded;
    private String noticePeriod;
    private String latePaymentTerms;
    private String otherRentalConditions;
    private String availability;
    private String availableFromDate;
    private String minCommitment;
    private String renewalTerms;

    private String facilities;
    private Boolean confirmedByOwner;
    private String shedsDataJson;
    private String shedDistancesJson;
    private Boolean isNegotiable;
    private List<AddFarmImageRequest> images;
    private String primaryImageUrl;

    private FarmStatus status;

    public UpdateFarmRequest() {
    }

    public String getFarmName() {
        return farmName != null ? farmName : title;
    }

    public void setFarmName(String farmName) {
        this.farmName = farmName;
    }

    public String getTitle() {
        return title != null ? title : farmName;
    }

    public void setTitle(String title) {
        this.title = title;
        if (this.farmName == null) {
            this.farmName = title;
        }
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
        return capacityBirds != null ? capacityBirds : capacity;
    }

    public void setCapacityBirds(Integer capacityBirds) {
        this.capacityBirds = capacityBirds;
    }

    public Integer getCapacity() {
        return capacity != null ? capacity : capacityBirds;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
        if (this.capacityBirds == null) {
            this.capacityBirds = capacity;
        }
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
        return monthlyRent != null ? monthlyRent : price;
    }

    public void setMonthlyRent(BigDecimal monthlyRent) {
        this.monthlyRent = monthlyRent;
    }

    public BigDecimal getPrice() {
        return price != null ? price : monthlyRent;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
        if (this.monthlyRent == null) {
            this.monthlyRent = price;
        }
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

    public Boolean getIsNegotiable() {
        return isNegotiable;
    }

    public void setIsNegotiable(Boolean isNegotiable) {
        this.isNegotiable = isNegotiable;
    }

    public List<AddFarmImageRequest> getImages() {
        return images;
    }

    public void setImages(List<AddFarmImageRequest> images) {
        this.images = images;
    }

    public String getPrimaryImageUrl() {
        return primaryImageUrl;
    }

    public void setPrimaryImageUrl(String primaryImageUrl) {
        this.primaryImageUrl = primaryImageUrl;
    }
}
