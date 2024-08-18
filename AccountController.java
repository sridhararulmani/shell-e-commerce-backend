package com.project.shell.controller;

import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.project.shell.dto.ErrorDto;
import com.project.shell.dto.RegisterNewUserDto;
import com.project.shell.entity.Account;
import com.project.shell.service.AccountService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/user")
public class AccountController {

	@Autowired
	private AccountService accountService;

	@PostMapping(consumes = "multipart/form-data", value = "/registerUser")
	public ResponseEntity<?> registerNewUser(@Valid @ModelAttribute RegisterNewUserDto registerNewUserDto,
			BindingResult bindingResult) {
		System.out.println("Register User controller is working");
		if (bindingResult.hasErrors()) {
			System.out.println("Error accured when registering");
			List<FieldError> fieldErrors = bindingResult.getFieldErrors();
			return ResponseEntity.badRequest()
					.body(fieldErrors.stream().map(error -> new ErrorDto(error.getField(), error.getDefaultMessage()))
							.collect(Collectors.toList()));
		}
		return ResponseEntity.ok(accountService.save(registerNewUserDto)).status(HttpStatus.CREATED).build();
	}

	@GetMapping(value = "/getUserDetails")
	public ResponseEntity<?> getUserDetails() {
		return ResponseEntity.ok(accountService.findByUserEmail());
	}
	
	@GetMapping(value = "/getUserProfile")
	public ResponseEntity<?> getUserProfile(){
		Account account = accountService.findByUserEmail();
		return ResponseEntity.ok().body(Base64.getEncoder().encodeToString(account.getUserProfile())); 
	}
}