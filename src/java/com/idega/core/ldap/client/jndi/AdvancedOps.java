
package com.idega.core.ldap.client.jndi;



import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.Name;
import javax.naming.NameClassPair;
import javax.naming.NameParser;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.DirContext;

/**
 *   The AdvancedOps class extends BasicOps to allow for complex directory
 *   operations such as manipulating entire trees. <p>
 *
 *   It requires initialisation with a Directory Context, through which
 *   all the low level directory calls are passed (basicOps is a wrapper
 *   to jndi).
 *
 *   It contains a number of functions (pop(), push() and inc() )
 *   that may be over-ridden by
 *   classes derived from this that with to track progress.
 *
 */
public class AdvancedOps extends BasicOps
{
//    protected BasicOps dirOp;

    protected NameParser parser;

    private final static Logger log = Logger.getLogger("com.idega.core.ldap.client.jndi.BasicOps");


    /**
     *    Initialise the AdvancedOps object with a BasicOps object. <p>
     *    Warning: the basic ops object is used to obtain a Name Parser
     *    for the current context, which is asumed to be homogenous:
     *    AdvancedOps does *not* support tree operations across multiple
     *    name spaces.
     */

    public AdvancedOps(DirContext c)
    {
        super(c);
        this.parser = getBaseNameParser();
    }

   /**
	* 	Factory Method to create BasicOps objects, initialised
	* 	with an ldap context created from the connectionData,
	* 	and maintaining a reference to that connectionData.
	*
	* 	@param cData the details of the directory to connect to
	* 	@return an AdvancedOps object (although it must be cast to
    *    this from the BasicOps required by the method sig - is there
    *    a better way of doing this?).
	*/

	public static BasicOps getInstance(ConnectionData cData)
		throws NamingException
	{
		AdvancedOps newObject = new AdvancedOps(openContext(cData));
		newObject.setConnectionData(cData);
		return newObject;
	}

    /**
     * overload this method for progress tracker.
     */

    public void open(String heading, String operationName) {}

    /**
     * overload this method for progress tracker.
     */

    public void close() {}

    /**
     * overload this method for progress tracker.
     */

    public void pop() {}

    /**
     * overload this method for progress tracker.  Note that elements
     * is passed to allow determination of the number of objects - but
     * the Enumeration must be returned without being reset, so be carefull
     * when using it...
     */

    public NamingEnumeration push(NamingEnumeration elements) {return elements;}

    /**
     * overload this method for progress tracker.
     */

    public void inc() {}

    /*
     *
     *    TREE FUNCTIONS
     *
     */

    public boolean deleteTree(Name nodeDN)        // may be a single node.
    {
        log.log(Level.FINER,"recursively delete Tree " + nodeDN.toString());

        open("Deleting " + nodeDN.toString(), "deleted ");
        boolean ret = recDeleteTree(nodeDN);
        close();
        return ret;
    }

    /**
     *   deletes a subtree by recursively deleting sub-sub trees.
     *
     *   @param dn the distinguished name of the sub-tree apex to delete.
     *   @return true if the tree was deleted, false otherwise.
     */

    public boolean recDeleteTree(Name dn)
    {
        try
        {
            NamingEnumeration children = list(dn);

            if (children!=null && children.hasMore())
            {
                children = push(children);         // inform progress tracker that we're going down a level.

                while (children.hasMoreElements())
                {
                    String subDN = ((NameClassPair)children.next()).getName();
                    if (recDeleteTree(this.parser.parse(subDN))==false) {
						return false;    // ... this also abandons updating progress bar
					}
                }
                pop();              // inform progress tracker that we've come up.
            }
            deleteObject(dn);

            inc();               // inform progress tracker that we've deleted an object.
            return true;
        }
        catch (NamingException e)
        {
            error("Failed to remove tree", e);
            close();
            return false;
        }

    }

    /*
     *
     *    MOVE TREE FUNCTIONS
     *
     */

   /**
    *    Moves a DN to a new DN, including all subordinate entries.
    *    (nb it is up to the implementer how this is done; e.g. if it is an
    *     ldap broker, it may choose rename, or copy-and-delete, as appropriate)
    *
    *    @param nodeDN the original DN of the sub tree root (may be a single
    *           entry).
    *    @param progBarDisplayer (may be null) - a Component within which to display
    *           a progress tracker if the operation is taking a long time...
    *    @return the operation's success status
    */

