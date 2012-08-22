package dbpack;

import java.sql.*;
import java.util.ArrayList;

public class SpaceDB {
	
	Connection conn;
	Statement stmt;
	ResultSet rs;
	
	ArrayList<String> star_field;
	ArrayList<String> cons_field;
	
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
		
		star_field = new ArrayList<String>();
		star_field.add("이름");
		star_field.add("타입");
		star_field.add("별자리");
		star_field.add("적경_시");
		star_field.add("적경_분");
		star_field.add("적경_초");
		star_field.add("적위_도");
		star_field.add("적위_분");
		star_field.add("적위_초");
		star_field.add("실시등급");
		star_field.add("최소실시등급");
		star_field.add("최대실시등급");
		star_field.add("절대등급");
		star_field.add("최소절대등급");
		star_field.add("최대절대등급");
		star_field.add("형태");
		star_field.add("연주시차");
		star_field.add("최소연주시차");
		star_field.add("최대연주시차");
		star_field.add("거리");
		star_field.add("최소거리");
		star_field.add("최대거리");
		star_field.add("질량");
		star_field.add("최소질량");
		star_field.add("최대질량");
		star_field.add("크기");
		star_field.add("최소크기");
		star_field.add("최대크기");
		star_field.add("밝기");
		star_field.add("최소밝기");
		star_field.add("최대밝기");
		star_field.add("온도");
		star_field.add("최소온도");
		star_field.add("최대온도");
		star_field.add("행성수");
		star_field.add("동반성수");
		
		cons_field = new ArrayList<String>();
		cons_field.add("이름");
		cons_field.add("라틴어");
		cons_field.add("약자");
		cons_field.add("소유격");
		cons_field.add("상징");
		cons_field.add("적경_시");
		cons_field.add("적경_분");
		cons_field.add("적위_도");
		cons_field.add("적위_분");
		cons_field.add("남중_월");
		cons_field.add("남중_일");
		cons_field.add("북쪽위도");
		cons_field.add("남쪽위도");
		cons_field.add("넓이");
		cons_field.add("넓이순위");
		cons_field.add("별의수");
		cons_field.add("밝은별");
		cons_field.add("별밝기");
		cons_field.add("유성군");
		cons_field.add("이웃별자리");
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
			System.out.println("query message : " + e.getMessage());
			e.printStackTrace();
		}
	}
	
	// 쿼리의 필드이름을 리턴
	public ArrayList<String> getField(String query) {
		query = query.toUpperCase();
		ArrayList<String> gf = new ArrayList<String>();
		String table = "";
		
		// where 앞부분만 본다.
		String[] where = query.split("WHERE");
		if( where.length > 0 ) {
			
			// from 앞부분만 본다.
			String[] from = where[0].split("FROM");
			table = from[1];
			table = table.replaceAll("\\s", "");
			if( from.length > 0 ) {
				
				// SELECT 뒷부분만 본다.
				String[] select = from[0].split("SELECT");
				if( select.length > 0 ) {
					
					// 띄어쓰기 제거
					String ns = select[1].replaceAll("\\s", "");
					
					// *인 경우 모든 필드를 리턴
					if( ns.equals("*") ) {
						gf = getAllField(table);
					}
					// *이 아닌 경우 ,를 중심으로 필드를 나눠 리스트에 넣는다.
					else {
						String[] field = ns.split(",");
						for( int i = 0 ; i < field.length ; i++ ) {
							gf.add(field[i]);
						}
					}
				}
			}
		}
		return gf;
	}
	
	// 모든 필드 내용을 리턴
	public ArrayList<String> getAllField(String table) {
		//System.out.println("table : " + table);
		if( table.equals("별자리") ) return cons_field;
		else						return star_field;
	}
	
	// ResultSet을 리턴
	public ResultSet getDB() {
		return rs;
	}
}
