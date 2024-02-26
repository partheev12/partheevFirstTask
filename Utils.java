package partheev;

import java.io.BufferedReader;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.regex.Pattern;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import jakarta.servlet.http.HttpServletRequest;

public class Utils {
	static JSONArray ListToJSONArray(ArrayList<Integer>list) {
		JSONArray ja = new JSONArray();
		for(Integer id: list) {
			ja.add(id);
		}
		return ja;
	}
	static JSONObject ResultToJSON(Result res) {
		JSONObject obj = new JSONObject();
		obj.put("examname", res.examname);
		int totalmarks = 0;
		for(Subject sub:res.sublist) {
			obj.put(sub.subject, sub.marks);
			totalmarks += sub.marks;
		}
		obj.put("totalmarks", totalmarks);
		return obj;
	}
	static DepartmentDetails updateJSONtoDepartment(DepartmentDetails dept,JSONObject obj) {
		if(obj.containsKey("fullname")&&!validateName(obj.get("fullname").toString())) {
			return null;
		}
		if(obj.containsKey("hodid")&&!validateNumber(obj.get("hodid").toString())) {
			return null;
		}
		if(obj.containsKey("fullname"))dept.fullname=obj.get("fullname").toString();
		if(obj.containsKey("hodid"))dept.hodid=Integer.parseInt(obj.get("hodid").toString());
		return dept;
	}
	static DepartmentDetails JSONToDepartment(JSONObject obj) {
		if(!obj.containsKey("deptid")||!validateDepartmentName(obj.get("deptid").toString())) {
			return null;
		}
		if(!obj.containsKey("fullname")||!validateName(obj.get("fullname").toString())) {
			return null;
		}
		if(!obj.containsKey("hodid")||!validateNumber(obj.get("hodid").toString())) {
			return null;
		}
		DepartmentDetails dept = new DepartmentDetails(obj.get("deptid").toString().toUpperCase());
		dept.fullname = obj.get("fullname").toString();
		dept.hodid = Integer.parseInt(obj.get("hodid").toString());
		return dept;
	}
	static JSONObject DepartmentToJSON(DepartmentDetails dept) {
		JSONObject obj = new JSONObject();
		obj.put("deptid", dept.deptid);
		obj.put("fullname", dept.fullname);
		obj.put("hod id", dept.hodid);
		return obj;
	}
	static StaffDetails updateStaffusingJson(StaffDetails sd,JSONObject obj) {
		if(obj.containsKey("name") && !validateName(obj.get("name").toString())) {
			return null;
		}
		if(obj.containsKey("deptid") && !validateDepartment(obj.get("deptid").toString())) {
			return null;
		}
		if(obj.containsKey("qualification") && !validateQualification(obj.get("qualification").toString())) {
			return null;
		}
		if(obj.containsKey("designation") && !validatedesignation(obj.get("designation").toString())) {
			return null;
		}
		if(obj.containsKey("name")) {
			sd.name = obj.get("name").toString();
		}
		if(obj.containsKey("deptid")) {
			sd.deptid = obj.get("deptid").toString().toUpperCase();
		}
		if(obj.containsKey("qualification")) {
			sd.qualification = obj.get("qualification").toString();
		}
		if(obj.containsKey("designation")) {
			sd.designation = obj.get("designation").toString().toUpperCase();
		}
		return sd;
	}
	static JSONObject staffToJSON(StaffDetails s) {
		JSONObject obj = new JSONObject();
		obj.put("id", s.id);
		obj.put("name", s.name);
		obj.put("deptid", s.deptid);
		obj.put("qualification",s.qualification);
		obj.put("designation", s.designation);
		return obj;
	}
	static StaffDetails JSONToStaff(JSONObject obj) {
		if(!obj.containsKey("name")||!validateName(obj.get("name").toString())) {
			return null;
		}
		if(!obj.containsKey("deptid")||!validateDepartment(obj.get("deptid").toString())) {
			return null;
		}
		if(!obj.containsKey("qualification")||!validateQualification(obj.get("qualification").toString())) {
			return null;
		}
		if(!obj.containsKey("designation")||!validatedesignation(obj.get("designation").toString())) {
			return null;
		}
		String name = obj.get("name").toString();
		String deptid = obj.get("deptid").toString().toUpperCase();
		String qualification = obj.get("qualification").toString().toUpperCase();
		String designation = obj.get("designation").toString().toLowerCase();
		StaffDetails s = new StaffDetails(name,deptid,qualification,designation);
		return s;
	}
	static JSONObject parseRequestAndGetBody(HttpServletRequest request) {
		StringBuffer sb = new StringBuffer();
		String line;
		try {
		BufferedReader reader = request.getReader();
		while ((line = reader.readLine()) != null) {
			sb.append(line);
		}
		JSONParser parser = new JSONParser();
		JSONObject obj = (JSONObject) parser.parse(sb.toString());
		return obj;
		}catch(Exception e) {
			MyException exception = new MyException(e.getMessage(),"error",500);
			return exception.createError();
		}
	}
	static boolean validatedesignation(String s) {
		String arr[] = {"professor","asstprofessor","nonteaching"};
		s =s.toLowerCase();
		s.replaceAll(".", "");
		for(String a:arr) {
			if(a.equals(s))return true;
		}
		return false;
	}
	static boolean validateQualification(String s) {
		if(s==null)return false;
		s = s.toUpperCase();
		Pattern p = Pattern.compile("[A-Z]{2,6}");
		return p.matcher(s).matches();
	}
	static boolean validateNumber(String s) {
		if(s==null)return false;
		Pattern p = Pattern.compile("\\d+");
		return p.matcher(s).matches();
	}
	static boolean validateDate(String s) {
		if(s==null)return false;
		 try {
	            SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
	            df.setLenient(false);
	            df.parse(s);
	            return true;
	        } catch (ParseException e) {
	            return false;
	        }
	}
	static boolean validateName(String s) {
		if(s==null)return false;
		s = s.toLowerCase();
		Pattern p = Pattern.compile("[a-z]{3,30}");
		return p.matcher(s).matches();
	}
	static boolean validateBool(String s) {
		if(s==null)return false;
		return s.toLowerCase().equals("true") || s.toLowerCase().equals("false");
	}
	static boolean validateDepartmentName(String s) {
		if(s==null)return false;
		s = s.toUpperCase();
		Pattern p = Pattern.compile("[A-Z]{2,6}");
		return p.matcher(s).matches();
	}
	static boolean validateDepartment(String id) {
		if(id==null)return false;
		DeptDbController ddc = new DeptDbController();
		DepartmentDetails dept = new DepartmentDetails(id.toUpperCase());
		try {
			dept = ddc.retreiveDepartment(dept);
			if(dept==null)return false;
			return true;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			return false;
		}
		
	}
	static boolean checkvalidParam(String s) {
    	if(s.equals("") || s.indexOf(',')!=-1 || !validateNumber(s))return false;
    	return true;
    }
	static boolean CheckvalidparamDept(String s) {
		if(s.equals("")||s.indexOf(',')!=-1||!validateDepartmentName(s))return false;
		return true;
	}
	static StudentDetails parseJsonToStudent(JSONObject obj) throws ParseException {
		if(obj == null) return null;
		if(!obj.containsKey("name")|| !validateName(obj.get("name").toString())) {
			return null;
		}
		if(!obj.containsKey("dob")|| !validateDate(obj.get("dob").toString())) {
			return null;
		}
		if(!obj.containsKey("isbus")|| !validateBool(obj.get("isbus").toString())) {
			return null;
		}
		if(!obj.containsKey("ishostel")|| !validateBool(obj.get("ishostel").toString())) {
			return null;
		}
		if(!obj.containsKey("totalfee") || !validateNumber(obj.get("totalfee").toString())) {
			return null;
		}
		if(!obj.containsKey("balancefee")|| !validateNumber(obj.get("balancefee").toString())) {
			return null;
		}
		if(!obj.containsKey("deptid") || !validateDepartment(obj.get("deptid").toString())) {
			return null;
		}
		int id = 0;
		String name = obj.get("name").toString().toLowerCase();
		String dob = obj.get("dob").toString();
		boolean isbus = Boolean.parseBoolean(obj.get("isbus").toString());
		boolean ishostel = Boolean.parseBoolean(obj.get("ishostel").toString());
		int totalfee = Integer.parseInt(obj.get("totalfee").toString());
		int balancefee = Integer.parseInt(obj.get("balancefee").toString());
		String deptId = obj.get("deptid").toString().toUpperCase();
		Bio sbio = new Bio(name,dob,isbus,ishostel,deptId);
		Fee sfee = new Fee(totalfee,balancefee);
		StudentDetails s = new StudentDetails(id,sbio,sfee);
		return s;
	}
	static StudentDetails updateStudentusingJson(StudentDetails s, JSONObject obj) throws ParseException {
		if(obj == null) return null;
		if(!obj.containsKey("id") || !validateNumber(obj.get("id").toString())) {
			return null;
		}
		if(obj.containsKey("name")&& !validateName(obj.get("name").toString())) {
			return null;
		}
		if(obj.containsKey("dob")&& !validateDate(obj.get("dob").toString())) {
			return null;
		}
		if(obj.containsKey("joindate")&& !validateDate(obj.get("joindate").toString())) {
			return null;
		}
		if(obj.containsKey("isbus")&& !validateBool(obj.get("isbus").toString())) {
			return null;
		}
		if(obj.containsKey("ishostel")&& !validateBool(obj.get("ishostel").toString())) {
			return null;
		}
		if(obj.containsKey("totalfee") && !validateNumber(obj.get("totalfee").toString())) {
			return null;
		}
		if(obj.containsKey("balancefee")&& !validateNumber(obj.get("balancefee").toString())) {
			return null;
		}
		if(obj.containsKey("deptid") && !validateDepartment(obj.get("deptid").toString())) {
			return null;
		}
		// if all are ok then
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		if(obj.containsKey("name")) {
			s.bio.name = obj.get("name").toString().toLowerCase();
		}
		if(obj.containsKey("dob")) {
			String dob = obj.get("dob").toString();
			s.bio.dob = sdf.parse(dob).getTime();
		}
		if(obj.containsKey("joindate")) {
			String joindate = obj.get("joindate").toString();
			s.bio.joindate = sdf.parse(joindate).getTime();
		}
		if(obj.containsKey("isbus")) {
			s.bio.isbus = Boolean.parseBoolean(obj.get("isbus").toString());
		}
		if(obj.containsKey("ishostel")) {
			s.bio.ishostel = Boolean.parseBoolean(obj.get("ishostel").toString());
		}
		if(obj.containsKey("totalfee")) {
			s.fee.totalfee = Integer.parseInt(obj.get("totalfee").toString());
		}
		if(obj.containsKey("balancefee")) {
			s.fee.balancefee = Integer.parseInt(obj.get("balancefee").toString());
		}
		if(obj.containsKey("deptid")) {
			s.bio.deptId = obj.get("deptid").toString();
		}
		return s;
		
	}
	static JSONObject studentBioToJson(StudentDetails s) {
		JSONObject obj = new JSONObject();
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
		obj.put("id",s.id);
		obj.put("name", s.bio.name);
		obj.put("dob", sdf.format(new Date(s.bio.dob)));
		obj.put("joindate", sdf.format(new Date(s.bio.joindate)));
		obj.put("isbus", s.bio.isbus);
		obj.put("ishostel", s.bio.ishostel);
		obj.put("deptid", s.bio.deptId);
		return obj;
	}
}
