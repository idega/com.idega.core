package com.idega.presentation.ui;

import java.net.URL;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;
import java.net.URLEncoder;
import java.util.Enumeration;
import java.io.IOException;
import javax.servlet.RequestDispatcher;
import com.idega.presentation.PresentationObject;
import com.idega.presentation.Page;
import com.idega.presentation.IWContext;
import com.idega.util.Index;
import com.idega.util.IndexComparator;
import com.idega.util.FileUtil;
import com.idega.util.Index;
import com.idega.util.text.TextSoap;
import com.idega.builder.data.IBPage;
import com.idega.builder.business.BuilderLogic;

/**
 * A presentationObject that uses FileUtil.getStringFromURL to serverside include a given URL
*@author <a href="mailto:eiki@idega.is">Eirikur Hrafnsson</a>
*@version 1.0
*/
public class PageIncluder extends PresentationObject implements Index{
  private String URL = null;
  private String BASEURL = null;
  private String RELATIVEURL = null;
  private String pageIncluderPrefix = null;
  private String _label = null;
  private String _sendToLabel = null;
  private IBPage _sendToPage = null;
  private String _sendToPageIfSet = null;

  private String sessionId = null;
  private String sessionURL = null;
  private String token = null;
  private String tokenReplacer = null;
  private String serverName = null;

  private String out;
  private int index = 1000;

  private static final String PAGE_INCLUDER_PARAMETER_NAME="iw_uri_";
  private static final String PAGE_INCLUDER_SESSION_NAME="iw_session_token";
  private String pageIncluderFinalParamName = null;
  private int instanceId;
  private boolean forceFrame = false;
  private boolean useSecureLinks = false;

  private boolean changeURL = false;
  private final String symbol = "$";


  public PageIncluder(){
    super();
  }

  public PageIncluder(String URL){
    this();
    this.URL = URL;
  }

  private void sortAndProcess(IWContext iwc){
    //sort
    Page parent = this.getParentPage();/**@todo get in main**/
    List objects = parent.getAllContainedObjectsRecursive();
    ArrayList includers = new ArrayList();
    Iterator iter = objects.iterator();
    while (iter.hasNext()) {
      Object item = iter.next();
      if(item instanceof PageIncluder){
	includers.add(item);
      }
    }

    IndexComparator indexer = new IndexComparator(IndexComparator.ORDER_BY_INDEX);
    includers = indexer.sortedArrayList(includers);

    //process
    Iterator iter2 = includers.iterator();
    while (iter2.hasNext()) {
      PageIncluder item = (PageIncluder)iter2.next();
      try {
	item.process(iwc);
      }
      catch (IOException ex) {
	ex.printStackTrace(System.err);
      }
    }

  }

  public void setIndex(int index){
   this.index = index;
  }

  public int getIndex(){
   return index;
  }


  public void main(IWContext iwc) throws Exception {
    Page fromPage = this.getParentPage();
    instanceId=getICObjectInstanceID();
    changeURL = (iwc.isParameterSet(PAGE_INCLUDER_PARAMETER_NAME+_label)) || (iwc.isParameterSet(PAGE_INCLUDER_PARAMETER_NAME+instanceId));

    if( changeURL && (_sendToPage != null) && iwc.isParameterSet(_sendToPageIfSet) ) {//forwarding
      forwardToIBPage(fromPage,_sendToPage,iwc);
    }
    else if(out==null) sortAndProcess(iwc);//ususal

  }

  public void print(IWContext iwc)throws IOException{
    if(URL!=null){
      if(out!=null) println(out);
      out = null;
    }
  }

