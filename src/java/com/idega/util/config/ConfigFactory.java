package com.idega.util.config;

import java.io.InputStream;
import java.net.URI;
import java.util.Map;

import com.idega.core.cache.IWCacheManager2;
import com.idega.idegaweb.IWMainApplication;
import com.idega.util.bundles.BundleResourceResolver;
import com.idega.util.bundles.ResourceResolver;

/**
 * 
 * @author <a href="mailto:civilis@idega.com">Vytautas Čivilis</a>
 * @version 1.0
 *
 */
public class ConfigFactory {
	
	private static ConfigFactory me;
	private IWMainApplication iwma;
	private Map<String, Config> configs;
	
	private static final String bundle_path_part1 = "bundle://";
	private static final String bundle_path_part2 = "/properties/";
	private static final String configs_cache_map = "ConfigFactory.configs_cache_map";

	public static ConfigFactory getInstance() {

		if (me == null) {
			
			synchronized (ConfigFactory.class) {
				if (me == null) {
					me = new ConfigFactory();
				}
			}
		}

		return me;
	}
	
	public Config getConfig(InputStream is) throws ConfigException {
		
		return Config.getInstance(is);
	}
	
	/**
	 * 
	 * loads up configuration file to the object.
	 * 
	 * This method guarantees that only one Config instance for each properties file exists
	 * 
	 * @param bundle_identifier - project - bundle where file exists
	 * @param property_file_name - file name of properties file - this file should exist in bundle's properties folder
	 * @return Object representing properties file
	 * @throws ConfigException - any kind of exception actually
	 */
	public Config getConfig(String bundle_identifier, String property_file_name) throws ConfigException {
		
		String config_uri_string = 
			new StringBuffer(bundle_path_part1)
			.append(bundle_identifier)
			.append(bundle_path_part2)
			.append(property_file_name)
			.toString();
		
		Map<String, Config> configs = getConfigs();
		
		if(configs.containsKey(config_uri_string))
			return configs.get(config_uri_string);
		
		synchronized (this) {
			
			if(configs.containsKey(config_uri_string))
				return configs.get(config_uri_string);
			
			Config cfg = null;
			
			try {
				URI config_uri = URI.create(config_uri_string);
				
				ResourceResolver resolver = new BundleResourceResolver(IWMainApplication.getDefaultIWMainApplication());
				InputStream is = resolver.resolve(config_uri).getInputStream();
				cfg = Config.getInstance(is);
				
			} catch (Exception e) {
				throw new ConfigException("Exception while resolving properties file from: "+config_uri_string, e);
			}

			if(cfg == null)
				throw new ConfigException("Couldn't resolve properties file from: "+config_uri_string);
			
			configs.put(config_uri_string, cfg);
			
			return cfg;
		}
	}
	
	protected Map<String, Config> getConfigs() {

		if(configs == null) {
			synchronized (this) {
				if(configs == null) {
					configs = IWCacheManager2.getInstance(getIWMainApplication()).getCache(configs_cache_map);
				}
			}
		}
		
		return configs;
	}
	
	protected IWMainApplication getIWMainApplication() {
		
		if(iwma == null)
			iwma = IWMainApplication.getDefaultIWMainApplication();
		
		return iwma;
	}
	
	
}