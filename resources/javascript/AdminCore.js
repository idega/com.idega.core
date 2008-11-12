jQuery.noConflict();

jQuery(document).ready(function() {
	jQuery('#adminTopLayer .adminEditMode').click(function() {
		jQuery('#adminTopLayer li.selected').removeClass('selected');
		jQuery(this).addClass('selected');
		jQuery('body').addClass('edit').removeClass('content').removeClass('preview');
		
		jQuery('div.content_item_toolbar, div.commentsController').fadeOut('slow');
	});
	
	jQuery('#adminTopLayer .adminContentMode').click(function() {
		jQuery('#adminTopLayer li.selected').removeClass('selected');
		jQuery(this).addClass('selected');
		jQuery('body').addClass('content').removeClass('edit').removeClass('preview');
		
		jQuery('div.content_item_toolbar, div.commentsController').fadeIn('slow');
	});

	jQuery('#adminTopLayer .adminPreviewMode').addClass('selected').click(function() {
		jQuery('#adminTopLayer li.selected').removeClass('selected');
		jQuery(this).addClass('selected');
		jQuery('body').addClass('preview').removeClass('edit').removeClass('content');
		
		jQuery('div.content_item_toolbar, div.commentsController').fadeOut('slow');
	});
});