package com.lcwd.store.repositories;

import com.lcwd.store.entities.VehicleEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface VehicleEntryRepository extends JpaRepository<VehicleEntry,Long> {

    @Query("SELECT MAX(f.gatePassNo) FROM VehicleEntry f")
    String findLastAccountCode();

    Optional<List<VehicleEntry>> findByVehicleNumber(String vehicleNumber);
}
