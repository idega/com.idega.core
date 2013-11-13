package com.idega.presentation.filter;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import javax.ejb.FinderException;

import com.idega.data.IDOEntity;
import com.idega.data.IDOHome;
import com.idega.data.IDOLookup;
import com.idega.data.IDOLookupException;
import com.idega.presentation.CSSSpacer;
import com.idega.presentation.IWContext;
import com.idega.presentation.Layer;
import com.idega.presentation.Span;
import com.idega.presentation.text.Heading3;
import com.idega.presentation.text.Text;
import com.idega.presentation.ui.CheckBox;
import com.idega.presentation.ui.InterfaceObject;
import com.idega.util.ArrayUtil;
import com.idega.util.CoreConstants;
import com.idega.util.ListUtil;
import com.idega.util.PresentationUtil;
import com.idega.util.StringUtil;

/**
 *
 * <p>Universal selector for {@link IDOEntity} classes. Displays
 * {@link CheckBox}es, which possible to select and selected ones.
 * After changing one of {@link CheckBox} result will be saved to
 * {@link FilterList#getSelectedEntities()} method. You can read selected
 * entities from there.</p>
 * <p>You can report about problems to:
 * <a href="mailto:martynas@idega.is">Martynas Stakė</a></p>
 * @param <T>
 *
 * @version 1.0.0 Mar 6, 2013
 * @author <a href="mailto:martynas@idega.is">Martynas Stakė</a>
 */
public abstract class FilterList<T extends IDOEntity> extends InterfaceObject {

	private List<T> selectedEntities = null;

	private List<T> entities = null;

	private String parameterName = null;

	@Override
	public void main(IWContext iwc) throws Exception {
		PresentationUtil.addStyleSheetToHeader(iwc, getBundle(iwc).getVirtualPathWithFileNameString("style/filter.css"));

		Layer container = new Layer();
		container.setStyleClass("filterListStyle");
		add(container);

		if (ListUtil.isEmpty(getEntities())) {
			container.add(
					new Heading3(getResourceBundle(iwc).getLocalizedString(
							"filter.nothing_found", "Nothing found"
							)
					));
			return;
		}

		if (!ListUtil.isEmpty(getEntities())) {
			for (T entity: getEntities()) {
				Layer entityEntry = new Layer();
				container.add(entityEntry);
				container.add(new CSSSpacer());

				String id = entity.getPrimaryKey().toString();
				String name = getRepresentation(entity, getRepresentationMethodName());

				entityEntry.add(getCheckBox(iwc, id));

				Span spanName = new Span(new Text(name));
				entityEntry.add(spanName);
			}
		}

		return;
	}

	protected CheckBox getCheckBox(IWContext iwc, String id) {
		if (StringUtil.isEmpty(id) || iwc == null) {
			return null;
		}

		String checkBoxName = getParameterName();
		String value = id;
		CheckBox select = new CheckBox(checkBoxName, value);
		select.setChecked(getSelectedEntity(id) != null);

		return select;
	}

	/**
	 *
	 * <p>Takes name of {@link IDOEntity} from given method.</p>
	 * @param entity to take name from, not <code>null</code>;
	 * @param representationMethodName of entity, which should be used to
	 * represent entity, not <code>null</code>.
	 * @return representation of {@link IDOEntity} of
	 * {@link CoreConstants#MINUS} if something failed.
	 * @author <a href="mailto:martynas@idega.com">Martynas Stakė</a>
	 */
	public String getRepresentation(T entity, String representationMethodName) {
		if (entity == null || StringUtil.isEmpty(representationMethodName)) {
			return CoreConstants.MINUS;
		}

		Method[] methods = entity.getClass().getMethods();
		if (ArrayUtil.isEmpty(methods)) {
			return CoreConstants.MINUS;
		}

		Method requiredMethod = null;
		for (Method method : methods) {
			if (representationMethodName.equals(method.getName()))  {
				requiredMethod = method;
			}
		}

		if (!String.class.equals(requiredMethod.getReturnType())) {
			return CoreConstants.MINUS;
		}

		try {
			return (String) requiredMethod.invoke(entity);
		} catch (IllegalArgumentException e) {
			getLogger().log(Level.WARNING,
					"Wrong arguments passed to " + entity.getClass().getName() +
					"." + requiredMethod.getName());
		} catch (IllegalAccessException e) {
			getLogger().log(Level.WARNING,
					"Method " + entity.getClass().getName() +
					"." + requiredMethod.getName() + " is not visible in this context");
		} catch (InvocationTargetException e) {
			getLogger().log(Level.WARNING,
					"Failed to invoke " + entity.getClass().getName() +
					"." + requiredMethod.getName(), e);
		}

		return CoreConstants.MINUS;
	}