  protected void process(IWContext iwc)throws IOException{
    serverName = iwc.getServerName();
    instanceId=getICObjectInstanceID();
    changeURL = ((iwc.isParameterSet(PAGE_INCLUDER_PARAMETER_NAME+_label)) || (iwc.isParameterSet(PAGE_INCLUDER_PARAMETER_NAME+instanceId))) && !iwc.isParameterSet(_sendToPageIfSet);

	StringBuffer location = new StringBuffer();
	StringBuffer queryBuf = new StringBuffer();
	String query = null;

	if (forceFrame ) {

	  StringBuffer buf = new StringBuffer();
          String uri = iwc.getRequestURI();
          if( useSecureLinks ){
            buf.append("https://");
            buf.append(iwc.getServerName());
          }
	  buf.append(uri);
	  buf.append('?');

	  if( _sendToPage!=null ){


	    if ( (_sendToLabel != null) && (_sendToPageIfSet==null) ){
	      buf.append(getSendToPageURLString());
	      buf.append('&');
	      buf.append(PAGE_INCLUDER_PARAMETER_NAME);
	      buf.append(_sendToLabel);
	      buf.append('=');
	    }
	    else{
	      buf.append(getCurrentIBPageIDToURLString(iwc));
	      buf.append('&');
	      buf.append(PAGE_INCLUDER_PARAMETER_NAME);
	      buf.append(instanceId);
	      buf.append('=');
	    }
	  }
	  else{
	    buf.append(getCurrentIBPageIDToURLString(iwc));
	    buf.append('&');

	    if ( (_sendToLabel != null) && (_sendToPageIfSet==null) ){
	      buf.append(PAGE_INCLUDER_PARAMETER_NAME);
	      buf.append(_sendToLabel);
	      buf.append('=');
	    }
	    else{
	      buf.append(PAGE_INCLUDER_PARAMETER_NAME);
	      buf.append(instanceId);
	      buf.append('=');
	    }
	  }

	  pageIncluderPrefix = buf.toString();
	  System.out.println("PAGEINCLUDER PREFIX = "+pageIncluderPrefix);
      }
      else {
	pageIncluderPrefix ="";
      }

	//after clicking a link and submitting a form
	// check if the action is for this page includer
	if ( changeURL ) {
	  //System.out.println("Changing!");

	  //get all parameters even from post actions
	  Enumeration enum = iwc.getParameterNames();
	  while (enum.hasMoreElements()) {
	    String param = (String) enum.nextElement();
	    //debug(param+" : "+iwc.getParameter(param));

            if ( param.equals(PAGE_INCLUDER_PARAMETER_NAME+instanceId) || param.equals(PAGE_INCLUDER_PARAMETER_NAME+_label)  ){
	      URL = decodeQueryString(iwc.getParameter(param));
	      location.append(URL);
	      //System.out.println("Changing location to:"+location.toString());
	    }
	    else{
	      if (param.indexOf(PAGE_INCLUDER_PARAMETER_NAME) == -1) {
                String[] values = iwc.getParameterValues(param);
	        for (int i = 0; i < values.length; i++) {
                  queryBuf.append(param);
		  queryBuf.append("=");
		  queryBuf.append(URLEncoder.encode(iwc.getParameter(param)));
		  queryBuf.append("&");
                }
	      }
	    }
	  }//while ends

	  query = queryBuf.toString();

	  if( !query.equals("") ){
	    if(URL.endsWith("/")){//check if the url ends with a slash
	      location.append("?");
	    }
	    else{//no slash at end
	      if( URL.indexOf("?")==-1 ){//check if the url contains a ?
		if(URL.indexOf("/",8)!=-1){//check if the url contains a slash
		  location.append("?");
		}
		else{
		  location.append("/?");
		}
	      }
	      else{//just add to the parameters
		location.append("&");
	      }
	    }
	    //add the extra parameters
	    location.append(query);
	  }
	}//if iw_uri check ends
	else {//using starting state url
	  location.append(URL);
	}

	String loc = location.toString();
	//System.out.println("Loc = "+loc);

	if( (sessionURL!=null) && (token!=null) ){

	  if( sessionId==null ){
	    sessionId = (String) iwc.getSessionAttribute( PAGE_INCLUDER_SESSION_NAME );
	    if(sessionId==null){
	      sessionId = FileUtil.getStringFromURL(sessionURL);
	      //debug("Sessions id is : "+sessionId);
	    }
	  }

	  iwc.setSessionAttribute(PAGE_INCLUDER_SESSION_NAME, sessionId);

	  loc = TextSoap.findAndReplace(loc,token,sessionId);
	  loc = TextSoap.findAndCut(loc,"\r\n");
	  loc = TextSoap.findAndCut(loc,"\n");


	}
	else if( (sessionId!=null) && (token!=null)){
	  loc = TextSoap.findAndReplace(loc,token,sessionId);
	}

	//System.out.println("Location url is: "+loc+" and index is: "+index);




	if(loc!=null && !loc.equals("") ){
	  out = FileUtil.getStringFromURL(loc);

	  URL url = new URL(loc);
	  BASEURL = url.getProtocol()+"://"+url.getHost()+"/";
	  if(loc.lastIndexOf("/")==6) loc+="/";
	  RELATIVEURL = loc.substring(0,loc.lastIndexOf("/")+1);

	  /**
	   * @todo use expressions to make none case sensitive or implement using HTMLDocumentLoader (Advanced Swing);
	   * **/

	  out = TextSoap.stripHTMLTagAndChangeBodyTagToTable(out);
	  out = preProcess(out);
	  if( forceFrame ){
	    out = encodeQueryStrings(out);
	  }
	  out = changeSrcAttributes(out);
	  out = changeAHrefAttributes(out);
	  out = changeFormActionAttributes(out);
	  out = postProcess(out);
	}
  }


