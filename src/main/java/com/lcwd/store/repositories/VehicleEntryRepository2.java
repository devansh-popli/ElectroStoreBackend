package com.lcwd.store.repositories;

import com.lcwd.store.entities.VehicleEntry2;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface VehicleEntryRepository2 extends JpaRepository<VehicleEntry2, Long> {
    Optional<List<VehicleEntry2>> findByVehicleNumber(String vehicleNumber);
}