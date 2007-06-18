if(ChooserHelper == null) var ChooserHelper = function() {};
if(ChooserHelperGlobal == null) var ChooserHelperGlobal = {};

ChooserHelper.prototype.ADVANCED_PROPERTIES = new Array();
ChooserHelper.prototype.CHOOSER_VALUE_VIEWER_ID = null;

ChooserHelperGlobal.ADVANCED_PROPERTIES = new Array();
ChooserHelperGlobal.CHOOSER_VALUE_VIEWER_ID = null;

/** Logic for Choosers/Advanced handlers starts **/
ChooserHelper.prototype.saveSelectedValues = function(message, instanceId, method, needsReload, reloadMessage, actionAfter) {
	showLoadingMessage(message);
	var values = new Array();
	var advancedProperty = null;
	for (var i = 0; i < this.ADVANCED_PROPERTIES.length; i++) {
		advancedProperty = this.ADVANCED_PROPERTIES[i];
		values.push(advancedProperty.value);
	}
	var callback_function = this.saveSelectedValuesCallback;
	var chooser_helper = this;
	
	ChooserService.updateHandler(values, {
		callback: function(result) {
			callback_function(result, message, instanceId, method, needsReload, reloadMessage, actionAfter, chooser_helper);
		}
	});
}

ChooserHelper.prototype.saveSelectedValuesCallback = function(result, message, instanceId, method, needsReload, reloadMessage, actionAfter, chooser_helper) {
	closeLoadingMessage();
	if (result && instanceId != null && method != null) {
		showLoadingMessage(message);
		
		ChooserService.setModuleProperty(instanceId, method, chooser_helper.ADVANCED_PROPERTIES, {
			callback: function(savedSuccessfully) {
				chooser_helper.setModulePropertyCallback(savedSuccessfully, instanceId, needsReload, reloadMessage, actionAfter);
			}
		});
	}
	else {
		closeLoadingMessage();
		return;
	}
}

ChooserHelper.prototype.setModulePropertyCallback = function(result, instanceId, needsReload, reloadMessage, actionAfter) {
	closeLoadingMessage();
	if (!result) {
		return;
	}
	if (needsReload) {
		var activePropertyBoxId = null;
		try {
			activePropertyBoxId = getActivePropertyBoxId();
			if (activePropertyBoxId != null) {
				var box = document.getElementById(activePropertyBoxId);
				if (box != null) {
					box.className = 'modulePropertyIsSet';
				}
			}
		} catch(ex) {}
		var actionOnClose = function() {
			showLoadingMessage(reloadMessage);
			reloadPage();
		};
		addActionForMoodalBoxOnCloseEvent(actionOnClose);
		return;
	}
	try {
		if (actionAfter != null) {
			actionAfter();
		}
	} catch(ex) {
		return false;
	}
}

ChooserHelperGlobal.addChooserObject = function(chooserObject, objectClass, hiddenInputAttribute, chooserValueViewerId, message) {
	var container = chooserObject.parentNode;
	
	ChooserHelperGlobal.CHOOSER_VALUE_VIEWER_ID = null;
	var attributes = chooserObject.attributes;
	if (attributes != null) {
		if (attributes.getNamedItem(chooserValueViewerId) != null) {
			ChooserHelperGlobal.CHOOSER_VALUE_VIEWER_ID = attributes.getNamedItem(chooserValueViewerId).value;
		}
	}
	
	var chooser = null;
	var choosers = getNeededElementsFromListById(container.childNodes, 'chooser_presentation_object');
	if (choosers != null) {
		if (choosers.length > 0) {
			chooser = choosers[0];
		}
	}
	if (chooser == null) {
		showLoadingMessage(message);
		ChooserService.getRenderedPresentationObject(objectClass, hiddenInputAttribute, false, {
			callback: function(renderedObject) {
				ChooserHelperGlobal.getRenderedPresentationObjectCallback(renderedObject, container);
			}
		});
	}
	else {
		if (chooser.style.display == null) {
			chooser.style.display = 'block';
		}
		else {
			if (chooser.style.display == 'block') {
				chooser.style.display = 'none';
			}
			else {
				chooser.style.display = 'block';
			}
		}
	}
}

ChooserHelperGlobal.getRenderedPresentationObjectCallback = function(renderedObject, container) {
	closeLoadingMessage();
	insertNodesToContainer(renderedObject, container);
}

ChooserHelperGlobal.chooseObject = function(element, attributeId, attributeValue) {
	if (element == null) {
		return null;
	}
	var attributes = element.attributes;
	if (attributes == null) {
		return null;
	}
	var id = attributeId;
	var value = null;
	if (attributes.getNamedItem(attributeId) != null) {
		value = attributes.getNamedItem(attributeId).value;
	}
	
	if (value == null) {
		return null;
	}
	
	ChooserHelperGlobal.addAdvancedProperty(id, value);
	
	return value;
}

