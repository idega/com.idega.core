/**
 * @(#)MapUtil.java    1.0.0 13:14:11
 *
 * Idega Software hf. Source Code Licence Agreement x
 *
 * This agreement, made this 10th of February 2006 by and between
 * Idega Software hf., a business formed and operating under laws
 * of Iceland, having its principal place of business in Reykjavik,
 * Iceland, hereinafter after referred to as "Manufacturer" and Agura
 * IT hereinafter referred to as "Licensee".
 * 1. License Grant: Upon completion of this agreement, the source
 *     code that may be made available according to the documentation for
 *     a particular software product (Software) from Manufacturer
 *     (Source Code) shall be provided to Licensee, provided that
 *     (1) funds have been received for payment of the License for Software and
 *     (2) the appropriate License has been purchased as stated in the
 *     documentation for Software. As used in this License Agreement,
 *     �Licensee� shall also mean the individual using or installing
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
 * 2. License Limitations
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
 * 3. Copyright: Manufacturer's source code is copyrighted and contains
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
 * 4. Warranty & Risks: Although efforts have been made to assure that the
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
package com.idega.util.datastructures.map;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.idega.util.ListUtil;

/**
 * <p>Class for managing {@link Map}s.</p>
 * <p>You can report about problems to: <a href="mailto:martynas@idega.com">
 * Martynas Stakė</a></p>
 * <p>You can expect to find some test cases notice in the end of the file.</p>
 *
 * @version 1.0.0 2011.10.31
 * @author martynas
 */
public class MapUtil {

	private MapUtil() {}

	/**
	 * <p>Checks if {@link Map} is empty.</p>
	 * @param map {@link Map} object.
	 * @return True, if empty, false otherwise.
	 * @author <a href="mailto:martynas@idega.com">Martynas Stakė</a>
	 */
	public static boolean isEmpty(Map<?, ?> map) {
		return map == null || map.isEmpty();
	}

	/**
	 * <p>Appends appending {@link Map} to original one, when both of their
	 * values are {@link Number}, {@link Collection}, {@link Map} type.</p>
	 * @param original {@link Map} to be appended.
	 * @param appending {@link Map} which data will be appended to original.
	 * @return <code>true</code> if successfully appended, <code>false</code>
	 * otherwise.
	 * @param <K> type for key, which should be implementing
	 * {@link Serializable};
	 * @param <L> type for value. Value could be any type, but only values of
	 * {@link Number}, {@link Collection}, {@link Map} are processed for
	 * appending, other types are just overrided.
	 * @author <a href="mailto:martynas@idega.com">Martynas Stakė</a>
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static <K extends Serializable, L> boolean append(Map<K, L> original, Map<K, L> appending) {
		if (MapUtil.isEmpty(original) && MapUtil.isEmpty(appending))
			return Boolean.FALSE;

		if (MapUtil.isEmpty(original)) {
			if (original == null)
				original = new HashMap<K, L>();

			original.putAll(appending);
			return Boolean.TRUE;
		}

		if (MapUtil.isEmpty(appending))
			return Boolean.FALSE;

		Map<K, L> appendingCopy = new HashMap<K, L>(appending);

		for (Iterator<K> keyIterator = appendingCopy.keySet().iterator(); keyIterator.hasNext();) {
			K key = keyIterator.next();
			L appendingValue = appendingCopy.get(key);
			L originalValue = original.get(key);

			if (originalValue instanceof Collection && appendingValue instanceof Collection) {
				((Collection<?>) originalValue).addAll((Collection) appendingValue);
			} else if (originalValue instanceof Number && appendingValue instanceof Number){
				Number originalNumber = (Number) originalValue;
				Number appendingNumber = (Number) appendingValue;
				Number result = null;

				if (originalNumber instanceof Byte) {
					result = (byte) (originalNumber.byteValue() + appendingNumber.byteValue());
				}else if (originalNumber instanceof Short) {
					result = (short) (originalNumber.shortValue() + appendingNumber.shortValue());
				} else if (originalNumber instanceof Integer) {
					result = originalNumber.intValue() + appendingNumber.intValue();
				} else if (originalNumber instanceof Long) {
					result = originalNumber.longValue() + appendingNumber.longValue();
				} else if (originalNumber instanceof Float) {
					result = originalNumber.floatValue() + appendingNumber.floatValue();
				} else if (originalNumber instanceof Double) {
					result = originalNumber.doubleValue() + appendingNumber.doubleValue();
				}

				original.put(key, (L) result);
			} else if (originalValue instanceof Map && appendingValue instanceof Map){
				MapUtil.append((Map<K, L>) originalValue, (Map<K, L>) appendingValue);
			} else {
				original.put(key, appendingValue);
			}
		}

		return Boolean.TRUE;
	}

	/**
	 * <p>Makes deep copy of given {@link Map}.</p>
	 * @param <K> key type of {@link Map}.
	 * @param <V> value type of {@link Map}.
	 * @param original {@link Map}, which should be cloned.
	 * @return {@link Map} with new copied or cloned objects. <code>null</code>
	 * on failure.
	 * @author <a href="mailto:martynas@idega.com">Martynas Stakė</a>
	 * @param <T>
	 */
	@SuppressWarnings("unchecked")
	public static <K extends Serializable, V, T> Map<K, V> deepCopy(Map<K, V> original) {
		if (MapUtil.isEmpty(original))
			return new HashMap<K, V>();

		Map<K, V> copy = new HashMap<K, V>();

		for (Iterator<K> keyIterator = original.keySet().iterator(); keyIterator.hasNext();) {
			K key = keyIterator.next();
			V originalValue = original.get(key);

			if (originalValue instanceof Collection) {
				Collection<?> tmpCollection = (Collection<?>) originalValue;
				tmpCollection = ListUtil.getDeepCopy(tmpCollection);
				copy.put(key, (V) tmpCollection);
			} else if (originalValue instanceof Map){
				original.put(key, (V) MapUtil.deepCopy((Map<K, V>) originalValue));
			} else {
				original.put(key, originalValue);
			}
		}

		return copy;
	}
}