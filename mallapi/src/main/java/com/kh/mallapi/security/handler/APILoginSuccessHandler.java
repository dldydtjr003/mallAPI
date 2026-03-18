package com.kh.mallapi.security.handler;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import com.google.gson.Gson;
import com.kh.mallapi.dto.MemberDTO;
import com.kh.mallapi.util.JWTUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class APILoginSuccessHandler implements AuthenticationSuccessHandler {

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws IOException, ServletException {

		log.info("--------------------------------- ");
		log.info(authentication);
		log.info("--------------------------------- ");
		
		// 시큐리티가 가지고 있는 인증, 인가 MemberDTO
		MemberDTO memberDTO = (MemberDTO) authentication.getPrincipal();
		// 리액트에게 전달 할 사용자의 인증, 인가
		Map<String, Object> claims = memberDTO.getClaims();
		// 10분 토큰
		String accessToken = JWTUtil.generateToken(claims, 10);
		
		// 24시간 토큰
		String refreshToken = JWTUtil.generateToken(claims, 60*24);
		
		claims.put("accessToken", accessToken); // 나중에 구현
		claims.put("refreshToken", refreshToken); // 나중에 구현

		Gson gson = new Gson();
		String jsonStr = gson.toJson(claims);

		response.setContentType("application/json; charset=UTF-8");
		PrintWriter printWriter = response.getWriter();
		// 리액트한테 전달 (인증, 인가)
		printWriter.println(jsonStr);
		printWriter.close();
	}

}
