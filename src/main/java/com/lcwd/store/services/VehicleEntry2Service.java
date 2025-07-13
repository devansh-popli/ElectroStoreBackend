package com.lcwd.store.services;

import com.lcwd.store.dtos.ImageResponse;
import com.lcwd.store.dtos.VehicleEntry2Dto;
import com.lcwd.store.entities.VehicleEntry2;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface VehicleEntry2Service {

    VehicleEntry2Dto save(VehicleEntry2Dto vehicleEntryDTO);

    VehicleEntry2Dto getById(Long id);

    List<VehicleEntry2Dto> getAll();

    ImageResponse uploadDocumentImage(MultipartFile image, Long vehicleEntryId, String type) throws IOException;

    VehicleEntry2Dto getByVehicleNumber(String vehicleNumber);
}
