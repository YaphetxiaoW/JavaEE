import java.io.*;
import java.net.*;

public class Server {
	
	public static final int PORT=3333;

	public static void main(String[] args) {
				
		try {
			ServerSocket serverSocket=new ServerSocket(PORT);
			System.out.println("������������....");
			//�����Ƿ�����������
			while(true){
				//һ���ж�������ʾ����
				Socket client=serverSocket.accept();	
				//System.out.println("���ӳɹ�");
				
				//�̴߳���˴�����
				HandelThread response=new HandelThread(client);			
				new Thread(response).start();	
			}
			
		} catch (IOException e) {
			System.out.println("�������쳣:"+e.getMessage());
		}	
	}
	
}
