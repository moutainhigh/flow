package com.aspire.thread.test;

public class javatest1 {
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
	        head = Reverse1(head);  
	  
	        System.out.println("\n**************************");  
	        // ��ӡ��ת��Ľ��  
	        while (null != head) {  
	            System.out.print(head.getData() + " ");  
	            head = head.getNext();  
	        }  
	    }  
	  
	    /** 
	     * �ݹ飬�ڷ�ת��ǰ�ڵ�֮ǰ�ȷ�ת�����ڵ� 
	     */  
	    public static Node Reverse1(Node head) {  
	        // head������ǰһ��㣬head.getNext()�ǵ�ǰ��㣬reHead�Ƿ�ת���������ͷ���  
	        if (head == null || head.getNext() == null) {  
	            return head;// ��Ϊ�������ߵ�ǰ�����β��㣬��ֱ�ӻ���  
	        }  
	        Node reHead = Reverse1(head.getNext());// �ȷ�ת�����ڵ�head.getNext()  
	        head.getNext().setNext(head);// ����ǰ����ָ����ָ��ǰһ���  
	        head.setNext(null);// ǰһ����ָ������Ϊnull;  
	        return reHead;// ��ת���������ͷ���  
	    }  
	}  
	  
	    class Node {  
	        private int Data;// ������  
	        private Node Next;// ָ����  
	  
	        public Node(int Data) {  
	            // super();  
	            this.Data = Data;  
	        }  
	  
	        public int getData() {  
	            return Data;  
	        }  
	  
	        public void setData(int Data) {  
	            this.Data = Data;  
	        }  
	  
	        public Node getNext() {  
	            return Next;  
	        }  
	  
	        public void setNext(Node Next) {  
	            this.Next = Next;  
	        }  
}
