package com.vbz.dbcards.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.vbz.dbcards.entity.BusinessCard;
import com.vbz.dbcards.repository.BusinessCardRepository;

import jakarta.persistence.EntityNotFoundException;

@RestController
public class NonDbcShareController {
	
	@Autowired
	private BusinessCardRepository businessCardRepository;
	
	@GetMapping("/nonuser/{id}")
	public ResponseEntity<?>getUerCard(@PathVariable Long id){
		BusinessCard businessuCard=businessCardRepository.findById(id)
				.orElseThrow(()->new EntityNotFoundException("not found"));
		
		return ResponseEntity.ok(businessuCard);
		
	}

}