ChooserHelperGlobal.chooseObjectWithHidden = function(element, attributeId, attributeValue, hiddenName) {
	var value = ChooserHelperGlobal.chooseObject(element, attributeId, attributeValue);
	if (value == null) {
		return;
	}
	var forms = document.getElementsByTagName('form');
	if (forms == null) {
		return;
	}
	if (forms.length == 0) {
		return;
	}
	
	var form = forms[0];
	if (form == null) {
		return;
	}
	
	var hidden = document.getElementById(hiddenName);
	if (hidden == null) {
		hidden = document.createElement('input');
		hidden.setAttribute('type', 'hidden');
		hidden.setAttribute('id', hiddenName);
		hidden.setAttribute('name', hiddenName);
		form.appendChild(hidden);
	}
	hidden.setAttribute('value', value);
}

ChooserHelperGlobal.setChooserView = function(element, attributeValue) {
	if (element == null || attributeValue == null || ChooserHelperGlobal.CHOOSER_VALUE_VIEWER_ID == null) {
		return;
	}
	var attributes = element.attributes;
	if (attributes == null) {
		return;
	}
	var value = null;
	if (attributes.getNamedItem(attributeValue) != null) {
		value = attributes.getNamedItem(attributeValue).value;
	}
	if (value == null) {
		alert('Error: no value found to set!');
		return;
	}
	
	var viewer = document.getElementById(ChooserHelperGlobal.CHOOSER_VALUE_VIEWER_ID);
	if (viewer == null) {
		return;
	}
	viewer.value = value;
}

ChooserHelperGlobal.addAdvancedProperty = function(id, value) {
	if (ChooserHelperGlobal.ADVANCED_PROPERTIES == null) {
		ChooserHelperGlobal.ADVANCED_PROPERTIES = new Array();
	}
	if (ChooserHelperGlobal.existAdvancedProperty(id)) {
		ChooserHelperGlobal.removeAdvancedProperty(id);	//	Removing old value
	}
	ChooserHelperGlobal.ADVANCED_PROPERTIES.push(new ChooserHelperAdvancedProperty(id, value));
}

ChooserHelper.prototype.addAdvancedProperty = function(id, value) {
	if (this.ADVANCED_PROPERTIES == null) {
		this.ADVANCED_PROPERTIES = new Array();
	}
	if (this.existAdvancedProperty(id)) {
		this.removeAdvancedProperty(id);	//	Removing old value
	}
	this.ADVANCED_PROPERTIES.push(new ChooserHelperAdvancedProperty(id, value));
}

ChooserHelperGlobal.removeAdvancedProperty = function(id) {
	if (id == null) {
		return;
	}
	if (ChooserHelperGlobal.ADVANCED_PROPERTIES == null) {
		return;
	}
	var needless = new Array();
	for (var i = 0; i < ChooserHelperGlobal.ADVANCED_PROPERTIES.length; i++) {
		if (ChooserHelperGlobal.ADVANCED_PROPERTIES[i].id == id) {
			needless.push(ChooserHelperGlobal.ADVANCED_PROPERTIES[i]);
		}
	}
	for (var i = 0; i < needless.length; i++) {
		removeElementFromArray(ChooserHelperGlobal.ADVANCED_PROPERTIES, needless[i]);
	}
}

ChooserHelper.prototype.removeAdvancedProperty = function(id) {
	if (id == null) {
		return;
	}
	if (this.ADVANCED_PROPERTIES == null) {
		return;
	}
	var needless = new Array();
	for (var i = 0; i < this.ADVANCED_PROPERTIES.length; i++) {
		if (this.ADVANCED_PROPERTIES[i].id == id) {
			needless.push(this.ADVANCED_PROPERTIES[i]);
		}
	}
	for (var i = 0; i < needless.length; i++) {
		removeElementFromArray(this.ADVANCED_PROPERTIES, needless[i]);
	}
}

ChooserHelper.prototype.removeAllAdvancedProperties = function() {
	this.ADVANCED_PROPERTIES = new Array();
}

ChooserHelperGlobal.removeAllAdvancedProperties = function() {
	ChooserHelperGlobal.ADVANCED_PROPERTIES = new Array();
}

ChooserHelper.prototype.existAdvancedProperty = function(id) {
	var property = this.getAdvancedProperty(id);
	if (property == null) {
		return false;
	}
	return true;
}

ChooserHelperGlobal.existAdvancedProperty = function(id) {
	var property = ChooserHelperGlobal.getAdvancedProperty(id);
	if (property == null) {
		return false;
	}
	return true;
}

ChooserHelper.prototype.getAdvancedProperty = function(id) {
	if (id == null) {
		return null;
	}
	for (var i = 0; i < this.ADVANCED_PROPERTIES.length; i++) {
		if (this.ADVANCED_PROPERTIES[i].id == id) {
			return this.ADVANCED_PROPERTIES[i];
		}
	}
	return null;
}

ChooserHelperGlobal.getAdvancedProperty = function(id) {
	if (id == null) {
		return null;
	}
	for (var i = 0; i < ChooserHelperGlobal.ADVANCED_PROPERTIES.length; i++) {
		if (ChooserHelperGlobal.ADVANCED_PROPERTIES[i].id == id) {
			return ChooserHelperGlobal.ADVANCED_PROPERTIES[i];
		}
	}
	return null;
}

ChooserHelperAdvancedProperty = function(id, value) {
	this.id = id;
	this.value = value;
}
/** Logic for Choosers/Advanced handlers ends **/