package com.tmj.tools.sort;

import java.util.Comparator;
import java.util.stream.Stream;
/**
 * 快速排序
 * @author madhouse
 * 从右边找一个比基准小的，从左边找一个比基准大的,交换,更新基准，递归处理基准两边的数据
 */
public class QuickSort {

	private QuickSort(){
		
	}
	/**
	 * 排序
	 * @param a
	 */
	public static void sort(Comparable[] a){
		sort(a, 0, a.length-1);
	}
	/**
	 * 排序
	 * @param a
	 * @param lo
	 * @param hi
	 */
	public static void sort(Comparable[] a,int lo,int hi){
		if(lo>hi) return;
		int i = lo,j = hi;
		Comparable temp = a[lo];
		while(i!=j){
			
			while(greatOrEqual(a[j], temp)&&i<j) j--;  //从右边找比基准小的位置
			
			while(lessOrEqual(a[i], temp)&&i<j) i++;  //从左边找比基准打的位置
			
			exch(a, i, j);                            //交换
		}
		//a[i] 做为新基准
//		a[lo] = a[i];
//		a[i] = temp;
		exch(a, lo, i);
		//递归处理基准两边的数据
		sort(a,lo,i-1);
		sort(a,i+1,hi);
	}
	
	/**
	 * 指定比较器排序
	 * @param a
	 * @param c
	 */
	public static <T> void sort(T[] a ,Comparator<T> c){
		sort(a,c,0,a.length-1);
	}
	/**
	 * 
	 * @param a
	 * @param c
	 * @param lo
	 * @param hi
	 */
	public static <T> void sort(T[] a ,Comparator<T> c,int lo,int hi){
		if(lo>hi) return;
		int i=lo,j=hi;
		T temp = a[lo];
		while(i!=j){
			while(greatOrEqual(a[j], temp,c)&&i<j) j--;   //找一个比基准小的
			while(lessOrEqual(a[i], temp,c)&&i<j) i++;    //找一个比基准打的
			exch(a, i, j);                           //交换
		}
		//更新基准
		exch(a, i, lo);
		//递归处理基准两边的数据
		sort(a,c,lo,i-1);
		sort(a,c,i+1,hi);
	}
	/**
	 * 交换数组i和j位置的元素
	 * @param a
	 * @param i
	 * @param j
	 */
	public static void exch(Object[] a,int i,int j){
		Object temp = a[i];
		a[i] = a[j];
		a[j] = temp;
	}
	/**
	 * v小于w???
	 * @param v
	 * @param w
	 * @return
	 */
	public static boolean less(Comparable v,Comparable w){
		return v.compareTo(w) < 0;
	}
	/**
	 * v小于w??? 指定比较器
	 * @param v
	 * @param w
	 * @param c
	 * @return
	 */
	public static <T> boolean less(T v,T w,Comparator<T> c){
		return c.compare(v, w) < 0;
	}
	/**
	 * v小于等于w???
	 * @param v
	 * @param w
	 * @return
	 */
	public static boolean lessOrEqual(Comparable v,Comparable w){
		return v.compareTo(w) <= 0;
	}
	/**
	 * 
	 * @param v
	 * @param w
	 * @return
	 */
	public static <T> boolean lessOrEqual(T v,T w,Comparator<T> c){
		return c.compare(v, w) <= 0;
	}
	/**
	 * v大于w？？？
	 * @param v
	 * @param w
	 * @return
	 */
	public static boolean great(Comparable v,Comparable w){
		return v.compareTo(w) > 0;
	}
	/**
	 * v大于w？？？，指定比较器 
	 * @param v
	 * @param w
	 * @param c
	 * @return
	 */
	public static <T> boolean great(T v,T w,Comparator<T>c){
		return c.compare(v, w) > 0;
	}
	/**
	 * v大于等于w？？？
	 * @param v
	 * @param w
	 * @return
	 */
	public static boolean greatOrEqual(Comparable v,Comparable w){
		return v.compareTo(w) >= 0;
	}
	/**
	 * v大于等于w？？？ 指定比较器
	 * @param v
	 * @param w
	 * @param c
	 * @return
	 */
	public static <T> boolean greatOrEqual(T v,T w,Comparator<T>c){
		return c.compare(v, w) >= 0;
	}
	/**
	 * 打印
	 * @param a
	 */
	public static void print(Object[] a){
		Stream.of(a).forEach(System.out::println);
	}
	/**
	 * 是否已经排好序
	 * @param a
	 * @return
	 */
	public static boolean isSorded(Comparable[] a){
		return isSorded(a, 0, a.length-1);
	}
	/**
	 * 是否排好序
	 * @param a
	 * @param lo
	 * @param hi
	 * @return
	 */
	public static boolean isSorded(Comparable[] a,int lo,int hi){
		for (int i = lo+1; i <= hi; i++) 
			if(less(a[i], a[i-1])) return false; 
		return true;
	}
	/**
	 * 是否排好序，指定比较器
	 * @param a
	 * @param c
	 * @return
	 */
	public static <T> boolean isSorded(T[] a,Comparator<T> c){
		return isSorded(a,c, 0, a.length-1);
	}
	/**
	 * 是否排好序，指定比较器
	 * @param a
	 * @param c
	 * @param lo
	 * @param hi
	 * @return
	 */
	public static <T> boolean isSorded(T[] a,Comparator<T> c,int lo,int hi){
		for (int i = lo+1; i <= hi; i++) 
			if(less(a[i], a[i-1], c)) return false;
		return true;
	}
}
