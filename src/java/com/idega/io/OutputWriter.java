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

        this.pathANDfile = new String("");

        this.theInput = new String("");

        this.errorFlag = 0;

    }



    public int WriteToFile(String string1, String string2, String string3)

    {

        this.theInput = string1;

        this.pathANDfile = new StringBuffer(String.valueOf(string3)).append(string2).toString();

        try

        {

            this.output = new DataOutputStream(new FileOutputStream(this.pathANDfile));

            this.output.writeUTF(this.theInput);

            this.output.flush();

            this.output.close();

            return this.errorFlag;

        }

        catch (IOException e)

        {

            this.errorFlag = -2;

        }

        return this.errorFlag;

    }

}
