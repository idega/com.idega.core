if (RemotePageViewer == null) var RemotePageViewer = {};

RemotePageViewer.loadPage = function(message, remotePage, server, personalId, containerId, loadAutoLogin, scriptToAttach, scriptToExecute) {
	showLoadingMessage(message);
	if (loadAutoLogin) {
		var dwrCallType = getDwrCallType(true);
		prepareDwr(WebUtil, server + getDefaultDwrPath());
		WebUtil.getAutoLoginUri(personalId, remotePage, {
			callback: function(uri) {
				RemotePageViewer.addPage(uri, containerId, scriptToAttach, scriptToExecute);
			},
			errorHandler: function(message, exception) {
				closeAllLoadingMessages();
				alert('Error loading page: ' + remotePage + (message != null && message != '' ? '. Message from the server: ' + message : ''));
			},
			rpcType: dwrCallType,
			transport: dwrCallType
		});
	} else {
		RemotePageViewer.addPage(remotePage, containerId, scriptToAttach, scriptToExecute);
	}
}

RemotePageViewer.addPage = function(remotePage, containerId, scriptToAttach, scriptToExecute) {
	if (remotePage == null || remotePage == '') {
		alert('Remote page is not provided');
		closeAllLoadingMessages();
		return;
	}
	
	if (scriptToAttach != null)
		remotePage += '&remote_script=' + scriptToAttach;
	if (scriptToExecute != null)
		remotePage += '&remote_js_action=' + scriptToExecute;
	if (scriptToAttach != null || scriptToExecute != null)
		remotePage += '&add_remote_js_session=true';
		
	var frameName = 'remotePage' + containerId;
	jQuery('#' + containerId).html('<iframe src="' + remotePage + '" width="100%" height="100%" name="'+frameName+'" onload="closeAllLoadingMessages();" />');
}

RemotePageViewer.displayRegions = function(regionsToDisplay) {
	if (regionsToDisplay == null || regionsToDisplay == '')
		return;
	
	var regions = regionsToDisplay.split(',');
	if (regions == null || regions.length == 0)
		return;

	jQuery('html').attr('style', 'background-image:none');
	jQuery(document.body).children().addClass('hidePageContent');	//	Hiding everything
	var foundAnyRegion = false;
	for (var i = 0; i < regions.length; i++) {
		var region = regions[i];
		if (region != null && region != '') {
			if (region == 'BODY' && !foundAnyRegion) {
				jQuery('.hidePageContent', document.body).each(function() {
					jQuery(this).removeClass('hidePageContent');
				});
			} else {
				var regionElement = jQuery('#' + region);
				if (regionElement != null && regionElement.length > 0) {
					foundAnyRegion = true;
					var parentElements = [];
					var parentElement = regionElement.parent();
					while (parentElement != null && parentElement.length > 0 && parentElement[0].tagName != 'BODY') {
						parentElements.push(parentElement);
						parentElement = parentElement.parent();
					}
					RemotePageViewer.displayParents(parentElements);
					
					if (regionElement.hasClass('hidePageContent')) {
						regionElement.removeClass('hidePageContent');
					} else {
						regionElement.addClass('showPageContent');
					}
				}
			}
		}
	}
}

RemotePageViewer.displayParents = function(parentElements) {
	if (parentElements == null || parentElements.length == 0)
		return;
	
	for (var i = 0; i < parentElements.length; i++) {
		var parentElement = parentElements[parentElements.length - (i + 1)];
		
		parentElement.children().addClass('hidePageContent');
		if (parentElement.hasClass('hidePageContent'))
			parentElement.removeClass('hidePageContent');
	}	
}