package com.project.shell.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.project.shell.entity.Role;
import com.project.shell.service.RoleService;

@RestController
@RequestMapping("/role")
public class RoleController {
	
	@Autowired
	private RoleService roleService;
	
	@PostMapping("/addNewRole")
	public ResponseEntity<Role> addNewRole(@RequestBody Role role){
		System.out.println("Role controller is working to persist role ");
		return ResponseEntity.ok(roleService.save(role));
	}
}
