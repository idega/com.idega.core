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
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


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
	private String urlPrefix;
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
		HtmlReferenceRewriter instance = new HtmlReferenceRewriter();
		String fromFile = "/Users/tryggvil/idega/webapps/Reykjavik/rrvk-dtemplate.html";
		String toFile = "/Users/tryggvil/Documents/Reykjavik/rvktest.html";
		String urlPrefix = "http://www.rvk.is/";
		FileReader reader = new FileReader(fromFile);
		Reader input = new BufferedReader(reader);
		FileWriter output = new FileWriter(toFile);
		instance.setInput(input);
		instance.setOutput(output);
		instance.setUrlPrefix(urlPrefix);
		instance.setRewriteOptionValues(true);
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
		StringBuffer outString = null;
		Iterator moduleIter = getSources().iterator();
		StringBuffer replaceBuffer = sb;
		while (moduleIter.hasNext()) {
			outString = new StringBuffer();
			//ModuleFile module = (ModuleFile)moduleIter.next();
			//String moduleId = module.getModuleIdentifier();
			//Pattern p = Pattern.compile("(<a[^>]+href=\")([^#][^\"]+)([^>]+>)",Pattern.CASE_INSENSITIVE);
			Pattern p = Pattern.compile("(<>)",Pattern.CASE_INSENSITIVE);
			Matcher m = p.matcher(replaceBuffer);
			while (m.find()) {
				// this pattern matches.
				int groupCount = m.groupCount();
				for(int i=0;i<=	groupCount;i++){
					String s = m.group(i);
					System.out.println(s);
				}
				String url = m.group(2);
				if(getIfRewriteURL(url)){
					//if this is a relative url:
					m.appendReplacement(outString,"$1"+getRewrittenURL(url)+"$3");
				}				
				else{
					//Do not replace the url
					m.appendReplacement(outString,"$0");
				}

			}
			m.appendTail(outString);
			replaceBuffer=new StringBuffer(outString.toString());
		}
		PrintWriter out = new PrintWriter(getOutput());
		out.write(outString.toString());
		out.close();
	}
	/**
	 * Gets the rewritten URL. this can be overridden
	 */
	public String getRewrittenURL(String relativeURL){
		String urlPrefix = getUrlPrefix();
		if(relativeURL.startsWith(SLASH)&&urlPrefix.endsWith(SLASH)){
			return urlPrefix+relativeURL.substring(1,relativeURL.length());
		}
		else{
			return this.urlPrefix+relativeURL;
		}
	}
	
	/**
	 * Gets if th URL is appropriate to be rewritten<br>
	 * e.g. if it does not contain http:, javascript:,mailto: or # prefixes
	 * @param url the found url in the source
	 * @return
	 */
	public boolean getIfRewriteURL(String url){
		// not if it starts with these prefixes::
		return !(url.startsWith("http:")||url.startsWith("javascript:")||url.startsWith("mailto:")||url.startsWith("#"));
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
	/**
	 * Returns the set URLPrefix and appends a "/" to the end if it is not set.
	 * @return Returns the urlPrefix.
	 */
	public String getUrlPrefix() {
		if(!urlPrefix.endsWith(SLASH)){
			return urlPrefix+SLASH;
		}
		return urlPrefix;
	}
	/**
	 * @param urlPrefix The urlPrefix to set.
	 */
	public void setUrlPrefix(String urlPrefix) {
		this.urlPrefix = urlPrefix;
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
			String fileContents = getRootXMLElement();
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
