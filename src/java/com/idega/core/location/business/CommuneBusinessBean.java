package com.idega.core.location.business;

import java.util.Collection;
import java.util.Vector;

import com.idega.business.IBOServiceBean;
import com.idega.core.location.data.Commune;
import com.idega.core.location.data.CommuneHome;
import com.idega.data.IDOLookup;
import com.idega.data.IDOLookupException;

/**
 * @author Gimmi
 */
public class CommuneBusinessBean extends IBOServiceBean implements CommuneBusiness {

	public Collection getCommunes() {
		Collection coll = new Vector();
		try {
			CommuneHome cHome = getCommuneHome();
			coll = cHome.findAllCommunes();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return coll;
	}
	
	public Commune getCommune(int communeId) {
		try {
			CommuneHome cHome = getCommuneHome();
			return cHome.findByPrimaryKey(new Integer(communeId));
		}	catch (Exception e) {
			e.printStackTrace(System.err);
			return null;
		}	
	}
	
	public Commune getCommune(String code) {
		try {
			return getCommuneHome().findByCommuneCode(code);
		} catch (Exception e) {
			//e.printStackTrace();
			return null;
		}
	}
	
	public Commune getDefaultCommune() {
		try {
			CommuneHome cHome = getCommuneHome();
			return cHome.findDefaultCommune();
		} catch (Exception e) {
			return null;
		}
	}
	
	public CommuneHome getCommuneHome() throws IDOLookupException {
		return (CommuneHome) IDOLookup.getHome(Commune.class);
	}
}
