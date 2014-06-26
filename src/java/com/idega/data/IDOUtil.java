package com.idega.data;
/**
 * Title:        idega Data Objects
 * Description:  Idega Data Objects is a Framework for Object/Relational mapping and seamless integration between datastores
 * Copyright:    Copyright (c) 2002
 * Company:      idega
 * @author <a href="mailto:tryggvi@idega.is">Tryggvi Larusson</a>
 * @version 1.0
 */
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.idega.repository.data.Instantiator;
import com.idega.repository.data.Singleton;
import com.idega.repository.data.SingletonRepository;
import com.idega.util.CoreConstants;
import com.idega.util.ListUtil;
public class IDOUtil implements Singleton {
	private static final String COMMA_AND_SPACE = ", ";
	private static final String SINGLE_QUOTE = "'";
	private static Instantiator instantiator = new Instantiator() { @Override
	public Object getInstance() { return new IDOUtil();}};

	protected IDOUtil() {
		// empty
	}

	public static IDOUtil getInstance() {
		return (IDOUtil) SingletonRepository.getRepository().getInstance(IDOUtil.class, instantiator);
	}

	/**
	 * @param list A list of IDOEntity objects
	 *
	 * @returns a String with comma separated list of primary keys for the IDOEntities
	 */
	public String convertListToCommaseparatedString(Collection<IDOEntity> list) {
		StringBuffer sList = new StringBuffer();
		if (list != null && !list.isEmpty()) {
			//String sGroupList = "";
			Iterator<IDOEntity> iter = list.iterator();
			for (int g = 0; iter.hasNext(); g++) {
				IDOEntity item = iter.next();
				if (g > 0) {
					sList.append(COMMA_AND_SPACE);
				}

				if(item!=null) {
					sList.append(item.getPrimaryKey());
				}

			}
		}
		return sList.toString();
	}

	/**
	 * @param Collection A collection of Strings
	 *
	 * @returns a String with comma separated values within quotationmarks e.g. 'asdf','asdf'
	 */
	public String convertCollectionOfStringsToCommaseparatedString(Collection<String> list) {
		StringBuffer sList = new StringBuffer();
		if (list != null && !list.isEmpty()) {
			//String sGroupList = "";
			Iterator<String> iter = list.iterator();
			for (int g = 0; iter.hasNext(); g++) {
				String item = iter.next();
				if (g > 0) {
					sList.append(COMMA_AND_SPACE);
				}

				if(item!=null) {
					sList.append("'").append(item).append("'");
				}

			}
		}
		return sList.toString();
	}

	/**
	 * @param Collection A list of Integers
	 *
	 * @returns a String with comma separated values e.g. 123,3,234
	 */
	public String convertCollectionOfIntegersToCommaseparatedString(Collection<Integer> list) {
		StringBuffer sList = new StringBuffer();
		if (list != null && !list.isEmpty()) {
			//String sGroupList = "";
			Iterator<Integer> iter = list.iterator();
			for (int g = 0; iter.hasNext(); g++) {
				Integer item = iter.next();
				if (g > 0) {
					sList.append(COMMA_AND_SPACE);
				}

				if(item!=null) {
					sList.append(item);
				}

			}
		}
		return sList.toString();
	}

	/**
	 * @param sArray An array of string primary keys
	 *
	 * @returns a String with comma separated list of primary keys for the IDOEntities
	 */
	public String convertArrayToCommaseparatedString(String[] sArray) {
		return convertArrayToCommaseparatedString(sArray, false);
	}

	/**
	 * @param entityArray An array of IDOEntity values
	 * @returns a String with comma separated list of primary keys for the IDOEntities with a simple quotemark between
	 */
	public String convertArrayToCommaseparatedString(
		IDOEntity[] entityArray) {
		return convertArrayToCommaseparatedString(entityArray,true);
	}

	/**
	 * @param entityArray An array of IDOEntity values
	 * @param whithSimpleQuoteMarks sets if there should be quotemarks around (primary key) values
	 *
	 * @returns a String with comma separated list of primary keys for the IDOEntities
	 */
	public String convertArrayToCommaseparatedString(
		IDOEntity[] entityArray,
		boolean whithSimpleQuoteMarks) {
		StringBuffer sList = new StringBuffer();
		if (entityArray != null && entityArray.length > 0) {
			for (int g = 0; g < entityArray.length; g++) {
				String sPK = null;
				try {
					sPK = entityArray[g].getPrimaryKey().toString();
				} catch (Exception e) {
					//e.printStackTrace();
				}
				if (sPK != null) {
					if (g > 0) {
						sList.append(COMMA_AND_SPACE);
					}
					if (whithSimpleQuoteMarks) {
						sList.append(SINGLE_QUOTE).append(sPK).append(SINGLE_QUOTE);
					} else {
						sList.append(sPK);
					}
				}
			}
		}
		return sList.toString();
	}
	public String convertArrayToCommaseparatedString(
		String[] sArray,
		boolean whithSimpleQuoteMarks) {
			StringBuffer sList = new StringBuffer();
		if (sArray != null && sArray.length > 0) {
			for (int g = 0; g < sArray.length; g++) {
				if (g > 0) {
					sList.append(COMMA_AND_SPACE);
				}
				if (whithSimpleQuoteMarks) {
					sList.append(SINGLE_QUOTE).append(sArray[g]).append(SINGLE_QUOTE);
				} else {
					sList.append(sArray[g]);
				}
			}
		}
		return sList.toString();
	}

