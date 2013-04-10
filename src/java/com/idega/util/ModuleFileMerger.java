/*
 * Created on 22.6.2004
 *
 */
package com.idega.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringReader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * @author tryggvil
 *
 * A class to merge files from many similar sources to a single file. e.g. to merge web.xml files from many sources into one.<br>
 * The file generates markers around what it merges and can use that to re-merge if changes occur.<br>
 * The merged file will look something like this:<br>
 * <br>
 * <!-- Generated file by idegaWeb please don't modify module markers -->
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
	//Sources of input Files:
	private List<ModuleFile> sources;
	private Map<String, ModuleFile> moduleMap;
	private String fileHeader;
	private boolean removeOlderModules=true;
	private String initalRootContents=null;

	/**
	 * @return Returns the rootXMLElement.
	 */
	public String getRootXMLElement() {
		return this.rootXMLElement;
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
		return this.inputFile;
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
		return this.outputFile;
	}
	/**
	 * Sets the outputFile. This method creates the file if it does not already exist.
	 * If input (setInput()) is not set it sets the input file the same as the output file if it exists.
	 * @param outputFile The outputFile to set.
	 */
	public void setOutputFile(File outputFile) {
		try {
			if(!outputFile.exists()){
				outputFile.createNewFile();
			}
			else{
				if(this.input==null){
					//Read the contents of the outputFile (because it exists) to the input Reader
					StringBuffer fileAsStringBuffer = readIntoBuffer(new FileReader(outputFile));
					Reader input = new StringReader(fileAsStringBuffer.toString());
					setInput(input);
				}
			}
			setOutput(new FileWriter(outputFile));
		} catch (IOException e) {
			e.printStackTrace();
		}
		this.outputFile = outputFile;
	}
	/**
	 * @return Returns the sources.
	 */
	public List<ModuleFile> getMergeInSources() {
		if(this.sources==null){
			this.sources = new ArrayList<ModuleFile>();
		}
		return this.sources;
	}
	/**
	 * @param sources The sources to set.
	 */
	public void setMergeInSources(List<ModuleFile> sources) {
		this.sources = sources;
	}

	public void addMergeInSourceFile(File sourceFile,String moduleIdentifier){
		if (sourceFile != null && sourceFile.exists())
			getMergeInSources().add(new ModuleFile(getRootXMLElement(),sourceFile,moduleIdentifier));
	}

	public void addMergeInSourceFile(File sourceFile,String moduleIdentifier,String moduleVersion){
		if (sourceFile != null && sourceFile.exists())
			getMergeInSources().add(new ModuleFile(this.getRootXMLElement(),sourceFile,moduleIdentifier,moduleVersion));
	}

	/**
	 * Read the contents of the Reader to a StringBuffer
	 * @param reader a reader (from file e.g.)
	 * @return the contents read from the reader in a StringBuffer
	 */
	protected StringBuffer readIntoBuffer(Reader reader){
		StringBuffer outString = new StringBuffer();
		int buffersize = 1000;
		char[] buffer = new char[buffersize];
		try {
			int read = reader.read(buffer);
			while(read!=-1){
				outString.append(buffer,0,read);
				read = reader.read(buffer);
			}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return outString;
	}

	/**
	 * <p>
	 * Calls first preProcess() and then processFileMerge();
	 * </p>
	 */
	public void process() {
		preProcess();
		processFileMerge();
	}

	/**
	 * <p>
	 * Does all processing before processFileMerge() is called.
	 * Called first from process();
	 * </p>
	 */
	protected void preProcess(){
		buildMapfromModules();
	}

	/**
	 * Execute the processing. Read the input files, search/replace and write to the output.
	 * This method should be called last, after all set methods are called.
	 */
	protected void processFileMerge(){
		StringBuffer outString = new StringBuffer();
		StringBuffer inString = new StringBuffer();

		Reader reader = getInput();
		if(reader!=null){
			inString = readIntoBuffer(reader);
			processContents(inString,outString);
		}
		else{
			String fileHeader = getFileHeader();
			if(fileHeader!=null){
				outString.append(fileHeader);
			}
			outString.append("<"+getRootXMLElement()+">\n");
		}


		String initialRootContents = getInitalRootContents();
		if(initialRootContents!=null){
			outString.append(initialRootContents+"\n");
		}


		for (Iterator<ModuleFile> moduleIter = getMergeInSources().iterator(); moduleIter.hasNext();) {
			ModuleFile module = moduleIter.next();
			//Not include the module part again
			if(!module.isHasBeenProcessed()){
				appendModulePartWithComments(module,outString);
			}

			//replaceBuffer=new StringBuffer(outString.toString());
		}
		outString.append("\n</"+getRootXMLElement()+">");

		PrintWriter out = new PrintWriter(getOutput());
		out.write(outString.toString());
		out.close();
	}

	protected void appendModulePartWithComments(ModuleFile module,StringBuffer outString){
		String moduleId = module.getModuleIdentifier();
		String moduleVersion = module.getModuleVersion();
		//File inputFile = module.getSourcefile();

		String modulePartBegin = "<!-- MODULE:BEGIN "+moduleId+" "+moduleVersion+" -->\n";
		String modulePartEnd = "<!-- MODULE:END "+moduleId+" "+moduleVersion+" -->\n";

		String moduleContents = module.getContentsWithinRootElement();

		outString.append(modulePartBegin);
		outString.append(moduleContents);
		outString.append(modulePartEnd);

		module.setHasBeenProcessed(true);
	}

	/**
	 * Process contents in the out source file (already existing and older module tags)
	 * @param inString
	 * @param outString
	 */
	protected void processContents(StringBuffer inString, StringBuffer outString) {
		//outString = new StringBuffer();
		StringBuffer semiOutBuffer = new StringBuffer();
		Pattern moduleBeginPattern = Pattern.compile("<!-- MODULE:BEGIN ([\\S]+)\\s([\\S]+)\\s[^\\n\\r]+",Pattern.CASE_INSENSITIVE);
		Matcher moduleBeginMatcher = moduleBeginPattern.matcher(inString);

		//StringBuffer remainder = new StringBuffer();
		//remainder.append(inString);
		StringBuffer remainder = null;

		while (moduleBeginMatcher.find()) {
			// this pattern matches.
			String moduleId = moduleBeginMatcher.group(1);
			String version = moduleBeginMatcher.group(2);
			StringBuffer oldModuleContents = new StringBuffer();
			oldModuleContents.append(moduleBeginMatcher.group(0));
			moduleBeginMatcher.appendReplacement(semiOutBuffer,"");

			remainder = new StringBuffer();
			moduleBeginMatcher.appendTail(remainder);

			String regexString = "<!-- MODULE:END "+getRegExEscaped(moduleId)+" "+getRegExEscaped(version)+"[^\\n\\r]+";
			Pattern moduleEndPattern = Pattern.compile(regexString,Pattern.CASE_INSENSITIVE);
			Matcher moduleEndMatcher = moduleEndPattern.matcher(remainder);



			ModuleFile module = this.getModuleMap().get(moduleId);

			moduleEndMatcher.find();
			//This must work, i.e. find() must return a result, otherwise the file is corrupt
			moduleEndMatcher.appendReplacement(oldModuleContents,"$0");
			//Remainder is only whats left after the MODULE:END tag
			remainder = new StringBuffer();
			moduleEndMatcher.appendTail(remainder);
			//Begin from where the last module tag ended for the next iteration:
			moduleBeginMatcher = moduleBeginPattern.matcher(remainder);

			if(module!=null){
				appendModulePartWithComments(module,semiOutBuffer);
			}
			else{
				//Module not found in new sources
				if(getIfRemoveOlderModules()){
					//Do nothing
				}
				else{
					semiOutBuffer.append(oldModuleContents);
				}
			}


		}
		moduleBeginMatcher.appendTail(semiOutBuffer);
		//Cut </web-app> off the ending:
		//replaceBuffer=new StringBuffer(outString.toString());
		String out = semiOutBuffer.toString();

		outString.append(out.substring(0,out.lastIndexOf("</"+getRootXMLElement()+">")));
	}
	/**
	 * Gets if to remove older module parts found
	 * in the source file but not found in the sources.
	 * Default is true.
	 * @return
	 */
	private boolean getIfRemoveOlderModules() {
		// TODO Auto-generated method stub
		return this.removeOlderModules;
	}
	/**
	 * Sets if to remove older module parts found
	 * in the source file but not found in the sources.
	 * @return
	 */
	public void setIfRemoveOlderModules(boolean value){
		this.removeOlderModules=value;
	}
	/**
	 *
	 */
	protected void buildMapfromModules() {
		for (Iterator<ModuleFile> moduleIter = getMergeInSources().iterator(); moduleIter.hasNext();) {
			ModuleFile module = moduleIter.next();
			String moduleId = module.getModuleIdentifier();
			Map<String, ModuleFile> moduleMap = getModuleMap();
			moduleMap.put(moduleId, module);
		}
	}
	/**
	 * @return
	 */
	private Map<String, ModuleFile> getModuleMap() {
		if(this.moduleMap==null){
			this.moduleMap=new HashMap<String, ModuleFile>();
		}
		return this.moduleMap;
	}
	/**
	 * @return Returns the input.
	 */
	public Reader getInput() {
		return this.input;
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
		return this.output;
	}
	/**
	 * Set the Output (file or stream) to write the rewritten HTML to.
	 * @param output The output to set.
	 */
	public void setOutput(Writer output) {
		this.output = output;
	}

	/**
	 * Gets the file Header (doctype) if it is generated
	 * @return
	 */
	public String getFileHeader(){
		return this.fileHeader;
	}

	public void setFileHeader(String fileHeader){
		this.fileHeader=fileHeader;
	}

	/**
	 * Escapes characters from the string that are reserved in regular expressions (with backslashes)
	 * @param inString
	 * @return
	 */
	public String getRegExEscaped(String inString){
		StringBuffer out = new StringBuffer();
		char[] charArray = inString.toCharArray();
		for (int i = 0; i < charArray.length; i++) {
			char character = charArray[i];
			//TODO: Handle more special regex characters
			switch (character) {
			case '.':
				out.append("\\.");
				break;
			case '$':
				out.append("\\$");
				break;
			case '[':
				out.append("\\[");
				break;
			case ']':
				out.append("\\]");
				break;
			case '(':
				out.append("\\(");
				break;
			case ')':
				out.append("\\)");
				break;
			default:
				out.append(character);
				break;
			}
		}
		return out.toString();
	}


	public class ModuleFile{
		private Reader reader;
		private String moduleIdentifier;
		private String moduleVersion="1.0";
		private String rootXmlElement;
		private File sourcefile;
		private boolean hasBeenProcessed=false;
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
		/**
		 * @return Returns the moduleIdentifier.
		 */
		public String getModuleIdentifier() {
			return this.moduleIdentifier;
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
			return this.moduleVersion;
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
			return this.reader;
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
			return this.sourcefile;
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

		/**
		 * @return Returns the rootXmlElement.
		 */
		public String getRootXmlElement() {
			return this.rootXmlElement;
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
			String split1[] = fileContents.split("<"+getRootXMLElement()+"[^>]*>");
			String remaining = split1[1];
			String remainingsplit[] = remaining.split("</"+getRootXMLElement()+">");
			String contents = remainingsplit[0];
			//Pattern p = Pattern.compile("<"+getRootXMLElement()+">()</"+getRootXMLElement()">",Pattern.CASE_INSENSITIVE);
			//Matcher m = p.matcher(replaceBuffer);
			return contents;
		}
		/**
		 * @return Returns the hasBeenProcessed.
		 */
		public boolean isHasBeenProcessed() {
			return this.hasBeenProcessed;
		}
		/**
		 * @param hasBeenProcessed The hasBeenProcessed to set.
		 */
		public void setHasBeenProcessed(boolean hasBeenProcessed) {
			this.hasBeenProcessed = hasBeenProcessed;
		}

	}


	/**
	 * @return Returns the initalRootContents.
	 */
	public String getInitalRootContents() {
		return this.initalRootContents;
	}

	/**
	 * @param initalRootContents The initalRootContents to set.
	 */
	public void setInitalRootContents(String initalRootContents) {
		this.initalRootContents = initalRootContents;
	}
}
