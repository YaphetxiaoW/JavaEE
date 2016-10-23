
import java.io.*;
import java.net.*;


public class Client {

	public static final String IP_ADDR="127.0.0.1";
	public static final int PORT=3333;
	
	public static void main(String[] args) {

		 while(true){
			
			Socket socket=null;
			
			try {
				
				System.out.print("client input(type 'x' to exit)��");
				//�Ӽ����������ַ���
				String inputStr=new BufferedReader(new InputStreamReader(System.in)).readLine();				
				
				//�Ͽ��������������
				if(inputStr.equals("x")){
					System.out.println("Close the connection!");
					Thread.sleep(500);
					break;				
				}
				
				//����һ�����׽��ֲ����ӵ������Ķ˿���
				socket=new Socket(IP_ADDR,PORT);
	
				//��ȡ���������
				DataInputStream input=new DataInputStream(socket.getInputStream());			
				//�����˷�������
				DataOutputStream out=new DataOutputStream(socket.getOutputStream());				
				//����˷�������
				out.writeUTF(inputStr);
				
				//��ȡ���������ص�����
				String responseStr=input.readUTF();
				System.out.println("Server response��"+responseStr);
			
				//������˴���ʱ��
				Thread.sleep(500);
				
				out.close();
				input.close();			
			}catch (Exception e) {			
				System.out.println("�ͻ����쳣��"+e.getMessage());
				break;
			}finally{
				if(socket!=null){
					try {
						socket.close();
					} catch (IOException e) {
						socket=null;						
						System.out.println("�ͻ��˹ر��쳣:"+e.getMessage());
					}
				}//if
			}
		
		}//while
	}

}
