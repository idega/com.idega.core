/**
 * @(#)DateUtil.java    1.0.0 12:41:33
 *
 * Idega Software hf. Source Code Licence Agreement x
 *
 * This agreement, made this 10th of February 2006 by and between 
 * Idega Software hf., a business formed and operating under laws 
 * of Iceland, having its principal place of business in Reykjavik, 
 * Iceland, hereinafter after referred to as "Manufacturer" and Agura 
 * IT hereinafter referred to as "Licensee".
 * 1.  License Grant: Upon completion of this agreement, the source 
 *     code that may be made available according to the documentation for 
 *     a particular software product (Software) from Manufacturer 
 *     (Source Code) shall be provided to Licensee, provided that 
 *     (1) funds have been received for payment of the License for Software and 
 *     (2) the appropriate License has been purchased as stated in the 
 *     documentation for Software. As used in this License Agreement, 
 *     Licensee shall also mean the individual using or installing 
 *     the source code together with any individual or entity, including 
 *     but not limited to your employer, on whose behalf you are acting 
 *     in using or installing the Source Code. By completing this agreement, 
 *     Licensee agrees to be bound by the terms and conditions of this Source 
 *     Code License Agreement. This Source Code License Agreement shall 
 *     be an extension of the Software License Agreement for the associated 
 *     product. No additional amendment or modification shall be made 
 *     to this Agreement except in writing signed by Licensee and 
 *     Manufacturer. This Agreement is effective indefinitely and once
 *     completed, cannot be terminated. Manufacturer hereby grants to 
 *     Licensee a non-transferable, worldwide license during the term of 
 *     this Agreement to use the Source Code for the associated product 
 *     purchased. In the event the Software License Agreement to the 
 *     associated product is terminated; (1) Licensee's rights to use 
 *     the Source Code are revoked and (2) Licensee shall destroy all 
 *     copies of the Source Code including any Source Code used in 
 *     Licensee's applications.
 * 2.  License Limitations
 *     2.1 Licensee may not resell, rent, lease or distribute the 
 *         Source Code alone, it shall only be distributed as a 
 *         compiled component of an application.
 *     2.2 Licensee shall protect and keep secure all Source Code 
 *         provided by this this Source Code License Agreement. 
 *         All Source Code provided by this Agreement that is used 
 *         with an application that is distributed or accessible outside
 *         Licensee's organization (including use from the Internet), 
 *         must be protected to the extent that it cannot be easily 
 *         extracted or decompiled.
 *     2.3 The Licensee shall not resell, rent, lease or distribute 
 *         the products created from the Source Code in any way that 
 *         would compete with Idega Software.
 *     2.4 Manufacturer's copyright notices may not be removed from 
 *         the Source Code.
 *     2.5 All modifications on the source code by Licencee must 
 *         be submitted to or provided to Manufacturer.
 * 3.  Copyright: Manufacturer's source code is copyrighted and contains 
 *     proprietary information. Licensee shall not distribute or 
 *     reveal the Source Code to anyone other than the software 
 *     developers of Licensee's organization. Licensee may be held 
 *     legally responsible for any infringement of intellectual property 
 *     rights that is caused or encouraged by Licensee's failure to abide 
 *     by the terms of this Agreement. Licensee may make copies of the 
 *     Source Code provided the copyright and trademark notices are 
 *     reproduced in their entirety on the copy. Manufacturer reserves 
 *     all rights not specifically granted to Licensee.
 *
 * 4.  Warranty & Risks: Although efforts have been made to assure that the 
 *     Source Code is correct, reliable, date compliant, and technically 
 *     accurate, the Source Code is licensed to Licensee as is and without 
 *     warranties as to performance of merchantability, fitness for a 
 *     particular purpose or use, or any other warranties whether 
 *     expressed or implied. Licensee's organization and all users 
 *     of the source code assume all risks when using it. The manufacturers, 
 *     distributors and resellers of the Source Code shall not be liable 
 *     for any consequential, incidental, punitive or special damages 
 *     arising out of the use of or inability to use the source code or 
 *     the provision of or failure to provide support services, even if we 
 *     have been advised of the possibility of such damages. In any case, 
 *     the entire liability under any provision of this agreement shall be 
 *     limited to the greater of the amount actually paid by Licensee for the 
 *     Software or 5.00 USD. No returns will be provided for the associated 
 *     License that was purchased to become eligible to receive the Source 
 *     Code after Licensee receives the source code. 
 */
package com.idega.util.timer;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Date;

