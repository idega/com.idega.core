// idega 2000 - laddi

package com.idega.presentation.ui;



import java.util.Map;
import javax.faces.context.FacesContext;
import javax.faces.el.ValueBinding;
import com.idega.idegaweb.IWResourceBundle;
import com.idega.idegaweb.service.HelpWindow;
import com.idega.presentation.IWContext;
import com.idega.presentation.text.Link;



public class HelpButton extends Link {



private final static String IW_BUNDLE_IDENTIFIER="com.idega.core";

public static final String PARAMETERSTRING_HEADLINE = "headline";

public static final String PARAMETERSTRING_TEXT = "text";

public static final String PARAMETERSTRING_URL = "imageUrl";

private String text = null;

private String headline = null;

private String url = null;



protected IWResourceBundle iwrb;


  public HelpButton() {
    super();
  }


  public HelpButton(String text) {

    super();

    this.text=text;

  }



  public HelpButton(String headline, String text) {

    super();

    this.headline=headline;

    this.text=text;

  }



  public HelpButton(String headline, String text, String imageURL) {

    super();

    this.headline=headline;

    this.text=text;

    this.url=imageURL;

  }



  public void main(IWContext iwc) {

    iwrb = getResourceBundle(iwc);

		if(iwrb != null)

    this.setPresentationObject(iwrb.getImage("/help/help.gif",text));

		String hVal = getHeadline();
	this.addParameter(PARAMETERSTRING_HEADLINE,(hVal == null)?"":hVal);

	String tVal = getText();
    this.addParameter(PARAMETERSTRING_TEXT,(tVal == null)?"":tVal);

    String iVal = getImageUrl();
    this.addParameter(PARAMETERSTRING_URL,(iVal == null)?"":iVal);

    this.setWindowToOpen(HelpWindow.class);

  }



 // public String getBundleIdentifier(){

 //   return IW_BUNDLE_IDENTIFIER;

 // }



public String getHeadline() {
	if(headline != null){
		return headline;
	}
	ValueBinding vb = getValueBinding("headline");
	return (String)((vb != null)?vb.getValue(getFacesContext()):null);
}
public void setHeadline(String headline) {
	this.headline = headline;
}
public String getText() {
	if(text != null){
		return text;
	}
	ValueBinding vb = getValueBinding("text");
	return (String)((vb != null)?vb.getValue(getFacesContext()):null);
}
public void setText(String text) {
	this.text = text;
}
public String getImageUrl() {
	if(url != null){
		return url;
	}
	ValueBinding vb = getValueBinding("imageUrl");
	return (String)((vb != null)?vb.getValue(getFacesContext()):null);
}
public void setImageUrl(String url) {
	this.url = url;
}

public void restoreState(FacesContext context, Object state) {
	Object values[] = (Object[])state;
	super.restoreState(context, values[0]);
	this.text = (String) values[1];
	this.headline = (String) values[2];
	this.url = (String)values[3];

}
/* (non-Javadoc)
 * @see javax.faces.component.StateHolder#saveState(javax.faces.context.FacesContext)
 */
public Object saveState(FacesContext context) {
	Object values[] = new Object[4];
	values[0] = super.saveState(context);
	values[1] = text;
	values[2] = headline;
	values[3] = url;

	return values;
}	

}
