package partheev;

import org.json.simple.JSONObject;

@SuppressWarnings("serial")
public class MyException extends Exception{
	String msg;
	String status;
	int statusCode;
	public MyException(String msg,String status,int statusCode) {
		super(msg);
		this.msg = msg;
		this.status = status;
		this.statusCode = statusCode;
	}
	public JSONObject createError() {
		JSONObject obj = new JSONObject();
		obj.put("message", msg);
		obj.put("status", status);
		obj.put("statuscode", statusCode);
		return obj;
	}
}
class MySuccess{
	String msg;
	String status;
	int statusCode;
	public MySuccess(String msg,String status, int statusCode) {
		this.msg = msg;
		this.status = status;
		this.statusCode = statusCode;
	}
	public JSONObject createResponse() {
		JSONObject obj = new JSONObject();
		obj.put("message", msg);
		obj.put("status", status);
		obj.put("statuscode", statusCode);
		return obj;
	}
}