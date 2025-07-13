package com.lcwd.store.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Date;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "status_history_jcb")
public class StatusHistoryJcb {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "status", nullable = false, length = 100)
    private String status;
    private String statusUpdatedBy;

    @Column(name = "description", length = 255)
    private String description;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "timestamp", nullable = false)
    private Date timestamp;
    @ManyToOne
    @JoinColumn(name = "vehicle_entry_id")
    private VehicleEntry vehicleEntry;

}
