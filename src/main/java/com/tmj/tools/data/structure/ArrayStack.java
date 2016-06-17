package com.tmj.tools.data.structure;

import java.security.NoSuchAlgorithmException;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * 栈 FIFO 基于数组实现
 * @author Tangmj
 *
 */
public class ArrayStack<Item>implements Iterable<Item> {
	
	private Item[] items;
	
	private final int DEFAULT_CAPACITY = 10;
	
	private int size;
	
	public ArrayStack(int capacity){
		capacity = capacity < 0?DEFAULT_CAPACITY:capacity;
		items = (Item[]) new Object[capacity]; 
	}
	/**
	 * 压栈
	 * @param item
	 */
	public void push(Item item){
		if(size >= items.length){   //items满了之后扩容
			Item[] newItems = (Item[]) new Object[2 * items.length];
			System.arraycopy(items, 0, newItems, 0, items.length);
			items = newItems;
		}
		items[size] = item;
		size ++;
	}
	/**
	 * 出栈
	 * @return
	 */
	public Item pop(){
		Item item = items[size-1];
		items[size-1] = null; //gc do it's work
		size --;
		if(size <= items.length/4){    //items中元素小于等于容量四分之一时减小容量
			Item[] newItems = (Item[]) new Object[items.length/2];
			System.arraycopy(items, 0, newItems, 0, size);
			items = newItems;
		}
		return item;
	}
	/**
	 * 是否为空
	 * @return
	 */
	public boolean empty(){
		return size == 0;
	}
	/**
	 * 返回第一个item
	 * @return
	 */
	public Item peek(){
		if(size==0) throw new NoSuchElementException();
		return items[size-1];
	}
	/**
	 * 打印
	 */
	public void print(){
		StringBuffer sb = new StringBuffer("[");
		Item item = null;
		for (Iterator<Item> it = iterator(); it.hasNext()&&(item = it.next()) != null; ) 
			sb.append(item).append(",");
		sb.deleteCharAt(sb.length()-1);
		sb.append("]");
		System.out.println(sb);
	}
	@Override
	public Iterator<Item> iterator() {
		return new StackIterator();
	}
	/**
	 * 迭代辅助类
	 * @author Tangmj
	 *
	 */
	private class StackIterator implements Iterator<Item>{
		int cursor; //the index of next item to return
		
		StackIterator(){
			cursor = size -1;
		}
		@Override
		public boolean hasNext() {
			return cursor >= 0;
		}

		@Override
		public Item next() {
			Item item = items[cursor];
			cursor --;
			return item;
		}
	}
	public static void main(String[] args) {
		ArrayStack<String> stack = new ArrayStack<String>(5);
		System.out.println(stack.empty());
		stack.push("hello");
		stack.push("world");
		stack.push("hello");
		stack.push("java");
		stack.push("ok");
		stack.push(":");
		stack.print();
		String s = stack.pop();
		System.out.println("pop()="+s);
		s = stack.pop();
		System.out.println("pop()="+s);
		s = stack.pop();
		System.out.println("pop()="+s);
		s = stack.pop();
		System.out.println("pop()="+s);
		s = stack.pop();
		System.out.println("pop()="+s);
		stack.print();
		
		s = stack.peek();
		System.out.println(s);
		stack.print();
		System.out.println(stack.empty());
	}
}
