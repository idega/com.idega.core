// Calendar Helper Routines
// From: http://students.infoiasi.ro/~mishoo/site/calendar.epl#gen4

var calendar = null; // remember the calendar object so that we reuse it and
                     // avoid creation other calendars.

// This function gets called when the end-user clicks on some date.
function selected(cal, formattedDate,date) {
  cal.sel.value = formattedDate; // just update the date in the input field.
  cal.hid.value = cal.formatDateNow(date,convertJavaDateFormat('yyyy-MM-dd'));
  closeHandler(calendar);
}

// And this gets called when the end-user clicks on the _selected_ date,
// or clicks on the "Close" button.  It just hides the calendar without
// destroying it.
function closeHandler(cal) {
  cal.hide();                        // hide the calendar
  cal.hideShowCovered(true);             // show again controls that disappeared behind the calendar 
  cal.sel.date = new Date(cal.date); // remember the date in the input field

  // don't check mousedown on document anymore (used to be able to hide the
  // calendar when someone clicks outside it, see the showCalendar function).
  Calendar.removeEvent(document, "mousedown", checkCalendar);
}

// This gets called when the user presses a mouse button anywhere in the
// document, if the calendar is shown.  If the click was outside the open
// calendar this function closes it.
function checkCalendar(ev) {
  var el = Calendar.is_ie ? Calendar.getElement(ev) : Calendar.getTargetElement(ev);
  for (; el != null; el = el.parentNode)
    // FIXME: allow end-user to click some link without closing the
    // calendar.  Good to see real-time stylesheet change :)
    if (el == calendar.element || el.tagName == "A") break;
  if (el == null) {
    // calls closeHandler which should hide the calendar.
    calendar.callCloseHandler();
  }
}

// This function shows the calendar under the element having the given id.
// It takes care of catching "mousedown" signals on document and hiding the
// calendar if the click was outside.
function showCalendar(id, dateFormat,hiddenID) {
  var el = document.getElementById(id);
  var hidEl = document.getElementById(hiddenID);
  if (calendar != null)
    // we already have some calendar created
    calendar.hide();                 // so we hide it first.
  else {
    // first-time call, create the calendar.
    var cal = new Calendar(true, null, selected, closeHandler);
    calendar = cal;                  // remember it in the global var
    //cal.setRange(2000, 2070);        // min/max year allowed.
    cal.mondayFirst = false;
    cal.create();                    // create popup calendar.
  }
  calendar.sel = el;                 // inform it what input field we use
  calendar.hid = hidEl;
  calendar.setDateFormat(convertJavaDateFormat(dateFormat));
  if(hidEl.value)
    calendar.parseDate(hidEl.value,'yyyy-mm-dd');      // set the date for the new field.
  calendar.showAtElement(el);        // show the calendar below it


  

  // catch "mousedown" on document
  Calendar.addEvent(document, "mousedown", checkCalendar);
  return false;
}

function convertJavaDateFormat(dateFormat) {
  //dateFormat = replaceAll(dateFormat, "MMMM", "MM");
  //dateFormat = replaceAll(dateFormat, "MMM", "mm");
  //dateFormat = replaceAll(dateFormat, "MM", "m");
  //dateFormat = replaceAll(dateFormat, "yyyy", "y");
  return dateFormat;
}

// http://www.rpd.univ.kiev.ua/~mav/tdoc/jcbook/DOSFILES/STRINGS/SUBST.HTM
// Substitute character string c2 for every c1 in aString
function replaceAll(aString, c1, c2)
{
	if (aString == "") return aString
	if (c1 == "") return aString
	
	// avoid infinite recursion when substituting aa for a by
	// providing an offset into the string.
	var argc = replaceAll.arguments.length
	if (argc < 4) {n = 0} else {n = replaceAll.arguments[3]}

	// find the first occurrence of c1 after the threshold
	var i = aString.indexOf(c1, n)
	
	// stop recursion and return the current string when c1 not found
	if (i < 0) return aString
	
	// extract substrings s1 and s2 around the c1
	var s1 = aString.substring(0, i)
	var s2 = aString.substring(i+c1.length, aString.length)
	
	// recurse with this new string
	return replaceAll(s1+c2+s2, c1, c2, (i+c2.length))
}

