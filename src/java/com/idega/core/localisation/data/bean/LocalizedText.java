package com.idega.core.localisation.data.bean;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = LocalizedText.TABLE_NAME)
public class LocalizedText implements Serializable {

	private static final long serialVersionUID = -5831667269755396655L;

	public static final String TABLE_NAME = "ic_loc_txt";

	public LocalizedText() {
		super();
	}

	public LocalizedText(ICLocale locale, String text) {
		this();

		this.locale = locale;
		this.text = text;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "text_id")
	private Long id;

	@ManyToOne
	@JoinColumn(name = ICLocale.COLUMN_LOCALE_ID, nullable = false)
	private ICLocale locale;

	@Column(name = "text", length = 65535, nullable = false)
	private String text;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public ICLocale getLocale() {
		return locale;
	}

	public void setLocale(ICLocale locale) {
		this.locale = locale;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	@Override
	public String toString() {
		return "Localized text ID: " + getId();
	}
}