import com.idega.util.StringUtil;

/**
 * <p>Quick conversion betwwen different formats</p>
 * <p>You can report about problems to: 
 * <a href="mailto:martynas@idega.is">Martynas Stakė</a></p>
 *
 * @version 1.0.0 2015 liep. 24
 * @author <a href="mailto:martynas@idega.is">Martynas Stakė</a>
 */
public class DateUtil {

	/**
	 * 
	 * <p>Obtains an instance of LocalDate from a text string such as 2007-12-03. 
	 * The string must represent a valid date and is parsed using 
	 * java.time.format.DateTimeFormatter.ISO_LOCAL_DATE.</p>
	 * @param date the text to parse such as "2007-12-03", not null;
	 * @return the parsed local date, or <code>null</code>;
	 * @author <a href="mailto:martynas@idega.is">Martynas Stakė</a>
	 */
	public static LocalDate getDate(String date) {
		if (!StringUtil.isEmpty(date)) {
			return LocalDate.parse(date);
		}

		return null;
	}

	/**
	 * 
	 * <p>Obtains an instance of LocalTime from a text string such as 10:15. 
	 * The string must represent a valid time and is parsed using 
	 * java.time.format.DateTimeFormatter.ISO_LOCAL_TIME.</p>
	 * @param time the text to parse such as "10:15:30", not <code>null</code>;
	 * @return the parsed local time or <code>null</code>;
	 * @author <a href="mailto:martynas@idega.is">Martynas Stakė</a>
	 */
	public static LocalTime getTime(String time) {
		if (!StringUtil.isEmpty(time)) {
			return LocalTime.parse(time);
		}

		return null;
	}

	/**
	 * 
	 * @param date to convert, not <code>null</code>;
	 * @return converted date or <code>null</code> on failure;
	 * @author <a href="mailto:martynas@idega.is">Martynas Stakė</a>
	 */
	public static LocalDateTime getDateTime(Date date) {
		if (date != null) {
			return Instant.ofEpochMilli(date.getTime()).atZone(ZoneId.systemDefault()).toLocalDateTime();
		}

		return null;
	}

	/**
	 * 
	 * @param date to convert, not <code>null</code>;
	 * @return converted date or <code>null</code> on failure;
	 * @author <a href="mailto:martynas@idega.is">Martynas Stakė</a>
	 */
	public static LocalDate getDate(Date date) {
		LocalDateTime dateTime = getDateTime(date);
		if (dateTime != null) {
			return dateTime.toLocalDate();
		}

		return null;
	}

	/**
	 * 
	 * @param date to convert, not <code>null</code>;
	 * @return converted date or <code>null</code> on failure;
	 * @author <a href="mailto:martynas@idega.is">Martynas Stakė</a>
	 */
	public static LocalTime getTime(Date date) {
		LocalDateTime dateTime = getDateTime(date);
		if (dateTime != null) {
			return dateTime.toLocalTime();
		}

		return null;
	}

	/**
	 * 
	 * @param dateTime to convert, not <code>null</code>;
	 * @return converted date or <code>null</code> on failure;
	 * @author <a href="mailto:martynas@idega.is">Martynas Stakė</a>
	 */
	public static Date getDate(LocalDateTime dateTime) {	
		if (dateTime != null) {
			return Date.from(dateTime.atZone(ZoneId.systemDefault()).toInstant());
		}

		return null;
	}

	/**
	 * 
	 * @param time to convert, not <code>null</code>;
	 * @param date to convert, not <code>null</code>;
	 * @return converted date or <code>null</code> on failure;
	 * @author <a href="mailto:martynas@idega.is">Martynas Stakė</a>
	 */
	public static Date getDate(LocalTime time, LocalDate date) {	
		if (time != null && date != null) {
			return getDate(time.atDate(date));
		}

		return null;
	}

	/**
	 * 
	 * @param date to convert, not <code>null</code>;
	 * @return converted date at 0 second of day or <code>null</code>;
	 * @author <a href="mailto:martynas@idega.is">Martynas Stakė</a>
	 */
	public static Date getDate(LocalDate date) {
		return getDate(LocalTime.ofSecondOfDay(0), date);
	}

	/**
	 * 
	 * @param time to convert, not <code>null</code>;
	 * @return converted date at 0 date of epoch or <code>null</code>;
	 * @author <a href="mailto:martynas@idega.is">Martynas Stakė</a>
	 */
	public static Date getDate(LocalTime time) {
		return getDate(time, LocalDate.ofEpochDay(0));
	}
}
