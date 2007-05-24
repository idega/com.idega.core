//	Constants
var ADVANCED_PROPERTIES = new Array();

var CHOOSER_VALUE_VIEWER_ID = null;

/** Logic for Choosers/Advanced handlers starts **/
function saveSelectedValues(message, instanceId, method) {
	showLoadingMessage(message);
	var values = new Array();
	var advancedProperty = null;
	for (var i = 0; i < ADVANCED_PROPERTIES.length; i++) {
		advancedProperty = ADVANCED_PROPERTIES[i];
		values.push(advancedProperty.value);
	}

	ChooserService.updateHandler(values, {
		callback: function(result) {
			saveSelectedValuesCallback(result, message, instanceId, method);
		}
	});
}

function saveSelectedValuesCallback(result, message, instanceId, method) {
	closeLoadingMessage();
	if (result && instanceId != null && method != null) {
		showLoadingMessage(message);
		ChooserService.setModuleProperty(instanceId, method, ADVANCED_PROPERTIES, {
			callback: function(savedSuccessfully) {
				setModulePropertyCallback(savedSuccessfully, instanceId, "true");	//	Temporary
			}
		});
	}
	else {
		closeLoadingMessage();
		return;
	}
}

function setModulePropertyCallback(result, instanceId, needsReload) {
	if (result) {
		ADVANCED_PROPERTIES = new Array();
	}
	reloadPage();
}

function addChooserObject(chooserObject, objectClass, hiddenInputAttribute, chooserValueViewerId, message) {
	var container = chooserObject.parentNode;
	
	CHOOSER_VALUE_VIEWER_ID = null;
	var attributes = chooserObject.attributes;
	if (attributes != null) {
		if (attributes.getNamedItem(chooserValueViewerId) != null) {
			CHOOSER_VALUE_VIEWER_ID = attributes.getNamedItem(chooserValueViewerId).value;
		}
	}
	
	var chooser = null;
	var choosers = getNeededElementsFromListById(container.childNodes, "chooser_presentation_object");
	if (choosers != null) {
		if (choosers.length > 0) {
			chooser = choosers[0];
		}
	}
	if (chooser == null) {
		showLoadingMessage(message);
		ChooserService.getRenderedPresentationObject(objectClass, hiddenInputAttribute, false, {
			callback: function(renderedObject) {
				getRenderedPresentationObjectCallback(renderedObject, container);
			}
		});
	}
	else {
		if (chooser.style.display == null) {
			chooser.style.display = "block";
		}
		else {
			if (chooser.style.display == "block") {
				chooser.style.display = "none";
			}
			else {
				chooser.style.display = "block";
			}
		}
	}
}

function getRenderedPresentationObjectCallback(renderedObject, container) {
	closeLoadingMessage();
	insertNodesToContainer(renderedObject, container);
}

function chooseObject(element, attributeId, attributeValue) {
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
	
	addAdvancedProperty(id, value);
	
	return value;
}

function chooseObjectWithHidden(element, attributeId, attributeValue, hiddenName) {
	var value = chooseObject(element, attributeId, attributeValue);
	if (value == null) {
		return;
	}
	var forms = document.getElementsByTagName("form");
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
		hidden = document.createElement("input");
		hidden.setAttribute("type", "hidden");
		hidden.setAttribute("id", hiddenName);
		hidden.setAttribute("name", hiddenName);
		form.appendChild(hidden);
	}
	hidden.setAttribute("value", value);
}

function setChooserView(element, attributeValue) {
	if (element == null || attributeValue == null || CHOOSER_VALUE_VIEWER_ID == null) {
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
		alert("Error: no value found to set!");
		return;
	}
	
	var viewer = document.getElementById(CHOOSER_VALUE_VIEWER_ID);
	if (viewer == null) {
		return;
	}
	viewer.value = value;
}

function addAdvancedProperty(id, value) {
	if (ADVANCED_PROPERTIES == null) {
		ADVANCED_PROPERTIES = new Array();
	}
	if (existAdvancedProperty(id, value)) {
		return;
	}
	ADVANCED_PROPERTIES.push(new AdvancedProperty(id, value));
}

function removeAdvancedProperty(id) {
	if (id == null) {
		return;
	}
	if (ADVANCED_PROPERTIES == null) {
		return;
	}
	var needless = new Array();
	for (var i = 0; i < ADVANCED_PROPERTIES.length; i++) {
		if (ADVANCED_PROPERTIES[i].id == id) {
			needless.push(ADVANCED_PROPERTIES[i]);
		}
	}
	for (var i = 0; i < needless.length; i++) {
		removeElementFromArray(ADVANCED_PROPERTIES, needless[i]);
	}
}

function existAdvancedProperty(id, value) {
	if (id == null && value == null) {
		return false;
	}
	var advancedProperty = null;
	for (var i = 0; i < ADVANCED_PROPERTIES.length; i++) {
		advancedProperty = ADVANCED_PROPERTIES[i];
		if (advancedProperty.id == id && advancedProperty.value == value) {
			return true;
		}
	}
}

function AdvancedProperty(id, value) {
	this.id = id;
	this.value = value;
}
/** Logic for Choosers/Advanced handlers ends **/