  protected String preProcess(String html){
    html = TextSoap.findAndReplace(html,"href=\"javascript","IW_PREPROCESSED");
    return html;
  }

  protected String postProcess(String html){
    html = TextSoap.findAndReplace(html,"IW_PREPROCESSED","href=\"javascript");
    return html;
  }

  protected String changeAHrefAttributes(String html){
    /*
      Possibilities	tags: ahref and action		src
      /xxx/xx		prefix+baseurl+/xxx/xx		baseurl+/xxx/xx
      xxx/xx		prefix+relativeurl+/+xxx/xx	relative+/+xxx/xx
      http://		prefix				ekkert
      //slashdot.org/ prefix+http:			http:
    */
    html = insertPageIncludeInTagIgnoreCase("href",html);
    return html;
  }


  protected String changeFormActionAttributes(String html){
    html = insertPageIncludeInTagIgnoreCase("action",html);
    return html;
  }

  protected String changeSrcAttributes(String html){
    html = changeURLToAbsoluteValueIgnoreCase("src",html);
    html = changeURLToAbsoluteValueIgnoreCase("background",html);
    return html;
  }

  protected String insertPageIncludeInTagIgnoreCase( String tag,String html){
    html = insertPageIncludeInTag(tag.toLowerCase(),html);
    html = insertPageIncludeInTag(tag.toUpperCase(),html);
    return html;
  }

  protected String insertPageIncludeInTag(String tag,String html){
    StringBuffer buf = new StringBuffer();
    buf.append(tag).append("=\"").append(pageIncluderPrefix).append("http://");
    String prefix = buf.toString();

    tag = tag+"=\"";

    html = TextSoap.findAndReplace(html,tag+"//",prefix);// the // case
    html = TextSoap.findAndReplace(html,tag+"http://",prefix);// the http:// case

    if(forceFrame){
      html = TextSoap.findAndReplace(html,tag+"/",pageIncluderPrefix.substring(1,pageIncluderPrefix.length()),tag+"/",tag+pageIncluderPrefix+BASEURL );// the / case
      /**@todo make this a boolean choice
       * special case. changes https to http
       */
      html = TextSoap.findAndReplace(html,tag+"https://",tag+"https://"+serverName+pageIncluderPrefix+"http://");// the http:// case
      html = TextSoap.findAndReplace(html,tag,pageIncluderPrefix,tag+pageIncluderPrefix+RELATIVEURL);
    }
    else{
      html = TextSoap.findAndReplace(html,tag+"/",tag+BASEURL);
      html = TextSoap.findAndReplace(html,tag,"http://",tag+RELATIVEURL);
    }
    return html;
  }

  protected String getCurrentIBPageIDToURLString(IWContext iwc){
    BuilderLogic bill = BuilderLogic.getInstance();
    return bill.IB_PAGE_PARAMETER+"="+bill.getCurrentIBPageID(iwc);
  }

  protected String getSendToPageURLString(){
    BuilderLogic bill = BuilderLogic.getInstance();
    return bill.IB_PAGE_PARAMETER+"="+_sendToPage.getID();
  }

  protected String changeURLToAbsoluteValueIgnoreCase(String tag,String html){
    html = changeURLToAbsoluteValue(tag.toLowerCase(),html);
    html = changeURLToAbsoluteValue(tag.toUpperCase(),html);
    return html;
  }

  protected String changeURLToAbsoluteValue(String tag,String html){
    html = TextSoap.findAndReplace(html,tag+"=\"//",tag+"=\""+"http://");// the // case
    html = TextSoap.findAndReplace(html,tag+"=\"/",tag+"=\""+BASEURL );// the / case
    html = TextSoap.findAndReplace(html,tag+"=\"","http://",tag+"=\""+RELATIVEURL);
    return html;
  }

  private String symbolReplace(String html, String tag){
    return TextSoap.findAndReplace(html,symbol+tag,"&"+tag);
  }

