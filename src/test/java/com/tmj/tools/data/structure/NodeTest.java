package com.tmj.tools.data.structure;

import org.junit.Before;
import org.junit.Test;

public class NodeTest {
	private Node<String> node;
	
	@Before
	public void init(){
		Node<String> first = new Node<String>("a");
		Node<String> second = new Node<String>("b");
		Node<String> third = new Node<String>("c");
		Node<String> forth = new Node<String>("d");
		first.setNext(second);
		second.setNext(third);
		third.setNext(forth);
		
		node = first;
		
	}
	
	@Test
	public void inversion(){
		Node<String> pre = node;
		Node<String> curr = pre.getNext();
		Node<String> tmp ;//用来保存curr.next
		while(curr != null){
			tmp = curr.getNext();
			curr.setNext(pre);
			//
			pre = curr;
			curr = tmp;
		}
		node.setNext(null);// 否则stackoverflow
		System.out.println(pre);
	}
	
	
	@SuppressWarnings("rawtypes")
	@Test
	public void testIterable(){
		for (Node n : node) {
			System.out.println(n.getValue());
		}
	}
}
