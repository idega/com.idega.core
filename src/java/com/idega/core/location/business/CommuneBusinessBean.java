package com.idega.core.location.business;

import java.rmi.RemoteException;
import java.util.Collection;
import java.util.Vector;
import javax.ejb.CreateException;
import javax.ejb.FinderException;
import com.idega.business.IBOServiceBean;
import com.idega.core.location.data.Commune;
import com.idega.core.location.data.CommuneHome;
import com.idega.core.location.data.PostalCode;
import com.idega.data.IDOLookup;
import com.idega.data.IDOLookupException;
import com.idega.user.data.Group;

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
	
	public Commune getCommuneByCode(String code) {
		try {
			return getCommuneHome().findByCommuneCode(code);
		} catch (Exception e) {
			//e.printStackTrace();
			return null;
		}
	}
	
	public Commune getCommuneByName(String name) {
		try {
			return getCommuneHome().findByCommuneName(name);
		} catch (Exception e) {
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
	
	public Commune getOtherCommuneCreateIfNotExist() throws CreateException, FinderException, RemoteException {
		return getCommuneHome().findOtherCommmuneCreateIfNotExist();
	}

	public Group getGroup(Commune commune) {
		return commune.getGroup();
	}
	
	public Commune getCommuneByPostalCode(PostalCode postalCode) {
		return postalCode.getCommune();
	}
	
	public CommuneHome getCommuneHome() throws IDOLookupException {
		return (CommuneHome) IDOLookup.getHome(Commune.class);
	}
}
