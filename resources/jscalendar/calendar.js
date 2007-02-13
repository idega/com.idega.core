/* Author: Mihai Bazon, September 2002
 * http://students.infoiasi.ro/~mishoo
 *
 * Version: 0.9
 *
 * Feel free to use this script under the terms of the GNU General Public
 * License, as long as you do not remove or alter this notice.
 */

// the Calendar object constructor.
Calendar = function (mondayFirst, dateStr, yourHandler, yourCloseHandler) {
	// member variables
	this.activeDateEl = null;
	this.activeMonEl = null;
	this.activeYearEl = null;
	this.currentDateEl = null;
	this.disabledHandler = null;
	this.timeout = null;
	this.yourHandler = yourHandler || null;
	this.yourCloseHandler = yourCloseHandler || null;
	this.dragging = false;
	this.minYear = 1970;
	this.maxYear = 2050;
	this.dateFormat = "y-mm-dd";
	this.isPopup = true;
	this.mondayFirst = mondayFirst;
	this.dateStr = dateStr;
};

// ** constants

// "static", needed for event handlers.
Calendar._C = null;

Calendar.is_ie = ( (navigator.userAgent.toLowerCase().indexOf("msie") != -1) &&
		   (navigator.userAgent.toLowerCase().indexOf("opera") == -1) );
Calendar._MD = new Array(31,28,31,30,31,30,31,31,30,31,30,31);

// ** public functions (these can be thought as static, since none of them use
// ** "this" and they can be accessed without using an instance of the object).

Calendar.getAbsolutePos = function(el) {
	var r = { x: el.offsetLeft, y: el.offsetTop };
	if (el.offsetParent) {
		var tmp = Calendar.getAbsolutePos(el.offsetParent);
		r.x += tmp.x;
		r.y += tmp.y;
	}
	return r;
};

Calendar._add_evs = function(el) {
	Calendar.addEvent(el, "mouseover", Calendar.dayMouseOver);
	Calendar.addEvent(el, "mousedown", Calendar.dayMouseDown);
	Calendar.addEvent(el, "mouseout", Calendar.dayMouseOut);
	if (Calendar.is_ie) {
		Calendar.addEvent(el, "dblclick", Calendar.dayMouseDblClick);
		el.setAttribute("unselectable", true);
	}
};

Calendar._del_evs = function(el) {
	Calendar.removeEvent(el, "mouseover", Calendar.dayMouseOver);
	Calendar.removeEvent(el, "mousedown", Calendar.dayMouseDown);
	Calendar.removeEvent(el, "mouseout", Calendar.dayMouseOut);
	if (Calendar.is_ie) {
		Calendar.removeEvent(el, "dblclick", Calendar.dayMouseDblClick);
	}
};

Calendar.isRelated = function (el, evt) {
	var related = evt.relatedTarget;
	if (!related) {
		var type = evt.type;
		if (type == "mouseover") {
			related = evt.fromElement;
		} else if (type == "mouseout") {
			related = evt.toElement;
		}
	}
	while (related) {
		if (related == el) {
			return true;
		}
		related = related.parentNode;
	}
	return false;
};

Calendar.getMonthDays = function(year, month) {
	if (((0 == (year%4)) && ( (0 != (year%100)) || (0 == (year%400)))) && month == 1) {
		return 29;
	} else {
		return Calendar._MD[month];
	}
};

Calendar.removeClass = function(el, className) {
	if (!(el && el.className)) {
		return;
	}
	var classes = el.className.split(" ");
	var newClasses = new Array;
	for (i = 0; i < classes.length; ++i) {
		if (classes[i] != className) {
			newClasses[newClasses.length] = classes[i];
		}
	}
	el.className = newClasses.join(" ");
};

Calendar.addClass = function(el, className) {
	el.className += " " + className;
};

Calendar.getElement = function(ev) {
	if (window.event) {
		return window.event.srcElement;
	} else {
		return ev.currentTarget;
	}
};

Calendar.getTargetElement = function(ev) {
	if (window.event) {
		return window.event.srcElement;
	} else {
		return ev.target;
	}
};

Calendar.stopEvent = function(ev) {
	if (window.event) {
		window.event.cancelBubble = true;
		window.event.returnValue = false;
	} else {
		ev.preventDefault();
		ev.stopPropagation();
	}
};

