package com.kh.mallapi.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.kh.mallapi.domain.Product;
import com.kh.mallapi.domain.ProductImage;
import com.kh.mallapi.dto.PageRequestDTO;
import com.kh.mallapi.dto.PageResponseDTO;
import com.kh.mallapi.dto.ProductDTO;
import com.kh.mallapi.repository.ProductRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Service
@Log4j2
@RequiredArgsConstructor
@Transactional
public class ProductServiceImpl implements ProductService {

	private final ProductRepository productRepository;

	@Override
	public PageResponseDTO<ProductDTO> getList(PageRequestDTO pageRequestDTO) {
		// 페이지 시작 번호가 0 부터 시작하므로
		Pageable pageable = PageRequest.of(pageRequestDTO.getPage() - 1, pageRequestDTO.getSize(),
				Sort.by("pno").descending());

		Page<Object[]> result = productRepository.selectList(pageable);

		List<ProductDTO> dtoList = result.get().map(arr -> {
			Product product = (Product) arr[0];
			ProductImage productImage = (ProductImage) arr[1];

			ProductDTO productDTO = ProductDTO.builder().pno(product.getPno()).pname(product.getPname())
					.pdesc(product.getPdesc()).price(product.getPrice()).build();

			String imageStr = productImage.getFileName();
			productDTO.setUploadFileNames(List.of(imageStr));

			return productDTO;
		}).collect(Collectors.toList());

		long totalCount = result.getTotalElements();
		return PageResponseDTO.<ProductDTO>withAll().dtoList(dtoList).totalCount(totalCount)
				.pageRequestDTO(pageRequestDTO).build();
	}

	@Override
	public Long register(ProductDTO productDTO) {
		Product product = dtoToEntity(productDTO);
		
		Product result = productRepository.save(product);
		return result.getPno();
	}

	private Product dtoToEntity(ProductDTO productDTO) {
		Product product = Product.builder().pno(productDTO.getPno()).pname(productDTO.getPname())
				.pdesc(productDTO.getPdesc()).price(productDTO.getPrice()).build();
		// 업로드 처리가 끝난 파일들의 이름 리스트
		List<String> uploadFileNames = productDTO.getUploadFileNames();
		if (uploadFileNames == null) {
			return product;
		}
		uploadFileNames.stream().forEach(uploadName -> {
			product.addImageString(uploadName);
		});

		return product;
	}

}
