package com.idega.presentation.ui;

import java.net.*;
import java.util.Enumeration;
import java.util.Vector;
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
  private int instanceId;

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
        StringBuffer queryBuf = new StringBuffer();
        String query = null;
        instanceId=getICObjectInstanceID();

        if( iwc.isParameterSet(PAGE_INCLUDER_PARAMETER_NAME+instanceId) ){//after clicking a link og submitting a form
          //get all parameters even from post actions
          Enumeration enum = iwc.getParameterNames();
          while (enum.hasMoreElements()) {
            String param = (String) enum.nextElement();
            debug(param+" : "+iwc.getParameter(param));
            /**@todo use a post method to get the page or stringbuffer this URL  encode?**/
            if( param.equals(PAGE_INCLUDER_PARAMETER_NAME+instanceId) ){
             URL = decodeQueryString(iwc.getParameter(param));
             location.append(URL);
            }
            else{
              queryBuf.append(param);
              queryBuf.append("=");
              queryBuf.append(URLEncoder.encode(iwc.getParameter(param)));
              queryBuf.append("&");
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

        debug("Location = "+location.toString());
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
    String prefix = iwc.getRequestURI()+"?"+PAGE_INCLUDER_PARAMETER_NAME+instanceId+"=";
    html = TextSoap.findAndInsertAfter(html,"href=\"",prefix);
    return TextSoap.findAndInsertAfter(html,"HREF=\"",prefix);
  }

  protected String changeFormActionAttributes(String html,IWContext iwc){
    String prefix = iwc.getRequestURI()+"?"+PAGE_INCLUDER_PARAMETER_NAME+instanceId+"=";
    html = TextSoap.findAndInsertAfter(html,"action=\"",prefix);
    return TextSoap.findAndInsertAfter(html,"ACTION=\"",prefix);
  }

  protected String changeSrcAttributes(String html, String location){

    try {
      URL url = new URL(location);
      String prefix = url.getProtocol()+"://"+url.getHost()+"/";
      html = TextSoap.findAndInsertAfter(html,"background=\"",prefix);
      html = TextSoap.findAndInsertAfter(html,"BACKGROUND=\"",prefix);
      html = TextSoap.findAndInsertAfter(html,"src=\"",prefix);
      html = TextSoap.findAndInsertAfter(html,"SRC=\"",prefix);
    }
    catch (MalformedURLException ex) {
      ex.printStackTrace();
    }

    return html;
  }

  protected String encodeQueryStrings(String html){
    html = TextSoap.findAndReplace(html,"&","#");
    //fixing this should be done with a HTMLEditor object
    html = TextSoap.findAndReplace(html,"#eth;","&eth;");
    html = TextSoap.findAndReplace(html,"#ETH;","&ETH;");
    html = TextSoap.findAndReplace(html,"#thorn;","&thorn;");
    html = TextSoap.findAndReplace(html,"#THORN;","&THORN;");
    html = TextSoap.findAndReplace(html,"#aelig;","&aelig;");
    html = TextSoap.findAndReplace(html,"#AELIG;","&AELIG;");
    html = TextSoap.findAndReplace(html,"#ouml;","&ouml;");
    html = TextSoap.findAndReplace(html,"#OUML;","&OUML;");
    html = TextSoap.findAndReplace(html,"#nbsp;","&nbsp;");
    html = TextSoap.findAndReplace(html,"#amp;","&amp;");
    html = TextSoap.findAndReplace(html,"#quot;","&quot;");
    html = TextSoap.findAndReplace(html,"#middot","&middot");
    html = TextSoap.findAndReplace(html," # "," & ");

//islenskir broddstafir
    html = TextSoap.findAndReplace(html,"#aacute;","&aacute;");
    html = TextSoap.findAndReplace(html,"#Aacute;","&Aacute;");
    html = TextSoap.findAndReplace(html,"#eacute;","&eacute;");
    html = TextSoap.findAndReplace(html,"#Eacute;","&Eacute;");
    html = TextSoap.findAndReplace(html,"#iacute;","&iacute;");
    html = TextSoap.findAndReplace(html,"#Iacute;","&Iacute;");
    html = TextSoap.findAndReplace(html,"#uacute;","&uacute;");
    html = TextSoap.findAndReplace(html,"#Uacute;","&Uacute;");
    html = TextSoap.findAndReplace(html,"#oacute;","&oacute;");
    html = TextSoap.findAndReplace(html,"#Oacute;","&Oacute;");
    html = TextSoap.findAndReplace(html,"#yacute;","&yacute;");
    html = TextSoap.findAndReplace(html,"#Yacute;","&Yacute;");

  //  html = TextSoap.findAndReplace(html,"#iacute;","&iacute;");
  return html;
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

