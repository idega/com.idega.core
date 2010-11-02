/**
 * 
 */
package com.idega.core.net.data.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

@Entity
@Table(name = ICNetwork.ENTITY_NAME)
@NamedQueries({
	@NamedQuery(name = "icNetwork.findAll", query = "select n from ICNetwork n")
})
public class ICNetwork implements Serializable {

	private static final long serialVersionUID = -8272304500346893807L;

	public static final String ENTITY_NAME = "ic_network";
	public static final String COLUMN_NETWORK_ID = "ic_network_id";
	private static final String COLUMN_IP_ADDRESS = "ipaddress";

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = COLUMN_NETWORK_ID)
	private Integer networkID;

	@Column(name = COLUMN_IP_ADDRESS)
	private String IPAddress;

	@ManyToMany(fetch = FetchType.LAZY, cascade = { CascadeType.PERSIST, CascadeType.MERGE }, targetEntity = ICProtocol.class)
	@JoinTable(name = "ib_protocol_network", joinColumns = { @JoinColumn(name = COLUMN_NETWORK_ID) }, inverseJoinColumns = { @JoinColumn(name = ICProtocol.COLUMN_PROTOCOL_ID) })
	private List<ICProtocol> protocols = new ArrayList<ICProtocol>();

	public Integer getId() {
		return this.networkID;
	}

	public void setId(Integer networkID) {
		this.networkID = networkID;
	}

	public String getIDAddress() {
		return this.IPAddress;
	}

	public void setIPAddress(String IPAddress) {
		this.IPAddress = IPAddress;
	}

	public List getProtocols() {
		return protocols;
	}

	public void addProtocol(ICProtocol protocol) {
		protocols.add(protocol);
	}

	public void removeProtocol(ICProtocol protocol) {
		protocols.remove(protocol);
	}

	public void removeAllProtocols() {
		protocols.clear();
	}
}