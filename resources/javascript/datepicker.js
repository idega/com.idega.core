(function ($) {
	var datepickerHelper = {
			cur : [],
			prv : []
	};
	var addDate = function(iwdp){
		var d = "";
    	if(iwdp.d1){
    		d = d + iwdp.d1;
    	}
    	if(iwdp.d2){
    		d = d + ' - ' + iwdp.d2;
    	}
    	jQuery('#'+iwdp.id).val(d);
	}
	var selectDate = function(iwdp,curDate){
		datepickerHelper.prv[iwdp.id] = datepickerHelper.cur[iwdp.id];
        datepickerHelper.cur[iwdp.id] = curDate.getTime();
        var el = $('#'+iwdp.id);
        var prv = datepickerHelper.prv[iwdp.id];
        var cur = datepickerHelper.cur[iwdp.id];
        if ( prv == -1) {
           prv = cur;
           el.datepicker('setDate', curDate );
           iwdp.d1 = el.val();
           iwdp.d2 = null;
        } else {
        	el.datepicker('setDate', new Date(Math.min(prv,cur)) );
        	iwdp.d1 = el.val();
        	el.datepicker('setDate', new Date(Math.max(prv,cur)) );
        	iwdp.d2 = el.val();
        }
        addDate(iwdp);
	}
	$.fn.iwDatePicker = function (o) {
		o = o || {};
		var tmp_args = arguments;

		if (typeof(o) === 'string') {
			if (o === 'getDate') {
				return $.fn.datepicker.apply($(this[0]), tmp_args);
			}else if (o === 'setDate') {
				var el = jQuery(this);
				$.fn.datetimepicker.apply(el, tmp_args);
				if(el.datepicker('option','rangeSelect')){
					var d = tmp_args[1];
					var iwdp = el.datepicker('option','iwdp');
					if(d instanceof Date){
						selectDate(iwdp,d);
					}
				}
			}else {
				return this.each(function () {
					var $t = $(this);
					$t.datepicker.apply($t, tmp_args);
				});
			}
		} else {
			if(o.rangeSelect){
				var getOpts = function(inst){
					if(inst.settings){
	            		return inst.settings;
	            	}else{
	            		return inst.inst.settings;
	            	}
				}
				datepickerHelper.cur[o.iwdp.id] = -1;
				datepickerHelper.prv[o.iwdp.id] = -1;
				var prevbeforeShowDay = o.beforeShowDay;
				o.beforeShowDay = function ( date ) {
					var ret = [true, (( (date.getTime() >= Math.min(datepickerHelper.prv[o.iwdp.id], datepickerHelper.cur[o.iwdp.id])) && (date.getTime() <= Math.max(datepickerHelper.prv[o.iwdp.id], datepickerHelper.cur[o.iwdp.id]))) ? 'date-range-selected' : '')];
					if(prevbeforeShowDay){
						prevbeforeShowDay.apply(el, arguments);
					}
					return ret;
				};
				var prevonSelect = o.onSelect;
	            o.onSelect = function ( dateText, inst ) {
	            	var opts = getOpts(inst);
	            	var curDate = new Date(inst.selectedYear, inst.selectedMonth, inst.selectedDay);
	            	selectDate(opts.iwdp,curDate);
	            	if(prevonSelect){
	            		prevonSelect.apply(el, arguments);
					}
	            };
	            var prevonChangeMonthYear = o.onChangeMonthYear;
	            o.onChangeMonthYear = function(year, month, inst){
	            	addDate(getOpts(inst).iwdp);
	            	if(prevonChangeMonthYear){
	            		prevonChangeMonthYear.apply(el, arguments);
					}
	            }
	            var prevonClose = o.onClose;
	            o.onClose = function(year, month, inst){
	            	addDate(getOpts(inst).iwdp);
	            	if(prevonClose){
	            		prevonClose.apply(el, arguments);
					}
	            }
	            o.showButtonPanel = true;
			}
//			if(!o.showTime || o.rangeSelect){
//				o.showTimepicker = false;
//			}
			return this.each(function () {
				var $t = $(this);
				if(o.showTime || o.rangeSelect){
					$t.datetimepicker(o);
				}else{
					$t.datepicker(o);
				}
			});
		}
	}
	
})(jQuery);


