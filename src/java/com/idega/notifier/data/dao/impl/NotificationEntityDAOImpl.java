/**
 * @(#)NotificationEntityDAOImpl.java    1.0.0 1:03:36 PM
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
package com.idega.notifier.data.dao.impl;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.idega.core.persistence.Param;
import com.idega.core.persistence.impl.GenericDaoImpl;
import com.idega.notifier.data.NotificationEntity;
import com.idega.notifier.data.NotificationReceiverEntity;
import com.idega.notifier.data.dao.NotificationEntityDAO;
import com.idega.user.data.User;
import com.idega.user.data.UserBMPBean;
import com.idega.util.ListUtil;
import com.idega.util.StringUtil;

/**
 * @see NotificationEntityDAO
 * <p>You can report about problems to: 
 * <a href="mailto:martynas@idega.is">Martynas Stakė</a></p>
 *
 * @version 1.0.0 Apr 9, 2013
 * @author <a href="mailto:martynas@idega.is">Martynas Stakė</a>
 */
@Repository(NotificationEntityDAO.BEAN_NAME)
@Transactional(readOnly = false)
@Scope(BeanDefinition.SCOPE_SINGLETON)
public class NotificationEntityDAOImpl extends GenericDaoImpl implements
		NotificationEntityDAO {

	/* (non-Javadoc)
	 * @see com.idega.egov.hub.data.dao.NotificationEntityDAO#update(com.idega.egov.hub.data.NotificationEntity)
	 */
	@Override
	public NotificationEntity update(NotificationEntity notificationEntity) {
		if (notificationEntity == null) {
			return null;
		}
		
		if (notificationEntity.getId() == null) {
			persist(notificationEntity);
		} else {
			merge(notificationEntity);
		}
		
		if (notificationEntity.getId() == null) {
			return null;
		}
		
		Set<NotificationReceiverEntity> receivers = notificationEntity.getReceivers();
		if (!ListUtil.isEmpty(receivers)) {
			for (NotificationReceiverEntity nte : receivers) {
				nte.setNotification(notificationEntity);
				update(nte);
			}
		}

		return notificationEntity;
	}
	
	@Override
	public NotificationReceiverEntity getNotificationReceiver(Long notificationTargetEntityId) {
		if (notificationTargetEntityId == null) {
			return null;
		}
		
		return getSingleResult(
				NotificationReceiverEntity.QUERY_FIND_BY_ID, 
				NotificationReceiverEntity.class, 
				new Param(NotificationReceiverEntity.idProp, notificationTargetEntityId));
	}

	protected NotificationReceiverEntity update(NotificationReceiverEntity nte) {
		if (nte == null) {
			return null;
		}
		
		if (nte.getId() == null) {
			persist(nte);
		} else {
			merge(nte);
		}
		
		if (nte.getId() == null) {
			return null;
		}
		
		return nte;
	}

	/* (non-Javadoc)
	 * @see com.idega.egov.hub.data.dao.NotificationEntityDAO#update(java.lang.Long, java.lang.Long)
	 */
	@Override
	public NotificationEntity update(
			Long id, 
			com.idega.user.data.User receiver, 
			String message) {

		return update(id, Arrays.asList(receiver), message);
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.idega.notifier.data.dao.NotificationEntityDAO#update(java.lang.Long, java.util.Collection, java.lang.String)
	 */
	@Override
	public NotificationEntity update(
			Long id, 
			Collection<User> receivers,
			String message) {
		
		NotificationEntity ne = null;
		if (id != null) {
			ne = getNotification(id);
		} else {
			ne = new NotificationEntity();
		}
		
		if (ne == null) {
			return null;
		}
		
		if (!ListUtil.isEmpty(receivers)) {
			for (User receiver: receivers) {
				ne.addReceiver(receiver);
			}
		}
		
		if (!StringUtil.isEmpty(message)) {
			ne.setMessage(message);
		}

		return update(ne);
	}

	/* (non-Javadoc)
	 * @see com.idega.egov.hub.data.dao.NotificationEntityDAO#remove(com.idega.egov.hub.data.NotificationEntity)
	 */
	@Override
	public boolean remove(NotificationEntity notificationEntity) {
		if (notificationEntity == null) {
			return Boolean.FALSE;
		}
		
		super.remove(notificationEntity);
		return Boolean.TRUE;
	}

	/* (non-Javadoc)
	 * @see com.idega.egov.hub.data.dao.NotificationEntityDAO#getNotification(java.lang.Long)
	 */
	@Override
	public NotificationEntity getNotification(long id) {
		return getSingleResult(
				NotificationEntity.QUERY_FIND_BY_ID, 
				NotificationEntity.class, 
				new Param(NotificationEntity.idProp, id));
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.idega.notifier.data.dao.NotificationEntityDAO#getNotification(long, com.idega.user.data.User)
	 */
	@Override
	public NotificationEntity getNotification(long id, 
			com.idega.user.data.User receiver) {
		NotificationEntity entity = getNotification(id);
		if (entity == null) {
			return null;
		}
		
		if (!entity.isReceiver(receiver)) {
			return null;
		}
		
		return entity;
	}

	/* (non-Javadoc)
	 * @see com.idega.egov.hub.data.dao.NotificationEntityDAO#getNotifications()
	 */
	@Override
	public List<NotificationEntity> getNotifications() {
		return getResultList(
				NotificationEntity.QUERY_FIND_ALL, 
				NotificationEntity.class);
	}

	/* (non-Javadoc)
	 * @see com.idega.egov.hub.data.dao.NotificationEntityDAO#getNotifications(java.lang.Long)
	 */
	@Override
	public List<NotificationEntity> getNotifications(com.idega.user.data.User user) {
		if (user == null) {
			return Collections.emptyList();
		}
		
		return getResultList(
				NotificationEntity.QUERY_FIND_BY_TARGET, 
				NotificationEntity.class,
				new Param(NotificationReceiverEntity.receiverClassProp, 
						user.getClass().toString()),
				new Param(NotificationReceiverEntity.receiverIdProp, 
						user.getPrimaryKey().toString()));
	}

	/*
	 * (non-Javadoc)
	 * @see com.idega.notifier.data.dao.NotificationEntityDAO#countNotifications(com.idega.user.data.User, boolean)
	 */
	@Override
	public Long countNotifications(com.idega.user.data.User receiver, boolean isRead) {
		return getSingleResult(
				NotificationEntity.QUERY_COUNT_BY_TARGET_AND_STATE, 
				Long.class,
				new Param(NotificationReceiverEntity.readProp, isRead),
				new Param(NotificationReceiverEntity.receiverClassProp, 
						receiver.getClass().toString()),
				new Param(NotificationReceiverEntity.receiverIdProp, 
						receiver.getPrimaryKey().toString()));
	}

	/*
	 * (non-Javadoc)
	 * @see com.idega.notifier.data.dao.NotificationEntityDAO#getNotificationReceiver(com.idega.user.data.User)
	 */
	@Override
	public NotificationReceiverEntity getNotificationReceiver(User user) {
		if (user == null) {
			return null;
		}
		
		return getSingleResult(
				NotificationReceiverEntity.QUERY_FIND_BY_CLASS_AND_RECEIVER_ID, 
				NotificationReceiverEntity.class, 
				new Param(NotificationReceiverEntity.receiverClassProp, UserBMPBean.class.toString()),
				new Param(NotificationReceiverEntity.receiverIdProp, user.getPrimaryKey().toString()));
	}
}
