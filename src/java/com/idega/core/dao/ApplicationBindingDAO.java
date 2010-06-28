/**
 * 
 */
package com.idega.core.dao;

import java.util.Set;

import com.idega.business.SpringBeanName;
import com.idega.core.persistence.GenericDao;

@SpringBeanName("applicationBindingDAO")
public interface ApplicationBindingDAO extends GenericDao {

	public String get(String key);

	public Set<String> keySet();
	
	public String put(String key, String value);

	public String put(String key, String value, String type);

	public String remove(String key);

}