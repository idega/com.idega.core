package com.idega.core.location.business;

import java.rmi.RemoteException;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;
import javax.ejb.CreateException;
import javax.ejb.FinderException;

import com.idega.business.IBORuntimeException;
import com.idega.business.IBOServiceBean;
import com.idega.core.contact.data.Email;
import com.idega.core.contact.data.EmailHome;
import com.idega.core.location.data.Address;
import com.idega.core.location.data.AddressHome;
import com.idega.core.location.data.AddressType;
import com.idega.core.location.data.Commune;
import com.idega.core.location.data.CommuneHome;
import com.idega.core.location.data.Country;
import com.idega.core.location.data.CountryHome;
import com.idega.core.location.data.PostalCode;
import com.idega.core.location.data.PostalCodeHome;
import com.idega.util.text.TextSoap;

/**
 * <p>
 * Title: com.idega.core.business.AddressBusinessBean
 * </p>
 * <p>
 * Description: Common business class for handling all Address related IDO
 * </p>
 * <p>
 * Copyright: (c) 2002
 * </p>
 * <p>
 * Company: Idega Software
 * </p>
 * 
 * @author <a href="eiki@idega.is">Eirikur S. Hrafnsson</a>
 * @version 1.0
 */
public class AddressBusinessBean extends IBOServiceBean implements AddressBusiness {

	public static final String NOT_AVAILABLE = "N/A";

	public AddressBusinessBean() {
	}

	/**
	 * @return The Country Beans' home
	 */
	public CountryHome getCountryHome() {
		try {
			return (CountryHome) this.getIDOHome(Country.class);
		}
		catch (RemoteException re) {
			throw new IBORuntimeException(re);
		}
	}

	/**
	 * @return The Commune Beans' home
	 */
	public CommuneHome getCommuneHome() {
		try {
			return (CommuneHome) this.getIDOHome(Commune.class);
		}
		catch (RemoteException re) {
			throw new IBORuntimeException(re);
		}
	}

	/**
	 * @return The PostalCode Beans' home
	 */
	public PostalCodeHome getPostalCodeHome() {
		try {
			return (PostalCodeHome) this.getIDOHome(PostalCode.class);
		}
		catch (RemoteException re) {
			throw new IBORuntimeException(re);
		}
	}

	/**
	 * @return The Email Beans' home
	 */
	public EmailHome getEmailHome() throws RemoteException {
		return (EmailHome) this.getIDOHome(Email.class);
	}

	/**
	 * @return The Address Beans' home
	 */
	public AddressHome getAddressHome() {
		try {
			return (AddressHome) this.getIDOHome(Address.class);
		}
		catch (RemoteException re) {
			throw new IBORuntimeException(re);
		}
	}

	/**
	 * Finds and updates or Creates a new postal code
	 * 
	 * @return A new or updates PostalCode
	 */
	public PostalCode getPostalCodeAndCreateIfDoesNotExist(String postCode, String name) throws CreateException {
		PostalCode code;
		try {
			code = getPostalCodeHome().findByPostalCode(postCode);
		}
		catch (FinderException ex) {
			code = getPostalCodeHome().create();
			code.setPostalCode(postCode);
			code.setName(name);
			code.store();
		}
		return code;
	}

	/**
	 * Finds and updates or Creates a new postal code
	 * 
	 * @return A new or updates PostalCode
	 */
	public PostalCode getPostalCodeAndCreateIfDoesNotExist(String postCode, String name, Country country) throws CreateException {
		PostalCode code;
		try {
			code = getPostalCodeHome().findByPostalCodeAndCountryId(postCode, ((Integer) country.getPrimaryKey()).intValue());
		}
		catch (FinderException ex) {
			code = getPostalCodeHome().create();
			code.setPostalCode(postCode);
			code.setName(name);
			code.setCountry(country);
			code.store();
		}
		return code;
	}

	/**
	 * Connects the postal code with the given commune The commune code is set to
	 * the commune name if it is not already set. This is a simplification since
	 * we didn't have that data for Iceland
	 */
	public void connectPostalCodeToCommune(PostalCode postalCode, String Commune) throws CreateException {
		Commune commune = createCommuneIfNotExisting(Commune);
		postalCode.setCommune(commune);
		postalCode.store();
	}

	/**
	 * @param Commune
	 * @return
	 * @throws RemoteException
	 * @throws CreateException
	 */
	public Commune createCommuneIfNotExisting(String Commune) throws CreateException {
		CommuneHome communeHome = getCommuneHome();
		Commune commune;
		try {
			commune = communeHome.findByCommuneName(Commune);
		}
		catch (FinderException e) {
			commune = communeHome.create();
			// commune.setCommuneCode(Commune); //Set only if we find commune
			// code
			commune.setCommuneName(Commune);
			commune.setIsValid(true);
			commune.store();
		}
		return commune;
	}

