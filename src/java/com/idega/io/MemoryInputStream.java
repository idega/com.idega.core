package com.idega.io;

import java.io.InputStream;
import java.io.IOException;

public class MemoryInputStream extends InputStream{
  private MemoryFileBuffer buffer;
  private int position=0;

  public MemoryInputStream(MemoryFileBuffer buffer){
    this.buffer=buffer;
  }


  public int read(){
    //System.out.println("Calling read1 on MemoryInputStream");
    if(position < buffer.length()){
      return (int)buffer.read(position++);
    }
    else{
      return -1;
    }
  }

  public int read(byte[] b){
    //System.out.println("Calling read2 on MemoryInputStream");
    return read(b, 0, b.length);
  }

  public int read(byte[] b, int off, int len){
    //System.out.println("Calling read3 on MemoryInputStream");
    if(position < buffer.length()){
      int oldPos = position;
      position+=len;
      return buffer.read(b,oldPos+off,len);
    }
    else{
      return -1;
    }
  }

  public int available() throws IOException {
    //System.out.println("Calling available on MemoryInputStream");
    return buffer.length()-position;
  }

  public void close(){ }

  public synchronized void mark(int p0) { }

  public synchronized void reset() throws IOException { }

  public boolean markSupported() {
    return false;
  }
}