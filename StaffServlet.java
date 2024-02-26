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
 * Servlet implementation class StaffServlet
 */
public class StaffServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public StaffServlet() {
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
				throw new MyException("id is not given","error",400);
			}
			String paramId = request.getParameter("id");
			if(!Utils.checkvalidParam(paramId)) {
				throw new MyException("not a valid id","error",400);
			}
			StaffDetails sd = new StaffDetails(Integer.parseInt(paramId));
			StaffDbController sdc = new StaffDbController();
			sd = sdc.retreiveStaffById(sd);
			if(sd==null) {
				throw new MyException("not got the user","error",400);
			}
			JSONObject obj = Utils.staffToJSON(sd);
			MySuccess success = new MySuccess("user found","success",200);
			JSONObject ans = success.createResponse();
			ans.put("data", obj);
			pw.println(ans);
		}catch(MyException e) {
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
		response.setContentType("application/json");
		try {
			JSONObject obj = Utils.parseRequestAndGetBody(request);
			StaffDetails sd = Utils.JSONToStaff(obj);
			if(sd == null) {
				throw new MyException("invalid or missing fields","error",400);
			}
			StaffDbController sdb = new StaffDbController();
			sd = sdb.createStaff(sd);
			if(sd == null) {
				throw new MyException("not a successed","error",500);
			}
			MySuccess success = new MySuccess("staff created","success",200);
			pw.println(success.createResponse());
		}catch(MyException e) {
			pw.println(e.createError());
		}
		catch(Exception e) {
			MyException exception = new MyException(e.getMessage(),"error",500);
			pw.println(exception.createError());
		}
	}
	@Override
	protected void doPut(HttpServletRequest request,HttpServletResponse response)throws ServletException,IOException{
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
			StaffDetails sd = new StaffDetails(Integer.parseInt(obj.get("id").toString()));
			StaffDbController sdc = new StaffDbController();
			sd = sdc.retreiveStaffById(sd);
			if(sd == null) {
				throw new MyException("user not found","error",400);
			}
			if(sd.designation.equalsIgnoreCase("HOD") && (obj.containsKey("designation")||obj.containsKey("deptid"))) {
				throw new MyException("cannot change depatment details of a hod","error",400);
			}
			sd = Utils.updateStaffusingJson(sd, obj);
			if(sd == null) {
				throw new MyException("check your input again","error",400);
			}
			sd = sdc.updateStaff(sd);
			//pw.println(sd.qualification);
			if(sd == null) {
				throw new MyException("update Unsuccess check your input","error",400);
			}
			MySuccess success = new MySuccess("user updated","success",200);
			pw.println(success.createResponse());
		}catch(MyException e) {
			pw.println(e.createError());
		}
		catch(Exception e) {
			MyException exception = new MyException(e.getMessage(),"error",500);
			pw.println(exception.createError());
		}
	}
	@Override
	protected void doDelete(HttpServletRequest request,HttpServletResponse response) throws ServletException,IOException{
		PrintWriter pw = response.getWriter();
		response.setContentType("application/json");
		try {
			JSONObject obj = Utils.parseRequestAndGetBody(request);
			if(!obj.containsKey("id") || !Utils.validateNumber(obj.get("id").toString())) {
				throw new MyException("invalid id","error",400);
			}
			StaffDetails sd = new StaffDetails(Integer.parseInt(obj.get("id").toString()));
			StaffDbController sdc = new StaffDbController();
			sd = sdc.retreiveStaffById(sd);
			if(sd == null) {
				throw new MyException("no user","error",404);
			}
			
			if(!sd.designation.equalsIgnoreCase("HOD")) {
				boolean val = sdc.deleteStaff(sd);
				if(val == false) {
					throw new MyException("failure in deletio","error",500);
				}
			}
			else {
				if(!obj.containsKey("newhodid") || !Utils.validateNumber(obj.get("newhodid").toString())) {
					throw new MyException("enter a new hod id to update","error",400);
				}
				StaffDetails newhod = new StaffDetails(Integer.parseInt(obj.get("newhodid").toString()));
				newhod = sdc.retreiveStaffById(newhod);
				if(newhod == null) {
					throw new MyException("enter a valid id","error",400);
				}
				//pw.print(newhod.deptid);
				boolean val = sdc.deleteStaff(sd, newhod.id);
				if(val == false) {
					throw new MyException("failure in deletionnew","error",500);
				}
			}
			MySuccess success = new MySuccess("deleted sucessfully","success",200);
			pw.println(success.createResponse());
		}catch(MyException e) {
			pw.println(e.createError());
		}
		catch(Exception e) {
			MyException exception = new MyException(e.getMessage(),"error",500);
			pw.println(exception.createError());
		}
	}
}