  protected String encodeQueryStrings(String html){
    html = TextSoap.findAndReplace(html,"&",symbol);
    //fixing this should be done with a HTMLEditor object OR
    //make a single general expression fix
    html = symbolReplace(html,"eth;");
    html = symbolReplace(html,"ETH;");
    html = symbolReplace(html,"thorn;");
    html = symbolReplace(html,"THORN;");
    html = symbolReplace(html,"aelig;");
    html = symbolReplace(html,"AElig;");
    html = symbolReplace(html,"ouml;");
    html = symbolReplace(html,"Ouml;");
    html = symbolReplace(html,"auml;");
    html = symbolReplace(html,"Auml;");
    html = symbolReplace(html,"euml;");
    html = symbolReplace(html,"Euml;");
    html = symbolReplace(html,"uuml;");
    html = symbolReplace(html,"Uuml;");

    html = symbolReplace(html,"nbsp;");
    html = symbolReplace(html,"amp;");
    html = symbolReplace(html,"quot;");
    html = symbolReplace(html,"middot");
    html = symbolReplace(html,"raquo;");
    html = symbolReplace(html,"#149;");
    html = symbolReplace(html,"#039;");
    html = symbolReplace(html,"#169;");
    html = symbolReplace(html,"#8211;");
    html = symbolReplace(html,"gt;");
    html = symbolReplace(html,"pound;");
    html = symbolReplace(html,"yen;");
    html = symbolReplace(html,"copy;");
    html = symbolReplace(html,"reg;");
    html = symbolReplace(html,"szlig;");
    html = symbolReplace(html,"#cedil;");
    html = symbolReplace(html,"ccedil;");
    html = symbolReplace(html,"Ccedil;");
    html = symbolReplace(html,"cedil;");
    html = symbolReplace(html,"oslash;");
    html = symbolReplace(html,"Oslash;");

    html = TextSoap.findAndReplace(html," "+symbol+" "," & ");

//islenskir broddstafir
    html = symbolReplace(html,"aacute;");
    html = symbolReplace(html,"Aacute;");
    html = symbolReplace(html,"eacute;");
    html = symbolReplace(html,"Eacute;");
    html = symbolReplace(html,"iacute;");
    html = symbolReplace(html,"Iacute;");
    html = symbolReplace(html,"uacute;");
    html = symbolReplace(html,"Uacute;");
    html = symbolReplace(html,"oacute;");
    html = symbolReplace(html,"Oacute;");
    html = symbolReplace(html,"yacute;");
    html = symbolReplace(html,"Yacute;");


  return html;
  }

  protected String decodeQueryString(String query){
   return TextSoap.findAndReplace(query,symbol,"&");
  }

  protected String copyJavaScript(String html,IWContext iwc){
    return html;
  }

  protected String copyIncludes(String html,IWContext iwc){
    return html;
  }

  public synchronized Object clone() {
    PageIncluder obj = null;
    try {
      obj = (PageIncluder)super.clone();
      obj.URL = this.URL;
      obj.BASEURL = this.BASEURL;
      obj.RELATIVEURL = this.RELATIVEURL;
      obj.pageIncluderPrefix = this.pageIncluderPrefix;
      obj.instanceId = this.instanceId;
      obj.sessionId = this.sessionId;
      obj.sessionURL = this.sessionURL;
      obj.token = this.token;
      obj.tokenReplacer = this.tokenReplacer;
      obj.out = this.out;
      obj.index = this.index;
    }
    catch(Exception ex) {
      ex.printStackTrace(System.err);
    }
    return obj;
  }

  public void setURL(String URL){
    this.URL = URL;
  }

  public void setSessionId(String sessionId){
    this.sessionId = sessionId;
  }

  public void setURLToGetSessionIDFrom(String sessionURL){
    this.sessionURL = sessionURL;
  }

  public void setTokenToReplaceWithSessionId(String token){
    this.token = token;
  }

  public void setForceInFrame(boolean forceFrame){
    this.forceFrame = forceFrame;
  }

  public void setKeepSecure(boolean useSecureLinks){
    this.useSecureLinks = useSecureLinks;
  }

  public void setLabel(String label) {
    _label = label;
  }

  /**
   * Redirects the URL from this PageIncluder to another PageIncluder with the
   * corresponding label.
   *
   * @param label The label of the PageIncluder which we want to redirect to.
   */
  public void setRedirectTo(String label) {
    _sendToLabel = label;
  }

  public String getLabel() {
    return _label;
  }

  public String getRedirectTo() {
    return _sendToLabel;
  }

  public void setSendToPage(IBPage page) {
    _sendToPage = page;
  }

  public IBPage getSendToPage() {
    return _sendToPage;
  }

  public void setSendToPageIfSet(String condition) {
    _sendToPageIfSet = condition;
  }

  public String getSendToPageIfSet() {
    return _sendToPageIfSet;
  }

  public void forwardToIBPage(Page fromPage ,IBPage page, IWContext iwc){
    StringBuffer URL = new StringBuffer();
    URL.append(BuilderLogic.getInstance().getIBPageURL(iwc.getApplicationContext(),((Integer)page.getPrimaryKeyValue()).intValue()));
    URL.append('&');
    String query = getRequest().getQueryString();
    if( _sendToLabel != null ){
      query = TextSoap.findAndReplace(query,PAGE_INCLUDER_PARAMETER_NAME+instanceId,PAGE_INCLUDER_PARAMETER_NAME+_sendToLabel);
    }
    URL.append(query);

    fromPage.setToRedirect(URL.toString());
    fromPage.empty();
   }
}