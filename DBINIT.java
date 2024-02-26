package partheev;

import java.sql.Connection;


import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;



public class DBINIT{
	public static  Connection connect() {
		try {
            Context initCtx = new InitialContext();
            Context envCtx = (Context) initCtx.lookup("java:comp/env");
            DataSource ds = (DataSource) envCtx.lookup("jdbc/CollegeManagment");

            Connection conn = ds.getConnection();
            return conn;
        }
        catch(Exception e) {
        	// throw exception
        }
		return null;
	}
    
}
