package com.idega.user.business;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
	}
	
	public void actionPerformed(ActionEvent event) {
		try {
			Collection relationsWithoutRelatedGroupType = getGroupRelationHome().findAllGroupsWithoutRelatedGroupType();
			Iterator iterWithoutRelGroupType = relationsWithoutRelatedGroupType.iterator();
			while (iterWithoutRelGroupType.hasNext()) {
				GroupRelation relation = (GroupRelation) iterWithoutRelGroupType.next();
				relation.setRelatedGroupType(relation.getRelatedGroup().getGroupType());
  				relation.store();
			}	
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
	}
	
	/**
	 * @see com.idega.idegaweb.IWBundleStartable#stop(IWBundle)
	 */
	public void stop(IWBundle starterBundle) {
		if (timer != null) {
			timer.stop();
			timer = null;
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

	private GroupRelationHome getGroupRelationHome() {
		return getGroupBusiness(bundle.getApplication().getIWApplicationContext()).getGroupRelationHome();
	}
}