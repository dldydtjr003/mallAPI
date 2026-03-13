package com.kh.mallapi.service;

import java.time.LocalDate;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.kh.mallapi.dto.PageRequestDTO;
import com.kh.mallapi.dto.PageResponseDTO;
import com.kh.mallapi.dto.ProductDTO;
import com.kh.mallapi.dto.TodoDTO;

import lombok.extern.log4j.Log4j2;

@SpringBootTest
@Log4j2
public class TodoServiceTest {

	@Autowired
	private TodoService todoService;

	@Autowired
	private ProductService productService;

	// @Test
	public void testRegister() {
		TodoDTO todoDTO = TodoDTO.builder().title("서비스 테스트").writer("tester").dueDate(LocalDate.of(2026, 5, 1)).build();
		Long tno = todoService.register(todoDTO);
		log.info("TNO: " + tno);
	}

	// @Test
	public void testGet() {
		Long tno = 250L;
		TodoDTO todoDTO = todoService.get(tno);
		log.info(todoDTO);
	}

	// @Test
	public void testModify() {
		TodoDTO todoDTO = TodoDTO.builder().tno(101L).title("사용자수정").writer("tester").dueDate(LocalDate.of(2026, 3, 9))
				.build();
		todoService.modify(todoDTO);
	}

	// @Test
	public void testRemove() {
		Long tno = 250L;
		todoService.remove(tno);
	}

	// @Test
	public void testList() {
		PageRequestDTO pageRequestDTO = PageRequestDTO.builder().page(2).size(10).build();

		PageResponseDTO<TodoDTO> response = todoService.list(pageRequestDTO);

		log.info(response);
	}

	// @Test
	public void testList2() {
		// 1 page, 10 size
		PageRequestDTO pageRequestDTO = PageRequestDTO.builder().build();
		PageResponseDTO<ProductDTO> result = productService.getList(pageRequestDTO);
		result.getDtoList().forEach(dto -> log.info(dto));
	}

	@Test
	public void testRegister2() {
		ProductDTO productDTO = ProductDTO.builder().pname("새로운 상품").pdesc("신규 추가 상품입니다.").price(1000).build();
		// uuid가 있어야함
		// List<String> listString = List.of("aaaa","bbbb")
		productDTO.setUploadFileNames(
				java.util.List.of(UUID.randomUUID() + "_" + "Test1.jpg", UUID.randomUUID() + "_" + "Test2.jpg"));
		productService.register(productDTO);
	}
}
