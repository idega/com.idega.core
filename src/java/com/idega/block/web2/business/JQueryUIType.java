package com.idega.block.web2.business;

/**
 * @author <a href="mailto:civilis@idega.com">Vytautas Čivilis</a>
 * @version $Revision: 1.2 $
 * 
 *          Last modified: $Date: 2008/07/14 12:49:54 $ by $Author: valdas $
 */
public enum JQueryUIType {

	UI_ACCORDION {
		public String getFileName() {
			return "ui.accordion.js";
		}
	},
	UI_AUTOCOMPLETE {
		public String getFileName() {
			return "ui.autocomplete.js";
		}
	},
	UI_BUTTON {
		public String getFileName() {
			return "ui.button.js";
		}
	},
	UI_CORE {
		public String getFileName() {
			return "ui.core.js";
		}
	},
	UI_DATEPICKER {
		public String getFileName() {
			return "ui.datepicker.js";
		}
	},
	UI_DIALOG {
		public String getFileName() {
			return "ui.dialog.js";
		}
	},
	UI_DRAGGABLE {
		public String getFileName() {
			return "ui.draggable.js";
		}
	},
	UI_DROPPABLE {
		public String getFileName() {
			return "ui.droppable.js";
		}
	},
	UI_MOUSE {
		public String getFileName() {
			return "ui.mouse.js";
		}
	},
	UI_POSITION {
		public String getFileName() {
			return "ui.position.js";
		}
	},
	UI_PROGRESSBAR {
		public String getFileName() {
			return "ui.progressbar.js";
		}
	},
	UI_RESIZABLE {
		public String getFileName() {
			return "ui.resizable.js";
		}
	},
	UI_SELECTABLE {
		public String getFileName() {
			return "ui.selectable.js";
		}
	},
	UI_SLIDER {
		public String getFileName() {
			return "ui.slider.js";
		}
	},
	UI_TABS {
		public String getFileName() {
			return "ui.tabs.js";
		}
	},
	UI_WIDGET {
		public String getFileName() {
			return "ui.widget.js";
		}
	};

	public abstract String getFileName();
}
