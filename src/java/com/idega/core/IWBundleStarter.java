package com.idega.core;

import java.io.Serializable;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.idega.core.builder.dao.ICPageDAO;
import com.idega.core.builder.data.ICPageBMPBean;
import com.idega.data.SimpleQuerier;
import com.idega.idegaweb.IWBundle;
import com.idega.idegaweb.IWBundleStartable;
import com.idega.util.ArrayUtil;
import com.idega.util.ListUtil;
import com.idega.util.expression.ELUtil;

public class IWBundleStarter implements IWBundleStartable {

	@Autowired
	private ICPageDAO icPageDAO;

	private ICPageDAO getICPageDAO() {
		if (icPageDAO == null) {
			ELUtil.getInstance().autowire(this);
		}
		return icPageDAO;
	}

	@Override
	public void start(IWBundle starterBundle) {
		//	Temporary solution to cache
		try {
			String query = "select p." + ICPageBMPBean.ENTITY_NAME + "_ID from " + ICPageBMPBean.ENTITY_NAME + " p";
			List<Serializable[]> ids = SimpleQuerier.executeQuery(query, 1);
			if (ListUtil.isEmpty(ids)) {
				return;
			}

			ICPageDAO icPageDAO = getICPageDAO();
			for (Serializable[] data: ids) {
				if (ArrayUtil.isEmpty(data)) {
					continue;
				}

				Serializable id = data[0];
				if (id instanceof Number) {
					icPageDAO.isPagePublished(((Number) id).intValue());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void stop(IWBundle starterBundle) {
	}

}