	public Commune getOtherCommuneCreateIfNotExist() throws CreateException, FinderException {
		try {
			return getCommuneHome().findOtherCommmuneCreateIfNotExist();
		}
		catch (RemoteException re) {
			throw new IBORuntimeException(re);
		}
	}

	/**
	 * Change postal code name when only one address is related to the postalcode
	 */
	public PostalCode changePostalCodeNameWhenOnlyOneAddressRelated(PostalCode postalCode, String newName) {
		java.util.Collection addresses = postalCode.getAddresses();
		if (addresses != null && addresses.size() == 1) {
			postalCode.setName(newName);
			postalCode.store();
		}
		return postalCode;
	}

	/**
	 * Gets the streetname from a string with the format.<br>
	 * "Streetname Number ..." e.g. "My Street 24 982 NY" would return "My
	 * Street".<br>
	 * not very flexibel but handles "my street 24, 982 NY" the same way.
	 * 
	 * @return Finds the first number in the string and return a sbustring to that
	 *         point or the whole string if no number is present
	 */
	public String getStreetNameFromAddressString(String addressString) {
		int index = TextSoap.getIndexOfFirstNumberInString(addressString);
		if (index == -1) {
			return addressString;
		}
		else {
			return addressString.substring(0, index);
		}
	}

	/**
	 * Gets the streetnumber from a string with the format.<br>
	 * "Streetname Number ..." e.g. "My Street 24" would return "24".<br>
	 * 
	 * @return Finds the first number in the string and returns a substring from
	 *         that point or null if no number found
	 */
	public String getStreetNumberFromAddressString(String addressString) {
		int index = TextSoap.getIndexOfFirstNumberInString(addressString);
		if (index != -1) {
			return addressString.substring(index, addressString.length());
		}
		return null;
	}

	/**
	 * @return Returns a fully qualifying address string with streetname and
	 *         number,postal code and postal address, country its ISO
	 *         abbreviation, Commune name and commune code (uid)<br>
	 *         Each piece is seperated by a semicolon (";") e.g. : "Stafnasel
	 *         6;107 Reykjavik;Iceland:is_IS;Reykjavik 12345" <br>
	 *         If a piece is missing the string "N/A" (not available) is added
	 *         instead e.g. "Stafnasel 6;107 Reykjavik;Iceland:is_IS;N/A"
	 */
	public String getFullAddressString(Address address) {
		StringBuffer fullAddress = new StringBuffer();
		String streetNameAndNumber = address.getStreetAddress();
		String postalCode = address.getPostalAddress();
		Country country = address.getCountry();
		Commune commune = address.getCommune();
		if (streetNameAndNumber != null && !"".equals(streetNameAndNumber)) {
			fullAddress.append(streetNameAndNumber);
		}
		else {
			fullAddress.append(NOT_AVAILABLE);
		}
		fullAddress.append(";");
		if (postalCode != null) {
			fullAddress.append(postalCode);
		}
		else {
			fullAddress.append(NOT_AVAILABLE);
		}
		fullAddress.append(";");
		if (country != null) {
			String countryName = country.getName();
			String isoAbbr = country.getIsoAbbreviation();
			fullAddress.append(countryName);
			fullAddress.append(":");
			fullAddress.append(isoAbbr);
		}
		else {
			fullAddress.append(NOT_AVAILABLE);
		}
		fullAddress.append(";");
		if (commune != null) {
			String communeName = commune.getCommuneName();
			String code = commune.getCommuneCode();
			fullAddress.append(communeName);
			fullAddress.append(":");
			fullAddress.append(code);
		}
		else {
			fullAddress.append(NOT_AVAILABLE);
		}
		return fullAddress.toString();
	}

