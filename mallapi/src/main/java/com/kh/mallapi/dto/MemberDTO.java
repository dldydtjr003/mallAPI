package com.kh.mallapi.dto;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import lombok.Data;

@Data
public class MemberDTO extends User {

	private static final long serialVersionUID = 1L;

	private String email;
	private String pw;
	private String nickname;
	private boolean social;

	private List<String> roleNames = new ArrayList<>();

	public MemberDTO(String email, String pw, String nickname, boolean social, List<String> roleNames) {
		// 시큐리티 User(username, password, authority) -> User(email, pw, List<ROLE_문자열>)
		super(email, pw,
				roleNames.stream().map(str -> new SimpleGrantedAuthority("ROLE_" + str)).collect(Collectors.toList()));
		this.email = email;
		this.pw = pw;
		this.nickname = nickname;
		this.social = social;
		this.roleNames = roleNames;
	}

	// 리액트한테 인증,인가된 사용자 정보를 json방식으로 보내기 위해 사용
	public Map<String, Object> getClaims() {
		Map<String, Object> dataMap = new HashMap<>();
		dataMap.put("email", email);
		dataMap.put("pw", pw);
		dataMap.put("nickname", nickname);
		dataMap.put("social", social);
		dataMap.put("roleNames", roleNames);
		return dataMap;
	}

}
