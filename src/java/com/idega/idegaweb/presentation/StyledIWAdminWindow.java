package com.idega.idegaweb.presentation;


import com.idega.core.builder.business.BuilderService;
import com.idega.core.builder.business.BuilderServiceFactory;
import com.idega.idegaweb.IWApplicationContext;
import com.idega.idegaweb.IWBundle;
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
private Table headerTable;
private Table mainTable;
private int _cellPadding = 0;
private Page parentPage;
private String styleSrc = "";
private String backTableStyle = "back";
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
		if(this.headerTable==null){
			this.headerTable = new Table();
			this.headerTable.setCellpadding(0);
			this.headerTable.setCellspacing(0);
			this.headerTable.setStyleClass(this.bannerTableStyle);
			this.headerTable.setWidth("100%");
			this.headerTable.setAlignment(2,1,"right");
			this.headerTable.setVerticalAlignment(1,1,"top");
			if(this.titleIsSet) {
				this.headerTable.setCellpaddingRight(2, 1, 12);
				this.headerTable.add(getAdminTitle(),2,1);
			}
			add(this.headerTable);
		}

		if(this.mainTable==null){
			this.mainTable = new Table();
			this.mainTable.setStyleClass(this.backTableStyle);
			this.mainTable.setCellpadding(this._cellPadding);
			this.mainTable.setWidth("100%");
			this.mainTable.setHeight("100%");
			this.mainTable.setCellspacing(0);
			this.mainTable.setVerticalAlignment(1, 1, "top");
			this.mainTable.setCellpadding(1, 1, 6);
			add(this.mainTable);	
		}
		
		

	}

	public void add(PresentationObject obj, IWContext iwc) {
		createTablesAndAddThemIfNeeded();	
		this.mainTable.add(obj, 1, 1);		
	}
	
	public void addTitle(String title) {
	    if(this.headerTable!=null) {
		    this.adminTitle = new Text(title+"&nbsp;&nbsp;");
	//		adminTitle.setBold();
			this.adminTitle.setFontColor("#FFFFFF");
	
			this.titleIsSet = true;

		
		    this.headerTable.add(this.adminTitle,2,1);
		}
	}

	public void addTitle(String title,String style) {
		this.adminTitle = new Text("." + title);
		this.adminTitle.setStyleClass(style);
		super.setTitle(title);
		this.titleIsSet = true;
	}
	public Text getAdminTitle() {
		return this.adminTitle;
	}
	public Text formatText(String s, boolean bold){
		Text T= new Text();
		if ( s != null ) {
			T= new Text(s);
			if ( bold ) {
				T.setBold();
			}
		}
		return T;
	}

	public void formatText(Text text, boolean bold){
		if ( bold ) {
			text.setBold();
		}
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
		this.iwb = getBundle(iwc);
		this.userBusiness = getUserBusiness(iwc);
		this.parentPage = this.getParentPage();
		this.styleSrc = this.userBusiness.getUserApplicationStyleSheetURL();
		this.parentPage.addStyleSheetURL(this.styleSrc);
		
		BuilderServiceFactory.getBuilderService(iwc).addJavaScriptForChooser(this.parentPage);
		
		super._main(iwc);
	}
	public void main(IWContext iwc)throws Exception{
	}
	public Help getHelp(String helpTextKey) {
		IWContext iwc = IWContext.getInstance();
		this.iwb = iwc.getIWMainApplication().getBundle(IW_BUNDLE_IDENTIFIER);
	 	Help help = new Help();
	 	this.helpImage = this.iwb.getImage("help.gif");//.setSrc("/idegaweb/bundles/com.idega.user.bundle/resources/help.gif");
 	  help.setHelpTextBundle( ICUserConstants.HELP_BUNDLE_IDENTFIER);
	  help.setHelpTextKey(helpTextKey);
	  help.setImage(this.helpImage);
	  return help;
	}
	public Help getHelpWithGrayImage(String helpTextKey, boolean dark) {
		IWContext iwc = IWContext.getInstance();
		this.iwb = iwc.getIWMainApplication().getBundle(IW_BUNDLE_IDENTIFIER);
		Help help = new Help();
		if(dark) {
			this.helpImage = this.iwb.getImage("help_small.gif");
		}
		else {
			this.helpImage = this.iwb.getImage("help_lightgray.gif");
		}
		help.setHelpTextBundle(ICUserConstants.HELP_BUNDLE_IDENTFIER);
		help.setHelpTextKey(helpTextKey);
		help.setImage(this.helpImage);
		return help;
		
	}
	
	public UserBusiness getUserBusiness(IWApplicationContext iwc) {
			if (this.userBusiness == null) {
				try {
					this.userBusiness = (UserBusiness) com.idega.business.IBOLookup.getServiceInstance(iwc, UserBusiness.class);
				}
				catch (java.rmi.RemoteException rme) {
					throw new RuntimeException(rme.getMessage());
				}
			}
			return this.userBusiness;
		}


	public String getBundleIdentifier(){
		return IW_BUNDLE_IDENTIFIER;
	}

}
