
package com.idega.core.ldap.client.naming;

import java.util.Hashtable;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttribute;
import com.idega.core.ldap.client.cbutil.CBBase64;
import com.idega.core.ldap.client.cbutil.CBUtility;
import com.idega.core.ldap.client.jndi.SchemaOps;

/**
 *    This is largely a schema-aware wrapper to BasicAttribute, with some
 *    utility classes spliced on.  <p>
 */

public class DXAttribute extends BasicAttribute
{

    boolean binary = false;    // whether it's binary (in this context, 'non-string') data

    static boolean verboseBinary = false; // whether to add ';binary' to the end of binary att names.

    String name;               // the name of the attribute (usually identical to the ID)
    String syntaxOID;          // the OID of the Syntax (i.e. "1.3.6.1.4.1.1466.115.121.1.5" for binary)
    String syntaxDesc;         // the same thing as a human readable description
    String description;        // the free form description of the attribute that may exist in schema

    static Hashtable knownBinaryAttributes;       // a list of known 'binary' attributes.

    static SchemaOps schema;  // the schema of the most recently connected to directory

    private final static Logger log = Logger.getLogger("com.idega.core.ldap.client.naming.DXAttribute");  // ...It's round it's heavy it's wood... It's better than bad, it's good...

    static
    {
        knownBinaryAttributes = new Hashtable(100);
    }

    /**
     *    Normal constructor for an Attribute with no (current) value.
     */

    public DXAttribute(String ID)
    {
        super(ID);
        setBinaryness();
    }

    /**
     *    Normal constructor an Attribute with a single value.
     */

    public DXAttribute(String ID, Object value)
    {
        super(ID, value);
        setBinaryness();
    }

    /**
     *    Make a copy of a normal, pre-existing attribute.
     *    @param att the attribute (e.g. BasicAttribute) to wrap.
     */

    public DXAttribute(Attribute att)
    {
        super (att.getID());
        try
        {
            addValues(att.getAll());
        }
        catch (NamingException e)
        {
            CBUtility.log("error reading attribute values for attribute " + getID() + "\n" + e.toString());
        }
            

        setBinaryness();
        setName(att.getID());  // ?? necessary??
    }

    public DXAttribute(String ID, NamingEnumeration values)
    {
        super(ID);
        addValues(values);
        setBinaryness();
    }

