package com.idega.core.file.data;

public class ICFileTypeHandlerHomeImpl extends com.idega.data.IDOFactory implements ICFileTypeHandlerHome {

	protected Class getEntityInterfaceClass() {
		return ICFileTypeHandler.class;
	}

	public ICFileTypeHandler create() throws javax.ejb.CreateException {
		return (ICFileTypeHandler) super.idoCreate();
	}

	public ICFileTypeHandler createLegacy() {
		try {
			return create();
		}
		catch (javax.ejb.CreateException ce) {
			throw new RuntimeException("CreateException:" + ce.getMessage());
		}
	}

	public ICFileTypeHandler findByPrimaryKey(int id) throws javax.ejb.FinderException {
		return (ICFileTypeHandler) super.idoFindByPrimaryKey(id);
	}

	public ICFileTypeHandler findByPrimaryKey(Object pk) throws javax.ejb.FinderException {
		return (ICFileTypeHandler) super.idoFindByPrimaryKey(pk);
	}

	public ICFileTypeHandler findByPrimaryKeyLegacy(int id) throws java.sql.SQLException {
		try {
			return findByPrimaryKey(id);
		}
		catch (javax.ejb.FinderException fe) {
			throw new java.sql.SQLException(fe.getMessage());
		}
	}

	public ICFileTypeHandler findByName(java.lang.String p0) throws javax.ejb.FinderException{
		com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
		Object pk = ((ICFileTypeHandlerBMPBean)entity).ejbFindByName(p0);
		this.idoCheckInPooledEntity(entity);
		return this.findByPrimaryKey(pk);
	}
}