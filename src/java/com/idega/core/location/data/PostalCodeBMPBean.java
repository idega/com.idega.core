// idega 2000 - Eiki

package com.idega.core.location.data;

import java.rmi.RemoteException;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Vector;
import java.util.logging.Level;

import javax.ejb.FinderException;

import com.idega.data.GenericEntity;
import com.idega.data.IDOLookup;
import com.idega.data.IDOLookupException;
import com.idega.data.IDOStoreException;
import com.idega.data.SimpleQuerier;
import com.idega.data.query.AND;
import com.idega.data.query.Column;
import com.idega.data.query.Criteria;
import com.idega.data.query.InCriteria;
import com.idega.data.query.MatchCriteria;
import com.idega.data.query.OR;
import com.idega.data.query.SelectQuery;
import com.idega.data.query.Table;
import com.idega.data.query.WildCardColumn;
import com.idega.util.ListUtil;
import com.idega.util.StringUtil;

public class PostalCodeBMPBean extends GenericEntity implements PostalCode {

	private static final long serialVersionUID = 1106307465807244618L;

	static final String TABLE_NAME = "IC_POSTAL_CODE";

	static final String COLUMN_POSTAL_CODE_ID = "IC_POSTAL_CODE_ID";
	static final String COLUMN_POSTAL_CODE = "POSTAL_CODE";
	static final String COLUMN_NAME = "NAME";
	static final String COLUMN_POSTAL_ADDRESS = "POSTAL_ADDRESS";
	static final String COLUMN_COUNTRY_ID = "IC_COUNTRY_ID";
	static final String COLUMN_COMMUNE_ID = "IC_COMMUNE_ID";

	private boolean upperCase = true;

	public PostalCodeBMPBean() {
		super();
	}

	public PostalCodeBMPBean(int id) throws SQLException {
		super(id);
	}

	@Override
	public void initializeAttributes() {
		addAttribute(getIDColumnName());
		addAttribute(COLUMN_POSTAL_CODE, "Postalcode", true, true, String.class, 50);
		addAttribute(COLUMN_NAME, "Name", true, true, String.class, 50);
		addAttribute(COLUMN_POSTAL_ADDRESS, "Postaladdress", true, true, String.class, 50);
		addManyToOneRelationship(COLUMN_COUNTRY_ID, "Country", Country.class);
		addManyToOneRelationship(COLUMN_COMMUNE_ID, "Commmune", Commune.class);

		addIndex("IDX_IC_POSTALCODE", COLUMN_POSTAL_CODE);
		addIndex("IDX_IC_POSTALCODE2", new String[] { COLUMN_POSTAL_CODE, COLUMN_NAME });
		addIndex("IDX_IC_POSTALCODE3", new String[] { COLUMN_POSTAL_CODE, COLUMN_COUNTRY_ID });
		addIndex("IDX_IC_POSTALCODE4", COLUMN_COUNTRY_ID);
		getEntityDefinition().setBeanCachingActiveByDefault(true);
	}

	@Override
	public void insertStartData() throws Exception {
		/*
		 * java.util.List countries =
		 * EntityFinder.findAllByColumn(com.idega.core.data.CountryBMPBean.getStaticInstance(),"ISO_Abbreviation","IS");
		 * if (countries != null) { Country country = (Country) countries.get(0);
		 * PostalCode pCode; pCode =
		 * ((com.idega.core.data.PostalCodeHome)com.idega.data.IDOLookup.getHomeLegacy(PostalCode.class)).createLegacy();
		 * pCode.setPostalCode("101"); pCode.setName("Reykjavik");
		 * pCode.setCountryID(country.getID()); pCode.insert(); pCode =
		 * ((com.idega.core.data.PostalCodeHome)com.idega.data.IDOLookup.getHomeLegacy(PostalCode.class)).createLegacy();
		 * pCode.setPostalCode("200"); pCode.setName("K�pavogur");
		 * pCode.setCountryID(country.getID()); pCode.insert(); pCode =
		 * ((com.idega.core.data.PostalCodeHome)com.idega.data.IDOLookup.getHomeLegacy(PostalCode.class)).createLegacy();
		 * pCode.setPostalCode("201");< pCode.setName("K�pavogur");
		 * pCode.setCountryID(country.getID()); pCode.insert(); }
		 */
	}

