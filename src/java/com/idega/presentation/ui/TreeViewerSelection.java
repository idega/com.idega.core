package com.idega.presentation.ui;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.idega.core.data.ICTreeNode;
import com.idega.presentation.IWContext;
import com.idega.presentation.PresentationObject;
import com.idega.presentation.text.Link;
import com.idega.presentation.text.Text;

/**
 * <p>Title: idegaWeb</p>
 * <p>Description: 
 * This TreeViewer shows the selected node bold by default and - even more important -
 * maintains the selection: The selected node is kept by the tree, even if you extend or
 * minimize the tree.
 * The look of the selected node can be set by a text proxy.
 * Preselection of a node is also possible.
 * Note: 
 * This functionality is programmed without using the event model.
 * The used parameter within the request for the selection is public string SELECTION_KEY. 
 * Avoid using the methods setNodeActionParameter(String) 
 * and getSecondColumnObject(ICTreeNode, IWContext, boolean).
 * </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: idega Software</p>
 * @author <a href="thomas@idega.is">Thomas Hilbig</a>
 * @version 1.0
 * Created on Jun 16, 2003
 */
public class TreeViewerSelection extends TreeViewer {

  public static final String SELECTION_KEY = "selection_key"; 
    
  private int selectedNodeId = -1;
  
  private String selectionKey = SELECTION_KEY;
  
  private Map maintainParameterMap = new HashMap();
        
  private Text textProxyForSelectedNode;
    
  public void setSelectedNode(int selectedNodeId) {
    this.selectedNodeId = selectedNodeId;
  }
    
  public void setTextProxyForSelection(Text textProxyForSelectedNode) {
    this.textProxyForSelectedNode = textProxyForSelectedNode;
  }
  
  /** Use this method if you use more than one instance at the same time */
  public void setSelectionKey(String selectionKey)  {
    this.selectionKey = ( selectionKey == null || selectionKey.length() == 0) ? SELECTION_KEY : selectionKey;
  } 
  
  /** This method uses a copy of the specified map */
  public void maintainParameters(Map maintainParameters) {
    this.maintainParameterMap.putAll(maintainParameters);
  }
  
    
  public void main(IWContext iwc) throws Exception {
    setNodeActionParameter(this.selectionKey);
    if (iwc.isParameterSet(this.selectionKey))  {
      try {
        this.selectedNodeId = Integer.parseInt(iwc.getParameter(this.selectionKey));
      }
      catch (NumberFormatException ex)  {
        this.selectedNodeId = -1;
      }
    }
    if (this.selectedNodeId != -1)  {
      addOpenCloseParameter(this.selectionKey, Integer.toString(this.selectedNodeId));
    }
    // add maintain parameters
    Iterator iterator = this.maintainParameterMap.entrySet().iterator();
    while (iterator.hasNext())  {
      Map.Entry entry = (Map.Entry) iterator.next();
      addOpenCloseParameter((String) entry.getKey(), entry.getValue().toString());
    }
    super.main(iwc);
  }
  
  /* (non-Javadoc)
   * @see com.idega.presentation.ui.TreeViewer#getSecondColumnObject(com.idega.core.ICTreeNode, com.idega.presentation.IWContext, boolean)
   */
  public PresentationObject getSecondColumnObject(ICTreeNode node, IWContext iwc, boolean fromEditor) {
    Link link = (Link) super.getSecondColumnObject( node, iwc, fromEditor);
    // add maintain parameters
    Iterator iterator = this.maintainParameterMap.entrySet().iterator();
    while (iterator.hasNext())  {
      Map.Entry entry = (Map.Entry) iterator.next();
      link.addParameter((String) entry.getKey(), entry.getValue().toString());
    }
    if ( this.selectedNodeId == node.getNodeID()) {
      String name = node.getNodeName(iwc.getCurrentLocale(),iwc);
      Text text = getProxyForSelectedNode();
      text.setText(name);
      link.setText(text);
    }
    return link;
  }
    
  private Text getProxyForSelectedNode()  {
    if (this.textProxyForSelectedNode == null)   {
      this.textProxyForSelectedNode = new Text();
      this.textProxyForSelectedNode.setBold();
    }
    return (Text) this.textProxyForSelectedNode.clone();
  }
}