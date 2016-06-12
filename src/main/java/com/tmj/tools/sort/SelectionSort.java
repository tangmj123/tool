package com.tmj.tools.sort;

import java.util.Comparator;
import java.util.stream.Stream;


/**
 * 选择排序 
 * @author Tangmj
 * 每次找出i位置后面最小的元素和i位置做交换
 */
public class SelectionSort{
	
	private SelectionSort(){}
	/**
	 * 对数组排序
	 * @param a
	 */
	public static void sort(Comparable[] a){
		int N = a.length;
		for (int i = 0; i < N; i++) {
			int min = i;
			for (int j = i+1; j < N; j++) 
				if(less(a[j], a[min])) min = j;//计算最小值 min
			exch(a, i, min);
		}
	}
	/**
	 * 指定比较器对数组排序
	 * @param a
	 * @param c
	 */
	public static void  sort(Object[] a,Comparator c){
		int N = a.length;
		for (int i = 0; i < N; i++) {
			int min = i;
			for (int j = i+1; j < N; j++) 
				if(less(a[j], a[min],c)) min = j;//计算最小值 min
			exch(a, i, min);
		}
	}
	/**
	 * v 排在 w之前？
	 * @param v
	 * @param w
	 * @return
	 */
	public static boolean less(Comparable v,Comparable w){
		return v.compareTo(w) < 0;
	}
	/**
	 * v 排在 w之前？
	 * @param v
	 * @param w
	 * @param c
	 * @return
	 */
	public static boolean less(Object v,Object w,Comparator c){
		return c.compare(v, w) < 0;
	}
	/**
	 * 交换i和j位置元素
	 * @param a
	 * @param i
	 * @param j
	 */
	public static void exch(Object[] a ,int i,int j){
		Object temp = a[i];
		a[i] = a[j];
		a[j] = temp;
	}
	/**
	 * 是否排序好
	 * @param a
	 * @return
	 */
	public static boolean isSorted(Comparable[] a){
		return isSorted(a, 0, a.length-1);
	}
	/**
	 * 从lo到hi是否排序好
	 * @param a
	 * @param lo
	 * @param hi
	 * @return
	 */
	public static boolean isSorted(Comparable[] a,int lo,int hi){
		for (int i = lo+1; i <= hi; i++) 
			if(less(a[i], a[i-1])) return false;
		return true;
	}
	/**
	 * 是否排序好
	 * @param a
	 * @param c
	 * @return
	 */
	public static boolean isSorted(Object[] a,Comparator c){
		return isSorted(a, c, 0, a.length-1);
	}
	/**
	 * 从lo到hi是否排序好
	 * @param a
	 * @param c
	 * @param lo
	 * @param hi
	 * @return
	 */
	public static boolean isSorted(Object[] a,Comparator c,int lo,int hi){
		for (int i = lo+1; i <= hi; i++) 
			if(less(a[i], a[i-1],c)) return false;
		return true;
	}
	/**
	 * 打印数组
	 * @param a
	 */
	public static void print(Object[] a){
		Stream.of(a).forEach(System.out::println);
	}
	
	
	public static void main(String[] args) {
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
