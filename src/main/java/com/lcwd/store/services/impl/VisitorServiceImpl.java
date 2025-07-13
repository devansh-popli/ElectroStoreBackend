package com.lcwd.store.services.impl;

import com.lcwd.store.dtos.ImageResponse;
import com.lcwd.store.dtos.VisitorDto;
import com.lcwd.store.entities.Visitor;
import com.lcwd.store.exceptions.ResourceNotFoundException;
import com.lcwd.store.repositories.VisitorRepository;
import com.lcwd.store.services.FileService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class VisitorServiceImpl implements com.lcwd.store.services.VisitorService {
    @Autowired
    private VisitorRepository visitorRepository;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private FileService fileService;

    @Value("${document.image.path}")
    private String imageUploadPath;

    @Override
    public List<VisitorDto> getAllVisitors() {
        List<Visitor> visitors = visitorRepository.findAll();
        return visitors.stream()
                .map(visitor -> modelMapper.map(visitor, VisitorDto.class))
                .collect(Collectors.toList());
    }

    @Override
    public VisitorDto saveVisitor(VisitorDto visitorDto) {
        Visitor visitor = modelMapper.map(visitorDto, Visitor.class);
        Visitor savedVisitor = visitorRepository.save(visitor);
        return modelMapper.map(savedVisitor, VisitorDto.class);
    }

    @Override
    public VisitorDto getVisitorById(Long id) {
        Visitor visitor = visitorRepository.findById(id).orElseThrow(()-> new ResourceNotFoundException("Visitor not found"));
        return visitor != null ? modelMapper.map(visitor, VisitorDto.class) : null;
    }

    @Override
    public ImageResponse uploadDocumentImage(MultipartFile image, Long visitorId, String type) throws IOException {
        String imageName = fileService.uploadImage(image, imageUploadPath);
        ImageResponse response = ImageResponse.builder().success(true).status(HttpStatus.OK).imageName(imageName).message("Image Saved Successfully").build();
       VisitorDto visitor=getVisitorById(visitorId);
       if(type.equalsIgnoreCase("profile"))
       visitor.setPhoto(imageName);
       else{
       visitor.setAadharNumber(imageName);
       }
        VisitorDto employeeDto1 = saveVisitor( visitor);
        return response;
    }

    // Add more methods as needed
}
