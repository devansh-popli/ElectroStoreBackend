package com.lcwd.store.services.impl;

import com.lcwd.store.dtos.ImageResponse;
import com.lcwd.store.dtos.VehicleEntry2Dto;
import com.lcwd.store.dtos.VehicleEntryDto;
import com.lcwd.store.dtos.VehicleEntryDto;
import com.lcwd.store.entities.StatusHistory;
import com.lcwd.store.entities.StatusHistoryJcb;
import com.lcwd.store.entities.VehicleEntry;
import com.lcwd.store.entities.VehicleEntry2;
import com.lcwd.store.exceptions.ResourceNotFoundException;
import com.lcwd.store.repositories.VehicleEntryRepository;
import com.lcwd.store.services.FileService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class VehicleEntryServiceImpl implements com.lcwd.store.services.VehicleEntryService {
    @Autowired
    private VehicleEntryRepository repository;

    @Autowired
    private FileService fileService;
    @Value("${vehicle.image.path}")
    private String imageUploadPath;

    @Override
    public List<VehicleEntryDto> getAllEntries() {
        List<VehicleEntry> entries = repository.findAll();

        return entries.stream().map(VehicleEntry::toDTO).collect(Collectors.toList());
    }

    @Override
    public Optional<VehicleEntryDto> getEntryById(Long id) {
        Optional<VehicleEntry> entry = repository.findById(id);
        return entry.map(VehicleEntry::toDTO);
    }

    @Override
    public VehicleEntryDto saveEntry(VehicleEntryDto entryDTO) {
        ModelMapper modelMapper = new ModelMapper();
        VehicleEntry vehicleEntry = modelMapper.map(entryDTO, VehicleEntry.class);
        vehicleEntry.setStatusHistories(new ArrayList<>());
        if(vehicleEntry.getStatus()!=null){
            VehicleEntry vehicleEntry2= repository.findById(vehicleEntry.getId()).orElseThrow(()->new ResourceNotFoundException("Resource not found "));
            StatusHistoryJcb statusHistory=new StatusHistoryJcb();
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
        VehicleEntry savedEntry = repository.save(vehicleEntry);
        return savedEntry.toDTO();
    }

    @Override
    public String getLastAccountCode() {
        String lastAccountCode = repository.findLastAccountCode();
        return lastAccountCode != null ? lastAccountCode : "abc-000";
    }

    @Override
    public ImageResponse uploadDocumentImage(MultipartFile image, Long vehicleEntryId) throws IOException {
        String imageName = fileService.uploadImage(image, imageUploadPath);
        ImageResponse response = ImageResponse.builder().success(true).status(HttpStatus.OK).imageName(imageName).message("Image Saved Successfully").build();
        VehicleEntryDto VehicleEntry=getEntryById(vehicleEntryId).orElseThrow(()->new ResourceNotFoundException("Vehicle Entry Not Found"));
        VehicleEntry.setPhotoUrl(imageName);
        VehicleEntryDto employeeDto1 = saveEntry( VehicleEntry);
        return response;
    
    }

    @Override
    public VehicleEntryDto getByVehicleNumber(String vehicleNumber) {
        List<VehicleEntry> vehicleEntryList = repository.findByVehicleNumber(vehicleNumber).orElseThrow(()->new ResourceNotFoundException("Resource Not Found"));
        vehicleEntryList.sort(Comparator.comparing(VehicleEntry::getInDate).reversed());
        ModelMapper modelMapper = new ModelMapper();
        if(vehicleEntryList.size()>0)
            return modelMapper.map(vehicleEntryList.get(0), VehicleEntryDto.class);
        return new VehicleEntryDto();
    }
}
