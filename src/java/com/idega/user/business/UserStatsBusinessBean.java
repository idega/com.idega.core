/*
 * Created on Jan 20, 2005
 */
package com.idega.user.business;

import java.rmi.RemoteException;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import javax.ejb.FinderException;

import com.idega.block.datareport.util.FieldsComparator;
import com.idega.block.datareport.util.ReportableCollection;
import com.idega.block.datareport.util.ReportableData;
import com.idega.block.datareport.util.ReportableField;
import com.idega.business.IBOLookup;
import com.idega.business.IBOSessionBean;
import com.idega.core.contact.data.Phone;
import com.idega.core.contact.data.PhoneType;
import com.idega.core.location.data.Address;
import com.idega.data.IDOLookup;
import com.idega.data.IDOLookupException;
import com.idega.idegaweb.IWBundle;
import com.idega.idegaweb.IWResourceBundle;
import com.idega.user.data.Group;
import com.idega.user.data.GroupHome;
import com.idega.user.data.User;
import com.idega.user.data.UserStatus;
import com.idega.user.data.UserStatusHome;
import com.idega.util.IWTimestamp;
import com.idega.util.Timer;
import com.idega.util.text.TextSoap;

/**
 * @author Sigtryggur
 *
 */
public class UserStatsBusinessBean extends IBOSessionBean  implements UserStatsBusiness{
    
    private UserBusiness userBiz = null;
    private GroupBusiness groupBiz = null;
	private IWBundle _iwb = null;
	private IWResourceBundle _iwrb = null;
	private final static String IW_BUNDLE_IDENTIFIER = "com.idega.user";

	private static final String LOCALIZED_CURRENT_DATE = "UserStatsBusiness.current_date";
	private static final String LOCALIZED_NAME = "UserStatsBusiness.name";
	private static final String LOCALIZED_PERSONAL_ID = "UserStatsBusiness.personal_id";
	private static final String LOCALIZED_GROUP_NAME = "UserStatsBusiness.group_name";
	private static final String LOCALIZED_USER_STATUS = "UserStatsBusiness.user_status";
	private static final String LOCALIZED_DATE_OF_BIRTH = "UserStatsBusiness.date_of_birth";
	private static final String LOCALIZED_STREET_ADDRESS = "UserStatsBusiness.street_address";
	private static final String LOCALIZED_POSTAL_ADDRESS = "UserStatsBusiness.postal_address";
	private static final String LOCALIZED_PHONE = "UserStatsBusiness.phone";

	private static final String FIELD_NAME_NAME = "name";
	private static final String FIELD_NAME_PERSONAL_ID = "personal_id";
	private static final String FIELD_NAME_GROUP_NAME = "group_name";
	private static final String FIELD_NAME_USER_STATUS = "user_status";
	private static final String FIELD_NAME_DATE_OF_BIRTH = "date_of_birth";
	private static final String FIELD_NAME_STREET_ADDRESS = "street_address";
	private static final String FIELD_NAME_POSTAL_ADDRESS = "postal_address";
	private static final String FIELD_NAME_PHONE = "phone";
	
