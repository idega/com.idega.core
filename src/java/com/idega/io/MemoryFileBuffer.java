package com.idega.io;



public class MemoryFileBuffer{
  byte[] buffer=new byte[0];
  private String mimeType;

  protected synchronized void write(byte[] myByteArray){
    byte[] newArray = new byte[buffer.length+myByteArray.length];
    System.arraycopy(buffer,0,newArray,0,buffer.length);
    System.arraycopy(myByteArray,0,newArray,buffer.length,myByteArray.length);
    buffer=newArray;
  }

  protected synchronized byte read(int position){
     return buffer[position];
  }

  protected synchronized byte[] read(int position,int lengthOfArray){
    byte[] newArray = new byte[lengthOfArray];
    System.arraycopy(buffer,position,newArray,0,lengthOfArray);
    return newArray;
  }

  protected synchronized int read(int position,byte[] buffer){
    System.arraycopy(this.buffer,position,buffer,0,buffer.length);
    return 1;
  }

  protected synchronized int read(byte[] b, int off, int len){
    System.arraycopy(this.buffer,off,b,0,len);
    return 1;
  }


   /*protected synchronized void write(byte[] b){
      byte[] newArray = new byte[buffer.length+b.length];
      System.arraycopy(buffer,0,newArray,0,buffer.length);
      System.arraycopy(myByteArray,0,newArray,buffer.length,b.length);
      buffer=newArray;
   }*/

   protected synchronized void write(byte[] b, int off, int len){
      byte[] newArray = new byte[buffer.length+len];
      System.arraycopy(buffer,0,newArray,0,buffer.length);
      System.arraycopy(b,0,newArray,buffer.length,len);
      buffer=newArray;
   }

  /*protected synchronized void write(int b){
    byte[] newArray = new byte[1];
    newArray[0]=(byte)b;
    System.arraycopy(buffer,0,newArray,0,buffer.length);
    System.arraycopy(myByteArray,0,newArray,buffer.length,myByteArray.length);
    buffer=newArray;
  }*/


  public void setMimeType(String type){
    mimeType=type;
  }

  public String getMimeType(){
    return mimeType;
  }
  public int length(){
    return this.buffer.length;
  }
  public byte[] buffer(){
    return this.buffer;
  }
}