Calendar.addEvent = function(el, evname, func) {
	if (el.attachEvent) {
		el.attachEvent("on" + evname, func);
	} else {
		el.addEventListener(evname, func, true);
	}
};

Calendar.removeEvent = function(el, evname, func) {
	if (el.detachEvent) {
		el.detachEvent("on" + evname, func);
	} else {
		el.removeEventListener(evname, func, true);
	}
};

Calendar.findMonth = function(el) {
	if (typeof el.month != 'undefined') {
		return el;
	} else if (typeof el.parentNode.month != 'undefined') {
		return el.parentNode;
	}
	return null;
};

Calendar.findYear = function(el) {
	if (typeof el.year != 'undefined') {
		return el;
	} else if (typeof el.parentNode.year != 'undefined') {
		return el.parentNode;
	}
	return null;
};

Calendar.tableMouseUp = function(ev) {
	var cal = Calendar._C;
	if (!cal) {
		return false;
	}
	if (cal.timeout) {
		clearTimeout(cal.timeout);
	}
	var el = cal.activeDateEl;
	if (!el) {
		return false;
	}
	var target = Calendar.getTargetElement(ev);
	Calendar.removeClass(el, "active");
	if (target == el || target.parentNode == el) {
		Calendar.cellClick(el);
	}
	var mon = Calendar.findMonth(target);
	var date = null;
	if (mon) {
		date = new Date(cal.date);
		if (mon.month != date.getMonth()) {
			date.setMonth(mon.month);
			cal.setDate(date);
		}
	} else {
		var year = Calendar.findYear(target);
		if (year) {
			date = new Date(cal.date);
			if (year.year != date.getFullYear()) {
				date.setFullYear(year.year);
				cal.setDate(date);
			}
		}
	}
	Calendar.removeEvent(document, "mouseup", Calendar.tableMouseUp);
	Calendar.removeEvent(document, "mouseover", Calendar.tableMouseOver);
	Calendar.removeEvent(document, "mousemove", Calendar.tableMouseOver);
	cal.hideCombos();
	Calendar.stopEvent(ev);
	Calendar._C = null;
};

Calendar.tableMouseOver = function (ev) {
	var cal = Calendar._C;
	if (!cal) {
		return;
	}
	var el = cal.activeDateEl;
	var target = Calendar.getTargetElement(ev);
	if (target == el || target.parentNode == el) {
		Calendar.addClass(el, "hilite active");
	} else {
		Calendar.removeClass(el, "active");
		Calendar.removeClass(el, "hilite");
	}
	var mon = Calendar.findMonth(target);
	var date = null;
	if (mon) {
		date = new Date(cal.date);
		if (mon.month != date.getMonth()) {
			if (cal.activeMonEl) {
				Calendar.removeClass(cal.activeMonEl, "hilite");
			}
			Calendar.addClass(mon, "hilite");
			cal.activeMonEl = mon;
		}
	} else {
		var year = Calendar.findYear(target);
		if (year) {
			date = new Date(cal.date);
			if (year.year != date.getFullYear()) {
				if (cal.activeYearEl) {
					Calendar.removeClass(cal.activeYearEl, "hilite");
				}
				Calendar.addClass(year, "hilite");
				cal.activeYearEl = year;
			}
		}
	}
	Calendar.stopEvent(ev);
};

Calendar.tableMouseDown = function (ev) {
	if (Calendar.getTargetElement(ev) == Calendar.getElement(ev)) {
		Calendar.stopEvent(ev);
	}
};

Calendar.showMonthsCombo = function () {
	var cal = Calendar._C;
	if (!cal) {
		return false;
	}
	var cal = cal;
	var cd = cal.activeDateEl;
	var mc = cal.monthsCombo;
	if (cal.activeMonEl) {
		Calendar.removeClass(cal.activeMonEl, "hilite");
	}
	var mon = cal.monthsCombo.getElementsByTagName("div")[cal.date.getMonth()];
	Calendar.addClass(mon, "hilite");
	cal.activeMonEl = mon;
	mc.style.left = cd.offsetLeft;
	mc.style.top = cd.offsetTop + cd.offsetHeight;
	mc.style.display = "block";
};

