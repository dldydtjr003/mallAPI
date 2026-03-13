package com.kh.mallapi.controller;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.kh.mallapi.dto.PageRequestDTO;
import com.kh.mallapi.dto.PageResponseDTO;
import com.kh.mallapi.dto.ProductDTO;
import com.kh.mallapi.service.ProductService;
import com.kh.mallapi.util.CustomFileUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@RestController
@RequiredArgsConstructor
@Log4j2
@RequestMapping("/api/products")
public class ProductController {

	private final CustomFileUtil fileUtil;
	private final ProductService productService;

	@PostMapping("/")
	public Map<String, String> register(ProductDTO productDTO) {
		log.info("register: " + productDTO);

		List<MultipartFile> files = productDTO.getFiles();

		List<String> uploadFileNames = fileUtil.saveFiles(files);
		productDTO.setUploadFileNames(uploadFileNames);

		log.info(uploadFileNames);

		return Map.of("RESULT", "SUCCESS");
	}

	@GetMapping("/view/{fileName}")
	public ResponseEntity<Resource> viewFileGET(@PathVariable String fileName) {
		return fileUtil.getFile(fileName);
	}

	@GetMapping("/list")
	public PageResponseDTO<ProductDTO> list(PageRequestDTO pageRequestDTO) {
		log.info("list............." + pageRequestDTO);
		return productService.getList(pageRequestDTO);
	}

	@GetMapping("/{pno}")
	public ProductDTO read(@PathVariable(name = "pno") Long pno) {
		return productService.get(pno);
	}

	@PutMapping("/{pno}")
	public Map<String, String> modify(@PathVariable(name = "pno") Long pno, ProductDTO productDTO) {
		productDTO.setPno(pno);
		// 현재 DB에있는 파일의 정보 (pno=120L pname="aaaa" price=10000 pdesc="aaaa" pfile=[]
		// imageString="aaaa.jpg")
		ProductDTO oldProductDTO = productService.get(pno);

		// 기존파일들 (데이터베이스에 존재하는 파일들-수정 과정에서 삭제되었을 수 있음)
		// aaaa.jpg
		List<String> oldFileNames = oldProductDTO.getUploadFileNames();

		// 새로 업로드 해야 하는 파일들(o, o) (x, o) (x, x) (o, x)
		// 1. "bbbb.jpg"
		// 2. "bbbb.jpg"
		// 3. 새로된 파일 X
		// 4. 새로된 파일 X
		List<MultipartFile> files = productDTO.getFiles();

		// 새로 업로드되어서 만들어진 파일 이름들
		// 1. "bbbb.jpg" 내부 폴더에 저장, 중복되지 않는 이름 받고 리턴
		// 2. "bbbb.jpg" 내부 폴더에 저장, 중복되지 않는 이름 받고 리턴
		// 3. 새로된 파일 X null 리턴
		// 4. 새로된 파일 X null 리턴
		List<String> currentUploadFileNames = null;
		
		if (files != null && !files.get(0).isEmpty()) {
			currentUploadFileNames = fileUtil.saveFiles(files);
		}
		// 화면에서 변화 없이 계속 유지된 파일들
		// 1. "aaaa.jpg"
		// 2. "aaaa.jpg" 삭제
		// 3. 기존 파일 삭제
		// 3. "aaaa.jpg"
		List<String> uploadedFileNames = productDTO.getUploadFileNames();

		
		// 유지되는 파일들 + 새로 업로드된 파일 이름들이 저장해야 하는 파일 목록이 됨
		if (currentUploadFileNames != null && !currentUploadFileNames.isEmpty()) {
			// {기존 : "aaaa.jpg" , 추가 : "bbbb.jpg"}
			// {기존 : x , 추가 : "bbbb.jpg"}
			// {기존 : x , 추가 : X} - 문장 실행 X
			// {기존 : "aaaa.jpg" , 추가 : X} - 문장 실행 X
			uploadedFileNames.addAll(currentUploadFileNames);
		}

		// 수정 작업
		productService.modify(productDTO);

		if (oldFileNames != null && !oldFileNames.isEmpty()) {
			// 지워야 하는 파일 목록 찾기
			// 예전 파일들 중에서 지워져야 하는 파일이름들
			List<String> removeFiles = oldFileNames.stream()
					.filter(fileName -> uploadedFileNames.indexOf(fileName) == -1).collect(Collectors.toList());
			// 실제 파일 삭제
			fileUtil.deleteFiles(removeFiles);
		}
		return Map.of("RESULT", "SUCCESS");
	}
}
