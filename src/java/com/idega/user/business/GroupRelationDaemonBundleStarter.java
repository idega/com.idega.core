package com.idega.user.business;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.rmi.RemoteException;
import java.util.Collection;
import java.util.Iterator;

import com.idega.idegaweb.IWApplicationContext;
import com.idega.idegaweb.IWBundle;
import com.idega.idegaweb.IWBundleStartable;
import com.idega.user.data.GroupRelation;
import com.idega.user.data.GroupRelationHome;
import com.idega.util.EventTimer;
import com.idega.util.IWTimestamp;

/**
 * Title:GroupRelationDaemonBundleStarter
 * Description: GroupRelationDaemonBundleStarter implements the IWBundleStartable interface. The start method of this
 * object is called during the Bundle loading when starting up a idegaWeb applications. It checks for pending grouprelations and processes them.
 * Copyright:    Copyright (c) 2001
 * Company:      idega software
 * @author Eirikur S. Hrafnsson eiki@idega.is
 * @version 1.0
 */
public class GroupRelationDaemonBundleStarter implements IWBundleStartable, ActionListener {
	private GroupBusiness groupBiz;
	private IWBundle bundle;
	private EventTimer timer;
	public static final String TIMER_THREAD_NAME = "ic_user_Group_Relation_Daemon";
	
	private EventTimer groupTreeEventTimer;
	public static final String GROUP_TREE_TIMER_THREAD_NAME = "user_fetch_grouptree";
	private static final String BUNDLE_PROPERTY_NAME_FETCH_GROUPTREE_INTERVAL = "user_fetch_grouptree_interval";

	
	public GroupRelationDaemonBundleStarter() {
	}
	
	public void start(IWBundle bundle) {
		this.bundle = bundle;
		timer = new EventTimer(EventTimer.THREAD_SLEEP_30_MINUTES, TIMER_THREAD_NAME);
		timer.addActionListener(this);
		//Starts the thread while waiting for 3 mins. before the idegaWebApp starts up.
		// -- Fix for working properly on Interebase with entity-auto-create-on.
		timer.start(3 * 60 * 1000);
		System.out.println("Group Relation Daemon Bundle Starter: starting");
		
		try {
			//System.out.println("[USER]: com.idega.user bundle starter starting...");
			if(GroupTreeImageProcedure.getInstance().isAvailable()){
				int fetchGroupTreeInterval = Integer.parseInt(bundle.getProperty(BUNDLE_PROPERTY_NAME_FETCH_GROUPTREE_INTERVAL, String.valueOf(EventTimer.THREAD_SLEEP_5_MINUTES)));
				groupTreeEventTimer = new EventTimer(fetchGroupTreeInterval, GROUP_TREE_TIMER_THREAD_NAME);
				groupTreeEventTimer.addActionListener(this);
				groupTreeEventTimer.start();
		    }
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void actionPerformed(ActionEvent event) {
		try {	
			if (event.getActionCommand().equalsIgnoreCase(TIMER_THREAD_NAME)) {
				System.out.println("[Group Relation Daemon - "+IWTimestamp.RightNow().toString()+" ] - Checking for pending relations");
				Collection relations = getGroupRelationHome().findAllPendingGroupRelationships();
				
				Iterator iter = relations.iterator();
				IWTimestamp stamp = IWTimestamp.RightNow();
				while (iter.hasNext()) {
					GroupRelation relation = (GroupRelation) iter.next();
					if (relation.isActivePending()) {
						IWTimestamp whenToActivate = new IWTimestamp(relation.getInitiationDate());
						if (whenToActivate.isEarlierThan(stamp)) { //activate now
							relation.setActive();
							//relation.setInitiationDate(stamp.getTimestamp());
							relation.store();
						}
					}
					else if (relation.isPassivePending()) {
						IWTimestamp whenToPassivate = new IWTimestamp(relation.getTerminationDate());
						if (whenToPassivate.isEarlierThan(stamp)) { //passivate now
							relation.setPassive();
							//relation.setTerminationDate(stamp.getTimestamp());
							relation.store();
						}
					}
				}
			}
		}
		catch (Exception x) {
			x.printStackTrace();
		}
		try {
			if(event.getActionCommand().equalsIgnoreCase(GROUP_TREE_TIMER_THREAD_NAME)){
				//System.out.println("[USER]: fetching grouptree "+IWTimestamp.RightNow());
				GroupBusiness business = getGroupBusiness(bundle.getApplication().getIWApplicationContext());
				business.refreshGroupTreeSnapShotInANewThread();
				int fetchGroupTreeInterval = Integer.parseInt(bundle.getProperty(BUNDLE_PROPERTY_NAME_FETCH_GROUPTREE_INTERVAL, String.valueOf(EventTimer.THREAD_SLEEP_5_MINUTES)));
				//System.out.println("[USER]: interval "+fetchGroupTreeInterval);
				groupTreeEventTimer.setInterval(fetchGroupTreeInterval);
			}
		}
		catch (Exception e1) {
			e1.printStackTrace();
		}
	}
	
	/**
	 * @see com.idega.idegaweb.IWBundleStartable#stop(IWBundle)
	 */
	public void stop(IWBundle starterBundle) {
		if (timer != null) {
			timer.stop();
			timer = null;
		}
		if (groupTreeEventTimer != null) {
			groupTreeEventTimer.stop();
			groupTreeEventTimer = null;
		}
	}
	
	public GroupBusiness getGroupBusiness(IWApplicationContext iwc) {
		if (groupBiz == null) {
			try {
				groupBiz = (GroupBusiness) com.idega.business.IBOLookup.getServiceInstance(iwc, GroupBusiness.class);
			}
			catch (java.rmi.RemoteException rme) {
				throw new RuntimeException(rme.getMessage());
			}
		}
		return groupBiz;
	}

	private GroupRelationHome getGroupRelationHome() throws RemoteException{
		return getGroupBusiness(bundle.getApplication().getIWApplicationContext()).getGroupRelationHome();
	}
}