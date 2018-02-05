package com.aspire.thread.test;

public class JavaTest2 {
	 public static void main(String[] args) {  
	        Node head = new Node(0);  
	        Node node1 = new Node(1);  
	        Node node2 = new Node(2);  
	        Node node3 = new Node(3);  
	  
	        head.setNext(node1);  
	        node1.setNext(node2);  
	        node2.setNext(node3);  
	  
	        // ��ӡ��תǰ������  
	        Node h = head;  
	        while (null != h) {  
	            System.out.print(h.getData() + " ");  
	            h = h.getNext();  
	        }  
	        // ���÷�ת����  
	        // head = reverse1(head);  
	        head = reverse2(head);  
	  
	        System.out.println("\n**************************");  
	        // ��ӡ��ת��Ľ��  
	        while (null != head) {  
	            System.out.print(head.getData() + " ");  
	            head = head.getNext();  
	        }  
	    }  
	  
	    /** 
	     * ����������ǰ�ڵ����һ���ڵ㻺�����ĵ�ǰ�ڵ�ָ�� 
	     */  
	    public static Node reverse2(Node head) {  
	        if (head == null)  
	            return head;  
	        Node pre = head;// ��һ���  
	        Node cur = head.getNext();// ��ǰ���  
	        Node tmp;// ��ʱ��㣬���ڱ��浱ǰ����ָ���򣨼���һ��㣩  
	        while (cur != null) {// ��ǰ���Ϊnull��˵��λ��β���  
	            tmp = cur.getNext();  
	            cur.setNext(pre);// ��תָ�����ָ��  
	  
	            // ָ�������ƶ�  
	            pre = cur;  
	            cur = tmp;  
	        }  
	        // ���ԭ�����ͷ�ڵ��ָ������Ϊnull�������������ͷ��㣬��ԭ�����β���  
	        head.setNext(null);  
	          
	        return pre;  
	    }  
	}  
	  
