package com.idega.core.persistence.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.orm.jpa.persistenceunit.MutablePersistenceUnitInfo;
import org.springframework.orm.jpa.persistenceunit.PersistenceUnitPostProcessor;

import com.idega.util.CoreConstants;

/**
 * @author <a href="mailto:civilis@idega.com">Vytautas Čivilis</a>
 * @version $Revision: 1.2 $
 *
 * Last modified: $Date: 2008/01/06 16:57:38 $ by $Author: civilis $
 */
public class OrmPathPatternMatchingResolver implements PersistenceUnitPostProcessor {
	
	private ResourcePatternResolver resourceResolver;
	public String CLASSPATH_ALL_URL_PREFIX = "classpath*:";
	public String CLASSPATH_URL_PREFIX = "classpath:";
	
	protected String resolveRelativePathPrefix(String path) {
		
		path = 	path.startsWith(CLASSPATH_ALL_URL_PREFIX) ? path.substring(CLASSPATH_ALL_URL_PREFIX.length()) :
				path.startsWith(CLASSPATH_URL_PREFIX) ? path.substring(CLASSPATH_URL_PREFIX.length()) :
				path;
		
		if(path.contains(CoreConstants.STAR))
			path = path.substring(0, path.indexOf(CoreConstants.STAR));
		
		return path;
	}
	
	protected String resolveMappingInClasspath(Resource resource, String relativePathPrefix) throws IOException {
		
		String resourceUrlPath = resource.getURL().getPath();
		String mappingInClasspath = resourceUrlPath.substring(resourceUrlPath.lastIndexOf(relativePathPrefix));
		
		return mappingInClasspath;
	}

	public void postProcessPersistenceUnitInfo(MutablePersistenceUnitInfo info) {

		if(info.getMappingFileNames() == null)
			return;
		
		try {
			Set<String> toAdd = new HashSet<String>();
			ArrayList<String> toRemove = new ArrayList<String>();
			
			for (String fileName : info.getMappingFileNames()) {
				
				if(fileName.contains(CoreConstants.STAR) || fileName.startsWith(CLASSPATH_URL_PREFIX)) {

					toRemove.add(fileName);
					String relativePrefix = resolveRelativePathPrefix(fileName);
					
					Resource[] resources = getResourceResolver().getResources(fileName);
					
					for (int i = 0; i < resources.length; i++) {
						
						String mappingInClasspath = resolveMappingInClasspath(resources[i], relativePrefix);
						toAdd.add(mappingInClasspath);
					}
				}
			}
			
			info.getMappingFileNames().removeAll(toRemove);
			info.getMappingFileNames().addAll(toAdd);
			
//			shameless hack
			String toIncludeFirstTempHack = "org/jbpm/db/hibernate.queries.hbm.xml";
			
			if(info.getMappingFileNames().contains(toIncludeFirstTempHack)) {
			
				info.getMappingFileNames().remove(toIncludeFirstTempHack);
				info.getMappingFileNames().add(0, toIncludeFirstTempHack);
			}
			
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public ResourcePatternResolver getResourceResolver() {
		return resourceResolver;
	}

	public void setResourceResolver(ResourcePatternResolver resourceResolver) {
		this.resourceResolver = resourceResolver;
	}
}