	private void initializeBundlesIfNeeded() {
		if (_iwb == null) {
			_iwb = this.getIWApplicationContext().getIWMainApplication().getBundle(IW_BUNDLE_IDENTIFIER);
		}
		_iwrb = _iwb.getResourceBundle(this.getUserContext().getCurrentLocale());
	}

    
    public ReportableCollection getStatisticsForUsers(String groupIDFilter, String groupsRecursiveFilter, Collection groupTypesFilter, Collection userStatusesFilter, Integer yearOfBirthFromFilter, Integer yearOfBirthToFilter, String genderFilter) throws RemoteException {        

        initializeBundlesIfNeeded();
		ReportableCollection reportCollection = new ReportableCollection();
		Locale currentLocale = this.getUserContext().getCurrentLocale();
		
		//PARAMETES
		//Add extra...because the inputhandlers supply the basic header texts
		
		reportCollection.addExtraHeaderParameter(
				"label_current_date", _iwrb.getLocalizedString(LOCALIZED_CURRENT_DATE, "Current date"),
				"current_date", TextSoap.findAndCut((new IWTimestamp()).getLocaleDateAndTime(currentLocale, IWTimestamp.LONG,IWTimestamp.SHORT),"GMT"));
 	
		//PARAMETERS that are also FIELDS
		 //data from entity columns, can also be defined with an entity definition, see getClubMemberStatisticsForRegionalUnions method
		 //The name you give the field/parameter must not contain spaces or special characters		
		 ReportableField nameField = new ReportableField(FIELD_NAME_NAME, String.class);
		 nameField.setLocalizedName(_iwrb.getLocalizedString(LOCALIZED_NAME, "Name"), currentLocale);
		 reportCollection.addField(nameField);
		 
		 ReportableField personalIDField = new ReportableField(FIELD_NAME_PERSONAL_ID, String.class);
		 personalIDField.setLocalizedName(_iwrb.getLocalizedString(LOCALIZED_PERSONAL_ID, "Personal ID"),currentLocale);
		 reportCollection.addField(personalIDField);
		 
		 ReportableField groupField = new ReportableField(FIELD_NAME_GROUP_NAME, String.class);
		 groupField.setLocalizedName(_iwrb.getLocalizedString(LOCALIZED_GROUP_NAME, "Group"), currentLocale);
		 reportCollection.addField(groupField);
		 
		 ReportableField userStatusField = new ReportableField(FIELD_NAME_USER_STATUS, String.class);
		 userStatusField.setLocalizedName(_iwrb.getLocalizedString(LOCALIZED_USER_STATUS, "User Status"), currentLocale);
		 reportCollection.addField(userStatusField);
		 
		 ReportableField dateOfBirthField = new ReportableField(FIELD_NAME_DATE_OF_BIRTH, String.class);
		 dateOfBirthField.setLocalizedName(_iwrb.getLocalizedString(LOCALIZED_DATE_OF_BIRTH, "Date of Birth"), currentLocale);
		 reportCollection.addField(dateOfBirthField);
		 
		 ReportableField streetAddressField = new ReportableField(FIELD_NAME_STREET_ADDRESS, String.class);
		 streetAddressField.setLocalizedName(_iwrb.getLocalizedString(LOCALIZED_STREET_ADDRESS, "Street Address"), currentLocale);
		 reportCollection.addField(streetAddressField);
		 
		 ReportableField postalAddressField = new ReportableField(FIELD_NAME_POSTAL_ADDRESS, String.class);
		 postalAddressField.setLocalizedName(_iwrb.getLocalizedString(LOCALIZED_POSTAL_ADDRESS, "Postal Address"), currentLocale);
		 reportCollection.addField(postalAddressField); 
		 
		 ReportableField phoneField = new ReportableField(FIELD_NAME_PHONE, String.class);
		 phoneField.setLocalizedName(_iwrb.getLocalizedString(LOCALIZED_PHONE, "Phone"), currentLocale);
		 reportCollection.addField(phoneField);
		
		Group group = null;
		Collection groups = null;
		Collection users = null;
		try {
		    if (groupIDFilter != null && !groupIDFilter.equals("")) {
		        groupIDFilter = groupIDFilter.substring(groupIDFilter.lastIndexOf("_")+1);
		        group = getGroupBusiness().getGroupByGroupID(Integer.parseInt((groupIDFilter)));
		    }
			if (group != null) {
			    if (groupsRecursiveFilter != null && groupsRecursiveFilter.equals("checked")) {
				    groups = getGroupBusiness().getChildGroupsRecursiveResultFiltered(group, groupTypesFilter, true);
				} else {
				    groups.add(group);
				}
			}
		} catch (FinderException e) {
		    e.printStackTrace();
    	}
			users = getUserBusiness().getUsersBySpecificGroupsUserstatusDateOfBirthAndGender(groups, userStatusesFilter, yearOfBirthFromFilter,  yearOfBirthToFilter,  genderFilter);
			Iterator iter = users.iterator();
			 while (iter.hasNext()) {
			     User user = (User) iter.next();
			     Collection parentGroupCollection = null;
			     try {
			         parentGroupCollection = getGroupHome().findParentGroups(Integer.parseInt(user.getGroup().getPrimaryKey().toString()));
			     } catch (FinderException e) {
			         System.out.println(e.getMessage());
			     }
			     parentGroupCollection.retainAll(groups);
			     Iterator parIt = parentGroupCollection.iterator();
			   
			   	String personalID = user.getPersonalID();
			   	if (personalID != null && personalID.length() == 10) {
			 		personalID = personalID.substring(0,6)+"-"+personalID.substring(6,10);
			 	}
			   	String dateOfBirthString = null;
			   	if (user.getDateOfBirth() != null) {
			   	 dateOfBirthString = new IWTimestamp(user.getDateOfBirth()).getDateString("dd.MM.yy");
			   	}
			    Collection addresses = user.getAddresses();
			    
			    Address address = null;
			    String streetAddressString = null;
			    String postalAddressString = null;
			   	if (!addresses.isEmpty()) {
			   	    address = (Address)addresses.iterator().next();
			   	    streetAddressString = address.getStreetAddress();
			   	    postalAddressString = address.getPostalAddress();
			   	}
			     while (parIt.hasNext()) {				     
			         Group parentGroup = (Group)parIt.next();
			         List userStatuses = null;
			         String userStatusString = null;
			         try {
			             userStatuses = (List) ((UserStatusHome)IDOLookup.getHome(UserStatus.class)).findAllActiveByUserIdAndGroupId(Integer.parseInt(user.getPrimaryKey().toString()),Integer.parseInt(parentGroup.getPrimaryKey().toString()));
			         } catch (FinderException e) {
			             System.out.println(e.getMessage());
			         }
			         if (userStatuses.isEmpty()) {
			             if (userStatusesFilter != null && !userStatusesFilter.isEmpty()) {
			                 continue;
			             }
			         }
			         else {
			             UserStatus userStatus =(UserStatus)userStatuses.iterator().next();
			             String userStatusKey = userStatus.getStatus().getStatusKey();
			             if (!userStatusesFilter.contains(userStatusKey)) {			                 
			                 continue;
			             }
			             else {
			                 userStatusString = _iwrb.getLocalizedString(userStatusKey);
			             }
			             
			         }
			         
			         String parentGroupName = parentGroup.getName();
			         
			         Group pparentGroup = null;
			         Collection pparentGroupCollection = null;
			         try {
				         pparentGroupCollection = getGroupHome().findParentGroups(Integer.parseInt(parentGroup.getPrimaryKey().toString()));
				     }
			         catch (FinderException e) {
				         System.out.println(e.getMessage());
				     }
				     if (!pparentGroupCollection.isEmpty()) {
				         pparentGroup = (Group)pparentGroupCollection.iterator().next();
				         parentGroupName = parentGroupName+"-"+pparentGroup.getName(); 
				     }
				     
				     // create a new ReportData for each row	    
			         ReportableData data = new ReportableData();
				     //	add the data to the correct fields/columns
				     data.addData(nameField, user.getName() );
				     data.addData(personalIDField, personalID);
				     data.addData(groupField, parentGroupName);	     
				     data.addData(userStatusField, userStatusString);
				     data.addData(dateOfBirthField, dateOfBirthString);
				     data.addData(streetAddressField, streetAddressString);
				     data.addData(postalAddressField, postalAddressString);
				     data.addData(phoneField, getPhoneNumber(user));
				     reportCollection.add(data);
			 	}
			 }
    	
    	ReportableField[] sortFields = new ReportableField[] {groupField, nameField, personalIDField };
		Comparator comparator = new FieldsComparator(sortFields);
		Collections.sort(reportCollection, comparator);
		return reportCollection;
    }

