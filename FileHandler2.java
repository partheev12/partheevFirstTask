package partheev;

import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Properties;
import java.util.logging.FileHandler;

public class FileHandler2 extends FileHandler{
	static String timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm").format(new java.util.Date());
	static String pattern = null;
	static int count = 0;
	static int limit = 0;
	static {
		Properties properties = new Properties();

        try (FileInputStream input = new FileInputStream("C:\\Program Files\\Apache Software Foundation\\Tomcat 10.1\\webapps\\CollegeManagement\\WEB-INF\\classes\\logging.properties")) {
            // Load the properties from the file
            properties.load(input);
            pattern=properties.getProperty("partheev.FileHandler2.pattern") + "_"+timeStamp + ".log";
            count = Integer.parseInt(properties.getProperty("partheev.FileHandler2.count"));
            limit = Integer.parseInt(properties.getProperty("partheev.FileHandler2.limit"));
            
        } catch (Exception e) {
            e.printStackTrace();
        }
	}
	public FileHandler2() throws SecurityException, IOException{
		super(pattern, limit,count);
	}
	
}
