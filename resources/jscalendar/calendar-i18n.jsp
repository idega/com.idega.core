<%@ page contentType = "text/html;charset=UTF-8"%>
<%@ taglib uri="struts-bean" prefix="bean" %>

// ** I18N
Calendar._DN = new Array
("<bean:message key="calendar.sunday"/>",
 "<bean:message key="calendar.monday"/>",
 "<bean:message key="calendar.tuesday"/>",
 "<bean:message key="calendar.wednesday"/>",
 "<bean:message key="calendar.thursday"/>",
 "<bean:message key="calendar.friday"/>",
 "<bean:message key="calendar.saturday"/>",
 "<bean:message key="calendar.sunday"/>");

Calendar._MN = new Array
("<bean:message key="calendar.january"/>",
 "<bean:message key="calendar.february"/>",
 "<bean:message key="calendar.march"/>",
 "<bean:message key="calendar.april"/>",
 "<bean:message key="calendar.may"/>",
 "<bean:message key="calendar.june"/>",
 "<bean:message key="calendar.july"/>",
 "<bean:message key="calendar.august"/>",
 "<bean:message key="calendar.september"/>",
 "<bean:message key="calendar.october"/>",
 "<bean:message key="calendar.november"/>",
 "<bean:message key="calendar.december"/>");

Calendar._TT = {};
Calendar._TT["TOGGLE"] = "<bean:message key="calendar.label.toggle"/>";
Calendar._TT["PREV_YEAR"] = "<bean:message key="calendar.label.prev_year"/>";
Calendar._TT["PREV_MONTH"] = "<bean:message key="calendar.label.prev_month"/>";
Calendar._TT["GO_TODAY"] = "<bean:message key="calendar.label.go_today"/>";
Calendar._TT["NEXT_MONTH"] = "<bean:message key="calendar.label.next_month"/>";
Calendar._TT["NEXT_YEAR"] = "<bean:message key="calendar.label.next_year"/>";
Calendar._TT["SEL_DATE"] = "<bean:message key="calendar.label.sel_date"/>";
Calendar._TT["DRAG_TO_MOVE"] = "<bean:message key="calendar.label.drag_to_move"/>";
Calendar._TT["PART_TODAY"] = "<bean:message key="calendar.label.part_today"/>";
Calendar._TT["MON_FIRST"] = "<bean:message key="calendar.label.mon_first"/>";
Calendar._TT["SUN_FIRST"] = "<bean:message key="calendar.label.sun_first"/>";
Calendar._TT["CLOSE"] = "<bean:message key="calendar.label.close"/>";
Calendar._TT["TODAY"] = "<bean:message key="calendar.label.today"/>";
