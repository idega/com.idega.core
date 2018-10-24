jQuery.noConflict();

jQuery(document).ready(function() {
	var isCookiesAccepted = function() {
		var name = 'cookies_accepted=';
		var decodedCookie = decodeURIComponent(document.cookie);
		var parsedCookies = decodedCookie.split(';');
		for(var i = 0; i < parsedCookies.length; i++) {
			var parsedCookie = parsedCookies[i];
			
			while (parsedCookie.charAt(0) == ' ') {
				parsedCookie = parsedCookie.substring(1);
			}
			
			if (parsedCookie.indexOf(name) == 0) {
				return parsedCookie.substring(name.length, parsedCookie.length) == 'true';
			}
		}
		return false;
	};
	
	if (!isCookiesAccepted()) {
		var body = jQuery('body');
		var cookiesFooter = jQuery('<div class="accept-cookies-footer"></div>');
		
		WebUtil.getLocalizedString('com.idega.core', 'cookies.this_site_uses_cookies', 'This site uses cookies', {
			callback: function(siteUsesCookiesText) {
				WebUtil.getLocalizedString('com.idega.core', 'cookies.accept', 'Accept', {
					callback: function(acceptText) {
						WebUtil.getLocalizedString('com.idega.core', 'cookies.see_more_information', 'See more information', {
							callback: function(seeMoreInfoText) {
								cookiesFooter.append(
									'<div class="accept-cookies-footer-content">' +
										'<div class="left-side-content">' +
											'<span>' + siteUsesCookiesText + '</span>' +
											'<a class="button accept-button">' +
												'<span>' + acceptText + '</span>' +
											'</a>' +
										'</div>' +
										'<div class="right-side-content">' +
											'<a href="#">' + seeMoreInfoText + '</a>' +
										'</div>' +
									'</div>'
								);
									
								body.append(cookiesFooter);
								
								jQuery('.accept-cookies-footer-content .accept-button').click(function() {
									document.cookie = 'cookies_accepted=true;' + 'path=/';
									jQuery('div').remove('.accept-cookies-footer');
								});
							}
						});
					}
				});
			}
		});
	}

});
