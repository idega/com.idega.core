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
	if (scriptToAttach != null)
		remotePage += '&remote_script=' + scriptToAttach;
	if (scriptToExecute != null)
		remotePage += '&remote_js_action=' + scriptToExecute;
		
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
	jQuery(document.body).children().addClass('hidePageContent');
	for (var i = 0; i < regions.length; i++) {
		var region = regions[i];
		if (region != null && region != '') {
			var regionElement = jQuery('#' + region);

			RemotePageViewer.displayParent(regionElement.parent());
			regionElement.parent().children().addClass('hidePageContent');
			if (regionElement.hasClass('hidePageContent')) {
				regionElement.removeClass('hidePageContent');
			} else {
				regionElement.addClass('showPageContent');
			}
		}
	}
}

RemotePageViewer.displayParent = function(parentElement) {
	if (parentElement == null || parentElement.length == 0)
		return;
		
	if (parentElement.hasClass('hidePageContent'))
		parentElement.removeClass('hidePageContent');
		
	RemotePageViewer.displayParent(parentElement.parent());
}