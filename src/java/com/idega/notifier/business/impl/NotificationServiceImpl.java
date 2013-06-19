/**
 * @(#)NotificationServiceImpl.java    1.0.0 9:04:36 AM
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
package com.idega.notifier.business.impl;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.idega.business.IBOLookup;
import com.idega.business.IBOLookupException;
import com.idega.core.business.DefaultSpringBean;
import com.idega.notifier.business.NotificationService;
import com.idega.notifier.data.NotificationEntity;
import com.idega.notifier.data.NotificationReceiverEntity;
import com.idega.notifier.data.dao.NotificationEntityDAO;
import com.idega.notifier.event.NewMessageEvent;
import com.idega.user.business.UserBusiness;
import com.idega.user.data.User;
import com.idega.util.CoreUtil;
import com.idega.util.StringUtil;
import com.idega.util.expression.ELUtil;

/**
 * <p>TODO</p>
 * <p>You can report about problems to: 
 * <a href="mailto:martynas@idega.is">Martynas Stakė</a></p>
 *
 * @version 1.0.0 Jun 14, 2013
 * @author <a href="mailto:martynas@idega.is">Martynas Stakė</a>
 */
@Scope(BeanDefinition.SCOPE_SINGLETON)
@Service(NotificationServiceImpl.BEAN_IDENTIFIER)
public class NotificationServiceImpl extends DefaultSpringBean implements
		NotificationService {
	
	public static final String BEAN_IDENTIFIER = "notificationService";
	
	@Autowired
	private NotificationEntityDAO notificationEntityDAO;
	
	private com.idega.user.business.UserBusiness userBusiness = null;
	
	protected com.idega.user.business.UserBusiness getUserBusiness() {
		if (this.userBusiness != null) {
			return this.userBusiness;
		}

		try {
			this.userBusiness = IBOLookup.getServiceInstance(
					CoreUtil.getIWContext(), UserBusiness.class);
		} catch (IBOLookupException e) {
			getLogger().log(Level.WARNING, 
					"Unable to get " + UserBusiness.class, e);
		}

		return this.userBusiness;
	}
	
	protected NotificationEntityDAO getNotificationEntityDAO() {
		if (this.notificationEntityDAO == null) {
			ELUtil.getInstance().autowire(this);
		}
		
		return this.notificationEntityDAO;
	}
	
	@Override
	public NotificationEntity createNotification(String body, Collection<User> receivers) {
		return getNotificationEntityDAO().update(null, receivers, body);
	}

	@Override
	public void onApplicationEvent(ApplicationEvent event) {
		if (!(event instanceof NewMessageEvent)) {
			return;
		}
		
		NewMessageEvent messageEvent = (NewMessageEvent) event;
		createNotification(
				messageEvent.getMessage(), 
				messageEvent.getReceivers());
	}

	/*
	 * (non-Javadoc)
	 * @see com.idega.notifier.business.NotificationService#getNotificationEntities(java.lang.String)
	 */
	@Override
	public List<NotificationEntity> getNotificationEntities(String userId) {
		User user = getUser(userId);
		if (user == null) {
			return Collections.emptyList();
		}
		
		return getNotificationEntityDAO().getNotifications(user);
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.idega.notifier.business.NotificationService#getNotificationReceiver(java.lang.String)
	 */
	@Override
	public NotificationReceiverEntity getNotificationReceiver(
			String userId) {
		User user = getUser(userId);
		if (user == null) {
			return null;
		}
		
		return getNotificationEntityDAO().getNotificationReceiver(user);
	}
	
	protected User getUser(String userId) {
		if (StringUtil.isEmpty(userId)) {
			return null;
		}
		
		try {
			return getUserBusiness().getUser(userId);
		} catch (Exception e) {
			getLogger().log(Level.WARNING, 
					"Unable to find " + User.class + " by personal id: " + userId);
		}
		
		try {
			return getUserBusiness().getUser(Integer.valueOf(userId));
		} catch (Exception e) {
			getLogger().log(Level.WARNING, 
					"Unable to find " + User.class + " by primary key: " + userId);
		}
		
		return null;
	}
}
