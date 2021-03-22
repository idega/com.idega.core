package com.idega.user.dao.impl;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;

import javax.persistence.Query;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.idega.core.persistence.Param;
import com.idega.core.persistence.impl.GenericDaoImpl;
import com.idega.group.cache.business.GroupsCacheService;
import com.idega.user.bean.GroupRelationBean;
import com.idega.user.dao.GroupRelationDAO;
import com.idega.user.data.GroupTypeBMPBean;
import com.idega.user.data.bean.Group;
import com.idega.user.data.bean.GroupRelation;
import com.idega.user.data.bean.GroupRelationType;
import com.idega.user.data.bean.GroupType;
import com.idega.user.data.bean.User;
import com.idega.util.ArrayUtil;
import com.idega.util.CoreUtil;
import com.idega.util.ListUtil;
import com.idega.util.StringUtil;
import com.idega.util.expression.ELUtil;

@Scope(BeanDefinition.SCOPE_SINGLETON)
@Repository(GroupRelationDAO.BEAN_NAME)
@Transactional(readOnly = true)
public class GroupRelationDAOImpl extends GenericDaoImpl implements GroupRelationDAO {

	@Override
	public GroupRelation getById(Integer groupRelationId) {
		try {
			return getSingleResult(GroupRelation.QUERY_FIND_BY_ID, GroupRelation.class, new Param(GroupRelation.PARAM_GROUP_RELATION_ID, groupRelationId));
		} catch (Exception e) {
			getLogger().log(Level.WARNING, "Error getting group relation by id.", e);
		}

		return null;
	}

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

	@Autowired
	private GroupsCacheService groupsCacheService;

	private GroupsCacheService getGroupsCacheService() {
		if (groupsCacheService == null) {
			ELUtil.getInstance().autowire(this);
		}
		return groupsCacheService;
	}

	@Override
	public List<GroupRelationBean> getGroupRelationsByRelatedGroupTypeAndGroupTypes(String relatedGroupType, List<String> groupTypes, List<Integer> groupsIds, List<String> entityTypes) {
		long start = System.currentTimeMillis();
		try {
			if (StringUtil.isEmpty(relatedGroupType)) {
				getLogger().warning("Related group's type is not provided");
				return null;
			}

			StringBuilder query = null;
			try {
				List<GroupRelationBean> cachedResults = getGroupsCacheService().getGroupRelationsByRelatedGroupTypeAndGroupTypes(
						relatedGroupType,
						groupTypes,
						groupsIds,
						entityTypes
				);
				if (!ListUtil.isEmpty(cachedResults)) {
					return cachedResults;
				}

				List<Param> params = new ArrayList<Param>();
				params.add(new Param(GroupRelation.PARAM_RELATED_GROUP_TYPE, relatedGroupType));

				query = new StringBuilder("SELECT DATE(CASE WHEN r.terminationDate IS NOT NULL THEN r.terminationDate WHEN r.terminationDate IS NULL AND r.initiationModificationDate IS NOT NULL THEN init_modification_date WHEN r.terminationDate IS NULL AND r.initiationModificationDate IS NULL AND r.initiationDate IS NOT NULL THEN initiation_date END) AS date, ");
				query.append(" r.group.id, r.group.groupType.groupType, ");	//	1, 2
				query.append(" r.initiationDate, r.terminationDate, r.initiationModificationDate, r.terminationModificationDate, r.status, r.groupRelationID, ");	//	3, 4, 5, 6, 7, 8
				query.append(" r.group.name, r.relatedGroup.name "); //	9, 10
				query.append(" FROM ").append(GroupRelation.class.getName()).append(" r");
				query.append(" WHERE 1 = 1 ");

				if (!ListUtil.isEmpty(groupsIds)) {
					query.append(" and r.group.id in (:groupsIds) ");
					params.add(new Param("groupsIds", groupsIds));
				}
				if (!ListUtil.isEmpty(groupTypes)) {
					query.append(" AND r.group.groupType.groupType in (:" + GroupRelation.PARAM_GROUP_TYPES).append(") ");
					params.add(new Param(GroupRelation.PARAM_GROUP_TYPES, groupTypes));
				}
				query.append(" AND r.relatedGroupType.groupType = :" + GroupRelation.PARAM_RELATED_GROUP_TYPE);

				query.append(" GROUP BY 2");
				query.append(" ORDER BY date DESC");

				List<Object[]> allData = getResultListByInlineQuery(query.toString(), Object[].class, ArrayUtil.convertListToArray(params));
				if (ListUtil.isEmpty(allData)) {
					return null;
				}

				List<GroupRelationBean> results = new ArrayList<>();
				for (Object[] data: allData) {
					if (ArrayUtil.isEmpty(data) || data.length < 11) {
						continue;
					}

					try {
						Integer groupId = ((Number) data[1]).intValue();
						String groupName = (String) data[9];
						String groupType = (String) data[2];
						Timestamp initiationDate = (Timestamp) data[3];
						Timestamp terminationDate = (Timestamp) data[4];
						Timestamp initiationModificationDate = (Timestamp) data[5];
						Timestamp terminationModificationDate = (Timestamp) data[6];
						String status = (String) data[7];
						Integer groupRelationId = ((Number) data[8]).intValue();
						String relatedGroupName = (String) data[10];
						boolean active = status != null && (GroupRelation.STATUS_ACTIVE.equals(status) || GroupRelation.STATUS_ACTIVE_PENDING.equals(status));
						GroupRelationBean bean = new GroupRelationBean(
								groupRelationId,
								groupId,
								groupName,
								groupType,
								null,
								relatedGroupName,
								relatedGroupType,
								active,
								initiationDate,
								terminationDate,
								initiationModificationDate,
								terminationModificationDate
						);
						results.add(bean);
					} catch (Exception e) {
						getLogger().log(Level.WARNING, "Error creating " + GroupRelationBean.class.getName() + " from " + data, e);
					}
				}
				return results;
			} catch (Exception e) {
				getLogger().log(Level.WARNING, "Error getting group relations by query " + query, e);
			}
			return null;
		} finally {
			CoreUtil.doDebug(start, System.currentTimeMillis(), getClass().getName() + ".getGroupRelationsByRelatedGroupTypeAndGroupTypes, related group type: " + relatedGroupType +
					", group types: " + groupTypes + ", group IDs: " + groupsIds);
		}
	}

