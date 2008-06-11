package com.idega.test;

import javax.faces.application.Application;
import javax.faces.application.ApplicationFactory;

public class MockApplicationFactory extends ApplicationFactory {

	private Application application;
	
	public MockApplicationFactory(){
		this(new MockApplication());
	}
	
	public MockApplicationFactory(Application app){
		setApplication(app);
	}
	
	@Override
	public Application getApplication() {
		return application;
	}

	@Override
	public void setApplication(Application app) {
		this.application=app;
	}

}