	@Override
	public String getBundleIdentifier() {
		return CoreConstants.CORE_IW_BUNDLE_IDENTIFIER;
	}

	/**
	 *
	 * <p>Contains {@link Class} instance of representable entity.</p>
	 * @return class name of something, which extends {@link IDOEntity}.
	 * @author <a href="mailto:martynas@idega.com">Martynas Stakė</a>
	 */
	public abstract Class<T> getRepresentableClass();

	/**
	 *
	 * @return method name, like: "getName", which contains name of
	 * given {@link IDOEntity} instance.
	 * @author <a href="mailto:martynas@idega.com">Martynas Stakė</a>
	 */
	public abstract String getRepresentationMethodName();

	/**
	 *
	 * @return {@link IDOEntity}s, which shouldbe marked as selected;
	 * @author <a href="mailto:martynas@idega.com">Martynas Stakė</a>
	 */
	public List<T> getSelectedEntities() {
		return selectedEntities;
	}

	public ArrayList<String> getSelectedEntitiesIDs() {
		if (ListUtil.isEmpty(getSelectedEntities())) {
			return null;
		}

		ArrayList<String> ids = new ArrayList<String>(getSelectedEntities().size());
		for (T entity : getSelectedEntities()) {
			ids.add(entity.getPrimaryKey().toString());
		}

		return ids;
	}

	/**
	 *
	 * <p>Searches for specified {@link IDOEntity} in
	 * {@link FilterList#getSelectedEntities()}.</p>
	 * @param entityID to search for, not <code>null</code>;
	 * @return selected {@link IDOEntity} or <code>null</code> if not found.
	 * @author <a href="mailto:martynas@idega.com">Martynas Stakė</a>
	 */
	public T getSelectedEntity(String entityID) {
		if (StringUtil.isEmpty(entityID)) {
			return null;
		}

		if (ListUtil.isEmpty(getSelectedEntities())) {
			return null;
		}

		for (T entity : getSelectedEntities()) {
			if (entityID.equals(entity.getPrimaryKey().toString())) {
				return entity;
			}
		}

		return null;
	}

	/**
	 *
	 * <p>Appends {@link FilterList#getSelectedEntities()} list if given
	 * {@link IDOEntity} is not in list.</p>
	 * @param entityID to append, not <code>null</code>;
	 * @return <code>true</code> if appended, <code>false</code> otherwise.
	 * @author <a href="mailto:martynas@idega.com">Martynas Stakė</a>
	 */
	public boolean addSelectedEntity(String entityID) {
		if (StringUtil.isEmpty(entityID)) {
			return Boolean.FALSE;
		}

		return addSelectedEntity(getEntity(entityID));
	}

	/**
	 *
	 * <p>Appends {@link FilterList#getSelectedEntities()} list if given
	 * {@link IDOEntity} is not in list.</p>
	 * @param entity to append, not <code>null</code>;
	 * @return <code>true</code> if appended, <code>false</code> otherwise.
	 * @author <a href="mailto:martynas@idega.com">Martynas Stakė</a>
	 */
	public boolean addSelectedEntity(T entity) {
		if (getSelectedEntities() == null) {
			setSelectedEntities(new ArrayList<T>(1));
		}

		if (getSelectedEntity(entity.getPrimaryKey().toString()) != null) {
			return Boolean.FALSE;
		}

		return getSelectedEntities().add(entity);
	}

