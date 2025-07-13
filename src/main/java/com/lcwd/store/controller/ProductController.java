package com.lcwd.store.controller;

import com.lcwd.store.dtos.*;
import com.lcwd.store.services.FileService;
import com.lcwd.store.services.ProductService;
import com.lcwd.store.services.impl.ProductServiceImpl;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/products")
public class ProductController {
    @Autowired
    ProductService productService;
    private Logger log = LoggerFactory.getLogger(ProductController.class);
    @Autowired
    private FileService fileService;

    @Value("${product.image.path}")
    private String imageUploadPath;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping()
    public ResponseEntity<ProductDto> createProduct(@Valid @RequestBody ProductDto ProductDto) {
        ProductDto Product = productService.createProduct(ProductDto);
        return new ResponseEntity<>(Product, HttpStatus.CREATED);
    }
    //update

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{ProductId}")
    public ResponseEntity<ProductDto> updateProduct(@Valid @RequestBody ProductDto ProductDto, @PathVariable("ProductId") String ProductId) {
        ProductDto updatedProductDto = productService.updateProduct(ProductDto, ProductId);
        return new ResponseEntity<>(updatedProductDto, HttpStatus.OK);
    }

    //delete
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{ProductId}")
    public ResponseEntity<ApiResponseMessage> deleteProduct(@PathVariable("ProductId") String ProductId) {
        productService.deleteProduct(ProductId);
        ApiResponseMessage apiResponseMessage = ApiResponseMessage.builder().message("Product Deleted Successfully")
                .success(true)
                .status(HttpStatus.OK).build();
        return new ResponseEntity<>(apiResponseMessage, HttpStatus.OK);
    }

    //getall
    @GetMapping
    public ResponseEntity<PageableResponse<ProductDto>> getAllProducts(@RequestParam(value = "pageNumber", defaultValue = "0", required = false) int pageNumber,
                                                                       @RequestParam(value = "pageSize", defaultValue = "10", required = false) int pageSize,

                                                                       @RequestParam(value = "sortBy", defaultValue = "title", required = false)
                                                                       String sortBy,
                                                                       @RequestParam(value = "sortDir", defaultValue = "asc", required = false)
                                                                       String sortDir) {
        return new ResponseEntity<>(productService.getAll(pageNumber, pageSize, sortBy, sortDir), HttpStatus.CREATED);
    }

    //getall
    @GetMapping("/live")
    public ResponseEntity<PageableResponse<ProductDto>> getAllProductsLive(@RequestParam(value = "pageNumber", defaultValue = "0", required = false) int pageNumber,
                                                                           @RequestParam(value = "pageSize", defaultValue = "10", required = false) int pageSize,

                                                                           @RequestParam(value = "sortBy", defaultValue = "title", required = false)
                                                                           String sortBy,
                                                                           @RequestParam(value = "sortDir", defaultValue = "asc", required = false)
                                                                           String sortDir) {
        return new ResponseEntity<>(productService.getAllLive(pageNumber, pageSize, sortBy, sortDir), HttpStatus.CREATED);
    }

    //getall
    @GetMapping("/search/{productName}")
    public ResponseEntity<PageableResponse<ProductSearchDto>> searchProducts(@RequestParam(value = "pageNumber", defaultValue = "0", required = false) int pageNumber,
                                                                             @RequestParam(value = "pageSize", defaultValue = "10", required = false) int pageSize,

                                                                             @RequestParam(value = "sortBy", defaultValue = "title", required = false)
                                                                       String sortBy,
                                                                             @RequestParam(value = "sortDir", defaultValue = "asc", required = false)
                                                                       String sortDir, @PathVariable String productName) {
        return new ResponseEntity<>(productService.searchProducts(productName, pageNumber, pageSize, sortBy, sortDir), HttpStatus.CREATED);
    }

    //getProductById
    @GetMapping("/{ProductId}")
    public ResponseEntity<ProductDto> getProductById(@PathVariable String ProductId) {
        return new ResponseEntity<>(productService.getProduct(ProductId), HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/image/{ProductId}")
    public ResponseEntity<List<ImageResponse>> uploadProductImage(   @RequestParam("ProductImages") MultipartFile[] images, @PathVariable String ProductId) throws IOException {
        List<ImageResponse> responses = new ArrayList<>();
        ProductDto productDto = productService.getProduct(ProductId);
        List<String> imageNames = new ArrayList<>();

        for (MultipartFile image : images) {
            if (!image.isEmpty()) {
                String imageName = fileService.uploadImage(image, imageUploadPath);
                imageNames.add(imageName);
                responses.add(ImageResponse.builder()
                        .success(true)
                        .status(HttpStatus.OK)
                        .imageName(imageName)
                        .message("Image Saved Successfully")
                        .build());
            } else {
                responses.add(ImageResponse.builder()
                        .success(false)
                        .status(HttpStatus.BAD_REQUEST)
                        .message("Empty Image File")
                        .build());
            }
        }

        // Update the product with the image names
        productDto.setProductImages(imageNames); // Assuming your ProductDto has a `List<String>` for images
        productService.updateProduct(productDto, ProductId);
        return new ResponseEntity<>(responses, HttpStatus.OK);
    }

    @PreAuthorize("permitAll()")
    @GetMapping("/image/{ProductId}")
    public ResponseEntity<List<String>> getProductImages(@PathVariable String ProductId) {
        List<String> productImages = productService.findProductImages(ProductId);
        log.info("Product Image Names: {}", productImages);

        // Optionally, generate full URLs if needed
        List<String> imageUrls = productImages.stream()
                .map(imageName -> ServletUriComponentsBuilder
                        .fromCurrentContextPath()
                        .path(imageUploadPath)
                        .path(imageName)
                        .toUriString())
                .collect(Collectors.toList());
        System.out.println(imageUrls);

        return ResponseEntity.ok(imageUrls);
    }
}
