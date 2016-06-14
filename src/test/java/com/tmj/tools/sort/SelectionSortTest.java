package com.tmj.tools.sort;

import org.junit.Test;
import static com.tmj.tools.sort.SelectionSort.*;

public class SelectionSortTest {

	@Test
	public void test1(){
		Integer[] a = {10,2,52,15,3};
		System.out.println(isSorted(a));;
		sort(a);
		print(a);
		System.out.println(isSorted(a));;
		String[] b = {"fds","a","af"};
		sort(b);
		print(b);
	}
}
