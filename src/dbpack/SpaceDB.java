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
	
	// MySQL 드라이버 생성
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
			// constellation 테이블 선택
			rs = stmt.executeQuery(query);
			
			// constellation 테이블에서 데이터 가져옴
			//while( rs.next() ) {
				//String name = rs.getString("name");
			//}
		} catch(Exception e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
	}
	
	// 쿼리의 필드이름을 리턴
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
	
	// ResultSet을 리턴
	public ResultSet getDB() {
		return rs;
	}
}
