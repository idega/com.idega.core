/**
 * Copyright 2007 Jordi Hernández Sellés
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 * 
 * 	http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language governing permissions
 * and limitations under the License.
 */
package com.idega.util.resources;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;

import com.idega.idegaweb.include.ExternalLink;
import com.idega.idegaweb.include.StyleSheetLink;
import com.idega.util.CoreConstants;
import com.idega.util.StringHandler;
import com.idega.util.StringUtil;
import com.idega.util.expression.ELUtil;

/**
 * Minifies CSS files by removing expendable whitespace and comments. 
 * 
 * @author Valdas Žemaitis
 *
 */
public class CSSMinifier implements AbstractMinifier {

	// This regex captures comments
	private static final String commentRegex ="(/\\*[^*]*\\*+([^/][^*]*\\*+)*/)"; 
	
	// Captures CSS strings
	private static final String quotedContentRegex = "('(\\\\'|[^'])*')|(\"(\\\\\"|[^\"])*\")";
	
	// A placeholder string to replace and restore CSS strings
	private static final String STRING_PLACEHOLDER = "______'COMPRESSED_CSS'______";
	
	// Captured CSS rules (requires replacing CSS strings with a placeholder, or quoted braces will fool it.  
	private static final String rulesRegex = "([^\\{\\}]*)(\\{[^\\{\\}]*})";

	// Captures newlines and tabs
	private static final String newlinesTabsRegex = "\\r|\\n|\\t|\\f";
	
	// This regex captures, in order: 
	//(\\s*\\{\\s*)|(\\s*\\}\\s*)|(\\s*\\(\\s*)|(\\s*;\\s*)|(\\s*\\))
	// 			brackets, parentheses,colons and semicolons and any spaces around them (except spaces AFTER a parentheses closing symbol), 
	//and ( +) occurrences of one or more spaces. 
	private static final String spacesRegex = "(\\s*\\{\\s*)|(\\s*\\}\\s*)|(\\s*\\(\\s*)|(\\s*;\\s*)|(\\s*:\\s*)|(\\s*\\))|( +)";
	
	private static final Pattern commentsPattern = Pattern.compile(commentRegex, Pattern.DOTALL);
	private static final Pattern spacesPattern = Pattern.compile(spacesRegex, Pattern.DOTALL);
	
	private static final Pattern quotedContentPattern = Pattern.compile(quotedContentRegex, Pattern.DOTALL);
	private static final Pattern rulesPattern = Pattern.compile(rulesRegex, Pattern.DOTALL);
	protected static final Pattern newlinesTabsPattern = Pattern.compile(newlinesTabsRegex, Pattern.DOTALL);
	private static final Pattern stringPlaceholderPattern = Pattern.compile(STRING_PLACEHOLDER, Pattern.DOTALL);
	
	
	private static final String SPACE = " ";
	private static final String BRACKET_OPEN = "{";
	private static final String BRACKET_CLOSE = "}";
	private static final String PAREN_OPEN = "(";
	private static final String PAREN_CLOSE = ")";

	private static final String COLON = ":";
	private static final String SEMICOLON = ";";
	
	@Autowired
	private ResourceScanner resourceScanner;
	
	/**
	 * Template class to abstract the pattern of iterating over a Matcher and performing 
	 * string replacement. 
	 */
	public abstract class MatcherProcessorCallback {
		String processWithMatcher(Matcher matcher){
			StringBuffer sb = new StringBuffer();
			while(matcher.find()){
				matcher.appendReplacement(sb, matchCallback(matcher));
			}
			matcher.appendTail(sb);
			return sb.toString();
		}
		abstract String matchCallback(Matcher matcher);
	}
	
