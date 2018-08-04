package com.lihl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Webserver {
	
	public static void Socket(int port) {
		try {
			ServerSocket serverSocket = new ServerSocket(port);
			while (true) {
				Socket socket = serverSocket.accept();
				Processor processor = new Processor(socket);
				processor.start();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	public static void main(String[] args) throws IOException {
		int port = 80;
		if(args.length==1){
			port = Integer.parseInt(args[0]);
		}
		Socket(port);
		
	}

}
