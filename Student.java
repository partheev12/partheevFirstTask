package partheev;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

class Bio{
	String name = null;
	Long dob = null;
	Long joindate = null;
	Boolean isbus = null;
	Boolean ishostel = null;
	String deptId = null;
	private SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
	Bio(){
		
	}
	Bio(String name,String sDob,Boolean isbus, Boolean ishostel,String deptId) throws ParseException{
		this.name = name;
		this.dob = sdf.parse(sDob).getTime();
		this.joindate = System.currentTimeMillis();
		this.isbus = isbus;
		this.ishostel = ishostel;
		this.deptId = deptId;
	}
}
class Fee{
	Integer totalfee = null;
	Integer balancefee = null;
	Fee(){
		
	}
	Fee(Integer totalfee, Integer balancefee){
		this.totalfee = totalfee;
		this.balancefee = balancefee;
	}
}
class StudentDetails{
	int id;
	Bio bio;
	Fee fee;
	StudentDetails(int id){
		this.id = id;
	}
	StudentDetails(int id,Bio studentBio,Fee studentFee){
		this.id = id;
		this.bio =  studentBio;
		this.fee = studentFee;
	}
}

public class Student {
	private SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
	Connection conn = null;
	public StudentDetails createStudent(StudentDetails s) throws SQLException, MyException {
		conn = DBINIT.connect();
		try {
			if(conn == null || conn.isClosed()) {
				throw new MyException("db not connected","error",500);
			}
			
			String sql = "insert into student(name,dob,joindate,isbus,ishostel"
						+ ",totalfee,balancefee,deptid)"
						+"values(?,?,?,?,?,?,?,?)";
			PreparedStatement st = conn.prepareStatement(sql);
			//st.setInt(1,s.id);
			st.setString(1, s.bio.name);
			st.setLong(2, s.bio.dob);
			st.setLong(3,s.bio.joindate);
			st.setBoolean(4,s.bio.isbus);
			st.setBoolean(5,s.bio.ishostel);
			st.setInt(6, s.fee.totalfee);
			st.setInt(7, s.fee.balancefee);
			st.setString(8,s.bio.deptId);
			int m = st.executeUpdate();
			if(m>=1) {
				return s;
			}
			else {
				return null;
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
	public StudentDetails retrieveStudentById(StudentDetails s) throws SQLException {
		conn = DBINIT.connect();
		try {
		if(conn == null || conn.isClosed()) {
			throw new MyException("db not connedcted","error",503);
		}
		String sql = "select * from student where id = ?";
		
		PreparedStatement st = conn.prepareStatement(sql);
		st.setInt(1,s.id);
		ResultSet result = st.executeQuery();
		if(!result.isBeforeFirst()) {
			throw new MyException("no user found","error",404);
		}
		StudentDetails ans = new StudentDetails(s.id,new Bio(),new Fee());
		while(result.next()) {
			ans.id = result.getInt("id");
			ans.bio.name = result.getString("name");
			ans.bio.dob = result.getLong("dob");
			ans.bio.isbus=result.getBoolean("isbus");
			ans.bio.ishostel = result.getBoolean("ishostel");
			ans.bio.joindate = result.getLong("joindate");
			ans.bio.deptId = result.getString("deptid");
			ans.fee.totalfee = result.getInt("totalfee");
			ans.fee.balancefee = result.getInt("balancefee");
		}
		return ans;
		}catch(Exception e) {
			
			return null;
		}
		finally {
			if(conn!=null) {
				conn.close();
			}
		}
	}
	public StudentDetails updateStudent(StudentDetails s) throws SQLException, MyException {
		conn = DBINIT.connect();
		
		try {
			if(conn == null || conn.isClosed()) {
				throw new MyException("db not connected","error",503);
			}
			 String sql = "update student set name=?,dob=?,joindate=?,isbus=?,ishostel=?,totalfee=?,balancefee=?,deptid=? where id = ?";
			
			PreparedStatement st = conn.prepareStatement(sql);
			st.setString(1, s.bio.name);
			st.setLong(2, s.bio.dob);
			st.setLong(3,s.bio.joindate);
			st.setBoolean(4,s.bio.isbus);
			st.setBoolean(5,s.bio.ishostel);
			st.setInt(6, s.fee.totalfee);
			st.setInt(7, s.fee.balancefee);
			st.setString(8,s.bio.deptId);
			st.setInt(9, s.id);
			int m = st.executeUpdate();
			if(m>=1) {
				return s;
			}
			else {
				throw new MyException("user not exsist","error",500);
			}
		}catch(Exception e) {
			throw new MyException(e.getMessage(),"error",500);
		}
		finally {
			if(conn!=null) {
				conn.close();
			}
		}
	}
	public boolean deleteStudentById(int id) throws SQLException {
		conn = DBINIT.connect();
		try {
			if(conn==null || conn.isClosed()) {
				throw new MyException("db not connected","error",503);
			}
			String sql = "delete from student where id = ?";
			PreparedStatement st = conn.prepareStatement(sql);
			st.setInt(1,id);
			int m = st.executeUpdate();
			if(m>=1) {
				return true;
			}
			else {
				throw new MyException("user does not exsist","error",404);
			}
		}catch(Exception e) {
			
			return false;
		}
		finally {
			if(conn!=null) {
				conn.close();
			}
		}
	}
	public ArrayList<StudentDetails> getDepartmentStudents(String deptid) throws SQLException {
		conn = DBINIT.connect();
		try {
			if(conn==null || conn.isClosed()) {
				throw new MyException("db not connected","error",500);
			}
			String sql = "select * from student where deptid=?";
			PreparedStatement smt = conn.prepareStatement(sql);
			smt.setString(1, deptid);
			ResultSet result = smt.executeQuery();
			ArrayList<StudentDetails>list=new ArrayList<>();
			while(result.next()) {
				int id = result.getInt("id");
				StudentDetails ans = new StudentDetails(id,new Bio(),new Fee());
				
				ans.bio.name = result.getString("name");
				ans.bio.dob = result.getLong("dob");
				ans.bio.isbus=result.getBoolean("isbus");
				ans.bio.ishostel = result.getBoolean("ishostel");
				ans.bio.joindate = result.getLong("joindate");
				ans.bio.deptId = result.getString("deptid");
				ans.fee.totalfee = result.getInt("totalfee");
				ans.fee.balancefee = result.getInt("balancefee");
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
	public StudentDetails getStudentFee(int id) throws SQLException {
		conn = DBINIT.connect();
		try {
			if(conn==null || conn.isClosed()) {
				throw new MyException("db not connected","error",500);
			}
			String sql = "select name,isbus,ishostel,totalfee,balancefee from student where id = ?";
			PreparedStatement smt = conn.prepareStatement(sql);
			smt.setInt(1, id);
			ResultSet rs = smt.executeQuery();
			if(!rs.isBeforeFirst()) {
				throw new MyException("student not found","error",400);
			}
			StudentDetails s = new StudentDetails(id,new Bio(),new Fee());
			while(rs.next()) {
				s.bio.name = rs.getString("name");
				s.bio.isbus = rs.getBoolean("isbus");
				s.bio.ishostel = rs.getBoolean("ishostel");
				s.fee.totalfee = rs.getInt("totalfee");
				s.fee.balancefee = rs.getInt("balanceFee");
			}
			return s;
		}catch(Exception e) {
			return null;
		}
		finally {
			if(conn!=null) {
				conn.close();
			}
		}
	}
	public StudentDetails updateStudentFee(int amount,StudentDetails sd) throws SQLException {
		conn = DBINIT.connect();
		try {
			if(conn == null || conn.isClosed()) {
				throw new MyException("db not connected","error",500);
			}
			String sql = "update student set balancefee=? where id=?";
			PreparedStatement smt = conn.prepareStatement(sql);
			smt.setInt(1, amount);
			smt.setInt(2, sd.id);
			int m = smt.executeUpdate();
			if(m==0)throw new MyException("no updation happened","error",500);
			return sd;
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
