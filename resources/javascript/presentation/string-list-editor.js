
(function($) {
   $.fn.stringListEditor = function(options) {
	   
//	    var opts = $.extend({}, $.fn.mediaSlider.defaults, options);
	    return this.each(function(){
	    	var input = jQuery(this);
	    	var initialization = input.data("initialize");
	    	if(initialization != undefined){
	    		return;
	    	}
	    	input.data("initialize",{initialized:true});
	    	
	    	var editLayer = jQuery("<div style='display:none;'/>");
	    	input.parent().append(editLayer);
	    	
	    	var controlls = jQuery("<div/>");
	    	controlls.css({"text-align" : "right"});
	    	editLayer.append(controlls);
	    	
	    	var closebutton = jQuery("<input type='button' value='X' />");
	    	controlls.append(closebutton);
	    	
	    	
	    	
	    	closebutton.click(function(){
	    		setValues(editLayer,input,editLayer.find(".values-layer"))
	    	});
	    	
	    	var inputsLayer = jQuery("<div/>");
	    	editLayer.append(inputsLayer);
	    	
	    	input.click(function(){
	    		input.blur();
	    		displayInputs(input,inputsLayer);
	    		editLayer.show();
	    		input.hide();
	    	});
	    	
	    	var addLayer = jQuery("<div/>");
	    	editLayer.append(addLayer);
	    	var addButton = jQuery("<input type='button' value='+'/>");
	    	addLayer.append(addButton);
	    	addButton.click(function(){
	    		var inputsLayer = editLayer.find(".values-layer");
	    		inputsLayer.append(createInputDiv(""));
	    	});
	    } ); 
	    
	    function setValues(layer,input,valuesLayer){
	    	var children = valuesLayer.find(".value");
	    	var value = "";
	    	var first = true;
	    	for(var i = 0; i< children.length;i++){
	    		var val = jQuery(children[i]).val();
	    		if((val == undefined) || (val == null) || (val.length < 1)){
	    			continue;
	    		}
	    		if(first){
	    			first = false;
	    		}else{
	    			value += ",";
	    		}
	    		value += val;
	    	}
	    	input.val(value);
	    	input.show();
	    	layer.hide();
	    	valuesLayer.remove();
	    	input.focus();
	    }
	    
	    function displayInputs(input,editLayer){
	    	var valuesLayer = jQuery("<div class='values-layer'/>");
	    	editLayer.append(valuesLayer);
	    	
	    	var string = input.attr("value");
	    	var list = string.split(",");
	    	for(var i = 0; i< list.length;i++){
	    		valuesLayer.append(createInputDiv(list[i]));
	    	}
	    }
	    
	    function createInputDiv(value){
	    	var div = jQuery("<div style='width:33em; text-align:center;'/>");
    		var editInput = jQuery("<input type='text' class='value' style='display:inline-block; width:30em;'  value='"+value+"'/>");
    		div.append(editInput);
    		var deleteButton = jQuery("<input type='button' style='display:inline-block; width:2em;' value='X' />");
    		div.append(deleteButton);
    		deleteButton.click({div:div},function(e){
    			e.data.div.remove();
    		});
    		return div;
	    }
	}
//   $.fn.stringListEditor.defaults = {}
})(jQuery);