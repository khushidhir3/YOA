package com.project.Lpu_Application.DAO;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.project.Lpu_Application.DTO.Client;
import com.project.Lpu_Application.Repository.ClientRepository;

@Repository
public class ClientDAO {

	@Autowired
	private ClientRepository clres;
	
	public Client saveClient(Client cl) {
		return clres.save(cl);
	}
	
	public String getByName(String name) {
		List<Client> cl = clres.findByName(name);
		return "the records with same anme are :"+cl;
	}
	
	public Client deleteById( int id) {
		Optional<Client> op = clres.findById(id);
		if(op.isPresent()) {
			clres.delete(op.get());
			return op.get();
		}
		return null;		
	}
}
