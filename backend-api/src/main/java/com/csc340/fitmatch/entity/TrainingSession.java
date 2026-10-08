package com.csc340.fitmatch.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "training_sessions")
public class TrainingSession {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Size(max = 2000)
    @Column(length = 2000)
    private String notes;

    @NotBlank
    @Size(max = 30)
    @Column(nullable = false, length = 30)
    private String status;

    @Size(max = 50)
    @Column(length = 50)
    private String level;

    @Size(max = 255)
    @Column(length = 255)
    private String location;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "training_service_id", nullable = false)
    private TrainingService trainingService;

    @OneToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "timeslot_id", nullable = true, unique = true)
    private Timeslot timeslot;

    @OneToOne(mappedBy = "trainingSession", fetch = FetchType.LAZY)
    private Review review;

    public TrainingSession() {
    }

    public Long getId() {
        return id;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public TrainingService getTrainingService() {
        return trainingService;
    }

    public void setTrainingService(TrainingService trainingService) {
        this.trainingService = trainingService;
    }

    public Timeslot getTimeslot() {
        return timeslot;
    }

    public void setTimeslot(Timeslot timeslot) {
        if (this.timeslot != null && this.timeslot.getTrainingSession() == this) {
            this.timeslot.setTrainingSession(null);
        }
        this.timeslot = timeslot;
        if (timeslot != null && timeslot.getTrainingSession() != this) {
            timeslot.setTrainingSession(this);
        }
    }

    public Review getReview() {
        return review;
    }

    public void setReview(Review review) {
        this.review = review;
    }
}
