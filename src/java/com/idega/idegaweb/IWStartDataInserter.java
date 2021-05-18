/**
 *
 */
package com.idega.idegaweb;

import java.sql.SQLException;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.CreateException;
import javax.ejb.FinderException;
import javax.faces.component.UIComponent;

import com.idega.core.builder.data.ICDomain;
import com.idega.core.builder.data.ICDomainHome;
import com.idega.core.builder.presentation.ICPropertyHandler;
import com.idega.core.component.data.ICObjectBMPBean;
import com.idega.core.component.data.ICObjectType;
import com.idega.core.component.data.ICObjectTypeHome;
import com.idega.core.contact.data.EmailType;
import com.idega.core.contact.data.EmailTypeBMPBean;
import com.idega.core.contact.data.EmailTypeHome;
import com.idega.core.contact.data.PhoneType;
import com.idega.core.contact.data.PhoneTypeBMPBean;
import com.idega.core.contact.data.PhoneTypeHome;
import com.idega.core.file.data.ICFileType;
import com.idega.core.file.data.ICFileTypeBMPBean;
import com.idega.core.file.data.ICFileTypeHandler;
import com.idega.core.file.data.ICFileTypeHandlerBMPBean;
import com.idega.core.file.data.ICFileTypeHandlerHome;
import com.idega.core.file.data.ICFileTypeHome;
import com.idega.core.localisation.data.ICLanguage;
import com.idega.core.localisation.data.ICLanguageHome;
import com.idega.core.localisation.data.ICLocale;
import com.idega.core.localisation.data.ICLocaleHome;
import com.idega.core.location.dao.AddressDAO;
import com.idega.core.location.data.AddressType;
import com.idega.core.location.data.AddressTypeBMPBean;
import com.idega.core.location.data.AddressTypeHome;
import com.idega.core.net.data.ICProtocol;
import com.idega.core.net.data.ICProtocolBMPBean;
import com.idega.core.net.data.ICProtocolHome;
import com.idega.core.search.business.SearchPlugin;
import com.idega.core.user.data.GenderBMPBean;
import com.idega.data.IDOEntity;
import com.idega.data.IDOHome;
import com.idega.data.IDOLookup;
import com.idega.data.IDOLookupException;
import com.idega.presentation.PresentationObject;
import com.idega.repository.data.Instantiator;
import com.idega.repository.data.RefactorClassRegistry;
import com.idega.repository.data.Singleton;
import com.idega.repository.data.SingletonRepository;
import com.idega.user.data.Gender;
import com.idega.user.data.GenderHome;
import com.idega.user.data.GroupDomainRelationType;
import com.idega.user.data.GroupDomainRelationTypeBMPBean;
import com.idega.user.data.GroupDomainRelationTypeHome;
import com.idega.user.data.GroupType;
import com.idega.user.data.GroupTypeBMPBean;
import com.idega.user.data.GroupTypeHome;
import com.idega.util.CoreConstants;
import com.idega.util.StringUtil;
import com.idega.util.expression.ELUtil;

/**
 * <p>
 * TODO laddi Describe Type IWStartDataInserter
 * </p>
 * Last modified: $Date: 2004/06/28 09:09:50 $ by $Author: laddi $
 *
 * @author <a href="mailto:laddi@idega.com">laddi</a>
 * @version $Revision: 1.1 $
 */
public class IWStartDataInserter implements Singleton {

	private static Instantiator instantiator = new Instantiator() {

		@Override
		public Object getInstance() {
			return new IWStartDataInserter();
		}
	};

	protected IWStartDataInserter() {
		super();
	}

	public static IWStartDataInserter getInstance() {
		return (IWStartDataInserter) SingletonRepository.getRepository().getInstance(IWStartDataInserter.class, instantiator);
	}

	public void insertStartData() {
		insertDefaultDomain();
		insertDefaultGroupTypes();
		insertDefaultGroupDomainRelation();
		insertDefaultObjectTypes();
		insertDefaultFileTypeHandlers();
		insertDefaultFileTypes();
		insertDefaultLocales();
		insertDefaultLanguages();
		insertDefaultCountries();
		insertDefaultEmailType();
		insertDefaultPhoneTypes();
		insertDefaultAddressTypes();
		insertDefaultProtocols();
		insertGenders();
	}

