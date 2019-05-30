package socket;

import java.io.*;
import java.net.*;

public class ServerSide {

	public int byte4ToInt(byte[] retBuf, int set){
		int toInt = 0;

		toInt = ((retBuf[set+3]&0xFF) << 24) + ((retBuf[set+2]&0xFF) << 16) + ((retBuf[set+1]&0xFF) << 8) + (retBuf[set]&0xFF);
		return toInt;
	}

	/*
	 * Swapping byte orders of given numeric types
	 */

	static short swap(short x) {
		return (short)((x << 8) | ((x >> 8) & 0xff));
	}

	static char swap(char x) {
		return (char)((x << 8) | ((x >> 8) & 0xff));
	}

	static int swap(int x) {
		return (int)((swap((short)x) << 16) | (swap((short)(x >> 16)) & 0xffff));
	}

	static long swap(long x) {
		return (long)(((long)swap((int)(x)) << 32) | ((long)swap((int)(x >> 32)) & 0xffffffffL));
	}

	static float swap(float x) {
		return Float.intBitsToFloat(swap(Float.floatToRawIntBits(x)));
	}

	static double swap(double x) {
		return Double.longBitsToDouble(swap(Double.doubleToRawLongBits(x)));
	}


	public static void main(String args[]) {
		DataInputStream dis = null;
		DataOutputStream dos = null;

		DBSide db = new DBSide();
		db.conToDB();

		try {

			Socket s = null;

			int port = Integer.parseInt("2018");

			ServerSocket ss = new ServerSocket(port);

			System.out.println("Java Server Starting... ");

			while(true) { // 데몬이 되기 위한 무한 루프 
				s = ss.accept();

				String clientIP = s.getInetAddress().toString();
				int clientPort = s.getPort();
				System.out.println("Client from " + clientIP + " : " + clientPort + "\n");


				dis = new DataInputStream(s.getInputStream());
				dos = new DataOutputStream(s.getOutputStream());

				/*
					System.out.println("1 >> "+ swap(dis.readInt()));
					System.out.println("2 >> "+ (char)dis.readByte());
					System.out.println("3 >> "+ (char)dis.readByte());
					System.out.println("4 >> "+ swap(dis.readDouble()));
					System.out.println("5 >> "+ swap(dis.readDouble()));
				 */
				while(true) {
					
					String msg = dis.readLine();
					
					//System.out.println("1 >> " + msg);
					if (!msg.contains(","))
						continue;
					
					String[] token = msg.split(",");
					
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
				s.close(); // 소켓을 닫는다
			}
		}catch(Exception e) {	
			System.out.println("Exception: " + e);
		}
		db.disconToDB();
	}
} 