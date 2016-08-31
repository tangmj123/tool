package com.tmj.tools.arithmetic;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

/**
 * 求两个数组的交集
 * @author Tangmj
 *
 */
public class Intersection {

	private static Integer[] arr1 = null;
	
	private static Integer[] arr2 = null;
	
	
//	@BeforeClass
	public static void genDate(){
		arr1 = genArr(100000);
		arr2 = genArr(5000);
		
		Path arr1Path = Paths.get("E:\\test\\arr1.txt");
		Path arr2Path = Paths.get("E:\\test\\arr2.txt");
		try (BufferedWriter bw1 = Files.newBufferedWriter(arr1Path,StandardOpenOption.CREATE);
				BufferedWriter bw2 = Files.newBufferedWriter(arr2Path, StandardOpenOption.CREATE)){
			int len1 = arr1.length;
			int len2 = arr2.length;
			for (int i = 0; i < len1; i++) {
				bw1.write(String.valueOf(arr1[i]));
				bw1.newLine();
			}
			for (int i = 0; i < len2; i++) {
				bw2.write(String.valueOf(arr2[i]));
				bw2.newLine();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Before
	public void init(){
		List<Integer> list1 = new ArrayList<Integer>();
		List<Integer> list2 = new ArrayList<Integer>();
		Path arr1Path = Paths.get("E:\\test\\arr1.txt");
		Path arr2Path = Paths.get("E:\\test\\arr2.txt");
		try(BufferedReader  br1 = Files.newBufferedReader(arr1Path);
				BufferedReader br2 = Files.newBufferedReader(arr2Path)) {
			String line1 = br1.readLine();
			while(line1!=null){
				list1.add(Integer.parseInt(line1));
				line1 = br1.readLine();
			}
			String line2 = br2.readLine();
			while(line2!=null){
				list2.add(Integer.parseInt(line2));
				line2 = br2.readLine();
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		arr1 = new Integer[list1.size()];
		arr2 = new Integer[list2.size()];
		
		arr1 = (Integer[]) list1.toArray(arr1);
		arr2 = (Integer[]) list2.toArray(arr2);
		
		List<Integer> asList = Arrays.asList(arr1);
	}
	
	@Test
	//暴力求解  时间复杂度o(len1*len2)
	public void test1(){
		long start = System.currentTimeMillis();
		List<Integer> intersection = new ArrayList<Integer>();
		int len1 = arr1.length;
		int len2 = arr2.length;
		for (int i = 0; i < len1; i++) {
			for (int j = 0; j < len2; j++) {
				if(arr1[i]==arr2[j]) intersection.add(arr1[i]);
			}
		}
		
		long end = System.currentTimeMillis();
		System.out.println("test1 time cost:"+(end-start));
		
		 
		System.out.println("intersection1"+intersection);
	}
	
	@Test
	//先排序，在比较 时间复杂度o(log2n+len_min)
	public void test2(){
		long start = System.currentTimeMillis();
		List<Integer> intersection = new ArrayList<Integer>();
		Arrays.sort(arr1);
		Arrays.sort(arr2);
		int i =0,j = 0;
		int len1 = arr1.length;
		int len2 = arr2.length;
		while(i<len1 && j<len2){
			if(arr1[i]<arr2[j])      i++;
			else if(arr1[i]>arr2[j]) j++;
			else{ 
				intersection.add(arr1[i]);
				i++;
				j++;
			}
		}
		
		long end = System.currentTimeMillis();
		System.out.println("test2 time cost:"+(end-start));
		
		System.out.println("intersection2"+intersection);
	}
	@Test 
	public void test3(){
		long start = System.currentTimeMillis();
		List<Integer> intersection = new ArrayList<Integer>();
		HashMap<Integer,Integer> map = new HashMap<Integer,Integer>();
		for (int i = 0; i < arr1.length; i++) {
			map.put(arr1[i], arr1[i]);
		}
		for (int i = 0; i < arr2.length; i++) {
			if(map.containsKey(arr2[i])) intersection.add(arr2[i]);
		}
		long end = System.currentTimeMillis();
		System.out.println("test3 time cost:"+(end-start));
		
		 
		System.out.println("intersection3"+intersection);
	}

	@Test
	public void test4(){
		long start = System.currentTimeMillis();
		List<Integer> intersection = new ArrayList<Integer>();
		Set<Integer> set = new HashSet<Integer>();
		for (int i = 0; i < arr1.length; i++) {
			set.add(arr1[i]);
		}
		for (int i = 0; i < arr2.length; i++) {
			if(!set.add(arr2[i])) intersection.add(arr2[i]);
		}
		long end = System.currentTimeMillis();
		System.out.println("test4 time cost:"+(end-start));
		
		 
		System.out.println("intersection4"+intersection);
	}
	
	
	public static Integer[] genArr(int size){
		Random random = new Random();
		Integer[] arr = new Integer[size];
		for (int i = 0; i < size; i++) {
			arr[i] = random.nextInt(10000);
		}
		return arr;
	}
	
	public static void main(String[] args) {
	}
}
