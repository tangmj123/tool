package com.tmj.tools.sort;

import java.util.Comparator;

import org.junit.Test;

import static com.tmj.tools.sort.QuickSort.*;

public class QuickSortTest {

	@Test
	public void test1(){
		Integer[] a = {10,1,6,3,5};
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