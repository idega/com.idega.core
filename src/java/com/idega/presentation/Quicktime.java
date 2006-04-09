package com.idega.presentation;

/** see information about the quicktimeplugin attributes at http://www.apple.com/quicktime/authoring/embed2.html **/

public class Quicktime extends GenericPlugin{



public Quicktime(){

  super();

  setClassId("02BF25D5-8C17-4B23-BC80-D3488ABDDC6B");

  setCodeBase("http://www.apple.com/qtactivex/qtplugin.cab");

  setPluginSpace("http://www.apple.com/quicktime/download/");

}



public Quicktime(String url){

  this(url,"untitled");

}



public Quicktime(String url,String name){

  this();

  setName(name);

  setURL(url);

}



public Quicktime(String url,String name,int width,int height){

  this(url,name);

  setWidth(width);

  setHeight(height);

}

/*

* The usual constructor

*/

public Quicktime(String url,int width,int height){

  this();

  setURL(url);

  setWidth(width);

  setHeight(height);

}



public void setName(String name){

 super.setName(name);

 setParamAndAttribute("MOVIENAME",name);

}



public void setId(String id){

 super.setID(id);

 setParamAndAttribute("MOVEID",id);

}



public void setAUTOHREF(boolean value){

  setParamAndAttribute("AUTOHREF",value);

}



public void setAUTOPLAY(boolean value){

  setParamAndAttribute("AUTOPLAY",value);

}



public void setCACHE(boolean value){

  setParamAndAttribute("CACHE",value);

}



public void setCONTROLLER(boolean value){

  setParamAndAttribute("CONTROLLER",value);

}



public void setDONTFLATTENWHENSAVING(boolean value){

  setParamAndAttribute("DONTFLATTENWHENSAVING",value);

}



public void setENABLEJAVASCRIPT(boolean value){

  setParamAndAttribute("ENABLEJAVASCRIPT",value);

}



public void setHIDDEN(boolean value){

  setParamAndAttribute("HIDDEN",value);

}



public void setKIOSKMODE(boolean value){

  setParamAndAttribute("KIOSKMODE",value);

}



public void setLOOP(boolean value){

  setParamAndAttribute("LOOP",value);

}



public void setQTSRCDONTUSEBROWSER(boolean value){

  setParamAndAttribute("QTSRCDONTUSEBROWSER",value);

}



public void setPLAYEVERYFRAME(boolean value){

  setParamAndAttribute("PLAYEVERYFRAME",value);

}



public void setHref(String value){

  setParamAndAttribute("HREF",value);

}



public void setStartTime(String value){

  setParamAndAttribute("STARTTIME",value);

}



public void setEndTime(String value){

  setParamAndAttribute("ENDTIME",value);

}



public void setChokeSpeed(String value){

  setParamAndAttribute("QTSRCCHOKESPEED",value);

}



/** audio volume 0-100**/

public void setAudioVolume(String value){

  setParamAndAttribute("QTSRCCHOKESPEED",value);

}



public void setScale(String value){

  setParamAndAttribute("SCALE",value);

}



/** for example: FRAME_NAME, MYSELF, QUICKTIMEPLAYER**/

public void setTarget(String value){

  setParamAndAttribute("TARGET",value);

}



public void setTargetForId(String id, String value){

  setParamAndAttribute("TARGET"+id,value);

}



public void setTARGETCACHE(boolean value){

  setParamAndAttribute("TARGETCACHE",value);

}



public void setToOpenInQuicktimePlayer(boolean value){

  if( value ) {
		setTarget("QUICKTIMEPLAYER");
	}
	else {
		setTarget("");
	}

}



public void setVRCorrection(boolean value){

  setParamAndAttribute("CORRECTION",value);

}



/** initial field of view angle: 8-64 **/

public void setVRFOV(String value){

  setParamAndAttribute("FOV",value);

}



public void setVRHotspotForId(String id, String value){

  setParamAndAttribute("HOTSPOT"+id,value);

}



/** initial node id **/

public void setVRInitialNodeId(String value){

  setParamAndAttribute("NODE",value);

}



/** initial pan angle: 0-360 **/

public void setVRPanAngle(String value){

  setParamAndAttribute("FOV",value);

}



/** initial tilt angle: -42.5 to 42.5 **/

public void setVRTiltAngle(String value){

  setParamAndAttribute("FOV",value);

}



public void setVRURLSubstitute(String id, String original, String newvalue){

  setParamAndAttribute("URLSUBSTITUTE"+id,original+":"+newvalue);

}



}



