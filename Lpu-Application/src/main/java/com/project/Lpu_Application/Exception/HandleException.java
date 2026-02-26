package com.project.Lpu_Application.Exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.project.Lpu_Application.Response.ResponseStructure;

@ControllerAdvice
public class HandleException {

	@ExceptionHandler(IdNotPresentException.class)
	public ResponseEntity<ResponseStructure<String>> handleExcep(IdNotPresentException ipe){
		ResponseStructure<String> res = new ResponseStructure<>();
		res.setStatuscode(HttpStatus.NOT_FOUND.value());
		res.setMessage("give existing id");
		res.setData(ipe.getMessage());
		return new ResponseEntity<ResponseStructure<String>>(res, HttpStatus.NOT_FOUND);
	}
}
