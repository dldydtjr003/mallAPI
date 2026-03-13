package com.kh.mallapi.service;

import com.kh.mallapi.dto.PageRequestDTO;
import com.kh.mallapi.dto.PageResponseDTO;
import com.kh.mallapi.dto.ProductDTO;

import jakarta.transaction.Transactional;

@Transactional
public interface ProductService {
	PageResponseDTO<ProductDTO> getList(PageRequestDTO pageRequestDTO);
	Long register(ProductDTO productDTO); 
	ProductDTO get(Long pno);
	void modify(ProductDTO productDTO)
}