    public boolean moveTree(Name oldNodeDN, Name newNodeDN)       // may be a single node.
    {
    		log.log(Level.FINER,"recursively move tree from " + oldNodeDN.toString() + " to " + newNodeDN.toString());

        open("Moving " + oldNodeDN.toString(), "moving");
        boolean ret = recMoveTree(oldNodeDN, newNodeDN);

        close();
        return ret;
    }

    /**
     *    <p>Moves a tree.  If the new position is a sibling of the current
     *    position a <i>rename</i> is performed, otherwise a new tree must
     *    be created, with all its children, and then the old tree deleted.<p>
     *
     *    <p>If the new tree creation fails during creation, an attempt is made
     *    to delete the new tree, and the operation fails.  If the new tree
     *    creation succeeds, but the old tree deletion fails, the operation
     *    fails, leaving the new tree and the partial old tree in existence.
     *    (This last should be unlikely.)</p>
     *
     *    <p>This move *deletes* the old value of the RDN when the node is
     *    moved.</p>
     *
     *    @param the root DN of the tree to be moved
     *    @param the root DN of the new tree position
     *    @return the success status of the operation.
     */

    public boolean recMoveTree(Name from, Name to)   // may be a single node.
    {
        if (from.size() == to.size() && from.startsWith(to.getPrefix(to.size()-1))) // DNs are siblings...
        {
            return renameObject (from, to, true);
        }
        else                               // DNs are not siblings; so copy them
        {                                  // from tree, and then delete the original

            //TE:   does the 'from' DN exist? What if someone gets the DNs around the wrong way?  For example
            //      in JXweb a user can enter the DN of where to move from & to...what if they, by mistake,
            //      make the 'to' field the 'from' field?  The actual real data will be deleted b/c the copy will
            //      fail due to the 'from' DN not existing and this will fall through to recDeletTree(to)!
            if(!exists(from))
            {
                error("The DN that you are trying to move does not exist.", null);
                return false;
            }

            if (recCopyTree(from, to)==true)
            {
                if (recDeleteTree(from)==true)
                {
                    return true;          // EXIT ON SUCCESS
                }
            }
            else
            {
                recDeleteTree(to);  // Try to clean up
            }
        }
        return false;
    }

    /*
     *
     *    COPY TREE FUNCTIONS
     *
     */


   /**
    *    Copies a DN representing a subtree to a new subtree, including
    *    copying all subordinate entries.
    *
    *    @param oldNodeDN the original DN of the sub tree root
    *           to be copied (may be a single entry).
    *    @param newNodeDN the target DN for the tree to be moved to.
    *    @param progBarDisplayer (may be null) - a Component within which to display
    *           a progress tracker if the operation is taking a long time...
    *    @return the operation's success status
    */

    public boolean copyTree(Name oldNodeDN, Name newNodeDN)       // may be a single node.
    {
    		log.log(Level.FINER,"recursively copy tree from " + oldNodeDN.toString() + " to " + newNodeDN.toString());

        open("Copying " + oldNodeDN.toString(), "copying");
        boolean ret = recCopyTree(oldNodeDN, newNodeDN);


        close();
        return ret;
    }


    /**
     *    Takes two DNs, and goes through the first, copying each element
     *    from the top down to the new DN.
     *
     *    @param from the ldap Name dn to copy the tree from
     *    @param to the ldap Name dn to copy the tree to
     *    @return true if the operation was successfull, false otherwise
     */

    public boolean recCopyTree(Name from, Name to)
    {
        if (copyObject(from,to)==false)
        {
            return false;
        }

        try
        {
            NamingEnumeration children = list(from);

            if (children.hasMoreElements())
            {
                children = push(children);

                while (children.hasMoreElements())
                {
                    String subDNString = ((NameClassPair)children.next()).getName();
                    Name subDN = this.parser.parse(subDNString);
                    String childDN = subDN.get(subDN.size()-1);

                    if (recCopyTree(this.parser.parse(childDN+","+from.toString()), this.parser.parse(childDN+","+to.toString()))==false)
                    {
                        recDeleteTree(this.parser.parse(childDN+","+to.toString()));  // error! - try to clean up
                        return false;              // and return false...(abandoning pbar updates)
                    }
                }
                pop();
            }
            inc();
            return true;
        }
        catch (NamingException e)
        {
            error("Failed to copy tree", e);
            close();
            return false;
        }
    }

}