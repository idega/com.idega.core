package com.idega.core.file.util;

import java.io.File;
import java.io.InputStream;
import java.net.URI;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.idega.core.test.base.IdegaBaseTest;

/**
 * @author <a href="mailto:civilis@idega.com">Vytautas Čivilis</a>
 * @version $Revision: 1.1 $
 *
 * Last modified: $Date: 2008/06/28 19:05:49 $ by $Author: civilis $
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
public final class FileURIHandlerFactoryTest extends IdegaBaseTest {
	
	@Autowired
	private FileURIHandlerFactory fileURIHandlerFactory;
	
	private URI fileURI;
	private String fileName = "FileURIHandlerFactoryTest-context.xml";
	
	@Override
	@Before
	public void setUp() throws Exception {
		
		ClassPathResource cpr = new ClassPathResource("FileURIHandlerFactoryTest-context.xml", getClass());
		File f = cpr.getFile();
		fileURI = f.toURI();
	}

	@Test
	public void testResolveFSHandler() throws Exception {
		
		FileURIHandler handler = fileURIHandlerFactory.getHandler(fileURI);
		
		assertNotNull(handler);
		assertTrue(handler instanceof FilesystemFileURIHandler);
	}
	
	@Test
	public void testResolveFileName() throws Exception {
		
		FileURIHandler handler = fileURIHandlerFactory.getHandler(fileURI);
		
		String resolvedFileName = handler.getFileInfo(fileURI).getFileName();
		assertEquals(fileName, resolvedFileName);
	}
	
	@Test
	public void testResolveFileInputStream() throws Exception {
		
		FileURIHandler handler = fileURIHandlerFactory.getHandler(fileURI);
		
		InputStream is = handler.getFile(fileURI);
		
		try {
			assertNotNull(is);
			
		} finally {
			
			if(is != null)
				is.close();
		}
	}
}