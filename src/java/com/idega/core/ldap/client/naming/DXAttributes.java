
package com.idega.core.ldap.client.naming;

import java.util.Enumeration;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Vector;

import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;

import com.idega.core.ldap.client.cbutil.CBArray;
import com.idega.core.ldap.client.cbutil.CBUtility;
import com.idega.core.ldap.client.jndi.SchemaOps;

/**
 *    This class is a container for a collection of
 *    Attribute objects.  It is schema aware, and will
 *    search schema to find a complete set of attributes
 *    for its component objectClass(s).<p>
 *
 *    The class is built around a hashtable of id/attribute
 *    pairs.
 */

// nb this class must work with both oid's (2.3.1.2.3.1.2.41.23.1.pi.2.e.34.phi)
// and ldap names ('c').  Ideally it should get the long form as well
// (i.e. 'country') but the server doesn't always oblige...
// I try to document the difference by referring to ids as either oids or ldap ids.
// if the distinction isn't obvious, it's probably an ldap id (i.e. a string name)

// XXX a lot of effort is gone to to trim out ';binary' from the names of
// XXX ldap attributes.  THis is a grubby hack, and handling should be improved
// XXX somehow.

public class DXAttributes implements Attributes
{

    static Hashtable attOIDs = new Hashtable(100);  // a global hashset of all attribute oid-s and corresponding ldap descr names known to the program...

    static int ID = 0;

    int id; // unique debug id.

    Hashtable atts;                 // a hashtable of attribute id/ attribute object values, keyed by lowercase id.

    HashSet must;                   // a hashset of attribute ldap ids that *must* be entered
                                    // - set in checkSchema()

    boolean ignoreCase = true;      // whether attribute IDs are case sensitive

    boolean schemaChecked = false;  // indicates a schema search has been made,
                                    // and a full list of objectclass(s) attribute obtained
    Attribute allObjectClasses;     // a list of allObjectClasses, including parents.

    //String baseObjectClass = null;  // the deepest ObjectClass.

    String objectClassName = null;  // the current name of 'objectclass' or 'objectClass'

    Vector orderedSOCs = new Vector(); // structural object classes, in deepest-first order


    static SchemaOps schema;              // the schema context

    static Hashtable knownParents = new Hashtable(30);      // hash of DXAttributes containing the known object class parents for a particular object class subset (e.g. 'inetorgperson' -> 'orgperson','person','top')
    static Hashtable knownSubSets = new Hashtable(30);      // hash of known object class subsets
    static Hashtable objectClassDepths = new Hashtable(30); // hash of known object class 'depths' in object class inheritance tree



    static
    {
        objectClassDepths.put("top", new Integer(0));

        // pre set 'synthetic' schema attributes
        objectClassDepths.put("schema", new Integer(1));
        objectClassDepths.put("AttributeDefinition", new Integer(2));
        objectClassDepths.put("ClassDefinition", new Integer(2));
        objectClassDepths.put("SyntaxDefinition", new Integer(2));

    }

    // common code run by all the more basic constructors.
    void basicInit()
    {
        id = ID++;

        atts = new Hashtable();
        must = new HashSet();

//        schema = null;
    }

    /**
     *    Initialises an empty set of attributes with no schema
     */
    public DXAttributes()
    {
        basicInit();
    }

    /**
     *    Initialises a set of attributes with a single attribute
     */

    public DXAttributes(Attribute a)
    {
        basicInit();
        put(a);
    }

    /**
     *    Copies an existing Attributes object into a DXAttributes
     *    object.
     */

    public DXAttributes(Attributes a)
    {

        if (a==null)
        {
            atts = new Hashtable();
            must = new HashSet();
        }
        else
        {
            atts = new Hashtable(a.size()+10);
            must = new HashSet(a.size());   // overkill, but what the heck...
            
            Enumeration e = a.getAll();
            while (e.hasMoreElements())
            {
                DXAttribute newAtt = new DXAttribute((Attribute)e.nextElement());
                put (newAtt);
            }
        }
    }

    /**
     *    Initialises a set of DXattributes using
     *    a Hashtable of existing id/attribute pairs...
     *    @param newAtts hashtable of id/attribute pairs to
     *           initialise the new DXAttributes object
     */

    public DXAttributes(Hashtable newAtts)
    {
        atts = (Hashtable) newAtts.clone();
    }


    /**
     *    Initialises a set of DXattributes using
     *    a NamingEnumeration of existing attribute(s).
     *    @param newAtts namingenumeration of attributes to
     *           initialise the new DXAttributes object
     */

    public DXAttributes(NamingEnumeration newAtts)
    {
        atts = new Hashtable();
        while (newAtts.hasMoreElements())
        {
            Attribute current = (Attribute) newAtts.nextElement();
            atts.put(current.getID().toLowerCase(), current);
        }
    }


    /**
     *    This sets the standard schema to use while this connection is open.
     *    (It may be possible in future releases to set schema on a per-Attribute
     *    basis - it is not clear yet whether this would be useful.)
     */
    public static void setDefaultSchema(SchemaOps defaultSchema)
    {
        schema = defaultSchema;
    }


    public int getID() { return id; }

    /**
     *    creates a new DXAttributes, copying each Attribute object.
     *    @return a new DXAttributes object
     */

    public Object clone()
    {
        return new DXAttributes(atts);
    }

    /**
     *    gets an Attribute specified by its ID
     *    @param attrID the ID of the attribute to retrieve
     *    @return the specified attribute (actually a DXAttribute)
     *            - null if it can't be found.
     */

