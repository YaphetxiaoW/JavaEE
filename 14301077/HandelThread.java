import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;


public class HandelThread implements Runnable{

	private Socket socket;

	public HandelThread(Socket client){
		socket=client;
	}
	
	public void run() {		
		try {
			
			//读取客户端数据
			DataInputStream input=new DataInputStream(socket.getInputStream());	
			//得到客户端输入的字符串
			String clientStr=input.readUTF();		
			//字符串倒序
            String responseStr=new StringBuffer(clientStr).reverse().toString();
			
			//向客户端发送信息
			DataOutputStream out=new DataOutputStream(socket.getOutputStream());
			out.writeUTF(responseStr);
	
			out.close();
			input.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			if(socket!=null){
				try {
					socket.close();
				} catch (IOException e) {
					socket=null;
					System.out.println("服务端finally异常"+e.getMessage());
				}
			}
		}
	}		
}
