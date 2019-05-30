package socket;

import java.sql.*;
import java.util.LinkedList;

public class DBSide {
	public String name;
	public LinkedList<String> datalist = new LinkedList<String>();

	String DB_URL = "jdbc:oracle:thin:@localhost:1521:XE";		//DB의 URL
	String DB_USER = "project";									//접속하려는 DB의 user id
	String DB_PASSWORD = "database";							//해당 user id의 비밀번호

	//모든 값은 null로 초기화 해준다
	Connection conn = null;
	CallableStatement cstmt = null;
	Statement stmt = null;
	ResultSet rs = null;
	
	String query;


	public void conToDB() {

		try {
			Class.forName("oracle.jdbc.driver.OracleDriver");		//드라이버 로딩
		} catch ( ClassNotFoundException e ) {						//드라이버 로딩시  실패하면 catch한다.
			e.printStackTrace();
		} 

		try {
			conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);	//데이터 베이스에 연결
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void getCharacters(String id) {
		try {
			query = "select c.name, c.lv, c.def, c.atk, c.maxhp, c.curhp, c.exp, c.state_num, c.status_str, c.status_con, c.status_luck, c.ap, c.money "
					+ "from char_owned o, character c "
					+ "where o.c_name = c.name and o.id = '" + id + "'";
			
			System.out.println("Execute : " + query);

			stmt = conn.createStatement();
			rs = stmt.executeQuery(query);				//query 수행.
			
			System.out.println("Send >>");
			while (rs.next()) {							//모든 행을 출력할때 까지 데이터를 받아 출력한다.
				String name = rs.getString("name");
				Integer lv = rs.getInt("lv");
				Double def = rs.getDouble("def");
				Double atk = rs.getDouble("atk");
				Double maxHp = rs.getDouble("maxhp");
				Double curHp = rs.getDouble("curhp");
				Integer exp = rs.getInt("exp");
				Integer stateNum = rs.getInt("state_num");
				Integer statusStr = rs.getInt("status_str");
				Integer statusCon = rs.getInt("status_con");
				Integer statusLuck = rs.getInt("status_luck");
				Integer ap = rs.getInt("ap");
				Integer money = rs.getInt("money");
				
				LinkedList<String> tmpList = new LinkedList<String>();
				String tmpString = "";
				
				tmpList.add(name);
				tmpList.add(lv.toString());
				tmpList.add(def.toString());
				tmpList.add(atk.toString());
				tmpList.add(maxHp.toString());
				tmpList.add(curHp.toString());
				tmpList.add(exp.toString());
				tmpList.add(stateNum.toString());
				tmpList.add(statusStr.toString());
				tmpList.add(statusCon.toString());
				tmpList.add(statusLuck.toString());
				tmpList.add(ap.toString());
				tmpList.add(money.toString());
				
				for (String get : tmpList)
					tmpString += get + ",";
				
				System.out.println(tmpString);
				datalist.add(tmpString);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		System.out.println();
	}
	
	public void getItemOwned(String name) {
		try {
			query = "select * "
					+ "from item_owned "
					+ "where c_name = '" + name + "' "
					+ "order by item_no";
			
			System.out.println("Execute : " + query);

			stmt = conn.createStatement();
			rs = stmt.executeQuery(query);				//query 수행.
			
			System.out.println("Send >>");
			while (rs.next()) {							//모든 행을 출력할때 까지 데이터를 받아 출력한다.
				Integer itemNo = rs.getInt("item_no");
				Integer haveAmount = rs.getInt("hav_amount");
				
				LinkedList<String> tmpList = new LinkedList<String>();
				String tmpString = "";
				
				tmpList.add(itemNo.toString());
				tmpList.add(haveAmount.toString());
				
				for (String get : tmpList)
					tmpString += get + ",";
				
				System.out.println(tmpString);
				datalist.add(tmpString);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		System.out.println();
	}
	
	public void getItemInfos() {
		try {
			query = "select * from item order by item_no";
			
			System.out.println("Execute : " + query);

			stmt = conn.createStatement();
			rs = stmt.executeQuery(query);				//query 수행.
			
			System.out.println("Send >>");
			while (rs.next()) {							//모든 행을 출력할때 까지 데이터를 받아 출력한다.
				Integer itemNo = rs.getInt("item_no");
				String name = rs.getString("name");
				Integer effect = rs.getInt("effect");
				String explain = rs.getString("explain");
				Integer duration = rs.getInt("duration");
				Integer cost = rs.getInt("cost");
				
				LinkedList<String> tmpList = new LinkedList<String>();
				String tmpString = "";
				
				tmpList.add(itemNo.toString());
				tmpList.add(name);
				tmpList.add(effect.toString());
				tmpList.add(explain);
				tmpList.add(duration.toString());
				tmpList.add(cost.toString());
				
				for (String get : tmpList)
					tmpString += get + ",";
				
				System.out.println(tmpString);
				datalist.add(tmpString);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		System.out.println();
	}
	
	public String saveCharacter(String data[]) {
		String result = null;
		try {
			query = "call savacharacter(?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
			System.out.println("Execute : " + query);

			cstmt = conn.prepareCall(query);
			cstmt.setString(1, data[2]);
			cstmt.setInt(2, Integer.parseInt(data[3]));
			cstmt.setDouble(3, Double.parseDouble(data[4]));
			cstmt.setDouble(4, Double.parseDouble(data[5]));
			cstmt.setDouble(5, Double.parseDouble(data[6]));
			cstmt.setDouble(6, Double.parseDouble(data[7]));
			cstmt.setInt(7, Integer.parseInt(data[8]));
			cstmt.setInt(8, Integer.parseInt(data[9]));
			cstmt.setInt(9, Integer.parseInt(data[10]));
			cstmt.setInt(10, Integer.parseInt(data[11]));
			cstmt.setInt(11, Integer.parseInt(data[12]));
			cstmt.setInt(12, Integer.parseInt(data[13]));
			cstmt.setInt(13, Integer.parseInt(data[14]));
			cstmt.registerOutParameter(14, java.sql.Types.VARCHAR);
			
			cstmt.execute();
			
			result = cstmt.getString(14);
			
			query = "delete from item_owned where c_name = '" + data[2] + "'";
			
			System.out.println("Execute : " + query);

			stmt = conn.createStatement();
			rs = stmt.executeQuery(query);	
			
			for (int i = 15; i < data.length - 1; i += 2) {
				query = "call saveItemOwned(?,?,?,?)";
				System.out.println("Execute : " + query);
				
				cstmt = conn.prepareCall(query);
				
				cstmt.setString(1, data[2]);
				cstmt.setInt(2, Integer.parseInt(data[i]));
				cstmt.setInt(3, Integer.parseInt(data[i + 1]));
				cstmt.registerOutParameter(4, java.sql.Types.VARCHAR);
				
				cstmt.execute();
				
				result = cstmt.getString(4);
			}
		} catch (NumberFormatException | SQLException e) {
			e.printStackTrace();
		}
		System.out.print("Send : ");
		System.out.println(result + "\n");
		return result;
	}

	public String newAccount(String id, String passwd) {
		String result = null;
		try {
			query = "call insertaccount(?,?,?)";
			System.out.println("Execute : " + query);

			cstmt = conn.prepareCall(query);
			cstmt.setString(1, id);
			cstmt.setString(2, passwd);
			cstmt.registerOutParameter(3, java.sql.Types.VARCHAR);

			cstmt.execute();

			result = cstmt.getString(3);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		System.out.print("Send : ");
		System.out.println(result + "\n");
		return result;
	}
	
	public String newCharacter(String id, String name) {
		String result = null;
		try {
			query = "call insertcharacter(?,?,?)";
			System.out.println("Execute : " + query);
			
			cstmt = conn.prepareCall(query);
			cstmt.setString(1, id);
			cstmt.setString(2, name);
			cstmt.registerOutParameter(3, java.sql.Types.VARCHAR);
			
			cstmt.execute();
			
			result = cstmt.getString(3);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		System.out.print("Send : ");
		System.out.println(result + "\n");
		return result;
	}
	
	public String delCharacter(String name) {
		String result = null;
		try {
			query = "call deleteCharacter(?,?)";
			System.out.println("Execute : " + query);
			
			cstmt = conn.prepareCall(query);
			cstmt.setString(1, name);
			cstmt.registerOutParameter(2, java.sql.Types.VARCHAR);
			
			cstmt.execute();
			
			result = cstmt.getString(2);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		System.out.print("Send : ");
		System.out.println(result + "\n");
		return result;
	}
	
	public String logIn(String id, String passwd) {
		String result = null;
		try {
			query = "call login(?,?,?)";
			System.out.println("Execute : " + query);
			
			cstmt = conn.prepareCall(query);
			cstmt.setString(1, id);
			cstmt.setString(2, passwd);
			cstmt.registerOutParameter(3, java.sql.Types.VARCHAR);
			
			cstmt.execute();
			
			result = cstmt.getString(3);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		System.out.print("Send : ");
		System.out.println(result + "\n");
		return result;
	}
	
	public void disconToDB() {
		try {
		conn.close();
		//cstmt.close();
		stmt.close();
		rs.close();
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (NullPointerException e) {
		}
	}
	
	public void clearLists() {
		datalist.clear();
	}
}
