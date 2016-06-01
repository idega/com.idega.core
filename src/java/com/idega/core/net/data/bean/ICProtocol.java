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

import com.idega.core.data.bean.GenericType;
import com.idega.util.DBUtil;

@Entity
@Table(name=ICProtocol.ENTITY_NAME)
@NamedQueries({
	@NamedQuery(name="icProtocol.findAll", query="select p from ICProtocol p")
})
public class ICProtocol extends GenericType implements Serializable {

	private static final long serialVersionUID = -515213446676555283L;

	public static final String ENTITY_NAME = "ic_protocol";
	public static final String COLUMN_PROTOCOL_ID = "ic_protocol_id";

  @Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = COLUMN_PROTOCOL_ID)
  private Integer id;

	@ManyToMany(fetch = FetchType.LAZY, cascade = { CascadeType.PERSIST, CascadeType.MERGE }, targetEntity = ICNetwork.class)
	@JoinTable(name = "ib_protocol_network", joinColumns = { @JoinColumn(name = ICProtocol.COLUMN_PROTOCOL_ID) }, inverseJoinColumns = { @JoinColumn(name = ICNetwork.COLUMN_NETWORK_ID) })
	private List<ICNetwork> networks = new ArrayList<ICNetwork>();

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public List<ICNetwork> getNetworks() {
		if (!DBUtil.getInstance().isInitialized(networks)) {
			networks = DBUtil.getInstance().lazyLoad(networks);
			networks = networks == null ? new ArrayList<>() : new ArrayList<>(networks);
		}
		return networks;
	}

	public void addNetwork(ICNetwork network) {
		networks.add(network);
	}

	public void removeNetwork(ICNetwork network) {
		networks.remove(network);
	}

	public void removeAllNetworks() {
		networks.clear();
	}
}