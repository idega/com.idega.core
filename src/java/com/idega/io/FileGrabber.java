package com.idega.io;



import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;



public class FileGrabber {





  //private int objectcounter= 0;



  public int errorFlag = 0;//-1 is MalformedURLException, -3 is IOException



//Methods



//This gets an url and writes what it gets in a file

  public int getTheURL(String theFileURL,String theFileName,String theFilePath) {



  //Variables

  	String line = new String("");

  	String fileName = new String("");

  	String filePath = new String("");

  	String fileURL = new String("");

        StringBuffer pageString = new StringBuffer();



  	BufferedInputStream mybuffer;

  	BufferedReader in;

  	URL url;

  	OutputWriter writer;





	filePath=theFilePath;

	fileName=theFileName;

	fileURL = theFileURL;



	//Go get the file

    try {

      	url = new URL(fileURL);

      	mybuffer = new BufferedInputStream(url.openStream());

      	in = new BufferedReader(new InputStreamReader(mybuffer));



      //Put the contents in a string

      	while ((line = in.readLine()) != null){

           pageString.append(line);

           pageString.append("\n");



        }

      	in.close();//close the file



    } catch(MalformedURLException mue) { // URL c'tor

        //System.out.println(fileURL + "is an invalid URL: " + mue);

      	this.errorFlag = -1;

      	return this.errorFlag;

    } catch(IOException ioe) { // Stream constructors

      //System.out.println("IOException: " + ioe);

      this.errorFlag=-3;

      return this.errorFlag;

    }





	writer = new OutputWriter();



	this.errorFlag = writer.WriteToFile(pageString.toString(),fileName,filePath);



	return this.errorFlag;



  }



//This gets an url and returns it as a string

public String getTheURL(String theFileURL) {



	String fileURL = new String("");



	fileURL = theFileURL;



  	BufferedInputStream mybuffer;

  	BufferedReader in;

  	URL url;

  	String line = new String("");

  	StringBuffer pageString = new StringBuffer("");



	 try {

	      	url = new URL(fileURL);

	      	mybuffer = new BufferedInputStream(url.openStream());

	      	in = new BufferedReader(new InputStreamReader(mybuffer));



	      //Put the contents in a string

	      	while ((line = in.readLine()) != null) {

	      	  pageString.append(line);//var +"\n"

                }

			in.close();



	  } catch(MalformedURLException mue) { // URL c'tor

	  	//System.out.println(fileURL);

	       // System.out.println(fileURL + "is an invalid URL: " + mue);

	      	return "Error 1";

	    } catch(IOException ioe) { // Stream constructors

	      //System.out.println("IOException: " + ioe);

	      return "Error 2";

	    }



return pageString.toString();



}





}

