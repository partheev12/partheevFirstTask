package partheev;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.simple.JSONObject;

/**
 * Servlet implementation class testdb
 */
public class testdb extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private final static Logger logger = Logger.getLogger(testdb.class.getName());
    /**
     * @see HttpServlet#HttpServlet()
     */
    public testdb() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
    
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		PrintWriter pw = response.getWriter();
		response.setContentType("application/json");
		try {
			if(request.getParameter("id")==null) {
				logger.log(Level.WARNING,"id is not present");
				throw new MyException("id is not given","error",400);
			}
			Student s = new Student();
			String paramId = request.getParameter("id");
			if(!Utils.checkvalidParam(paramId)) {
				logger.log(Level.WARNING,"not a valid id");
				throw new MyException("not a valid id","error",400);
			}
			StudentDetails student = new StudentDetails(Integer.parseInt(paramId));
			logger.log(Level.INFO,"created studentdetails object");
			//pw.println(student.id);
			student = s.retrieveStudentById(student);
			logger.log(Level.INFO,"got the student");
			if(student == null) {
				logger.log(Level.WARNING,"student is null");
				throw new MyException("user doesnot exist or server error","error",500);
			}
			MySuccess success = new MySuccess("retrieved successfully","success",200);
			JSONObject ans=success.createResponse();
			ans.put("data", Utils.studentBioToJson(student));
			logger.log(Level.INFO,"successfully completed getstudent");
			pw.print(ans);
		}
		catch(MyException e) {
			pw.println(e.createError());
		}
		catch(Exception e) {
			MyException exception = new MyException(e.getMessage(),"error",500);
			pw.println(exception.createError());
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		PrintWriter pw = response.getWriter();
		response.setContentType("application/html");
		try {
			JSONObject obj = Utils.parseRequestAndGetBody(request);
			StudentDetails student = Utils.parseJsonToStudent(obj);
			if(student == null) {
				throw new MyException("incorrect fields","error",400);
			}
			Student s = new Student();
			student = s.createStudent(student);
			if(student==null) {
				throw new MyException("student either exsist or invalid input","error",400);
			}
			MySuccess success = new MySuccess("user created","success",200);
			pw.println(success.createResponse());
		}
		catch(Exception e) {
			response.setStatus(500);
			MyException exception = new MyException(e.getMessage(),"error",500);
			pw.println(exception.createError());
		}
	}
	@Override
	protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
		PrintWriter pw = response.getWriter();
		response.setContentType("application/json");
		try {
			JSONObject obj = Utils.parseRequestAndGetBody(request);
			if(obj.size()==0) {
				throw new MyException("nothing to update","error",400);
			}
			if(!obj.containsKey("id") || !Utils.validateNumber(obj.get("id").toString())) {
				throw new MyException("invalid id","error",400);
			}
			StudentDetails student = new StudentDetails(Integer.parseInt(obj.get("id").toString()));
			Student s = new Student();
			
			student = s.retrieveStudentById(student);
			
			if(student == null) {
				throw new MyException("user doesnot exist or server error","error",400);
			}
			student = Utils.updateStudentusingJson(student, obj);
			if(student == null) {
				throw new MyException("invalid inputs","error",400);
			}
			
			student = s.updateStudent(student);
			if(student == null) {
				throw new MyException("cannot update student error in input","error",400);
			}
			MySuccess success = new MySuccess("user updated","success",200);
			JSONObject ans=success.createResponse();
			pw.println(ans);
		} catch (NullPointerException e) {
			// TODO Auto-generated catch block
			response.setStatus(400);
			MyException exception = new MyException("empty fieldpp","error",400);
			pw.println(exception.createError());
		}
		catch(MyException e) {
			pw.println(e.createError());
		}
		catch(Exception e) {
			response.setStatus(500);
			MyException exception = new MyException(e.getMessage(),"errormk",500);
			pw.println(exception.createError());
		}
	}
	@Override
	protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
		PrintWriter pw = response.getWriter();
		response.setContentType("application/json");
		try {
			Student s = new Student();
			Map<String, String[]>parameterMap = request.getParameterMap();
			String arrId = parameterMap.get("id")[0].toString();
			if(!Utils.checkvalidParam(arrId)) {
				throw new MyException("invalid id","error",400);
			}
			boolean obj = s.deleteStudentById(Integer.parseInt(arrId));
			if(!obj) {
				throw new MyException("user invalid","error",404);
			}
			MySuccess success = new MySuccess("deleted successfully","success",200);
			pw.println(success.createResponse());
		}catch(NullPointerException e) {
			MyException exception = new MyException("null values are present","error",400);
			pw.println(exception.createError());
		}
		catch(Exception e) {
			MyException exception = new MyException(e.getMessage(),"error",500);
			pw.println(exception.createError());
		}
	}
}