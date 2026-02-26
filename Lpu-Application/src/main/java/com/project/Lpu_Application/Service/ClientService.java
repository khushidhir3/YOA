package com.project.Lpu_Application.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.project.Lpu_Application.DAO.ClientDAO;
import com.project.Lpu_Application.DTO.Client;
import com.project.Lpu_Application.Exception.IdNotPresentException;
import com.project.Lpu_Application.Response.ResponseStructure;

@Service
public class ClientService {

	@Autowired
	private ClientDAO dao;
	
	public ResponseEntity<ResponseStructure<Client>> saveCl(Client cl){
		ResponseStructure<Client> res = new ResponseStructure<>();
		res.setStatuscode(HttpStatus.OK.value());
		res.setMessage("data saved");
		res.setData(dao.saveClient(cl));
		return new ResponseEntity<ResponseStructure<Client>>(res, HttpStatus.OK);
	}
	
	public ResponseEntity<ResponseStructure<String>> getByName(String name){
		ResponseStructure<String> res = new ResponseStructure<>();
		res.setStatuscode(HttpStatus.OK.value());
		res.setMessage("data saved");
		res.setData(dao.getByName(name));
		return new ResponseEntity<ResponseStructure<String>>(res, HttpStatus.OK);

	}
	
	public ResponseEntity<ResponseStructure<String>> deleteById(int id){
		ResponseStructure<String> res = new ResponseStructure<>();
		Client cl =  dao.deleteById(id);
		if(cl!=null) {
			res.setStatuscode(HttpStatus.OK.value());
			res.setMessage("id deleted");
			res.setData(" data deleted");
			return new ResponseEntity<ResponseStructure<String>>(res, HttpStatus.OK);
		}
		throw new IdNotPresentException();
	}
}
