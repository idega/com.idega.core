/*
 * Created on 22.6.2004
 *
 */
package com.idega.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


/**
 * @author tryggvil
 * 
 * This class is UNFINISHED<br>
 *
 * A class to merge files from many similar sources to a single file. e.g. to merge web.xml files from many sources into one.<br>
 * The file generates markers around what it merges and can use that to re-merge if changes occur.<br>
 * The merged file will look something like this:<br>
 * <br>
 * <!-- Generated file by idegaWeb please don't modify markers -->
 * <rootxml>
 * <!-- MODULE:BEGIN com.idega.core 1.0 -->
 *	<somexmltag>... </somexmltag>
 * <!-- MODULE:END com.idega.core 1.0 -->
 * </rootxml>
 * 
 */
public class ModuleFileMerger {

	private Reader input;
	private File inputFile;
	private Writer output;
	private File outputFile;
	private String rootXMLElement="web-app";
	private List patterns;
	//Sources of input Files:
	private List sources;
	private static String SLASH="/";
	
	/**
	 * @return Returns the rootXMLElement.
	 */
	public String getRootXMLElement() {
		return rootXMLElement;
	}
	/**
	 * @param rootXMLElement The rootXMLElement to set.
	 */
	public void setRootXMLElement(String rootXMLElement) {
		this.rootXMLElement = rootXMLElement;
	}
	/**
	 * @return Returns the inputFile.
	 */
	public File getInputFile() {
		return inputFile;
	}
	/**
	 * @param inputFile The inputFile to set.
	 */
	public void setInputFile(File inputFile) {
		try {
			setInput(new FileReader(inputFile));
		} catch (IOException e) {
			e.printStackTrace();
		}		
		this.inputFile = inputFile;
	}
	/**
	 * @return Returns the outputFile.
	 */
	public File getOutputFile() {
		return outputFile;
	}
	/**
	 * @param outputFile The outputFile to set.
	 */
	public void setOutputFile(File outputFile) {
		try {
			setOutput(new FileWriter(outputFile));
		} catch (IOException e) {
			e.printStackTrace();
		}
		this.outputFile = outputFile;
	}
	/**
	 * @return Returns the sources.
	 */
	public List getSources() {
		if(sources==null){
			sources = new ArrayList();
		}
		return sources;
	}
	/**
	 * @param sources The sources to set.
	 */
	public void setSources(List sources) {
		this.sources = sources;
	}
	
	public void addSourceFile(File sourceFile,String moduleIdentifier){
		getSources().add(new ModuleFile(getRootXMLElement(),sourceFile,moduleIdentifier));
	}
	
