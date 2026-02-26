package com.project.Lpu_Application.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.project.Lpu_Application.DTO.Client;

public interface ClientRepository extends JpaRepository<Client, Integer>{

	// if we want to get the client based on name
	// and if we have the clients names duplicated
	List<Client> findByName(String name);
	
	// if we want to find a client based on his/her age
	// age also might be duplicated so return type is list
	List<Client> findByAge(int age);
}