    public Attribute get(java.lang.String attrID)
    {
        Attribute ret = (Attribute) atts.get(attrID.toLowerCase());
        if (ret==null)
        {
            ret = (Attribute) atts.get(attrID.toLowerCase() + ";binary");
        }

        return ret;
    }

    /**
     *    returns all the Attribute(s) in this DXAttributes object.
     *    - the NamingEnumeration is (evily) pre-sorted alphabetically...
     *    @return enumeration of all stored Attribute objects
     */
    public NamingEnumeration getAll()
    {
        return (new DXNamingEnumeration (atts.elements())).sort();
    }

    /**
     *    For convenience and display, DXAttributes objects have a complete
     *    list of all attribute(s) that the objectclass(s) represented might
     *    possibly contain.  However sometimes we need an <b>Attributes</b>
     *    object that has no null-valued attributes (i.e. when adding it to
     *    the directory).
     *    @return an Attributes object containing no null valued attribute(s).
     */
    public Attributes getAsNonNullAttributes()
    {
        return new DXAttributes(getAllNonNull());
    }

    /**
     *    returns all the Attribute(s) that have non-null values
     *    in this DXAttributes object.
     *    - the NamingEnumeration is (evily) pre-sorted alphabetically...<p>
     *
     *    Warning: whether an attribute is added is undefined if the
     *    attribute is multi-valued and contains one or more null values in
     *    addition to other non-null values.
     *
     *    @return enumeration of all stored Attribute objects with non-null values
     */

    public NamingEnumeration getAllNonNull()
    {
        DXNamingEnumeration returnEnumeration = new DXNamingEnumeration ();
        Enumeration allatts = getAll();

        while (allatts.hasMoreElements())
        {
            Attribute fnord = (Attribute) allatts.nextElement();
            if (fnord != null) // should never happen...
            {
                try
                {
                    if (fnord.get() != null)            // if there is at least one non-null value...
                        returnEnumeration.add(fnord);   // add it to the list
                }
                catch (NoSuchElementException e)        // 'expected' exception (love that jndi)
                {

                }
                catch (NamingException e2)
                {
                    CBUtility.log("whoops: Naming Exception reading " + fnord.getID(),0);
                }
            }
        }

        returnEnumeration.sort();

        return returnEnumeration;
    }

    /**
     *    returns all the mandatory 'MUST' have Attribute(s) in this DXAttributes object.
     *    - the NamingEnumeration is (evily) pre-sorted alphabetically...
     *    @return enumeration of all stored Attribute objects
     */
    public NamingEnumeration getMandatory()
    {
        DXNamingEnumeration returnEnumeration = new DXNamingEnumeration ();

        if (must==null) return returnEnumeration;  // return empty enumeration if not initialised...

        Iterator musts = must.iterator();
        while (musts.hasNext())
        {
            String s = (String)musts.next();
            returnEnumeration.add(get(s));
        }
        returnEnumeration.sort();

        return returnEnumeration;
    }



   /**
    *   Returns the list of mandatory attribute IDs (HashSet:must).
    *   @return the list of mandatory attribute IDs (e.g. objectClass, cn, sn).
    */

    public HashSet getMandatoryIDs()
    {
        return must;
    }



    /**
     *    returns all the optional 'MAY' Attribute(s) in this DXAttributes object.
     *    - the NamingEnumeration is (evily) pre-sorted alphabetically...
     *    @return enumeration of all stored Attribute objects
     */
    public NamingEnumeration getOptional()
    {
        DXNamingEnumeration returnEnumeration = new DXNamingEnumeration ();
        Enumeration allIDs = atts.keys();
        while (allIDs.hasMoreElements())
        {
            String id = (String) allIDs.nextElement();
            if (must.contains(id)==false)    // if it's *not* mandatory
                returnEnumeration.add(get(id)); // add it to the optional list
        }
        returnEnumeration.sort();

        return returnEnumeration;
    }



    /**
     *    returns all the attribute IDs held in this DXAttributes object.
     *    @return all attribute IDs
     */

    public NamingEnumeration getIDs()
    {
        // cannot simply return hash keys, as they are standardised to lower case.
        DXNamingEnumeration ret = new DXNamingEnumeration();
        NamingEnumeration allAtts = getAll();
        while (allAtts.hasMoreElements())
            ret.add(((Attribute)allAtts.nextElement()).getID());

        return ret;

        //return (NamingEnumeration)(new DXNamingEnumeration (atts.keys()));
    }

    /**
     *    returns whether attribute IDs are stored in a case
     *    sensitive manner; i.e. whether 'person' is different
     *    from 'Person'.  The default is <i>false</i>, implying
     *    case sensitivity.
     *    @return whether case is ignored for attribute IDs
     */

    public boolean isCaseIgnored()
    {
        return ignoreCase;
    }

    /**
     *    adds an attribute to the DXAttributes collection,
     *    using the attribute.getID() method to find a key.
     *    NB: doco seems unclear on whether this adds to,
     *    or replaces, any existing attribute with the same
     *    ID.  At the moment this <b>replaces</b> the values...
     *    @param attr the attribute to add
     *    @return the previous attribute (if any) with this key.
     */

    public Attribute put(Attribute attr)
    {
        if (attr == null) return null; // sanity check - can't add a null attribute...

        Attribute old = get(attr.getID().toLowerCase());
        schemaChecked = false;

        if (old!=null)
        {
            atts.remove(old.getID().toLowerCase()); // code for *replacing* existing attribute values
        }

        String ID = attr.getID().toLowerCase();
        if (attr instanceof DXAttribute)
            atts.put(ID, attr);
        else
            atts.put(ID, new DXAttribute(attr));

        return old;
    }