	public void addSourceFile(File sourceFile,String moduleIdentifier,String moduleVersion){
		getSources().add(new ModuleFile(this.getRootXMLElement(),sourceFile,moduleIdentifier,moduleVersion));
	}	
		
	
	/*
	 * 
	public List getPatterns() {
		if(patterns==null){
			patterns = new ArrayList();
			Pattern p1 = Pattern.compile("(<a[^>]+href=\")([^#][^\"]+)([^>]+>)",Pattern.CASE_INSENSITIVE);
			patterns.add(p1);
			Pattern p2 = Pattern.compile("(<link[^>]+href=\")([^#][^\"]+)([^>]+>)",Pattern.CASE_INSENSITIVE);
			patterns.add(p2);
			Pattern p3 = Pattern.compile("(<img[^>]+src=\")([^#][^\"]+)([^>]+>)",Pattern.CASE_INSENSITIVE);
			patterns.add(p3);
			Pattern p4 = Pattern.compile("(<script[^>]+src=\")([^#][^\"]+)([^>]+>)",Pattern.CASE_INSENSITIVE);
			patterns.add(p4);
			Pattern p5 = Pattern.compile("(<input[^>]+src=\")([^#][^\"]+)([^>]+>)",Pattern.CASE_INSENSITIVE);
			patterns.add(p5);
			Pattern p6 = Pattern.compile("(<form[^>]+action=\")([^#][^\"]+)([^>]+>)",Pattern.CASE_INSENSITIVE);
			patterns.add(p6);
			Pattern p7 = Pattern.compile("(<embed[^>]+src=\")([^#][^\"]+)([^>]+>)",Pattern.CASE_INSENSITIVE);
			patterns.add(p7);

		}
		return patterns;
	}
	*/
	/**
	 * @param patterns The patterns to set.
	 */
	public void setPatterns(List patterns) {
		this.patterns = patterns;
	}
	public static void main(String[] args) throws Exception{
		performWebXmlsTest();
	}

	
	public static void performWebXmlsTest()throws Exception{
		
		ModuleFileMerger instance = new ModuleFileMerger();
		
		String sBundlesDir = "/idega/eclipse/maven/bundles";
		File bundlesDir = new File(sBundlesDir);
		
		File[] bundles = bundlesDir.listFiles();
		for (int i = 0; i < bundles.length; i++) {
			File bundle = bundles[i];
			String path = bundle.getAbsolutePath();
			String sWebXml =path+"/WEB-INF/web.xml";
			File webXml = new File(sWebXml);
			if(webXml.exists()){
				String bundleFolderName = bundle.getName();
				String bundleId = null;
				if(bundleFolderName.endsWith(".bundle")){
					bundleId = bundleFolderName.substring(0,bundleFolderName.indexOf(".bundle"));
				}
				else{
					bundleId=bundleFolderName;
				}
				instance.addSourceFile(webXml,bundleId);
			}
		}
		
		
		String sFromFile = "/tmp/web.xml";
		String sToFile = "/tmp/web.xml";
		File toFile = new File(sToFile);
		if(!toFile.exists()){
			toFile.createNewFile();
		}
		File fromFile = new File(sFromFile);
		if(fromFile.exists()){
			FileReader reader = new FileReader(fromFile);
			Reader input = new BufferedReader(reader);
			instance.setInput(input);
		}
		
		FileWriter output = new FileWriter(toFile);
		
		instance.setOutput(output);
		instance.process();
	}
	

