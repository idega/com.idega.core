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
	},
	TEXT_AREA_RESIZER {
		@Override
		public String getFileName() {
			return ".textarearesizer.js";
		}
	},
	AUTO_GROW {
		@Override
		public String getFileName() {
			return ".autogrow.js";
		}
	},
	AUTO_RESIZE {
		@Override
		public String getFileName() {
			return ".autoresize.js";
		}
	},
	HOT_KEY {
		@Override
		public String getFileName() {
			return ".hotkey.js";
		}
	},
	URL_PARSER {
		@Override
		public String getFileName() {
			return ".urlParser.js";
		}
	},
	EDITABLE {
		@Override
		public String getFileName() {
			return ".editable.js";
		}
	},
	EASING {
		@Override
		public String getFileName() {
			return ".easing.js";
		}
	},
	MASKED_INPUT {
		@Override
		public String getFileName() {
			return ".maskedinput.js";
		}
	},
	MOUSE_WHEEL {
		@Override
		public String getFileName() {
			return ".mousewheel.js";
		}
	},
	TEXT_AREA_AUTO_GROW {
		@Override
		public String getFileName() {
			return ".autogrowtextarea.js";
		}
	};

	public abstract String getFileName();
}
