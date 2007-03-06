package com.idega.idegaweb.browser.presentation;

import java.util.HashSet;
import java.util.Set;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.idega.event.IWPresentationState;
import com.idega.event.IWPresentationStateImpl;
import com.idega.presentation.Frame;

/**
 * <p>Title: idegaWeb</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: idega Software</p>
 * @author <a href="gummi@idega.is">Gudmundur Agust Saemundsson</a>
 * @version 1.0
 */

public class IWControlFramePresentationState extends IWPresentationStateImpl implements ChangeListener {

  Set onLoadSet = new HashSet();

  public IWControlFramePresentationState() {
  }

  public void setOnLoad(String action){
    this.onLoadSet.add(action);
  }

  public void removeOnLoad(String action){
    this.onLoadSet.remove(action);
  }

  public void clearOnLoad(){
    this.onLoadSet.clear();
  }

  public Set getOnLoadSet(){
    return this.onLoadSet;
  }


  public void stateChanged(ChangeEvent e){
      Object object = e.getSource();
      // refuse objects that you can not handle
      if (! (object instanceof IWPresentationState)) {
				return;
			}
      IWPresentationState state = (IWPresentationState) object;
      String compoundId = state.getArtificialCompoundId();
      if (compoundId == null) {
				compoundId = state.getCompoundId();
			}
      String frameName = Frame.getFrameName(compoundId);
      if (frameName != null) {
				this.setOnLoad("parent.frames['" + frameName + "'].location.reload()");
//      if(location.isInFrameSet()){
//        this.setOnLoad("parent.frames['" + location.getTarget() + "'].location.reload()");
//      } else if (location.isInPopUpWindow()) {
//        //this.setOnLoad("");
//      } else{
//        // is in controlframe hence is reloading.
//      }
			}
        

    }


}