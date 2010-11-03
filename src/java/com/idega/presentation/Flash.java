package com.idega.presentation;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.idega.core.file.data.ICFile;

public class Flash extends GenericPlugin {

	private List sources;
	private List files;

	public Flash() {
		super();
		setClassId("D27CDB6E-AE6D-11cf-96B8-444553540000");
		/** @todo add version parameter **/
		setCodeBase("https://active.macromedia.com/flash5/cabs/swflash.cab#version=5,0,0,0");
		setPluginSpace("https://www.macromedia.com/shockwave/download/index.cgi?P1_Prod_Version=ShockwaveFlash");
		setParam("quality", "high");
		setParamAndAttribute("play", "true");
	}

	public Flash(String url) {
		this(url, "untitled");
	}

	public Flash(String url, String name) {
		this();
		// setName(name);
		setURL(url);
		setHeight("100%");
		setWidth("100%");
	}

	public Flash(String url, String name, int width, int height) {
		this();
		// setName(name);
		setURL(url);
		setWidth(width);
		setHeight(height);
	}

	/*
	 * The usual constructor
	 */
	public Flash(String url, int width, int height) {
		this();
		setURL(url);
		setWidth(width);
		setHeight(height);
	}

	public void main(IWContext iwc) throws Exception {
		Random random = new Random();

		String url = "";
		if (sources != null && !sources.isEmpty()) {
			int randomPosition = random.nextInt(sources.size());
			url = (String) sources.get(randomPosition);
		}
		if (files != null && !files.isEmpty()) {
			int randomPosition = random.nextInt(files.size());
			ICFile file = (ICFile) files.get(randomPosition);
			url = getICFileSystem(iwc).getFileURI(file);
		}

		setParam("movie", url);
		setMarkupAttribute("src", url);
	}

	public void setURL(String url) {
		if (sources == null) {
			sources = new ArrayList();
		}
		sources.add(url);
	}

	public void setFile(ICFile file) {
		if (files == null) {
			files = new ArrayList();
		}
		files.add(file);
	}

	public void setLoop(boolean loop) {
		setParamAndAttribute("loop", String.valueOf(loop));
	}

	public void setTransparent() {
		setParamAndAttribute("wmode", "transparent");
	}

	public void setMenuVisibility(String visible) {
		setParamAndAttribute("menu", visible);
	}
}