package com.idega.core.component.data;


import java.util.Collection;

import javax.ejb.CreateException;
import javax.ejb.FinderException;

import com.idega.data.IDOHome;

public interface ICObjectHome extends IDOHome {

	public ICObject create() throws CreateException;

	public ICObject findByPrimaryKey(Object pk) throws FinderException;

	public Collection<ICObject> findAll() throws FinderException;

	public Collection<ICObject> findAllByObjectType(String type) throws FinderException;

	public Collection<ICObject> findAllByObjectTypeOrdered(String type) throws FinderException;

	public Collection<ICObject> findAllByObjectTypeAndBundle(String type, String bundle) throws FinderException;

	public Collection<ICObject> findAllByBundle(String bundle) throws FinderException;

	public ICObject findByClassName(String className) throws FinderException;

	public Collection<ICObject> findAllBlocksByBundle(String bundle) throws FinderException;

	public Collection<ICObject> findAllBlocks() throws FinderException;

	public Collection<ICObject> findAllElementsByBundle(String bundle) throws FinderException;

	public Collection<ICObject> findAllElements() throws FinderException;
}