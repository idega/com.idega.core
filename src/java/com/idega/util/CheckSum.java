package com.idega.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Calculates a checksum of a file or bytestream.
 * By default the MD5 digest algorithm is used.
 * Other algorithms can be used if the default provider package  
 * provides an implementation of the requested digest algorithm.
 *
 * see java.security.MessageDigest for other algorithms
 * 
 * modified: $Date: 2004/11/21 01:30:43 $ by $Author: aron $
 * 
 * @author <a href="mailto:aron@idega.com">aron </a>
 * @version $Revision: 1.1 $
 */

public class CheckSum {
    
    
    private static MessageDigest md;
    private static byte[] buffer;
    //private static char[] nibbles = { "0", "1", "2", "3", "4", "5", "6", "7","8", "9", "A", "B", "C", "D", "E", "F" };
    private static char[] nibbles = {'0','1','2','3','4','5','6','7','8','9','a','b','c','d','e','f'};

    public CheckSum() throws NoSuchAlgorithmException{
        buffer = new byte[1024];
        setAlgorithm("MD5");
    }
    
    public CheckSum(String algorithm) throws NoSuchAlgorithmException{
        buffer = new byte[1024];
        setAlgorithm(algorithm);
    }
    
    /**
     * Sets the digest algorithm to be used
     * @param algorithm
     * @throws NoSuchAlgorithmException
     */
    public void setAlgorithm(String algorithm) throws NoSuchAlgorithmException{
        md = MessageDigest.getInstance(algorithm);
    }
    
    public String byte2Hex(byte b) {
        // create a 8 bit mask
        int nb = b & 0xFF;
        // get upper nibble
        int i_1 = (nb >> 4) & 0xF;
        // get lower nibble
        int i_2 = nb & 0xF;
        // convert to HEX presentation
        return String.valueOf(nibbles[i_1]) + String.valueOf(nibbles[i_2]);
    }
    
    public String byte2Hex(byte[] b){
        StringBuffer buffer = new StringBuffer(b.length);
        for (int i = 0; i < b.length; i++) {
            buffer.append(byte2Hex(b[i]));
        }
        return buffer.toString();
    }
    
    
//  converts the hex base to integer values.
    public static int hex2Int(char c) {
        if (c == '0')
            return 0;
        if (c == '1')
            return 1;
        if (c == '2')
            return 2;
        if (c == '3')
            return 3;
        if (c == '4')
            return 4;
        if (c == '5')
            return 5;
        if (c == '6')
            return 6;
        if (c == '7')
            return 7;
        if (c == '8')
            return 8;
        if (c == '9')
            return 9;
        if (c == 'a' || c == 'A')
            return 10;
        if (c == 'b' || c == 'B')
            return 11;
        if (c == 'c' || c == 'C')
            return 12;
        if (c == 'd' || c == 'D')
            return 13;
        if (c == 'e' || c == 'E')
            return 14;
        if (c == 'f' || c == 'F')
            return 15;

        System.out.println("should not occur! " + c);

        return -1;

    }

    
    //  converts a two digit hex string to a byte
    public static int hex2Byte(String s) {
        int result = 0;
        for (int i = 0; i < s.length(); i++) {
            result += Math.pow(16, i) * hex2Int(s.charAt(s.length() - 1 - i));
        }
        return result;
    }
    
    /**
     * Gets the checksum for the given file url
     * @param file
     * @return
     */
    public String getSum(String file){
        return getSum(new File(file));
    }
    
    /**
     * Gets the checksum for the given file
     * @param file
     * @return
     */
    public String getSum(File file){
        try {
            return getSum(new FileInputStream(file));
        } catch (FileNotFoundException e) {
            return "failed: "+e.getMessage();
        }
    }

    /**
     * Calculates a checksum of the given stream of bytes
     * The stream is closed after the check.
     * @param f
     * @return
     */
    public String getSum(InputStream stream){
       return  getSum(stream,true);
    }
    /**
     * Calculates a checksum on a byte stream.
     * If stream is not set to be closed, it is reset.
     * @param s
     * @param closeStream
     * @return
     */
    public String getSum(InputStream s,boolean closeStream) {
        try {
            
            int b_read;
            b_read = s.read(buffer);
            while (b_read > 0) {
                md.update(buffer, 0, b_read);
                b_read = s.read(buffer);
            }
            if(closeStream)
                s.close();
            else
                s.reset();
            byte[] digest = md.digest();
            return byte2Hex(digest);
        } catch (FileNotFoundException e) {
            return "failed: "+e.getMessage();
        } catch (IOException e) {
            return "failed: "+e.getMessage();
        }
    }

    public static void main(String[] args) {
        if(args.length>0){
	        try {
	           
	            CheckSum summer = new CheckSum("MD5");
	            System.out.println(summer.getSum(args[0]));
	            summer.setAlgorithm("SHA");
	            System.out.println(summer.getSum(args[0]));
	        } catch (NoSuchAlgorithmException e) {
	            // TODO Auto-generated catch block
	            e.printStackTrace();
	        }
        }

    }
}