    /**
     *    creates an attribute with the specified values and adds it
     *    to the DXAttributes collection,
     *    using the attrID string as a key.
     *    NB: doco seems unclear on whether this adds to, or replaces,
     *    an existing attribute with the same ID.  This implementation
     *    <b>adds</b> the new object value...
     *
     *    @param attrID the String ID of the newly added attribute
     *    @param val the value to associate with the ID for the newly
     *               created attribute
     *    @return the previous attribute (if any) with this key.
     */

    public Attribute put(java.lang.String attrID, java.lang.Object val)
    {
        schemaChecked = false;
        return put(new DXAttribute(attrID.toLowerCase(), val));
    }

    /**
     *    Adds an Enumeration of Attribute(s) to a DXAttribute
     *
     *    @param attributeList the list of attributes to add.
     */

    public void put(Enumeration attributeList)
    {
        while (attributeList.hasMoreElements())
        {
            Attribute a = (Attribute)attributeList.nextElement();
            if (a instanceof DXAttribute)
                put(a);
            else
                put(new DXAttribute(a));
        }
    }

    /**
     *    removes the attribute containing this key (if any).
     *    @param attrID the ID of the attribute to remove
     *    @return the removed attribute (if any)
     */

    public Attribute remove(java.lang.String attrID)
    {
        schemaChecked = false;
        return (Attribute) atts.remove(attrID.toLowerCase());
    }

    /**
     *    returns the number of Attribute objects held.
     *
     *    @return number of attribute objects
     */
    public int size()
    {
        return atts.size();
    }

    /**
     *    searches the schema for all the possible attributes that the
     *    objectClasses present *could* have, and adds new
     *    attributes that are not already present to the list
     *    of current Attribute objects as empty attributes.
     */
/*
    public void checkSchema()
    {

        if ((schema == null)|| (schemaChecked==true)) return; // nothing to do

        try
        {
                // update the hashtable with complete list
                // of attributes as read from schema
            expandAllAttributes(getAllObjectClasses());

            //print();
        }
        catch (NamingException e)
        {
            CBUtility.log("Naming Exception in Schema Read of DXAttribute\n    " + e);
        }
        schemaChecked = true;

    }
*/
/*
    public String getBaseObjectClass()
    {
        if (baseObjectClass == null)
        {
            setAllObjectClasses();
            if (baseObjectClass == null) // Still?  Must be an error somewhere...
                return "top";
        }

        return baseObjectClass;
    }
*/
    public void setAllObjectClasses()
    {
        allObjectClasses = getAllObjectClasses();
    }

    /**
     *    Return a list of object classes as a vector from deepest (at pos 0) to 'top' (at pos (size()-1) ).
     */
    public Vector getOrderedOCs()
    {
        Vector ret = null;
        try
        {
            Attribute oc = getAllObjectClasses();
            if (oc == null) 
                return null;
                
            ret = new Vector(oc.size());
            NamingEnumeration vals = oc.getAll();
            while (vals.hasMore())
            {
                ret.add(vals.next());
            }
            return ret;
        }
        catch (NamingException e)
        {
            CBUtility.log("Yet another rare naming exception - DXAttributes:getOrderedOCs " + e);
            return new Vector(0);
        }
    }

    public Attribute getAllObjectClasses()
    {
		Attribute att = get("objectclass");
        
		if (att != null)	//TE: check that the attribute is set.
		{
	        if (att instanceof DXAttribute)
	            return getAllObjectClasses((DXAttribute)att);
	        else
	            return getAllObjectClasses(new DXAttribute(att));			
		}
		return null;	//TE: return null if att is null.
    }

    /**
     *    Some directories don't include all of an entry's object classes,
     *    but only the lowest level ones.  This looks up all the parents
     *    the low level object classes are inherited from and, forms a
     *    complete ordered list of all object classes for this Attributes object.
     *
     *    @return an Attribute containing the names of all the object classes...
     */

    // XXX objecClass should no longer be case sensitive.  When happy this works,
    // XXX delete code below.

    public static DXAttribute getAllObjectClasses(DXAttribute oc)
    {
        if (oc==null) return null; // no object classes (may be virtual entry such as DSA prefix)

        if (knownSubSets.containsKey(oc))
            return(DXAttribute) knownSubSets.get(oc);

        try
        {
            DXAttribute orig = new DXAttribute(oc);

            Enumeration vals = oc.getAll();
            while (vals.hasMoreElements())
            {
                Attribute parents = getParentObjectClasses((String)vals.nextElement());
                if (parents != null)
                {
                    Enumeration parentVals = parents.getAll();
                    while (parentVals.hasMoreElements())
                    {
                        String parent = (String) parentVals.nextElement();
                        if (oc.contains(parent) == false)
                        {
                            oc.add(parent);
                        }
                    }
                }
            }

            DXAttribute fullOC = sortOCByDepth(oc);
            knownSubSets.put(orig, fullOC);
            return fullOC;
        }
        catch (NamingException e)
        {
            CBUtility.log("NamingException in getAllObjectClasses " + e);
            return oc;
        }
    }

    /**
     *    Takes a list of *all* object class values, and returns them
     *    sorted by depth.  This requires the objectClassDepths hashtable
     *    to be set (which is done by getParentObjectClasses() ).
     */