	/**
	 *
	 * <p>Removes {@link IDOEntity} from {@link FilterList#getSelectedEntities()}
	 * list if found.</p>
	 * @param entity to remove, not <code>null</code>;
	 * @return <code>true</code> when removed, <code>false</code> otherwise.
	 * @author <a href="mailto:martynas@idega.com">Martynas Stakė</a>
	 */
	public boolean removeSelectedEntity(IDOEntity entity) {
		if (entity == null) {
			return Boolean.FALSE;
		}

		return getSelectedEntities().remove(entity);
	}

	/**
	 *
	 * <p>Removes {@link IDOEntity} from {@link FilterList#getSelectedEntities()}
	 * list if found.</p>
	 * @param entityID of entity to remove, not <code>null</code>;
	 * @return <code>true</code> when removed, <code>false</code> otherwise.
	 * @author <a href="mailto:martynas@idega.com">Martynas Stakė</a>
	 */
	public boolean removeSelectedEntity(String entityID) {
		if (StringUtil.isEmpty(entityID)) {
			return Boolean.FALSE;
		}

		return removeSelectedEntity(getSelectedEntity(entityID));
	}

	public void setSelectedEntities(List<T> selectedEntities) {
		if (!ListUtil.isEmpty(selectedEntities)) {
			this.selectedEntities = new ArrayList<T>();

			for (T entity : selectedEntities) {
				this.selectedEntities.add(entity);
			}
		}
	}

	public void setSelectedEntitiesByIDs(List<String> selectedEntitiesIDs) {
		setSelectedEntities(getEntities(selectedEntitiesIDs));
	}

	/**
	 *
	 * @return full list of {@link IDOEntity}s to select from.
	 * @author <a href="mailto:martynas@idega.com">Martynas Stakė</a>
	 */
	public List<T> getEntities() {
		return entities;
	}

	/**
	 *
	 * @param id of entity selected or ready to select, not <code>null</code>;
	 * @return entity if exists, <code>null</code> otherwise;
	 * @author <a href="mailto:martynas@idega.com">Martynas Stakė</a>
	 */
	protected T getEntity(String id) {
		if (StringUtil.isEmpty(id)) {
			return null;
		}

		try {
			return getEntityHome().findByPrimaryKeyIDO(id);
		} catch (FinderException e) {
			getLogger().log(Level.WARNING, "Unable to find entity by ID: " + id);
		}

		return null;
	}

	/**
	 *
	 * @param ids of entities to get, not empty;
	 * @return specified entities from full list of selection, <code>null</code>
	 * on failure.
	 * @author <a href="mailto:martynas@idega.com">Martynas Stakė</a>
	 */
	protected List<T> getEntities(List<String> ids) {
		if (ListUtil.isEmpty(ids)) {
			return null;
		}

		List<T> entities = new ArrayList<T>();
		for (String id : ids) {
			T entity = getEntity(id);

			if (entity == null) {
				continue;
			}

			entities.add(entity);
		}

		return entities;
	}

	public void setEntities(List<T> entities) {
		if (!ListUtil.isEmpty(entities)) {
			this.entities = new ArrayList<T>();

			for (T entity : entities) {
				this.entities.add(entity);
			}
		}
	}

	public void setEntitiesByIDs(List<String> ids) {
		setEntities(getEntities(ids));
	}

	private IDOHome entityHome = null;

	protected IDOHome getEntityHome() {
		if (this.entityHome != null) {
			return this.entityHome;
		}

		try {
			this.entityHome = IDOLookup.getHome(getRepresentableClass());
		} catch (IDOLookupException e) {
			getLogger().log(Level.WARNING, "Unable to get home for " +
					getRepresentableClass());
		}

		return this.entityHome;
	}

	public String getParameterName() {
		return parameterName;
	}

	public void setParameterName(String parameterName) {
		this.parameterName = parameterName;
	}
}
