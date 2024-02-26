package partheev;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

import org.json.simple.JSONObject;

/**
 * Servlet implementation class StudentFeeServlet
 */
public class StudentFeeServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public StudentFeeServlet() {
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
				throw new MyException("id is required","error",400);
			}
			String param = request.getParameter("id");
			if(!Utils.checkvalidParam(param)) {
				throw new MyException("id should be an number","error",400);
			}
			Student s = new Student();
			StudentDetails sd = s.getStudentFee(Integer.parseInt(param));
			if(sd == null) {
				throw new MyException("No such student","error",400);
			}
			JSONObject obj = new JSONObject();
			obj.put("id", param);
			obj.put("totalfee", sd.fee.totalfee);
			obj.put("balancefee", sd.fee.balancefee);
			obj.put("name", sd.bio.name);
			obj.put("isbus", sd.bio.isbus);
			obj.put("ishostel", sd.bio.ishostel);
			MySuccess success = new MySuccess("found student","success",200);
			JSONObject ans = success.createResponse();
			ans.put("data", obj);
			pw.print(ans);
		}catch(MyException e) {
			pw.println(e.createError());
		}
		catch(Exception e) {
			MyException ex = new MyException("internal error","error",500);
			pw.println(ex.createError());
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		PrintWriter pw = response.getWriter();
		response.setContentType("application/json");
		try {
			JSONObject obj = Utils.parseRequestAndGetBody(request);
			if(!obj.containsKey("id")||!Utils.validateNumber(obj.get("id").toString())) {
				throw new MyException("id is required","error",400);
			}
			if(!obj.containsKey("amount")||!Utils.validateNumber(obj.get("amount").toString())) {
				throw new MyException("amount is reuired and a valid one","error",400);
			}
			StudentDetails sd = new StudentDetails(Integer.parseInt(obj.get("id").toString()));
			Student s = new Student();
			sd = s.retrieveStudentById(sd);
			if(sd == null) {
				throw new MyException("no student found","error",400);
			}
			if(sd.fee.balancefee==0) {
				throw new MyException("no need of amount fee already cleared","error",400);
			}
			int amount = Integer.parseInt(obj.get("amount").toString());
			int balance = sd.fee.balancefee - amount;
			boolean isClear = false;
			if(balance>0) {
				sd = s.updateStudentFee(balance, sd);
			}
			else {
				isClear = true;
				balance = Math.abs(balance);
				sd = s.updateStudentFee(0,sd);
			}
			if(sd == null)throw new MyException("not updated fee","error",500);
			String message ="";
			if(isClear) {
				message = "fee cleared pls take excess amount of "+String.valueOf(balance);
			}
			else {
				message = "balance fee is "+String.valueOf(balance);
			}
			MySuccess success = new MySuccess(message,"success",200);
			pw.println(success.createResponse());
		}catch(MyException e) {
			pw.println(e.createError());
		}
		catch(Exception e) {
			MyException ex = new MyException("internal error","error",500);
			pw.println(ex.createError());
		}
	}

}
