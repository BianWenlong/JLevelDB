package com.dianping.bian.Jlevel.core;

import java.util.Map;
import java.util.TreeMap;

import org.junit.Test;

/**
 *
 * @author Mr.Bian
 *
 */
public class SkipListTest {
	
	@Test
	public void skipListTest(){
		SkipList<Integer,Integer> sl=new SkipList<Integer,Integer>();
		Map<Integer,Integer> map=new TreeMap<Integer,Integer>();
		long begin=System.currentTimeMillis();
		for (int i=0;i<1000000;++i){
			sl.put(i, i);
		}
		long end=System.currentTimeMillis();
		System.out.println(end-begin);
		for (int i=1000000-1;i>=0;--i){
			sl.get(i);
		}
		long end1=System.currentTimeMillis();
		System.out.println(end1-end);
		for (int i=0;i<1000000;++i){
			map.put(i, i);
		}
		long end2=System.currentTimeMillis();
		System.out.println(end2-end1);
		for (int i=1000000-1;i>=0;--i){
			map.get(i);
		}
		long end3=System.currentTimeMillis();
		System.out.println(end3-end2);
	}
}
