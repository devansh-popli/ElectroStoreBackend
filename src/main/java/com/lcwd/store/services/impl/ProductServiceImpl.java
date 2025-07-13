package com.lcwd.store.services.impl;

import com.lcwd.store.dtos.PageableResponse;
import com.lcwd.store.dtos.ProductDto;
import com.lcwd.store.dtos.ProductSearchDto;
import com.lcwd.store.entities.Category;
import com.lcwd.store.entities.Product;
import com.lcwd.store.exceptions.ResourceNotFoundException;
import com.lcwd.store.helper.HelperUtils;
import com.lcwd.store.repositories.CategoryRepository;
import com.lcwd.store.repositories.ProductRepository;
import com.lcwd.store.services.ProductService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    ProductRepository productRepository;
    @Autowired
    CategoryRepository categoryRepository;

    @Autowired
    ModelMapper modelMapper;

    @Value("${product.image.path}")
    private String imageUploadPath;

    @Override
    @CacheEvict(value = "products", allEntries = true)
    public ProductDto createProduct(ProductDto productDto) {
        productDto.setProductId(UUID.randomUUID().toString());
        productDto.setAddedDate(new Date());
        Product savedProduct = productRepository.save(modelMapper.map(productDto, Product.class));
        return modelMapper.map(savedProduct, ProductDto.class);
    }

    @Override
    @CacheEvict(value = "products", allEntries = true)
    public ProductDto updateProduct(ProductDto productDto, String productId) {
        Product product = productRepository.findById(productId).orElseThrow(() -> new ResourceNotFoundException("product not found"));
        product.setTitle(productDto.getTitle());
        product.setStock(productDto.isStock());
        product.setLive(productDto.isLive());
        product.setPrice(productDto.getPrice());
        product.setProductImages(productDto.getProductImages());
        product.setDiscountedPrice(productDto.getDiscountedPrice());
        product.setDescription(productDto.getDescription());
        product.setQuantity(productDto.getQuantity());
        Product updatedProduct = productRepository.save(product);
        return modelMapper.map(updatedProduct, ProductDto.class);
    }

    @Override
    @CacheEvict(value = "products", allEntries = true)
    public void deleteProduct(String productId) {
        Product product = productRepository.findById(productId).orElseThrow(() -> new ResourceNotFoundException("product not found"));
        product.getProductImages().forEach(productImage -> {
            String imageFullPath = imageUploadPath + productImage;
            Path path = Paths.get(imageFullPath);
            try {
                Files.delete(path);
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (product.getCategories() != null && !CollectionUtils.isEmpty(product.getCategories())) {
                for (Category category : product.getCategories()) {
                    category.getProducts().remove(product);
                }
                product.getCategories().clear();
                productRepository.save(product);
            }
            productRepository.delete(product);
        });

    }

    @Override
//    @Cacheable(value = "products",key = "#productId")
    @Transactional(readOnly = true)
    public ProductDto getProduct(String productId) {
        Product product = productRepository.findById(productId).orElseThrow(() -> new ResourceNotFoundException("product not found"));
        return modelMapper.map(product, ProductDto.class);
    }

    @Override
    @Cacheable(value = "products", key = "'pageNumber'+#pageNumber+'pageSize'+#pageSize+'sortBy'+#sortBy+'sortDir'+#sortDir")
    public PageableResponse<ProductDto> getAll(int pageNumber, int pageSize, String sortBy, String sortDir) {
        Sort sort = (sortDir.equalsIgnoreCase("asc")) ? (Sort.by(sortBy).ascending()) : (Sort.by(sortBy).descending());
        Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);
        Page<Product> productPage = productRepository.findAll(pageable);
        PageableResponse<ProductDto> response = HelperUtils.getPageableResponse(productPage, ProductDto.class);
        return response;
    }

    @Override
    @Cacheable(value = "products", key = "'pageNumber'+#pageNumber+'pageSize'+#pageSize+'sortBy'+#sortBy+'sortDir'+#sortDir")
    public PageableResponse<ProductDto> getAllLive(int pageNumber, int pageSize, String sortBy, String sortDir) {
        Sort sort = (sortDir.equalsIgnoreCase("asc")) ? (Sort.by(sortBy).ascending()) : (Sort.by(sortBy).descending());
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        Page<Product> productPage = productRepository.findByLiveTrue(pageable);
        PageableResponse<ProductDto> response = HelperUtils.getPageableResponse(productPage, ProductDto.class);
        return response;
    }

    @Override
    @CacheEvict(value = "products", allEntries = true)
    public ProductDto createWithCategory(ProductDto productDto, String categoryId) {
        //fetch the category
        Category category = categoryRepository.findById(categoryId).orElseThrow(() -> new ResourceNotFoundException("Category Not Found"));
        productDto.setProductId(UUID.randomUUID().toString());
        productDto.setAddedDate(new Date());
        Product product = modelMapper.map(productDto, Product.class);
        product.getCategories().add(category);
        Product savedProduct = productRepository.save(product);
        return modelMapper.map(savedProduct, ProductDto.class);
    }

    @Override
    @CacheEvict(value = "products", allEntries = true)
    public ProductDto updateProductWithCategory(String productId, List<String> categoryIds) {
        //fetch the category

        Product product = productRepository.findById(productId).orElseThrow(() -> new ResourceNotFoundException("product not found"));
        product.getCategories().removeAll(product.getCategories());
        for (String categoryId : categoryIds) {
            Category category = categoryRepository.findById(categoryId).orElseThrow(() -> new ResourceNotFoundException("Category Not Found"));
            if (!product.getCategories().contains(category)) {
                product.getCategories().add(category);
            }
        }
        product = productRepository.save(product);

        return modelMapper.map(product, ProductDto.class);
    }

    @Override
    @Cacheable(value = "products", key = "'pageNumber'+#pageNumber+'pageSize'+#pageSize+'sortBy'+#sortBy+'sortDir'+#sortDir+'productName'+#productName")
    public PageableResponse<ProductSearchDto> searchProducts(String productName, int pageNumber, int pageSize, String sortBy, String sortDir) {
        Sort sort = (sortDir.equalsIgnoreCase("asc")) ? (Sort.by(sortBy).ascending()) : (Sort.by(sortBy).descending());
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        Page<Product> productPage = productRepository.findByTitleContaining(productName, pageable);
        PageableResponse<ProductSearchDto> response = HelperUtils.getPageableResponse(productPage, ProductSearchDto.class);
        return response;
    }

    @Override
    @Cacheable(value = "products", key = "'pageNumber'+#pageNumber+'pageSize'+#pageSize+'sortBy'+#sortBy+'sortDir'+#sortDir+'categoryId'+#categoryId")
    public PageableResponse<ProductDto> getAllOfCategory(int pageNumber, int pageSize, String sortBy, String sortDir, String categoryId) {
        Sort sort = (sortDir.equalsIgnoreCase("asc")) ? (Sort.by(sortBy).ascending()) : (Sort.by(sortBy).descending());
        Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);
        Category category = new Category();
        category.setCategoryId(categoryId);
        Page<Product> productPage = productRepository.findByCategories(category, pageable);
        PageableResponse<ProductDto> response = HelperUtils.getPageableResponse(productPage, ProductDto.class);
        return response;
    }

    @Override
    public List<String> findProductImages(String productId) {
        return productRepository.findProductImages(productId).orElseThrow(() -> new ResourceNotFoundException("Images not found"));
    }

}
