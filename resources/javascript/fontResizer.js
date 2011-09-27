jQuery.noConflict();

jQuery(document).ready(function() {
	var originalFontSize = jQuery('body').css('font-size');
	
	// Normal Font Size
	jQuery("ul.fontResizer li.normal").click(function() {
		jQuery('body').css('font-size', originalFontSize);
		return false;
	});
	
	// Increase Font Size
	jQuery("ul.fontResizer li.larger").click(function() {
		var currentFontSize = jQuery('body').css('font-size');
		var currentFontSizeNum = parseFloat(currentFontSize, 10);
		var newFontSize = currentFontSizeNum * 1.2;
		jQuery('body').css('font-size', newFontSize);
		return false;
	});
	
	// Decrease Font Size
	jQuery("ul.fontResizer li.smaller").click(function() {
		var currentFontSize = jQuery('body').css('font-size');
		var currentFontSizeNum = parseFloat(currentFontSize, 10);
		var newFontSize = currentFontSizeNum * 0.8;
		jQuery('body').css('font-size', newFontSize);
		return false;
	});
});