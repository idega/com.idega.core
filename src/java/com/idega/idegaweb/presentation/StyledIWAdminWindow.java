package com.idega.idegaweb.presentation;


import com.idega.idegaweb.IWApplicationContext;
import com.idega.idegaweb.IWBundle;
import com.idega.idegaweb.IWResourceBundle;
import com.idega.idegaweb.help.presentation.Help;
import com.idega.presentation.IWContext;
import com.idega.presentation.Image;
import com.idega.presentation.Page;
import com.idega.presentation.PresentationObject;
import com.idega.presentation.Table;
import com.idega.presentation.text.Text;
import com.idega.presentation.ui.Window;
import com.idega.user.business.UserBusiness;
import com.idega.user.util.ICUserConstants;

public class StyledIWAdminWindow extends Window {

private final static String IW_BUNDLE_IDENTIFIER="com.idega.user"; 

public static final String MAIN_STYLECLASS = "main";
public static final String TITLE_STYLECLASS = "windowTitleStyle";

private IWBundle iwb;
public IWBundle iwbCore;
public IWBundle iwbUser;
private IWResourceBundle iwrb;
private Table headerTable;
private Table mainTable;
private boolean merged = true;

private String rightWidth = "160";
private String method = "post";
private int _cellPadding = 0;
private Page parentPage;
private String styleSrc = "";
private String inputTextStyle = "text";
private String backTableStyle = "back";
private String mainTableStyle = "main";
private String bannerTableStyle = "banner";

private UserBusiness userBusiness = null;
private boolean titleIsSet = false;
private Text adminTitle = null; 
private Image helpImage = null;


	public StyledIWAdminWindow(){
		super();
		setScrollbar(true);
	}

	public StyledIWAdminWindow(String name){
		super(name);
	}

	public StyledIWAdminWindow(int width, int heigth) {
		super(width,heigth);
	}

	public StyledIWAdminWindow(String name,int width,int height){
		super(name,width,height);
	}

	public StyledIWAdminWindow(String name,String url){
		super(name,url);
	}

	public StyledIWAdminWindow(String name, int width, int height, String url){
		super(name,width,height,url);
	}

	public StyledIWAdminWindow(String name,String classToInstanciate,String template){
		super(name,classToInstanciate,template);
	}

	public StyledIWAdminWindow(String name,Class classToInstanciate){
		super(name,classToInstanciate);
	}

	private void createTablesAndAddThemIfNeeded()  {
		if(headerTable==null){
			headerTable = new Table();
			headerTable.setCellpadding(0);
			headerTable.setCellspacing(0);
			headerTable.setStyleClass(bannerTableStyle);
			headerTable.setWidth("100%");
			headerTable.setAlignment(2,1,"right");
			headerTable.setVerticalAlignment(1,1,"top");
			if(titleIsSet) {
				headerTable.setCellpaddingRight(2, 1, 12);
				headerTable.add(getAdminTitle(),2,1);
			}
			add(headerTable);
		}

		if(mainTable==null){
			mainTable = new Table();
			mainTable.setStyleClass(backTableStyle);
			mainTable.setCellpadding(_cellPadding);
			mainTable.setWidth("100%");
			mainTable.setHeight("100%");
			mainTable.setCellspacing(0);
			mainTable.setVerticalAlignment(1, 1, "top");
			mainTable.setCellpadding(1, 1, 6);
			add(mainTable);	
		}
		
		

	}

	public void add(PresentationObject obj, IWContext iwc) {
		createTablesAndAddThemIfNeeded();	
		mainTable.add(obj, 1, 1);		
	}
	
	public void addTitle(String title) {
	    if(headerTable!=null) {
		    adminTitle = new Text(title+"&nbsp;&nbsp;");
	//		adminTitle.setBold();
			adminTitle.setFontColor("#FFFFFF");
	
			titleIsSet = true;

		
		    headerTable.add(adminTitle,2,1);
		}
	}

	public void addTitle(String title,String style) {
		adminTitle = new Text("." + title);
		adminTitle.setStyleClass(style);
		super.setTitle(title);
		titleIsSet = true;
	}
	public Text getAdminTitle() {
		return adminTitle;
	}
	public Text formatText(String s, boolean bold){
		Text T= new Text();
		if ( s != null ) {
			T= new Text(s);
			if ( bold )
				T.setBold();
		}
		return T;
	}

	public void formatText(Text text, boolean bold){
		if ( bold )
			text.setBold();
	}

	public Text formatText(String s) {
		Text T = formatText(s,true);
		return T;
	}

	public Text formatHeadline(String s) {
		Text T= new Text();
		if ( s != null ) {
			T= new Text(s);
			T.setBold();

		}
		return T;
	}
	public void _main(IWContext iwc)throws Exception{
		iwb = getBundle(iwc);
		userBusiness = getUserBusiness(iwc);
		parentPage = this.getParentPage();
		styleSrc = userBusiness.getUserApplicationStyleSheetURL();
		parentPage.addStyleSheetURL(styleSrc);
		
		super._main(iwc);
	}
	public void main(IWContext iwc)throws Exception{
	}
	public Help getHelp(String helpTextKey) {
		IWContext iwc = IWContext.getInstance();
		iwb = iwc.getIWMainApplication().getBundle(IW_BUNDLE_IDENTIFIER);
	 	Help help = new Help();
	 	helpImage = iwb.getImage("help.gif");//.setSrc("/idegaweb/bundles/com.idega.user.bundle/resources/help.gif");
 	  help.setHelpTextBundle( ICUserConstants.HELP_BUNDLE_IDENTFIER);
	  help.setHelpTextKey(helpTextKey);
	  help.setImage(helpImage);
	  return help;
	}
	public Help getHelpWithGrayImage(String helpTextKey, boolean dark) {
		IWContext iwc = IWContext.getInstance();
		iwb = iwc.getIWMainApplication().getBundle(IW_BUNDLE_IDENTIFIER);
		Help help = new Help();
		if(dark) {
			helpImage = iwb.getImage("help_small.gif");
		}
		else {
			helpImage = iwb.getImage("help_lightgray.gif");
		}
		help.setHelpTextBundle(ICUserConstants.HELP_BUNDLE_IDENTFIER);
		help.setHelpTextKey(helpTextKey);
		help.setImage(helpImage);
		return help;
		
	}
	
	protected UserBusiness getUserBusiness(IWApplicationContext iwc) {
			if (userBusiness == null) {
				try {
					userBusiness = (UserBusiness) com.idega.business.IBOLookup.getServiceInstance(iwc, UserBusiness.class);
				}
				catch (java.rmi.RemoteException rme) {
					throw new RuntimeException(rme.getMessage());
				}
			}
			return userBusiness;
		}


	public String getBundleIdentifier(){
		return IW_BUNDLE_IDENTIFIER;
	}

}
