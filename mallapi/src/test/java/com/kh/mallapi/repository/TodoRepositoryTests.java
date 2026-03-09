package com.kh.mallapi.repository;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.kh.mallapi.domain.Todo;

import lombok.extern.log4j.Log4j2;

@SpringBootTest
@Log4j2
public class TodoRepositoryTests {

	@Autowired
	private TodoRepository todoRepository;

	// @Test
	public void test1() {
		log.info("------------------------");
		log.info(todoRepository);
		log.info("------------------------");
	}

	// insert == repository.save(Entity)
	// @Test
	public void testInsert() {
		for (int i = 1; i <= 100; i++) {
			Todo todo = Todo.builder().title("Title..." + i).dueDate(LocalDate.of(2026, 3, 9)).writer("user00").build();
			// jpa : save(Entity) insert
			// insert into tbl_todo values(todo_seq.nextval(), title1...100, false,
			// 'user00', '2026-03-09')
			todoRepository.save(todo);
		}
	}

	// select == repository.findById(pk)
	// @Test
	public void testRead() {
		// 존재하는 번호로 확인
		Long tno = 233L;
		java.util.Optional<Todo> result = todoRepository.findById(tno);
		// 에러가 발생하면 NosuchElementException
		Todo todo = result.orElseThrow();
		log.info(todo);
	}

	// update -> 1. repository.findById(pk) -> 2. 컬럼 setter로 값 삽입 -> 3.
	// repository.save(Entity)
	// @Test
	public void testModify() {
		Long tno = 233L;
		java.util.Optional<Todo> result = todoRepository.findById(tno);
		Todo todo = result.orElseThrow();
		todo.changeTitle("Modified 233...");
		todo.changeComplete(true);
		todo.changeDueDate(LocalDate.of(2026, 3, 20));
		todoRepository.save(todo);
	}

	// delete == repository.deleteById(pk)
	@Test
	public void testDelete() {
		Long tno = 201L;
		todoRepository.deleteById(tno);
	}
}
