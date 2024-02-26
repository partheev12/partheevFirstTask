package partheev;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

class StaffDetails{
	int id = 0;
	String name;
	String deptid;
	String qualification;
	String designation;
	StaffDetails(int id){
		this.id = id;
	}
	StaffDetails(String name,String deptid,String qualification,String designation){
		this.name = name;
		this.deptid = deptid;
		this.qualification=qualification;
		this.designation=designation;
	}
}

public class StaffDbController {
	Connection conn = null;
	public StaffDetails createStaff(StaffDetails s) throws SQLException {
		conn = DBINIT.connect();
		try {
			if(conn == null || conn.isClosed()) {
				throw new MyException("db not connected","error",500);
			}
			String sql = "insert into staff(name,deptid,qualification,designation)values"
					+ "(?,?,?,?)";
			PreparedStatement st= conn.prepareStatement(sql);
			st.setString(1, s.name);
			st.setString(2, s.deptid);
			st.setString(3, s.qualification);
			st.setString(4, s.designation);
			int m = st.executeUpdate();
			if(m>=1) {
				return s;
			}
			else {
				throw new MyException("check if any duplicates given","error",400);
			}
		}
		catch(Exception e) {
			return null;
		}
		finally {
			if(conn!=null) {
				conn.close();
			}
		}
	}
	public StaffDetails retreiveStaffById(StaffDetails s) throws SQLException {
		conn = DBINIT.connect();
		try {
			if(conn == null || conn.isClosed()) {
				throw new MyException("db not connected","error",500);
			}
			String sql = "select * from staff where id = ?";
			PreparedStatement st = conn.prepareStatement(sql);
			st.setInt(1,s.id);
			ResultSet result = st.executeQuery();
			if(!result.isBeforeFirst()) {
				throw new MyException("no staff found","error",404);
			}
			while(result.next()) {
				s.name = result.getString("name");
				s.deptid = result.getString("deptid");
				s.qualification=result.getString("qualification");
				s.designation=result.getString("designation");
			}
			return s;
		}
		catch(Exception e) {
			return null;
		}
		finally {
			if(conn!=null) {
				conn.close();
			}
		}
	}
	public StaffDetails updateStaff(StaffDetails s) throws SQLException {
		conn = DBINIT.connect();
		try {
			if(conn == null || conn.isClosed()) {
				throw new MyException("db is not connected","error",500);
			}
			String sql = "update staff set name=?,deptid=?,qualification=?,designation=? where id =?";
			PreparedStatement st = conn.prepareStatement(sql);
			st.setString(1, s.name);
			st.setString(2, s.deptid);
			st.setString(3, s.qualification);
			st.setString(4, s.designation);
			st.setInt(5, s.id);
			int m = st.executeUpdate();
			if(m>=1) {
				return s;
			}
			throw new MyException("not found user","error",404);
		}
		catch(Exception e) {
			return null;
		}
		finally {
			if(conn!=null) {
				conn.close();
			}
		}
	}
	public boolean deleteStaff(StaffDetails s,int... args) throws SQLException, MyException {
		conn = DBINIT.connect();
		try {
			if(conn == null || conn.isClosed()) {
				throw new MyException("db not connected","error",500);
			}
			conn.setAutoCommit(false);
			String sql = "delete from staff where id = ?";
			PreparedStatement st = conn.prepareStatement(sql);
			st.setInt(1, s.id);
			int m = st.executeUpdate();
			if(s.designation.equalsIgnoreCase("HOD")) {
				if(m>=1) {
					sql = "update department set hod=? where deptid=?";
					st = conn.prepareStatement(sql);
					st.setInt(1, args[0]);
					st.setString(2, s.deptid);
					m = st.executeUpdate();
					if(m>=1) {
						sql = "update staff set designation=? where id = ?";
						st = conn.prepareStatement(sql);
						st.setString(1,"HOD");
						st.setInt(2, args[0]);
						m=st.executeUpdate();
						if(m<=0)throw new MyException("no updation in staff","error",500);
					}
					else
					throw new MyException("failed due to internal updation reasons","error",500);
				}
				else {
					throw new MyException("cannot delete","erorr",500);
				}
			}
			conn.commit();
			return true;
		}
		catch(Exception e) {
			conn.rollback();
			throw new MyException(e.getMessage(),"error",500);
		}
		finally {
			if(conn!=null) {
				conn.close();
			}
		}
	}
	public ArrayList<StaffDetails> getDepartmentStaff(String deptid) throws SQLException{
		conn = DBINIT.connect();
		try {
			if(conn==null || conn.isClosed()) {
				throw new MyException("db not connected","error",500);
			}
			String sql = "select * from staff where deptid=?";
			PreparedStatement smt = conn.prepareStatement(sql);
			smt.setString(1, deptid);
			ResultSet result = smt.executeQuery();
			ArrayList<StaffDetails>list=new ArrayList<>();
			while(result.next()) {
				int id = result.getInt("id");
				StaffDetails ans = new StaffDetails(id);
				
				ans.name = result.getString("name");
				ans.deptid=result.getString("deptid");
				ans.qualification=result.getString("qualification");
				ans.designation = result.getString("designation");
				list.add(ans);
			}
			return list;
		}
		catch(Exception e) {
			return null;
		}
		finally {
			if(conn!=null) {
				conn.close();
			}
		}
	}
}
