package com.lcwd.store.services;

import com.lcwd.store.dtos.ImageResponse;
import com.lcwd.store.dtos.VisitorDto;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface VisitorService {
    List<VisitorDto> getAllVisitors();

    VisitorDto saveVisitor(VisitorDto visitorDto);

    VisitorDto getVisitorById(Long id);

    ImageResponse uploadDocumentImage(MultipartFile image, Long visitorId, String type) throws IOException;
}
