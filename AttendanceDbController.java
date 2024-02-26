package partheev;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

class AttendanceDate{
	ArrayList<Integer>presentList;
	ArrayList<Integer>absentList;
	AttendanceDate(ArrayList<Integer>presentList,ArrayList<Integer>absentList){
		this.presentList=presentList;
		this.absentList=absentList;
	}
}
class AttendanceId{
	ArrayList<Long>presentDates;
	ArrayList<Long>absentDates;
	AttendanceId(ArrayList<Long>presentDates,ArrayList<Long>absentDates){
		this.presentDates=presentDates;
		this.absentDates=absentDates;
	}
}
public class AttendanceDbController {
	Connection conn = null;
	public boolean createAttendance(long date,int sid) throws SQLException, MyException {
		conn=DBINIT.connect();
		try {
			if(conn == null || conn.isClosed()) {
				throw new MyException("db not connected","error",500);
			}
			String sql = "insert into attendance(date,sid)values(?,?)";
			PreparedStatement smt = conn.prepareStatement(sql);
			smt.setLong(1,date);
			smt.setInt(2, sid);
			int m = smt.executeUpdate();
			if(m==0)return false;
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
	public AttendanceDate getById(long date) throws SQLException, MyException {
		conn = DBINIT.connect();
		try {
			if(conn == null || conn.isClosed()) {
				throw new MyException("db not connected","error",500);
			}
			String sql = "select s.id from student s inner join attendance a on s.id=a.sid where date=?";
			PreparedStatement smt = conn.prepareStatement(sql);
			smt.setLong(1, date);
			ResultSet rs = smt.executeQuery();
			ArrayList<Integer>presentid=new ArrayList<>();
			while(rs.next()) {
				presentid.add(rs.getInt("id"));
			}
			sql = "select s.id from student s   left join attendance a  on  s.id=a.sid and date=? "
					+ "where a.sid is null";
			smt = conn.prepareStatement(sql);
			smt.setLong(1, date);
			rs = smt.executeQuery();
			ArrayList<Integer>absentid=new ArrayList<>();
			while(rs.next()) {
				absentid.add(rs.getInt("id"));
			}
			AttendanceDate ans = new AttendanceDate(presentid,absentid);
			return ans;
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
}
