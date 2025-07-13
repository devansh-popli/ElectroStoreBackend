package com.lcwd.store.repositories;

import com.lcwd.store.entities.Category;
import com.lcwd.store.entities.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface ProductRepository extends JpaRepository<Product, String> {

    Page<Product> findByTitleContaining(String subTitle, Pageable pageable);

    @Query(value = "SELECT image_name FROM product_images WHERE product_id = :id", nativeQuery = true)
    Optional<List<String>> findProductImages(@Param("id") String productId);

    Page<Product> findByLiveTrue(Pageable pageable);


    Page<Product> findByCategories(Category categories, Pageable pageable);
}
