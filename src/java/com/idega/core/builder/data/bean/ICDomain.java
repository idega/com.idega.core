/**
 *
 */
package com.idega.core.builder.data.bean;

import java.io.Serializable;
import java.util.List;
import java.util.Locale;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;

import com.idega.core.idgenerator.business.IdGenerator;
import com.idega.core.idgenerator.business.IdGeneratorFactory;
import com.idega.data.UniqueIDCapable;
import com.idega.util.DBUtil;
import com.idega.util.LocaleUtil;

@Entity
@Table(name = ICDomain.ENTITY_NAME)
@NamedQueries({
	@NamedQuery(name = "domain.findAll", query = "select d from ICDomain d"),
	@NamedQuery(name = "domain.findAllByServerName", query = "select d from ICDomain d where d.serverName = :serverName"),
	@NamedQuery(name = "domain.findDefaultDomain", query = "select d from ICDomain d where d.type = " + ICDomain.TYPE_DEFAULT)
})
@Cacheable
public class ICDomain implements Serializable, UniqueIDCapable {

	private static final long serialVersionUID = 5174977643190597419L;

	public static final String TYPE_DEFAULT = "default";
	public static final String TYPE_SUBDOMAIN = "subdomain";

	public static final String ENTITY_NAME = "ib_domain";
	public static final String COLUMN_DOMAIN_ID = "ib_domain_id";
	private static final String COLUMN_NAME = "domain_name";
	private static final String COLUMN_URL = "url";
	private static final String COLUMN_UNIQUE_ID = "unique_id";
	private static final String COLUMN_START_PAGE = "start_ib_page_id";
	private static final String COLUMN_START_TEMPLATE = "start_ib_template_id";
	private static final String COLUMN_SERVER_NAME = "server_name";
	private static final String COLUMN_SERVER_PORT = "server_port";
	private static final String COLUMN_SERVER_PROTOCOL = "server_protocol";
	private static final String COLUMN_SERVER_CONTEXT_PATH = "server_context_path";
	private static final String COLUMN_DEFAULT_LOCALE = "default_locale";
	private static final String COLUMN_TYPE = "domain_type";
	private static final String COLUMN_SERVER_ALIASES = "server_aliases";

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = ICDomain.COLUMN_DOMAIN_ID)
	private Integer domainID;

	@Column(name = COLUMN_NAME)
	private String domainName;

	@Column(name = COLUMN_URL, length = 1000)
	private String url;

	@Column(name = COLUMN_UNIQUE_ID, length = 36, nullable = false, unique = true)
	private String uniqueID;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = COLUMN_START_PAGE)
	private ICPage startPage;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = COLUMN_START_TEMPLATE)
	private ICPage startTemplate;

	@Column(name = COLUMN_SERVER_NAME)
	private String serverName;

	@Column(name = COLUMN_SERVER_PORT)
	private Integer serverPort;

	@Column(name = COLUMN_SERVER_PROTOCOL, length = 30)
	private String serverProtocol;

	@Column(name = COLUMN_SERVER_CONTEXT_PATH)
	private String serverContextPath;

	@Column(name = COLUMN_DEFAULT_LOCALE, length = 5)
	private String defaultLocale;

	@Column(name = COLUMN_TYPE, length = 20)
	private String type;

	@Column(name = COLUMN_SERVER_ALIASES)
	private String serverAliases;

	@OneToMany(mappedBy = "domain", fetch = FetchType.LAZY)
	private List<ICPage> pages;

	@PrePersist
	@PreUpdate
	public void setDefaultValues() {
		if (getUniqueId() == null) {
			IdGenerator uidGenerator = IdGeneratorFactory.getUUIDGenerator();
			setUniqueId(uidGenerator.generateId());
		}
	}

	/**
	 * @return the domainID
	 */
	public Integer getId() {
		return this.domainID;
	}

	/**
	 * @param domainID
	 *          the domainID to set
	 */
	public void setId(Integer domainID) {
		this.domainID = domainID;
	}

	/**
	 * @return the domainName
	 */
	public String getDomainName() {
		return this.domainName;
	}

	/**
	 * @param domainName
	 *          the domainName to set
	 */
	public void setDomainName(String domainName) {
		this.domainName = domainName;
	}

	/**
	 * @return the url
	 */
	public String getUrl() {
		return this.url;
	}

	/**
	 * @param url
	 *          the url to set
	 */
	public void setUrl(String url) {
		this.url = url;
	}

	/**
	 * @return the startPage
	 */
	public ICPage getStartPage() {
		DBUtil.getInstance().lazyLoad(startPage);
		return this.startPage;
	}

	/**
	 * @param startPage
	 *          the startPage to set
	 */
	public void setStartPage(ICPage startPage) {
		this.startPage = startPage;
	}

	/**
	 * @return the startTemplate
	 */
	public ICPage getStartTemplate() {
		DBUtil.getInstance().lazyLoad(startTemplate);
		return this.startTemplate;
	}

	/**
	 * @param startTemplate
	 *          the startTemplate to set
	 */
	public void setStartTemplate(ICPage startTemplate) {
		this.startTemplate = startTemplate;
	}

	/**
	 * @return the serverName
	 */
	public String getServerName() {
		return this.serverName;
	}

	/**
	 * @param serverName
	 *          the serverName to set
	 */
	public void setServerName(String serverName) {
		this.serverName = serverName;
	}

	/**
	 * @return the serverPort
	 */
	public Integer getServerPort() {
		return this.serverPort;
	}

	/**
	 * @param serverPort
	 *          the serverPort to set
	 */
	public void setServerPort(Integer serverPort) {
		this.serverPort = serverPort;
	}

	/**
	 * @return the serverProtocol
	 */
	public String getServerProtocol() {
		return this.serverProtocol;
	}

	/**
	 * @param serverProtocol
	 *          the serverProtocol to set
	 */
	public void setServerProtocol(String serverProtocol) {
		this.serverProtocol = serverProtocol;
	}

	/**
	 * @return the serverContextPath
	 */
	public String getServerContextPath() {
		return this.serverContextPath;
	}

	/**
	 * @param serverContextPath
	 *          the serverContextPath to set
	 */
	public void setServerContextPath(String serverContextPath) {
		this.serverContextPath = serverContextPath;
	}

	/**
	 * @return the defaultLocale
	 */
	public Locale getDefaultLocale() {
		if (this.defaultLocale != null) {
			return LocaleUtil.getLocale(this.defaultLocale);
		}
		return null;
	}

	/**
	 * @param defaultLocale
	 *          the defaultLocale to set
	 */
	public void setDefaultLocale(String defaultLocale) {
		this.defaultLocale = defaultLocale;
	}

	/**
	 * @param defaultLocale
	 *          the defaultLocale to set
	 */
	public void setDefaultLocale(Locale defaultLocale) {
		this.defaultLocale = defaultLocale.toString();
	}

	/**
	 * @return the type
	 */
	public String getType() {
		return this.type;
	}

	/**
	 * @param type
	 *          the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * @return the serverAliases
	 */
	public String getServerAliases() {
		return this.serverAliases;
	}

	/**
	 * @param serverAliases
	 *          the serverAliases to set
	 */
	public void setServerAliases(String serverAliases) {
		this.serverAliases = serverAliases;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.idega.data.UniqueIDCapable#getUniqueId()
	 */
	@Override
	public String getUniqueId() {
		return this.uniqueID;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.idega.data.UniqueIDCapable#setUniqueId(java.lang.String)
	 */
	@Override
	public void setUniqueId(String uniqueId) {
		this.uniqueID = uniqueId;
	}

	/**
	 * @return the pages
	 */
	public List<ICPage> getPages() {
		DBUtil.getInstance().lazyLoad(pages);
		return this.pages;
	}

	@Override
	public String toString() {
		return "Domain ID: " + getId();
	}

}