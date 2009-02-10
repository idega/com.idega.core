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
	},
	BLOCK {
		@Override
		public String getFileName() {
			return ".block.js";
		}	
	},
	TABLE_SORTER {
		@Override
		public String getFileName() {
			return ".tablesorter.js";
		}
	},
	TABLE_SORTER_PAGER {
		@Override
		public String getFileName() {
			return ".tablesorter.pager.js";
		}
	};

	public abstract String getFileName();
}
