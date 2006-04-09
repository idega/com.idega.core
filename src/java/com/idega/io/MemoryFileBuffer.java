package com.idega.io;
/**
 * A utility class to use for temporary buffering and connecting streams in memory.
 *
 * @author <a href="mailto:tryggvi@idega.is">Tryggvi Larusson</a>
 * @version 1.0
 */
public class MemoryFileBuffer {

	byte[] buffer = new byte[0];
	private String mimeType;

	protected synchronized void write(byte[] myByteArray) {
		byte[] newArray = new byte[this.buffer.length + myByteArray.length];
		System.arraycopy(this.buffer, 0, newArray, 0, this.buffer.length);
		System.arraycopy(myByteArray, 0, newArray, this.buffer.length,
				myByteArray.length);
		this.buffer = newArray;
	}

	protected synchronized byte read(int position) {
		try {
			return this.buffer[position];
		} catch (ArrayIndexOutOfBoundsException e) {
			return (byte) -1;
		}
	}

	/*
	 * protected synchronized byte[] read(int position,int lengthOfArray){
	 * 
	 * byte[] newArray = new byte[lengthOfArray];
	 * 
	 * System.arraycopy(buffer,position,newArray,0,lengthOfArray);
	 * 
	 * return newArray;
	 *  }
	 */
	protected synchronized int read(byte[] buffer, int position) {
		System.arraycopy(this.buffer, position, buffer, 0, buffer.length);
		return 1;
	}

	protected synchronized int read(byte[] b, int off, int len) {
		//try{
		int bufferlength = this.buffer.length;
		int remaining = bufferlength - off;
		int numberCopied = 0;
		if (remaining < len) {
			numberCopied = remaining;
		} else {
			numberCopied = len;
		}
		System.arraycopy(this.buffer, off, b, 0, numberCopied);
		return numberCopied;
		//}
		//catch(RuntimeException e){
		//  return -1;
		//}
	}

	/*
	 * protected synchronized void write(byte[] b){
	 * 
	 * byte[] newArray = new byte[buffer.length+b.length];
	 * 
	 * System.arraycopy(buffer,0,newArray,0,buffer.length);
	 * 
	 * System.arraycopy(myByteArray,0,newArray,buffer.length,b.length);
	 * 
	 * buffer=newArray;
	 *  }
	 */
	protected synchronized void write(byte[] b, int off, int len) {
		byte[] newArray = new byte[this.buffer.length + len];
		System.arraycopy(this.buffer, 0, newArray, 0, this.buffer.length);
		System.arraycopy(b, 0, newArray, this.buffer.length, len);
		this.buffer = newArray;
	}

	/*
	 * protected synchronized void write(int b){
	 * 
	 * byte[] newArray = new byte[1];
	 * 
	 * newArray[0]=(byte)b;
	 * 
	 * System.arraycopy(buffer,0,newArray,0,buffer.length);
	 * 
	 * System.arraycopy(myByteArray,0,newArray,buffer.length,myByteArray.length);
	 * 
	 * buffer=newArray;
	 *  }
	 */
	public void setMimeType(String type) {
		this.mimeType = type;
	}

	public String getMimeType() {
		return this.mimeType;
	}

	/**
	 * 
	 * The length of the Buffer in bytes
	 *  
	 */
	public int length() {
		return this.buffer.length;
	}

	public byte[] buffer() {
		return this.buffer;
	}
}