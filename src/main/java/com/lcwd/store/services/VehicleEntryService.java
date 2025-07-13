package com.lcwd.store.services;

import com.lcwd.store.dtos.ImageResponse;
import com.lcwd.store.dtos.VehicleEntryDto;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public interface VehicleEntryService {
    List<VehicleEntryDto> getAllEntries();

    Optional<VehicleEntryDto> getEntryById(Long id);

    VehicleEntryDto saveEntry(VehicleEntryDto entryDTO);

    String getLastAccountCode();

    ImageResponse uploadDocumentImage(MultipartFile image, Long vehicleEntryId) throws IOException;

    VehicleEntryDto getByVehicleNumber(String vehicleNumber);
}