	/**
	 * Returns the int ID of an entity if the primaryKey of the entity is an Integer.
	 * If not it throws a RuntimeException	 * @param entity an IDOEntity instance.	 * @return int Which is the ID of the entity if the primary key is an integer
	 * @throws RuntimeException if the primary key of the entity is not Integer	 */
	public int getID(IDOEntity entity){
		try{
			Integer iID = (Integer)entity.getPrimaryKey();
			return iID.intValue();
		}
		catch(Exception e){
			throw new RuntimeException("Error getting ID of entity. The underlying exception was: "+e.getClass().getName()+" : "+e.getMessage());
		}
	}

	/**
	 * Returns the int IDs of all entities as an int[] if the primaryKey of the entities is an Integer.
	 * If not it throws a RuntimeException
	 * @param entities a Collection of IDOEntity instances.
	 * @return int[] Which is the IDs of all the entities in the Collection if the primary key is an integer
	 * @throws RuntimeException if the primary key of the entities is not Integer
	 */
	public int[] getIDs(Collection<IDOEntity> entities){
		int[] theReturn = new int[entities.size()];
		Iterator<IDOEntity> iter = entities.iterator();
		int i = 0;
		while(iter.hasNext()){
			IDOEntity entity = iter.next();
			theReturn[i++]=getID(entity);
		}
		return theReturn;
	}

	/**
	 * Used to convert a Collection of IDOEntities to a Map.
	 * @param entities a Collection of IDOEntity instances.
	 * @return if entities are not null or empty it return a Map that has the primarykeys of the entities as the keys and the entities as values, else returns null
	 */
	@SuppressWarnings("unchecked")
	public <K extends Object, E extends IDOEntity> Map<K, E> convertIDOEntityCollectionToMapOfPrimaryKeysAndEntityValues(Collection<E> entities){
		if (entities!=null && !entities.isEmpty()) {
			Map<K, E> map = new HashMap<K, E>();

			for (Iterator<E> iter = entities.iterator(); iter.hasNext();) {
				E entity = iter.next();
				map.put((K) entity.getPrimaryKey(), entity);
			}
			return map;
		} else {
			return null;
		}
	}

	/**
	 *
	 * @param entities to get primary keys for;
	 * @return {@link List} of {@link IDOEntity#getPrimaryKey()} or
	 * {@link Collections#emptyList()} on failure;
	 * @author <a href="mailto:martynas@idega.is">Martynas Stakė</a>
	 */
	public List<String> getPrimaryKeys(Collection<? extends IDOEntity> entities){
		if(!ListUtil.isEmpty(entities)){
			List<String> returnList = new ArrayList<String>(entities.size());
			for (Iterator<? extends IDOEntity> iter = entities.iterator(); iter.hasNext();) {
				returnList.add(iter.next().getPrimaryKey().toString());
			}

			return returnList;
		}
		else {
			return Collections.emptyList();
		}
	}

	/**
	 *
	 * @param coll to convert, not <code>null</code>;
	 * @param whithSimpleQuoteMarks tells if each element should have quotes
	 * or not;
	 * @return {@link String} of {@link Collection} of
	 * {@link IDOEntity#getPrimaryKey()} separated by quotes or {@link CoreConstants#EMPTY}
	 * on failure;
	 * @author <a href="mailto:martynas@idega.is">Martynas Stakė</a>
	 */
	public String convertListToCommaseparatedString(Collection<? extends IDOEntity> coll, boolean whithSimpleQuoteMarks) {
		StringBuffer sList = new StringBuffer();
		if (!ListUtil.isEmpty(coll)) {
			for (Iterator<? extends IDOEntity> iter = coll.iterator(); iter.hasNext();) {
				Object sPK = iter.next().getPrimaryKey();
				if (sPK != null) {
					if (whithSimpleQuoteMarks) {
						sList.append(SINGLE_QUOTE).append(sPK).append(SINGLE_QUOTE);
					} else {
						sList.append(sPK);
					}
				}

				if (iter.hasNext()) {
					sList.append(COMMA_AND_SPACE);
				}
			}
		}

		return sList.toString();
	}



}