	/**
	 * Execute the processing. Read the input, search/replace and write to the output.
	 * This method should be called last, after all set methods are called.
	 */
	public void process() {
		
		Reader reader = getInput();
		StringBuffer sb = new StringBuffer();
		int buffersize = 1000;
		char[] buffer = new char[buffersize];
		try {
			int read = reader.read(buffer);
			while(read!=-1){
				sb.append(buffer,0,read);
				read = reader.read(buffer);
			}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		StringBuffer outString = new StringBuffer();
		Iterator moduleIter = getSources().iterator();
		//StringBuffer replaceBuffer = sb;
		
		outString.append("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>\n");
		outString.append("<!DOCTYPE web-app\n");
		outString.append("\tPUBLIC \"-//Sun Microsystems, Inc.//DTD Web Application 2.3//EN\"\n");
		outString.append("\thttp://java.sun.com/dtd/web-app_2_3.dtd\">\n");
		
		outString.append("<"+getRootXMLElement()+">\n");
		
		
		while (moduleIter.hasNext()) {
			ModuleFile module = (ModuleFile)moduleIter.next();
			String moduleId = module.getModuleIdentifier();
			String moduleVersion = module.getModuleVersion();
			File inputFile = module.getSourcefile();
			
			String modulePartBegin = "<!-- MODULE:BEGIN "+moduleId+" "+moduleVersion+" -->\n";
			String modulePartEnd = "<!-- MODULE:END "+moduleId+" "+moduleVersion+" -->\n";
			
			String moduleContents = module.getContentsWithinRootElement();
			
			outString.append(modulePartBegin);
			outString.append(moduleContents);
			outString.append(modulePartEnd);
			
			//replaceBuffer=new StringBuffer(outString.toString());
		}
		outString.append("\n</"+getRootXMLElement()+">");
		PrintWriter out = new PrintWriter(getOutput());
		out.write(outString.toString());
		out.close();
	}
	/**
	 * @return Returns the input.
	 */
	public Reader getInput() {
		return input;
	}
	/**
	 * Set the Input (file or stream)
	 * @param input The input to set.
	 */
	public void setInput(Reader input) {
		this.input = input;
	}
	/**
	 * @return Returns the output.
	 */
	public Writer getOutput() {
		return output;
	}
	/**
	 * Set the Output (file or stream) to write the rewritten HTML to.
	 * @param output The output to set.
	 */
	public void setOutput(Writer output) {
		this.output = output;
	}

	
	
	private class ModuleFile{
		/**
		 * @param sourceFile2
		 * @param moduleIdentifier2
		 */
		public ModuleFile(String xmlRootElement,File sourceFile2, String moduleIdentifier2) {
			setRootXMLElement(xmlRootElement);
			setSourcefile(sourceFile2);
			setModuleIdentifier(moduleIdentifier2);
		}
		/**
		 * @param sourceFile2
		 * @param moduleIdentifier2
		 * @param moduleVersion2
		 */
		public ModuleFile(String xmlRootElement,File sourceFile2, String moduleIdentifier2, String moduleVersion2) {
			setRootXMLElement(xmlRootElement);
			setSourcefile(sourceFile2);
			setModuleIdentifier(moduleIdentifier2);
			setModuleVersion(moduleVersion2);
		}
		private File sourcefile;
		/**
		 * @return Returns the moduleIdentifier.
		 */
		public String getModuleIdentifier() {
			return moduleIdentifier;
		}
		/**
		 * @param moduleIdentifier The moduleIdentifier to set.
		 */
		public void setModuleIdentifier(String moduleIdentifier) {
			this.moduleIdentifier = moduleIdentifier;
		}
		/**
		 * @return Returns the moduleVersion.
		 */
		public String getModuleVersion() {
			return moduleVersion;
		}
		/**
		 * @param moduleVersion The moduleVersion to set.
		 */
		public void setModuleVersion(String moduleVersion) {
			this.moduleVersion = moduleVersion;
		}
		/**
		 * @return Returns the reader.
		 */
		public Reader getReader() {
			return reader;
		}
		/**
		 * @param reader The reader to set.
		 */
		public void setReader(Reader reader) {
			this.reader = reader;
		}
		/**
		 * @return Returns the sourcefile.
		 */
		public File getSourcefile() {
			return sourcefile;
		}
		/**
		 * @param sourcefile The sourcefile to set.
		 */
		public void setSourcefile(File sourcefile) {
			try {
				setReader(new FileReader(sourcefile));
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			this.sourcefile = sourcefile;
		}
		private Reader reader;
		private String moduleIdentifier;
		private String moduleVersion="1.0";
		private String rootXmlElement;
		
		/**
		 * @return Returns the rootXmlElement.
		 */
		public String getRootXmlElement() {
			return rootXmlElement;
		}
		/**
		 * @param rootXmlElement The rootXmlElement to set.
		 */
		public void setRootXmlElement(String rootXmlElement) {
			this.rootXmlElement = rootXmlElement;
		}
		
		public String getFileContents(){
			Reader reader = getReader();
			StringBuffer sb = new StringBuffer();
			int buffersize = 1000;
			char[] buffer = new char[buffersize];
			try {
				int read = reader.read(buffer);
				while(read!=-1){
					sb.append(buffer,0,read);
					read = reader.read(buffer);
				}
				reader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return sb.toString();
		}
		
		public String getContentsWithinRootElement(){
			//Pattern p = Pattern.compile("(<a[^>]+href=\")([^#][^\"]+)([^>]+>)",Pattern.CASE_INSENSITIVE);
			String fileContents = getFileContents();
			String split1[] = fileContents.split("<"+getRootXMLElement()+">");
			String remaining = split1[1];
			String remainingsplit[] = remaining.split("</"+getRootXMLElement()+">");
			String contents = remainingsplit[0];
			//Pattern p = Pattern.compile("<"+getRootXMLElement()+">()</"+getRootXMLElement()">",Pattern.CASE_INSENSITIVE);
			//Matcher m = p.matcher(replaceBuffer);
			return contents;
		}
	}
}