	/**
	 * @param data CSS to minify
	 * @return StringBuffer Minified CSS. 
	 */
	public StringBuffer minifyCSS(StringBuffer data) {
		// Remove comments and carriage returns
		String compressed = commentsPattern.matcher(data.toString()).replaceAll("");

		// Temporarily replace the strings with a placeholder
		final List<String> strings = new ArrayList<String>();		
		Matcher stringMatcher = quotedContentPattern.matcher(compressed);
		compressed = new MatcherProcessorCallback(){
			@Override
			String matchCallback(Matcher matcher) {
				String match = matcher.group();
				strings.add(match);
				return STRING_PLACEHOLDER;				
			}}.processWithMatcher(stringMatcher);
		
		// Grab all rules and replace whitespace in selectors
		Matcher rulesmatcher = rulesPattern.matcher(compressed);
		compressed = new MatcherProcessorCallback(){
			@Override
			String matchCallback(Matcher matcher) {
				String match = matcher.group(1);
				String spaced = newlinesTabsPattern.matcher(match.toString()).replaceAll(SPACE).trim();
				return spaced + matcher.group(2);	
			}}.processWithMatcher(rulesmatcher);
		
		// Replace all linefeeds and tabs
		compressed = newlinesTabsPattern.matcher(compressed).replaceAll("");
		
		// Match all empty space we can minify 
		Matcher matcher = spacesPattern.matcher(compressed);
		compressed = new MatcherProcessorCallback(){
			@Override
			String matchCallback(Matcher matcher) {
				String replacement = SPACE;
				String match = matcher.group();
		
				if(match.indexOf(BRACKET_OPEN) != -1)
					replacement = BRACKET_OPEN;
				else if(match.indexOf(BRACKET_CLOSE) != -1)
					replacement = BRACKET_CLOSE;
				else if(match.indexOf(PAREN_OPEN) != -1)
					replacement = PAREN_OPEN;
				else if(match.indexOf(COLON) != -1)
					replacement = COLON;
				else if(match.indexOf(SEMICOLON) != -1)
					replacement = SEMICOLON;
				else if(match.indexOf(PAREN_CLOSE) != -1)
					replacement = PAREN_CLOSE;
		
				return replacement;
			}}.processWithMatcher(matcher);

		// Restore all Strings
		Matcher restoreMatcher = stringPlaceholderPattern.matcher(compressed);		
		final Iterator<String> it = strings.iterator();
		compressed = new MatcherProcessorCallback(){
			@Override
			String matchCallback(Matcher matcher) {
				return it.next();	
			}}.processWithMatcher(restoreMatcher);	
			
				
		return new StringBuffer(compressed);
	}

	private String getParsedContent(String resourceUri, String content) {
		if (!resourceUri.startsWith(CoreConstants.SLASH)) {
			resourceUri = CoreConstants.SLASH + resourceUri;
		}
		return getResourceScanner().getParsedContent(StringUtil.getLinesFromString(content),
				resourceUri.substring(0, resourceUri.lastIndexOf(CoreConstants.SLASH) + 1));
	}
	
	private String getMinifiedResource(String resourceUri, String content, String media) {
		if (StringUtil.isEmpty(content)) {
			return null;
		}
		
		StringBuilder minified = new StringBuilder("\n/***************** STARTS: ").append(resourceUri).append(" *****************/\n");
		
		boolean mediaIsEmpty = StringUtil.isEmpty(media);
		if (!mediaIsEmpty) {
			minified.append("@media ").append(media).append(" {\n");
		}

		minified.append(getParsedContent(resourceUri, content));
		
		if (!mediaIsEmpty) {
			minified.append("\n}");
		}
		
		minified.append("\n/***************** ENDS: ").append(resourceUri).append(" *****************/\n").toString();
		return minified.toString();
	}

	public String getMinifiedResource(ExternalLink resource) {
		if (!(resource instanceof StyleSheetLink)) {
			Logger.getLogger(getClass().getName()).warning("Resource " + resource + " is not type of " + StyleSheetLink.class);
			return null;
		}
		
		StyleSheetLink cssResource = (StyleSheetLink) resource;
		try {
			return getMinifiedResource(cssResource.getUrl(), StringUtil.isEmpty(resource.getContent()) ?
					StringHandler.getContentFromInputStream(cssResource.getContentStream()) : resource.getContent(),
			cssResource.getMedia());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}

	public ResourceScanner getResourceScanner() {
		if (resourceScanner == null) {
			ELUtil.getInstance().autowire(this);
		}
		return resourceScanner;
	}

	public void setResourceScanner(ResourceScanner resourceScanner) {
		this.resourceScanner = resourceScanner;
	}

}
