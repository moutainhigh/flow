
public class ProTest extends AbstractConfig{
//	static{
//		id = "11";
//		name = "22";
//	}
	public static void main(String[] args) throws InterruptedException {
		init();
		System.out.println(id + name);
		Thread.sleep(Long.MAX_VALUE);
	}
}
