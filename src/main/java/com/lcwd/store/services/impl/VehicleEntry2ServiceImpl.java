package com.lcwd.store.services.impl;

import com.lcwd.store.dtos.ImageResponse;
import com.lcwd.store.dtos.VehicleEntry2Dto;
import com.lcwd.store.dtos.VehicleEntryDto;
import com.lcwd.store.entities.StatusHistory;
import com.lcwd.store.entities.VehicleEntry2;
import com.lcwd.store.exceptions.ResourceNotFoundException;
import com.lcwd.store.repositories.VehicleEntryRepository2;
import com.lcwd.store.services.FileService;
import com.lcwd.store.services.VehicleEntry2Service;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.ui.ModelMap;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class VehicleEntry2ServiceImpl implements VehicleEntry2Service {

    private final VehicleEntryRepository2 repository;
    private final ModelMapper modelMapper;
    @Autowired
    private FileService fileService;
    @Value("${vehicle.image.path}")
    private String imageUploadPath;

    @Autowired
    public VehicleEntry2ServiceImpl(VehicleEntryRepository2 repository, ModelMapper modelMapper) {
        this.repository = repository;
        this.modelMapper = modelMapper;
    }

    @Override
    public VehicleEntry2Dto save(VehicleEntry2Dto vehicleEntryDTO) {
        VehicleEntry2 vehicleEntry = modelMapper.map(vehicleEntryDTO, VehicleEntry2.class);
        vehicleEntry.setStatusHistories(new ArrayList<>());
        if(vehicleEntry.getStatus()!=null){
            VehicleEntry2 vehicleEntry2= repository.findById(vehicleEntry.getId()).orElseThrow(()->new ResourceNotFoundException("Resource not found "));
            StatusHistory statusHistory=new StatusHistory();
            statusHistory.setVehicleEntry(vehicleEntry);
            statusHistory.setStatus(vehicleEntry.getStatus());
            statusHistory.setStatusUpdatedBy(vehicleEntry.getStatusUpdatedBy());
            statusHistory.setTimestamp(new Date());
            if(vehicleEntry.getRemarks()!=null)
            {
                statusHistory.setDescription(vehicleEntry.getRemarks());
            }
            vehicleEntry2.getStatusHistories().add(statusHistory);
            vehicleEntry.setStatusHistories(vehicleEntry2.getStatusHistories());
        }
        return modelMapper.map(repository.save(vehicleEntry),VehicleEntry2Dto.class);
    }

    @Override
    public VehicleEntry2Dto getById(Long id) {
        return modelMapper.map(repository.findById(id).orElse(null),VehicleEntry2Dto.class);
    }

    @Override
    public List<VehicleEntry2Dto> getAll() {
        return repository.findAll().stream().map(VehicleEntry2::toDTO).collect(Collectors.toList());
    }

    @Override
    public ImageResponse uploadDocumentImage(MultipartFile image, Long vehicleEntryId, String type) throws IOException {
        String imageName = fileService.uploadImage(image, imageUploadPath);
        ImageResponse response = ImageResponse.builder().success(true).status(HttpStatus.OK).imageName(imageName).message("Image Saved Successfully").build();
        VehicleEntry2 VehicleEntry=modelMapper.map(getById(vehicleEntryId),VehicleEntry2.class);
        if(type.equalsIgnoreCase("vehicle"))
            VehicleEntry.getVehicleDocument().add(imageName);
        else if(type.equalsIgnoreCase("driver"))
            VehicleEntry.getDriverDocument().add(imageName);
        else if(type.equalsIgnoreCase("vehicleImage"))
            VehicleEntry.getVehicleImages().add(imageName);
        VehicleEntry2Dto employeeDto1 = modelMapper.map(save( modelMapper.map(VehicleEntry,VehicleEntry2Dto.class)),VehicleEntry2Dto.class);
        return response;
    }

    @Override
    public VehicleEntry2Dto getByVehicleNumber(String vehicleNumber) {
        List<VehicleEntry2> vehicleEntry2List = repository.findByVehicleNumber(vehicleNumber).orElseThrow(()->new ResourceNotFoundException("Resource Not Found"));
        vehicleEntry2List.sort(Comparator.comparing(VehicleEntry2::getDateOfEntry).reversed());
    if(vehicleEntry2List.size()>0)
        return modelMapper.map(vehicleEntry2List.get(0),VehicleEntry2Dto.class);
    return new VehicleEntry2Dto();
    }
}