Calendar.showYearsCombo = function (fwd) {
	var cal = Calendar._C;
	if (!cal) {
		return false;
	}
	var cal = cal;
	var cd = cal.activeDateEl;
	var yc = cal.yearsCombo;
	if (cal.activeYearEl) {
		Calendar.removeClass(cal.activeYearEl, "hilite");
	}
	cal.activeYearEl = null;
	var Y = cal.date.getFullYear() + (fwd ? 1 : -1);
	var yr = yc.firstChild;
	var show = false;
	for (var i = 0; i < 12; ++i) {
		if (Y >= cal.minYear && Y <= cal.maxYear) {
			yr.firstChild.data = Y;
			yr.year = Y;
			yr.style.display = "block";
			show = true;
		} else {
			yr.style.display = "none";
		}
		yr = yr.nextSibling;
		Y += fwd ? 2 : -2;
	}
	if (show) {
		yc.style.left = cd.offsetLeft;
		yc.style.top = cd.offsetTop + cd.offsetHeight;
		yc.style.display = "block";
	}
};

Calendar.calDragIt = function (ev) {
	var cal = Calendar._C;
	if (!cal.dragging) {
		return false;
	}
	var posX;
	var posY;
	if (window.event) {
		posY = window.event.clientY + document.body.scrollTop;
		posX = window.event.clientX + document.body.scrollLeft;
	} else {
		posX = ev.pageX;
		posY = ev.pageY;
	}
	cal.hideShowCovered();
	var st = cal.element.style;
	st.left = (posX - cal.xOffs) + "px";
	st.top = (posY - cal.yOffs) + "px";
	Calendar.stopEvent(ev);
};

Calendar.calDragEnd = function (ev) {
	var cal = Calendar._C;
	cal.dragging = false;
	Calendar.removeEvent(document, "mousemove", Calendar.calDragIt);
	Calendar.removeEvent(document, "mouseover", Calendar.stopEvent);
	Calendar.removeEvent(document, "mouseup", Calendar.calDragEnd);
	Calendar.tableMouseUp(ev);
	cal.hideShowCovered();
};

Calendar.dayMouseDown = function(ev) {
	var el = Calendar.getElement(ev);
	var cal = el.calendar;
	cal.activeDateEl = el;
	Calendar._C = cal;
	if (el.navtype != 300) {
		Calendar.addClass(el, "hilite active");
		Calendar.addEvent(document, "mouseover", Calendar.tableMouseOver);
		Calendar.addEvent(document, "mousemove", Calendar.tableMouseOver);
		Calendar.addEvent(document, "mouseup", Calendar.tableMouseUp);
	} else if (cal.isPopup) {
		cal.dragStart(ev);
	}
	Calendar.stopEvent(ev);
	if (el.navtype == -1 || el.navtype == 1) {
		cal.timeout = setTimeout("Calendar.showMonthsCombo()", 250);
	} else if (el.navtype == -2 || el.navtype == 2) {
		cal.timeout = setTimeout((el.navtype > 0) ? "Calendar.showYearsCombo(true)" : "Calendar.showYearsCombo(false)", 250);
	} else {
		cal.timeout = null;
	}
};

Calendar.dayMouseDblClick = function(ev) {
	Calendar.cellClick(Calendar.getElement(ev));
	if (Calendar.is_ie) {
		document.selection.empty();
	}
};

Calendar.dayMouseOver = function(ev) {
	var el = Calendar.getElement(ev);
	if (Calendar.isRelated(el, ev) || Calendar._C) {
		return false;
	}
	if (el.ttip) {
		el.calendar.tooltips.firstChild.data = el.ttip;
	}
	if (el.navtype != 300) {
		Calendar.addClass(el, "hilite");
	}
	Calendar.stopEvent(ev);
};

Calendar.dayMouseOut = function(ev) {
	var el = Calendar.getElement(ev);
	if (Calendar.isRelated(el, ev) || Calendar._C) {
		return false;
	}
	Calendar.removeClass(el, "hilite");
	el.calendar.tooltips.firstChild.data = Calendar._TT["SEL_DATE"];
	Calendar.stopEvent(ev);
};

Calendar.datesEqual = function(a, b) {
	return ((a.getFullYear() == b.getFullYear()) &&
		(a.getMonth() == b.getMonth()) &&
		(a.getDate() == b.getDate()));
};

