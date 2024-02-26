package partheev;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Savepoint;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

class Result{
	String examname;
	ArrayList<Subject>sublist;
	Result(String examname,ArrayList<Subject>sublist){
		this.examname = examname;
		this.sublist = sublist;
	}
}
class Subject{
	String subject;
	int marks;
	Subject(String subject,int marks){
		this.subject=subject;
		this.marks=marks;
	}
}

public class ResultDbController {
	Connection conn = null;
	public int getExamByName(String name) throws SQLException {
		conn = DBINIT.connect();
		try {
			if(conn == null || conn.isClosed()) {
				throw new MyException("db not connected","error",500);
			}
			String sql = "select id from exams where name=?";
			PreparedStatement smt = conn.prepareStatement(sql);
			smt.setString(1, name);
			ResultSet rs = smt.executeQuery();
			if(!rs.isBeforeFirst()) {
				return -1;
			}
			int id = -1;
			if(rs.next()) {
				id = rs.getInt("id");
			}
			return id;
		}catch(Exception e) {
			return -1;
		}
		finally {
			if(conn!=null) {
				conn.close();
			}
		}
	}
	public int getSubjectById(String name) throws SQLException{
		conn = DBINIT.connect();
		try {
			if(conn == null || conn.isClosed()) {
				throw new MyException("db not connected","error",500);
			}
			String sql = "select id from subject where name=?";
			PreparedStatement smt = conn.prepareStatement(sql);
			smt.setString(1, name);
			ResultSet rs = smt.executeQuery();
			if(!rs.isBeforeFirst()) {
				return -1;
			}
			int id = -1;
			if(rs.next()) {
				id = rs.getInt("id");
			}
			return id;
		}catch(Exception e) {
			return -1;
		}
		finally {
			if(conn!=null) {
				conn.close();
			}
		}
	}
	public Result getResultByIdAndExam(Result result,int sid,int eid) throws SQLException, MyException {
		conn = DBINIT.connect();
		try {
			if(conn == null || conn.isClosed()) {
				throw new MyException("db not connected","error",500);
			}
			String sql = "select s.name,r.marks from exams e,subject s,results r "
					+ "where r.studentid=? and r.examid=? and e.id=r.examid and s.id=r.subid";
			PreparedStatement smt = conn.prepareStatement(sql);
			smt.setInt(1, sid);
			smt.setInt(2, eid);
			ResultSet rs = smt.executeQuery();
			if(!rs.isBeforeFirst()) {
				return null;
			}
			while(rs.next()) {
				Subject sub = new Subject(rs.getString("name"),rs.getInt("marks"));
				result.sublist.add(sub);
			}
			return result;
		}
		catch(Exception e) {
			throw new MyException(e.getMessage(),"error",500);
		}
		finally {
			if(conn!=null) {
				conn.close();
			}
		}
	}
	public ArrayList<Result> getResultById(int sid) throws MyException, SQLException{
		conn = DBINIT.connect();
		try {
			if(conn == null || conn.isClosed()) {
				throw new MyException("db not connected","error",500);
			}
			String sql = "select s.name,r.marks,e.name as examname from exams e,subject s,results r "
					+ "where r.studentid=?  and e.id=r.examid and s.id=r.subid";
			PreparedStatement smt = conn.prepareStatement(sql);
			smt.setInt(1, sid);
			ResultSet rs = smt.executeQuery();
			if(!rs.isBeforeFirst()) {
				return null;
			}
			ConcurrentHashMap<String,ArrayList<Subject>>map = new ConcurrentHashMap<>();
			while(rs.next()) {
				String examname = rs.getString("examname");
				if(!map.containsKey(examname)) {
					map.put(examname, new ArrayList<>());
				}
				map.get(examname).add(new Subject(rs.getString("name"),rs.getInt("marks")));
			}
			ArrayList<Result>result = new ArrayList<>();
			for(Map.Entry<String, ArrayList<Subject>>entry : map.entrySet()) {
				result.add(new Result(entry.getKey(),entry.getValue()));
			}
			return result;
		}
		catch(Exception e) {
			throw new MyException(e.getMessage(),"error",500);
		}
		finally {
			if(conn!=null) {
				conn.close();
			}
		}
	}
	public boolean createResult(int sid,int eid,ArrayList<pair>sublist) throws MyException, SQLException {
		conn = DBINIT.connect();
		try {
			if(conn == null || conn.isClosed()) {
				throw new MyException("db not connected","error",400);
			}
			conn.setAutoCommit(false);
			for(pair p : sublist) {
				String sql = "insert into results(studentid,examid,subid,marks)values(?,?,?,?)";
				PreparedStatement smt = conn.prepareStatement(sql);
				smt.setInt(1, sid);
				smt.setInt(2, eid);
				smt.setInt(3, p.subid);
				smt.setInt(4, p.marks);
				int m = smt.executeUpdate();
				if(m == 0)throw new MyException("insertion failed","error",500);
			}
			conn.commit();
			return true;
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
	public boolean updateResult(int sid,int eid,int subid,int marks) throws SQLException, MyException {
		conn = DBINIT.connect();
		try {
			if(conn == null || conn.isClosed()) {
				throw new MyException("db not connected","error",500);
			}
			String sql = "update results set marks=? where studentid=? and examid=? and subid=?";
			PreparedStatement smt = conn.prepareStatement(sql);
			smt.setInt(1, marks);
			smt.setInt(2, sid);
			smt.setInt(3, eid);
			smt.setInt(4, subid);
			int m = smt.executeUpdate();
			if(m == 0)throw new MyException("no updation done either student didnt gave exam or server error","error",500);
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
}
