
import java.io.*;
import java.net.*;


public class Client {

	public static final String IP_ADDR="127.0.0.1";
	public static final int PORT=3333;
	
	public static void main(String[] args) {

		 while(true){
			
			Socket socket=null;
			
			try {
				
				System.out.print("client input(type 'x' to exit)：");
				//从键盘上输入字符串
				String inputStr=new BufferedReader(new InputStreamReader(System.in)).readLine();				
				
				//断开与服务器的连接
				if(inputStr.equals("x")){
					System.out.println("Close the connection!");
					Thread.sleep(500);
					break;				
				}
				
				//创建一个流套接字并连接到主机的端口上
				socket=new Socket(IP_ADDR,PORT);
	
				//读取服务端数据
				DataInputStream input=new DataInputStream(socket.getInputStream());			
				//向服务端发送数据
				DataOutputStream out=new DataOutputStream(socket.getOutputStream());				
				//服务端发送数据
				out.writeUTF(inputStr);
				
				//读取服务器返回的数据
				String responseStr=input.readUTF();
				System.out.println("Server response："+responseStr);
			
				//给服务端处理时间
				Thread.sleep(500);
				
				out.close();
				input.close();			
			}catch (Exception e) {			
				System.out.println("客户端异常："+e.getMessage());
				break;
			}finally{
				if(socket!=null){
					try {
						socket.close();
					} catch (IOException e) {
						socket=null;						
						System.out.println("客户端关闭异常:"+e.getMessage());
					}
				}//if
			}
		
		}//while
	}

}