Calendar.cellClick = function(el) {
	var cal = el.calendar;
	var closing = false;
	var newdate = false;
	var date = null;
	if (typeof el.navtype == 'undefined') {
		Calendar.removeClass(cal.currentDateEl, "selected");
		Calendar.addClass(el, "selected");
		closing = cal.currentDateEl == el;
		if (!closing) {
			cal.currentDateEl = el;
		}
		date = el.caldate;
		cal.date = date;
		newdate = true;
	} else {
		if (el.navtype == 200) {
			Calendar.removeClass(el, "hilite");
			cal.callCloseHandler();
			return;
		}
		date = (el.navtype == 0) ? new Date() : new Date(cal.date);
		var year = date.getFullYear();
		var mon = date.getMonth();
		var setMonth = function (mon) {
			var day = date.getDate();
			var max = Calendar.getMonthDays(year, mon);
			if (day > max) {
				date.setDate(max);
			}
			date.setMonth(mon);
		};
		switch (el.navtype) {
		    case -2:
			if (year > cal.minYear) {
				date.setFullYear(year - 1);
			}
			break;
		    case -1:
			if (mon > 0) {
				setMonth(mon - 1);
			} else if (year-- > cal.minYear) {
				date.setFullYear(year);
				setMonth(11);
			}
			break;
		    case 1:
			if (mon < 11) {
				setMonth(mon + 1);
			} else if (year < cal.maxYear) {
				date.setFullYear(year + 1);
				setMonth(0);
			}
			break;
		    case 2:
			if (year < cal.maxYear) {
				date.setFullYear(year + 1);
			}
			break;
		    case 100:
			cal.setMondayFirst(!cal.mondayFirst);
			return;
		}
		if (!Calendar.datesEqual(date, cal.date)) {
			cal.setDate(date);
			newdate = el.navtype == 0;
		}
	}
	if (newdate) {
		cal.callHandler();
	}
	if (closing) {
		Calendar.removeClass(el, "hilite");
		cal.callCloseHandler();
	}
};

