/**
 *
 */
package com.idega.core.dao.impl;

import java.util.Collection;
import java.util.Set;
import java.util.TreeSet;

import javax.persistence.Query;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.idega.core.dao.ApplicationBindingDAO;
import com.idega.core.data.bean.ApplicationBinding;
import com.idega.core.persistence.impl.GenericDaoImpl;
import com.idega.util.StringHandler;

@Scope(BeanDefinition.SCOPE_SINGLETON)
@Repository("applicationBindingDAO")
@Transactional(readOnly = true)
public class ApplicationBindingDAOImpl extends GenericDaoImpl implements ApplicationBindingDAO {

	@Override
	public String get(String key) {
		return getKey(key);
	}

	@Override
	public String put(String key, String value) {
		return put(key, value, null);
	}

	@Override
	public String put(String key, String value, String type) {
		return putKeyValue(key, value, type);
	}

	@Override
	public String remove(String key) {
		return put(key, null);
	}

	@Override
	public Set<String> keySet() {
		Collection<ApplicationBinding> coll = getAllApplicationBindings();
		if (coll == null)
			return new TreeSet<String>();

		// we are keeping things simple, the list is not very large
		Set<String> keyList = new TreeSet<String>();
		for (ApplicationBinding binding : coll) {
			keyList.add(binding.getKey());
		}

		return keyList;
	}

	private String getKey(String key) {
		String shortKey = StringHandler.shortenToLength(key, ApplicationBinding.MAX_KEY_LENGTH);

		ApplicationBinding binding = getApplicationBinding(shortKey);
		if (binding != null) {
			return binding.getValue();
		}
		return null;
	}

	@Transactional(readOnly = false)
	private String putKeyValue(String key, String value, String type) {
		key = StringHandler.shortenToLength(key, ApplicationBinding.MAX_KEY_LENGTH);
		String oldValue = null;

		ApplicationBinding binding = getApplicationBinding(key);
		if (binding == null) {
			binding = createApplicationBinding(key, value, type);
			return binding.getValue();
		}

		oldValue = binding.getValue();
		if (value == null) {
			remove(binding);
		} else {
			binding.setValue(value);
			merge(binding);
		}

		return oldValue;
	}

	private ApplicationBinding getApplicationBinding(String key) {
		ApplicationBinding binding = find(ApplicationBinding.class, key);
		return binding;
	}

	@Transactional(readOnly = false)
	private ApplicationBinding createApplicationBinding(String key, String value, String type) {
		ApplicationBinding binding = getApplicationBinding(key);
		if (binding == null) {
			binding = new ApplicationBinding();
			binding.setKey(StringHandler.shortenToLength(key, ApplicationBinding.MAX_KEY_LENGTH));
		}
		binding.setValue(value);
		binding.setType(type);
		merge(binding);

		return binding;
	}

	@SuppressWarnings("unchecked")
	private Collection<ApplicationBinding> getAllApplicationBindings() {
		Query q = createNamedQuery("applicationBinding.findAll");
		return q.getResultList();
	}
}