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
 * Servlet implementation class ResultServlet
 */
public class ResultServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ResultServlet() {
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
			if(request.getParameter("studentid")==null) {
				throw new MyException("student id is must","error",400);
			}
			String paramid = request.getParameter("studentid");
			if(!Utils.checkvalidParam(paramid)) {
				throw new MyException("should be a number","error",400);
			}
			
			Student s = new Student();
			int sid = Integer.parseInt(paramid);
			StudentDetails sd = new StudentDetails(sid);
			sd = s.retrieveStudentById(sd);
			if(sd == null) {
				throw new MyException("student not found","error",400);
			}
			ResultDbController rdc = new ResultDbController();
			if(request.getParameter("exam")==null) {
				ArrayList<Result>list = rdc.getResultById(sid);
				if(list==null) {
					throw new MyException("no results","error",500);
				}
				MySuccess success = new MySuccess("data got","success",200);
				JSONObject res = success.createResponse();
				JSONObject data = new JSONObject();
				data.put("student name", sd.bio.name);
				data.put("dept", sd.bio.deptId);
				data.put("student id", sd.id);
				JSONArray ja = new JSONArray();
				for(Result result : list) {
					ja.add(Utils.ResultToJSON(result));
				}
				data.put("results", ja);
				res.put("data", data);
				pw.println(res);
				return;
			}
			String paramName = request.getParameter("exam");
			
			int examid = rdc.getExamByName(paramName);
			if(examid == -1) {
				throw new MyException("exam not found","error",400);
			}
			Result result = new Result(paramName,new ArrayList<Subject>());
			result = rdc.getResultByIdAndExam(result, sid, examid);
			if(result == null) {
				throw new MyException("unable to retureve","error",500);
			}
			JSONObject resultJSON = Utils.ResultToJSON(result);
			JSONObject data = new JSONObject();
			data.put("student name", sd.bio.name);
			data.put("dept", sd.bio.deptId);
			data.put("student id", sd.id);
			data.put("result", resultJSON);
			MySuccess success = new MySuccess("got results","success",200);
			JSONObject res = success.createResponse();
			res.put("data", data);
			pw.println(res);
		}catch(MyException e) {
			pw.println(e.createError());
		}
		catch(Exception e) {
			MyException ex = new MyException(e.getMessage(),"error",500);
			pw.println(ex.createError());
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
			if(!obj.containsKey("sid") || !Utils.validateNumber(obj.get("sid").toString())) {
				throw new MyException("sid is invalid","error",400);
			}
			if(!obj.containsKey("exam")) {
				throw new MyException("need exam name","error",400);
			}
			if(!obj.containsKey("subjects")) {
				throw new MyException("need subjects to insert","error",400);
			}
			Student s = new Student();
			StudentDetails sd = new StudentDetails(Integer.parseInt(obj.get("sid").toString()));
			sd = s.retrieveStudentById(sd);
			if(sd == null) {
				throw new MyException("no such student","error",400);
			}
			ResultDbController rdc = new ResultDbController();
			int eid = rdc.getExamByName(obj.get("exam").toString());
			if(eid == -1) {
				throw new MyException("enter a valid exam name","error",400);
			}
			JSONObject subjects = (JSONObject) obj.get("subjects");
			ArrayList<pair>validSub=new ArrayList<>();
			for(Object subname:subjects.keySet()) {
				String nameOfSub = (String)subname;
				String marks = subjects.get(nameOfSub).toString();
				if(!Utils.validateNumber(marks) || !(Integer.parseInt(marks)>=0 && Integer.parseInt(marks)<=100)) {
					throw new MyException("enter valid marks","error",400);
				}
				int subid = rdc.getSubjectById(nameOfSub);
				if(subid == -1) {
					throw new MyException(nameOfSub+"not exsist","error",400);
				}
				validSub.add(new pair(subid,Integer.parseInt(marks)));
			}
			boolean b = rdc.createResult(sd.id, eid, validSub);
			if(!b) {
				throw new MyException("not created results","error",500);
			}
			MySuccess success = new MySuccess("created","success",200);
			pw.println(success.createResponse());
			
		}catch(MyException e) {
			pw.println(e.createError());
		}
		catch(Exception e) {
			MyException ex = new MyException(e.getMessage(),"error",500);
			pw.println(ex.createError());
		}
		
	}
	@Override
	protected void doPut(HttpServletRequest request,HttpServletResponse response)throws ServletException,IOException{
		PrintWriter pw = response.getWriter();
		response.setContentType("application/json");
		try {
			JSONObject obj = Utils.parseRequestAndGetBody(request);
			if(!obj.containsKey("sid") || !Utils.validateNumber(obj.get("sid").toString())) {
				throw new MyException("sid is invalid","error",400);
			}
			if(!obj.containsKey("exam")) {
				throw new MyException("need exam name","error",400);
			}
			if(!obj.containsKey("subject")) {
				throw new MyException("need subjects to update","error",400);
			}
			if(!obj.containsKey("marks")) {
				throw new MyException("need marks to update","error",400);
			}
			String marks = obj.get("marks").toString();
			if(!Utils.validateNumber(marks)||!(Integer.parseInt(marks)>=0 && Integer.parseInt(marks)<=100)) {
				throw new MyException("need valid marks","error",400);
			}
			Student s = new Student();
			StudentDetails sd = new StudentDetails(Integer.parseInt(obj.get("sid").toString()));
			sd = s.retrieveStudentById(sd);
			if(sd == null) {
				throw new MyException("no such student","error",400);
			}
			ResultDbController rdc = new ResultDbController();
			int eid = rdc.getExamByName(obj.get("exam").toString());
			if(eid == -1) {
				throw new MyException("enter a valid exam name","error",400);
			}
			int subid = rdc.getSubjectById(obj.get("subject").toString());
			if(subid == -1) {
				throw new MyException("enter a valid subject","error",400);
			}
			boolean b = rdc.updateResult(sd.id, eid, subid, Integer.parseInt(marks));
			if(!b) {
				throw new MyException("updation failed","error",500);
			}
			MySuccess success = new MySuccess("updated","success",200);
			pw.println(success.createResponse());
			
		}catch(MyException e) {
			pw.println(e.createError());
		}
		catch(Exception e) {
			MyException ex = new MyException(e.getMessage(),"error",500);
			pw.println(ex.createError());
		}
	}
}
class pair{
	int subid,marks;
	pair(int subid,int marks){
		this.subid = subid;
		this.marks = marks;
	}
}