    protected static DXAttribute sortOCByDepth(Attribute oc)
    {
        DXAttribute ret = new DXAttribute("objectClass");
        ret.setOrdered(true);

        try
        {
            Enumeration vals = oc.getAll();
            while (vals.hasMoreElements())
            {
                String val = (String) vals.nextElement();
                Integer myInt = (Integer)objectClassDepths.get(val);

                if (myInt == null) // shouldn't happen (if schema is correct), but in case we missed one...
                {
                	getParentObjectClasses(val);  // try again to set the objectClassDepths hash for this value. (probably won't work).
                	myInt = (Integer)objectClassDepths.get(val); // and try to reget the value.
                	if (myInt == null) 			  // if still null, give up and set to zero.
                		myInt = new Integer(0);
                }
                int depth = myInt.intValue();
                int i;
                for (i=ret.size()-1; i>=0; i--)
                {
                    int existing = ( (Integer)objectClassDepths.get(ret.get(i)) ).intValue();
                    if (  existing >= depth)
                    {
                        ret.add(i+1, val);
                        break;
                    }
                }
                if (i == -1)
                    ret.add(0, val);
            }
            return ret;
        }
        catch (NamingException e)
        {
            CBUtility.log("Naming Exception in DXAttributes sortOCByDepth()" + e);
            return new DXAttribute(oc);
        }
    }



    /**
     *    recursively builds up a complete ordered list of all the parents of a particular
     *    object class (including the child object class) from schema.
     *
     *    @param childOC the child Object Class to search for parents of
     *    @return an attribute containing the child class and all parents
     */

    public static DXAttribute getParentObjectClasses(String childOC)
        throws NamingException
    {
        if (schema == null) // in the absence of a schema, everything is at level '1', just below 'top'
        {
            objectClassDepths.put(childOC, new Integer(1));
            return null;
        }

        if ("schema attributedefinition classdefinition syntaxdefinition matchingrule".indexOf(childOC.toLowerCase()) != -1) return null;  // don't bother looking up synthetic object classes.

        if (knownParents.containsKey(childOC))
        {
            return (DXAttribute) knownParents.get(childOC);
        }

        DXAttribute parents = new DXAttribute("objectClass");  // this is the attribute returned

        String schemaParent = null;
        try
        {
            //schemaParents = schema.getAttributes("ClassDefinition/" + childOC, new String[] {"SUP"});
            Attributes schemaDef = schema.getAttributes("ClassDefinition/" + childOC);
            if (schemaDef!=null)
            {
                Attribute sup = schemaDef.get("SUP");
                if (sup!=null)
                    schemaParent = (String)sup.get();
            }
        }
        catch (NamingException e) // easily throws a name-not-found exception
        {
			CBUtility.log("Possible Schema Error: class definition for " + childOC + " could not be found");
			objectClassDepths.put(childOC, new Integer(1));  // BAD SCHEMA! NO BISCUIT!  Set to one 'cause we don't know true depth (and top is always zero).

            return null;  // do nothing
        }

        // TODO: this is silly; why don't we just reuse the DXAttribute object returned?
        // XXX - no time to fix now; maybe later...

        if (schemaParent != null) // may not: e.g. 'top' has no parent
        {
            DXAttribute oc = getParentObjectClasses(schemaParent);                // recurse -> this should also set the depth in objectClassDepths

            if (oc != null)
            {
                Enumeration vals = oc.getAll();
                while (vals.hasMoreElements())                 // load up the return attribute
                {
                    parents.add(vals.nextElement());           // (o.k. to add the same value twice...)
                }
            }

            int depth = ((Integer)objectClassDepths.get(schemaParent)).intValue();
            if (objectClassDepths.containsKey(childOC) == false)
            {
                objectClassDepths.put(childOC, new Integer(depth+1));
            }
            else
            {
                int oldDepth = ((Integer)objectClassDepths.get(childOC)).intValue();
                if (oldDepth <= depth)
                    objectClassDepths.put(childOC, new Integer(depth+1));
            }
        }
        else  // no schemaParents
        {
			objectClassDepths.put(childOC, new Integer(1));  // BAD SCHEMA! NO BISCUIT!  Set to one 'cause we don't know true depth (and top is always zero).
            //schemaParents = null; // so store a blank place holder
        }

        parents.add(childOC);     // *** Note Cunning Recursive Magic - this is where base stuff actually gets added to parents... ***

        knownParents.put(childOC, parents);

        return parents;
    }

    /**
     *    Get structural object classes, in deepest oc first order.
     *    @return vector of structural OCs (may be null if can't be resolved)
     */
/*
    public Vector getOrderedStructOCs()
    {
        try
        {
            if (orderedSOCs.size() != 0) return orderedSOCs;

            if (baseObjectClass == null) setAllObjectClasses();

            setOrderedSOCs(baseObjectClass);

            return orderedSOCs;
        }
        catch (Exception e)
        {
            return orderedSOCs;
        }
    }
*/
    /**
     *    Sets the vector of structural object classes
     *    in this attribute set.
     *    @param oc the object class to find (and add) the structural parents of.
     */

