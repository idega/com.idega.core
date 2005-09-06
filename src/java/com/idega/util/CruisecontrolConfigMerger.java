/*
 * Created on 14.7.2004
 * 
 * TODO To change the template for this generated file go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
package com.idega.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Iterator;
import com.idega.xml.XMLDocument;
import com.idega.xml.XMLElement;
import com.idega.xml.XMLException;
import com.idega.xml.XMLOutput;
import com.idega.xml.XMLParser;

/**
 * A Class to merge contents of many config.xml files for cruisecontrol and merges their projects into a single
 * outputfile.
 * 
 * @author tryggvil
 * 
 */
public class CruisecontrolConfigMerger extends BundleFileMerger {

	public CruisecontrolConfigMerger() {
		String xmlHeader = "<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>\n";
		setFileHeader(xmlHeader);
		setRootXMLElement("cruisecontrol");
		setBundleFilePath("/cruisecontrol.xml");
	}

	protected void processFileMerge() {
		try {
			XMLDocument outDoc = new XMLDocument(new XMLElement(getRootXMLElement()));
			XMLParser parser = new XMLParser(false);
			Iterator moduleIter = getMergeInSources().iterator();
			while (moduleIter.hasNext()) {
				ModuleFile module = (ModuleFile) moduleIter.next();
				// Not include the module part again
				if (!module.isHasBeenProcessed()) {
					try {
						XMLDocument inDoc = parser.parse(module.getSourcefile());
						XMLElement projectElement = inDoc.getRootElement().getChild("project");
						outDoc.getRootElement().addContent(projectElement);
					}
					catch (XMLException e) {
						e.printStackTrace();
					}
				}
			}
			OutputStream outStream = new FileOutputStream(getOutputFile());
			XMLOutput output = new XMLOutput("  ", true);
			output.setLineSeparator(System.getProperty("line.separator"));
			output.setTextNormalize(true);
			output.setEncoding("UTF-8");
			output.output(outDoc, outStream);
			outStream.close();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Test method
	 * 
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		//performCruisecontrolMavenTest();
		if(args.length<1){
			System.out.println("Usage: java CruisecontrolConfigMerger [outputfile] [input(bundle)Folder]");
		}
		else{
			String outputFile = args[0];
			String inputFolder = null;
			if(args.length>=2){
				inputFolder = args[1];
			}
			else{
				inputFolder = System.getProperty("user.dir");
			}
			execute(outputFile,inputFolder);
		}
	
	}

	/**
	 * Test method
	 * 
	 * @throws Exception
	 */
	public static void performCruisecontrolMavenTest() throws Exception {
		
		String sBundlesDir = "/idega/cruisecontrol/mergetest";
		String sToFile = "/idega/cruisecontrol/mergetest/cruisecontrol.xml";
		//String sBundlesDir = "/home/maven/bundles";
		//String sToFile = "/home/maven/cruisecontrol.xml";
		execute(sToFile, sBundlesDir);
	}

	/**
	 * <p>
	 * TODO tryggvil describe method execute
	 * </p>
	 * @param instance
	 * @param sBundlesDir
	 * @param sToFile
	 * @throws IOException
	 */
	private static void execute(String sToFile,String sBundlesDir) throws IOException {
		BundleFileMerger instance = new CruisecontrolConfigMerger();
		File bundlesDir = new File(sBundlesDir);
		instance.setBundlesFolder(bundlesDir);
		File toFile = new File(sToFile);
		if (!toFile.exists()) {
			toFile.createNewFile();
		}
		instance.setOutputFile(toFile);
		instance.process();
	}
}
