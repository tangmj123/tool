package com.tmj.tools.sort;

import static com.tmj.tools.sort.QuickSort.print;
import static com.tmj.tools.sort.QuickSort.sort;

import java.util.Comparator;
import java.util.Random;

import org.junit.Test;

public class QuickSortTest {

	@Test
	public void test1(){
		Integer[] a = {10,1,6,3,5};
//		Integer[] a = {12,13,15,16,3,5};
		sort(a);
		print(a);
		
		Person[] persons = {new Person(9, "t"),new Person(5, "w"),new Person(15, "c"),new Person(4, "d")};
		
		sort(persons, new Comparator<Person>() {
			@Override
			public int compare(Person v, Person w) {
				return v.id - w.id;
			}
		});
		print(persons);
	}
	//@Test
	public void test2(){
		int size = 50000000;
		Integer[] a = new Integer[size];
		Random random = new Random();
		for (int i = 0; i < size; i++) {
			a[i] = random.nextInt(size);
		}
		
		long start = System.currentTimeMillis();
		sort(a);   
		long end = System.currentTimeMillis();
		long timeCost = (end-start);
		System.out.println("sort time cost="+timeCost);//4286(1KW) 9420(2KW) 15234(3KW) 22818(4KW) 29013
		start = System.currentTimeMillis();
		bitSort(a);
		end = System.currentTimeMillis();
		timeCost = (end-start);
		System.out.println("bitsort time cost="+timeCost);//5162(1KW) 3567(2KW) 18240(3KW) 23422 32816
	}
	
	public void bitSort(Integer[] a){
		char[] ca = new char[a.length];
		int N = a.length;
		for (int i = 0; i < N; i++) {
			ca[a[i]] = 1;
		}
		int i = 0;
//		for (int j = 0; j < N; j++) {
//			if(ca[j]==1) a[i++]=j;
//		}
	}
}
class Person{
	int id;
	String name;
	
	public Person(int id,String name){
		this.id = id;
		this.name = name;
	}

	@Override
	public String toString() {
		return "Person [id=" + id + ", name=" + name + "]";
	}
}