	private void insertDefaultDomain() {
		try {
			ICDomainHome dHome = (ICDomainHome) IDOLookup.getHome(ICDomain.class);
			try {
				dHome.findDefaultDomain();
			}
			catch (FinderException e) {
				ICDomain domain = dHome.create();
				domain.setName("Default Site");
				domain.setType(ICDomain.TYPE_DEFAULT);
				domain.store();
			}
		}
		catch (IDOLookupException e) {
			e.printStackTrace();
		}
		catch (CreateException e) {
			e.printStackTrace();
		}
	}

	private void insertDefaultGroupTypes() {
		insertGroupType(GroupTypeBMPBean.TYPE_GENERAL_GROUP, CoreConstants.EMPTY, true);
		insertGroupType(GroupTypeBMPBean.TYPE_PERMISSION_GROUP, CoreConstants.EMPTY, true);
		insertGroupType(GroupTypeBMPBean.TYPE_USER_REPRESENTATIVE, CoreConstants.EMPTY, false);
		insertGroupType(GroupTypeBMPBean.TYPE_ALIAS, "Alias group, points to another group", true);
	}

	private void insertGroupType(String type, String description, boolean visibility) {
		try {
			GroupTypeHome home = (GroupTypeHome) IDOLookup.getHome(GroupType.class);
			try {
				home.findByPrimaryKey(type);
			}
			catch (FinderException e) {
				GroupType groupType = home.create();
				groupType.setType(type);
				groupType.setDescription(description);
				groupType.setVisibility(visibility);
				groupType.store();
			}
		}
		catch (CreateException ex) {
			ex.printStackTrace();
		}
		catch (IDOLookupException e) {
			e.printStackTrace();
		}
	}

