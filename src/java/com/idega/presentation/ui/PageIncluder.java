package com.idega.presentation.ui;

import java.net.MalformedURLException;
import java.util.Vector;
import java.net.URL;
import java.io.IOException;
import com.idega.presentation.PresentationObject;
import com.idega.presentation.IWContext;
import com.idega.util.FileUtil;
import com.idega.util.text.TextSoap;

/**
 * A presentationObject that uses FileUtil.getStringFromURL to serverside include a given URL
*@author <a href="mailto:eiki@idega.is">Eirikur Hrafnsson</a>
*@version 1.0
*/
public class PageIncluder extends PresentationObject{
  private String URL = null;
  private static final String PAGE_INCLUDER_PARAMETER_NAME="iw_uri";

  public PageIncluder(){
    super();
  }

  public PageIncluder(String URL){
    this();
    this.URL = URL;
  }

  public void print(IWContext iwc)throws IOException{
    if(URL!=null){
      initVariables(iwc);
      if( doPrint(iwc) ){
        StringBuffer location = new StringBuffer();
        String query = iwc.getQueryString();
        if( query==null ) query = "";

        if( iwc.isParameterSet(PAGE_INCLUDER_PARAMETER_NAME) ){
          query = TextSoap.findAndCut(decodeQueryString(query),PAGE_INCLUDER_PARAMETER_NAME+"=");
        }

        location.append(URL);

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

          location.append(query);
        }
        //debug(location.toString());
        String html = FileUtil.getStringFromURL(location.toString());

        /**@todo fix if e.g. some src= do include the URI so that the URI isn't added twice
         * @todo use expressions to make none case sensitive or implement using HTMLDocumentLoader (Advanced Swing);
         * **/

        html = TextSoap.stripHTMLandBodyTag(html);
        html = encodeQueryStrings(html);
        html = changeSrcAttributes(html,location.toString());
        html = changeAHrefAttributes(html,iwc);
        html = changeFormActionAttributes(html,iwc);

        println(html);
      }
    }
  }

  protected String changeAHrefAttributes(String html,IWContext iwc){
    return TextSoap.findAndInsertAfter(html,"href=\"",iwc.getRequestURI()+"?"+PAGE_INCLUDER_PARAMETER_NAME+"=");
  }

  protected String changeFormActionAttributes(String html,IWContext iwc){
    return TextSoap.findAndInsertAfter(html,"action=\"",iwc.getRequestURI()+"?"+PAGE_INCLUDER_PARAMETER_NAME+"=");
  }

  protected String changeSrcAttributes(String html, String location) throws MalformedURLException{
    URL url = new URL(location);
    return TextSoap.findAndInsertAfter(html,"src=\"",url.getProtocol()+"://"+url.getHost()+"/");
  }

  protected String encodeQueryStrings(String html){
    html = TextSoap.findAndReplace(html,"&","#");
    return TextSoap.findAndReplace(html," #"," &");
  }

  protected String decodeQueryString(String query){
   return TextSoap.findAndReplace(query,"#","&");
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
    }
    catch(Exception ex) {
      ex.printStackTrace(System.err);
    }
    return obj;
  }

  public void setURL(String URL){
    this.URL = URL;
  }

  }