    void setOrderedSOCs(String oc)
        throws NamingException
    {
        orderedSOCs.add(oc);

        if (oc.equalsIgnoreCase("top")) return;  // recursive search finished.


        String parent = schema.schemaLookup("ClassDefinition/" + oc, "SUP");
        String struct = schema.schemaLookup("ClassDefinition/" + parent, "STRUCTURAL");
        // try to figure out if that was a structural object class...
        if ("true".equalsIgnoreCase(struct) )
        {
            setOrderedSOCs(parent);  // recurse to next set of parents.
            return;              // finished.
        }

/*
        Attributes parents = null;
        parents = schema.getAttributes("ClassDefinition/" + oc, new String[] {"SUP"});  // returns one attribute
        NamingEnumeration parentList = parents.get("SUP").getAll();    // get that attribute's values.
        while (parentList.hasMore())
        {
            String id = parentList.next().toString();
            Attributes struct = schema.getAttributes("ClassDefinition/" + id, new String[] {"STRUCTURAL"});
            // try to figure out if that was a structural object class...
            if (struct != null && struct.get("STRUCTURAL") != null && ("true".equalsIgnoreCase(struct.get("STRUCTURAL").get().toString())) )
            {
                setOrderedSOCs(id);  // recurse to next set of parents.
                return;              // finished.
            }
        }
*/
    }


    /**
     *    Adds an OID / ldapName combination to the hashtable
     *    of all oid / ldapNames.<p>
     *
     *    If the oid is actually an ldap String (different servers
     *    return different things...) it doesn't bother registering
     *    the string...
     *
     *    @param oid the numeric oid to register
     *    @param ldapName the name of the corresponding ldap descr
     *    @return true if new oid was registered, false if it was already known
     */
    public boolean registerOID(String oid, String ldapName)
    {
        // Don't register non-oids...
        char test = oid.charAt(0);
        if (test < '0' || test > '9') // not an oid
        {
            return false;
        }

        // XXX ;binary hack.
        if (oid.endsWith(";binary")) oid = oid.substring(0,oid.indexOf(";binary"));
        if (ldapName.endsWith(";binary")) ldapName = ldapName.substring(0,ldapName.indexOf(";binary"));

        if (attOIDs.contains(oid)==true) return false;
        attOIDs.put(oid, ldapName);    // add it to the list...
        return true;
    }

    /**
     *    This method trims all empty attributes (attributes without values) from
     *    the DXAttributes object.
     */

    public void removeEmptyAttributes()
    {
        Enumeration atts = getAll();
        while (atts.hasMoreElements())
        {
            Attribute att = (Attribute)atts.nextElement();
            if (att.size() == 0)
                remove(att.getID());
        }
    }




    /**
     *    Sets the internal list of all attribute IDs needed for
     *    a given set of object classes, as well as noting which
     *    are mandatory and which optional.<p>
     *
     *    As an added wrinkle, this must be able to cope with attribute
     *    ID's expressed either as ldap strings, or as numeric OIDs.  It
     *    does this by automatically detecting OIDs, and translating them
     *    to strings using schema lookups.<p>
     *
     *    This method uses the schema to create empty valued attributes for
     *    attributes which don't currently exist, but which are allowed.
     *
     */

    public void expandAllAttributes()
    {
        if (schema == null) return;

        Attribute oc = null;
        oc = getAllObjectClasses();

        try
        {
        // Quick Hack to eliminate 'fake attributes' used for top level of syntaxes...
        //XXX Might want to redo if efficiency is ever a concern :-)

            if (oc.contains(SchemaOps.SCHEMA_FAKE_OBJECT_CLASS_NAME) )
                return;  // ignore the synthetic 'schema' object classes...

            NamingEnumeration ocs = oc.getAll();

            // cycle through all object classes, finding attributes for each
            while (ocs.hasMore())
            {
                String objectClass = (String)ocs.next();
                Attributes ocAttrs = schema.getAttributes("ClassDefinition/" + objectClass);
                Attribute mustAtt = ocAttrs.get("MUST");  // get mandatory attribute IDs
                Attribute mayAtt  = ocAttrs.get("MAY");   // get optional attribute IDs

                if (mustAtt != null)
                {
                    NamingEnumeration musts = mustAtt.getAll();
                    while (musts.hasMore())
                    {
                        String attOID = (String) musts.next();

                        //XXX binary hack
                        if (attOID.indexOf(";binary")>0) attOID = attOID.substring(0,attOID.indexOf(";binary"));

                        String ldapName = getldapName(attOID);

                        registerOID(attOID, ldapName);
                        if (get(ldapName)==null)                                  // if we don't already have this attribute...
                        {
                            put(new DXAttribute(getldapName(attOID)));   // ... add it to the list
                            //CB empty atts now. put(new DXAttribute(getldapName(attOID), null));   // ... add it to the list
                        }

                        if (must.contains(ldapName.toLowerCase())==false)            // and if it isn't already mandatory
                        {
                            must.add(ldapName.toLowerCase());                        // ... add it to the mandatory list as well
                        }
                    }
                }

                if (mayAtt != null)
                {
                    NamingEnumeration mays = mayAtt.getAll();
                    while (mays.hasMore())
                    {
                        String attOID = (String) mays.next();
                        //XXX binary hack
                        if (attOID.indexOf(";binary")>0) attOID = attOID.substring(0,attOID.indexOf(";binary"));

                        String ldapName = getldapName(attOID);
                        registerOID(attOID, ldapName);

                        if (get(ldapName)==null)   // if we don't already have this one...
                        {
                            put(new DXAttribute(getldapName(attOID)));   // ... add it to the list
                            //put(new DXAttribute(getldapName(attOID), null));   // ... add it to the list
                        }
                    }
                }
            }
        }
        catch (NamingException e)
        {
            CBUtility.log("unable to read attribute list for object classes: ");
            try
            {
                CBUtility.printEnumeration(oc.getAll());
            }
            catch (NamingException e2)
            {
                CBUtility.log("...(further error: can't print object class list)...");
            }
            CBUtility.log(" ... error is: " + e);
            return;
        }
        catch (NullPointerException e)
        {
            CBUtility.log("ERROR: unable to read list of object classes from schema - some functionality will not be available");
            StackTraceElement trace[] = e.getStackTrace();
            for (int i=0; i<trace.length; i++)
                CBUtility.log("   " + trace[i].toString());
        }
    }

