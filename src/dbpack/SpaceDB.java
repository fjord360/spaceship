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
		
		star_field = new ArrayList<String>();
		star_field.add("�̸�");
		star_field.add("Ÿ��");
		star_field.add("���ڸ�");
		star_field.add("����_��");
		star_field.add("����_��");
		star_field.add("����_��");
		star_field.add("����_��");
		star_field.add("����_��");
		star_field.add("����_��");
		star_field.add("�ǽõ��");
		star_field.add("�ּҽǽõ��");
		star_field.add("�ִ�ǽõ��");
		star_field.add("������");
		star_field.add("�ּ�������");
		star_field.add("�ִ�������");
		star_field.add("����");
		star_field.add("���ֽ���");
		star_field.add("�ּҿ��ֽ���");
		star_field.add("�ִ뿬�ֽ���");
		star_field.add("�Ÿ�");
		star_field.add("�ּҰŸ�");
		star_field.add("�ִ�Ÿ�");
		star_field.add("����");
		star_field.add("�ּ�����");
		star_field.add("�ִ�����");
		star_field.add("ũ��");
		star_field.add("�ּ�ũ��");
		star_field.add("�ִ�ũ��");
		star_field.add("���");
		star_field.add("�ּҹ��");
		star_field.add("�ִ���");
		star_field.add("�µ�");
		star_field.add("�ּҿµ�");
		star_field.add("�ִ�µ�");
		star_field.add("�༺��");
		star_field.add("���ݼ���");
		
		cons_field = new ArrayList<String>();
		cons_field.add("�̸�");
		cons_field.add("��ƾ��");
		cons_field.add("����");
		cons_field.add("������");
		cons_field.add("��¡");
		cons_field.add("����_��");
		cons_field.add("����_��");
		cons_field.add("����_��");
		cons_field.add("����_��");
		cons_field.add("����_��");
		cons_field.add("����_��");
		cons_field.add("��������");
		cons_field.add("��������");
		cons_field.add("����");
		cons_field.add("���̼���");
		cons_field.add("���Ǽ�");
		cons_field.add("������");
		cons_field.add("�����");
		cons_field.add("������");
		cons_field.add("�̿����ڸ�");
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
			System.out.println("query message : " + e.getMessage());
			e.printStackTrace();
		}
	}
	
	// ������ �ʵ��̸��� ����
	public ArrayList<String> getField(String query) {
		query = query.toUpperCase();
		ArrayList<String> gf = new ArrayList<String>();
		String table = "";
		
		// where �պκи� ����.
		String[] where = query.split("WHERE");
		if( where.length > 0 ) {
			
			// from �պκи� ����.
			String[] from = where[0].split("FROM");
			table = from[1];
			table = table.replaceAll("\\s", "");
			if( from.length > 0 ) {
				
				// SELECT �޺κи� ����.
				String[] select = from[0].split("SELECT");
				if( select.length > 0 ) {
					
					// ���� ����
					String ns = select[1].replaceAll("\\s", "");
					
					// *�� ��� ��� �ʵ带 ����
					if( ns.equals("*") ) {
						gf = getAllField(table);
					}
					// *�� �ƴ� ��� ,�� �߽����� �ʵ带 ���� ����Ʈ�� �ִ´�.
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
	
	// ��� �ʵ� ������ ����
	public ArrayList<String> getAllField(String table) {
		//System.out.println("table : " + table);
		if( table.equals("���ڸ�") ) return cons_field;
		else						return star_field;
	}
	
	// ResultSet�� ����
	public ResultSet getDB() {
		return rs;
	}
}
