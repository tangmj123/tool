package com.tmj.tools.sort;

import java.util.Arrays;
import java.util.Comparator;
/**
 * 选择排序
 * 那一个元素在排好的序列中找到合适的位置插入
 * 时间复杂度  N(N-1)/2
 * @author Tangmj
 *
 */
@SuppressWarnings({"rawtypes","unchecked"})
public class InsertionSort{

	private InsertionSort(){}
	
	/**
	 * 对实现了comparable接口的数组排序
	 * @author Tangmj
	 * @param arr
	 */
	public static void sort(Comparable[] arr){
		int N = arr.length;
		for(int i = 1;i < N; i++){
			for (int j = i; j > 0 ; j--) {
				if(lessthan(arr[j],arr[j-1])) exchange(arr, j, j-1);
			}
		}
	}
	
	/**
	 * 给定比较器进行排序
	 * @author Tangmj
	 * @param arr
	 * @param c
	 */
	public static void sort(Object[] arr,Comparator c){
		int N = arr.length;
		for (int i = 1; i < N; i++) {
			for(int j = i;j > 0;j--){
				if(lessthan(arr[j], arr[j-1], c)) exchange(arr, j, j-1);
					
			}
		}
	}
	
	/**
	 * 交换数组 两个位置的元素
	 * @author Tangmj
	 * @param arr
	 * @param a
	 * @param b
	 */
	private static void exchange(Object[] arr,int a,int b){
		Object tmp = arr[a];
		arr[a] = arr[b];
		arr[b] = tmp;
	}
	
	
	private static boolean lessthan(Comparable a,Comparable b){
		return a.compareTo(b) < 0;
	}
	
	private static  boolean lessthan(Object a,Object b,Comparator c){
		return c.compare(a, b) < 0;
	}
	
	public static void main(String[] args) {
//		String[] arr = {"e","c","a","f"};
//		sort(arr);
//		System.out.println(Arrays.toString(arr));
		
		P[] arr = {new P(10,"p10"),new P(2, "p2"),new P(20,"p20"),new P(15,"p15")};
		Comparator<P> c = new Comparator<P>() {
			@Override
			public int compare(P o1, P o2) {
				return o1.getId() - o2.getId();
			}
		};
		
		sort(arr, c);
		System.out.println(Arrays.toString(arr));
	}
}
class P{
	int id;
	String name;
	public int getId() {
		return id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public void setId(int id) {
		this.id = id;
	}
	public P(int id, String name) {
		super();
		this.id = id;
		this.name = name;
	}
	@Override
	public String toString() {
		return "P [id=" + id + ", name=" + name + "]";
	}
}