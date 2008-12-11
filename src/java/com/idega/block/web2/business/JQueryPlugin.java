package com.idega.block.web2.business;

public enum JQueryPlugin {
	
	COOKIE {
		@Override
		public String getFileName() {
			return ".cookie.js";
		}
	},
	LISTEN {
		@Override
		public String getFileName() {
			return ".listen.js";
		}
	},
	METADATA {
		@Override
		public String getFileName() {
			return ".metadata.js";
		}
	},
	XSLT {
		@Override
		public String getFileName() {
			return ".xslt.js";
		}
	},
	PROGRESS_BAR {
		@Override
		public String getFileName() {
			return ".progressbar.js";
		}
	},
	SCROLL_TO {
		@Override
		public String getFileName() {
			return ".scrollTo.js";
		}
	};

	public abstract String getFileName();
}
