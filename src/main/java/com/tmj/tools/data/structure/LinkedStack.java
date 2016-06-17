package com.tmj.tools.data.structure;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * 
 * @author Tangmj
 * 栈 FIFO (链表实现)
 */
public class LinkedStack<Item> implements Iterable<Item>{
	private Node<Item> first;   //top of the stack
	private int N;              //the size of stack
	
	private static class Node<Item>{
		Item item;       //item
		Node<Item> next; //the next 
	}
	
	public LinkedStack(){
		first = null;
		N     = 0;
	}
	/**
	 * 压栈
	 * @param item
	 */
	public void push(Item item){
		Node<Item> oldFirst = first;
		first = new Node<Item>();
		first.item = item;
		first.next = oldFirst;  //新Node的next指向原Node
		N ++;
	}
	/**
	 * 出栈
	 * @return
	 */
	public Item pop(){
		if(isEmpty()) throw new NoSuchElementException("stack underflow");
		Item item = first.item;
		first = first.next;     //删除first
		N --;
		return item;
	}
	/**
	 * 尺寸
	 * @return
	 */
	public int size(){
		return N;
	}
	/**
	 * 栈是否为空
	 * @return
	 */
	public boolean isEmpty(){
		return N == 0;
	}
	/**
	 * 返回第一个元素
	 * @return
	 */
	public Item peek(){
		if(isEmpty()) throw new NoSuchElementException();
		return first.item;
	}
	
	@Override
	public Iterator<Item> iterator() {
		return new StackIterator<Item>(first);
	}
	/**
	 * 迭代器
	 * @author Tangmj
	 *
	 * @param <Item>
	 */
	private class StackIterator<Item> implements Iterator<Item>{
		private Node<Item> current;
		public StackIterator(Node<Item> node) {
			this.current = node;
		}
		@Override
		public boolean hasNext() {
			return current != null;
		}

		@Override
		public Item next() {
			if(!hasNext()) throw new NoSuchElementException();
			Item item = current.item;   //得到item
			current = current.next;     //更新current
			return item;                //返回
		}
	}

	public static void main(String[] args) {
		LinkedStack<String> stack = new LinkedStack<String>();
		stack.push("hello");
		stack.push("world");
		String s = stack.pop();
		System.out.println(s);
		stack.push("Hello");
		stack.push("java");
		System.out.println("size="+stack.size());
		
		for (String string : stack) 
			System.out.println(string);
	}
}