	private void insertDefaultFileTypeHandlers() {
		try {
			insertFileTypeHandler(ICFileTypeHandlerBMPBean.IC_FILE_TYPE_HANDLER_SYSTEM, RefactorClassRegistry.forName("com.idega.block.media.business.SystemTypeHandler"));
			insertFileTypeHandler(ICFileTypeHandlerBMPBean.IC_FILE_TYPE_HANDLER_APPLICATION, RefactorClassRegistry.forName("com.idega.block.media.business.ApplicationTypeHandler"));
			insertFileTypeHandler(ICFileTypeHandlerBMPBean.IC_FILE_TYPE_HANDLER_AUDIO, RefactorClassRegistry.forName("com.idega.block.media.business.AudioTypeHandler"));
			insertFileTypeHandler(ICFileTypeHandlerBMPBean.IC_FILE_TYPE_HANDLER_DOCUMENT, RefactorClassRegistry.forName("com.idega.block.media.business.DocumentTypeHandler"));
			insertFileTypeHandler(ICFileTypeHandlerBMPBean.IC_FILE_TYPE_HANDLER_IMAGE, RefactorClassRegistry.forName("com.idega.block.media.business.ImageTypeHandler"));
			insertFileTypeHandler(ICFileTypeHandlerBMPBean.IC_FILE_TYPE_HANDLER_VECTOR_GRAPHICS, RefactorClassRegistry.forName("com.idega.block.media.business.VectorTypeHandler"));
			insertFileTypeHandler(ICFileTypeHandlerBMPBean.IC_FILE_TYPE_HANDLER_VIDEO, RefactorClassRegistry.forName("com.idega.block.media.business.VideoTypeHandler"));
			insertFileTypeHandler(ICFileTypeHandlerBMPBean.IC_FILE_TYPE_HANDLER_ZIP, RefactorClassRegistry.forName("com.idega.block.media.business.ZipTypeHandler"));
		}
		catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	private void insertFileTypeHandler(String name, Class handlerClass) {
		try {
			ICFileTypeHandlerHome ftHome = (ICFileTypeHandlerHome) IDOLookup.getHome(ICFileTypeHandler.class);
			try {
				ftHome.findByName(name);
			}
			catch (FinderException e) {
				ICFileTypeHandler handler = ftHome.create();
				handler.setNameAndHandlerClass(name, handlerClass);
				handler.insert();
			}
		}
		catch (IDOLookupException e) {
			e.printStackTrace();
		}
		catch (CreateException e) {
			e.printStackTrace();
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private void insertDefaultFileTypes() {
		insertFileType("iW system", ICFileTypeBMPBean.IC_FILE_TYPE_SYSTEM, "IdegaWeb database file system types such as ic_folder", ICFileTypeHandlerBMPBean.IC_FILE_TYPE_HANDLER_SYSTEM);
		insertFileType("Applications", ICFileTypeBMPBean.IC_FILE_TYPE_APPLICATION, "Applications or executables", ICFileTypeHandlerBMPBean.IC_FILE_TYPE_HANDLER_APPLICATION);
		insertFileType("Audio", ICFileTypeBMPBean.IC_FILE_TYPE_AUDIO, "Audio files such as .mp3 .au", ICFileTypeHandlerBMPBean.IC_FILE_TYPE_HANDLER_AUDIO);
		insertFileType("Documents", ICFileTypeBMPBean.IC_FILE_TYPE_DOCUMENT, "Documents or textfiles such as .doc .xls .txt .html", ICFileTypeHandlerBMPBean.IC_FILE_TYPE_HANDLER_DOCUMENT);
		insertFileType("Images", ICFileTypeBMPBean.IC_FILE_TYPE_IMAGE, "Image files", ICFileTypeHandlerBMPBean.IC_FILE_TYPE_HANDLER_IMAGE);
		insertFileType("Vector graphics", ICFileTypeBMPBean.IC_FILE_TYPE_VECTOR_GRAPHICS, "Vector graphic files such as .swf (Flash) .dir (Shockwave)", ICFileTypeHandlerBMPBean.IC_FILE_TYPE_HANDLER_VECTOR_GRAPHICS);
		insertFileType("Video", ICFileTypeBMPBean.IC_FILE_TYPE_VIDEO, "Video or movie files such as .mov .mpg .avi", ICFileTypeHandlerBMPBean.IC_FILE_TYPE_HANDLER_VIDEO);
		insertFileType("Zip", ICFileTypeBMPBean.IC_FILE_TYPE_ZIP, "Zip archive files .zip", ICFileTypeHandlerBMPBean.IC_FILE_TYPE_HANDLER_ZIP);
	}

	private void insertFileType(String name, String type, String description, String fileTypeHandler) {
		try {
			ICFileTypeHome home = (ICFileTypeHome) IDOLookup.getHome(ICFileType.class);
			try {
				home.findByUniqueName(type);
			}
			catch (FinderException fe) {
				try {
					ICFileTypeHandlerHome ftHome = (ICFileTypeHandlerHome) IDOLookup.getHome(ICFileTypeHandler.class);
					ICFileTypeHandler handler = ftHome.findByName(fileTypeHandler);

					ICFileType fileType = home.create();
					fileType.setName(name);
					fileType.setType(type);
					fileType.setDescription(description);
					fileType.setFileTypeHandler(handler);
					fileType.insert();
				}
				catch (FinderException e) {
					e.printStackTrace();
				}
			}
		}
		catch (IDOLookupException e) {
			e.printStackTrace();
		}
		catch (CreateException e) {
			e.printStackTrace();
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private void insertDefaultLanguages() {
		try {
			ICLanguageHome home = (ICLanguageHome) IDOLookup.getHome(ICLanguage.class);
			String[] javaLocales = Locale.getISOLanguages();
			for (String iso: javaLocales) {
				try {
					home.findByISOAbbreviation(iso);
				} catch (FinderException fe) {
					Locale locale = new Locale(iso, CoreConstants.EMPTY);
					try {
						doCreateMissingLanguage(locale, home, iso);
					} catch (Exception e) {
						Logger.getLogger(getClass().getName()).warning("Error creating language from locale " + locale);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void doCreateMissingLanguage(Locale locale, ICLanguageHome home, String iso) throws Exception {
		ICLanguage language = home.create();
		language.setName(locale.getDisplayLanguage(locale));
		language.setIsoAbbreviation(iso);
		language.store();
	}

	private void insertDefaultLocales() {
		try {
			ICLocaleHome home = (ICLocaleHome) IDOLookup.getHome(ICLocale.class);
			Locale[] javaLocales = Locale.getAvailableLocales();
			for (Locale locale: javaLocales) {
				String localeString = locale.toString();
				try {
					home.findByLocaleName(localeString);
				} catch (FinderException e) {
					try {
						doCreateMissingLocale(home, localeString);
					}catch (Exception ex) {
						Logger.getLogger(getClass().getName()).log(
								Level.WARNING, 
								"Failed creating locale: (" + localeString + ")",
								ex
						);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void doCreateMissingLocale(ICLocaleHome home, String localeString) {
		if (StringUtil.isEmpty(localeString)) {
			return;
		}

		Logger logger= Logger.getLogger(getClass().getName());
		logger.info("Creating locale '" + localeString + "'");
		try {
			ICLocale icLocale = home.create();
			if (localeString.length() > 25) {
				localeString = localeString.substring(0, 25);
			}
			icLocale.setLocale(localeString);
			icLocale.setInUse(localeString.equals(Locale.ENGLISH.toString()));
			icLocale.store();
		} catch (Exception e) {
			logger.log(Level.WARNING, "Error creating locale " + localeString, e);
		}
	}

	private void insertDefaultCountries() {
		try {
			Locale locale = Locale.ENGLISH;
			String lang = Locale.ENGLISH.getISO3Language();

			AddressDAO addressDAO = ELUtil.getInstance().getBean(AddressDAO.class);
			String[] isoCountries = Locale.getISOCountries();
			for (String isoCountry : isoCountries) {
				com.idega.core.location.data.bean.Country country = null;
				try {
					country = addressDAO.getCountryByISOAbbreviation(isoCountry);
				} catch (Exception e) {}
				if (country == null) {
					Locale countryLocale = new Locale(lang, isoCountry);
					country = addressDAO.createCountry(countryLocale.getDisplayCountry(locale), isoCountry, null);
					if (country == null || country.getId() == null)
						Logger.getLogger(getClass().getName()).warning("Failed to create country: " + isoCountry);
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private void insertDefaultGroupDomainRelation() {
		try {
			GroupDomainRelationTypeHome home = (GroupDomainRelationTypeHome) IDOLookup.getHome(GroupDomainRelationType.class);
			try {
				home.findByPrimaryKey(GroupDomainRelationTypeBMPBean.RELATION_TYPE_TOP_NODE);
			}
			catch (FinderException e) {
				GroupDomainRelationType type = home.create();
				type.setType(GroupDomainRelationTypeBMPBean.RELATION_TYPE_TOP_NODE);
				type.setDescription(CoreConstants.EMPTY);
				type.store();
			}
		}
		catch (CreateException ex) {
			ex.printStackTrace();
		}
		catch (IDOLookupException e) {
			e.printStackTrace();
		}
	}

	private void insertDefaultEmailType() {
		try {
			EmailTypeHome home = (EmailTypeHome) IDOLookup.getHome(EmailType.class);
			try {
				home.findEmailTypeByUniqueName(EmailTypeBMPBean.MAIN_EMAIL);
			}
			catch (FinderException e) {
				EmailType email = home.create();
				email.setUniqueName(EmailTypeBMPBean.MAIN_EMAIL);
				email.setName(EmailTypeBMPBean.MAIN_EMAIL);
				email.setDescription("Main email");
				email.store();
			}
		}
		catch (IDOLookupException e) {
			e.printStackTrace();
		}
		catch (CreateException ce) {
			ce.printStackTrace();
		}
	}

	private void insertDefaultPhoneTypes() {
		insertPhoneType(PhoneTypeBMPBean.HOME_PHONE_ID, "home", PhoneTypeBMPBean.UNIQUE_NAME_HOME_PHONE);
		insertPhoneType(PhoneTypeBMPBean.WORK_PHONE_ID, "work", PhoneTypeBMPBean.UNIQUE_NAME_WORK_PHONE);
		insertPhoneType(PhoneTypeBMPBean.MOBILE_PHONE_ID, "mobile", PhoneTypeBMPBean.UNIQUE_NAME_MOBILE_PHONE);
		insertPhoneType(PhoneTypeBMPBean.FAX_NUMBER_ID, "fax", PhoneTypeBMPBean.UNIQUE_NAME_FAX_NUMBER);
	}

	private void insertPhoneType(int id, String name, String uniqueName) {
		try {
			PhoneTypeHome home = (PhoneTypeHome) IDOLookup.getHome(PhoneType.class);
			try {
				home.findByPrimaryKey(id);
			}
			catch (FinderException e) {
				PhoneType type = home.create();
				type.setID(id);
				type.setName(name);
				type.setUniqueName(uniqueName);
				type.store();
			}
		}
		catch (IDOLookupException e) {
			e.printStackTrace();
		}
		catch (CreateException e) {
			e.printStackTrace();
		}
	}

	private void insertDefaultObjectTypes() {
		String[][] startData = { { ICObjectBMPBean.COMPONENT_TYPE_ELEMENT, "Element", PresentationObject.class.getName(), null, PresentationObject.class.getName(), "set" }, { ICObjectBMPBean.COMPONENT_TYPE_BLOCK, "Block", PresentationObject.class.getName(), null, PresentationObject.class.getName(), "set" }, { ICObjectBMPBean.COMPONENT_TYPE_JSFUICOMPONENT, "JSF UIComponent", UIComponent.class.getName(), null, UIComponent.class.getName(), "set" }, { ICObjectBMPBean.COMPONENT_TYPE_APPLICATION, "Application", PresentationObject.class.getName(), null, PresentationObject.class.getName(), "set" }, { ICObjectBMPBean.COMPONENT_TYPE_APPLICATION_COMPONENT, "Application component", PresentationObject.class.getName(), null, PresentationObject.class.getName(), "set" }, { ICObjectBMPBean.COMPONENT_TYPE_DATA, "Data", null, IDOEntity.class.getName(), null, "get,set" }, { ICObjectBMPBean.COMPONENT_TYPE_HOME, "Home", null, IDOHome.class.getName(), null, "find,get" }, { ICObjectBMPBean.COMPONENT_TYPE_PROPERTYHANDLER, "Property handler", null, ICPropertyHandler.class.getName(), null, "set" }, { "iw.plugin.user", "User Plugin", null, null, null, null }, { ICObjectBMPBean.COMPONENT_TYPE_SEARCH_PLUGIN, "Search plugin", null, SearchPlugin.class.getName(), null, null } };

		for (int i = 0; i < startData.length; i++) {
			insertObjectType(startData[i][0], startData[i][1], startData[i][2], startData[i][3], startData[i][4], startData[i][5]);
		}
	}

	private void insertObjectType(String objectType, String objectName, String superClass, String interfaces, String reflection, String filters) {
		try {
			ICObjectTypeHome home = (ICObjectTypeHome) IDOLookup.getHome(ICObjectType.class);
			try {
				home.findByPrimaryKey(objectType);
			}
			catch (FinderException e) {
				ICObjectType type = home.create();
				type.setType(objectType);
				type.setName(objectName);
				type.setRequiredSuperClassName(superClass);
				type.setRequiredInterfacesString(interfaces);
				type.setFinalReflectionClassName(reflection);
				type.setMethodStartFiltersString(filters);
				type.store();
			}
		}
		catch (CreateException ex) {
			ex.printStackTrace();
		}
		catch (IDOLookupException e) {
			e.printStackTrace();
		}
	}

	private void insertDefaultAddressTypes() {
		try {
			AddressTypeHome home = (AddressTypeHome) IDOLookup.getHome(AddressType.class);

			try {
				home.findAddressType1();
			}
			catch (FinderException e) {
				AddressType type = home.create();
				type.setName("Home");
				type.setDescription("Home");
				type.setUniqueName(AddressTypeBMPBean.ADDRESS_1);
				type.store();
			}

			try {
				home.findAddressType2();
			}
			catch (FinderException e) {
				AddressType type = home.create();
				type.setName("Work");
				type.setDescription("Work");
				type.setUniqueName(AddressTypeBMPBean.ADDRESS_2);
				type.store();
			}
		}
		catch (IDOLookupException e) {
			e.printStackTrace();
		}
		catch (CreateException ce) {
			ce.printStackTrace();
		}
	}

	private void insertDefaultProtocols() {
		try {
			ICProtocolHome home = (ICProtocolHome) IDOLookup.getHome(ICProtocol.class);

			try {
				home.findByName(ICProtocolBMPBean._PROTOCOL_HTTP);
			}
			catch (FinderException e) {
				ICProtocol icp = home.create();
				icp.setName(ICProtocolBMPBean._PROTOCOL_HTTP);
				icp.setDescription("Hypertext Transfer Protocol");
				icp.store();
			}

			try {
				home.findByName(ICProtocolBMPBean._PROTOCOL_HTTPS);
			}
			catch (FinderException e) {
				ICProtocol icp = home.create();
				icp.setName(ICProtocolBMPBean._PROTOCOL_HTTPS);
				icp.setDescription("Hypertext Transfer Protocol Secure - Secure Socket Layer");
				icp.store();
			}
		}
		catch (IDOLookupException e) {
			e.printStackTrace();
		}
		catch (CreateException e) {
			e.printStackTrace();
		}
	}

	private void insertGenders() {
		insertGender(GenderBMPBean.NAME_MALE);
		insertGender(GenderBMPBean.NAME_FEMALE);
	}

	private void insertGender(String name) {
		try {
			GenderHome home = (GenderHome) IDOLookup.getHome(Gender.class);
			try {
				home.findByGenderName(name);
			}
			catch (FinderException e) {
				Gender gender = home.create();
				gender.setName(name);
				gender.store();
			}
		}
		catch (IDOLookupException e) {
			e.printStackTrace();
		}
		catch (CreateException e) {
			e.printStackTrace();
		}
	}
}