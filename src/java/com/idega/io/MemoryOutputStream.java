package com.idega.io;

import java.io.OutputStream;

public class MemoryOutputStream extends OutputStream{
  MemoryFileBuffer buffer;
  private boolean isClosed=false;
  private int position=0;


  MemoryOutputStream(MemoryFileBuffer buffer){
    this.buffer=buffer;
  }

  public void close(){
    isClosed=true;
  }
  /**
  * @todo IMPLEMENT
  */
  public void flush(){

  }

  public void write(byte[] b){
    if(!isClosed){
      int oldPos = position;
      position += b.length;
      write(b,oldPos,b.length);
    }
  }

  public void write(byte[] b, int off, int len){
    if(!isClosed){
      buffer.write(b,off,len);
    }
  }

  public void write(int b){
    if(!isClosed){
      byte[] myByte = new byte[1];
      myByte[0]=(byte)b;
      write(myByte);
    }
  }
}


