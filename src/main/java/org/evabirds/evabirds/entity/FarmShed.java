package org.evabirds.evabirds.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "farm_sheds")
public class FarmShed {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "farm_id", nullable = false)
    @JsonIgnore
    private Farm farm;

    @Column(name = "shed_name", nullable = false)
    private String shedName;

    @Column(name = "bird_category")
    private String birdCategory;

    @Column(name = "housing_system")
    private String housingSystem;

    @Column(name = "environment_control")
    private String environmentControl;

    @Column(name = "length_ft")
    private Double lengthFt;

    @Column(name = "width_ft")
    private Double widthFt;

    @Column(name = "shed_area_sqft")
    private Double shedAreaSqft;

    @Column(name = "max_capacity")
    private Integer maxCapacity;

    @Column(name = "available_capacity")
    private Integer availableCapacity;

    @Column(name = "operational_status")
    private String operationalStatus = "Operational";

    public FarmShed() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Farm getFarm() {
        return farm;
    }

    public void setFarm(Farm farm) {
        this.farm = farm;
    }

    public String getShedName() {
        return shedName;
    }

    public void setShedName(String shedName) {
        this.shedName = shedName;
    }

    public String getBirdCategory() {
        return birdCategory;
    }

    public void setBirdCategory(String birdCategory) {
        this.birdCategory = birdCategory;
    }

    public String getHousingSystem() {
        return housingSystem;
    }

    public void setHousingSystem(String housingSystem) {
        this.housingSystem = housingSystem;
    }

    public String getEnvironmentControl() {
        return environmentControl;
    }

    public void setEnvironmentControl(String environmentControl) {
        this.environmentControl = environmentControl;
    }

    public Double getLengthFt() {
        return lengthFt;
    }

    public void setLengthFt(Double lengthFt) {
        this.lengthFt = lengthFt;
    }

    public Double getWidthFt() {
        return widthFt;
    }

    public void setWidthFt(Double widthFt) {
        this.widthFt = widthFt;
    }

    public Double getShedAreaSqft() {
        return shedAreaSqft;
    }

    public void setShedAreaSqft(Double shedAreaSqft) {
        this.shedAreaSqft = shedAreaSqft;
    }

    public Integer getMaxCapacity() {
        return maxCapacity;
    }

    public void setMaxCapacity(Integer maxCapacity) {
        this.maxCapacity = maxCapacity;
    }

    public Integer getAvailableCapacity() {
        return availableCapacity;
    }

    public void setAvailableCapacity(Integer availableCapacity) {
        this.availableCapacity = availableCapacity;
    }

    public String getOperationalStatus() {
        return operationalStatus;
    }

    public void setOperationalStatus(String operationalStatus) {
        this.operationalStatus = operationalStatus;
    }
}