	@Override
	public String getEntityName() {
		return TABLE_NAME;
	}

	private String stripWhitespace(String strip) {
		if (StringUtil.isEmpty(strip)) {
			return null;
		}

		String s = strip;
		return s.replaceAll("\\s", "");
	}

	@Override
	public void setPostalCode(String code) {
		code = stripWhitespace(code);
		setColumn(COLUMN_POSTAL_CODE, code);

		String name = getName();
		StringBuffer buffer = new StringBuffer();
		buffer.append(code);
		if (name != null) {
			buffer.append(" ").append(name);
		}
		setColumn(COLUMN_POSTAL_ADDRESS, buffer.toString());
	}

	@Override
	public String getPostalCode() {
		return getStringColumnValue(COLUMN_POSTAL_CODE);
	}

	@Override
	public void setCommuneID(String commune) {
		// remove all whitespace from postal code
		setColumn(COLUMN_COMMUNE_ID, commune);
	}

	@Override
	public String getCommuneID() {
		return getStringColumnValue(COLUMN_COMMUNE_ID);
	}

	@Override
	public void setCommune(Commune commune) {
		setColumn(COLUMN_COMMUNE_ID, commune);
	}

	@Override
	public Commune getCommune() {
		return (Commune) getColumnValue(COLUMN_COMMUNE_ID);
	}

	/**
	 * All names are stored in uppercase, uses String.toUpperCase();
	 */
	@Override
	public void setName(String name) {
		if (!StringUtil.isEmpty(name)) {
			setColumn(COLUMN_NAME, upperCase ? name.toUpperCase() : name);

			String code = getPostalCode();
			StringBuffer buffer = new StringBuffer();
			if (code != null) {
				buffer.append(code).append(" ");
			}

			buffer.append(name);
			setColumn(COLUMN_POSTAL_ADDRESS, buffer.toString());
		}
	}

	@Override
	public String getName() {
		return getStringColumnValue(COLUMN_NAME);
	}

	@Override
	public void setCountry(Country country) {
		setColumn(COLUMN_COUNTRY_ID, country);
	}

	@Override
	public Country getCountry() {
		return (Country) getColumnValue(COLUMN_COUNTRY_ID);
	}

	@Override
	public void setCountryID(int country_id) {
		setColumn(COLUMN_COUNTRY_ID, country_id);
	}

	@Override
	public int getCountryID() {
		return getIntColumnValue(COLUMN_COUNTRY_ID);
	}

	private void setPostalAddress(String postal_address) {
		setColumn(COLUMN_POSTAL_ADDRESS, postal_address);
	}

	public Collection ejbFindByCommune(Commune commune) throws FinderException {
		Table table = new Table(this);

		SelectQuery query = new SelectQuery(table);
		query.addColumn(table.getColumn(getIDColumnName()));
		query.addCriteria(new MatchCriteria(table.getColumn(COLUMN_COMMUNE_ID), MatchCriteria.EQUALS, commune));
		query.addOrder(table, COLUMN_POSTAL_CODE, true);

		return idoFindPKsByQuery(query);
	}

	/**
	 *
	 * <p>Finds primary keys by given postal codes.</p>
	 * @param codes is {@link Collection} of {@link PostalCode#getPostalCode()},
	 * not <code>null</code>;
	 * @return {@link Collection} of {@link PostalCode#getPrimaryKey()} or
	 * {@link Collections#emptyList()} on failure;
	 * @author <a href="mailto:martynas@idega.is">Martynas Stakė</a>
	 */
	public Collection<Object> ejbFindByPostalCode(Collection<String> codes) {
		if (ListUtil.isEmpty(codes)) {
			return Collections.emptyList();
		}

		Table table = new Table(this);

		SelectQuery query = new SelectQuery(table);
		query.addColumn(table.getColumn(getIDColumnName()));
		query.addCriteria(new InCriteria(table.getColumn(COLUMN_POSTAL_CODE), codes));
		query.addOrder(table, COLUMN_POSTAL_CODE, true);

		try {
			return idoFindPKsByQuery(query);
		} catch (FinderException e) {
			getLogger().log(Level.WARNING,
					"Failed to find primary keys by query: " + query +
					" cause of: ", e);
		}

		return Collections.emptyList();
	}

