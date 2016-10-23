import java.io.*;
import java.net.*;

public class Server {
	
	public static final int PORT=3333;

	public static void main(String[] args) {
				
		try {
			ServerSocket serverSocket=new ServerSocket(PORT);
			System.out.println("服务器已启动....");
			//监听是否有连接请求
			while(true){
				//一旦有堵塞，表示连接
				Socket client=serverSocket.accept();	
				//System.out.println("连接成功");
				
				//线程处理此次连接
				HandelThread response=new HandelThread(client);			
				new Thread(response).start();	
			}
			
		} catch (IOException e) {
			System.out.println("服务器异常:"+e.getMessage());
		}	
	}
	
}
