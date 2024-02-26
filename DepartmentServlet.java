package partheev;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Level;

import org.json.simple.JSONObject;

/**
 * Servlet implementation class DepartmentServlet
 */
public class DepartmentServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public DepartmentServlet() {
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
				throw new MyException("deptid is not given","error",400);
			}
			String paramId = request.getParameter("deptid");
			if(!Utils.CheckvalidparamDept(paramId)) {
				throw new MyException("not a valid id","error",400);
			}
			DepartmentDetails dept = new DepartmentDetails(paramId.toUpperCase());
			DeptDbController ddc = new DeptDbController();
			dept = ddc.retreiveDepartment(dept);
			if(dept == null) {
				throw new MyException("user not found","error",400);
			}
			MySuccess success = new MySuccess("user found","success",200);
			JSONObject obj = new JSONObject();
			obj = success.createResponse();
			JSONObject deptJSON = Utils.DepartmentToJSON(dept);
			obj.put("data", deptJSON);
			pw.println(obj);
		}catch(MyException e) {
			pw.println(e.createError());
		}
		catch(Exception e) {
			MyException exception = new MyException("internal server error","error",500);
			pw.println(exception.createError());
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		PrintWriter pw = response.getWriter();
		response.setContentType("application/json");
		try {
			JSONObject obj = Utils.parseRequestAndGetBody(request);
			DepartmentDetails dept = Utils.JSONToDepartment(obj);
			if(dept==null) {
				throw new MyException("invalid or less number of fields","error",400);
			}
			DeptDbController ddc = new DeptDbController();
			if(ddc.retreiveDepartmentByNameOrId(dept.fullname, dept.deptid)) {
				throw new MyException("already department exsists with id or name","error",400);
			}
			StaffDetails sd = new StaffDetails(dept.hodid);
			StaffDbController sdc = new StaffDbController();
			sd = sdc.retreiveStaffById(sd);
			if(sd == null) {
				throw new MyException("no staff with id","error",400);
			}
			dept = ddc.createDepartment(dept);
			if(dept==null) {
				throw new MyException("failed to insert or update","error",500);
			}
			MySuccess success = new MySuccess("created successfully","success",200);
			pw.println(success.createResponse());
		}catch(MyException e) {
			pw.println(e.createError());
		}
		catch(Exception e) {
			MyException exception = new MyException("internal server error","error",500);
			pw.println(exception.createError());
		}
	}
	@Override
	protected void doPut(HttpServletRequest request,HttpServletResponse response) throws ServletException,IOException{
		PrintWriter pw = response.getWriter();
		response.setContentType("application/json");
		try {
			JSONObject obj = Utils.parseRequestAndGetBody(request);
			if(!obj.containsKey("deptid")||!Utils.validateDepartmentName(obj.get("deptid").toString())) {
				throw new MyException("invalid id","error",400);
			}
			if(obj.size()==1)throw new MyException("nothing to update","error",400);
			DepartmentDetails dept = new DepartmentDetails(obj.get("deptid").toString().toUpperCase());
			DeptDbController ddc = new DeptDbController();
			dept = ddc.retreiveDepartment(dept);
			if(dept==null) {
				throw new MyException("no such department","error",400);
			}
			
			
			if(obj.containsKey("fullname") && ddc.retreiveDepartmentByName(obj.get("fullname").toString())){
				throw new MyException("department with this name already exsists","error",400);
			}
			int prevhodid = dept.hodid;
			dept = Utils.updateJSONtoDepartment(dept, obj);
			if(dept == null)throw new MyException("invalid fields","error",400);
			StaffDetails sd = new StaffDetails(dept.hodid);
			StaffDbController sdc = new StaffDbController();
			sd = sdc.retreiveStaffById(sd);
			if(sd == null) {
				throw new MyException("staff do not exsist","error",400);
			}
			dept = ddc.updateDepartment(dept, prevhodid);
			if(dept == null) {
				throw new MyException("internal upadtion failed in db","error",500);
			}
			MySuccess success = new MySuccess("updated successfully","success",200);
			pw.println(success.createResponse());
		}catch(MyException e) {
			pw.print(e.createError());
		}
		catch(Exception e) {
			MyException exception = new MyException(e.getMessage(),"error",500);
			pw.print(exception.createError());
		}
	}
	@Override
	protected void doDelete(HttpServletRequest request,HttpServletResponse response)throws ServletException,IOException {
		PrintWriter pw = response.getWriter();
		response.setContentType("application/json");
		try {
			JSONObject obj = Utils.parseRequestAndGetBody(request);
			if(!obj.containsKey("deptid")||!Utils.validateDepartment(obj.get("deptid").toString())) {
				throw new MyException("wanted department id","error",400);
			}
			if(!obj.containsKey("newdeptid")||!Utils.validateDepartment(obj.get("newdeptid").toString())) {
				throw new MyException("new deptid is invalid","error",400);
			}
			DeptDbController dcc = new DeptDbController();
			boolean ans = dcc.deleteDepartment(obj.get("deptid").toString().toUpperCase(), obj.get("newdeptid").toString().toUpperCase());
			if(ans == false) {
				throw new MyException("unbale to delete","error",500);
			}
			MySuccess success = new MySuccess("deleted successfully","success",200);
			pw.println(success.createResponse());
		}
		catch(MyException e) {
			pw.println(e.createError());
		}
		catch(Exception e) {
			MyException exception = new MyException("internal server error","error",500);
			pw.println(exception.createError());
		}
	}
}
