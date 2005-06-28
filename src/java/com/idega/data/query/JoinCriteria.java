package com.idega.data.query;

import java.util.HashSet;
import java.util.Set;

import com.idega.data.IDOCompositePrimaryKeyException;
import com.idega.data.IDOEntityDefinition;
import com.idega.data.IDOEntityField;
import com.idega.data.IDORelationshipException;
import com.idega.data.query.output.Output;

/**
 * @author <a href="joe@truemesh.com">Joe Walnes</a>
 */
public class JoinCriteria extends Criteria {

    private Column source, dest;
    private Table middleTable = null;

    public JoinCriteria(Column source, Column dest) {
        this.source = source;
        this.dest = dest;
    }
    
    public JoinCriteria(Table srcTable, Table destTable) throws IDORelationshipException {
		if (srcTable.hasEntityDefinition() && destTable.hasEntityDefinition()) {
			IDOEntityDefinition source = srcTable.getEntityDefinition();
			IDOEntityDefinition destination = destTable.getEntityDefinition();

			IDOEntityField[] fields = source.getFields();
			for (int i = 0; i < fields.length; i++) {
				IDOEntityField field = fields[i];
				if (field.isPartOfManyToOneRelationship()) {
					if (field.getManyToOneRelated().equals(destination)) {
						try {
							this.source = srcTable.getColumn(field.getSQLFieldName().toLowerCase());
							this.dest =  destTable.getColumn(destination.getPrimaryKeyDefinition().getField().getSQLFieldName().toLowerCase());
						}
						catch (IDOCompositePrimaryKeyException e) {
							throw new IDORelationshipException(e.getMessage());
						}
						return;
					}
				}
				IDOEntityDefinition[] definitions = source.getManyToManyRelatedEntities();
				if (definitions != null && definitions.length > 0) {
					for (int j = 0; j < definitions.length; j++) {
						IDOEntityDefinition definition = definitions[j];
						if (destination.equals(definition)) {
							try {
								String middleTableName = source.getMiddleTableNameForRelation(destination.getSQLTableName());
								if (middleTableName == null) { throw new IDORelationshipException("Middle table not found for tables."); }
		
								middleTable = new Table(middleTableName);
		
								this.source = srcTable.getColumn(source.getPrimaryKeyDefinition().getField().getSQLFieldName().toLowerCase());
								this.dest =  destTable.getColumn(destination.getPrimaryKeyDefinition().getField().getSQLFieldName().toLowerCase());
							}
							catch (IDOCompositePrimaryKeyException e) {
								throw new IDORelationshipException(e.getMessage());
							}
							return;
						}
					}
				}
			}
			return;
		}
		throw new IDORelationshipException("No relation found between tables!");
    }
    
    public Column getSource() {
        return source;
    }

    public Column getDest() {
        return dest;
    }

    public void write(Output out) {
    	if (middleTable == null) {
    		out.print(source)
            .print(" = ")
            .print(dest);
    	}
    	else {
    		out.print(source)
            .print(" = ")
            .print(middleTable.toString()+"."+source.getName())
        	.print(" and ")
        	.print(middleTable.toString()+"."+dest.getName())
        	.print(" = ")
        	.print(dest);
        }
    }
    
    public Set getTables(){
		Set s = new HashSet();
		s.add(source.getTable());
		s.add(dest.getTable());
		if (middleTable != null) {
			s.add(middleTable);
		}
		return s; 
    }
    
    public Object clone(){
		JoinCriteria obj = (JoinCriteria)super.clone();
		if(source!=null){
			obj.source = (Column) this.source.clone();
		}
		
		if(dest!=null){
			obj.dest = (Column) this.dest.clone();
		}
		
		if(middleTable!=null){
			obj.middleTable = (Table) this.middleTable.clone();
		}
		return obj;
	}

}
