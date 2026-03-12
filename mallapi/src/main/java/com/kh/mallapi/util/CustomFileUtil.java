package com.kh.mallapi.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.coobird.thumbnailator.Thumbnails;

@Component
@Log4j2
@RequiredArgsConstructor
public class CustomFileUtil {

	// C:\SpringBootProject\workSpace\mallapiPubData\ m a l l a p i \ u p l o a d
	@Value("${com.kh.upload.path}")
	private String uploadPath;

	// 폴더 생성
	@PostConstruct
	// CustomFileUtil 객체로 load 되면서, init() 자동으로 실행
	public void init() {
		File tempFolder = new File(uploadPath);
		if (tempFolder.exists() == false) {
			tempFolder.mkdir();
		}
		uploadPath = tempFolder.getAbsolutePath();
		log.info("tempFolder.getAbsolutePath = " + uploadPath);
	}

	// 사용자가 보내준 리스트 파일들을 내장 폴더에 중복되지 않는 이름으로 변경해서 저장하고, 파일명을 리스트에 저장 후 리턴
	public List<String> saveFiles(List<MultipartFile> files) throws RuntimeException {
		// size() == 0 대신 isEmpty() 권장
		if (files == null || files.isEmpty()) {
			return null;
		}

		// 중복되지 않는 파일명 생성 후 저장 리스트
		List<String> uploadNames = new ArrayList<>();

		for (MultipartFile multipartFile : files) {
			// 중복되지 않는 파일명 생성
			String savedName = UUID.randomUUID().toString() + "_" + multipartFile.getOriginalFilename();
			/*
			 * savePath = C:\SpringBootProject\workSpace\mallapiPubData\ m a l l a p i \ u p
			 * l o a d\"saveName"
			 */
			Path savePath = Paths.get(uploadPath, savedName);

			try {
				/*
				 * 사용자가 보내준 파일 복사 (inputStream -> C:\SpringBootProject\workSpace\mallapiPubData\
				 * m a l l a p i \ u p l o a d\"savedName")
				 */
				Files.copy(multipartFile.getInputStream(), savePath);
				// 파일의 타입 saveName.jpg -> jpg 타입
				String contentType = multipartFile.getContentType();

				// 썸네일 생성
				// 타입 체크 후 이미지 파일인지 확인
				if (contentType != null && contentType.startsWith("image")) {
					/*
					 * 썸네일 파일명 생성 : C:\SpringBootProject\workSpace\mallapiPubData\ m a l l a p i \ u
					 * p l o a d\s_"savedName".jpg
					 */
					Path thumbnailPath = Paths.get(uploadPath, "s_" + savedName);
					// 원본 파일 사이즈(400) 변경 후 썸네일 파일에 저장
					Thumbnails.of(savePath.toFile()).size(400, 400).toFile(thumbnailPath.toFile());
				}
				// uploadNames 리스트안에 "saveName" 추가
				uploadNames.add(savedName);
			} catch (IOException e) {
				throw new RuntimeException("File save error: " + e.getMessage());
			}
		}
		return uploadNames;
	}

	// 브라우저에게 화면을 보여주는 기능
	public ResponseEntity<Resource> getFile(String fileName) {
		/*
		 * C:\SpringBootProject\workSpace\mallapiPubData\ m a l l a p i \ u p l o a
		 * d+" \ "+s_"savedName".jpg
		 */
		Resource resource = new FileSystemResource(uploadPath + File.separator + fileName);
		// 보낼 파일 존재 유/무 확인
		if (!resource.exists()) {
			resource = new FileSystemResource(uploadPath + File.separator + "default.jpg");
		}
		// 웹 브라우저에게 보낼 Header 생성
		HttpHeaders headers = new HttpHeaders();
		try {
			/*
			 * Files.probeContentType()은 파일 경로를 분석하여 MIME 타입을 자동 감지 jpg → image/jpeg, png →
			 * image/png pdf → application/pdf 이 정보를 HTTP 응답 헤더에 Content-Type으로 추가한다
			 */
			headers.add("Content-Type", Files.probeContentType(resource.getFile().toPath()));
		} catch (Exception e) {
			return ResponseEntity.internalServerError().build();
		}

		return ResponseEntity.ok().headers(headers).body(resource);
	}

	// 파일 삭제
	public void deleteFiles(List<String> fileNames) {
		if (fileNames == null || fileNames.isEmpty()) {
			return;
		}
		fileNames.forEach(fileName -> {
			// 썸네일이 있는지 확인하고 삭제
			String thumbnailFileName = "s_" + fileName;
			// 썸네일 경로
			Path thumbnailPath = Paths.get(uploadPath, thumbnailFileName);
			// 원본 경로
			Path filePath = Paths.get(uploadPath, fileName);
			try {
				Files.deleteIfExists(filePath);
				Files.deleteIfExists(thumbnailPath);
			} catch (IOException e) {
				throw new RuntimeException(e.getMessage());
			}
		});
	}

}
