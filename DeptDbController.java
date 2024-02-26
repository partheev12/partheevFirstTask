package partheev;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

class DepartmentDetails{
	String deptid, fullname;
	int hodid;
	DepartmentDetails(String deptid){
		this.deptid = deptid;
	}
}

public class DeptDbController {
	Connection conn = null;
	public DepartmentDetails retreiveDepartment(DepartmentDetails dept) throws SQLException {
		conn = DBINIT.connect();
		try {
			if(conn == null || conn.isClosed()) {
				throw new MyException("db not connected","error",500);
			}
			String sql = "select * from department where deptid=?";
			PreparedStatement smt = conn.prepareStatement(sql);
			smt.setString(1,dept.deptid);
			ResultSet result = smt.executeQuery();
			if(!result.isBeforeFirst()) {
				return null;
			}
			while(result.next()) {
				dept.fullname = result.getString("fullname");
				dept.hodid = result.getInt("hod");
			}
			return dept;
		}catch(Exception e) {
			return null;
		}
		finally {
			if(conn!=null) {
				conn.close();
			}
		}
	}
	public boolean retreiveDepartmentByNameOrId(String name,String deptid) throws SQLException, MyException {
		conn = DBINIT.connect();
		try {
			if(conn == null || conn.isClosed()) {
				throw new MyException("db not connected","error",500);
			}
			String sql = "select * from department where fullname=? or deptid=?";
			PreparedStatement smt = conn.prepareStatement(sql);
			smt.setString(1, name);
			smt.setString(2, deptid);
			ResultSet result = smt.executeQuery();
			if(!result.isBeforeFirst()) {
				return false;
			}
			return true;
		}catch(Exception e) {
			throw new MyException(e.getMessage(),"error",500);
		}
		finally {
			if(conn!=null) {
				conn.close();
			}
		}
	}
	public boolean retreiveDepartmentByName(String name) throws SQLException, MyException{
		return retreiveDepartmentByNameOrId(name,"");
	}
	public DepartmentDetails createDepartment(DepartmentDetails dept) throws SQLException{
		conn = DBINIT.connect();
		try {
			if(conn == null || conn.isClosed()) {
				throw new MyException("db not connected","error",500);
			}
			String sql = "insert into department(deptid,fullname,hod)values(?,?,?)";
			PreparedStatement smt = conn.prepareStatement(sql);
			smt.setString(1, dept.deptid);
			smt.setString(2, dept.fullname);
			smt.setInt(3,dept.hodid);
			conn.setAutoCommit(false);
			int m = smt.executeUpdate();
			if(m==0)throw new MyException("insertion failed","error",500);
			sql = "update staff set deptid=?,designation=? where id=?";
			smt = conn.prepareStatement(sql);
			smt.setString(1, dept.deptid);
			smt.setString(2, "HOD");
			smt.setInt(3, dept.hodid);
			m = smt.executeUpdate();
			if(m==0)throw new MyException("updation in staff failed","error",500);
			conn.commit();
			return dept;
		}catch(Exception e) {
			conn.rollback();
			return null;
		}
		finally {
			if(conn!=null) {
				conn.close();
			}
		}
	}
	public DepartmentDetails updateDepartment(DepartmentDetails dept,int prevhodid) throws SQLException, MyException {
		conn = DBINIT.connect();
		try {
			if(conn==null||conn.isClosed()) {
				throw new MyException("db not connected","error",500);
			}
			conn.setAutoCommit(false);
			String sql = "update department set fullname=?,hod=? where deptid=?";
			PreparedStatement smt = conn.prepareStatement(sql);
			smt.setString(1, dept.fullname);
			smt.setInt(2, dept.hodid);
			smt.setString(3,dept.deptid);
			int m = smt.executeUpdate();
			if(m==0)throw new MyException("failed to update department","error",500);
			sql = "update staff set designation=?,deptid=? where id=?";
			smt = conn.prepareStatement(sql);
			smt.setString(1, "professor");
			smt.setString(2,dept.deptid);
			smt.setInt(3, prevhodid);
			m = smt.executeUpdate();
			if(m==0)throw new MyException("failed to update staff","error",500);
			smt.setString(1, "HOD");
			smt.setString(2, dept.deptid);
			smt.setInt(3, dept.hodid);
			m = smt.executeUpdate();
			if(m==0)throw new MyException("failed to update prev staff","error",500);
			conn.commit();
			return dept;
		}catch(Exception e) {
			conn.rollback();
			throw new MyException(e.getMessage(),"error",500);
		}
		finally {
			if(conn!=null) {
				conn.close();
			}
		}
	}
	public boolean deleteDepartment(String deptid,String newdeptid) throws SQLException {
		conn = DBINIT.connect();
		try {
			if(conn==null || conn.isClosed()) {
				throw new MyException("db not connected","error",500);
			}
			conn.setAutoCommit(false);
			String sql = "update student set deptid=? where deptid=?";
			PreparedStatement smt=conn.prepareStatement(sql);
			smt.setString(1, newdeptid);
			smt.setString(2, deptid);
			int m = smt.executeUpdate();
			sql = "update staff set deptid=?,designation=? where deptid =?";
			smt = conn.prepareStatement(sql);
			smt.setString(1,newdeptid);
			smt.setString(2,"Asstprofessor");
			smt.setString(3,deptid);
			m = smt.executeUpdate();
			sql = "delete from department where deptid=?";
			smt = conn.prepareStatement(sql);
			smt.setString(1, deptid);
			 m = smt.executeUpdate();
			if(m==0)throw new MyException("no deletion happened","error",400);
			conn.commit();
			return true;
		}
		catch(Exception e) {
			conn.rollback();
			return false;
		}
		finally {
			if(conn!=null) {
				conn.close();
			}
		}
	}
}
