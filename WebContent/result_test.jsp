<%@page language="java" contentType="text/html; charset=EUC-KR" pageEncoding="EUC-KR"%>
<%@page import="analyzerpack.NameFinder" %>
<%@page import="analyzerpack.Keyword" %>
<%@page import="analyzerpack.TextAnalyzer" %>
<%@page import="analyzerpack.ResultAnalyzer" %>
<%@page import="analyzerpack.AnswerText" %>
<%@page import="analyzerpack.Pos" %>
<%@page import="dbpack.SpaceDB" %>
<%@page import="spacepack.Buildmap" %>
<%@page import="java.io.FileOutputStream" %>
<%@page import="java.io.OutputStreamWriter" %>
<%@page import="java.util.ArrayList" %>
<%@page import="java.util.Enumeration" %>
<%@page import="java.util.Calendar" %>
<%
	request.setCharacterEncoding("utf-8");
	String order = request.getParameter("order");
	
	// IP üũ
	String USER_IP = request.getRemoteAddr();
	String answer = "";
	String query = "NoResult";
	String analyzedOrder = order;
	ArrayList<String> VALUE = new ArrayList<String>();
	ArrayList<String> gf = new ArrayList<String>();
	//long start = System.currentTimeMillis();
	
	if( order.indexOf("@INFORMATION=") >= 0 ) {
		
		String infosplit[] = order.split("=");
		String infotable = infosplit[1];
		String infoname = infosplit[2];
		
		SpaceDB db = new SpaceDB();
		db.CreateDB("jdbc:mysql://localhost/stardb?characterEncoding=UTF-8", "root", "");
		
		// �̸� ����
		VALUE.add(infoname);
		String frompos = "";
		if( infotable.equals("C") ) frompos = "���ڸ�";
		if( infotable.equals("S") ) frompos = "��";
		
		// �������� �������� DB���� �����ͼ� �ٽ� ����
		// ���� ���� �������� ȥ���� �߱��ϰ�, �������� ���� ������ �������� �����Ѵ�. -�絹�� �Ƹ�����
		ArrayList<String> infofield = new ArrayList<String>();
		infofield.add("Ÿ��");
		infofield.add("����");
		infofield.add("����_��");
		infofield.add("����_��");
		infofield.add("����_��");
		infofield.add("����_��");
		infofield.add("����_��");
		infofield.add("����_��");
		infofield.add("�Ÿ�");
		infofield.add("������");
		infofield.add("ũ��");
		infofield.add("����");
		infofield.add("�µ�");
		query = "SELECT ";
		for( int i = 0 ; i < infofield.size() ; i++ ) {
			query += infofield.get(i);
			if( i != infofield.size()-1 ) query += ",";
		}
		query += " FROM " + frompos + " WHERE �̸�='" + infoname + "'";
		db.Query(query);
		gf = db.getField(query);
		answer = infoname + frompos + "�� �̵��մϴ�.@" + infoname;
		String answer_info = "";
		Buildmap bm = new Buildmap();
		String type = "";
		String spec = "";
		double mass = 0.0f;
		double kelvin = 0.0f;
		
		while( db.getDB().next() ) {
			for( int i = 0 ; i < gf.size() ; i++ ) {
				String dbval = db.getDB().getString(gf.get(i));
				if( infofield.get(i).equals("����") ) mass = bm.ParseValueAverage(dbval);
				else if( infofield.get(i).equals("�µ�") ) kelvin = bm.ParseValueAverage(dbval);
				else if( infofield.get(i).equals("Ÿ��") ) type = dbval;
				else {
					if( infofield.get(i).equals("����") ) spec = dbval;
					answer_info += ("@" + infofield.get(i) + "=" + dbval);
				}
			}
		}
		if( type.equals("star") ) answer += ("@������=" + bm.BuildStar(spec));
		else answer += ("@������=" + bm.BuildPlanet(mass, kelvin));
		
		answer += answer_info;
	}	
	else {
		// �̸�/���� ûŷ
		NameFinder nf = new NameFinder();
		nf.CreateMap();
		String chunkedOrder = nf.Find(order);
		System.out.println("ûŷ�� �̸� : " + chunkedOrder);
		out.println("<p>ûŷ�� �̸� : " + chunkedOrder + "</p>");
		
		// ûŷ�� �̸�, ����, Ư�����ڸ� ����Ʈ�� �ִ´�.
		ArrayList<Keyword> keyword = nf.getKeyword();
		ArrayList<Keyword> numberList = nf.getNumberList();
		ArrayList<Keyword> special = nf.getSpecial();
		
		if( keyword.size() > 0 ) {
			System.out.println(keyword.get(0).text);
		}
		
		// ���¼Һм�
		TextAnalyzer ta = new TextAnalyzer();
		ta.SetChunk(keyword, numberList, special);
		analyzedOrder = ta.Anaylze(chunkedOrder);
		System.out.println("���¼Һм��� �̸� : " + analyzedOrder);
		out.println("<p>���¼Һм��� �̸� : " + analyzedOrder + "</p>");
		
		// DB ����..!
		if( !analyzedOrder.equals("NoResult") ) {
			SpaceDB db = new SpaceDB();
			db.CreateDB("jdbc:mysql://localhost/stardb?characterEncoding=UTF-8", "root", "");
			query = ta.getQuery();
			System.out.println("���� : " + query);
			out.println("<p>���� : " + query + "</p>");
		
			// DB ����
			if( !query.equals("NoResult") ) {
				db.Query(query);
				gf = db.getField(query);
				while( db.getDB().next() ) {
					for( int i = 0 ; i < gf.size() ; i++ ) {
						//System.out.println(db.getDB().getString(gf.get(i)));
						VALUE.add(db.getDB().getString(gf.get(i)));
					}
				}
			}
		}
		
		// ���� �޾ƿ�
		if( answer.length() <= 0 ) {
			AnswerText at = new AnswerText();
			at.CreatAnswerPattern();
			answer = at.AnswerFromQuery(order, query, VALUE);
		}
	}
	// ���� ���
	out.println("<p>���� : " + answer + "</p>");
	System.out.println("���� : " + answer);
%>
 