	@Override
	public List<Object[]> getGroupRelationsCountByRelatedGroupTypeAndGroupIdAndPeriodGroupedByDate(String relatedGroupType, List<Integer> groupIds, Date dateFrom, Date dateTo) {
		StringBuilder query = null;
		try {
			List<Param> params = new ArrayList<Param>();
			params.add(new Param(GroupRelation.PARAM_RELATED_GROUP_TYPE, relatedGroupType));
			if (!ListUtil.isEmpty(groupIds)) {
				params.add(new Param(GroupRelation.PARAM_GROUP_ID, groupIds));
			}
			if (dateFrom != null && dateTo != null) {
				params.add(new Param(GroupRelation.PARAM_DATE_FROM, dateFrom));
				params.add(new Param(GroupRelation.PARAM_DATE_TO, dateTo));
			}

			query = new StringBuilder("SELECT DISTINCT DATE(CASE WHEN r.terminationDate IS NOT NULL AND r.terminationDate >= :" + GroupRelation.PARAM_DATE_FROM + " AND r.terminationDate < :" + GroupRelation.PARAM_DATE_TO + " THEN r.terminationDate WHEN r.terminationDate IS NULL AND r.initiationModificationDate IS NOT NULL AND r.initiationModificationDate >= :" + GroupRelation.PARAM_DATE_FROM + " AND r.initiationModificationDate < :" + GroupRelation.PARAM_DATE_TO + " THEN init_modification_date WHEN r.terminationDate IS NULL AND r.initiationModificationDate IS NULL AND r.initiationDate IS NOT NULL AND r.initiationDate >= :" + GroupRelation.PARAM_DATE_FROM + " AND r.initiationDate < :" + GroupRelation.PARAM_DATE_TO + " THEN initiation_date END) AS date, COUNT(r.groupRelationID) ");
			query.append(" FROM GroupRelation r");
			query.append(" WHERE r.relatedGroupType.groupType = :" + GroupRelation.PARAM_RELATED_GROUP_TYPE);
			if (!ListUtil.isEmpty(groupIds)) {
				query.append(" AND r.group.groupID IN (:" + GroupRelation.PARAM_GROUP_ID + ") ");
			}
			if (dateFrom != null && dateTo != null) {
				query.append(" AND (");
				query.append(" (r.terminationDate IS NOT NULL AND r.terminationDate >= :" + GroupRelation.PARAM_DATE_FROM + " AND r.terminationDate < :" + GroupRelation.PARAM_DATE_TO + ") ");
				query.append(" OR (r.terminationDate IS NULL AND r.initiationModificationDate IS NOT NULL AND r.initiationModificationDate >= :" + GroupRelation.PARAM_DATE_FROM + " AND r.initiationModificationDate < :" + GroupRelation.PARAM_DATE_TO + ") ");
				query.append(" OR (r.terminationDate IS NULL AND r.initiationModificationDate IS NULL AND r.initiationDate IS NOT NULL AND r.initiationDate >= :" + GroupRelation.PARAM_DATE_FROM + " AND r.initiationDate < :" + GroupRelation.PARAM_DATE_TO + ") ");
				query.append(") ");
			}
			query.append(" GROUP BY 1 ");
			query.append(" HAVING COUNT(r.groupRelationID) > 0 ");
			query.append(" ORDER BY date DESC");

			List<Object[]> results = getResultListByInlineQuery(query.toString(), Object[].class, ArrayUtil.convertListToArray(params));
			return results;
		} catch (Exception e) {
			getLogger().log(Level.WARNING, "Error getting group relations: ", e);
		}
		return null;
	}