    /**
     *    This method does it's darndnest to return a string ldap name.<p>
     *    First, it checks whether the string is <i>already</i> an ldap
     *    name; if it is, it returns it unchanged.<p>
     *
     *    Secondly, it tries to find the ldap text name for an oid
     *    (i.e. converts 2.5.4.0 to 'objectClass').<p>
     *
     *    Finally, if it <b>can't</b> find the name it returns the oid instead...
     *    (This shouldn't really happen, but means that the system may still work,
     *    although the raw OIDs aren't very user friendly)<p>
     */

    public String getldapName(String attOID)
    {
        return (schema==null?attOID:schema.translateOID(attOID));
/*
        try
        {
        // Don't register non-oids...
        char test = attOID.charAt(0);
        if (test < '0' || test > '9') // not an oid
        {
            return attOID;
        }


             if (attOIDs.containsKey(attOID))           // check if we already have one
                 return (String) attOIDs.get(attOID);

             // specify search constraints to search subtree
             SearchControls constraints = new SearchControls();
             constraints.setSearchScope(SearchControls.ONELEVEL_SCOPE);
             constraints.setCountLimit(1);
             constraints.setTimeLimit(0);

             String searchfilter = "(NUMERICOID="+attOID + ")";
             NamingEnumeration results = schema.search("AttributeDefinition", searchfilter, constraints);

             if (results.hasMoreElements())
             {
                 SearchResult result = (SearchResult) results.next();
                 Attributes a = result.getAttributes();           // may throw null pointer exception, caught below?
                 Attribute ldapName = (Attribute)a.get("NAME");   // may throw null pointer exception, caught below?
                 String name = (String)ldapName.get();            // may throw null pointer exception, caught below?
                 //XXX ;binary hack
                 if (name.indexOf(";binary")>0) name = name.substring(0,name.indexOf(";binary"));

                 return name;
             }
             else
                 {CBUtility.log("can't read schema for: " + attOID + "... no results"); return attOID; }

        }
        catch (Exception e)
        {
             {CBUtility.log("can't read schema for: " + attOID + "\n  " +e); return attOID; }
        }
*/
    }

    public String toString()
    {
        StringBuffer text = new StringBuffer("size (" + size() + ")\n");

        NamingEnumeration allatts = this.getAll();
        while (allatts.hasMoreElements())
        {
            Attribute fnord = (Attribute) allatts.nextElement();
            if (fnord == null)
                CBUtility.log("bizarre null attribute in element list");
            else
            {
                if (must != null && must.contains(fnord.getID()))
                    text.append("must ");

                if (fnord instanceof DXAttribute)
                    text.append("dx " + fnord.toString());
                else
                {
                    String ID = fnord.getID();
                    text.append("\n    " + ID + " (not DXAttribute)" );
                    try
                    {
                        if (fnord.size() == 0)
                            text.append("        " + " (empty) ");
                        else
                        {
                            Enumeration vals = fnord.getAll();

                            while (vals.hasMoreElements())
                            {
                                Object val = vals.nextElement();
                                String fnordel = (val==null)?"*null*":val.toString();
                                text.append("        " + fnordel);
                            }
                        }
                    }
                    catch (NamingException e)
                    {
                        CBUtility.log("whoops: Naming Exception reading " + ID + "\n " + e);
                    }
                }
            }
        }
        return text.toString();
    }

    public void print() { print(null); }

    public void print(String msg)
    {
        if (msg!=null) System.out.println(msg);
        printAttributes(this);
    }

    public static void printAttributes(Attributes a)
    {
        if (a==null) System.out.println("null attributes set");
        NamingEnumeration allatts = a.getAll();

        printAttributeList(allatts);
    }

    public static void printAttributeList(NamingEnumeration enum)
    {
        while (enum.hasMoreElements())
        {
            Attribute fnord = (Attribute) enum.nextElement();
            if (fnord == null)
                CBUtility.log("bizarre null attribute in element list");
            else
            {
                String ID = fnord.getID();
                System.out.println("    " + ID);
                try
                {
                    Enumeration vals = fnord.getAll();

                    while (vals.hasMoreElements())
                    {
                        Object val = vals.nextElement();
                        String fnordel = (val==null)?"*null*":val.toString();
                        System.out.println("        " + fnordel);
                    }
                }
                catch (NamingException e)
                {
                    CBUtility.log("whoops: Naming Exception reading " + ID + "\n " + e);
                }
            }
        }
    }



    /**
     *    <p>Returns the set of attributes that are in the newSet, but
     *    not in the old set, excluding the rdnclass).</p>
     *
     *    <p>It also returns partial attributes that contain values that
     *    are in the newSet but not in the oldSet, when either a)
     *    either attribute has a size larger than one, or b) the attribute has
     *    a distinguished value.</p>
     *
     *    <p>The logic for case a) is that we can use adds and deletes on
     *    individual attributes to simulate a 'replace' operation, but we
     *    need to avoid doing this for single valued attributes (for which
     *    we *always* use replace).  So if Attribute A has values {1,2,3},
     *    and is changed to A' {1,3,5,6}, this method returns an 'add' {5,6}.</p>
     *
     *    <p>The logic for case b) is that we cannot use 'replace' on an
     *    attribute with a naming value in it, so we *must* use adds and
     *    deletes - and we'll have to take our chances that it is not
     *    single valued. (Possibly later on we can check this from schema).
     *    This method cannot change the distinguished value, but produces an
     *    'add' for any other changed.  So if, for entry cn=fred, Attribute cn
     *    has values {fred,erik}
     *    and cn' has values {fred, nigel], this method produces an 'add' {nigel}.</p>
     *
     *
     *    @param newRDN the new RDN of the entry that is being created
     *           (usually this is the same as the RDN of the original entry)
     *           May be null if it is not to be checked.
     *    @param oldSet the set of already existing attributes
     *    @param newSet the set of new attributes to test
     *    @return attributes that must be added to the object.
     */

