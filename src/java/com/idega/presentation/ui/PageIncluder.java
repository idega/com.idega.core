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
  private String _sendURLTo = null;
  private IBPage _sendToPage = null;
  private String _sendToPageIfSet = null;
  private String currentPageParameterAndValue = null;

  private String sessionId = null;
  private String sessionURL = null;
  private String token = null;
  private String tokenReplacer;

  private String out;
  private int index = 1000;

  private static final String PAGE_INCLUDER_PARAMETER_NAME="iw_uri";
  private static final String PAGE_INCLUDER_SESSION_NAME="iw_session_token";
  private int instanceId;
  private boolean forceFrame = false;

  public PageIncluder(){
    super();
  }

  public PageIncluder(String URL){
    this();
    this.URL = URL;
  }

  private void sortAndProcess(IWContext iwc){
    //sort
    Page parent = this.getParentPage();
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
    if (out==null) sortAndProcess(iwc);

    if (forceFrame) {
      if (_sendToPage != null) {
        if (_sendToPageIfSet == null){
          RequestDispatcher req = iwc.getRequest().getRequestDispatcher(BuilderLogic.getInstance().getIBPageURL(iwc.getApplicationContext(),((Integer)_sendToPage.getPrimaryKeyValue()).intValue()));
          req.forward(iwc.getRequest(),iwc.getResponse());
        }
        else {
          if (iwc.isParameterSet(_sendToPageIfSet)){
            RequestDispatcher req = iwc.getRequest().getRequestDispatcher(BuilderLogic.getInstance().getIBPageURL(iwc.getApplicationContext(),((Integer)_sendToPage.getPrimaryKeyValue()).intValue()));
            req.forward(iwc.getRequest(),iwc.getResponse());
          }
        }
      }
    }
  }

  public void print(IWContext iwc)throws IOException{
    if(URL!=null){
      initVariables(iwc);
      if( doPrint(iwc) ){
        if(out!=null) println(out);
        out = null;
      }
    }
  }

  protected void process(IWContext iwc)throws IOException{
        StringBuffer location = new StringBuffer();
        StringBuffer queryBuf = new StringBuffer();
        String query = null;
        instanceId=getICObjectInstanceID();

/*        Enumeration enum2 = iwc.getParameterNames();
        while (enum2.hasMoreElements()) {
          String param2 = (String) enum2.nextElement();
          System.out.println("param = " + param2);
        }*/

        if (forceFrame) {
          String currentPage = getCurrentIBPageIDToURLString(iwc);
          StringBuffer buf = new StringBuffer();
          buf.append(iwc.getRequestURI());
          buf.append('?');
          buf.append(currentPage);
          buf.append('&');


          if (_sendURLTo == null){
            buf.append(PAGE_INCLUDER_PARAMETER_NAME);
            buf.append(instanceId);
            buf.append('=');
          }
          else {
            if (_sendToPageIfSet == null){
              buf.append(PAGE_INCLUDER_PARAMETER_NAME);
              buf.append(_sendURLTo);
              buf.append('=');
            }
            else {
              if (iwc.isParameterSet(_sendToPageIfSet)){
                buf.append(PAGE_INCLUDER_PARAMETER_NAME);
                buf.append(_sendURLTo);
                buf.append('=');
              }
              else{
                buf.append(PAGE_INCLUDER_PARAMETER_NAME);
                buf.append(instanceId);
                buf.append('=');
              }
            }
          }

          pageIncluderPrefix = buf.toString();


        }
        else {
         pageIncluderPrefix ="";
        }

        if (iwc.isParameterSet(PAGE_INCLUDER_PARAMETER_NAME+_label)) {//after clicking a link og submitting a form
          //get all parameters even from post actions
          Enumeration enum = iwc.getParameterNames();
          while (enum.hasMoreElements()) {
            String param = (String) enum.nextElement();
            //debug(param+" : "+iwc.getParameter(param));
            if (param.equals(PAGE_INCLUDER_PARAMETER_NAME+_label) ){
             URL = decodeQueryString(iwc.getParameter(param));
             TextSoap.findAndReplace(URL,PAGE_INCLUDER_PARAMETER_NAME+_label,PAGE_INCLUDER_PARAMETER_NAME+instanceId);
             location.append(URL);
            }
            else if(!param.equals(PAGE_INCLUDER_PARAMETER_NAME+instanceId) ){
              if (param.indexOf(PAGE_INCLUDER_PARAMETER_NAME) == -1) {
                queryBuf.append(param);
                queryBuf.append("=");
                queryBuf.append(URLEncoder.encode(iwc.getParameter(param)));
                queryBuf.append("&");
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
        }
        else if(iwc.isParameterSet(PAGE_INCLUDER_PARAMETER_NAME+instanceId) ){//after clicking a link og submitting a form
          //get all parameters even from post actions
          Enumeration enum = iwc.getParameterNames();
          while (enum.hasMoreElements()) {
            String param = (String) enum.nextElement();
            //debug(param+" : "+iwc.getParameter(param));
            /**@todo use a post method to get the page or stringbuffer this URL  encode?**/
            if( param.equals(PAGE_INCLUDER_PARAMETER_NAME+instanceId) ){
             URL = decodeQueryString(iwc.getParameter(param));
             if (_sendURLTo != null)
               TextSoap.findAndReplace(URL,PAGE_INCLUDER_PARAMETER_NAME+instanceId,PAGE_INCLUDER_PARAMETER_NAME+_sendURLTo);
             location.append(URL);
            }
            else{
//              if (param.indexOf(PAGE_INCLUDER_PARAMETER_NAME) == -1) {
                queryBuf.append(param);
                queryBuf.append("=");
                queryBuf.append(URLEncoder.encode(iwc.getParameter(param)));
                queryBuf.append("&");
//              }
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

        debug("Location url is: "+loc+" and index is: "+index);


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
        out = encodeQueryStrings(out);
        out = changeSrcAttributes(out);
        out = changeAHrefAttributes(out);
        out = changeFormActionAttributes(out);
        out = postProcess(out);
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
    html = TextSoap.findAndReplace(html,tag+"=\"//",tag+"=\""+pageIncluderPrefix+"http://" );// the // case
    html = TextSoap.findAndReplace(html,tag+"=\"http://",tag+"=\""+pageIncluderPrefix+"http://");// the http:// case

    if(forceFrame){
      html = TextSoap.findAndReplace(html,tag+"=\"/",pageIncluderPrefix.substring(1,pageIncluderPrefix.length()),tag+"=\"/",tag+"=\""+pageIncluderPrefix+BASEURL );// the / case
      html = TextSoap.findAndReplace(html,tag+"=\"",pageIncluderPrefix,tag+"=\""+pageIncluderPrefix+RELATIVEURL);
    }
    else{
      html = TextSoap.findAndReplace(html,tag+"=\"/",tag+"=\""+BASEURL);
      html = TextSoap.findAndReplace(html,tag+"=\"","http://",tag+"=\""+RELATIVEURL);
    }
    return html;
  }

  protected String getCurrentIBPageIDToURLString(IWContext iwc){
    BuilderLogic bill = BuilderLogic.getInstance();
    return bill.IB_PAGE_PARAMETER+"="+bill.getCurrentIBPageID(iwc);
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


  protected String encodeQueryStrings(String html){
    html = TextSoap.findAndReplace(html,"&","##");
    //fixing this should be done with a HTMLEditor object OR
    //make a single general expression fix
    html = TextSoap.findAndReplace(html,"##eth;","&eth;");
    html = TextSoap.findAndReplace(html,"##ETH;","&ETH;");
    html = TextSoap.findAndReplace(html,"##thorn;","&thorn;");
    html = TextSoap.findAndReplace(html,"##THORN;","&THORN;");
    html = TextSoap.findAndReplace(html,"##aelig;","&aelig;");
    html = TextSoap.findAndReplace(html,"##AELIG;","&AELIG;");
    html = TextSoap.findAndReplace(html,"##ouml;","&ouml;");
    html = TextSoap.findAndReplace(html,"##Ouml;","&Ouml;");
    html = TextSoap.findAndReplace(html,"##nbsp;","&nbsp;");
    html = TextSoap.findAndReplace(html,"##amp;","&amp;");
    html = TextSoap.findAndReplace(html,"##quot;","&quot;");
    html = TextSoap.findAndReplace(html,"##middot","&middot");
    html = TextSoap.findAndReplace(html,"##raquo;","&raquo;");
    html = TextSoap.findAndReplace(html,"###149;","&#149;");
    html = TextSoap.findAndReplace(html,"###039;","&#039;");
    html = TextSoap.findAndReplace(html,"###169;","&#169;");
    html = TextSoap.findAndReplace(html,"##gt;","&gt;");
    html = TextSoap.findAndReplace(html," ## "," & ");

//islenskir broddstafir
    html = TextSoap.findAndReplace(html,"##aacute;","&aacute;");
    html = TextSoap.findAndReplace(html,"##Aacute;","&Aacute;");
    html = TextSoap.findAndReplace(html,"##eacute;","&eacute;");
    html = TextSoap.findAndReplace(html,"##Eacute;","&Eacute;");
    html = TextSoap.findAndReplace(html,"##iacute;","&iacute;");
    html = TextSoap.findAndReplace(html,"##Iacute;","&Iacute;");
    html = TextSoap.findAndReplace(html,"##uacute;","&uacute;");
    html = TextSoap.findAndReplace(html,"##Uacute;","&Uacute;");
    html = TextSoap.findAndReplace(html,"##oacute;","&oacute;");
    html = TextSoap.findAndReplace(html,"##Oacute;","&Oacute;");
    html = TextSoap.findAndReplace(html,"##yacute;","&yacute;");
    html = TextSoap.findAndReplace(html,"##Yacute;","&Yacute;");

  //  html = TextSoap.findAndReplace(html,"##iacute;","&iacute;");
  return html;
  }

  protected String decodeQueryString(String query){
   return TextSoap.findAndReplace(query,"##","&");
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
    _sendURLTo = label;
  }

  public String getLabel() {
    return _label;
  }

  public String getRedirectTo() {
    return _sendURLTo;
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
}