
public class StringTest {
	public static void main(String[] args) {
		
		String str2 = "SEUCalvin";//�¼ӵ�һ�д��룬���಻��  
		String str1 = new String("SEU")+ new String("Calvin");      
		System.out.println(str1.intern() == str1);   
		System.out.println(str1 == "SEUCalvin"); 
		
		
		String s = new String("1").intern();  
		//s.intern();  
		String s2 = "1";  
		System.out.println(s == s2);  
		  
		String s3 = new String("1") + new String("1");  
		s3.intern();  
		String s4 = "11";  
		System.out.println(s3 == s4);  
		
		String a =  "b" ;   
	    String b =  "b" ;   
	      
	    System.out.println( a == b);   
	      
	    String d = new String( "d" ).intern() ;   
	    String c = "d" ;  
	    System.out.println( c == d);  
	}
}
