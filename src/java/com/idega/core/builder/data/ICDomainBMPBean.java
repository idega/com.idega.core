/*
 * $Id: ICDomainBMPBean.java,v 1.3 2006/03/28 10:20:10 tryggvil Exp $
 * Created on 25.11.2005 in project com.idega.core
 *
 * Copyright (C) 2005 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.core.builder.data;

import java.rmi.RemoteException;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;
import javax.ejb.FinderException;
import com.idega.data.GenericEntity;
import com.idega.data.IDOLookup;
import com.idega.data.IDOQuery;
import com.idega.data.IDORelationshipException;
import com.idega.data.query.SelectQuery;
import com.idega.data.query.Table;
import com.idega.user.data.GroupDomainRelation;
import com.idega.user.data.GroupDomainRelationHome;
import com.idega.user.data.GroupDomainRelationTypeBMPBean;


/**
 * <p>
 * Default implementation of ICDomain and mapping of the IB_DOMAIN Table.
 * </p>
 *  Last modified: $Date: 2006/03/28 10:20:10 $ by $Author: tryggvil $
 * 
 * @author <a href="mailto:tryggvil@idega.com">tryggvil</a>
 * @version $Revision: 1.3 $
 */
public class ICDomainBMPBean extends GenericEntity implements ICDomain{
	  /**
	 * Comment for <code>serialVersionUID</code>
	 */
	private static final long serialVersionUID = -1390285582951301632L;
	public static final String tableName = "IB_DOMAIN";
	  public static final String domain_name = "DOMAIN_NAME";
	  public static final String domain_url = "URL";
	  public static final String start_page = "START_IB_PAGE_ID";
	  public static final String start_template = "START_IB_TEMPLATE_ID";
	  public static final String COLUMNNAME_GROUP_ID = "GROUP_ID";
	  public static final String COLUMNNAME_SERVER_NAME = "SERVER_NAME";
	  public static final String COLUMNNAME_SERVER_PORT = "SERVER_PORT";
	  public static final String COLUMNNAME_SERVER_PROTOCOL = "SERVER_PROTOCOL";
	  public static final String COLUMNNAME_SERVER_CONTEXT_PATH = "SERVER_CONTEXT_PATH";

	  private static Map cachedDomains;

	  public ICDomainBMPBean() {
	    super();
	  }


	  public void initializeAttributes() {
	    addAttribute(getIDColumnName());

	    addAttribute(getColumnDomainName(),"Domain name",true,true,String.class);
	    addAttribute(getColumnURL(),"Domain URL",true,true,String.class,1000);
	    addAttribute(getColumnStartPage(),"Start Page",true,true,Integer.class,"many-to-one",ICPage.class);
	    addAttribute(getColumnStartTemplate(),"Start Template",true,true,Integer.class,"many-to-one",ICPage.class);
	    addAttribute(COLUMNNAME_SERVER_NAME,"Server NAME",true,true,String.class);
//	    this.addManyToManyRelationShip(Group.class);
//	    addAttribute(COLUMNNAME_GROUP_ID,"Group ID",true,true,Integer.class,"one-to-one",Group.class);

	    //Add a UUID column to uniquely identify the domain:
	    super.addUniqueIDColumn();
	    addAttribute(COLUMNNAME_SERVER_PORT,"Server port",Integer.class);
	    addAttribute(COLUMNNAME_SERVER_PROTOCOL,"Server protocol",String.class,30);
	    addAttribute(COLUMNNAME_SERVER_CONTEXT_PATH,"Server context path",String.class);
	    
	  }

	  public static ICDomain getDomain(int id)throws SQLException {
	    ICDomain theReturn;
	    theReturn = (ICDomain)getDomainsMap().get(new Integer(id));
	    if (theReturn == null) {
	      theReturn = ((ICDomainHome)IDOLookup.getHomeLegacy(ICDomain.class)).findByPrimaryKeyLegacy(id);
	      if (theReturn != null) {
	        getDomainsMap().put(new Integer(id),theReturn);
	      }
	    }
	    return(theReturn);
	  }

	  private static Map getDomainsMap() {
	    if (cachedDomains==null) {
	      cachedDomains = new HashMap();
	    }
	    return(cachedDomains);
	  }

	  public void insertStartData() throws Exception {
	    //BuilderLogic instance = BuilderLogic.getInstance();
	    ICDomainHome dHome = (ICDomainHome)getIDOHome(ICDomain.class);
	    ICDomain domain = dHome.create();
	    //TODO: Make this possible to set
	    String domainName = "Default Site";
	    domain.setName(domainName);
/*
		ICPageHome pageHome = (ICPageHome)getIDOHome(ICPage.class);

	    ICPage page = pageHome.create();
	    String rootPageName = domainName;
	    page.setName(rootPageName);
	    page.setDefaultPageURI("/");
	    page.setType(com.idega.builder.data.IBPageBMPBean.PAGE);
	    page.store();
	    instance.unlockRegion(page.getPrimaryKey().toString(),"-1",null);

	    ICPage page2 = pageHome.create();
	    page2.setName("Default Template");
	    page2.setType(com.idega.builder.data.IBPageBMPBean.TEMPLATE);
	    page2.store();

	    instance.unlockRegion(page2.getPageKey(),"-1",null);

	    page.setTemplateKey(page2.getPageKey());
	    page.store();

	    domain.setIBPage(page);
	    domain.setStartTemplate(page2);
	    domain.store();

	    instance.setTemplateId(page.getPrimaryKey().toString(),page2.getPrimaryKey().toString());
	    instance.getIBXMLPage(page2.getPrimaryKey().toString()).addPageUsingThisTemplate(page.getPrimaryKey().toString());
*/	    
	    
	    domain.store();
	  }