Calendar.prototype.create = function (_par) {
	var parent = null;
	if (! _par) {
		// default parent is the document body, in which case we create
		// a popup calendar.
		parent = document.getElementsByTagName("body")[0];
		this.isPopup = true;
	} else {
		parent = _par;
		this.isPopup = false;
	}
	this.date = this.dateStr ? new Date(this.dateStr) : new Date();

	var table = document.createElement("table");
	this.table = table;
	table.cellSpacing = 0;
	table.cellPadding = 0;
	table.calendar = this;
	Calendar.addEvent(table, "mousedown", Calendar.tableMouseDown);

	var div = document.createElement("div");
	this.element = div;
	div.className = "calendar";
	if (this.isPopup) {
		div.style.position = "absolute";
		div.style.display = "none";
	}
	div.appendChild(table);

	var thead = document.createElement("thead");
	table.appendChild(thead);
	var cell = null;
	var row = null;

	var cal = this;
	var hh = function (text, cs, navtype) {
		cell = document.createElement("td");
		row.appendChild(cell);
		cell.colSpan = cs;
		cell.className = "button";
		Calendar._add_evs(cell);
		cell.calendar = cal;
		cell.navtype = navtype;
		if (text.substr(0, 1) != '&') {
			cell.appendChild(document.createTextNode(text));
		}
		else {
			// FIXME: dirty hack for entities
			cell.innerHTML = text;
		}
		return cell;
	};

	row = document.createElement("tr");
	thead.appendChild(row);
	row.className = "headrow";

	hh("-", 1, 100).ttip = Calendar._TT["TOGGLE"];
	this.title = hh("", this.isPopup ? 5 : 6, 300);
	this.title.className = "title";
	if (this.isPopup) {
		this.title.ttip = Calendar._TT["DRAG_TO_MOVE"];
		this.title.style.cursor = "move";
		hh("X", 1, 200).ttip = Calendar._TT["CLOSE"];
	}

	row = document.createElement("tr");
	thead.appendChild(row);
	row.className = "headrow";

	hh("&#x00ab;", 1, -2).ttip = Calendar._TT["PREV_YEAR"];
	hh("&#x2039;", 1, -1).ttip = Calendar._TT["PREV_MONTH"];
	hh(Calendar._TT["TODAY"], 3, 0).ttip = Calendar._TT["GO_TODAY"];
	hh("&#x203a;", 1, 1).ttip = Calendar._TT["NEXT_MONTH"];
	hh("&#x00bb;", 1, 2).ttip = Calendar._TT["NEXT_YEAR"];

	// day names
	row = document.createElement("tr");
	thead.appendChild(row);
	row.className = "daynames";
	this.daynames = row;
	for (var i = 0; i < 7; ++i) {
		cell = document.createElement("td");
		row.appendChild(cell);
		cell.appendChild(document.createTextNode(""));
		if (!i) {
			cell.navtype = 100;
			cell.calendar = this;
			Calendar._add_evs(cell);
		}
	}
	this.displayWeekdays();

	var tbody = document.createElement("tbody");
	table.appendChild(tbody);

	for (i = 0; i < 6; ++i) {
		row = document.createElement("tr");
		tbody.appendChild(row);
		for (var j = 0; j < 7; ++j) {
			cell = document.createElement("td");
			row.appendChild(cell);
			cell.appendChild(document.createTextNode(""));
			cell.calendar = this;
		}
	}

	var tfoot = document.createElement("tfoot");
	table.appendChild(tfoot);

	row = document.createElement("tr");
	tfoot.appendChild(row);
	row.className = "footrow";

	cell = hh(Calendar._TT["SEL_DATE"], 7, 300);
	cell.className = "ttip";
	if (this.isPopup) {
		cell.ttip = Calendar._TT["DRAG_TO_MOVE"];
		cell.style.cursor = "move";
	}
	this.tooltips = cell;

	div = document.createElement("div");
	this.monthsCombo = div;
	div.className = "combo";
	for (i = 0; i < Calendar._MN.length; ++i) {
		var mn = document.createElement("div");
		mn.className = "label";
		mn.month = i;
		mn.appendChild(document.createTextNode(Calendar._MN[i].substr(0, 3)));
		div.appendChild(mn);
	}
	this.element.appendChild(div);

	div = document.createElement("div");
	this.yearsCombo = div;
	div.className = "combo";
	for (i = 0; i < 12; ++i) {
		var yr = document.createElement("div");
		yr.className = "label";
		yr.appendChild(document.createTextNode(""));
		div.appendChild(yr);
	}
	this.element.appendChild(div);

	this._init(this.mondayFirst, this.date);
	parent.appendChild(this.element);
}

Calendar.prototype._init = function (mondayFirst, date) {
	var today = new Date();
	var year = date.getFullYear();
	if (year < this.minYear) {
		year = this.minYear;
	} else if (year > this.maxYear) {
		year = this.maxYear;
	}
	date.setFullYear(year);
	this.mondayFirst = mondayFirst;
	this.date = new Date(date);
	var month = date.getMonth();
	var mday = date.getDate();
	var no_days = Calendar.getMonthDays(year, month);
	date.setDate(1);
	var wday = date.getDay();
	var MON = mondayFirst ? 1 : 0;
	var SAT = mondayFirst ? 5 : 6;
	var SUN = mondayFirst ? 6 : 0;
	if (mondayFirst) {
		wday = (wday > 0) ? (wday - 1) : 6;
	}
	var iday = 1;
	var row = this.table.getElementsByTagName("tbody")[0].firstChild;
	for (var i = 0; i < 6; ++i) {
		var cell = row.firstChild;
		if (iday > no_days) {
			row.className = "emptyrow"; continue;
		} else {
			row.className = "daysrow";
			for (var j = 0; j < 7; ++j) {
				if ((!i && j < wday) || iday > no_days) {
					cell.className = "emptycell";
				} else {
					cell.firstChild.data = iday;
					cell.className = "day";
					date.setDate(iday);
					if (this.disabledHandler && this.disabledHandler(date)) {
						Calendar.addClass(cell, "disabled");
						Calendar._del_evs(cell);
					} else {
						cell.caldate = new Date(date);
						cell.ttip =
							Calendar._DN[wday + MON].substr(0, 3) +
							", " +
							Calendar._MN[month].substr(0, 3) +
							" " + iday + ", " + year;
						Calendar._add_evs(cell);
						if (iday == mday) {
							Calendar.addClass(cell, "selected");
							this.currentDateEl = cell;
						}
						if (Calendar.datesEqual(date, today)) {
							Calendar.addClass(cell, "today");
							cell.ttip += Calendar._TT["PART_TODAY"];
						}
						if (wday == SAT || wday == SUN) {
							Calendar.addClass(cell, "weekend");
						}
					}
					++wday; ++iday;
					if (wday == 7) {
						wday = 0;
					}
				}
				cell = cell.nextSibling;
			}
		}
		row = row.nextSibling;
	}
	this.title.firstChild.data = Calendar._MN[month] + ", " + year;
};

