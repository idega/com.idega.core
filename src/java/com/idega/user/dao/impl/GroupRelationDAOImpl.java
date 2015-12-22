package com.idega.user.dao.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.idega.core.persistence.Param;
import com.idega.core.persistence.impl.GenericDaoImpl;
import com.idega.user.dao.GroupRelationDAO;
import com.idega.user.data.bean.GroupRelation;
import com.idega.util.ArrayUtil;

@Scope(BeanDefinition.SCOPE_SINGLETON)
@Repository(GroupRelationDAO.BEAN_NAME)
@Transactional(readOnly = true)
public class GroupRelationDAOImpl extends GenericDaoImpl implements GroupRelationDAO {

	@Override
	public Long getCountByRelatedGroupType(String relatedGroupType) {
		try {
			return getSingleResult(GroupRelation.QUERY_COUNT_BY_RELATED_GROUP_TYPE, Long.class, new Param(GroupRelation.PARAM_RELATED_GROUP_TYPE, relatedGroupType));
		} catch (Exception e) {
			getLogger().log(Level.WARNING, "Error getting count of group relations by related group type.", e);
		}

		return null;
	}

	@Override
	public List<GroupRelation> getGroupRelationsByRelatedGroupTypeOrderedByDate(String relatedGroupType, Integer firstResult, Integer maxResults) {
		StringBuilder query = null;
		try {
			List<Param> params = new ArrayList<>();
			params.add(new Param(GroupRelation.PARAM_RELATED_GROUP_TYPE, relatedGroupType));

			query = new StringBuilder("select r from GroupRelation r where r.relatedGroupType.groupType = :" + GroupRelation.PARAM_RELATED_GROUP_TYPE);
			query.append(" order by r.terminationDate, r.groupRelationID desc ");
//			query.append("CASE ");
//			query.append("WHEN r.initiationModificationDate >= r.terminationDate AND r.initiationModificationDate >= r.initiationDate ");
//			query.append("THEN r.initiationModificationDate ");
//			query.append("WHEN r.terminationDate >= r.initiationDate ");
//			query.append("THEN r.terminationDate ");
//			query.append("ELSE r.initiationDate ");
//			query.append("END ");
//			query.append("desc ");

			List<GroupRelation> results = getResultListByInlineQuery(query.toString(), GroupRelation.class, firstResult, maxResults, "groupRelationsByRelatedGroupTypeOrderedByDate", ArrayUtil.convertListToArray(params));
			return results;
		} catch (Exception e) {
			getLogger().log(Level.WARNING, "Error getting group relations: ", e);
		}
		return null;
	}


	@Override
	public List<GroupRelation> getGroupRelationsByRelatedGroupTypeAndRelatedGroupIdsAndDate(String relatedGroupType, List<Integer> relatedGroupIds, Date dateFrom, Date dateTo) {
		StringBuilder query = null;
		try {
			List<Param> params = new ArrayList<Param>();
			params.add(new Param(GroupRelation.PARAM_RELATED_GROUP_TYPE, relatedGroupType));
			params.add(new Param(GroupRelation.PARAM_RELATED_GROUP_IDS, relatedGroupIds));
			if (dateFrom != null && dateTo != null) {
				params.add(new Param(GroupRelation.PARAM_DATE_FROM, dateFrom));
				params.add(new Param(GroupRelation.PARAM_DATE_TO, dateTo));
			}

			query = new StringBuilder("SELECT r FROM GroupRelation r WHERE r.relatedGroupType.groupType = :" + GroupRelation.PARAM_RELATED_GROUP_TYPE);
			query.append(" AND r.group.groupID IN (:" + GroupRelation.PARAM_RELATED_GROUP_IDS + ") ");
			if (dateFrom != null && dateTo != null) {
				query.append(" AND (");
				query.append(" (r.terminationDate IS NOT NULL AND r.terminationDate >= :" + GroupRelation.PARAM_DATE_FROM + " AND r.terminationDate < :" + GroupRelation.PARAM_DATE_TO + ") ");
				query.append(" OR (r.terminationDate IS NULL AND r.initiationModificationDate IS NOT NULL AND r.initiationModificationDate >= :" + GroupRelation.PARAM_DATE_FROM + " AND r.initiationModificationDate < :" + GroupRelation.PARAM_DATE_TO + ") ");
				query.append(" OR (r.terminationDate IS NULL AND r.initiationModificationDate IS NULL AND r.initiationDate IS NOT NULL AND r.initiationDate >= :" + GroupRelation.PARAM_DATE_FROM + " AND r.initiationDate < :" + GroupRelation.PARAM_DATE_TO + ") ");
				query.append(") ");
			}

			List<GroupRelation> results = getResultListByInlineQuery(query.toString(), GroupRelation.class, ArrayUtil.convertListToArray(params));
			return results;
		} catch (Exception e) {
			getLogger().log(Level.WARNING, "Error getting group relations: ", e);
		}
		return null;
	}


