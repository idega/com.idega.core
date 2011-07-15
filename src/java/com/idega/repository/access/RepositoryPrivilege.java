package com.idega.repository.access;

import javax.jcr.security.Privilege;

public class RepositoryPrivilege implements Privilege {

	private String name;

	public RepositoryPrivilege(String name) {
		this.name = name;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public boolean isAbstract() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isAggregate() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Privilege[] getDeclaredAggregatePrivileges() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Privilege[] getAggregatePrivileges() {
		// TODO Auto-generated method stub
		return null;
	}

}