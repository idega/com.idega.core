/**
 * 
 */
package com.idega.idegaweb.widget.general;

import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import com.idega.core.localisation.business.ICLocaleBusiness;
import com.idega.core.localisation.data.ICLocale;
import com.idega.idegaweb.widget.Widget;
import com.idega.presentation.IWContext;
import com.idega.presentation.PresentationObject;
import com.idega.presentation.text.Link;
import com.idega.presentation.text.ListItem;
import com.idega.presentation.text.Lists;


/**
 * <p>
 * TODO laddi Describe Type LanguageList
 * </p>
 *  Last modified: $Date: 2008/06/19 08:53:30 $ by $Author: laddi $
 * 
 * @author <a href="mailto:laddi@idega.com">laddi</a>
 * @version $Revision: 1.1 $
 */
public class LanguageList extends Widget {

	public LanguageList() {
		super();
		setStyleClass("languageList");
	}
	
	/* (non-Javadoc)
	 * @see com.idega.idegaweb.widget.Widget#getWidget(com.idega.presentation.IWContext)
	 */
	@Override
	protected PresentationObject getWidget(IWContext iwc) {
		List locales = ICLocaleBusiness.listOfLocales(true);
		
		Lists list = new Lists();
		Iterator iter = locales.iterator();
		while (iter.hasNext()) {
			ICLocale icLocale = (ICLocale) iter.next();
			Locale locale = icLocale.getLocaleObject();
			
			Link link = new Link(locale.getDisplayLanguage(locale));
			link.setLocale(locale);
			
			ListItem item = new ListItem();
			item.setStyleClass(locale.toString());
			if (locale.equals(getLocale())) {
				item.setStyleClass("current");
			}
			item.add(link);
			list.add(item);
		}
		
		
		return list;
	}
}