	@Override
	public Integer getGroupRelationsCountByRelatedGroupTypeAndRelatedGroupIdsAndDate(String relatedGroupType, List<Integer> relatedGroupIds, Date dateFrom, Date dateTo) {
		try {
			List<GroupRelation> groupRelationList = getGroupRelationsByRelatedGroupTypeAndRelatedGroupIdsAndDate(relatedGroupType, relatedGroupIds, dateFrom, dateTo);
			if (groupRelationList != null) {
				return groupRelationList.size();
			}
		} catch (Exception e) {
			getLogger().log(Level.WARNING, "Error getting group relations count: ", e);
		}
		return 0;
	}


	@Override
	public List<Object[]> getGroupRelationsCountForPeriod(String relatedGroupType, List<Integer> relatedGroupIds, Date dateFrom, Date dateTo) {
		StringBuilder query = null;
		try {
			List<Param> params = new ArrayList<Param>();
			params.add(new Param(GroupRelation.PARAM_RELATED_GROUP_TYPE, relatedGroupType));
			params.add(new Param(GroupRelation.PARAM_RELATED_GROUP_IDS, relatedGroupIds));
			if (dateFrom != null && dateTo != null) {
				params.add(new Param(GroupRelation.PARAM_DATE_FROM, dateFrom));
				params.add(new Param(GroupRelation.PARAM_DATE_TO, dateTo));
			}

			query = new StringBuilder("SELECT DISTINCT DATE(CASE WHEN r.terminationDate IS NOT NULL AND r.terminationDate >= :" + GroupRelation.PARAM_DATE_FROM + " AND r.terminationDate < :" + GroupRelation.PARAM_DATE_TO + " THEN r.terminationDate WHEN r.terminationDate IS NULL AND r.initiationModificationDate IS NOT NULL AND r.initiationModificationDate >= :" + GroupRelation.PARAM_DATE_FROM + " AND r.initiationModificationDate < :" + GroupRelation.PARAM_DATE_TO + " THEN init_modification_date WHEN r.terminationDate IS NULL AND r.initiationModificationDate IS NULL AND r.initiationDate IS NOT NULL AND r.initiationDate >= :" + GroupRelation.PARAM_DATE_FROM + " AND r.initiationDate < :" + GroupRelation.PARAM_DATE_TO + " THEN initiation_date END) AS date, COUNT(r.groupRelationID) ");
			query.append(" FROM GroupRelation r");
			query.append(" WHERE r.relatedGroupType.groupType = :" + GroupRelation.PARAM_RELATED_GROUP_TYPE);
			query.append(" AND r.group.groupID IN (:" + GroupRelation.PARAM_RELATED_GROUP_IDS + ") ");
			if (dateFrom != null && dateTo != null) {
				query.append(" AND (");
				query.append(" (r.terminationDate IS NOT NULL AND r.terminationDate >= :" + GroupRelation.PARAM_DATE_FROM + " AND r.terminationDate < :" + GroupRelation.PARAM_DATE_TO + ") ");
				query.append(" OR (r.terminationDate IS NULL AND r.initiationModificationDate IS NOT NULL AND r.initiationModificationDate >= :" + GroupRelation.PARAM_DATE_FROM + " AND r.initiationModificationDate < :" + GroupRelation.PARAM_DATE_TO + ") ");
				query.append(" OR (r.terminationDate IS NULL AND r.initiationModificationDate IS NULL AND r.initiationDate IS NOT NULL AND r.initiationDate >= :" + GroupRelation.PARAM_DATE_FROM + " AND r.initiationDate < :" + GroupRelation.PARAM_DATE_TO + ") ");
				query.append(") ");
			}
			query.append(" GROUP BY 1 ");

			List<Object[]> results = getResultListByInlineQuery(query.toString(), Object[].class, ArrayUtil.convertListToArray(params));
			return results;
		} catch (Exception e) {
			getLogger().log(Level.WARNING, "Error getting group relations: ", e);
		}
		return null;
	}


}