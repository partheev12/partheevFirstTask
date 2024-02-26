package partheev;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 * Servlet implementation class DepartmentStudentsServlet
 */
public class DepartmentStudentsServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public DepartmentStudentsServlet() {
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
			if(request.getParameter("deptid")==null) {
				throw new MyException("deptid is needed","error",400);
			}
			String param = request.getParameter("deptid");
			if(!Utils.validateDepartment(param)) {
				throw new MyException("enter exsisting department","error",400);
			}
			Student s = new Student();
			ArrayList<StudentDetails>list = s.getDepartmentStudents(param.toUpperCase());
			JSONArray ja = new JSONArray();
			for(StudentDetails sd:list) {
				ja.add(Utils.studentBioToJson(sd));
			}
			MySuccess success = new MySuccess("data got","success",200);
			JSONObject ans = success.createResponse();
			ans.put("data", ja);
			pw.println(ans);
		}catch(MyException e) {
			pw.print(e.createError());
		}
		catch(Exception e) {
			MyException exception = new MyException("internal server error","error",500);
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