    private String getPhoneNumber(User user) {
		Collection phones = user.getPhones();
		String phoneNumber = "";
		if (!phones.isEmpty()) {
			Phone phone = null;
			int tempPhoneType = 0;			
			int selectedPhoneType = 0;
			
			Iterator phIt =	phones.iterator();
			while (phIt.hasNext()) {
				phone = (Phone) phIt.next();
				if (phone != null) {
					tempPhoneType = phone.getPhoneTypeId();
					if (tempPhoneType != PhoneType.FAX_NUMBER_ID) {
						if (tempPhoneType == PhoneType.MOBILE_PHONE_ID) {							
							phoneNumber = phone.getNumber();
							break;
						}
						else if (tempPhoneType == PhoneType.HOME_PHONE_ID && selectedPhoneType != PhoneType.HOME_PHONE_ID) {
							phoneNumber = phone.getNumber();
							selectedPhoneType = phone.getPhoneTypeId();
						}
						else if (tempPhoneType == PhoneType.WORK_PHONE_ID && selectedPhoneType != PhoneType.WORK_PHONE_ID) {
							phoneNumber = phone.getNumber();
							selectedPhoneType = phone.getPhoneTypeId();
						}
					}
				}
			}
		}
		return phoneNumber;
    }
    
    private GroupHome getGroupHome() {
        try {
            return (GroupHome) IDOLookup.getHome(Group.class);
        } catch (IDOLookupException e) {
            e.printStackTrace();
        }
        return null;
    }

    private GroupBusiness getGroupBusiness() throws RemoteException {
		if (groupBiz == null) {
			groupBiz = (GroupBusiness) IBOLookup.getServiceInstance(this.getIWApplicationContext(), GroupBusiness.class);
		}	
		return groupBiz;
	}

	private UserBusiness getUserBusiness() throws RemoteException {
		if (userBiz == null) {
			userBiz = (UserBusiness) IBOLookup.getServiceInstance(this.getIWApplicationContext(), UserBusiness.class);
		}	
		return userBiz;
	}
}