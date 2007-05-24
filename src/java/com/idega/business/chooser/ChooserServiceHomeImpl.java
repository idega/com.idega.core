package com.idega.business.chooser;


import javax.ejb.CreateException;
import com.idega.business.IBOHomeImpl;

public class ChooserServiceHomeImpl extends IBOHomeImpl implements ChooserServiceHome {

	private static final long serialVersionUID = 1628924374771246076L;

	public Class getBeanInterfaceClass() {
		return ChooserService.class;
	}

	public ChooserService create() throws CreateException {
		return (ChooserService) super.createIBO();
	}
}