/**
 * @(#)GenericTypeDAOImpl.java    1.0.0 11:11:18
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
package com.idega.core.dao.impl;

import java.util.logging.Level;

import javax.persistence.TypedQuery;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.idega.core.dao.GenericTypeDAO;
import com.idega.core.data.bean.GenericType;
import com.idega.core.persistence.impl.GenericDaoImpl;
import com.idega.util.StringUtil;

/**
 * <p>You can report about problems to: 
 * <a href="mailto:martynas@idega.is">Martynas Stakė</a></p>
 *
 * @version 1.0.0 2016 bal. 14
 * @author <a href="mailto:martynas@idega.is">Martynas Stakė</a>
 */
/*
 * Spring
 */
@Repository(GenericTypeDAOImpl.BEAN_NAME)
@Scope(BeanDefinition.SCOPE_SINGLETON)
@Transactional(readOnly = false)
public class GenericTypeDAOImpl extends GenericDaoImpl implements
		GenericTypeDAO {

	public static final String BEAN_NAME = "genericTypeDAO";
	
	/* (non-Javadoc)
	 * @see com.idega.core.dao.GenericTypeDAO#findById(java.lang.Long, java.lang.Class)
	 */
	@Override
	public <T extends GenericType> T findById(Integer id, Class<T> clazz) {
		if (id != null) {
			TypedQuery<T> query = getEntityManager().createQuery(
					"FROM " + clazz.getSimpleName() + " g " +
					"WHERE g.id = :id",
					clazz);

			query.setParameter("id", id);
			return query.getSingleResult();
		}

		return null;
	}

	/* (non-Javadoc)
	 * @see com.idega.core.dao.GenericTypeDAO#findByUniqueName(java.lang.String, java.lang.Class)
	 */
	@Override
	public <T extends GenericType> T findByUniqueName(String name,
			Class<T> clazz) {
		if (!StringUtil.isEmpty(name)) {
			TypedQuery<T> query = getEntityManager().createQuery(
					"FROM " + clazz.getSimpleName() + " g " +
					"WHERE g.uniqueName = :uniqueName",
					clazz);

			query.setParameter("uniqueName", name);
			try {
				return query.getSingleResult();
			} catch (Exception e) {
				getLogger().warning("Nothing found by name: " + name + " and class: " + clazz );
			}
		}

		return null;
	}

	/* (non-Javadoc)
	 * @see com.idega.core.dao.GenericTypeDAO#update(com.idega.core.data.bean.GenericType)
	 */
	@Override
	public <T extends GenericType> T update(T entity, Class<T> clazz) {
		if (entity != null) {
			if (findById(entity.getId(), clazz) == null) {
				persist(entity);
				if (entity.getId() != null) {
					getLogger().fine("Entity: " + entity + " created!");
					return entity;
				}
			} else {
				entity = merge(entity);
				if (entity != null) {
					getLogger().fine("Entity: " + entity + " updated");
					return entity;
				}
			}		
		}

		getLogger().warning("Failed to create/update entity: " + entity);
		return null;
	}

	/* (non-Javadoc)
	 * @see com.idega.core.dao.GenericTypeDAO#update(java.lang.Integer, java.lang.String, java.lang.String, java.lang.String, java.lang.Class)
	 */
	@Override
	public <T extends GenericType> T update(
			Integer id, 
			String uniqueName,
			String displayName, 
			String description, 
			Class<T> clazz) {
		T entityToUpdate = findById(id, clazz);
		if (entityToUpdate == null) {
			entityToUpdate = findByUniqueName(uniqueName, clazz);
		}

		if (entityToUpdate == null) {
			try {
				entityToUpdate = clazz.newInstance();
			} catch (InstantiationException e) {
				getLogger().log(Level.WARNING, 
						"Unable to find constructor for: " + clazz, e);
				return null;
			} catch (IllegalAccessException e) {
				getLogger().log(Level.WARNING, 
						"Constructor of: " + clazz + " can't be accessed: " , e);
				return null;
			}
		}

		if (!StringUtil.isEmpty(uniqueName)) {
			entityToUpdate.setUniqueName(uniqueName);
		}

		if (!StringUtil.isEmpty(displayName)) {
			entityToUpdate.setDisplayName(displayName);
		}
		
		if (!StringUtil.isEmpty(description)) {
			entityToUpdate.setDescription(description);
		}

		return update(entityToUpdate, clazz);
	}

}
