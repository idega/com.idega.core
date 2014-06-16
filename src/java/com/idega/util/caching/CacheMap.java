package com.idega.util.caching;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
/**
 *<p>
 *This class is to be used as a java.util.Map as a convenient way of caching objects.
 *The maximum number of cached objects can be set and increased an decreased (default number is 100)
 *The Map removes the least used objects in favour of newly inserted ones and keeps the most accessed objects.<br/>
 *This Cache Map is deprecated as of platform 3.1 as now is more recommended to use IWCacheManager2.getCache()
 *<p>
 * @author <a href="tryggvi@idega.is">Tryggvi Larusson</a>
 * @version 0.5
 */
public class CacheMap<K, V> extends ConcurrentHashMap<K, V> {

	private static final long serialVersionUID = -2566778836998184283L;
	private int maxNumberOfObjectsInMap = 100;
	private Map<Object, Integer> accesses;

	/**
	 * Constructs a CacheMap with maximum 100 stored objects
	 **/
	public CacheMap()
	{
		this(100);
	}

	/**
	 * Constructs a CacheMap setting the maximum number stored objects that will reside in cache.
	 * @param maxNumberOfStoredObjects The size that the CacheMap will always be less than or equal to
	 **/
	public CacheMap(int maxNumberOfStoredObjects)
	{
		setMaxNumberOfObjects(maxNumberOfStoredObjects);
	}

	/**
	 * Sets the maximum number of objects to be stored in cache
	 **/
	public void setMaxNumberOfObjects(int size)
	{
		this.maxNumberOfObjectsInMap = size;
	}
	public int getMaxNumberOfObjects()
	{
		return this.maxNumberOfObjectsInMap;
	}
	private Map<Object, Integer> getAcesses()
	{
		if (this.accesses == null)
		{
			this.accesses = new ConcurrentHashMap<Object, Integer>();
		}
		return this.accesses;
	}

	protected void reduce(int byCount)
	{
		for (int i = 0; i < byCount; i++)
		{
			removeLeastUsedObjectFromMemory();
		}
	}
	@Override
	public V put(K key, V value)
	{
		int difference = super.size() - this.getMaxNumberOfObjects();
		if (difference >= 0)
		{
			reduce(difference + 1);
		}
		incrementAccessesForKey(key);
		return super.put(key, value);
	}
	@Override
	public V get(Object key)
	{
		V value = super.get(key);
		if (value == null)
		{
			this.removeAccessesForKey(key);
			return value;
		} else
		{
			incrementAccessesForKey(key);
			return value;
		}
	}
	@Override
	public V remove(Object key)
	{
		removeAccessesForKey(key);
		return super.remove(key);
	}
	protected void removeLeastUsedObjectFromMemory()
	{
		Object key = getLeastAccessedKey();
		//System.out.println("Removing: "+key);
		onReducementFromMemory(key);
	}
	public void removeLeastUsedObjectFromMap()
	{
		Object key = getLeastAccessedKey();
		onReducementFromMap(key);
	}
	protected void onReducementFromMap(Object key)
	{
		remove(key);
	}
	protected void onReducementFromMemory(Object key)
	{
		remove(key);
	}
	protected Object getLeastAccessedKey()
	{
		return getLeastAccessedKeyInteger();
	}

	protected Object getLeastAccessedKeyInteger()
	{
		Iterator<Object> keyIter = getAcesses().keySet().iterator();
		Object theReturn = null;
		int lowestIntVal = -1;
		while (keyIter.hasNext())
		{
			Object key = keyIter.next();
			Integer integ = this.getAcesses().get(key);
			int intval = integ.intValue();
			if (intval < lowestIntVal || lowestIntVal == -1)
			{
				lowestIntVal = intval;
				theReturn = key;
			}
		}
		return theReturn;
	}

	protected void incrementAccessesForKey(Object key){
		incrementAccessesForKeyInteger(key);
	}

	protected void incrementAccessesForKeyInteger(Object key)
	{
		Integer prevValue = this.getAcesses().get(key);
		int prevIntValue;
		if (prevValue == null)
		{
			prevIntValue = 0;
		} else
		{
			prevIntValue = prevValue.intValue();
		}
		Integer newValue = new Integer(prevIntValue+1);
		getAcesses().put(key, newValue);
		//System.out.println("Incrementing for key: "+key+" - from "+prevValue+" to "+newValue);
	}
	protected void removeAccessesForKey(Object key)
	{
		this.getAcesses().remove(key);
	}
	@Override
	public void clear()
	{
		super.clear();
		this.accesses.clear();
	}

	protected class AccessCount implements Comparable<AccessCount> {
		int intValue=0;
		Object oKey;
		protected AccessCount(Object key){
			this.oKey=key;
		}

		public void increment(){
			this.intValue++;
		}
		public int getCount(){
			return this.intValue;
		}

		@Override
		public int compareTo(AccessCount c){
			return(getCount()-c.getCount());
		}

		public Object getKey(){
			return this.oKey;
		}
	}


	/**
	 * Testing method
	 **/
	public static void main(String[] args){

			CacheMap<String, Integer> map = new CacheMap<String, Integer>(100);
			//Map map = new HashMap();
			int max = 10000;
			String key0 = "key0";
			map.put(key0,new Integer(0));
			for (int i = 1; i < 100; i++)
			{
				map.get(key0);
			}
			long begin = System.currentTimeMillis();
			for (int i = 1; i < max; i++)
			{
				Integer test1 = new Integer(i);
				String key = "key"+i;
				map.put(key,test1);
			}

			long end = System.currentTimeMillis();
			long elapsed  = end-begin;
			System.out.println("Put1 for "+map.getClass().getName()+" Took: "+elapsed+" ms.");
			 begin = System.currentTimeMillis();
			for (int i = 0; i < max; i++)
			{
				map.get(key0);
			}
			 end = System.currentTimeMillis();
			 elapsed  = end-begin;
			System.out.println("Get2 for "+map.getClass().getName()+" Took: "+elapsed+" ms.");
			System.out.println("Map.size():"+map.size());

			if(args.length>0){
				System.out.println("Contents:");
				for (Iterator<String> iter = map.keySet().iterator(); iter.hasNext();)
				{
					Object key =  iter.next();
					Object value = map.get(key);
					System.out.print("Key "+key+"="+value);
					if(map != null){
						CacheMap<String, Integer> cMap = map;
						System.out.print(" - With accesses: "+cMap.getAcesses().get(key));
					}
					System.out.println("");

				}
			}


	}
}