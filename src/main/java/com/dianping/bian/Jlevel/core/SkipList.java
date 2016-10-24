package com.dianping.bian.Jlevel.core;

import java.io.Serializable;
import java.util.Comparator;
import java.util.Map;
import java.util.Random;


/**
 *
 * @author Mr.Bian
 *
 */
public class SkipList<K,V> implements Serializable{
	
	private static final long serialVersionUID = -1908031651274789448L;

	
	private int level=0;
	
	private int size=0;
	
	private Entry<K,V> header;
	
	private Comparator<? super K> comparator;
	
	private Random r=new Random();
	
	private int randomSeed;
	
	public SkipList(){
	}
	
	public SkipList(Comparator<? super K> comparator){
		this.comparator=comparator;
	}
	
	public void init(){
		header=new Entry<K,V>();
		header.forwards=new Entry[level];
		randomSeed=r.nextInt();
	}
	
	public int size(){
		return size;
	}
	
	public int level(){
		return level;
	}
	
	public V get(K key){
		Entry<K,V> kv=getEntry(key);
		return kv==null? null:kv.value;
	}
	
	public Entry<K,V> getEntry(K key){
		if (comparator!=null){
			return getEntryByComparator(key);
		}
		if (key==null){
			throw new NullPointerException();
		}
		Comparable<? super K> k=(Comparable<? super K>) key;
		Entry<K,V> cur=header;
		for (int i=level-1;i>=0;--i){
			while(cur!=null && cur.forwards[i]!=null){
				int com=k.compareTo(cur.forwards[i].key);
				if (com==1){
					cur=cur.forwards[i];
				}else if (com==0){
					return cur.forwards[i];
				}else{
					break;
				}
			}
		}
		return null;
	}
	
	Entry<K,V> getEntryByComparator(K key){
		Comparator<? super K> cpr=comparator;
		if (cpr!=null){
			Entry<K,V> cur=header;
			for (int i=level-1;i>=0;--i){
				while(cur!=null && cur.forwards[i]!=null){
					int com=comparator.compare(key, cur.forwards[i].key);
					if (com==1){
						cur=cur.forwards[i];
					}else if (com==0){
						return cur;
					}else{
						break;
					}
				}
			}
		}
		return null;
	}
	
	public V put(K key, V value){
		Entry<K,V> entry=null;
		if (header==null){
			compare(key,key);
			init();
			entry=newEntry(key,value);
			if (entry.nodeLevel>level){
				resetHeaderForwards(entry.nodeLevel);
			}
			for (int i=entry.nodeLevel-1;i>=0;--i){
				
				header.forwards[i]=entry;
			}
			++size;
			return null;
		}
		entry=newEntry(key,value);
		if (entry.nodeLevel>level){
			resetHeaderForwards(entry.nodeLevel);
		}
		Entry<K,V>[] update=new Entry[level];
		Entry<K,V> cur=header;
		Comparator<? super K> cpr=comparator;
		if (cpr==null){
			if (key == null){
				throw new NullPointerException();
			}
			Comparable<? super K> cmp=(Comparable<? super K>)key;
			for (int i=level-1;i>=0;--i){
				while(cur!=null && cur.forwards[i]!=null){
					int com=cmp.compareTo(cur.forwards[i].key);
					if (com==1){
						cur=cur.forwards[i];
					}else if (com==0){
						return cur.forwards[i].setValue(value);
					}else{
						update[i]=cur;
						break;
					}
				}
				update[i]=cur;
			}
		}else{
			for (int i=level-1;i>=0;--i){
				while(cur!=null && cur.forwards[i]!=null){
					int com=cpr.compare(key, cur.forwards[i].key);
					if (com==1){
						cur=cur.forwards[i];
					}else if (com==0){
						return cur.forwards[i].setValue(value);
					}else{
						update[i]=cur;
						break;
					}
				}
				update[i]=cur;
			}
		}
		for (int i=entry.nodeLevel-1;i>=0;--i){
			if (i>level-1){
				header.forwards[i]=entry;
				entry.forwards[i]=null;
				continue;
			}
			Entry<K,V> pre=update[i];
			entry.forwards[i]=pre.forwards[i];
			pre.forwards[i]=entry;
		}
		if (entry.nodeLevel>level){
			level=entry.nodeLevel;
		}
		++size;
		return null;
	}
	
	public V deleteKey(K key){
		if (key==null){
			throw new NullPointerException();
		}
		Entry<K,V> delEntry=null;
		Entry<K,V> cur=header;
		for (int i=level-1;i>=0;--i){
			while(cur!=null && cur.forwards[i]!=null){
				int com=compare(key, cur.forwards[i].key);
				if (com==1){
					cur=cur.forwards[i];
				}else if (com==0){
					delEntry=cur.forwards[i];
					cur.forwards[i]=cur.forwards[i].forwards[i];
				}else{
					break;
				}
			}
		}
		if (delEntry==null){
			return null;
		}else{
			size--;
			return delEntry.value;
		}
	}
	
	private void resetHeaderForwards(int level){
		Entry<K,V>[] forwards=new Entry[level];
		for (int i=this.level-1;i>=0;--i){
			forwards[i]=header.forwards[i];
		}
		header.forwards=forwards;
	}
	
	int compare(K k1,K k2){
		return comparator==null? ((Comparable<? super K>)k1).compareTo(k2)
				: comparator.compare(k1, k2);
	}
	
	Entry<K,V> newEntry(K key,V value){
		Entry<K,V> entry=new Entry<K,V>();
		entry.key=key;
		entry.value=value;
		entry.nodeLevel=randomLevel();
		entry.forwards=new Entry[entry.nodeLevel];
		return entry;
	}
	
	private int randomLevel() {
        int x = randomSeed;
        x ^= x << 13;
        x ^= x >>> 17;
        randomSeed = x ^= x << 5;
        if ((x & 0x80000001) != 0) // test highest and lowest bits
            return 0;
        int level = 1;
        while (((x >>>= 1) & 1) != 0) ++level;
        return level;
    }
	
	static final class Entry<K,V> implements Map.Entry<K,V>{
		K key;
		V value;
		Entry<K,V>[] forwards;
		int nodeLevel;
		public Entry(){}
		
		public Entry(K key,V value){
			this.key=key;
			this.value=value;
		}
		
		public K getKey() {
			return key;
		}

		public V getValue() {
			return value;
		}

		public V setValue(V value) {
			V oldValue=this.value;
			this.value=value;
			return oldValue;
		}
		
		@Override
		public String toString(){
			return key+"="+value;
		}
	}
}
