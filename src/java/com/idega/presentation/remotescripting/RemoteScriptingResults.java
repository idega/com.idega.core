package com.idega.presentation.remotescripting;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import com.idega.presentation.IWContext;
import com.idega.presentation.Layer;
import com.idega.presentation.Page;


/**
 * @author gimmi
 */
public class RemoteScriptingResults extends Page {
	
	private Map map = new HashMap();
	
	public RemoteScriptingResults(String layerID, Collection items) {
		map.put(layerID, items);
	}
	
	public void addLayer(String layerID, Collection items) {
		map.put(layerID, items);
	}
	
	public void main(IWContext iwc) {
		
		if (map != null) {
			Set layerIDSet = map.keySet();
			Iterator layerIDIter = layerIDSet.iterator();
			Collection items;
			Iterator itemIter;
			Object layerID;

			while (layerIDIter.hasNext()) {
				layerID = layerIDIter.next();
				Layer layer = new Layer();

				layer.setID(layerID.toString());
				
				items = (Collection) map.get(layerID);
				itemIter = items.iterator();
				while (itemIter.hasNext()) {
					Layer l = new Layer();
					String id = itemIter.next().toString();
					l.setName(id);
					layer.add(l);
				}
				
				add(layer);
			}
		}
		
		
	}
	
	
}