	/**
	 * Deserialized the fullAddressString and updates the address bean with what
	 * it finds and returns the address STORED
	 * 
	 * @param address
	 * @param fullAddressString
	 *          A fully qualifying address string with streetname and
	 *          number,postal code and postal address, country its ISO
	 *          abbreviation, Commune name and commune code (uid)<br>
	 *          Each piece is seperated by a semicolon (";") and country and
	 *          commune info by a ":"<br>
	 *          e.g. : "Stafnasel 6;107 Reykjavik;Iceland:is_IS;Reykjavik:12345"
	 *          <br>
	 *          If a piece is missing the string "N/A" (not available) should be
	 *          added instead e.g. "Stafnasel 6;107 Reykjavik;Iceland:is_IS;N/A"
	 * @return Deserialized the fullAddressString and updates the address bean
	 *         with what it finds and returns the address STORED
	 * @throws CreateException
	 * @throws RemoteException
	 */
	public Address getUpdatedAddressByFullAddressString(Address address, String fullAddressString) throws CreateException {
		String streetName = null;
		String streetNumber = null;
		String postalCode = null;
		String postalName = null;
		String countryName = null;
		String countryISOAbbr = null;
		String communeName = null;
		String communeCode = null;
		PostalCode postal = null;
		Country country = null;
		Commune commune = null;

		if (fullAddressString == null || "".equals(fullAddressString)) {
			// no change or should we delete the address? NO CHANGE for now
			return address;
		}

		// deserialize the string
		StringTokenizer nizer = new StringTokenizer(fullAddressString, ";");
		String streetNameAndNumber = NOT_AVAILABLE;
		String postalCodeAndPostalAddress = NOT_AVAILABLE;
		String countryNameAndISOAbbreviation = NOT_AVAILABLE;
		String communeNameAndCommuneCode = NOT_AVAILABLE;
		try {
			streetNameAndNumber = nizer.nextToken();
			postalCodeAndPostalAddress = nizer.nextToken();
			countryNameAndISOAbbreviation = nizer.nextToken();
			communeNameAndCommuneCode = nizer.nextToken();
		}
		catch (NoSuchElementException e) {
			// no need to print, we use what we can
			// e.printStackTrace();
		}

		// deserialize the string even more
		if (!NOT_AVAILABLE.equals(streetNameAndNumber)) {
			streetName = getStreetNameFromAddressString(streetNameAndNumber);
			streetNumber = getStreetNumberFromAddressString(streetNameAndNumber);
		}
		if (!NOT_AVAILABLE.equals(countryNameAndISOAbbreviation)) {
			countryName = countryNameAndISOAbbreviation.substring(0, countryNameAndISOAbbreviation.indexOf(":"));
			countryISOAbbr = countryNameAndISOAbbreviation.substring(countryNameAndISOAbbreviation.indexOf(":") + 1);
			// get country by iso...or name
			country = getCountryAndCreateIfDoesNotExist(countryName, countryISOAbbr);
		}
		if (!NOT_AVAILABLE.equals(communeNameAndCommuneCode)) {
			communeName = communeNameAndCommuneCode.substring(0, communeNameAndCommuneCode.indexOf(":"));
			communeCode = communeNameAndCommuneCode.substring(communeNameAndCommuneCode.indexOf(":") + 1);
			// get commune by code or name
			commune = getCommuneAndCreateIfDoesNotExist(communeName, communeCode);
		}
		if (!NOT_AVAILABLE.equals(postalCodeAndPostalAddress) && country != null) {
			postalCode = postalCodeAndPostalAddress.substring(0, postalCodeAndPostalAddress.indexOf(" "));
			postalName = postalCodeAndPostalAddress.substring(postalCodeAndPostalAddress.indexOf(" ") + 1);
			postal = getPostalCodeAndCreateIfDoesNotExist(postalCode, postalName, country);
		}

		// Set what we have and erase what we don't have
		if (streetName != null) {
			address.setStreetName(streetName);
		}
		else {
			// no nasty nullpointer there please..
			address.setStreetName("");
		}

		if (streetNumber != null) {
			address.setStreetNumber(streetNumber);
		}
		else {
			// Fix when entering unnumbered addresses, something I saw Aron do
			address.setStreetNumber("");
		}

		address.setCountry(country);
		address.setPostalCode(postal);
		address.setCommune(commune);
		// and store
		address.store();

		return address;
	}

	/**
	 * @param countryName
	 * @param countryISOAbbr
	 * @return
	 * @throws RemoteException
	 * @throws CreateException
	 */
	public Country getCountryAndCreateIfDoesNotExist(String countryName, String countryISOAbbr) throws CreateException {
		Country country = null;
		try {
			country = getCountryHome().findByIsoAbbreviation(countryISOAbbr);
		}
		catch (FinderException e) {
			try {
				country = getCountryHome().findByCountryName(countryName);
			}
			catch (FinderException e1) {
				log("No record of country in database for: " + countryName + " " + countryISOAbbr + " adding it...");
				country = getCountryHome().create();
				country.setName(countryName);
				country.setIsoAbbreviation(countryISOAbbr);
				country.store();
			}
		}
		return country;
	}

	public Country getCountry(String countryISOAbbreviation) throws FinderException {
		return getCountryHome().findByIsoAbbreviation(countryISOAbbreviation);
	}

	/**
	 * @param communeName
	 * @param communeCode
	 * @return
	 * @throws RemoteException
	 * @throws CreateException
	 */
	public Commune getCommuneAndCreateIfDoesNotExist(String communeName, String communeCode) throws CreateException {
		Commune commune = null;
		try {
			commune = getCommuneHome().findByCommuneCode(communeCode);
		}
		catch (FinderException e) {
			try {
				commune = getCommuneHome().findByCommuneName(communeName);
			}
			catch (FinderException e1) {
				log("No record of commune in database for: " + communeName + " " + communeCode + " adding it...");
				commune = getCommuneHome().create();
				commune.setCommuneName(communeName);
				commune.setCommuneCode(communeCode);
				commune.store();
			}
		}
		return commune;
	}

	public AddressType getMainAddressType() throws RemoteException {
		return getAddressHome().getAddressType1();
	}

	public AddressType getCOAddressType() throws RemoteException {
		return getAddressHome().getAddressType2();
	}

} // Class AddressBusinessBean
