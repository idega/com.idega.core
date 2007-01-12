//idega 2000 - Tryggvi Larusson

/*

*Copyright 2000 idega.is All Rights Reserved.

*/

package com.idega.util.datastructures;



import java.util.*;





/**

*@author <a href="mailto:tryggvi@idega.is">Tryggvi Larusson</a>

*@version o.5

*DRAFT - UNFINISHED CLASS

*/

public class TreeStructure{



private Hashtable theTree;

int lastNodeid;

int lastParentNodeid;



public TreeStructure(){

	this.theTree = new Hashtable();

	this.lastNodeid=0;

	this.lastParentNodeid=0;

}



public int addNode(){

	//lastNodeid

        return -1;

}



public int add(idegaTreeNode node){

  return -1;

}



/*

public boolean isLeaf(int nodenum){

	return true

}*/



}

