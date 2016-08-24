package com.tmj.tools.data.structure;

import java.util.Iterator;

@SuppressWarnings({"rawtypes","unchecked"})
public class Node <T> implements Iterable<Node>{
	private T value;
	private Node<T> next;
	
	public Node(T value){
		this.value = value;
	}
	public T getValue() {
		return value;
	}
	public void setValue(T value) {
		this.value = value;
	}
	public Node<T> getNext() {
		return next;
	}
	public void setNext(Node<T> next) {
		this.next = next;
	}
	@Override
	public String toString() {
		return "Node [value=" + value + ", next=" + next + "]";
	}
	
	@Override
	public Iterator<Node> iterator() {
		return new NodeIterator(this);
	}
	
	private static class NodeIterator implements Iterator{
		private Node curr;
		
		public NodeIterator(Node curr){
			this.curr = curr;
		}
		@Override
		public boolean hasNext() {
			return curr != null;
		}

		@Override
		public Object next() {
			Node n = curr;
			curr = n.getNext();
			return n;
		}
	}
}
