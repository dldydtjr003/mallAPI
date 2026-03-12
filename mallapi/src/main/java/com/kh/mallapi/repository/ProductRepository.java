package com.kh.mallapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kh.mallapi.domain.Product;

public interface ProductRepository extends JpaRepository<Product, Long>{

}
