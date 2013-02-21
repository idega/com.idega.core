package com.idega.core.location.data;

import java.util.Collection;

import javax.ejb.CreateException;
import javax.ejb.FinderException;

import com.idega.data.GenericEntity;
import com.idega.data.IDOFinderException;
import com.idega.data.IDOLookup;
import com.idega.data.IDOLookupException;
import com.idega.data.IDOQuery;
import com.idega.user.data.Group;
import com.idega.util.CoreConstants;


public class CommuneBMPBean extends GenericEntity implements Commune {

	private static String COMMUNE_ENTITY_NAME = "ic_commune";

	private static String COLUMN_COMMUNE_NAME = "commune_name";
	private static String COLUMN_COMMUNE = "commune";
	private static String COLUMN_COMMUNE_CODE = "commune_code";
	private static String COLUMN_PROVINCE_ID = "ic_province_id";
	private static String COLUMN_GROUP_ID = "ic_group_id";
	private static String COLUMN_DEFAULT = "default_commune";
	private static String COLUMN_COMMUNE_WEB_URL = "web_url";
	private static String COLUMN_VALID = "IS_VALID";

	private static String OTHER = "Other";

	public CommuneBMPBean(){
		super();
	}

	@Override
	public void initializeAttributes(){
		addAttribute(getIDColumnName());
		addAttribute(COLUMN_COMMUNE_NAME, "Commune", true, true, String.class,50);
		addAttribute(COLUMN_COMMUNE, "Commune name uppercase", true, true, String.class, 50);
		addAttribute(COLUMN_COMMUNE_CODE, "Commune code", true, true, String.class, 20);
		addAttribute(COLUMN_COMMUNE_WEB_URL, "Commune website", true, true, String.class,255);
		addManyToOneRelationship(COLUMN_PROVINCE_ID, "Province", Province.class);
		addManyToOneRelationship(COLUMN_GROUP_ID, "Group", Group.class);
		addAttribute(COLUMN_DEFAULT, "Default commune", true, true, Boolean.class);
		addAttribute(COLUMN_VALID, "valid", true, true, Boolean.class);
	}

	@Override
	public String getEntityName(){
		return COMMUNE_ENTITY_NAME;
	}

	@Override
	public void setDefaultValues() {
		setIsValid(true);
	}

	/**
	 * All names are stored in uppercase, uses String.toUpperCase();
	 */
	@Override
	public void setCommuneName(String name){
		setColumn(COLUMN_COMMUNE_NAME, name);
		setColumn(COLUMN_COMMUNE, name.toUpperCase());
	}

	@Override
	public String getCommuneName(){
		return getStringColumnValue(COLUMN_COMMUNE_NAME);
	}

	@Override
	public void setCommuneWebsiteURL(String URL){
		setColumn(COLUMN_COMMUNE_WEB_URL, URL);
	}

	@Override
	public String getCommuneWebsiteURL(){
		return getStringColumnValue(COLUMN_COMMUNE_WEB_URL);
	}

	@Override
	public void setCommuneCode(String code) {
		setColumn(COLUMN_COMMUNE_CODE, code);
	}

	@Override
	public String getCommuneCode() {
		return getStringColumnValue(COLUMN_COMMUNE_CODE);
	}

	@Override
	public void setProvince(Province province){
		setColumn(COLUMN_PROVINCE_ID,province);
	}

	@Override
	public Province getProvince(){
		return (Province)getColumnValue(COLUMN_PROVINCE_ID);
	}

	@Override
	public void setProvinceID(int province_id){
		setColumn(COLUMN_PROVINCE_ID,province_id);
	}

	@Override
	public int getProvinceID(){
		return getIntColumnValue(COLUMN_PROVINCE_ID);
	}

	@Override
	public void setGroup(Group group){
		setColumn(COLUMN_GROUP_ID,group);
	}

	@Override
	public Group getGroup(){
		return (Group)getColumnValue(COLUMN_GROUP_ID);
	}

	@Override
	public void setGroupID(int group_id){
		setColumn(COLUMN_GROUP_ID,group_id);
	}

	@Override
	public int getGroupID(){
		return getIntColumnValue(COLUMN_GROUP_ID);
	}

