package com.project.shell.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.project.shell.custom.CustomUserDetails;
import com.project.shell.custom.exception.InvalidTokenException;
import com.project.shell.dto.LoginRequestDto;
import com.project.shell.dto.LoginResposeDto;
import com.project.shell.dto.LogoutRequestDto;
import com.project.shell.entity.Account;
import com.project.shell.jwt.JwtUtil;
import com.project.shell.jwt.TokenBlackList;
import com.project.shell.repository.AccountRepository;
import com.project.shell.service.CustomUserDetailsService;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {

	@Autowired
	private JwtUtil jwtUtil;

	@Autowired
	private CustomUserDetailsService customUserDetailsService;

	@Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	private AccountRepository accountRepository;

	@Autowired
	private TokenBlackList tokenBlackList;

	@PostMapping("/login")
	public ResponseEntity<LoginResposeDto> login(@RequestBody LoginRequestDto loginRequestDto) {
		System.out.println("Login Process working");
		try {
			System.out.println(loginRequestDto.getUsername());
			System.out.println(loginRequestDto.getPassword());
			Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
					loginRequestDto.getUsername(), loginRequestDto.getPassword()));

			CustomUserDetails customUserDetails = (CustomUserDetails) customUserDetailsService
					.loadUserByUsername(authentication.getName());

			String accessToken = jwtUtil.createAccessToken(customUserDetails);
			String refreshToken = jwtUtil.createRefreshToken(customUserDetails);

			Optional<Account> optional = accountRepository.findByUserEmail(authentication.getName());

			System.out.println(accessToken);
			System.out.println(refreshToken);
			System.out.println(optional.get());
			return ResponseEntity.ok(new LoginResposeDto(accessToken, refreshToken, optional.get()));
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
		}
	}

	@PostMapping("/logout")
	public ResponseEntity<?> logout(@RequestBody LogoutRequestDto logoutRequestDto) {
		try {
			tokenBlackList.addTokenToBlockList(logoutRequestDto.getAccessToken());
			tokenBlackList.addTokenToBlockList(logoutRequestDto.getRefreshToken());
			SecurityContextHolder.clearContext();
			return ResponseEntity.ok().body("Logedouted successfully...");
		} catch (Exception e) {
			throw new InvalidTokenException("Invalid Token");
		}
	}

}
