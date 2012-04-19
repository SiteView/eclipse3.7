/*
 * 
 * Created on 2005-2-28 7:00:23
 *
 * HashMapOrdered.java
 *
 * History:
 *
 */
package COM.dragonflow.Properties;

/**
 * Comment for <code>HashMapOrdered</code>
 * 
 * @author 
 * @version 0.0
 *
 *
 */
import java.io.PrintWriter;
import java.util.Enumeration;

import java.util.ArrayList;
import com.recursionsw.jgl.HashMap;

public class HashMapOrdered extends HashMap
{

 public HashMapOrdered(boolean flag)
 {
     super(flag);
 }

 public int count(Object obj)
 {
     if(allowsDuplicates())
     {
         Object obj1 = get(obj);
         if(obj1 != null && (obj1 instanceof ArrayList))
         {
             return ((ArrayList)obj1).size();
         }
     }
     return super.count(obj);
 }

 public Object add(Object obj, Object obj1)
 {
     if(allowsDuplicates())
     {
         Object obj2 = get(obj);
         if(obj2 != null)
         {
             if(obj2 instanceof ArrayList)
             {
                 ((ArrayList)obj2).add(obj1);
             } else
             {
                  ArrayList array = new ArrayList();
                 array.add(obj2);
                 array.add(obj1);
                 put(obj, array);
             }
             return null;
         }
     }
     return super.add(obj, obj1);
 }

 public synchronized Enumeration values(Object obj)
 {
     if(allowsDuplicates())
     {
         Object obj1 = get(obj);
         if(obj1 != null && (obj1 instanceof ArrayList))
         {
             return ((ArrayList)obj1).elements();
         }
     }
     return super.values(obj);
 }

 public void print(PrintWriter printwriter)
 {
     Object obj;
     for(Enumeration enumeration = keys(); enumeration.hasMoreElements(); printwriter.println(obj + "=" + get(obj)))
     {
         obj = enumeration.nextElement();
     }

     printwriter.flush();
 }
}
