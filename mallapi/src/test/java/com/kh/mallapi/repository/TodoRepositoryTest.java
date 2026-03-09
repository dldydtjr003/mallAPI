package com.kh.mallapi.repository;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.log;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import com.kh.mallapi.domain.Todo;

import lombok.extern.log4j.Log4j2;

@SpringBootTest
@Log4j2
public class TodoRepositoryTest {
	@Autowired
	private TodoRepository todoRepository;

	// @Test
	public void test1() {
		log.info("============================");
		log.info(todoRepository);
		log.info("============================");
	}

	// Insert == repository.save(Entity)
	// @Test
	public void testInsert() {
		for (int i = 1; i <= 100; i++) {
			Todo todo = Todo.builder().title("Title..." + i).dueDate(LocalDate.of(2026, 3, 9)).writer("user" + i)
					.build();
			// save(Entity) : insert
			// insert into tbl_todo values(todo_seq.nextval, 'title...1', flase, 'user1',
			// '2026-03-09')와 같다.
			todoRepository.save(todo);
		}
	}

	// Select == findById(Entity.PK)
	// @Test
	public void testRead() {
		// 존재하는 번호로 확인
		Long tno = 55L;
		java.util.Optional<Todo> result = todoRepository.findById(tno);
		Todo todo = result.orElseThrow();
		log.info(todo);
	}

	// update 로직
	// 1. java.util.Optional<Todo> result = ...findById(Entity.PK) => 수정할 데이터를 불러온다.
	// 2. Todo todo = result.orElseThrow() / todo.changeTitle("Modified 33...") ...
	// => 해당 데이터를 수정한다.
	// 3. todoRepository.save(Entity) => 수정된 데이터를 저장한다.
	// @Test
	public void testModify() {
		Long tno = 33L;
		java.util.Optional<Todo> result = todoRepository.findById(tno);
		Todo todo = result.orElseThrow();
		todo.changeTitle("Modified 33...");
		todo.changeComplete(true);
		todo.changeDueDate(LocalDate.of(2026, 4, 10));
		todoRepository.save(todo);
	}

	// Delete == deleteById(Entity.PK)
	// @Test
	public void testDelete() {
		Long tno = 1L;
		todoRepository.deleteById(tno);
	}
	
	//페이징 처리 로직
	//Pageable pageable = PageRequest.of(시작페이지 - 1, 페이지당 보여주는 라인 수, 정렬기준);
	//@Test
	public void testPaging() {
		// 0번째 페이지요청(페이지 인덱스는 0부터 시작), 한 페이지에 10개의 데이터,
		// 정렬기준은 tno 필드를 기준으로 내림차순
		Pageable pageable = PageRequest.of(0, 10, Sort.by("tno").descending());
		// Page<Todo>타입 반환되며, 전체 정보(총 개수, 현재 페이지 등)가 포함
		Page<Todo> result = todoRepository.findAll(pageable);
		// 전체 데이터 개수(전체 Todo 엔티티 수)를 로그로 출력
		log.info(result.getTotalElements());
		// 현재 페이지(0페이지)에 포함된 Todo 목록을 가져온다.
		result.getContent().stream().forEach(todo -> log.info(todo));
	}
}