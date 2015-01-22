if (AdminCoreHelper == null) var AdminCoreHelper = {};

AdminCoreHelper.currentMode = null;
AdminCoreHelper.linksValues = [];
AdminCoreHelper.modes = {
	preview: 'preview',
	content: 'isContentAdmin',
	builder: 'isEditAdmin',
	themes: 'isThemesAdmin'
};

AdminCoreHelper.showThemes = function(forceToOpen, setMode) {
	AdminCoreHelper.currentMode = AdminCoreHelper.modes.themes;
	
	if (forceToOpen || !jQuery('body').hasClass(AdminCoreHelper.modes.themes)) {
		jQuery('body').addClass(AdminCoreHelper.modes.themes);
		
		if (setMode) {
			AdminToolbarSession.setMode(AdminCoreHelper.modes.themes);
		}	
		
		jQuery('body').append('<div id="themeSlider" style="display: none;"></div>');
		IWCORE.getRenderedComponentByClassName({
			className: 'com.idega.content.themes.presentation.ThemesSliderViewer',
			container: 'themeSlider',
			properties: [{id: 'setInitAction', value: 'manageSlider(null);'}, {id: 'setHiddenOnLoad', value: 'true'}],
			rewrite: true
		});
		jQuery('#themeSlider').show('fast');
	}
}

AdminCoreHelper.initializeInlineEditableComponents = function() {
	jQuery.each(jQuery('.InlineEditableComponent'), function() {
		jQuery(this).click(function(event) {
			if (!jQuery('body').hasClass(AdminCoreHelper.modes.content)) {
				jQuery(this).replaceWith(jQuery(this).removeClass('inlineEditableInited').clone(false));
				
				if (this.tagName == 'a' || this.tagName == 'A') {
					return true;
				}
				return false;
			}
		});
	});
	
	if (jQuery('body').hasClass(AdminCoreHelper.modes.content)) {
		jQuery.each(jQuery('.InlineEditableComponent'), function() {
			if (!jQuery(this).hasClass('inlineEditableInited')) {
				AdminCoreHelper.makeComponentEditable(this, jQuery(this).text());
				jQuery(this).addClass('inlineEditableInited');
			}
		});
	}
}

AdminCoreHelper.restoreInlineEditableComponents = function() {
	if (AdminCoreHelper.linksValues == null || AdminCoreHelper.linksValues.length == 0) {
		return;
	}
	
	jQuery.each(AdminCoreHelper.linksValues, function() {
		jQuery('#' + this.id).attr('href', this.href);
	});
	
	AdminCoreHelper.linksValues = [];
}

AdminCoreHelper.makeComponentEditable = function(component, oldValue) {
	if (component.tagName == 'a' || component.tagName == 'A') {
		AdminCoreHelper.linksValues.push({id: jQuery(component).attr('id'), href: jQuery(component).attr('href')});
		jQuery(component).attr('href', 'javascript:void(0);');
	}
	
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
				BuilderService.setLocalizedText(instanceId, newText, {
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

jQuery(window).load(function() {
	if (typeof(AdminToolbarSession) == 'function') {
		AdminToolbarSession.getMode({callback:
			function(mode) {
				if (mode == AdminCoreHelper.modes.preview) {
					AdminCoreHelper.currentMode = mode;
					jQuery('li.adminPreviewMode').click();
				}
			}
		});
	}
});

jQuery(document).ready(function() {
	var toolbar = jQuery('#adminTopLayer').remove();
	jQuery('body').prepend(toolbar);
	
	if (!jQuery('body').hasClass(AdminCoreHelper.modes.content)) {
		AdminCoreHelper.currentMode = AdminCoreHelper.modes.content;
		jQuery('div.content_item_toolbar, div.commentsController').hide();
		AdminCoreHelper.initializeInlineEditableComponents();
	}
	if (jQuery('body').hasClass(AdminCoreHelper.modes.builder)) {
		AdminCoreHelper.currentMode = AdminCoreHelper.modes.builder;
	}
	if (jQuery('body').hasClass(AdminCoreHelper.modes.themes)) {
		AdminCoreHelper.currentMode = AdminCoreHelper.modes.themes;
		AdminCoreHelper.showThemes(true, false);
	}
	if (jQuery('body').hasClass(AdminCoreHelper.modes.preview)) {
		AdminCoreHelper.currentMode = AdminCoreHelper.modes.preview;
	}
	
	try {
		jQuery('.applicationPropertyStyleClass').append('<span class=\'icon\'></span>');
	} catch(e) {}

	jQuery('#adminTopLayer li').hover(
		function() {
			jQuery(this).children('.modeHelper').fadeIn('fast');
		},
		function() {
			jQuery(this).children('.modeHelper').fadeOut('fast');
		}
	);
	
	jQuery('#adminTopLayer li').click(function() {
		jQuery('#adminTopLayer li.selected').removeClass('selected');
		jQuery(this).addClass('selected');
		jQuery('body').removeClass(AdminCoreHelper.modes.themes).removeClass(AdminCoreHelper.modes.builder).removeClass(AdminCoreHelper.modes.content);
		jQuery('.applicationPropertyStyleClass .icon').hide();
		jQuery('div.content_item_toolbar, div.commentsController').hide();
		jQuery('body div#themeSlider').remove();

		closeAllLoadingMessages();
		jQuery('.LocalLoadLayer').remove();

		if (jQuery(this).hasClass('adminThemesMode')) {
			AdminCoreHelper.showThemes(false, true);
		}

		if (jQuery(this).hasClass('adminEditMode')) {
			AdminCoreHelper.currentMode = AdminCoreHelper.modes.builder;
			
			jQuery('body').addClass(AdminCoreHelper.modes.builder);
			AdminToolbarSession.setMode(AdminCoreHelper.modes.builder);
			if (typeof BuilderHelper != 'undefined') {
				BuilderHelper.initializeBuilder(AdminCoreHelper.modes.builder);
			}
		}
		
		if (jQuery(this).hasClass('adminContentMode')) {
			AdminCoreHelper.currentMode = AdminCoreHelper.modes.content;
			
			jQuery('body').addClass(AdminCoreHelper.modes.content);
			jQuery('div.content_item_toolbar, div.commentsController').fadeIn('slow');
			jQuery('.applicationPropertyStyleClass .icon').fadeIn('slow');
			
			AdminToolbarSession.setMode(AdminCoreHelper.modes.content);
		}
		else {
		}
		
		if (jQuery(this).hasClass('adminPreviewMode')) {
			AdminCoreHelper.currentMode = AdminCoreHelper.modes.preview;
			
			AdminToolbarSession.setMode(AdminCoreHelper.modes.preview);
		}
		
		AdminCoreHelper.restoreInlineEditableComponents();
		AdminCoreHelper.initializeInlineEditableComponents();
	});
	
	jQuery(document).click(function() {
		jQuery('.themeTemplateChildrenContainerAsStackStyle').remove();
		jQuery('.themeChooseStyle').remove();
	});
	
	jQuery(window).resize(function() {
		if (AdminCoreHelper.currentMode == AdminCoreHelper.modes.themes) {
			try {
				ThemesSliderHelper.resizeSlider();
			} catch(e) {}
		}
	});
});