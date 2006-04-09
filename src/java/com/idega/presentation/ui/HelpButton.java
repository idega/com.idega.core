// idega 2000 - laddi

package com.idega.presentation.ui;



import javax.faces.context.FacesContext;
import javax.faces.el.ValueBinding;
import com.idega.idegaweb.IWResourceBundle;
import com.idega.idegaweb.service.HelpWindow;
import com.idega.presentation.IWContext;
import com.idega.presentation.text.Link;



public class HelpButton extends Link {



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

    this.iwrb = getResourceBundle(iwc);

		if(this.iwrb != null) {
			this.setPresentationObject(this.iwrb.getImage("/help/help.gif",this.text));
		}

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
	if(this.headline != null){
		return this.headline;
	}
	ValueBinding vb = getValueBinding("headline");
	return (String)((vb != null)?vb.getValue(getFacesContext()):null);
}
public void setHeadline(String headline) {
	this.headline = headline;
}
public String getText() {
	if(this.text != null){
		return this.text;
	}
	ValueBinding vb = getValueBinding("text");
	return (String)((vb != null)?vb.getValue(getFacesContext()):null);
}
public void setText(String text) {
	this.text = text;
}
public String getImageUrl() {
	if(this.url != null){
		return this.url;
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
	values[1] = this.text;
	values[2] = this.headline;
	values[3] = this.url;

	return values;
}	

}