    public static DXAttributes getAdditionSet(RDN newRDN, Attributes oldSet, Attributes newSet) 
		throws NamingException
    {
        DXAttributes additionSet = new DXAttributes();
        NamingEnumeration listOfNewAttributes = newSet.getAll();

        while (listOfNewAttributes.hasMore())
        {
            Attribute newAtt = (Attribute)listOfNewAttributes.next();
         
            String attributeName = newAtt.getID();

            boolean isNamingAttribute = newRDN.contains(attributeName);

            Attribute oldAtt = oldSet.get(attributeName);
		    
            if (! emptyAtt(newAtt))            // don't add empty atts!
            {
                /*
                 *    Check for simple "whole attribute" adds
				 *    if the old Att is null, or only had null values add it. 
				 *    (It was probably created by the browser,
				 *     and doesn't exist in the database)
                 */
                 
                if ((isNamingAttribute == false) && (oldAtt == null || emptyAtt(oldAtt)))   
				{
                    additionSet.put(newAtt);      
				}
				    
                /*
                 *   Check for attribute values that have been changed in attributes
                 *   that are larger than 1, or that are naming attributes
                 */
// TODO: - clean this up for DSML etc. ...
                    
                else if (isNamingAttribute || (oldAtt.size() > 1 || newAtt.size() > 1 ))
                {
                    DXNamingEnumeration valuesToAdd = getMissingValues(oldAtt.getAll(), newAtt.getAll());
                    
                    // check for distinguished value, and ignore it...
                    if (isNamingAttribute)
                    	removeAnyDistinguishedValues(newRDN, attributeName, valuesToAdd);

                    if (valuesToAdd.size()>0)  
                        additionSet.put(new DXAttribute(attributeName, valuesToAdd));
                }    
            }            
        }
        return additionSet;
    }
	
	

    /**
     *    <p>Returns all single valued attributes whose values have changed - 
     *    that is, exist in both the new set and the old set, but have different values.
     *    Note that this function ignores the naming attribute.</p>
     *
	 *    <p>We need this function to cope with mandatory single valued attributes
	 *       (otherwise we could just use add and delete).</p>
	 *
     *    <p>All other attribute combinations are handled by attribute value adds and
     *    deletes. (This is slightly more efficient, and is required to modify 
     *    non-distinguished values of the naming attribute anyway).</p>
     *
     *    @param newRDN the RDN of the newer entry.
     *           (usually this is the same as the RDN of the original entry)
     *           May be null if it is not to be checked.
     *    @param oldSet the set of already existing attributes
     *    @param newSet the set of new attributes to test
     *    @param alwaysReplace option to always include a given attribute, even if it
     *           is unchanged (special purpose hack for OS390 group)
     *    @return attributes that require updating
     */

	// LOGIC NOTE - only replace attributes that are single valued (old & new) and NOT naming values.

    public static DXAttributes getReplacementSet(RDN newRDN, Attributes oldSet, Attributes newSet) 
        throws NamingException
    {
        DXAttributes replacementSet = new DXAttributes();
		
        NamingEnumeration listOfNewAttributes = newSet.getAll();

        while (listOfNewAttributes.hasMore())                                // find changed attributes
        {
            Attribute newAtt = (Attribute)listOfNewAttributes.next();

			if (newAtt != null && newAtt.size() == 1)  // only consider a single valued new attribute
			{
	            String attributeName = newAtt.getID();
				
	            if (newRDN.contains(attributeName) == false) // skip any naming attributes
				{
		            Attribute oldAtt = oldSet.get(attributeName);
		            
		            if (oldAtt != null && oldAtt.size() == 1)	// only look at changed single valued attributes.
		            {
		                // if  a single valued attribute has changed, make it a 'replace' op.
		                if (attributesEqual(newAtt, oldAtt)==false)
		                    replacementSet.put(newAtt);    
		            }					
	            }
			}				
        }
        return replacementSet;
    }



    /**
     *    <p>Returns the set of attributes that are in the oldSet, but
     *    not in the new set, and thus must be deleted. </p>
     *
     *    <p>It also returns the set of attribute *values* that are in 
     *    the old set, but not in the new set.  E.g. if attribute A
     *    has values {1,2,3,4}, and the new attribute A' has {1,4,6,7},
     *    this returns {2,3} for deletion.</p>
     *
	 *    <p>This method will ignore naming values, but will correctly
	 *    handle other values of the naming attribute.</p>
	 *
	 *    @param newRDN the RDN of the newer entry.
     *           (usually this is the same as the RDN of the original entry). 
     *           May be null if it is not to be checked.
     *    @param oldSet the set of already existing attributes
     *    @param newSet the set of new attributes to test
     */

