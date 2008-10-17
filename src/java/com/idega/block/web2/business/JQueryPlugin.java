package com.idega.block.web2.business;

public enum JQueryPlugin {
	
	COOKIE { public String getFileName() { return ".cookie.js"; } },
	LISTEN { public String getFileName() { return ".listen.js"; } },
	METADATA { public String getFileName() { return ".metadata.js"; } },
	XSLT { public String getFileName() { return ".xslt.js"; } };

	public abstract String getFileName();
}
