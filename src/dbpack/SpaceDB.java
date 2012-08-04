package dbpack;

import java.sql.*;
import java.util.StringTokenizer;;

public class SpaceDB {
	
	Connection conn;
	Statement stmt;
	ResultSet rs;
	
	public SpaceDB() {
		conn = null;
		stmt = null;
		rs = null;
	}
	
	// MySQL ����̹� ����
	public void CreateDB(String URL, String USER, String PASS) {
		try{
			Class.forName("org.gjt.mm.mysql.Driver");
			conn = DriverManager.getConnection(URL, USER, PASS);
			stmt = conn.createStatement();
		} catch(Exception e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
	}
	
	public void Query(String query) {
		try {
			// constellation ���̺� ����
			rs = stmt.executeQuery(query);
			
			// constellation ���̺��� ������ ������
			//while( rs.next() ) {
				//String name = rs.getString("name");
			//}
		} catch(Exception e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
	}
	
	// ������ �ʵ��̸��� ����
	public String getField(String query) {
		query = query.toUpperCase();
		String gf = "NoResult";
		String[] where = query.split("WHERE");
		if( where.length > 0 ) {
			String[] from = where[0].split("FROM");
			if( from.length > 0 ) {
				String[] select = from[0].split("SELECT");
				if( select.length > 0 ) {
					gf = select[1];
					gf = gf.replaceAll("\\s", "");
				}
			}
		}
		return gf;
	}
	
	// ResultSet�� ����
	public ResultSet getDB() {
		return rs;
	}
}