	@Override
	public Long getGroupRelationsCountByRelatedGroupTypeAndGroupIdAndPeriod(String relatedGroupType, Integer groupId, Date dateFrom, Date dateTo) {
		StringBuilder query = null;
		try {
			List<Param> params = new ArrayList<Param>();
			params.add(new Param(GroupRelation.PARAM_RELATED_GROUP_TYPE, relatedGroupType));
			if (groupId != null) {
				params.add(new Param(GroupRelation.PARAM_GROUP_ID, groupId));
			}
			if (dateFrom != null && dateTo != null) {
				params.add(new Param(GroupRelation.PARAM_DATE_FROM, dateFrom));
				params.add(new Param(GroupRelation.PARAM_DATE_TO, dateTo));
			}

			query = new StringBuilder("SELECT count(r) FROM GroupRelation r WHERE r.relatedGroupType.groupType = :" + GroupRelation.PARAM_RELATED_GROUP_TYPE);
			if (groupId != null) {
				query.append(" AND r.group.groupID = :" + GroupRelation.PARAM_GROUP_ID);
			}
			if (dateFrom != null && dateTo != null) {
				query.append(" AND (");
				query.append(" (r.terminationDate IS NOT NULL AND r.terminationDate >= :" + GroupRelation.PARAM_DATE_FROM + " AND r.terminationDate < :" + GroupRelation.PARAM_DATE_TO + ") ");
				query.append(" OR (r.terminationDate IS NULL AND r.initiationModificationDate IS NOT NULL AND r.initiationModificationDate >= :" + GroupRelation.PARAM_DATE_FROM + " AND r.initiationModificationDate < :" + GroupRelation.PARAM_DATE_TO + ") ");
				query.append(" OR (r.terminationDate IS NULL AND r.initiationModificationDate IS NULL AND r.initiationDate IS NOT NULL AND r.initiationDate >= :" + GroupRelation.PARAM_DATE_FROM + " AND r.initiationDate < :" + GroupRelation.PARAM_DATE_TO + ") ");
				query.append(") ");
			}

			Long count = getSingleResultByInlineQuery(query.toString(), Long.class, ArrayUtil.convertListToArray(params));
			return count;
		} catch (Exception e) {
			getLogger().log(Level.WARNING, "Error counting group relations: ", e);
		}
		return null;
	}

