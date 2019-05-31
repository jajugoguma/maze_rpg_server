package socket;

import java.io.*;
import java.net.*;

public class ServerSide {
	public static void main(String args[]) {
		Socket s = null;

		try {
			ServerSocket server = new ServerSocket(8090);
			System.out.println("Java Server Starting... ");
			
			while (true) {
				s = server.accept();
				
				String clientIP = s.getInetAddress().toString();
				int clientPort = s.getPort();
				System.out.println("[" + clientIP + " : " + clientPort + "] : Connected\n");
				
				Connected con = new Connected(s);
				con.start();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}

class Connected extends Thread{
	Socket s;
	public Connected(Socket sock) {
		this.s = sock;
	}
	
	public void run() {
		DataInputStream dis = null;
		DataOutputStream dos = null;

		DBSide db = new DBSide();
		db.conToDB();

		try {
			while(true) {
				dis = new DataInputStream(s.getInputStream());
				dos = new DataOutputStream(s.getOutputStream());

				while(true) {
					
					String msg = dis.readLine();
					
					if (!msg.contains(",,"))
						continue;
					
					String[] token = msg.split(",,");
					
					System.out.print("Receive : ");
					for (int i = 1; i < token.length; i++) {
						System.out.print(token[i] + ",");
					}
					System.out.println();
					
					if (token[1].compareTo("SELCHAR") == 0) {
						db.getCharacters(token[2]);
						byte[] ret = null;

						String tmp = "";

						for (String get : db.datalist)
							tmp += get + ",";

						tmp += '\0';
						ret = (tmp).getBytes();
						dos.write(ret);
						dos.flush();
					}
					else if (token[1].compareTo("LOADITEM") == 0) {
						db.getItemOwned(token[2]);
						byte[] ret = null;

						String tmp = "";

						for (String get : db.datalist)
							tmp += get + ",";

						tmp += '\0';
						ret = (tmp).getBytes();
						dos.write(ret);
						dos.flush();
					}
					else if (token[1].compareTo("ITEM") == 0) {
						db.getItemInfos();
						byte[] ret = null;

						String tmp = "";

						for (String get : db.datalist)
							tmp += get + ",";

						tmp += '\0';
						ret = (tmp).getBytes();
						dos.write(ret);
						dos.flush();
					}
					else if (token[1].compareTo("NEWACC") == 0) {	
						byte[] ret = null;
						
						String result = db.newAccount(token[2], token[3]);
						result += '\0';
						ret = (result).getBytes();
						dos.write(ret);
						dos.flush();
					}
					else if (token[1].compareTo("SAVE") == 0) {	
						byte[] ret = null;
						
						String result = db.saveCharacter(token);
						result += '\0';
						ret = (result).getBytes();
						dos.write(ret);
						dos.flush();
					}
					else if (token[1].compareTo("CRTCHAR") == 0) {	
						byte[] ret = null;
						
						String result = db.newCharacter(token[2], token[3]);
						result += '\0';
						ret = (result).getBytes();
						dos.write(ret);
						dos.flush();
					}
					else if (token[1].compareTo("DELCHAR") == 0) {	
						byte[] ret = null;
						
						String result = db.delCharacter(token[2]);
						result += '\0';
						ret = (result).getBytes();
						dos.write(ret);
						dos.flush();
					}
					else if (token[1].compareTo("LOGIN") == 0) {
						byte[] ret = null;
						
						String result = db.logIn(token[2], token[3]);
						result += '\0';
						ret = (result).getBytes();
						dos.write(ret);
						dos.flush();
					}
					else if (token[1].compareTo("ENDCON") == 0) {
						break;
					}
					db.clearLists();
				}
				String clientIP = s.getInetAddress().toString();
				int clientPort = s.getPort();
				System.out.println("[" + clientIP + " : " + clientPort + "] : Disconnected\n");
				
				s.close(); // 소켓을 닫는다
				db.disconToDB();
				return;
			}
		} catch(Exception e) {	
			System.out.println("Exception: " + e);
			db.disconToDB();
			return;
		}
	}
} 