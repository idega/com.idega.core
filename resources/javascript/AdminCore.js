if (AdminCoreHelper == null) var AdminCoreHelper = {};

AdminCoreHelper.currentMode = null;

jQuery.noConflict();

jQuery(document).ready(function() {
	if (!jQuery('body').hasClass('isContentAdmin')) {
		jQuery('div.content_item_toolbar, div.commentsController').hide();
	}
	if (jQuery('body').hasClass('isEditAdmin')) {
		jQuery('.moduleName').dropShadow({left: 0, top: 2, opacity: 0.5, blur: 2});
	}
	if (jQuery('body').hasClass('isThemesAdmin')) {
		jQuery('body').append('<div id="themeSlider"></div>');
	}
	jQuery('.applicationPropertyStyleClass').append("<span class=\"icon\"></span>");

	jQuery('#adminTopLayer li').hover(
			function() {
				jQuery(this).children('.modeHelper').fadeIn('fast');
			},
			function() {
				jQuery(this).children('.modeHelper').fadeOut('fast');
			}
	);
	
	jQuery('.moduleContainer').hover(
			function() {
				jQuery(this).children('.regionInfoImageContainer').dropShadow({left: 0, top: 2, opacity: 0.5, blur: 2});
			},
			function() {
				jQuery(this).children('.regionInfoImageContainer').removeShadow();
			}
	);
	
	jQuery('#adminTopLayer li').click(function() {
		jQuery('#adminTopLayer li.selected').removeClass('selected');
		jQuery(this).addClass('selected');
		jQuery('body').removeClass('isThemesAdmin').removeClass('isEditAdmin').removeClass('isContentAdmin');
		jQuery('.applicationPropertyStyleClass .icon').hide();
		jQuery('div.content_item_toolbar, div.commentsController').hide();
		jQuery('body div#themeSlider').remove();

		if (jQuery(this).hasClass('adminThemesMode')) {
			AdminCoreHelper.currentMode = 'isThemesAdmin';
			
			jQuery('body').addClass('isThemesAdmin');
			AdminToolbarSession.setMode('isThemesAdmin');
			jQuery('body').append('<div id="themeSlider"></div>');
		}

		if (jQuery(this).hasClass('adminEditMode')) {
			AdminCoreHelper.currentMode = 'isEditAdmin';
			
			jQuery('body').addClass('isEditAdmin');
			AdminToolbarSession.setMode('isEditAdmin');
			jQuery('.moduleName').dropShadow({left: 0, top: 1, opacity: 0.5, blur: 2});
		}
		else {
			jQuery('.moduleName').removeShadow();
		}
		
		if (jQuery(this).hasClass('adminContentMode')) {
			AdminCoreHelper.currentMode = 'isContentAdmin';
			
			jQuery('body').addClass('isContentAdmin');
			jQuery('div.content_item_toolbar, div.commentsController').fadeIn('slow');
			jQuery('.applicationPropertyStyleClass .icon').fadeIn('slow');
			
			AdminToolbarSession.setMode('isContentAdmin');
		}
		else {
		}
		
		if (jQuery(this).hasClass('adminPreviewMode')) {
			AdminCoreHelper.currentMode = 'preview';
			
			AdminToolbarSession.setMode('preview');
		}
		
		AdminCoreHelper.initializeInlineEditableComponents();
	})
});

AdminCoreHelper.initializeInlineEditableComponents = function() {
	jQuery.each(jQuery('.InlineEditableComponent'), function() {
		jQuery(this).click(function(event) {
			if (!jQuery('body').hasClass('isContentAdmin')) {
				jQuery(this).replaceWith(jQuery(this).removeClass('inlineEditableInited').clone(false));
				
				return false;
			}
		});
	});
	
	if (jQuery('body').hasClass('isContentAdmin')) {
		jQuery.each(jQuery('.InlineEditableComponent'), function() {
			if (!jQuery(this).hasClass('inlineEditableInited')) {
				AdminCoreHelper.makeComponentEditable(this, jQuery(this).text());
				jQuery(this).addClass('inlineEditableInited');
			}
		});
	}
}

AdminCoreHelper.makeComponentEditable = function(component, oldValue) {
	jQuery(component).editable(function(newText, settings) {
		var instanceId = jQuery(component).attr('id');
		if (instanceId == null || instanceId == '') {
			return;
		}
		
		if (newText == oldValue) {
			jQuery('#' + instanceId).empty().text(oldValue);
			return oldValue;
		}
		
		if (jQuery('input[id=\''+instanceId+'inlineEditingInProcess\'][type=\'hidden\']', jQuery(component)).length == 0) {
			jQuery(component).append('<input id=\''+instanceId+'inlineEditingInProcess\' type=\'hidden\' />');
			
			LazyLoader.loadMultiple(['/dwr/engine.js', '/dwr/interface/BuilderService.js'], function() {
				BuilderService.setLocalizedText(window.location.pathname, instanceId, newText, {
					callback: function(result) {
						if (!result) {
							reloadPage();
						}
						
						jQuery('#' + instanceId).empty().text(newText);
						return newText;
					}
				});
			});
		}
	},
	{
		onblur:		'submit'
	});
}