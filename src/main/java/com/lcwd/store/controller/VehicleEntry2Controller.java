package com.lcwd.store.controller;

import com.lcwd.store.dtos.ImageResponse;
import com.lcwd.store.dtos.VehicleEntry2Dto;
import com.lcwd.store.dtos.VehicleEntryDto;
import com.lcwd.store.entities.VehicleEntry2;
import com.lcwd.store.services.FileService;
import com.lcwd.store.services.VehicleEntry2Service;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v2/vehicle-entries")
@Slf4j
public class VehicleEntry2Controller {

    private final VehicleEntry2Service vehicleEntryService;
    @Autowired
    private FileService fileService;
    @Value("${vehicle.image.path}")
    private String imageUploadPath;
    @Autowired
    public VehicleEntry2Controller(VehicleEntry2Service vehicleEntryService) {
        this.vehicleEntryService = vehicleEntryService;
    }

    @PostMapping
    public ResponseEntity<VehicleEntry2Dto> createVehicleEntry(@RequestBody VehicleEntry2Dto vehicleEntryDTO) {
        VehicleEntry2Dto savedEntry = vehicleEntryService.save(vehicleEntryDTO);
        return new ResponseEntity<>(savedEntry, HttpStatus.CREATED);
    }

    @GetMapping("/roleName/{id}")
    public ResponseEntity<VehicleEntry2Dto> getVehicleEntryById(@PathVariable Long id) {
        VehicleEntry2Dto entry = vehicleEntryService.getById(id);
        return entry != null ?
                new ResponseEntity<>(entry, HttpStatus.OK) :
                new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
    @GetMapping("/vehicleNumber/{vehicleNumber}")
    public ResponseEntity<VehicleEntry2Dto> getVehicleEntryById(@PathVariable String vehicleNumber) {
        VehicleEntry2Dto entry = vehicleEntryService.getByVehicleNumber(vehicleNumber);
        return entry != null ?
                new ResponseEntity<>(entry, HttpStatus.OK) :
                new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
    @GetMapping
    public ResponseEntity<List<VehicleEntry2Dto>> getAllVehicleEntries() {
        List<VehicleEntry2Dto> entries = vehicleEntryService.getAll();
        return new ResponseEntity<>(entries, HttpStatus.OK);
    }
    @PostMapping("/upload/image/{vehicleEntryId}/{type}")
    public ResponseEntity<ImageResponse> uploadDocumentImage(@PathVariable String type,@RequestParam("vehicleProfileImage") MultipartFile image, @PathVariable Long vehicleEntryId) throws IOException {

        ImageResponse response = vehicleEntryService.uploadDocumentImage(image,vehicleEntryId,type);
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("permitAll()")
    @GetMapping("/image/{imageName}")
    public void getProductImage(@PathVariable String imageName , HttpServletResponse response) throws IOException {
//        VehicleEntry2 visitorDto = vehicleEntryService.getById(entryId);
        log.info("VehicleEntry2 Image Get {}",imageName);
//        List<String> vehicleDocumentImageNames= visitorDto.getVehicleDocument();
//        List<String> driverDocumentImageNames= visitorDto.getDriverDocument();
        InputStream resource = fileService.getResouce(imageUploadPath, imageName);
        response.setContentType(MediaType.IMAGE_JPEG_VALUE);
        //to remember
        StreamUtils.copy(resource, response.getOutputStream());

    }
}

