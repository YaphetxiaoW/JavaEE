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
			
			//��ȡ�ͻ�������
			DataInputStream input=new DataInputStream(socket.getInputStream());	
			//�õ��ͻ���������ַ���
			String clientStr=input.readUTF();		
			//�ַ�������
            String responseStr=new StringBuffer(clientStr).reverse().toString();
			
			//��ͻ��˷�����Ϣ
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
					System.out.println("�����finally�쳣"+e.getMessage());
				}
			}
		}
	}		
}
