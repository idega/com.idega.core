package com.idega.io;



import java.io.*;



public class OutputWriter

{

    private DataOutputStream output;

    private String pathANDfile;

    private String theInput;

    public int errorFlag;



    public OutputWriter()

    {

        pathANDfile = new String("");

        theInput = new String("");

        errorFlag = 0;

    }



    public int WriteToFile(String string1, String string2, String string3)

    {

        theInput = string1;

        pathANDfile = new StringBuffer(String.valueOf(string3)).append(string2).toString();

        try

        {

            output = new DataOutputStream(new FileOutputStream(pathANDfile));

            output.writeUTF(theInput);

            output.flush();

            output.close();

            return errorFlag;

        }

        catch (IOException e)

        {

            errorFlag = -2;

        }

        return errorFlag;

    }

}
