package com.iflytek;

import java.io.File;
import java.io.IOException;

import org.json.*;;
public class JsonParse {
	private JSONObject obj=null;
	private JSONObject semantic=null;
	private JSONObject slots=null;
	private JSONObject data=null;
	private String name=null;
	private String text=null;
	private String service=null;
	private JSONObject webPage=null;
	private String url=null;
	private static String target=null;
	private static String source=null;
	private static String content=null;
	private JSONArray resultArray=null;
	private String airQuality=null;
	private String date=null;
	private String wind=null;
	private String weather=null;
	private String tempRange=null;
	private String api=null;
	private String quality=null;
	private String keywords=null;
	private String channel=null;
	
	private int rc;
	private JSONObject answer=null;
	private String result=null;
	public JsonParse(String mResult){
		try {
			obj = new JSONObject(mResult);
			text = obj.getString("text");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(getText());
		
		//System.out.println(service);
		jsonClassify();
	}
	public String getText(){
		return text;
	}
	public String getResult(){
		return result;
	}
	public static String getTarget(){
		return target;
	}
	public static String getSource(){
		return source;
	}
	public static String getContent(){
		return content;
	}
	public void jsonClassify(){
		try {
			rc = obj.getInt("rc");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(rc!=0){
			result="没有听懂你说的哟~";
		}
		else{
			try {
				service = obj.getString("service");
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if(service.equals("app")) {
				try {
					semantic=obj.getJSONObject("semantic");
					slots=semantic.getJSONObject("slots");
					name=slots.getString("name");
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				File folder = new File("C:\\ProgramData\\Microsoft\\Windows\\Start Menu\\Programs\\");
				//String keyword = name;
				String path=SearchFile.serachFilePath(folder, name);
				if(path==null){
					result="没有找到这个应用呢";
				}
				else{
					result="您的应用正在打开";
					String cmdCommand=path;
					cmdCommand = "cmd /c start "+cmdCommand.replaceAll(" ","\" \"");
				    //执行CMD代码,返回一个Process  
					Process p;
				    try {
						p = Runtime.getRuntime().exec(cmdCommand);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}    
				}
			}
			else if(service.equals("translation")){
				try {
					semantic=obj.getJSONObject("semantic");
					slots=semantic.getJSONObject("slots");
					target=slots.getString("target");
					source=slots.getString("source");
					content=slots.getString("content");
					Translation translation=new Translation();
					try {
						result=translation.getTransResult();
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
			else if(service.equals("cookbook")||service.equals("gift")||service.equals("flower")||service.equals("shortRent")||service.equals("tv")){
				try {
					webPage=obj.getJSONObject("webPage");
					url=webPage.getString("url");
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				url="rundll32 url.dll,FileProtocolHandler "+url;
				try {
					Runtime.getRuntime().exec(url);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				result="正在为您打开网页";
			}
			else if(service.equals("website")){
				try {
					semantic=obj.getJSONObject("semantic");
					slots=semantic.getJSONObject("slots");
					name=slots.getString("name");
					url=slots.getString("url");
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				url="rundll32 url.dll,FileProtocolHandler "+url;
				try {
					Runtime.getRuntime().exec(url);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				result=name+"正在打开";
			}
			else if(service.equals("weather")){
				try {
					data=obj.getJSONObject("data");
					resultArray=data.getJSONArray("result");
					JSONObject mid=resultArray.getJSONObject(1);
					date=mid.getString("date");
					airQuality=mid.getString("airQuality");
					wind=mid.getString("wind");
					weather=mid.getString("weather");
					tempRange=mid.getString("tempRange");
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				result="日期："+date+"\r\n空气质量："+airQuality+"\r\n风向："+wind+"\r\n天气："+weather+"\r\n温度："+tempRange;
			}
			else if(service.equals("pm25")){
				try {
					data=obj.getJSONObject("data");
					resultArray=data.getJSONArray("result");
					JSONObject mid=resultArray.getJSONObject(0);
					api=mid.getString("api");
					quality=mid.getString("quality");
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				result="空气质量："+api+" "+quality;
			}
			else if(service.equals("websearch")){
				try {
					semantic=obj.getJSONObject("semantic");
					slots=semantic.getJSONObject("slots");
					channel=slots.getString("channel");
					keywords=slots.getString("keywords");
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if(channel.equals("taobao")){
					url="rundll32 url.dll,FileProtocolHandler http://s.taobao.com/search?q="+keywords;
				}
				else{
					url="rundll32 url.dll,FileProtocolHandler http://www.baidu.com/s?ie=utf-8&f=8&rsv_bp=1&tn=baidu&wd="+keywords;
				}
				try {
					Runtime.getRuntime().exec(url);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				result="正在为您打开网页";
			}
			else {
				try {
					answer = obj.getJSONObject("answer");
					result = answer.getString("text");
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				if(service.equals("openQA")||service.equals("chat")||service.equals("faq")||service.equals("baike")||service.equals("calc")||service.equals("datetime")){
			}
				System.out.println(result);
			}
		}
	}
//git test
}