	public Object ejbFindByPostalCode(String code) throws FinderException {
		return ejbFindByPostalCodeAndCountryId(code, -1);
	}

	public Object ejbFindByPostalCodeAndCountryId(String code, int countryId) throws FinderException {
		Table table = new Table(this);
		Column codeCol = new Column(table, COLUMN_POSTAL_CODE);
		Column countryCol = new Column(table, COLUMN_COUNTRY_ID);

		SelectQuery query = new SelectQuery(table);
		query.addColumn(new Column(table, getIDColumnName()));

		query.addCriteria(new MatchCriteria(codeCol, MatchCriteria.EQUALS, code));
		if (countryId != -1) {
			query.addCriteria(new MatchCriteria(countryCol, MatchCriteria.EQUALS, countryId));
		}

		return this.idoFindOnePKByQuery(query);
	}

	public Collection ejbFindAllByCountryIdOrderedByPostalCode(int countryId) throws FinderException {
		Table table = new Table(this);
		Column countryCol = new Column(table, COLUMN_COUNTRY_ID);

		SelectQuery query = new SelectQuery(table);
		query.addColumn(new Column(table, getIDColumnName()));
		query.addCriteria(new MatchCriteria(countryCol, MatchCriteria.EQUALS, countryId));
		query.addOrder(table, COLUMN_POSTAL_CODE, true);

		return this.idoFindPKsByQuery(query);
	}

	/**
	 *
	 * @param countryId
	 * @return Collection of strings, containing names
	 * @throws FinderException
	 * @throws RemoteException
	 */
	public Collection ejbHomeGetUniquePostalCodeNamesByCountryIdOrderedByPostalCodeName(int countryId) throws FinderException {
		Table table = new Table(this);
		Column countryCol = new Column(table, COLUMN_COUNTRY_ID);

		SelectQuery query = new SelectQuery(table);
		query.addColumn(table, COLUMN_NAME, true);
		query.addCriteria(new MatchCriteria(countryCol, MatchCriteria.EQUALS, countryId));
		query.addGroupByColumn(table, COLUMN_NAME);
		query.addOrder(table, COLUMN_NAME, true);

		try {
			String[] names = SimpleQuerier.executeStringQuery(query.toString());
			Vector coll = new Vector();
			if (names != null) {
				for (int i = 0; i < names.length; i++) {
					coll.add(names[i]);
				}
			}
			return coll;
		}
		catch (Exception e) {
			throw new FinderException(e.getMessage());
		}
	}

	public Collection ejbFindByNameAndCountry(String name, Object countryPK) throws FinderException {
		Table table = new Table(this);
		Column nameCol = new Column(table, COLUMN_NAME);
		Column countryCol = new Column(table, COLUMN_COUNTRY_ID);

		SelectQuery query = new SelectQuery(table);
		query.addColumn(new Column(table, getIDColumnName()));

		query.addCriteria(new MatchCriteria(nameCol, MatchCriteria.EQUALS, name));
		query.addCriteria(new MatchCriteria(countryCol, MatchCriteria.EQUALS, countryPK));

		return this.idoFindPKsByQuery(query);
	}

	public Collection ejbFindByCountry(Object countryPK) throws FinderException {
		Table table = new Table(this);
		Column countryCol = new Column(table, COLUMN_COUNTRY_ID);

		SelectQuery query = new SelectQuery(table);
		query.addColumn(new Column(table, getIDColumnName()));

		query.addCriteria(new MatchCriteria(countryCol, MatchCriteria.EQUALS, countryPK));

		return this.idoFindPKsByQuery(query);
	}

	public Collection ejbFindAllUniqueNames() throws RemoteException, FinderException {
		Collection all = ejbFindAll();
		Collection names = new Vector();
		Collection pks = new Vector();
		if (all != null && !all.isEmpty()) {
			Iterator iter = all.iterator();
			PostalCode pc;
			while (iter.hasNext()) {
				pc = ((PostalCodeHome) this.getEJBLocalHome()).findByPrimaryKey(iter.next());
				if (!names.contains(pc.getName())) {
					names.add(pc.getName());
					pks.add(pc.getPrimaryKey());
				}
			}

		}

		return pks;
	}

	public Collection ejbFindAll() throws FinderException {
		return idoFindAllIDsBySQL();
	}

