/*
 * Created on Nov 4, 2004
 */
package com.idega.user.data;

/**
 * @author Sigtryggur
 * This class is meant as a container for a minimal set of information about groups.
 * That information is then kept in a HashMap that resides in memory as an application attribute.
 * This is therefore a "compact" version of Group to minimize memory usage.
 * This class should only be used when some minimal group information is needed for displaying- or sorting purposes.
 * 
 */
public class CachedGroup {
    private Integer primaryKey;
    private String name;
    private String groupType;

    public CachedGroup(Group group) {
        primaryKey = (Integer)group.getPrimaryKey();
        name = group.getName();
        groupType = group.getGroupType();
    }
    
    public Integer getPrimaryKey() {
        return primaryKey;
    }
 
    public void setPrimaryKey(Integer primaryKey) {
        this.primaryKey = primaryKey;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGroupType() {
        return groupType;
    }

    public void setGroupType(String groupType) {
        this.groupType = groupType;
    }

    public String getName() {
        return name;
    }
}
