/**
 * 
 */
package com.idega.user.data.bean;

import java.io.Serializable;

import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Embeddable
public class TopNodeGroupPK implements Serializable {

	private static final long serialVersionUID = 2203802800387156542L;

	@ManyToOne
	@JoinColumn(name = TopNodeGroup.COLUMN_USER)
	private User user;

	@ManyToOne
	@JoinColumn(name = TopNodeGroup.COLUMN_GROUP)
	private Group group;
	
	public TopNodeGroupPK() {}

	public TopNodeGroupPK(User user, Group group) {
		this.user = user;
		this.group = group;
	}

	public User getUser() {
		return this.user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Group getGroup() {
		return this.group;
	}

	public void setGroup(Group group) {
		this.group = group;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((this.group == null) ? 0 : this.group.getID().hashCode());
		result = prime * result + ((this.user == null) ? 0 : this.user.getId().hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TopNodeGroupPK other = (TopNodeGroupPK) obj;
		if (this.group == null) {
			if (other.group != null)
				return false;
		}
		else if (!this.group.equals(other.group))
			return false;
		if (this.user == null) {
			if (other.user != null)
				return false;
		}
		else if (!this.user.equals(other.user))
			return false;
		return true;
	}
}