    public static DXAttributes getDeletionSet(RDN newRDN, Attributes oldSet, Attributes newSet) 
		throws NamingException
    {
        DXAttributes deletionSet = new DXAttributes();  
		
        NamingEnumeration listOfOldAttributes = oldSet.getAll();

        while (listOfOldAttributes.hasMore())
        {
            Attribute oldAtt = (Attribute)listOfOldAttributes.next();

            if (! emptyAtt(oldAtt))            // don't delete empty atts!
            {
	            String attributeName = oldAtt.getID();
	            
	            boolean isNamingAttribute = newRDN.contains(attributeName);

	            Attribute newAtt = newSet.get(attributeName);

                if (newAtt == null)             
               		newAtt = new DXAttribute(attributeName);
				
                /*
                 *    Check for simple "whole attribute" deletes
                 */
                 
                if (emptyAtt(newAtt) && !isNamingAttribute)
                {
                    deletionSet.put(newAtt);       
                }   
				 
                /*
                 *   Check for attribute values that have been dropped, in attributes
                 *   that are larger than 1
                 */
                    
                else if (isNamingAttribute || oldAtt.size() > 1 || newAtt.size() > 1 )
                {
                    DXNamingEnumeration valuesToDelete = getMissingValues(newAtt.getAll(), oldAtt.getAll());
                    // check for distinguished value, and ignore it...
                    if (isNamingAttribute)
                    	removeAnyDistinguishedValues(newRDN, attributeName, valuesToDelete);

                    if (valuesToDelete.size()>0)  
                        deletionSet.put(new DXAttribute(attributeName, valuesToDelete));
                } 				
            }
        }
        return deletionSet;
    }



	/**
	 *    Checks two 'Attribute' objects for equality.
     *    XXX - should this be moved to DXAttribute?
	 */
	 
	private static boolean attributesEqual(Attribute a, Attribute b)
	    throws NamingException
	{
	    // sanity checks...
	    if (a == null && b == null) return true;
	    if (a == null || b == null) return false;
	    if (a.size() == 0 && b.size() == 0) return true;
	    if (a.size() != b.size()) return false;
	    if (a.get() == null && b.get() == null) return true;
	    if (a.get() == null || b.get() == null) return false;
	    if (a.getID().equalsIgnoreCase(b.getID())==false) return false;


	    try
	    {
	        Object[] A = CBArray.enumerationToArray(a.getAll());
	        Object[] B = CBArray.enumerationToArray(b.getAll());
	        return CBArray.isUnorderedEqual(A,B);
	    }
	    catch (NamingException e)
	    {
	        CBUtility.log("Naming Exception testing attributes " + a.getID() + " & " + b.getID() + " in DXAttributes:attributesEqual()");
	    }
	    return false; // only here if error occurred.
	}

	/**
	 *    Checks whether two 'Attributes' objects are equivalent (including naming attributes, if any).
	 */
	 
	public static boolean attributesEqual(Attributes a, Attributes b)
	{
        if (a == null && b == null) return true;
        if (a == null || b == null) return false;
        return a.equals(b);
    }

    public boolean equals(Object o)
    {
        if (o == null) return false;

        try
        {
            if (o instanceof Attributes)
                return this.equals((Attributes) o);
        }
        catch (NamingException e)
        {
            return false;  // suppress exception :-(...
        }

        return false;
    }

    public boolean equals(Attributes atts) throws NamingException
    {
        // some quick and simple equality checks
        if (atts == null) return false;
        if (size() == 0 && atts.size() == 0) return true;
        if (size() != atts.size()) return false;

        // at this stage, we have equality candidates - two equally sized attributes...

        NamingEnumeration testAtts = getAll();
        while (testAtts.hasMore())                                // find changed attributes
        {
            Attribute testAtt = (Attribute)testAtts.next();
            String ID = testAtt.getID();

            Attribute bAtt = atts.get(ID);

            if ( emptyAtt(bAtt) ^ emptyAtt(testAtt) ) return false;

            if (attributesEqual(testAtt, bAtt) == false) return false;
        }

        // if we're here, the attributes must be equal!

        return true;
    }

    /**
	 *	Checks the naming enumeration and removes any distinguished values that 
	 *  occur in the RDN.
	 *  @param newRDN the rdn to check for values in
	 *  @param attributeName the name of the attribute (potentially) in the RDN
	 *  @param values the list of values to potentially remove the distinguished value from
	 */
	 
    private static void removeAnyDistinguishedValues(RDN newRDN, String attributeName, DXNamingEnumeration values)
    {
        String distinguishedValue = newRDN.getRawVal(attributeName);
        values.remove(distinguishedValue);  // remove dist. val. (if it is there)
    }                        


    /**
     *    Returns all the values in B that are missing in A.  (i.e. return B minus (B union A)).
     *    @param A the list of values to exclude.
     *    @param B the list of values to include.
     *    @return all elements of B not found in A.
     */
     
    static private DXNamingEnumeration getMissingValues(NamingEnumeration A, NamingEnumeration B)
        throws NamingException
    {
        DXNamingEnumeration ret = new DXNamingEnumeration(B);

        if (A == null) return ret;
        
        while (A.hasMore())
        {
            ret.remove(A.next());
        }
        return ret;
    }

//    public DirContext getSchema() { return schema; }

    public String[] toIDStringArray()
    {
        DXNamingEnumeration ret = new DXNamingEnumeration(getIDs());
        return ret.toStringArray();
    }

    /**
     *    A quick test - do we have any numeric OIDs
     *
     */
    public boolean hasOIDs()
    {
        return (attOIDs.size()>0);
    }

    /**
     *    Utility ftn: checks that an attribute is not null and has at least
     *    one non-null value.
     */
    public static boolean emptyAtt(Attribute att)
    {
         return DXAttribute.isEmpty(att);
    }



}