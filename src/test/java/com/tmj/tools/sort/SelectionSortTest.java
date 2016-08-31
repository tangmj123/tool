package com.tmj.tools.sort;

import static com.tmj.tools.sort.SelectionSort.isSorted;
import static com.tmj.tools.sort.SelectionSort.print;
import static com.tmj.tools.sort.SelectionSort.sort;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.Test;

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
	
	
	@Test
	public void seletionSort(){
		Integer[] a = {10,2,52,15,3};
		int length = a.length;
		for (int i = 0; i < length; i++) {
			int min = i;
			for (int j = i+1; j < length; j++) {
				if(a[j]<a[min]) min = j;
			}
			
			int tmp = a[i];
			a[i] = a[min];
			a[min] = tmp;
		}
		System.out.println(Arrays.toString(a));
	}
}
