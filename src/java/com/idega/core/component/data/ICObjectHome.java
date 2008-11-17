package com.idega.core.component.data;


import java.util.Collection;
import javax.ejb.CreateException;
import com.idega.data.IDOHome;
import javax.ejb.FinderException;

public interface ICObjectHome extends IDOHome {

	public ICObject create() throws CreateException;

	public ICObject findByPrimaryKey(Object pk) throws FinderException;

	public Collection findAll() throws FinderException;

	public Collection findAllByObjectType(String type) throws FinderException;

	public Collection findAllByObjectTypeOrdered(String type) throws FinderException;

	public Collection findAllByObjectTypeAndBundle(String type, String bundle) throws FinderException;

	public Collection findAllByBundle(String bundle) throws FinderException;

	public ICObject findByClassName(String className) throws FinderException;

	public Collection findAllBlocksByBundle(String bundle) throws FinderException;

	public Collection findAllBlocks() throws FinderException;

	public Collection findAllElementsByBundle(String bundle) throws FinderException;

	public Collection findAllElements() throws FinderException;
}