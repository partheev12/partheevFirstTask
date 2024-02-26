package partheev;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 * Servlet implementation class AttendanceServlet
 */
public class AttendanceServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
    /**
     * @see HttpServlet#HttpServlet()
     */
    public AttendanceServlet() {
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
			String date = request.getParameter("date");
			if(date == null || !Utils.validateDate(date)) {
				throw new MyException("date is invalid","error",400);
			}
			AttendanceDbController adc = new AttendanceDbController();
			AttendanceDate ad = adc.getById(sdf.parse(date).getTime());
			if(ad==null)throw new MyException("unable to get","error",500);
			JSONObject data = new JSONObject();
			data.put("present",Utils.ListToJSONArray(ad.presentList));
			data.put("absent", Utils.ListToJSONArray(ad.absentList));
			MySuccess success = new MySuccess("got data","succes",200);
			JSONObject res = success.createResponse();
			res.put("data", data);
			pw.println(res);
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
			if(!obj.containsKey("date") || !Utils.validateDate(obj.get("date").toString())) {
				throw new MyException("give valid date","error",400);
			}
			JSONArray ja = (JSONArray)obj.get("present");
			if(ja==null || ja.size()==0) {
				throw new MyException("give a valid list","error",400);
			}
			Student s = new Student();
			String msg="";
			for(Object a: ja) {
				String id = (String)a;
				if(Utils.validateNumber(id)) {
					StudentDetails sd = new StudentDetails(Integer.parseInt(id));
					sd = s.retrieveStudentById(sd);
					if(sd!=null) {
						AttendanceDbController adc = new AttendanceDbController();
						boolean b = adc.createAttendance(sdf.parse(obj.get("date").toString()).getTime(), sd.id);
						if(!b)throw new MyException("not inserted","error",500);
					}
					else {
						msg += id+" ,";
					}
				}
				else {
					msg += id+" ,";
				}
			}
			if(msg == "") {
				MySuccess success = new MySuccess("all inserted","success",200);
				pw.println(success.createResponse());
			}
			else {
				MySuccess success = new MySuccess(msg+" not inserted and remaining inserted","error",400);
				pw.println(success.createResponse());
			}
			
		}catch(MyException e) {
			pw.println(e.createError());
		}
		catch(Exception e) {
			MyException exception = new MyException(e.getMessage(),"error",500);
			pw.println(exception.createError());
		}
	}

}
