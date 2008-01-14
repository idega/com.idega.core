package com.idega.presentation.ui;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.el.ValueBinding;

import org.apache.myfaces.component.html.util.HtmlComponentUtils;

import com.idega.presentation.IWBaseComponent;

/**
 * 
 * @author <a href="civilis@idega.com">Vytautas Čivilis</a>
 * @version $Revision: 1.1.2.3 $
 * 
 * Last modified: $Date: 2008/01/14 14:08:01 $ by $Author: laddi $
 * 
 */
public class UIDateInput extends IWBaseComponent {

	public static final String COMPONENT_TYPE = "idega_DateInput";

	private static final String uidateFacet = "uidate";
	private static final String dateInputName = "idg_di_name";
	private static final String idAtt = "id";
	private Integer fromYear;
	private Integer toYear;
	private String[] dateElementsIdentifiers;
	private Boolean showDay;

	protected void initializeComponent(FacesContext context) {
		super.initializeComponent(context);

		getFacets().put(uidateFacet, createDateInput());
	}

	public boolean isRendered() {
		return true;
	}

	public void encodeChildren(FacesContext context) throws IOException {
		super.encodeChildren(context);

		DateInput dateInput = (DateInput) getFacet(uidateFacet);

		if (dateInput == null) {
			dateInput = createDateInput();
			getFacets().put(uidateFacet, dateInput);
		}

		ValueBinding vb = getValueBinding(valueAtt);

		if (vb != null) {

			if (isShowDay()) {
				dateElementsIdentifiers = new String[] { dateInput.getNameForYear(), dateInput.getNameForMonth(), dateInput.getNameForDay() };
			}
			else {
				dateElementsIdentifiers = new String[] { dateInput.getNameForYear(), dateInput.getNameForMonth() };
			}

			Date date = (Date) vb.getValue(context);

			if (date == null) {
				date = new Date();
				vb.setValue(context, date);
			}

			dateInput.setDate(date);
		}

		renderChild(context, dateInput);
	}

	private DateInput createDateInput() {

		DateInput dateInput = new DateInput();
		dateInput.setName(dateInputName);
		dateInput.setToShowDay(isShowDay());

		if (fromYear != null && toYear != null) {
			dateInput.setYearRange(fromYear.intValue(), toYear.intValue());
		}

		return dateInput;
	}

	public void decode(FacesContext context) {

		super.decode(context);

		ValueBinding vb = getValueBinding(valueAtt);

		if (vb != null) {

			Map requestParameters = context.getExternalContext().getRequestParameterMap();

			int i = 0;
			String yearParam = (String) requestParameters.get(dateElementsIdentifiers[i++]);
			String monthParam = (String) requestParameters.get(dateElementsIdentifiers[i++]);
			String dayParam = dateElementsIdentifiers.length == 3 ? (String) requestParameters.get(dateElementsIdentifiers[i++]) : null;

			if (yearParam != null && monthParam != null) {

				Date date = (Date) vb.getValue(context);

				if (date == null) {
					date = new Date();
					vb.setValue(context, date);
				}

				Calendar cal = Calendar.getInstance();
				cal.set(new Integer(yearParam).intValue(), new Integer(monthParam).intValue() - 1, dayParam == null ? 1 : new Integer(dayParam).intValue());

				date.setTime(cal.getTimeInMillis());
			}
		}
	}

	/**
	 * @Override
	 */
	public void encodeBegin(FacesContext context) throws IOException {
		super.encodeBegin(context);

		ResponseWriter writer = context.getResponseWriter();
		writer.startElement(spanTag, this);
		writer.writeAttribute(idAtt, getClientId(context), null);
	}

	/**
	 * @Override
	 */
	public void encodeEnd(FacesContext context) throws IOException {
		super.encodeEnd(context);

		ResponseWriter writer = context.getResponseWriter();
		writer.endElement(spanTag);
		writer.flush();
	}

	/**
	 * @Override
	 */
	public String getClientId(FacesContext context) {
		String clientId = HtmlComponentUtils.getClientId(this, getRenderer(context), context);

		if (clientId == null) {
			clientId = super.getClientId(context);
		}

		return clientId;
	}

	/**
	 * @Override
	 */
	public boolean getRendersChildren() {
		return true;
	}

	/**
	 * @Override
	 */
	public Object saveState(FacesContext context) {

		Object values[] = new Object[5];
		values[0] = super.saveState(context);
		values[1] = dateElementsIdentifiers;
		values[2] = fromYear;
		values[3] = toYear;
		values[4] = showDay;

		return values;
	}

	/**
	 * @Override
	 */
	public void restoreState(FacesContext context, Object state) {
		Object values[] = (Object[]) state;
		super.restoreState(context, values[0]);
		dateElementsIdentifiers = (String[]) values[1];
		fromYear = (Integer) values[2];
		toYear = (Integer) values[3];
		showDay = (Boolean) values[4];
	}

	public void setYearRange(int fromYear, int toYear) {
		this.fromYear = new Integer(fromYear);
		this.toYear = new Integer(toYear);
	}

	public void setShowDay(boolean showDay) {
		this.showDay = new Boolean(showDay);
	}

	public boolean isShowDay() {
		//		show day by default
		return showDay == null || showDay.booleanValue();
	}
}