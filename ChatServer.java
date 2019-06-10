
import java.net.*;
import java.text.SimpleDateFormat;
import java.io.*;
import java.util.*;


public class ChatServer {

	public static void main(String[] args) {
		try{
			ServerSocket server = new ServerSocket(10001);
			System.out.println("Waiting connection...");
			HashMap hm = new HashMap();
			while(true){
				Socket sock = server.accept();
				ChatThread chatthread = new ChatThread(sock, hm);
				chatthread.start();
			} // while
		}catch(Exception e){
			System.out.println(e);
		}
	} // main
}

class ChatThread extends Thread{
	private Socket sock;
	private String id;
	private BufferedReader br; //client의 메시지를 받기 위한 BR
	private HashMap hm;
	private boolean initFlag = false;

	private BufferedReader br2; //File의 문장을 받기위한 BR
	private File file;
	private ArrayList<String> spam=new ArrayList<>();
	private FileReader fr;

	public ChatThread(Socket sock, HashMap hm){
		this.sock = sock;
		this.hm = hm;
		try{

			PrintWriter pw = new PrintWriter(new OutputStreamWriter(sock.getOutputStream())); // client로 메세지를 보내기 위함.
			br = new BufferedReader(new InputStreamReader(sock.getInputStream())); // client에서 전송된 메시지를 받기 위함.
			id = br.readLine(); // client에서 pw를 통해 보낸 id를 받음.
			broadcast(id + " entered."); // 모든 client에게 메시지를 보냄.
			System.out.println("[Server] User (" + id + ") entered."); //server에서만 print 됨.
			synchronized(hm){ //hm에 있는 id value들에 모든 client가 접근 가능하도록 묶어줌
				hm.put(this.id, pw);
			} //
			initFlag = true;
		}catch(Exception ex){
			System.out.println(ex);
		}
	} // construcor
	public void run(){
		try{
			String line = null;
			try {
				fr = new FileReader("spamlist.txt");
				br2 = new BufferedReader(fr);
				int i=0;
				String get;
				while((get=br2.readLine())!=null) {
					spam.add((String)get); //txt 파일에 있는 단어 리스트를 ArrayList로 이동시킴
				}
				br2.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			while((line = br.readLine()) != null){ //client에서 보낸 메시지가 없을 때까지..
				if(line.equals("/quit")) //Client 채팅 끝내기
					break;
				if(line.equals("/userlist")) { // 현재 접속중인 user들을 보여줌
					send_userlist();
					continue;
				}

				if(line.equals("/spamlist")) { //ArrayList에 들어있는 spam들을 보여줌
					spamlist();
				}
//	만약 클라언트에게서 온 메시지에 spam이 있다면 전달하지 않고 사용자에게 경고함.
				for(int i=0;i<spam.size();i++) {
					if(line.contains(spam.get(i))) {
						warn();
						continue;
					}
				}

//				/addspam 단어 를 입력하면 그 단어를 ArrayList에 넣음.
				if(line.indexOf("/addspam ")==0) {
					addspam(line);
				}

				if(line.indexOf("/to ") == 0){
					sendmsg(line);
				}else
					broadcast(id + " : " + line); //모든 client에게 메시지 전송
			}
		}catch(Exception ex){
			System.out.println(ex);
		}finally{
			synchronized(hm){
				hm.remove(id); // 접속 종료한 client의 id 삭제
			}
			broadcast(id + " exited.");
			try{
				if(sock != null)
					sock.close();
			}catch(Exception ex){}
		}
	} // run

	private void addspam(String line) {
		/* 단어 입력하면 arraylist에 있는지 확인 후 있으면 추가하지 않고 return
		 * 없으면 arraylist에 먼저 추가하고, file에 append.
		 */

		int start = line.indexOf(" ") +1; //단어의 첫 글자의 인덱스
		int end =line.length(); //
		String word = line.substring(start, end);
		if(!spam.contains(word))
			spam.add(word);
		else {
			synchronized(hm){
				Collection collection = hm.values(); //hm에 있는 value들을 collection에 대입
				Iterator iter = collection.iterator(); // collection에 있는 value들을 반복적으로 불러내기 위함
				while(iter.hasNext()){ // iter에 반복적으로 있는 value들이 다 읽힐 때까지
					PrintWriter idvalue= (PrintWriter)hm.get(id);
					PrintWriter pw = (PrintWriter)iter.next(); // 사용자들의 id를 pw에 대입
					if(idvalue==pw) {
						pw.println("The word is already in the list");
						pw.flush();
						return;
					}
				}
			}
		}
		int i=0;
		try {
			PrintWriter outputStream = new PrintWriter(new FileOutputStream("spamlist.txt"));
			while(i<spam.size()) {
				String totxt = spam.get(i);
				outputStream.println(totxt);
				i++;
			}
			outputStream.close();
		}
		catch(FileNotFoundException e) {
			System.out.println(e);
		}



	}

	private void spamlist() {
		synchronized(hm){
			Collection collection = hm.values(); //hm에 있는 value들을 collection에 대입
			Iterator iter = collection.iterator(); // collection에 있는 value들을 반복적으로 불러내기 위함
			while(iter.hasNext()){ // iter에 반복적으로 있는 value들이 다 읽힐 때까지
				PrintWriter idvalue= (PrintWriter)hm.get(id);
				PrintWriter pw = (PrintWriter)iter.next(); // 사용자들의 id를 pw에 대입
				if(idvalue==pw) {
					pw.println("--Spam list--");
					for(int i=0;i<spam.size();i++) {
						pw.println(spam.get(i));
					}
					pw.flush();
				}
			}
		}

	}

	private void warn() {
		synchronized(hm){
			Collection collection = hm.values(); //hm에 있는 value들을 collection에 대입
			Iterator iter = collection.iterator(); // collection에 있는 value들을 반복적으로 불러내기 위함
			Iterator printiter = collection.iterator();
			while(iter.hasNext()){ // iter에 반복적으로 있는 value들이 다 읽힐 때까지
				PrintWriter idvalue= (PrintWriter)hm.get(id);
				PrintWriter pw = (PrintWriter)iter.next(); // 사용자들의 id를 pw에 대입
				if(idvalue==pw) {
					pw.println("Warn : do not use bad words!");
					pw.flush();
				}
			}
		}

	}
	private void send_userlist() {
		synchronized(hm){
			Collection collection = hm.values(); //hm에 있는 value들을 collection에 대입
			Iterator iter = collection.iterator(); // collection에 있는 value들을 반복적으로 불러내기 위함
			while(iter.hasNext()){ // iter에 반복적으로 있는 value들이 다 읽힐 때까지
				PrintWriter idvalue= (PrintWriter)hm.get(id);
				PrintWriter pw = (PrintWriter)iter.next(); // 사용자들의 id를 pw에 대입
				if(idvalue==pw) {
					int i=1;
					for(Object key : hm.keySet()){

			            //Object value =  hm.get(key);

			            pw.print(i+" "+((String) key).substring(0,3));
			            for(int a=0; a<((String)key).length()-3;a++) {
			            	pw.print("*");
			            }
			            pw.println("");
			            i++;
					}
					i--;
					pw.println("사용자 수 : " + i); //pw에 대입된 사용자에게 메시지 전달-> 결과적으로 hm에 있는 모든 사용자에게 메시지 전달
					pw.flush();
				}
			}
		}

	}
	public void sendmsg(String msg){
		int start = msg.indexOf(" ") +1; //받을 사람의 id의 첫 index
		int end = msg.indexOf(" ", start); //받을 사람의 id의 마지막 index + " "
		Date today=new Date();
		SimpleDateFormat f1=new SimpleDateFormat("[a hh:mm]");

		if(end != -1){
			String to = msg.substring(start, end); //id값을 to에 대입
			String msg2 = msg.substring(end+1); // id 뒤에 있는 전달할 메시지를 msg2에 넣음.
			Object obj = hm.get(to); //key값인 id에 해당하는 value를 obj에 넣음.
			if(obj != null){
				PrintWriter pw = (PrintWriter)obj; // pw에 obj값 대입
				pw.println(f1.format(today)+id + " whisphered. : " + msg2); // pw에 있는 id에게 메시지 전달
				pw.flush();
			} // if
		}
	} // sendmsg
	public void broadcast(String msg){
		Date today=new Date();
		SimpleDateFormat f1=new SimpleDateFormat("[a hh:mm]");
		synchronized(hm){
			Collection collection = hm.values(); //hm에 있는 value들을 collection에 대입
			Iterator iter = collection.iterator(); // collection에 있는 value들을 반복적으로 불러내기 위함
			while(iter.hasNext()){ // iter에 반복적으로 있는 value들이 다 읽힐 때까지
				PrintWriter idvalue= (PrintWriter)hm.get(id);
				PrintWriter pw = (PrintWriter)iter.next(); // 사용자들의 id를 pw에 대입
				if(idvalue!=pw)
					pw.println(f1.format(today)+msg); //pw에 대입된 사용자에게 메시지 전달-> 결과적으로 hm에 있는 모든 사용자에게 메시지 전달
				pw.flush();
			}
		}
	} // broadcast
}
