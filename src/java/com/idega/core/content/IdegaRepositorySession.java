package com.idega.core.content;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;


@Scope("session")
@Service(IdegaRepositorySession.SPRING_BEAN_IDENTIFIER)
public class IdegaRepositorySession {

	public static final String SPRING_BEAN_IDENTIFIER  = "idegaRepositorySession";
	
	IdegaRepository repository;
	
	
	public IdegaRepository getRepository() {
		return repository;
	}
	
	@Autowired
	public void setRepository(IdegaRepository repository) {
		this.repository = repository;
	}	
	
}
