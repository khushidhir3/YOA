package com.project.Lpu_Application.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.project.Lpu_Application.DTO.Client;
import com.project.Lpu_Application.Response.ResponseStructure;
import com.project.Lpu_Application.Service.ClientService;

@RestController
@RequestMapping("client")
public class ClientController {

	@Autowired
	private ClientService clser;
	
	@PostMapping
	public ResponseEntity<ResponseStructure<Client>> saveClient(@RequestBody Client cl){
		return clser.saveCl(cl);
	}
	
	@GetMapping
	public ResponseEntity<ResponseStructure<String>> getClientByName(){
		return clser.getByName("oiut");
	}
	
	@DeleteMapping("/{id}")
	public ResponseEntity<ResponseStructure<String>> deleteClient(@PathVariable int id){
		return clser.deleteById(id);
	}
}
