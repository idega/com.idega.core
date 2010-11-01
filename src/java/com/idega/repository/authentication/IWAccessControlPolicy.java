package com.idega.repository.authentication;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jcr.security.AccessControlPolicy;

public class IWAccessControlPolicy implements AccessControlPolicy {

	private Map<String, List<String>> policies = new HashMap<String, List<String>>();

	public IWAccessControlPolicy(Map<String, List<String>> policies) {
		this.policies = policies;
	}

	public IWAccessControlPolicy(String principal, List<String> privilegies) {
		setPolicies(principal, privilegies);
	}

	private void setPolicies(String principal, List<String> privilegies) {
		if (principal == null || privilegies == null) {
			return;
		}

		List<String> priv = getPrivilegies(principal);
		priv.addAll(privilegies);
	}

	public void addPolicy(String principal, String privilege) {
		if (principal == null || privilege == null) {
			return;
		}

		List<String> privilegies = getPrivilegies(principal);
		privilegies.add(privilege);
	}

	private List<String> getPrivilegies(String principal) {
		List<String> privilegies = policies.get(principal);
		if (privilegies == null) {
			privilegies = new ArrayList<String>();
			policies.put(principal, privilegies);
		}
		return privilegies;
	}

	public Map<String, List<String>> getPolicies() {
		return policies;
	}
}