	  public String getEntityName() {
	    return(tableName);
	  }

	  public static String getColumnDomainName() {
	    return(domain_name);
	  }

	  public static String getColumnURL() {
	    return(domain_url);
	  }

	  public static String getColumnStartPage() {
	    return(start_page);
	  }

	  public static String getColumnStartTemplate() {
	    return(start_template);
	  }

	  public ICPage getStartPage() {
	    return((ICPage)getColumnValue(getColumnStartPage()));
	  }

	  public int getStartPageID() {
	    return(getIntColumnValue(getColumnStartPage()));
	  }

	  public ICPage getStartTemplate() {
	    return((ICPage)getColumnValue(getColumnStartTemplate()));
	  }

	  public int getStartTemplateID() {
	    return(getIntColumnValue(getColumnStartTemplate()));
	  }

//	  public Group getGroup() {
//	    return((Group)getColumnValue(COLUMNNAME_GROUP_ID));
//	  }
	//
//	  public int getGroupID() {
//	    return(getIntColumnValue(COLUMNNAME_GROUP_ID));
//	  }
	  
	  // thomas is asking: why are there two methods (getName and  getDomainName) for the same attribute?
	  public String getName() {
	    return(getDomainName());
	  }
	  
	  public void setName(String name) {
	    setColumn(getColumnDomainName(),name);
	  }
	  
	  public String getDomainName() {
	    return(getStringColumnValue(getColumnDomainName()));
	  }
	  
	  public void setDomainName(String domainName){
		  setColumn(getColumnDomainName(),domainName);
	  }

	  public String getURL() {
	    return(getStringColumnValue(getColumnURL()));
	  }
	  
	  public void setURL(String url){
		  setColumn(getColumnURL(),url);
	  }

	  public Collection getTopLevelGroupsUnderDomain() throws IDORelationshipException, RemoteException, FinderException{

	    Collection relations = ((GroupDomainRelationHome)IDOLookup.getHome(GroupDomainRelation.class)).findGroupsRelationshipsUnderDomainByRelationshipType(this.getID(),GroupDomainRelationTypeBMPBean.RELATION_TYPE_TOP_NODE);
	    //TODO do this in one sql command like in groupbmpbean and grouprelation
	    Iterator iter = relations.iterator();
	    Collection groups = new Vector();
	    while (iter.hasNext()) {
	      GroupDomainRelation item = (GroupDomainRelation)iter.next();
	        groups.add(item.getRelatedGroup());
	    }

	    return groups;
	  }

	  public void setIBPage(ICPage page) {
	     setColumn(getColumnStartPage(),page);
	  }

	  public void setStartTemplate(ICPage template) {
	    setColumn(getColumnStartTemplate(),template);
	  }

//	  public void setGroup(Group group) {
//	     setColumn(COLUMNNAME_GROUP_ID,group);
//	  }


	  
	  public void setServerName(String serverName){
	      setColumn(COLUMNNAME_SERVER_NAME,serverName);
	  }
	  
	  public String getServerName(){
	      return getStringColumnValue(COLUMNNAME_SERVER_NAME);
	  }
	  
	  
	  public void setServerPort(int serverPort){
	      setColumn(COLUMNNAME_SERVER_PORT,serverPort);
	  }
	  
	  public int getServerPort(){
	      return getIntColumnValue(COLUMNNAME_SERVER_PORT);
	  } 
	  
	  
	  public void setServerContextPath(String serverContextPath){
	      setColumn(COLUMNNAME_SERVER_CONTEXT_PATH,serverContextPath);
	  }
	  
	  public String getServerContextPath(){
	      return getStringColumnValue(COLUMNNAME_SERVER_CONTEXT_PATH);
	  }
	  
	  public void setServerProtocol(String serverProtocol){
	      setColumn(COLUMNNAME_SERVER_PROTOCOL,serverProtocol);
	  }
	  
	  public String getServerProtocol(){
	      return getStringColumnValue(COLUMNNAME_SERVER_PROTOCOL);
	  } 

	  public Collection ejbFindAllDomains() throws FinderException {
	    String sql = "select * from " + getTableName();
	    return super.idoFindPKsBySQL(sql);
	  }
	  
	  public Collection ejbFindAllDomainsByServerName(String serverName) throws FinderException{
	  	IDOQuery query = idoQueryGetSelect();
	  	query.appendWhereEqualsWithSingleQuotes(COLUMNNAME_SERVER_NAME,serverName);
	  	System.out.println(query.toString());
	  	return idoFindPKsByQuery(query);
	  }
	  
	  public String getURLWithoutLastSlash(){
		  String url = getURL();
		  if(url!=null){
			  if(url.endsWith("/")){
				  return url.substring(0,url.length()-1);
			  }
			  return url;
		  }
		  else{
			  return null;
		  }
	  }
	  
	  /**
	   * Get the UUID for the domain:
	   */
	  public String getUniqueId(){
	  	return super.getUniqueId();
	  }


	/**
	 * <p>
	 * Finds the first domain (Default Domain) in the table.
	 * </p>
	 * @return
	 */
	public Object ejbFindFirstDomain() throws FinderException{
	  	SelectQuery query = idoSelectPKQuery();
	  	Table t = new Table(this);
	  	query.addOrder(t,getIDColumnName(),true);
	  	return idoFindOnePKByQuery(query);
	}
}
