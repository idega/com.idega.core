/**
 *
 */
package com.idega.presentation.paging;

import java.util.Collection;

import com.idega.util.CoreConstants;
import com.idega.util.ListUtil;

/**
 * A wrapper class for paged data list and total count of records.
 *
 * @author donatas
 *
 */
public class PagedDataCollection<T> {

	private Collection<T> data;

	private Long totalCount;

	public PagedDataCollection(Collection<T> data, Long totalCount) {
		this.data = data;
		this.totalCount = totalCount;
	}

	public PagedDataCollection(Collection<T> data) {
		this.data = data;
		if (data != null) {
			this.totalCount = Long.valueOf(data.size());
		}
	}

	public Collection<T> getCollection() {
		return data;
	}

	public Long getTotalCount() {
		return totalCount;
	}

	public void setTotalCount(Long totalCount) {
		this.totalCount = totalCount;
	}

	public void setData(Collection<T> data) {
		this.data = data;
	}

	@Override
	public String toString() {
		return ListUtil.isEmpty(data) ? CoreConstants.MINUS : data.toString();
	}

}