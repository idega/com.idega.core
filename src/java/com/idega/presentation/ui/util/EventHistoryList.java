/*
 * Created on 23.8.2004
 *
 * Copyright (C) 2004 Idega hf. All Rights Reserved.
 *
 *  This software is the proprietary information of Idega hf.
 *  Use is subject to license terms.
 */
package com.idega.presentation.ui.util;

import java.text.DateFormat;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.TreeSet;

import com.idega.idegaweb.IWResourceBundle;
import com.idega.presentation.Block;
import com.idega.presentation.IWContext;
import com.idega.presentation.Table;
import com.idega.presentation.text.Text;

/**
 * @author aron
 *
 * EventHistoryList a represents and formats a list of event entries
 */
public class EventHistoryList extends Block{
    
    private TreeSet eventEntries = new TreeSet(new EventEntryComparator());
    private String bundleIdentifier = null;
    private boolean showUser = true;
    private boolean showSource = true;
    private boolean showEvent = true;

    /* (non-Javadoc)
     * @see com.idega.presentation.PresentationObject#main(com.idega.presentation.IWContext)
     */
    public void main(IWContext iwc) throws Exception {
        if(eventEntries!=null && !eventEntries.isEmpty()){
            IWResourceBundle iwrb = getResourceBundle(iwc);
            Table eventTable = new Table();
            eventTable.setCellspacing(1);
            eventTable.setCellpadding(2);
            eventTable.setNoWrap();
            DateFormat df = DateFormat.getDateInstance(DateFormat.SHORT,iwc.getCurrentLocale());
            DateFormat tf = DateFormat.getTimeInstance(DateFormat.MEDIUM,iwc.getCurrentLocale());
            int row = 1;
            int col = 1;
            eventTable.add(getText(iwrb.getLocalizedString("eventhistory.event_type","Type")),col++,row);
            eventTable.add(getText(iwrb.getLocalizedString("eventhistory.date","Date")),col++,row);
            eventTable.add(getText(iwrb.getLocalizedString("eventhistory.time","Time")),col++,row);
            if(showEvent)
                eventTable.add(getText(iwrb.getLocalizedString("eventhistory.event","Event")),col++,row);
            if(showSource)
                eventTable.add(getText(iwrb.getLocalizedString("eventhistory.source","Source")),col++,row);
            if(showUser)
                eventTable.add(getText(iwrb.getLocalizedString("eventhistory.user","User")),col++,row);
            eventTable.setLineAfterRow(row);
            row++;
            col = 1;
            for (Iterator iter = eventEntries.iterator(); iter.hasNext();) {
                EventEntry event = (EventEntry) iter.next();
                eventTable.add(getText(event.getType()),col++,row);
                eventTable.add(getText(df.format(event.getDate())),col++,row);
                eventTable.add(getText(tf.format(event.getDate())),col++,row);
                if(showEvent)
                    eventTable.add(getText(event.getEvent()),col++,row);
                if(showSource)
                    eventTable.add(getText(event.getSource()),col++,row);
                if(showUser)
                    eventTable.add(getText(event.getUser()),col++,row);
                
                row++;
                col =1;
            }
            
            add(eventTable);
        }
        else{
            
        }
        
      
    }
    
    
    private Text getText(String text){
        return new Text(text);
    }
    
    /**
     * Adding EventEntry object
     * @param entry
     */
    public void addEvent(EventEntry entry){
        eventEntries.add(entry);
    }
    
    /**
     * Adding collection of EventEntry objects
     * @param events
     */
    public void addEvents(Collection events){
        eventEntries.addAll(events);
    }
    
    
    /* (non-Javadoc)
     * @see com.idega.presentation.PresentationObject#getBundleIdentifier()
     */
    public String getBundleIdentifier() {
        // TODO Auto-generated method stub
        if(bundleIdentifier!=null)
            return bundleIdentifier;
        else
            return super.getBundleIdentifier();
    }
    
    private class EventEntryComparator implements Comparator{

        /* (non-Javadoc)
         * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
         */
        public int compare(Object arg0, Object arg1) {
            EventEntry entryOne = (EventEntry) arg0;
            EventEntry entryTwo = (EventEntry) arg1;
            int ret =  entryOne.getDate().compareTo(entryTwo.getDate());
            ret *=-1;
            return ret;
        }
        
    }
    
    /**
     * @param showEvent The showEvent to set.
     */
    public void setShowEvent(boolean showEvent) {
        this.showEvent = showEvent;
    }
    /**
     * @param showSource The showSource to set.
     */
    public void setShowSource(boolean showSource) {
        this.showSource = showSource;
    }
    /**
     * @param showUser The showUser to set.
     */
    public void setShowUser(boolean showUser) {
        this.showUser = showUser;
    }
}
