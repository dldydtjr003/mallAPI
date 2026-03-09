package com.kh.mallapi.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.kh.mallapi.domain.Todo;
import com.kh.mallapi.dto.PageRequestDTO;
import com.kh.mallapi.dto.PageResponseDTO;
import com.kh.mallapi.dto.TodoDTO;
import com.kh.mallapi.repository.TodoRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Transactional
@Slf4j
//final로 선언된 필드(modelMapper, todoRepository)에 대해 자동 생성자주입 
@RequiredArgsConstructor
public class TodoServiceImpl implements TodoService {
	// 자동주입 대상은 final로 해야한다.
	private final ModelMapper modelMapper;
	private final TodoRepository todoRepository;

	@Override
	public Long register(TodoDTO todoDTO) {
		log.info("......................... ");
		// TodoDTO를 Todo로 변환
		Todo todo = modelMapper.map(todoDTO, Todo.class);
		Todo savedTodo = todoRepository.save(todo);

		return savedTodo.getTno();
	}

	@Override
	public TodoDTO get(Long tno) {
		log.info("......................... ");
		java.util.Optional<Todo> result = todoRepository.findById(tno);
		Todo todo = result.orElseThrow();

		TodoDTO todoDTO = modelMapper.map(todo, TodoDTO.class);
		return todoDTO;
	}

	@Override
	public void modify(TodoDTO todoDTO) {
		Optional<Todo> result = todoRepository.findById(todoDTO.getTno());
		Todo todo = result.orElseThrow();

		todo.changeTitle(todoDTO.getTitle());
		todo.changeDueDate(todoDTO.getDueDate());
		todo.changeComplete(todoDTO.isComplete());

		todoRepository.save(todo);
	}

	@Override
	public void remove(Long tno) {
		todoRepository.deleteById(tno);
	}

	@Override
	public PageResponseDTO<TodoDTO> list(PageRequestDTO pageRequestDTO) {
		Pageable pageable = PageRequest.of(pageRequestDTO.getPage() - 1, // 1 페이지가 0 이므로 주의
				pageRequestDTO.getSize(), Sort.by("tno").descending());
		//1페이지에 해당되는 레코드 10개를 가져온다.
		Page<Todo> result = todoRepository.findAll(pageable);
		//1페이지에 해당되는 10개 레코드를 가져온다.
		List<TodoDTO> dtoList = result.getContent().stream().map(todo -> modelMapper.map(todo, TodoDTO.class))
				.collect(Collectors.toList());
		//전체 레코드수를 구함
		long totalCount = result.getTotalElements();
		
		PageResponseDTO<TodoDTO> responseDTO = PageResponseDTO.<TodoDTO>withAll().dtoList(dtoList)
				.pageRequestDTO(pageRequestDTO).totalCount(totalCount).build();
		
		return responseDTO;
	}
}
