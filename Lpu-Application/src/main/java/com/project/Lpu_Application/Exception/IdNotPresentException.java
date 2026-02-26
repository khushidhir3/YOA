package com.project.Lpu_Application.Exception;

public class IdNotPresentException extends RuntimeException{

	String message = "id dosen't exists...";
	
	@Override
	public String getMessage() {
		return message;
	}
	public IdNotPresentException(String message) {
		this.message= message;
	}
	
	public IdNotPresentException() {
		System.out.println("id not present...");
	}
	
}