	@Override
	public List<GroupRelation> getGroupRelationsByRelatedGroupTypeAndGroupIdAndPeriod(String relatedGroupType, List<Integer> groupIds, Date dateFrom, Date dateTo) {
		StringBuilder query = null;
		try {
			List<Param> params = new ArrayList<Param>();
			params.add(new Param(GroupRelation.PARAM_RELATED_GROUP_TYPE, relatedGroupType));
			if (!ListUtil.isEmpty(groupIds)) {
				params.add(new Param(GroupRelation.PARAM_GROUP_ID, groupIds));
			}
			if (dateFrom != null && dateTo != null) {
				params.add(new Param(GroupRelation.PARAM_DATE_FROM, dateFrom));
				params.add(new Param(GroupRelation.PARAM_DATE_TO, dateTo));
			}

			query = new StringBuilder("SELECT r FROM GroupRelation r WHERE r.relatedGroupType.groupType = :" + GroupRelation.PARAM_RELATED_GROUP_TYPE);
			if (!ListUtil.isEmpty(groupIds)) {
				query.append(" AND r.group.groupID IN (:" + GroupRelation.PARAM_GROUP_ID + ") ");
			}
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
	public List<GroupRelation> getGroupRelationsByRelatedGroupTypeAndGroupId(String relatedGroupType, Integer groupId) {
		StringBuilder query = null;
		try {
			List<Param> params = new ArrayList<Param>();
			params.add(new Param(GroupRelation.PARAM_RELATED_GROUP_TYPE, relatedGroupType));
			if (groupId != null) {
				params.add(new Param(GroupRelation.PARAM_GROUP_ID, groupId));
			}

			query = new StringBuilder("SELECT r FROM GroupRelation r WHERE r.relatedGroupType.groupType = :" + GroupRelation.PARAM_RELATED_GROUP_TYPE);
			if (groupId != null) {
				query.append(" AND r.group.groupID = :" + GroupRelation.PARAM_GROUP_ID);
			}
			query.append(" ORDER BY ");
			query.append(" r.terminationDate, r.initiationModificationDate DESC ");

//			query.append("CASE ");
//			query.append("WHEN r.initiationModificationDate >= r.terminationDate AND r.initiationModificationDate >= r.initiationDate ");
//			query.append("THEN r.initiationModificationDate ");
//			query.append("WHEN r.terminationDate >= r.initiationDate ");
//			query.append("THEN r.terminationDate ");
//			query.append("ELSE r.initiationDate ");
//			query.append("END ");
//			query.append("desc ");
//			query.append(" LIMIT 1 ");

			List<GroupRelation> results = getResultListByInlineQuery(query.toString(), GroupRelation.class, null, 5, null, ArrayUtil.convertListToArray(params));
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
	public List<GroupRelation> getGroupRelationsByRelatedGroupTypeAndGroupTypeAndDate(String relatedGroupType, List<String> groupTypes, Date dateFrom, Date dateTo) {
		StringBuilder query = new StringBuilder();
		try {
			List<Param> params = new ArrayList<Param>();
			params.add(new Param(GroupRelation.PARAM_RELATED_GROUP_TYPE, relatedGroupType));
			if (!ListUtil.isEmpty(groupTypes)) {
				params.add(new Param(GroupRelation.PARAM_GROUP_TYPES, groupTypes));
			}
			if (dateFrom != null && dateTo != null) {
				params.add(new Param(GroupRelation.PARAM_DATE_FROM, dateFrom));
				params.add(new Param(GroupRelation.PARAM_DATE_TO, dateTo));
			}

			query.append("SELECT r FROM GroupRelation r WHERE r.relatedGroupType.groupType = :" + GroupRelation.PARAM_RELATED_GROUP_TYPE);
			if (!ListUtil.isEmpty(groupTypes)) {
				query.append(" AND r.group.groupType.groupType in (:" + GroupRelation.PARAM_GROUP_TYPES).append(") ");
			}
			if (dateFrom != null && dateTo != null) {
				query.append(" AND (");
				query.append(" (r.terminationDate IS NOT NULL AND r.terminationDate >= :" + GroupRelation.PARAM_DATE_FROM + " AND r.terminationDate < :" + GroupRelation.PARAM_DATE_TO + ") ");
				query.append(" OR (r.terminationDate IS NULL AND r.initiationModificationDate IS NOT NULL AND r.initiationModificationDate >= :" + GroupRelation.PARAM_DATE_FROM + " AND r.initiationModificationDate < :" + GroupRelation.PARAM_DATE_TO + ") ");
				query.append(" OR (r.terminationDate IS NULL AND r.initiationModificationDate IS NULL AND r.initiationDate IS NOT NULL AND r.initiationDate >= :" + GroupRelation.PARAM_DATE_FROM + " AND r.initiationDate < :" + GroupRelation.PARAM_DATE_TO + ") ");
				query.append(") ");
			}
			query.append(" ORDER BY r.terminationDate, r.initiationModificationDate DESC");

			List<GroupRelation> results = getResultListByInlineQuery(query.toString(), GroupRelation.class, ArrayUtil.convertListToArray(params));
			return results;
		} catch (Exception e) {
			getLogger().log(Level.WARNING, "Error getting group relations by query " + query, e);
		}
		return null;
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

	@Override
	public List<GroupRelation> getGroupRelationsByGroupIdAndRelatedGroupId(Integer groupId, Integer relatedGroupId) {
		try {
			return getResultList(GroupRelation.QUERY_FIND_BY_GROUP_ID_AND_RELATED_GROUP_ID,
					GroupRelation.class,
					new Param(GroupRelation.PARAM_GROUP_ID, groupId),
					new Param(GroupRelation.PARAM_RELATED_GROUP_ID, relatedGroupId));
		} catch (Exception e) {
			getLogger().log(Level.WARNING, "Error getting group relations by group id: " + groupId + " and relatedGroupId: " + relatedGroupId, e);
		}

		return null;
	}

	@Override
	public List<GroupRelation> getGroupRelationsByInitiationDate(Timestamp initiationDateStart, Timestamp initiationDateEnd) {
		try {
			return getResultList(GroupRelation.QUERY_FIND_BY_INITIATION_DATE,
					GroupRelation.class,
					new Param(GroupRelation.PARAM_INITIATION_DATE_START, initiationDateStart),
					new Param(GroupRelation.PARAM_INITIATION_DATE_END, initiationDateEnd)
			);
		} catch (Exception e) {
			getLogger().log(Level.WARNING, "Error getting group relations by initiation date start: " + initiationDateStart + ", end: " + initiationDateEnd, e);
		}

		return null;
	}


	@Override
	public List<GroupRelation> getTerminatedGroupRelationsByGroupIdsAndPeriod(List<Integer> groupIds, Date dateFrom, Date dateTo) {
		if (ListUtil.isEmpty(groupIds) || dateFrom == null || dateTo == null) {
			getLogger().log(Level.WARNING, "Group ids or date from, or date to are empty.");
			return null;
		}

		try {
			return getResultList(
					GroupRelation.QUERY_FIND_BY_GROUPS_IDS_AND_DELETED_AT_GIVEN_TIMEFRAME,
					GroupRelation.class,
					new Param("groupsIds", groupIds),
					new Param("dateFrom", dateFrom),
					new Param("dateTo", dateTo)
			);
		} catch (Exception e) {
			getLogger().log(Level.WARNING, "Error getting terminated group relations by group ids (" + groupIds + ") and period: " + dateFrom + " - " + dateTo, e);
		}
		return null;
	}


	@Override
	public void onChange(GroupRelation gr) {
		if (gr != null) {
			gr.onChange();
		}

	}

	@Override
	@Transactional(readOnly = false)
	public void updateTerminationDate(List<Integer> groupRelationIds, Date newDate) {
		if (newDate == null || ListUtil.isEmpty(groupRelationIds)) {
			getLogger().warning("Data or group relation ids are not provided");
			return;
		}

		try {
				Query q = getEntityManager().createNativeQuery(
						"UPDATE ic_group_relation SET termination_date = ?, term_modification_date = ? WHERE ic_group_relation_id IN (?)");
				q.setParameter(1, newDate);
				q.setParameter(2, newDate);
				q.setParameter(3, groupRelationIds);
				q.executeUpdate();
		} catch (Exception e) {
			getLogger().log(Level.WARNING, "Could not update group relations with a new termination date: " + newDate, e);
		}
	}

	@Override
	@Transactional(readOnly = false)
	public void fixInvalidRelations() {
		List<GroupRelation> invalidRelations = getResultList(
				GroupRelation.QUERY_FIND_INVALID_RELATIONS,
				GroupRelation.class
		);
		if(ListUtil.isEmpty(invalidRelations)) {
			return;
		}
		GroupRelationType relationType = find(
				GroupRelationType.class,
				GroupRelation.RELATION_TYPE_GROUP_PARENT
		);
		GroupType relatedGroupType = find(
				GroupType.class,
				GroupTypeBMPBean.TYPE_PERMISSION_GROUP
		);
		for(GroupRelation relation : invalidRelations) {
			relation.setStatus(GroupRelation.STATUS_ACTIVE);
			relation.setGroupRelationType(relationType);
			relation.setRelatedGroupType(relatedGroupType);
			merge(relation);
		}
	}

	@Override
	public List<GroupRelation> findParentGroupRelationsForGroup(Integer id) {
		return getResultList(
				GroupRelation.QUERY_FIND_PARENT_GROUP_RELATIONS_FOR_GROUP,
				GroupRelation.class,
				new Param("id", id)
		);
	}

	@Override
	public List<Group> findParentGroupsForGroup(Integer id) {
		return getResultList(
				GroupRelation.QUERY_FIND_PARENT_GROUPS_FOR_GROUP,
				Group.class,
				new Param("id", id)
		);
	}

	@Override
	@Transactional
	public void removeParentGroupsForGroup(
			Integer group,
			List<Integer> parents,
			User byUser
	) {
		List<GroupRelation> parentRelations = getResultList(
				GroupRelation.QUERY_FIND_PARENT_GROUP_RELATIONS_FOR_GROUP_BY_PARENT_GROUPS,
				GroupRelation.class,
				new Param("group", group),
				new Param("parents", parents)
		);
		if(parentRelations == null) {
			return;
		}
		Date time = new Date();
//		TODO: possible to do with one query and then publish events possibly
//		in separate thread
		for(GroupRelation relation : parentRelations) {
			setRemoved(relation, byUser, time);
			merge(relation);
		}
	}

	private void setRemoved(GroupRelation relation,User byUser,Date time) {
		relation.setStatus(GroupRelation.STATUS_PASSIVE);
		relation.setPassiveBy(byUser);
		relation.setTerminationDate(time);
	}

	@Override
	@Transactional
	public void storeGroupAsChildForGroups(
			Group group,
			List<Group> parents,
			User byUser
	) {
		Date time = new Date();
		GroupType type = group.getGroupType();
		GroupRelationType relationType = find(
				GroupRelationType.class,
				GroupRelation.RELATION_TYPE_GROUP_PARENT
		);
		for(Group parent : parents) {
			GroupRelation relation = new GroupRelation();
			relation.setGroup(parent);
			relation.setRelatedGroup(group);
			relation.setGroupRelationType(relationType);
			relation.setRelatedGroupType(type);
			relation.setCreatedBy(byUser);
			relation.setInitiationDate(time);
			persist(relation);
		}
	}

	@Override
	public List<GroupRelation> getByGroupIdsAndRelatedGroupIds(List<Integer> groupIds, List<Integer> relatedGroupIds) {
		if (ListUtil.isEmpty(groupIds) || ListUtil.isEmpty(relatedGroupIds)) {
			getLogger().log(Level.WARNING, "Group ids or date from, or date to are empty.");
			return null;
		}

		try {
			return getResultList(
					GroupRelation.QUERY_FIND_BY_GROUPS_IDS_AND_RELATED_GROUP_IDS,
					GroupRelation.class,
					new Param("groupsIds", groupIds),
					new Param("relatedGroupsIds", relatedGroupIds)
			);
		} catch (Exception e) {
			getLogger().log(Level.WARNING, "Error getting group relations by group ids (" + groupIds + ") and related group ids ( " + relatedGroupIds + " )" , e);
		}
		return null;
	}

}