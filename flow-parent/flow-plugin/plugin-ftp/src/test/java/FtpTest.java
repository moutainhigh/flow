import java.util.HashMap;
import java.util.Map;

import com.aspire.etl.plugins.Ftp;

public class FtpTest {
	public static void main(String[] args) {
		Ftp ftp = new Ftp();
		Map<String, Object> map = new HashMap<>();
		map.put("host", "192.168.98.141");
		map.put("port", 21);
		map.put("username", "ftpuser");
		map.put("password", "071359cht");
		map.put("basePath", "/home/ftpuser/www/images");
		map.put("filePath", "cht");
		map.put("filename", "000.jpg");
		map.put("originFile", "/Users/chenhaitao/Desktop/000.jpg");
		map.put("TASKFLOW", "TASKFLOW");
		ftp.execute(map);
	}
}
