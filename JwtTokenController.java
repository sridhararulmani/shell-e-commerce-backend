package com.project.shell.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.project.shell.custom.CustomUserDetails;
import com.project.shell.dto.TokenRenewalResponseDto;
import com.project.shell.dto.TokenRenrwalRequestDto;
import com.project.shell.jwt.JwtUtil;
import com.project.shell.jwt.TokenBlackList;
import com.project.shell.repository.AccountRepository;

@RestController
@RequestMapping("/tokens")
public class JwtTokenController {

	@Autowired
	private JwtUtil jwtUtil;

	@Autowired
	private AccountRepository accountRepository;

	@Autowired
	private TokenBlackList tokenBlackList;

	@GetMapping("/refreshTokens")
	public ResponseEntity<?> renewalAccessTokenUsingRefreshToken(@RequestBody TokenRenrwalRequestDto tokenRenrwalRequestDto) {
		String refreshToken = tokenRenrwalRequestDto.getRefreshToken();
		if (refreshToken != null && !jwtUtil.isTokenExpired(refreshToken) && !tokenBlackList.contains(refreshToken)) {

			String username = jwtUtil.extractAllClaims(refreshToken).getSubject();

			String newAccessToken = jwtUtil
					.createAccessToken(new CustomUserDetails(accountRepository.findByUserEmail(username).get()));
			String newRefreshToken = jwtUtil
					.createRefreshToken(new CustomUserDetails(accountRepository.findByUserEmail(username).get()));

			return ResponseEntity.ok(new TokenRenewalResponseDto(newAccessToken, newRefreshToken));
		} else {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid Refresh Token");
		}
	}
}
