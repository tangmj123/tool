package com.tmj.tools.data.structure;

import java.util.ArrayList;
import java.util.List;

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
	
	
	@Test
	public void test(){
		Node<String> curr = node;
		Node<String> next = curr.getNext();
		Node<String> tmp;
		while(next!=null){
			tmp = next.getNext();
			next.setNext(curr);
			
			curr = next;
			next = tmp;
		}
		node.setNext(null);
		System.out.println(curr);
	}
	
	
	@Test
	public void genNodeByArray(){
		//逆向生成
		String[] arr = {"a","b","c","d","e"};
		int len = arr.length;
		Node<String> first = null;
		for(int i = len -1;i >= 0;i--){
//			if(first == null) first = new Node<String>(arr[i]);// 不用处理
//			else{
//				Node<String> tmp = new Node<String>(arr[i]);
//				tmp.setNext(first);
//				first = tmp;
//			}
			
			Node<String> tmp = new Node<String>(arr[i]);
			tmp.setNext(first);
			first = tmp;// 更新first
		}
		System.out.println(first);
	}
	
	@Test
	public void genNodeByArray2(){
		//正向生成
		String[] arr = {"a","b","c","d","e"};
		int len = arr.length;
		Node<String> first = null;
		Node<String> tmp = null;
		for(int i = 0;i < len;i++ ){
			if(first == null) first = new Node<String>(arr[i]);
			else if(tmp == null) {
				tmp = new Node<String>(arr[i]);
				first.setNext(tmp);
			}
			else tmp.setNext(tmp = new Node<String>(arr[i]));	
		}
		System.out.println(first);
	}
	
	@Test
	public void genArrayByNode(){
		List<String> list = new ArrayList<String>();
		
		Node<String> next = new Node<String>("");
		next.setNext(node);
		while((next = next.getNext()) != null){
			list.add(next.getValue());
		}
		System.out.println(list);
	}
}

