/*
 * Created on Mar 30, 2004
 */
package com.idega.presentation;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.idega.presentation.text.AnchorLink;
import com.idega.presentation.text.Link;
import com.idega.presentation.text.Text;


/**
 * A Javascript style multi level dropdown menu but implemented in pure CSS and HTML! <br>
 * Based on the great work of <a href="http://www.alistapart.com/articles/taminglists/">ALA</a> and <br>
 * <a href="http://www.aplus.co.yu/">Aplus</a> (ADxMenu). To customize the look of this component you simply <br>
 * need to copy the default style sheet it uses and change it. The menus can contain any PresentationObject.
 * 
 * Example usage:<br>
 * <code>
 		CSSMultiLevelMenu menuBar = new CSSMultiLevelMenu();
		//create a menu
		CSSMenu level0101 = menuBar.createCSSMenu("First top menu");
		//add as a top menu
		menuBar.add(level0101);
		//insert items
		level0101.add("Item one");
		level0101.add("Item two");
		level0101.add("Item three");
		//insert item that is a submenu
		CSSMenu level010103 = menuBar.createCSSMenu("Item three submenu");
		level0101.add(level010103);
		level010103.add("Item one in submenu");
		level010103.add(new Link("Item two in submenu","#"));
		//one last item
		level0101.add(new Text("Item four"));
		
		//create and add an empty top menu
		CSSMenu level0102 = menuBar.createCSSMenu("Second top menu");
		menuBar.add(level0102);
		add(menuBar);
		</code>
 * 
 * @author <a href="mailto:eiki@idega.is">Eirikur Hrafnsson</a>
 */
public class CSSMultiLevelMenu extends PresentationObjectContainer {

    Map topMenuMap;
   
    
    private static String prefix = "<div id=\"menu\">\n<ul id=\"menuList\">\n";
    private static String suffix = "\t</ul>\n</div>";
    private static String CSS_FILE_PATH = "cssmenu/CSSMultiLevelMenu.css";
    private static String WCH_JAVASCRIPT_FILE_PATH = "cssmenu/WCH.js";//for layering over dropdowns etc.
    private static String ADxMENU_JAVASCRIPT_FILE_PATH = "cssmenu/ADxMenu.js";//for fixing explorer on windows
 
    private boolean addTestData = false;
    
    
    /**
     * 
     */
    public CSSMultiLevelMenu() {
        super();
    }
    
    
    /*
     * Constructs and returns a CSSMenu with your presentationobject as the label
     */
    public CSSMenu createCSSMenu(String menuName, PresentationObject menuLabel) {
        if(topMenuMap==null) {
            topMenuMap = new LinkedHashMap();
        }
        
        CSSMenu menu = (CSSMenu)topMenuMap.get(menuName);
        
        if(menu==null) {
            menu = new CSSMenu(menuName,menuLabel);
            topMenuMap.put(menuName,menu);
        }
        
        return menu;
    }
    

    /*
     * Constructs and returns a CSSMenu with a Link(menuName,"#") as the label
     */
    public CSSMenu createCSSMenu(String menuName) {
        AnchorLink menuNameNowherelink = new AnchorLink(new Text(menuName),"");
        
        return createCSSMenu(menuName, menuNameNowherelink);
    }
    
    /*
     * Add a CSSMenu. You could add other things but the results might be strange. 
     */
    public void add(PresentationObject menu) {
        super.add(menu);
    }
    
    /*
     * Add a CSSMenu.
     */
    public void addCSSMenu(CSSMenu menu) {
        add(menu);
    }
    
    /* (non-Javadoc)
     * @see com.idega.presentation.PresentationObject#main(com.idega.presentation.IWContext)
     */
    public void main(IWContext iwc) throws Exception {
        Page parentPage = this.getParentPage();
        
        //not needed any more, implementing with ADxMenu 2b
		/*if(iwc.isIE()) {
		    String pathToExplorerFixCSS = this.getBundle(iwc).getVirtualPathWithFileNameString(CSS_EXPLORER_FIX_FILE_PATH);
		    parentPage.setStyleDefinition("body","behavior:url(\""+pathToExplorerFixCSS+"\");");
		}*/
		
		String pathToMenuCSS = this.getBundle(iwc).getVirtualPathWithFileNameString(CSS_FILE_PATH);
		String pathToWCH = this.getBundle(iwc).getVirtualPathWithFileNameString(WCH_JAVASCRIPT_FILE_PATH);
		String pathToMainScript= this.getBundle(iwc).getVirtualPathWithFileNameString(ADxMENU_JAVASCRIPT_FILE_PATH);
		parentPage.addStyleSheetURL(pathToMenuCSS);
		parentPage.addJavascriptURL(pathToWCH);
		parentPage.addJavascriptURL(pathToMainScript);
		
		
		if(addTestData) {
			//A little test code, 
			//create a menu
			CSSMenu level0101 = createCSSMenu("First top menu");
			//add as a top menu
			this.add(level0101);
			//insert items
			level0101.add(new Link("Item one","#"));
			level0101.add(new Link("Item two","#"));
			
			//insert item that is a submenu
			CSSMenu level010103 = createCSSMenu("Item three submenu");
			level0101.add(level010103);
			level010103.add(new Link("Item one in submenu","#"));
			level010103.add(new Link("Item two in submenu","#"));
			//one last item
			level0101.add(new Link("Item four","#"));
			
			//create and add an empty top menu
			CSSMenu level0102 = createCSSMenu("Second empty top menu");
			this.add(level0102);
		}
		
    }
    