Calendar.prototype.setDate = function (date) {
	if (!Calendar.datesEqual(date, this.date)) {
		this._init(this.mondayFirst, date);
	}
};

Calendar.prototype.setMondayFirst = function (mondayFirst) {
	this._init(mondayFirst, this.date);
	this.displayWeekdays();
};

Calendar.prototype.displayWeekdays = function () {
	var MON = this.mondayFirst ? 0 : 1;
	var SUN = this.mondayFirst ? 6 : 0;
	var SAT = this.mondayFirst ? 5 : 6;
	var cell = this.daynames.firstChild;
	for (var i = 0; i < 7; ++i) {
		cell.className = "day name";
		if (!i) {
			if (this.mondayFirst) {
				cell.ttip = Calendar._TT["SUN_FIRST"];
			} else {
				cell.ttip = Calendar._TT["MON_FIRST"];
			}
		}
		if (i == SUN || i == SAT) {
			Calendar.addClass(cell, "weekend");
		}
		cell.firstChild.data = Calendar._DN[i + 1 - MON].substr(0,2);
		cell = cell.nextSibling;
	}
};

Calendar.prototype.setDisabledHandler = function (unaryFunction) {
	this.disabledHandler = unaryFunction;
};

Calendar.prototype.setRange = function (a, z) {
	this.minYear = a;
	this.maxYear = z;
};

Calendar.prototype.callHandler = function () {
	if (this.yourHandler) {
		this.yourHandler(this, this.formatDate(),this.date);
	}
};

Calendar.prototype.callCloseHandler = function () {
	if (this.yourCloseHandler) {
		this.yourCloseHandler(this);
	}
	this.hideShowCovered(true);
};

Calendar.prototype.destroy = function () {
	var el = this.element.parentNode;
	el.removeChild(this.element);
	Calendar._C = null;
	delete el;
};

Calendar.prototype.reparent = function (new_parent) {
	var el = this.element;
	el.parentNode.removeChild(el);
	new_parent.appendChild(el);
};

Calendar.prototype.show = function () {
	this.element.style.display = "block";
	this.hideShowCovered();
};

Calendar.prototype.hide = function () {
	this.element.style.display = "none";
};

Calendar.prototype.showAt = function (x, y) {
	var s = this.element.style;
	s.left = x + "px";
	s.top = y + "px";
	this.show();
};

Calendar.prototype.showAtElement = function (el) {
	var p = Calendar.getAbsolutePos(el);
	this.showAt(p.x, p.y + el.offsetHeight);
};

Calendar.prototype.hideCombos = function () {
	this.monthsCombo.style.display = "none";
	this.yearsCombo.style.display = "none";
};

Calendar.prototype.dragStart = function (ev) {
	if (this.dragging) {
		return;
	}
	this.dragging = true;
	var posX;
	var posY;
	if (window.event) {
		posY = window.event.clientY + document.body.scrollTop;
		posX = window.event.clientX + document.body.scrollLeft;
	} else {
		posY = ev.clientY + window.scrollY;
		posX = ev.clientX + window.scrollX;
	}
	var st = this.element.style;
	this.xOffs = posX - parseInt(st.left);
	this.yOffs = posY - parseInt(st.top);
	Calendar.addEvent(document, "mousemove", Calendar.calDragIt);
	Calendar.addEvent(document, "mouseover", Calendar.stopEvent);
	Calendar.addEvent(document, "mouseup", Calendar.calDragEnd);
};

Calendar.prototype.setDateFormat = function (str) {
	this.dateFormat = str;
};

