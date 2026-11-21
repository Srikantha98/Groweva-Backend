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
@Table(name = "shed_distances")
public class ShedDistance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "farm_id", nullable = false)
    @JsonIgnore
    private Farm farm;

    @Column(name = "first_shed_name", nullable = false)
    private String firstShedName;

    @Column(name = "second_shed_name", nullable = false)
    private String secondShedName;

    @Column(name = "distance_value")
    private Double distanceValue;

    @Column(name = "distance_unit")
    private String distanceUnit = "Feet";

    @Column(name = "measurement_reference")
    private String measurementReference = "Nearest exterior walls";

    public ShedDistance() {
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

    public String getFirstShedName() {
        return firstShedName;
    }

    public void setFirstShedName(String firstShedName) {
        this.firstShedName = firstShedName;
    }

    public String getSecondShedName() {
        return secondShedName;
    }

    public void setSecondShedName(String secondShedName) {
        this.secondShedName = secondShedName;
    }

    public Double getDistanceValue() {
        return distanceValue;
    }

    public void setDistanceValue(Double distanceValue) {
        this.distanceValue = distanceValue;
    }

    public String getDistanceUnit() {
        return distanceUnit;
    }

    public void setDistanceUnit(String distanceUnit) {
        this.distanceUnit = distanceUnit;
    }

    public String getMeasurementReference() {
        return measurementReference;
    }

    public void setMeasurementReference(String measurementReference) {
        this.measurementReference = measurementReference;
    }
}
