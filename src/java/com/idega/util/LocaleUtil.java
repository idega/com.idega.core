package com.idega.util;

import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

import com.idega.core.localisation.business.ICLocaleBusiness;
import com.idega.util.datastructures.map.MapUtil;

/**
 * Title: idega Framework Description: Copyright: Copyright (c) 2001 Company:
 * idega
 *
 * @author <a href=mailto:"tryggvi@idega.is">Tryggvi Larusson</a>
 * @version 1.0
 */
public class LocaleUtil {

	private static Map<String, Locale> locales = new TreeMap<>();

	private static Locale icelandicLocale;
	private static Locale swedishLocale;

	private static final String ICELANDIC_IDENTIFIER = "is_IS";
	private static final String ENGLISH_IDENTIFIER = "en";
	private static final String US_IDENTIFIER = "en_US";
	private static final String UK_IDENTIFIER = "en_UK";

	private LocaleUtil() {
	}

	public static Locale getIcelandicLocale() {
		if (icelandicLocale == null) {
			icelandicLocale = new Locale("is", "IS");
		}
		return icelandicLocale;
	}

	public static Locale getSwedishLocale() {
		if (swedishLocale == null) {
			swedishLocale = new Locale("sv", "SE");
		}
		return swedishLocale;
	}

	public static Locale getLocale(String localeIdentifier) {
		if (StringUtil.isEmpty(localeIdentifier)) {
			return null;
		}

		if (localeIdentifier.equals(ICELANDIC_IDENTIFIER)) {
			return getIcelandicLocale();
		} else if (localeIdentifier.equals(ENGLISH_IDENTIFIER)) {
			return Locale.ENGLISH;
		} else if (localeIdentifier.equals(getSwedishLocale().toString())) {
			return getSwedishLocale();
		} else if (localeIdentifier.equals(US_IDENTIFIER)) {
			return Locale.US;
		} else if (localeIdentifier.equals(UK_IDENTIFIER)) {
			return Locale.UK;
		} else {
			return ICLocaleBusiness.getLocaleFromLocaleString(localeIdentifier);
		}
	}

	/**
	 *
	 * @return {@link Map} of {@link Locale#getCountry()} and {@link Locale};
	 */
	public static Map<String, Locale> getLocales() {
		if (MapUtil.isEmpty(locales)) {
			Locale[] localesArray = Locale.getAvailableLocales();
			for (Locale locale: localesArray) {
				if (locale != null && !StringUtil.isEmpty(locale.getCountry())) {
					locales.put(locale.getCountry(), locale);
				}
			}

			locales.put(Locale.US.getCountry(), Locale.US);
		}

		return locales;
	}

	/**
	 *
	 * @param locales {@link Map} of {@link Locale#getCountry()} and {@link Locale};
	 */
	public static void setLocales(Map<String, Locale> locales) {
		LocaleUtil.locales = locales;
	}

	/**
	 *
	 * @param countryCode is {@link Locale#getCountry()}, not <code>null</code>;
	 * @return {@link Locale} or <code>null</code> on failure;
	 */
	public static Locale getLocaleByCountry(String countryCode) {
		if (!StringUtil.isEmpty(countryCode)) {
			return getLocales().get(countryCode.toUpperCase());
		}

		return null;
	}

	/**
	 * Returns locale for given country
	 * @param countryName, i.e. Iceland
	 * @param countryNameInLocale - not required, i.e. {@code Locale.ENGLISH}
	 * @return {@link Locale}
	 */
	public static Locale getLocaleByCountryName(String countryName, Locale countryNameInLocale) {
		if (StringUtil.isEmpty(countryName)) {
			return null;
		}

		countryNameInLocale = countryNameInLocale == null ? Locale.ENGLISH: countryNameInLocale;

		for (Locale locale: getLocales().values()) {
			String countryNameForLocale = locale.getDisplayCountry(countryNameInLocale);
			if (countryName.equals(countryNameForLocale)) {
				return locale;
			}
		}

		return null;
	}

	public static String getLocalizedCountryName(Locale localeIn, String defaultName, String isoAbbreviation) {
		if (localeIn == null || StringUtil.isEmpty(isoAbbreviation)) {
			return defaultName;
		}

		Locale countryLocale = LocaleUtil.getLocaleByCountry(isoAbbreviation);
		if (countryLocale == null) {
			return defaultName;
		}

		String localizedCountryName = countryLocale.getDisplayCountry(localeIn);
		if (StringUtil.isEmpty(localizedCountryName)) {
			return defaultName;
		}

		return localizedCountryName;
	}

	public static String getLocalizedLanguageName(Locale localeIn, String defaultName, String isoAbbreviation) {
		if (localeIn == null || StringUtil.isEmpty(isoAbbreviation)) {
			return defaultName;
		}

		Map<String, Locale> locales = getLocales();
		if (MapUtil.isEmpty(locales)) {
			return defaultName;
		}

		Locale languageLocale = null;
		for (Iterator<Locale> iter = locales.values().iterator(); (languageLocale == null && iter.hasNext());) {
			Locale locale = iter.next();
			if (isoAbbreviation.equals(locale.getLanguage()) || isoAbbreviation.equals(locale.getCountry())) {
				languageLocale = locale;
			}
		}

		if (languageLocale == null) {
			return defaultName;
		}

		String localizedLanguageName = languageLocale.getDisplayLanguage(localeIn);
		if (StringUtil.isEmpty(localizedLanguageName)) {
			return defaultName;
		}

		localizedLanguageName = StringUtil.getCapitalized(localizedLanguageName, localeIn);
		return localizedLanguageName;
	}

}