Calendar.prototype.formatDateNow = function(date,dateFormat){
	var str = new String(dateFormat);
	var m = date.getMonth();
	var d = date.getDate();
	var y = date.getFullYear();
	var w = date.getDay();
	var s = new Array;
	s['d'] = d;
	s['dd'] = (d < 10) ? ('0' + d) : d;
	s['M'] = 1+m;
	s['MM'] = (m < 9) ? ('0' + (1+m)) : (1+m);
	s['m'] = 1+m;
	s['mm'] = (m < 9) ? ('0' + (1+m)) : (1+m);
	s['y'] = y;
	s['yyyy'] = y;
	s['yy'] = new String(y).substr(2, 2);
	s['D'] = Calendar._DN[w].substr(0, 3);
	s['DD'] = Calendar._DN[w];
	s['MMM'] = Calendar._MN[m].substr(0, 3);
	s['MMMM'] = Calendar._MN[m];
	var re = /(.*)(\W|^)(d|dd|m|mm|M|MM|y|yyyy|yy|MMM|MMMM|DD|D)(\W|$)(.*)/;
	while (re.exec(str) != null) {
		str = RegExp.$1 + RegExp.$2 + s[RegExp.$3] + RegExp.$4 + RegExp.$5;
	}
	return str;
};

Calendar.prototype.formatDate = function(){
	return this.formatDateNow(this.date,this.dateFormat);
};


Calendar.prototype.parseDate = function (str, fmt) {
	var y = 0;
	var m = -1;
	var d = 0;
	var a = str.split(/\W+/);
	if (!fmt) {
		fmt = this.dateFormat;
	}
	var b = fmt.split(/\W+/);
	var i = 0, j = 0;
	for (i = 0; i < a.length; ++i) {
		if (b[i] == 'D' || b[i] == 'DD') {
			continue;
		}
		if (b[i] == 'd' || b[i] == 'dd') {
			d = a[i];
		}
		if (b[i] == 'm' || b[i] == 'mm') {
			m = a[i]-1;
		}
		if (b[i] == 'y') {
			y = a[i];
		}
		if (b[i] == 'yy') {
			y = parseInt(a[i]) + 1900;
		}
		if (b[i] == 'M' || b[i] == 'MM') {
			for (j = 0; j < 12; ++j) {
				if (Calendar._MN[j].substr(0, a[i].length).toLowerCase() == a[i].toLowerCase()) { m = j; break; }
			}
		}
	}
	if (y != 0 && m != -1 && d != 0) {
		this.setDate(new Date(y, m, d));
		return;
	}
	y = 0; m = -1; d = 0;
	for (i = 0; i < a.length; ++i) {
		if (a[i].search(/[a-zA-Z]+/) != -1) {
			var t = -1;
			for (j = 0; j < 12; ++j) {
				if (Calendar._MN[j].substr(0, a[i].length).toLowerCase() == a[i].toLowerCase()) { t = j; break; }
			}
			if (t != -1) {
				if (m != -1) {
					d = m+1;
				}
				m = t;
			}
		} else if (parseInt(a[i]) <= 12 && m == -1) {
			m = a[i]-1;
		} else if (parseInt(a[i]) > 31 && y == 0) {
			y = a[i];
		} else if (d == 0) {
			d = a[i];
		}
	}
	if (y == 0) {
		var today = new Date();
		y = today.getFullYear();
	}
	if (m != -1 && d != 0) {
		this.setDate(new Date(y, m, d));
	}
};

Calendar.prototype.hideShowCovered = function (closingCalendar) {
	var tags = new Array ('applet', 'iframe', 'select');
	var el = this.element;

	var p = Calendar.getAbsolutePos(el);
	var EX1 = p.x;
	var EX2 = el.offsetWidth + EX1;
	var EY1 = p.y;
	var EY2 = el.offsetHeight + EY1;

	for (var k = 0; k < tags.length; k++) {
		var ar = document.getElementsByTagName(tags[k]);
		var cc = null;

		for (var i = 0; i < ar.length; i++) {
			cc = ar[i];

			p = Calendar.getAbsolutePos(cc);
			var CX1 = p.x;
			var CX2 = cc.offsetWidth + CX1;
			var CY1 = p.y;
			var CY2 = cc.offsetHeight + CY1;

			if (closingCalendar || (CX1 > EX2) || (CX2 < EX1) || (CY1 > EY2) || (CY2 < EY1)) {
				cc.style.visibility = "visible";
			} else {
				cc.style.visibility = "hidden";
			}
		}
	}
};
