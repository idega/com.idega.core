package com.idega.io;

import java.io.InputStream;

public class MemoryInputStream extends InputStream{
  private MemoryFileBuffer buffer;
  private int position=0;

  MemoryInputStream(MemoryFileBuffer buffer){
    this.buffer=buffer;
  }


  public int read(){
    if(position < buffer.length()){
      return (int)buffer.read(position++);
    }
    else{
      return 0;
    }
  }

  public int read(byte[] b){
    if(position < buffer.length()){
      int oldPos = position;
      position += b.length;
      return buffer.read(oldPos, buffer.buffer());
    }
    else{
      return 0;
    }
  }

  public int read(byte[] b, int off, int len){
    if(position < buffer.length()){
      position+=len;
      return buffer.read(b,off,len);
    }
    else{
      return 0;
    }
  }
}