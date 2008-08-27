if(ChooserHelper == null) var ChooserHelper = function() {};

ChooserHelper.prototype.ADVANCED_PROPERTIES = new Array();
ChooserHelper.prototype.CHOOSER_VALUE_VIEWER_ID = null;

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
	closeAllLoadingMessages();
	if (result && instanceId != null && method != null) {
		showLoadingMessage(message);
		
		ChooserService.setModuleProperty(instanceId, method, chooser_helper.ADVANCED_PROPERTIES, {
			callback: function(savedSuccessfully) {
				chooser_helper.setModulePropertyCallback(savedSuccessfully, instanceId, needsReload, reloadMessage, actionAfter, method);
			}
		});
	}
	else {
		closeAllLoadingMessages();
		return;
	}
}

ChooserHelper.prototype.setModulePropertyCallback = function(result, instanceId, needsReload, reloadMessage, actionAfter, method) {
	closeAllLoadingMessages();
	if (!result) {
		return false;
	}
	
	var activePropertyBoxId = null;
	try {
		activePropertyBoxId = getActivePropertyBoxId();
		if (activePropertyBoxId != null) {
			var box = document.getElementById(activePropertyBoxId);
			if (box != null) {
				box.className = 'modulePropertyIsSet';

				getRemoveBuilderPropertyImage(instanceId, method);
			}
		}
	} catch(ex) {}
	
	if (needsReload) {
		addActionBeforeClosingMoodalBox(reloadMessage);
		return true;
	}
	
	try {
		if (actionAfter != null) {
			actionAfter();
		}
	} catch(ex) {
		addActionBeforeClosingMoodalBox(reloadMessage);
		return false;
	}
	
	return true;
}

function addActionBeforeClosingMoodalBox(reloadMessage) {
	var actionOnClose = function() {
		showLoadingMessage(reloadMessage);
		reloadPage();
	};
	addActionForMoodalBoxOnCloseEvent(actionOnClose);
}

addChooserObject = function(id, objectClass, hiddenInputAttribute, chooserValueViewerId, message, value, displayValue) {
	var chooserObject = document.getElementById(id);
	if (chooserObject == null) {
		return false;
	}
	
	var container = chooserObject.parentNode;
	
	var chooserObjectName = null;
	var attributes = chooserObject.attributes;
	if (attributes != null) {
		if (attributes.getNamedItem('choosername') != null) {
			chooserObjectName = attributes.getNamedItem('choosername').value;
			
			var actions = 'var needToCreateNewChooser = false; try{if(!'+chooserObjectName+'){needToCreateNewChooser = true;} } catch(e){needToCreateNewChooser = true;}';
			actions += ' if(needToCreateNewChooser) {'+chooserObjectName+' = new ChooserHelper();} ';
			if (attributes.getNamedItem(chooserValueViewerId) != null) {
				actions += '' + chooserObjectName + ".CHOOSER_VALUE_VIEWER_ID = '" + attributes.getNamedItem(chooserValueViewerId).value + "'; ";
			}
			
			actions += '' + chooserObjectName + '.ADVANCED_PROPERTIES = new Array();'
		}
		
		window.eval(actions);
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
		ChooserService.getRenderedPresentationObject(objectClass, hiddenInputAttribute, chooserObjectName, value, displayValue, true, {
			callback: function(renderedObject) {
				closeAllLoadingMessages();
				insertNodesToContainer(renderedObject, container);
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

ChooserHelper.prototype.chooseObject = function(elementId, attributeId, attributeValue) {
	var element = document.getElementById(elementId);
	if (element == null) {
		return null;
	}
	var attributes = element.attributes;
	if (attributes == null) {
		return null;
	}
	var value = null;
	var attribute = attributes.getNamedItem(attributeId);
	if (attribute != null) {
		value = attribute.value == null ? attribute.nodeValue : attribute.value;
	}
	
	if (value == null) {
		return null;
	}
	
	return value;
}

ChooserHelper.prototype.chooseObjectWithHidden = function(elementId, attributeId, attributeValue, hiddenName) {
	var value = this.chooseObject(elementId, attributeId, attributeValue);
	if (value == null) {
		return;
	}
	this.addAdvancedProperty(attributeId, value);
	
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

ChooserHelper.prototype.setChooserView = function(elementId, attributeValue) {
	if (elementId == null || attributeValue == null || this.CHOOSER_VALUE_VIEWER_ID == null) {
		return;
	}
	var value = this.chooseObject(elementId, attributeValue, attributeValue);
	if (value == null) {
		alert('ChooserHelper.js: Error: no value found to set!');
		return;
	}
	
	var viewer = document.getElementById(this.CHOOSER_VALUE_VIEWER_ID);
	if (viewer == null) {
		return;
	}
	viewer.value = value;
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

ChooserHelper.prototype.existAdvancedProperty = function(id) {
	var property = this.getAdvancedProperty(id);
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

ChooserHelperAdvancedProperty = function(id, value) {
	this.id = id;
	this.value = value;
}
/** Logic for Choosers/Advanced handlers ends **/