    /* (non-Javadoc)
     * @see com.idega.presentation.PresentationObject#print(com.idega.presentation.IWContext)
     */
    public void print(IWContext iwc) throws Exception {
        
        if(getMarkupLanguage().equals("HTML")) {
	        if(topMenuMap!=null && !topMenuMap.isEmpty()) {
	        	println("<script type=\"text/javascript\">\n ADXM.Add( \"menuList\", \"H\" ); \n </script>");
	            print(prefix);
	        
	            super.print(iwc);
	            
	        		print(suffix);
	        }
        }
    }
    
    
    public void setToAddTestData(boolean addTestData) {
        this.addTestData = addTestData;
    }
    
    
    

    /**
     * An helper object to support multiple levels of menus. CSSMenu stores presentationobjects and other CSSMenus as its menu items.
     * It cannot be used alone so it must be added to a CSSMultiLevelMenu to work.
     * @author <a href="mailto:eiki@idega.is">Eirikur S. Hrafnsson</a>
     *
     **/
    public class CSSMenu extends PresentationObjectContainer{
     
		private static final String UL_END_TAG = "</ul>";
		private static final String UL_START_TAG = "\n<ul>";
		private static final String LI_END_TAG = "</li>";
		private static final String LI_START_TAG = "<li>";
        private List menuItems;
        private String menuName;
        private PresentationObject topMenuItem;
        
        
        /**
         * Constructs a CSSMenu with the given presentationobject as the top item.
         */
        public CSSMenu(String menuName, PresentationObject topMenuItem) {
            this.menuName = menuName;
            this.topMenuItem = topMenuItem;
        }
        
        /*
         * Add a PresentationObject (another CSSMenu for example) as a menu item.
         */
        public void addMenuItem(PresentationObject menuItem) {
            add(menuItem);
        }
        
        /*
         * Add a PresentationObject (another CSSMenu for example) as a menu item.
         */
        public void add(PresentationObject menuItem) {
            super.add(menuItem);
        }
        
        /*
         *  Constructs and adds an Link("#") object to the menu using the supplied text.
         */
        public void add(String menuItem) {
            addMenuItem(menuItem);
        }
        
        /*
         * Constructs and adds an Link("#") object to the menu using the supplied text.
         */
        public void addMenuItem(String textMenuItem) {
            AnchorLink menuItemNowhereLink = new AnchorLink(new Text(textMenuItem),"");
            
            addMenuItem(menuItemNowhereLink);
        }
        
        
        /* (non-Javadoc)
         * @see com.idega.presentation.PresentationObject#print(com.idega.presentation.IWContext)
         */
        public void print(IWContext iwc) throws Exception {
            List children = getChildren();
            if(children!=null && !children.isEmpty()) {
                topMenuItem.setStyleClass("submenu");
                
                print(LI_START_TAG);
                topMenuItem._print(iwc);
                println(UL_START_TAG);
                
                //iterate through the children and print them to
                Iterator iter = children.iterator();
                while (iter.hasNext()) {
                    PresentationObject obj = (PresentationObject) iter.next();
                    
                    if(!(obj instanceof CSSMenu)){
                        print(LI_START_TAG);
                        obj._print(iwc);
                        println(LI_END_TAG);
                    }
                    else {
                        obj._print(iwc);
                    }
                    
                    
                }
                println(UL_END_TAG);
                println(LI_END_TAG);
                
            }
            else {
                //topMenuItem.setStyleClass("menuitem");
                print(LI_START_TAG);
                topMenuItem._print(iwc);
                print(LI_END_TAG);
            }
            
        }
        
        
        public String getMenuName() {
            return menuName;
        }
        
        public void setMenuName(String menuName) {
            this.menuName = menuName;
        }

    }
    /* (non-Javadoc)
     * @see com.idega.presentation.PresentationObject#isContainer()
     */
    public boolean isContainer() {
        //not allowed to add to this module in the Builder yet!
        return false;
    }
}
