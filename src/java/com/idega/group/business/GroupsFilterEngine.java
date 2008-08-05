package com.idega.group.business;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

import javax.ejb.EJBException;

import org.jdom.Document;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.idega.business.IBOLookup;
import com.idega.business.IBOLookupException;
import com.idega.core.accesscontrol.business.NotLoggedOnException;
import com.idega.core.builder.business.BuilderService;
import com.idega.core.builder.business.BuilderServiceFactory;
import com.idega.group.bean.GroupFilterResult;
import com.idega.presentation.IWContext;
import com.idega.presentation.groups.FilteredGroupsBox;
import com.idega.repository.data.Singleton;
import com.idega.user.business.UserBusiness;
import com.idega.user.data.Group;
import com.idega.user.data.User;
import com.idega.util.CoreUtil;
import com.idega.util.ListUtil;
import com.idega.util.StringUtil;

/**
 * 
 * @author <a href="mailto:valdas@idega.com">Valdas Žemaitis</a>
 * @version Revision: 1.00
 *
 * Last modified: 2008.07.31 09:30:25 by: valdas
 */

@Scope("singleton")
@Service(GroupsFilterEngine.SPRING_BEAN_IDENTIFIER)
public class GroupsFilterEngine implements Singleton {

	public static final String SPRING_BEAN_IDENTIFIER = "groupsFilterEngine";
	
	private GroupsFilterEngine() {}
	
	public Document getFilteredGroups(String searchKey, String selectedGroupName, List<String> selectedGroups) {
		if (StringUtil.isEmpty(searchKey)) {
			return null;
		}
		
		IWContext iwc = CoreUtil.getIWContext();
		if (iwc == null) {
			return null;
		}
		User currentUser = null;
		try {
			currentUser = iwc.getCurrentUser();
		} catch(NotLoggedOnException e) {
			e.printStackTrace();
		}
		if (currentUser == null) {
			return null;
		}
		
		FilteredGroupsBox filteredGroups = new FilteredGroupsBox();
		filteredGroups.setFilteredGroups(getUserGroupsBySearchKey(iwc, searchKey));
		filteredGroups.setSearchResult(Boolean.TRUE);
		filteredGroups.setSelectedGroups(selectedGroups);
		filteredGroups.setSelectedGroupParameterName(selectedGroupName);
		
		BuilderService builderService = null;
		try {
			builderService = BuilderServiceFactory.getBuilderService(iwc);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		if (builderService == null) {
			return null;
		}
		
		return builderService.getRenderedComponent(iwc, filteredGroups, false);
	}
	
	@SuppressWarnings("unchecked")
	private List<GroupFilterResult> getUserGroupsBySearchKey(IWContext iwc, String searchKey) {
		UserBusiness userBusiness = null;
		try {
			userBusiness = (UserBusiness) IBOLookup.getServiceInstance(iwc, UserBusiness.class);
		} catch (IBOLookupException e) {
			e.printStackTrace();
		}
		if (userBusiness == null) {
			return null;
		}
		
		Collection<Group> userTopGroups = null;
		try {
			userTopGroups = userBusiness.getUsersTopGroupNodesByViewAndOwnerPermissions(iwc.getCurrentUser(), iwc);
		} catch (EJBException e) {
			e.printStackTrace();
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		if (ListUtil.isEmpty(userTopGroups)) {
			return null;
		}
		
		Locale locale = iwc.getCurrentLocale();
		List<GroupFilterResult> results = new ArrayList<GroupFilterResult>();
		checkGroupsByQuery(results, userTopGroups, searchKey, locale, 0);
		
		return ListUtil.isEmpty(results) ? null : results;
	}
	
	@SuppressWarnings("unchecked")
	private void checkGroupsByQuery(List<GroupFilterResult> results, Collection<Group> groups, String searchKey, Locale locale, int level) {
		if (ListUtil.isEmpty(groups)) {
			return;
		}
		
		String name = null;
		for (Group group: groups) {
			name = group.getNodeName(locale).toLowerCase(locale);
			
			if (name.indexOf(searchKey) != -1) {
				results.add(new GroupFilterResult(level, group));
			}
			
			checkGroupsByQuery(results, group.getChildren(), searchKey, locale, level + 1);
		}
	}
	
}
