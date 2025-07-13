package com.lcwd.store.controller;

import com.lcwd.store.dtos.ImageResponse;
import com.lcwd.store.dtos.VisitorDto;
import com.lcwd.store.services.FileService;
import com.lcwd.store.services.VisitorService;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping({"/api/visitors"})
public class VisitorController {
   private static final Logger log = LoggerFactory.getLogger(VisitorController.class);
   @Autowired
   private VisitorService visitorService;
   @Autowired
   private FileService fileService;
   @Value("${document.image.path}")
   private String imageUploadPath;

   @Autowired
   public VisitorController(VisitorService visitorService) {
      this.visitorService = visitorService;
   }

   @GetMapping
   public List<VisitorDto> getAllVisitors() {
      return this.visitorService.getAllVisitors();
   }

   @PostMapping
   public VisitorDto saveVisitor(@RequestBody VisitorDto visitorDto) {
      return this.visitorService.saveVisitor(visitorDto);
   }

   @GetMapping({"/{id}"})
   public VisitorDto getVisitorById(@PathVariable Long id) {
      return this.visitorService.getVisitorById(id);
   }

   @PostMapping({"/upload/image/{visitorId}/{type}"})
   public ResponseEntity<ImageResponse> uploadDocumentImage(@PathVariable String type, @RequestParam("visitorProfileImage") MultipartFile image, @PathVariable Long visitorId) throws IOException {
      ImageResponse response = this.visitorService.uploadDocumentImage(image, visitorId, type);
      return ResponseEntity.ok(response);
   }

   @PreAuthorize("permitAll()")
   @GetMapping({"/image/{visitorId}/{type}"})
   public void getProductImage(@PathVariable String type, @PathVariable Long visitorId, HttpServletResponse response) throws IOException {
      VisitorDto visitorDto = this.visitorService.getVisitorById(visitorId);
      log.info("Visitor Image Get {}", visitorId);
      String imageName = "";
      if (type.equalsIgnoreCase("profile")) {
         imageName = visitorDto.getPhoto();
      } else {
         imageName = visitorDto.getAadharNumber();
      }

      InputStream resource = this.fileService.getResouce(this.imageUploadPath, imageName);
      response.setContentType("image/jpeg");
      StreamUtils.copy(resource, response.getOutputStream());
   }
}
