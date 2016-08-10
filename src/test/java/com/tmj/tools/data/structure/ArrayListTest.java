package com.tmj.tools.data.structure;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class ArrayListTest {

	ArrayList<String> list = null;
	
	@Before
	public void setUp() throws Exception {
		list = new ArrayList<String>(5);
		list.add("你");
		list.add("好");
		list.add("!");
		list.add("你");
		list.add("是");
		list.add("谁");
		list.add("?");
	}

	@After
	public void tearDown() throws Exception {
	
	}


	@Test
	public void testAdd() {
		
		list.show();
	}

	@Test
	public void testInsert() {
		list.insert(0, "我是在0新插入的");
		list.show();
		System.out.println("------------------");
		list.insert(2, "我是在2新插入的");
		list.show();
		System.out.println("------------------");
		list.insert(9, "我是在最后新插入的");
		list.show();
	}

	@Test
	public void testGet() {
		String s = list.get(2);
		Assert.assertEquals("!", s);
	}

	@Test
	public void testIndexOf() {
		int index = list.indexOf("好");
		Assert.assertEquals(1, index);
	}

	@Test
	public void testRemove() {
		list.remove("好");
		list.remove("你");
		list.remove("!");
		list.remove("?");
		list.remove("谁");
		list.show();
	}

	@Test
	public void testRemoveAt() {
		list.removeAt(2);
		list.show();
	}

	@Test
	public void testShow() {
		
	}
	
	@Test
	public void testSize(){
		Assert.assertEquals(7, list.size());
	}
}
