package com.idega.notifier.data;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.idega.user.data.User;
import com.idega.user.data.UserBMPBean;
import com.idega.util.CoreConstants;
import com.idega.util.ListUtil;

/**
 * 
 * <p>Entity to report number of new messages in mailbox.</p>
 * <p>You can report about problems to: 
 * <a href="mailto:martynas@idega.is">Martynas Stakė</a></p>
 *
 * @version 1.0.0 Apr 9, 2013
 * @author <a href="mailto:martynas@idega.is">Martynas Stakė</a>
 */
@Entity
@Table(name = NotificationEntity.TABLE_NAME)
@NamedQueries({
	@NamedQuery(
			name = NotificationEntity.QUERY_FIND_ALL, 
			query = "SELECT DISTINCT s FROM NotificationEntity s " +
					"LEFT OUTER JOIN FETCH s.receivers"),
	@NamedQuery(
			name = NotificationEntity.QUERY_FIND_BY_ID, 
			query = "SELECT DISTINCT s FROM NotificationEntity s " +
					"LEFT OUTER JOIN FETCH s.receivers " +
					"WHERE s.id =:" + 
					NotificationEntity.idProp),
	@NamedQuery(
			name = NotificationEntity.QUERY_FIND_BY_TARGET,
			query = "SELECT DISTINCT ne FROM NotificationEntity ne " +
					"LEFT OUTER JOIN FETCH ne.receivers AS target " +
					"WHERE target.notification = ne " +
					"AND target.receiverId =:" + 
					NotificationReceiverEntity.receiverIdProp + CoreConstants.SPACE +
					"AND target.receiverClass =:" + NotificationReceiverEntity.receiverClassProp),
	@NamedQuery(
			name = NotificationEntity.QUERY_COUNT_BY_TARGET_AND_STATE,
			query = "SELECT COUNT(DISTINCT ne) FROM NotificationEntity ne " +
					"LEFT OUTER JOIN ne.receivers AS target " + 
					"WHERE target.notification = ne " +
					"AND target.receiverId =:" + 
					NotificationReceiverEntity.receiverIdProp + CoreConstants.SPACE +
					"AND target.receiverClass =:" + 
					NotificationReceiverEntity.receiverClassProp + CoreConstants.SPACE + 
					"AND target.read =:" + NotificationReceiverEntity.readProp)
})
public class NotificationEntity implements Serializable {

	private static final long serialVersionUID = 2764472085912873052L;

	public static final String TABLE_NAME = "ic_notification";
	public static final String JOIN_TABLE_NAME = "ic_notification_subscription";
	
	public static final String QUERY_FIND_ALL = "notificationEntity.findAll";
	public static final String QUERY_FIND_BY_ID = "notificationEntity.findByID";
	public static final String QUERY_FIND_BY_TARGET = "notificationEntity.findByTargetId";
	public static final String QUERY_COUNT_BY_TARGET_AND_STATE = "notificationEntity.countByUserIDAndState";
	
	public static final String idProp = "id";
    @Id @GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
    
    public static final String messageProp = "message";
    public static final String COLUMN_MESSAGE = "message";
    @Column(name = COLUMN_MESSAGE)
	private String message;
    
    @OneToMany(mappedBy = NotificationReceiverEntity.notificationProp)
    private Set<NotificationReceiverEntity> receivers;

	/**
	 * @return the id
	 */
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * @param message the message to set
	 */
	public void setMessage(String message) {
		this.message = message;
	}

	public Set<NotificationReceiverEntity> getReceivers() {
		return receivers;
	}

	public void setReceivers(Set<NotificationReceiverEntity> receivers) {
		this.receivers = receivers;
	}
	
	public boolean addReceiver(NotificationReceiverEntity receiver) {
		if (receiver == null) {
			return Boolean.FALSE;
		}
		
		if (this.receivers == null) {
			this.receivers = new HashSet<NotificationReceiverEntity>();
		}
		
		return this.receivers.add(receiver);
	}
	
	public void removeReceiver(NotificationReceiverEntity target) {
		if (target == null) {
			return;
		}
	
		if (ListUtil.isEmpty(this.receivers)) {
			return;
		}
		
		this.receivers.remove(target);
	}
	
	public void addReceiver(User receiver) {
		if (receiver != null) {
			addReceiver(new NotificationReceiverEntity(
					receiver.getPrimaryKey().toString(), 
					receiver.getClass()));
		}
	}
	
	public boolean isReceiver(User user) {
		if (user == null) {
			return Boolean.FALSE;
		}
		
		String userId = user.getPrimaryKey().toString();
		for (NotificationReceiverEntity receiver : this.receivers) {
			if (!userId.equals(receiver.getReceiverId())) {
				continue;
			}
			
			if (UserBMPBean.class.toString().equals(receiver.getReceiverClass())) {
				return Boolean.TRUE;
			}
		}
		
		return Boolean.FALSE;
	}
	
	public Boolean isRead(User user) {
		if (user == null) {
			return null;
		}
		
		String userId = user.getPrimaryKey().toString();
		for (NotificationReceiverEntity receiver : this.receivers) {
			if (!userId.equals(receiver.getReceiverId())) {
				continue;
			}
			
			if (!UserBMPBean.class.toString().equals(receiver.getReceiverClass())) {
				continue;
			}
			
			return receiver.isRead();
		}
		
		return null;
	}
}
