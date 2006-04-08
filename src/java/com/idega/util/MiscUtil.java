package com.idega.util;


import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.SecureRandom;
import java.util.*;
import com.idega.repository.data.RefactorClassRegistry;

/**
 * Title:
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:
 * @author <br><a href="mailto:aron@idega.is">Aron Birkir</a><br>
 * @version 1.0
 */

public class MiscUtil {

  public MiscUtil() {
  }



    public static Object[] shuffle(Object aobj[])
    {
        Object aobj1[] = aobj;
        SecureRandom securerandom = new SecureRandom();
        for(int i = 0; i < aobj1.length; i++)
        {
            int j = securerandom.nextInt();
            if(j < 0)
                j = -j;
            j %= aobj1.length;
            Object obj = aobj1[i];
            aobj1[i] = aobj1[j];
            aobj1[j] = obj;
        }

        return aobj1;
    }

    public static String[] split(String s, String s1)
    {
        StringTokenizer stringtokenizer = new StringTokenizer(s1, s);
        String as[] = new String[stringtokenizer.countTokens()];
        int i = 0;
        while(stringtokenizer.hasMoreTokens())
            as[i++] = stringtokenizer.nextToken();
        return as;
    }

    /**
     * @deprecated Method getFileData is deprecated
     */

    public static String getFileData(String s)
        throws IOException
    {
        return getFileData(new File(s));
    }

    public static String getFileData(File file)
        throws IOException
    {
        byte abyte0[] = new byte[8192];
        FileInputStream fileinputstream = new FileInputStream(file);
        StringBuffer stringbuffer = new StringBuffer();
        int i;
        while((i = fileinputstream.read(abyte0)) >= 0)
        {
            String s = new String(abyte0);
            stringbuffer.append(s.substring(0, i));
        }
        fileinputstream.close();
        return stringbuffer.toString();
    }

    public static String join(Enumeration enumeration, String s)
    {
        StringBuffer stringbuffer = new StringBuffer();
        while(enumeration.hasMoreElements())
        {
            String s1 = (String)enumeration.nextElement();
            stringbuffer.append(s1);
            if(enumeration.hasMoreElements())
                stringbuffer.append(s);
        }
        return stringbuffer.toString();
    }

    public static Enumeration getStackEnum(Exception exception, int i)
    {
        Vector vector = new Vector();
        ByteArrayOutputStream bytearrayoutputstream = new ByteArrayOutputStream();
        PrintWriter printwriter = new PrintWriter(bytearrayoutputstream, true);
        exception.printStackTrace(printwriter);
        StringTokenizer stringtokenizer = new StringTokenizer(bytearrayoutputstream.toString(), "\n");
        for(int j = 0; j < i; j++)
            stringtokenizer.nextToken();

        while (stringtokenizer.hasMoreTokens()) {
        	vector.addElement(stringtokenizer.nextToken().substring(4));
				}
        return vector.elements();
    }

    public static String getStack(Exception exception, int i)
    {
        return join(getStackEnum(exception, i), ", ");
    }

    public static void dumpThreads()
    {
        ThreadGroup threadgroup1 = null;
        for(ThreadGroup threadgroup = Thread.currentThread().getThreadGroup(); threadgroup != null; threadgroup = threadgroup.getParent())
            threadgroup1 = threadgroup;

        threadgroup1.list();
    }

    public static void runClass(String s, String as[])
        throws Exception
    {
        Class class1 = RefactorClassRegistry.forName(s);
        Class aclass[] = new Class[1];
        String as1[] = new String[0];
        aclass[0] = as1.getClass();
        Method method = class1.getMethod("main", aclass);
        Object aobj[] = new Object[1];
        aobj[0] = as;
        try
        {
            method.invoke(class1, aobj);
        }
        catch(InvocationTargetException invocationtargetexception)
        {
            invocationtargetexception.printStackTrace();
            Throwable throwable = invocationtargetexception.getTargetException();
            if(throwable instanceof Exception)
                throw (Exception)throwable;
            throwable.printStackTrace();
        }
    }

  public static String array2str(String[] array,String delim){
    StringBuffer s = new StringBuffer();
    for (int i = 0; i < array.length; i++) {
      if(i != 0)
        s.append(delim);
      s.append(array[i]);
    }
    return s.toString();
  }
  public static String[] str2array(String s,String delim){
    StringTokenizer st = new StringTokenizer(s,delim);
    String[] array = new String[st.countTokens()];
    int i = 0;
    while(st.hasMoreTokens()){
      array[i++] = st.nextToken();
    }
    return array;
  }

}