	@Override
	public boolean getIsValid() {
		return getBooleanColumnValue(COLUMN_VALID);
	}

	@Override
	public void setValid(boolean isValid) {
		setIsValid(isValid);
	}

	@Override
	public void setIsValid(boolean isValid) {
		setColumn(COLUMN_VALID, isValid);
	}

	@Override
	public boolean getIsDefault() {
		return getBooleanColumnValue(COLUMN_DEFAULT);
	}

	@Override
	public void setIsDefault(boolean isDefault) {
		if (isDefault == true) {
			try {
				Object defId = null;
				try {
					defId = ejbFindDefaultCommune();
				} catch (IDOFinderException ido) {}
				if (defId != null) {
					CommuneHome cHome = (CommuneHome) IDOLookup.getHome(Commune.class);
					Commune defaultCommune = cHome.findByPrimaryKey(defId);
					defaultCommune.setIsDefault(false);
					defaultCommune.store();
				}
			} catch (Exception e) {
				debug("No previous default commune found (Exception caught : "+e.getMessage()+")");
			}
		}
		setColumn(COLUMN_DEFAULT, isDefault);
	}

	public Object ejbFindDefaultCommune() throws FinderException {
		IDOQuery query = idoQuery();
		query.appendSelect().append(getIDColumnName()).appendFrom(this.getEntityName()).appendWhereEqualsQuoted(COLUMN_DEFAULT, CoreConstants.Y)
		.appendAndEqualsQuoted(COLUMN_VALID, CoreConstants.Y);
		return  idoFindOnePKByQuery(query);
	}

	public Collection ejbFindAllCommunes() throws FinderException {
		IDOQuery query = idoQuery();
		query.appendSelect().append(getIDColumnName()).appendFrom(this.getEntityName())
		.appendWhereEqualsQuoted(COLUMN_VALID, CoreConstants.Y)
		.appendOrderBy(COLUMN_COMMUNE);
		return idoFindPKsByQuery(query);
	}

	public Integer ejbFindByCommuneName(String name) throws FinderException {
		IDOQuery query = idoQuery();
		query.appendSelect().append(getIDColumnName()).appendFrom(this.getEntityName()).appendWhere()
		.appendEqualsQuoted(COLUMN_COMMUNE_NAME, name)
		.appendAndEqualsQuoted(COLUMN_VALID, CoreConstants.Y)
		.appendOrderBy(COLUMN_COMMUNE);
		return (Integer) idoFindOnePKByQuery(query);
	}
	public Integer ejbFindByCommuneNameAndProvince(String name, Object provinceID) throws FinderException {
		IDOQuery query = idoQuery();
		query.appendSelect().append(getIDColumnName()).appendFrom(this.getEntityName()).appendWhereEquals(COLUMN_PROVINCE_ID, provinceID).appendAndEqualsQuoted(COLUMN_COMMUNE_NAME, name)
		.appendAndEqualsQuoted(COLUMN_VALID, CoreConstants.Y)
		.appendOrderBy(COLUMN_COMMUNE);
		return (Integer) idoFindOnePKByQuery(query);
	}

	public Integer ejbFindByCommuneCode(String communeCode) throws FinderException {
		IDOQuery query = idoQuery();
		query.appendSelect().append(getIDColumnName()).appendFrom(this.getEntityName()).appendWhereEqualsWithSingleQuotes(COLUMN_COMMUNE_CODE, communeCode)
		.appendAndEqualsQuoted(COLUMN_VALID, CoreConstants.Y);
		return (Integer) idoFindOnePKByQuery(query);
	}

	public Object ejbFindOtherCommmuneCreateIfNotExist() throws IDOLookupException, CreateException, FinderException {
		CommuneHome communeHome = (CommuneHome)IDOLookup.getHome(Commune.class);

		Commune commune;
		try {
			commune = communeHome.findByCommuneName(OTHER);
		}
		catch (FinderException e) {
			commune = communeHome.create();
//			commune.setCommuneCode(OTHER);
			commune.setCommuneName(OTHER);
			commune.setIsValid(true);
			commune.store();
		}
		return commune.getPrimaryKey();
	}

	@Override
	public void remove() {
		setIsValid(false);
		store();
	}
}