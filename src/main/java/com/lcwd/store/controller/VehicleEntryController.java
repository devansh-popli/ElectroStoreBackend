package com.lcwd.store.controller;

import com.lcwd.store.dtos.ImageResponse;
import com.lcwd.store.dtos.VehicleEntry2Dto;
import com.lcwd.store.dtos.VehicleEntryDto;
import com.lcwd.store.dtos.VisitorDto;
import com.lcwd.store.services.FileService;
import com.lcwd.store.services.VehicleEntryService;
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
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/vehicle-entries")
@Slf4j
public class VehicleEntryController {

    private final VehicleEntryService entryService;
    @Autowired
    private FileService fileService;
    @Value("${vehicle.image.path}")
    private String imageUploadPath;

    @GetMapping("/lastAccountCode")
    public String getLastAccountCode() {
        String lastAccountCode = entryService.getLastAccountCode();
        return lastAccountCode;
    }
    @Autowired
    public VehicleEntryController(VehicleEntryService entryService) {
        this.entryService = entryService;
    }

    @GetMapping
    public List<VehicleEntryDto> getAllEntries() {
        return entryService.getAllEntries();
    }

    @GetMapping("/{id}")
    public ResponseEntity<VehicleEntryDto> getEntryById(@PathVariable Long id) {
        Optional<VehicleEntryDto> entry = entryService.getEntryById(id);
        return entry.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<VehicleEntryDto> saveEntry(@RequestBody VehicleEntryDto entryDTO) {
        VehicleEntryDto savedEntry = entryService.saveEntry(entryDTO);
        return ResponseEntity.ok(savedEntry);
    }
    @GetMapping("/vehicleNumber/{vehicleNumber}")
    public ResponseEntity<VehicleEntryDto> getVehicleEntryById(@PathVariable String vehicleNumber) {
        VehicleEntryDto entry = entryService.getByVehicleNumber(vehicleNumber);
        return entry != null ?
                new ResponseEntity<>(entry, HttpStatus.OK) :
                new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
    @PostMapping("/upload/image/{vehicleEntryId}")
    public ResponseEntity<ImageResponse> uploadDocumentImage(@RequestParam("vehicleProfileImage") MultipartFile image, @PathVariable Long vehicleEntryId) throws IOException {

        ImageResponse response = entryService.uploadDocumentImage(image,vehicleEntryId);
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("permitAll()")
    @GetMapping("/image/{entryId}")
    public void getProductImage(@PathVariable Long entryId , HttpServletResponse response) throws IOException {
        Optional<VehicleEntryDto> visitorDto = entryService.getEntryById(entryId);
        log.info("Visitor Image Get {}",entryId);
        String imageName="";
        imageName= visitorDto.get().getPhotoUrl();
        InputStream resource = fileService.getResouce(imageUploadPath, imageName);
        response.setContentType(MediaType.IMAGE_JPEG_VALUE);
        //to remember
        StreamUtils.copy(resource, response.getOutputStream());

    }
}