    public void addValues(NamingEnumeration values)
    {            
        try
        {
            while (values.hasMore())
            {
                add(values.next());
            }
        }
        catch (NamingException e)
        {
            CBUtility.log("error adding values for attribute " + getID() + "\n" + e.toString());
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

    /**
     *    This method examines the schema to test whether the attribute is 'binary' data.
     *    In this context, 'binary' simply means 'not string'; i.e. photo, certificate,
     *    octet etc.
     */

    public void setBinaryness()
    {
        // quickly handle schema atts.
        String ID = getID();

        if ("SYNTAXNAMENUMERICOIDDESC".indexOf(ID) != -1) {
			return;
		}

        ID = ID.toLowerCase();

        if (knownBinaryAttributes.get(ID) != null)    // see if we already know this attribute ID...
        {
            this.binary = (knownBinaryAttributes.get(ID).equals(Boolean.TRUE));
        }

        if (ID.endsWith(";binary")) {
			this.binary = true;
		}

        if (this.binary == false)
        {
            this.binary = checkIsBinaryInSchema();
        }

        if (this.binary == false && schema==null) // backup checks for if no schema or bad schema
        {
            Object value = null;
            try
            {
                value = get();
                this.binary =  !(value==null || value instanceof String);
            }
            catch (Exception e) {}
        }

        knownBinaryAttributes.put(ID, new Boolean(this.binary));
    }

    /**
     *    Does this attribute have a valid value (i.e. not null, or empty string).
     */

    public static boolean isEmpty(Attribute att)
    {
        if (att == null) {
			return true;
		}

        if (att.size()==0) {
			return true;
		}

        if (att.size()==1)
        {
            Object val = null;
            try
            {
                val = att.get();
            }
            catch (NamingException e)
            {
                return true;  // assume naming exception means empty attribute... (?)
            }

            if (val == null || "".equals(val)) {
				return true;
			}
        }

        return false;
    }

    /**
     *    Used to register the schema with a particular attribute - currently the schema is
     *    then looked up for Description,name,syntax description; this may be
     *    inefficient; maybe these should only be looked up when necessary.<p>
     *    In the event that no particular schema is registered for this attribute, the
     *    default schema is used.
     */
/*
    public void registerSchema(DirContext s)
    {
        schema = s;
        if (schema == null) schema = defaultSchema;  // use the backup schema instead
        if (defaultSchema == null) return;           // can't do anything.

        getSyntaxDesc();  // sets syntaxDesc
        getSyntaxOID();   // sets syntaxOID & checks binaryness
        getName();        // sets name
        getDescription(); // sets description
        getOptions();
    }
*/
    /**
     *    Utility method - returns the schema context in use by this attribute.
     */
    //public DirContext getSchema() { return schema; }

    /**
     *    implements the interface spec.
     */
/*
    public DirContext getAttributeDefinition()
    {
        if (schema == null) return null;
        try
        {
            return (DirContext) schema.lookup("AttributeDefinition/" + getID());
        }
        catch (NamingException e) {return null;}
    }
*/

    public boolean checkKnownBinaryAttributes(String ID)
    {
//System.out.println("cache caught: " + ID + " = " + Boolean.TRUE.equals(knownBinaryAttributes.get(ID)));
        //CB this is wierd.  was there a reason?  return (Boolean.TRUE.equals(knownBinaryAttributes.get(ID)));
        return knownBinaryAttributes.contains(ID);
    }



    public static boolean isBinarySyntax(String syntaxName)
    {
        if (syntaxName == null) {
			return false;
		}

        int pos = syntaxName.indexOf("1.3.6.1.4.1.1466.115.121.1.");
        if (pos == -1) {
			return false;
		}

        String number = syntaxName.substring(pos + "1.3.6.1.4.1.1466.115.121.1.".length());

        if (number.length() > 2)
        {
            number = number.substring(0, 2);
            char c = number.charAt(1);
            if (Character.isDigit(c) == false) {
				number = number.substring(0, 1);
			}
        }

        try
        {
            int finalNumber = Integer.parseInt(number);

            switch (finalNumber)
            {
                case 4:  return true; // 1.3.6.1.4.1.1466.115.121.1.4 - audio
                case 5:  return true; // 1.3.6.1.4.1.1466.115.121.1.5 - binary
                case 8:  return true; // 1.3.6.1.4.1.1466.115.121.1.8 - certificate
                case 9:  return true; // 1.3.6.1.4.1.1466.115.121.1.9 - certificate list
                case 10: return true; // 1.3.6.1.4.1.1466.115.121.1.10 - certificate pair
                case 28: return true; // 1.3.6.1.4.1.1466.115.121.1.28 - jpeg
                case 40: return true; // 1.3.6.1.4.1.1466.115.121.1.40 - octet string
                default: return false;
            }
        }
        catch (NumberFormatException e)
        {
            CBUtility.log("Unexpected error parsing syntax: " + syntaxName + "\n " + e);
            return false;
        }
    }


    public boolean checkIsBinaryInSchema()
    {
        if (schema != null)
        {
            try
            {
//System.out.println("\n" + getID() + " starting to test! ");
                Attributes atts = schema.getAttributes("AttributeDefinition/" + getID());

                Attribute a;
                while ((a = atts.get("SUP")) != null)
                {
                    atts = schema.getAttributes("AttributeDefinition/" + a.get().toString());
                }
                a = atts.get("SYNTAX");
                if (a != null)
                {
                    String syntaxName = a.toString();
//System.out.println(getID() + " found syntax as: " + syntaxName);
                    if (isBinarySyntax(syntaxName)) {
						return true;
					}
                }
                else
                {
                    CBUtility.log("%$%$%$%$%$%$%$%$ Can't find SYNTAX for... " + a.getID());
                }
            }
            catch (Exception e)
            {
                try
                {
                    Attributes atts = schema.getAttributes("AttributeDefinition/" + getID() + ";binary");
                //XXX check for bad schema
                    String syntaxName = atts.get("SYNTAX").toString();
                // XXX check for string length less than 1.3....5
                    if (isBinarySyntax(syntaxName)) {
						return true;
					}
                }
                catch (Exception e2) // give up
                {
                    //CBUtility.log("error checking for binary attribute " + getID() + "\n" + e.toString());
                }
            }
        }
        else
        {
            CBUtility.log("no schema available", 4);
        }
//System.out.println(getID() + " not binary...");
        return false;
    }

    /**
     *    implements the interface spec.
     */
/*
    public DirContext getAttributeSyntaxDefinition()
    {
        //return schema.getAttributeSyntax(getID());

        if (schema == null) return null;

        try
        {
            DirContext attDef = (DirContext) schema.lookup("AttributeDefinition/" + getID());
            String syntaxName = attDef.lookup("SYNTAX").toString();

            if (isBinarySyntax(syntaxName))
                binary=true;

            return (DirContext) schema.lookup("SyntaxDefinition/" + syntaxName);
        }
        catch (NamingException e) {return null;}
    }
*/
    /**
     *    Whether the attribute contains binary data; ideally found by checking Syntax, but
     *    often set by inspection of the attribute value (whether it is a Byte array).
     */
    public boolean isBinary() { return this.binary; }

    /**
     *    Sets the binary status of the attribute.  Shouldn't be required any more;
     *    should be set by syntax checking.
     */
    public void setBinary(boolean bin)
    {
        knownBinaryAttributes.put(getID(), new Boolean(bin));
        this.binary = bin;
    }

    /**
     *    Utility Function: takes a schema Entry class such as 'AttributeDefinition/cn',     *
     *    and returns the result of looking up a particular attribute within that
     *    (e.g. 'DESC').
     *    @param schemaEntry the full schema entry name to lookup, e.g. 'ClassDefinition/alias'
     *    @param schemaAttribute the attribute to look up, e.g. 'MUST'
     *    @return the value of the schema entry attribute (e.g. '2.5.4.1')
     */

    public String schemaLookup(String schemaEntry, String schemaAttribute)
    {
        if (schema == null) {
			return null;
		}
		else {
			return schema.schemaLookup(schemaEntry, schemaAttribute);
/*
        String value = null;
        try
        {
            Attributes theWorks = schema.getAttributes(schemaEntry);
            DXAttributes temp = new DXAttributes(theWorks);

            Attributes searchResult = schema.getAttributes(schemaEntry, new String[] {schemaAttribute.toLowerCase()});


            NamingEnumeration searchResults = searchResult.getAll();
            //probably only a single parent attribute...
            while (searchResults.hasMore())              //XXX redundant - should only be one.
            {
                Attribute result = (Attribute) searchResults.next();
                NamingEnumeration values = result.getAll(); // XXX redundant - only the first value is returned.

                while (values.hasMore())
                {
                    value = (String)values.next();
                }
            }
        }
        catch (NamingException e)
        {
            return null;
        }
        return value;
*/
		}
    }

    public boolean hasOptions()
    {
        if (this.description==null) {
			getDescription();
		}

        if (this.description==null || this.description == "" || this.description.indexOf("LIST:")<0) {
			return false;
		}

        return true;
    }

    /**
     *    IF a description field has been set in the schema, and IF that
     *    description field LISTs a set of values, read 'em and return them
     *    as a string array.
     */

    public String[] getOptions()
    {
        if (this.description==null) {
			getDescription();
		}
        if (this.description==null || this.description == "" ) {
			return new String[0];
		}

        // can't be fagged working out java generic parse stuff - do own quickie...
        int len = this.description.length();
        int pos = this.description.indexOf("LIST:");
        if (pos < 0) {
			return new String[0];
		}
        pos += 5;
        int next = pos;

        Vector resVect = new Vector();
        resVect.add("");
//        String option;
        while (pos < len && pos > 0)
        {
            next = this.description.indexOf(',', next+1);
            if (next < 0)
            {
                resVect.add(this.description.substring(pos));
                pos = 0;
            }
            else if (this.description.charAt(next-1)!= '\\')
            {
                resVect.add(this.description.substring(pos, next));
                pos = next+1;
            }
            else
            {
                next++;  // move past the escaped comma
            }
        }

        // dump vector into string array, unescaping as we go.
        String[] result = new String[resVect.size()];
        for (int i=0; i<resVect.size(); i++) {
			result[i] = unEscape((String)resVect.elementAt(i));
		}

        return result;
    }

    public String unEscape(String escapeMe)
    {
        int slashpos = escapeMe.indexOf('\\');
        while (slashpos>=0)
        {
            escapeMe = escapeMe.substring(0,slashpos) + escapeMe.substring(slashpos+1);
            slashpos = escapeMe.indexOf('\\');
        }
        return escapeMe;
    }



    public String getSyntaxOID()
    {
        if (this.syntaxOID == null)
        {
            this.syntaxOID = schemaLookup("AttributeDefinition/" + getID(), "SYNTAX");

            if (isBinarySyntax(this.syntaxOID)) {
				this.binary = true;
			}
        }
        return this.syntaxOID;
    }

    /**
     *    Usefull escape to allow renaming of attributes.  Use with caution,
     *    since an arbitrary name may not correspond to a valid schema name.
     */
    public void setName(String newName)
    {
        this.name = newName;
    }

    public String getName()
    {
        if (this.name == null) {
			this.name = schemaLookup("AttributeDefinition/" + getID(), "NAME");
		}
        if (this.name == null) {
			this.name = getID();
		}

        return this.name;
    }

    /**
     *    Returns (and caches) the syntax description.
     */
    public String getSyntaxDesc()
    {
        if (this.syntaxDesc == null) {
			this.syntaxDesc = schemaLookup("SyntaxDefinition/" + getSyntaxOID(), "DESC");
		}
        return this.syntaxDesc;
    }

    /**
     *    Returns the attribute's 'DESC' field from the attribute schema
     *    definition, if such exists (it's an extension).  Not to be
     *    confused with the Syntax Description, which is something like "binary".
     */

    public String getDescription()
    {
        if (this.description == null)
        {
            this.description = schemaLookup("AttributeDefinition/" + getID(), "DESC");
            if (this.description == null) {
				this.description = "";       // to an empty string, to avoid repeatedly looking it up.
			}
        }
        return this.description;
    }


    /**
     *    General descriptive string: used mainly for debugging...
     *
     */

    public String toString()
    {
        int count = 1;
        try
        {
            String result = "att: " + getID() + " (size=" + size() + ") ";
            NamingEnumeration vals = getAll();
            if (this.binary)
            {
                result = result + " (binary) ";
                while (vals.hasMore())
                {
                    try
                    {
                        byte[] b = (byte[])vals.next();
                        result = result + "\n    " + (count++) + ":" + ((b==null)?"null":CBBase64.binaryToString(b));
                    }
                    catch (ClassCastException cce)
                    {
                        result = result + "\n    " + (count++) + ": <error - not a byte array>";
                    }                        
                }
            }
            else
            {
                while (vals.hasMore())
                {
                    Object o = vals.next();
                    result = result + "\n    " + (count++) + ":" + ((o==null)?"null":o.toString());
                }
            }

            return result + "\n";
        }
        catch (NamingException e)
        {
            CBUtility.log("error listing values for " + getID() + " : " + e.toString());
            return (getID() + " (error listing values)");
        }


    }

    // ugly, ugly hack to add ';binary' when writting data to dir, but
    // not otherwise.
    public static void setVerboseBinary(boolean status)
    {
        verboseBinary = status;
        log.log(Level.FINER,"setting binary attribute status to " + status);
    }

    public String getID()
    {
         String id = super.getID();
         if (verboseBinary && !(id.endsWith(";binary")))
         {
             //System.out.println("binaryness = " + binary);
             if (this.binary) {
				id = id + ";binary";
			}
         }
         log.log(Level.FINER,"returning binary id " + id);
         return id;
    }
	
	
	
   /**
    *	This method gets all the values of an attribute.  Useful only
	*	for multivalued attributes (use get() otherwise).  
	*	@return all the values of this attribute in a String Array.
    *   XXX - assumes all values are strings??
	*/
	
	public Object[] getValues()
	{
		Object[] values = new String[size()];
		
		try
		{
			for(int i=0; i<size(); i++) {
				values[i] = get(i);
			}
			return values;	
		}
		catch(NamingException e)
		{
			return new String[]{};
		}
	}
	   
	
	
	

    /**
     *    Gets rid of null values and empty strings from the value set.
     */
    public void trim()
    {
        for (int i=size()-1; i>0; i--)
        {
            Object o = null;
            try
            {
                o = get(i);
                if (o==null || "".equals(o))
                {
                    remove(i);
                }
            }
            catch (NamingException e)  // shouldn't happen...
            {
                CBUtility.log("Bad Attribute value in DXAttribute - removing ");
                remove(i);             // .. but remove offending entry if it does.

            }
        }
    }

    public void setOrdered(boolean state) { this.ordered = state; }



    /**
     * Returns true if this attribute is a SINGLE-VALUE attribute.
     * @return true if this attribute is a SINGLE-VALUE attribute,
     * false otherwise.
     */

    public boolean isSingleValued()
    {/* TE */
        return schema.isAttributeSingleValued(getName());
    }



    /**
     *    Ye olde equals method - so we can use these thingumies in Hashtables.
     */
/*
    public boolean equals(Object obj)
    {
        System.out.println("testing equality for:\n" + toString() + "\n vs: \n" + obj.toString());

        boolean ret = super.equals(obj);

        System.out.println(" = " + ret);

        return ret;
    }
*/
/*
        if ((obj instanceof DXAttribute) == false) return false;

        DXAttribute test = (DXAttribute)obj;

        if (getID().equals(test.getID()) == false) return false;

        if (size() != test.size()) return false;

        for (int i=0; i<size(); i++)
        {
            try
            {
                if (get(i).equals(test.get(i)) == false) return false;
            }
            catch (NamingException e)
            {
                return false;
            }
        }

        return true;
    }

    public int hashCode()
    {


    }
*/
    /**
     *    A wrapper to BasicAttributes.get() that catches NoSuchElementException
     *    and gets it to return 'null' instead.  (Unclear if this is strictly
     *    correct, however JXplorer commonly equates a 'null' value to 'no value'.)
     */
/*
    public Object get() throws NamingException
    {
        try
        {
            return super.get();
        }
        catch (NoSuchElementException e)
        {
            return null;
        }
    }
*/

    /*
     *        *** Attribute Interface Methods ***
     */
/*
     public void add(int ix, Object attrVal) { values.add(ix, attrVal); }

     public boolean add(Object attrVal) { return values.add(attrVal); }

     public void clear() { values.clear(); }

     public Object clone() { return new DXAttribute(this); }

     public boolean contains(Object attrVal) { return values.contains(attrVal); }

     public Object get() { return values.elementAt(0); }

     public Object get(int ix) { return values.elementAt(ix); }

     public NamingEnumeration getAll() { return new DXNamingEnumeration(values.elements()); }

// defined above     DirContext getAttributeDefinition()

// defined above     DirContext getAttributeSyntaxDefinition()

     public String getID() { return id; }

     public boolean isOrdered() { return false; }

     public Object remove(int ix) { return values.remove(ix); }

     public boolean remove(Object attrval) { return values.remove(attrval); }

     public Object set(int ix, Object attrVal) { return values.set(ix, attrVal); }

     public int size() { return values.size(); }
*/
}