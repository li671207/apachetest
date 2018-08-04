package com.lihl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;

public class Processor extends Thread {
	private Socket socket;
	private InputStream in;
	private PrintStream out;
	private final static String WEB_ROOT=System.getProperty("user.dir")+File.separator+"test";
	
	public Processor() {
		
	}
	
	public Processor(Socket socket) {
		this.socket = socket;
		try {
			in = socket.getInputStream();
			out = new PrintStream(socket.getOutputStream()); 
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	/**
	 * 发送文件
	 * @param fileName
	 */
	private void sendFile(String fileName){
		File file = new File(Processor.WEB_ROOT+fileName);
		if(!file.exists()
				|| !file.isFile()){
			sendErrorMessage(404, "File Not Exists");
			return;
		}
		try {
			FileInputStream in = new FileInputStream(file);
			byte[] content = new byte[(int) file.length()];
			in.read(content);
			out.println("HTTP/1.0 200 getFileContent");
			out.println("content-length:"+content.length);
			
			out.println();
			
			out.write(content);
			
			out.flush();
			out.close();
			in.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	/**
	 * 获取文件内容，解析文件
	 * @param in
	 * @return
	 */
	private String parse(InputStream in){
		BufferedReader br = new BufferedReader(new InputStreamReader(in));
		try {
			//获取请求内容第一行
			String httpMessage = br.readLine();
			System.out.println("===="+httpMessage);
			String[] content = httpMessage.split(" ");
			if(content.length != 3){
				sendErrorMessage(400,"Client query error!");
				return null;
			}
			//[0]:请求方式，[1]:请求文件名称，[2]:协议版本号
			System.out.println("code:"+content[0]+", fileName:"+content[1]+", version:"+content[2]);
			return content[1];
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	private void sendErrorMessage(int code,String errorMessage){
		out.println("HTTP/1.0 "+code+" "+errorMessage);
		
		out.println("content-type: text/html");
		out.println();
		
		out.println("<html>");
		out.println("<title>Error Message</title>");
		out.println("<body>");
		out.println("<h1>ErrorCode: "+code+" ErrorMessage: "+errorMessage);
		out.println("</h1>");
		out.println("</body>");
		out.println("</html>");
		out.flush();
		out.close();
		try {
			in.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	

	public void run() {
		String fileName = parse(in);
		sendFile(fileName);
	}

}
