package com.idega.presentation.ui;

import com.idega.core.ICTreeNode;
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
        
  private Text textProxyForSelectedNode;
    
  public void setSelectedNode(Text textProxyForSelectedNode) {
    this.selectedNodeId = selectedNodeId;
  }
    
  public void setTextProxyForSelection(Text textProxyForSelectedNode) {
    this.textProxyForSelectedNode = textProxyForSelectedNode;
  }
    
  public void main(IWContext iwc) throws Exception {
    setNodeActionParameter(SELECTION_KEY);
    if (iwc.isParameterSet(SELECTION_KEY))  {
      try {
        selectedNodeId = Integer.parseInt(iwc.getParameter(SELECTION_KEY));
      }
      catch (NumberFormatException ex)  {
        selectedNodeId = -1;
      }
    }
    if (selectedNodeId != -1)  {
      addOpenCloseParameter(SELECTION_KEY, Integer.toString(selectedNodeId));
    }
    super.main(iwc);
  }
  
  /* (non-Javadoc)
   * @see com.idega.presentation.ui.TreeViewer#getSecondColumnObject(com.idega.core.ICTreeNode, com.idega.presentation.IWContext, boolean)
   */
  public PresentationObject getSecondColumnObject(ICTreeNode node, IWContext iwc, boolean fromEditor) {
    Link link = (Link) super.getSecondColumnObject( node, iwc, fromEditor);
    if ( selectedNodeId == node.getNodeID()) {
      String name = node.getNodeName();
      Text text = getProxyForSelectedNode();
      text.setText(name);
      link.setText(text);
    }
    return link;
  }
    
  private Text getProxyForSelectedNode()  {
    if (textProxyForSelectedNode == null)   {
      textProxyForSelectedNode = new Text();
      textProxyForSelectedNode.setBold();
    }
    return (Text) textProxyForSelectedNode.clone();
  }
}