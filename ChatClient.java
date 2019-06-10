
import java.net.*;
import java.util.*;
import java.io.*;
import java.text.*;

public class ChatClient {

	private String ip;
	private String name;

	public ChatClient(String ip, String name) {
		this.ip=ip;
		this.name=name;
	}

	public static void main(String[] args) {
		String name, ip;
		Scanner s=new Scanner(System.in);
		System.out.println("java ChatClient");
		System.out.println("your name >> ");
		name=s.nextLine();
		while(name.length()<4) {
			System.out.println("your name is too short");
			System.out.println("Rewrite your name >> ");
			name=s.nextLine();
		}
		System.out.println("server ip >> ");
		ip=s.nextLine();

		ChatClient chatClient = new ChatClient(ip,name);
		chatClient.start();
		s.close();
	} // main

	private void start() {
		Socket sock = null;
		BufferedReader br = null;
		PrintWriter pw = null;
		boolean endflag = false;
		try{
			sock = new Socket(ip, 10001);
			pw = new PrintWriter(new OutputStreamWriter(sock.getOutputStream())); //server로 메시지 보내기 위
			br = new BufferedReader(new InputStreamReader(sock.getInputStream())); //server로부터 메시지 받기 위
			BufferedReader keyboard = new BufferedReader(new InputStreamReader(System.in));
			// send user name.
			pw.println(name); //id를 server로 전송
			pw.flush(); // pw 데이타 제
			InputThread it = new InputThread(sock, br); // class InputThread 호출
			it.start(); // Thread 실행
			String line = null;
			while((line = keyboard.readLine()) != null){
				pw.println(line); // InputThread를 통해 입력받은 line을 server로 전송
				pw.flush();
				if(line.equals("/quit")){
					endflag = true;
					break;
				}
			}
			System.out.println("Connection closed.");
		}catch(Exception ex){
			if(!endflag)
				System.out.println(ex);
		}finally{
			try{
				if(pw != null)
					pw.close();
			}catch(Exception ex){}
			try{
				if(br != null)
					br.close();
			}catch(Exception ex){}
			try{
				if(sock != null)
					sock.close();
			}catch(Exception ex){}
		} // finally

	}
} // class



class InputThread extends Thread{
	private Socket sock = null;
	private BufferedReader br = null;
	public InputThread(Socket sock, BufferedReader br){
		this.sock = sock;
		this.br = br;
	}
	public void run(){
		try{
			String line = null;
			while((line = br.readLine()) != null){
				System.out.println(line);
			} // server로부터 오는 메시지를 받아서 출력
		}catch(Exception ex){
		}finally{
			try{
				if(br != null)
					br.close();
			}catch(Exception ex){}
			try{
				if(sock != null)
					sock.close();
			}catch(Exception ex){}
		}
	} // InputThread
}
