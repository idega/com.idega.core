package com.idega.repository.event;

import java.util.Map;

import org.springframework.context.ApplicationEvent;

public class RepositoryResourceLocalizer extends ApplicationEvent {

	private static final long serialVersionUID = -565295302144536065L;

	private Map<String, Map<String, String>> localizations;

	public RepositoryResourceLocalizer(Map<String, Map<String, String>> localizations) {
		super(localizations);

		this.localizations = localizations;
	}

	public Map<String, Map<String, String>> getLocalizations() {
		return localizations;
	}

	public void setLocalizations(Map<String, Map<String, String>> localizations) {
		this.localizations = localizations;
	}

	@Override
	public String toString() {
		return "Localizations: " + getLocalizations();
	}
}