	public Collection ejbFindAllOrdererByCode() throws FinderException {
		return super.idoFindAllIDsOrderedBySQL(COLUMN_POSTAL_CODE);
	}

	@Override
	public String getPostalAddress() {
		String postalAddress = getStringColumnValue(COLUMN_POSTAL_ADDRESS);
		if (postalAddress == null) {
			StringBuffer addr = new StringBuffer();
			String code = getPostalCode();
			if (code != null) {
				addr.append(code).append(" ");
			}
			String name = this.getName();
			if (name != null) {
				addr.append(name);
			}
			postalAddress = addr.toString();
		}
		return postalAddress;
	}

	public Collection ejbFindByPostalCodeFromTo(String codeFrom, String codeTo) throws FinderException {
		return ejbFindByPostalCodeFromTo(new String[] { codeFrom }, new String[] { codeTo });
	}

	public Collection ejbFindByPostalCodeFromTo(String codeFrom[], String codeTo[]) throws FinderException {
		int fromLength = codeFrom.length;
		int toLength = codeTo.length;
		if (fromLength != toLength) {
			throw new FinderException("From and To arrays must be of same size");
		}

		Table postal = new Table(this);
		Column postalCode = new Column(postal, COLUMN_POSTAL_CODE);

		SelectQuery query = new SelectQuery(postal);
		query.addColumn(new WildCardColumn(postal));

		if (fromLength > 0) {

			Vector crits = new Vector();
			for (int i = 0; i < fromLength; i++) {
				if (codeTo[i] == null) {
					crits.add(new MatchCriteria(postalCode, MatchCriteria.EQUALS, codeFrom[i]));
				}
				else {
					AND and = new AND(new MatchCriteria(postalCode, MatchCriteria.GREATEREQUAL, codeFrom[i]), new MatchCriteria(postalCode, MatchCriteria.LESSEQUAL, codeTo[i]));
					crits.add(and);
				}
			}
			if (fromLength == 1) {
				query.addCriteria((Criteria) crits.get(0));
			}
			else {
				OR mainOR = new OR((Criteria) crits.get(0), (Criteria) crits.get(1));

				for (int i = 2; i < fromLength; i++) {
					mainOR = new OR(mainOR, (Criteria) crits.get(i));
				}

				query.addCriteria(mainOR);
			}
		}

		query.addOrder(postal, COLUMN_POSTAL_CODE, true);
		return this.idoFindPKsByQuery(query);

		// IDOQuery query = this.idoQuery();
		// query.append("Select * from ").append(getEntityName())
		// .append(" where ").append(COLUMN_POSTAL_CODE).append(" >=
		// '").append(codeFrom)
		// .append("' and ").append(COLUMN_POSTAL_CODE).append(" <=
		// '").append(codeTo)
		// .append("' order by ").append(COLUMN_POSTAL_CODE);
		// return this.idoFindPKsByQuery(query);
	}

	/**
	 * Gets related addresses null if no found
	 */
	@Override
	public Collection<Address> getAddresses() {
		try {
			return ((AddressHome) IDOLookup.getHome(Address.class)).findByPostalCode((Integer) getPrimaryKey());
		}
		catch (FinderException e) {

		}
		catch (IDOLookupException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof PostalCode) {
			PostalCode other = (PostalCode) obj;
			return other.getPostalCode().equals(this.getPostalCode());
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.idega.core.location.data.PostalCode#isEqualTo(com.idega.core.location.data.PostalCode)
	 */
	@Override
	public boolean isEqualTo(PostalCode postal) {
		if (postal != null) {
			return (this.getPostalCode().equalsIgnoreCase(postal.getPostalCode()));
			// && this.getName().equalsIgnoreCase(postal.getName()));
		}
		return false;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		if (getPostalCode() != null) {
			builder.append(getPostalCode());
		}
		if (builder.length() > 0) {
			builder.append(" ");
		}
		if (getName() != null) {
			builder.append(getName());
		}

		return builder.toString();
	}

	@Override
	public void store() throws IDOStoreException {
		setPostalAddress(getPostalCode() + " " + getName());
		super.store();
	}

	@Override
	public void setConvertToUpperCase(boolean upperCase) {
		this.upperCase = upperCase;
	}
}