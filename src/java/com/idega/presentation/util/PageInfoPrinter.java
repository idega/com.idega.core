package com.idega.presentation.util;

import com.idega.presentation.Block;
import com.idega.presentation.IWContext;

public class PageInfoPrinter extends Block {

	public void main(IWContext iwc) {
		System.out.println("[PageInfoWriter] ================================================== ");
		System.out.println("[PageInfoWriter] URI        = "+iwc.getRequestURI());
		System.out.println("[PageInfoWriter] IB_PAGE_ID = "+iwc.getCurrentIBPageID());
		System.out.println("[PageInfoWriter] REFERER    = "+iwc.getReferer());
		System.out.println("[PageInfoWriter] ================================================== ");
	}
}
