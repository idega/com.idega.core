package com.idega.io;



import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;



public class FileStringWriter {



  public static void StringToFile(String theStringToWrite, String theFileName, String theFilePath) throws IOException {



    DataOutputStream output;

    output = new DataOutputStream( new FileOutputStream(theFilePath + theFileName));

    output.writeUTF(theStringToWrite);

    output.flush();

    output.close();



  }



  public static void StringToFile(String theStringToWrite, String theFileNameAndtheFilePath) throws IOException {

    StringToFile(theStringToWrite,theFileNameAndtheFilePath,"");

  }

}
