package com.tmj.tools.data.structure;


public class ArrayList<T> {

	private T[] datas;     // 数组，用来存储元素
	
	private int size;			   // 数据大小
	
	private static final int DEFAULT_CAPACITY = 10;
	
	public ArrayList(){
		datas = (T[])new Object[DEFAULT_CAPACITY];
	}
	
	public ArrayList(int capacity){
		datas = (T[])new Object[capacity<=0?DEFAULT_CAPACITY:capacity];
	}
	public boolean add(T item){
		if(size+1>datas.length){       //是否需要扩容
			grow(datas.length*2);
		}
		datas[size++] = item;
		return true;
	}
	public boolean insert(int index,T item){
		if(index<0 || index>size)       // 范围校验
			throw new IllegalArgumentException("index out of limit");
		
		if(size+1>datas.length){       //是否需要扩容
			grow(datas.length*2);
		}
		if(index == size){   //修改最后一个元素
			datas[index++] = item;
		}else{               //中间插入
			System.arraycopy(datas, index, datas, index+1, size-index);
			datas[index++] = item;
		}
		size++;
		return true;
	}

	private void grow(int length) {
		T[] newDatas = (T[])new Object[length];
		System.arraycopy(datas, 0, newDatas, 0, datas.length);
		datas = newDatas;
		newDatas = null;
	}
	
	public T get(int index){
		if(index<0 || index>datas.length)       // 范围校验
			throw new IllegalArgumentException("index out of bounds");
		return datas[index];
	}
	
	public int indexOf(T item){
		int length = datas.length;
		for (int i = 0; i < length; i++) {
			if(item==datas[i])
				return i;
		}
		return -1;
	}
	
	public boolean remove(T item){
		if(item == null){
			for (int i = 0; i < size; i++) {
				if(datas[i]==null){
					removeAt(i);
					return true;
				}
			}
		}else{
			for (int i = 0; i < size; i++) {
				if(datas[i].equals(item)){
					removeAt(i);
					return true;
				}
			}
		}
		return false;
	}

	public boolean removeAt(int index) {
		if(index<0 || index>size-1)
			throw new IllegalArgumentException("out of bounds");
		System.arraycopy(datas, index+1, datas, index, size-1-index);
		datas[--size] = null;
		if(size<=datas.length/4){ //缩小容量
			T[] newDatas = (T[])new Object[datas.length/2];
			System.arraycopy(datas, 0, newDatas, 0, size);
			datas = newDatas;
			newDatas = null;
		}
		return true;
	}
	
	
	public void show(){
		int length = datas.length;
		for (int i = 0; i <length; i++) {
			System.out.println(datas[i]);
		}
	}
	
	public int size(){
		return size;
	}
}
