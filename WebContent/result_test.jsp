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
	
	// IP 체크
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
		
		// 이름 정보
		VALUE.add(infoname);
		String frompos = "";
		if( infotable.equals("C") ) frompos = "별자리";
		if( infotable.equals("S") ) frompos = "별";
		
		// 전송해줄 정보들을 DB에서 가져와서 다시 전송
		// 질서 없는 복잡함은 혼란을 야기하고, 복잡함이 없는 질서는 지루함을 유발한다. -루돌프 아른하임
		ArrayList<String> infofield = new ArrayList<String>();
		infofield.add("타입");
		infofield.add("형태");
		infofield.add("적경_시");
		infofield.add("적경_분");
		infofield.add("적경_초");
		infofield.add("적위_도");
		infofield.add("적위_분");
		infofield.add("적위_초");
		infofield.add("거리");
		infofield.add("절대등급");
		infofield.add("크기");
		infofield.add("질량");
		infofield.add("온도");
		query = "SELECT ";
		for( int i = 0 ; i < infofield.size() ; i++ ) {
			query += infofield.get(i);
			if( i != infofield.size()-1 ) query += ",";
		}
		query += " FROM " + frompos + " WHERE 이름='" + infoname + "'";
		db.Query(query);
		gf = db.getField(query);
		answer = infoname + frompos + "로 이동합니다.@" + infoname;
		String answer_info = "";
		Buildmap bm = new Buildmap();
		String type = "";
		String spec = "";
		double mass = 0.0f;
		double kelvin = 0.0f;
		
		while( db.getDB().next() ) {
			for( int i = 0 ; i < gf.size() ; i++ ) {
				String dbval = db.getDB().getString(gf.get(i));
				if( infofield.get(i).equals("질량") ) mass = bm.ParseValueAverage(dbval);
				else if( infofield.get(i).equals("온도") ) kelvin = bm.ParseValueAverage(dbval);
				else if( infofield.get(i).equals("타입") ) type = dbval;
				else {
					if( infofield.get(i).equals("형태") ) spec = dbval;
					answer_info += ("@" + infofield.get(i) + "=" + dbval);
				}
			}
		}
		if( type.equals("star") ) answer += ("@별형태=" + bm.BuildStar(spec));
		else answer += ("@별형태=" + bm.BuildPlanet(mass, kelvin));
		
		answer += answer_info;
	}	
	else {
		// 이름/숫자 청킹
		NameFinder nf = new NameFinder();
		nf.CreateMap();
		String chunkedOrder = nf.Find(order);
		System.out.println("청킹된 이름 : " + chunkedOrder);
		out.println("<p>청킹된 이름 : " + chunkedOrder + "</p>");
		
		// 청킹한 이름, 숫자, 특수문자를 리스트에 넣는다.
		ArrayList<Keyword> keyword = nf.getKeyword();
		ArrayList<Keyword> numberList = nf.getNumberList();
		ArrayList<Keyword> special = nf.getSpecial();
		
		if( keyword.size() > 0 ) {
			System.out.println(keyword.get(0).text);
		}
		
		// 형태소분석
		TextAnalyzer ta = new TextAnalyzer();
		ta.SetChunk(keyword, numberList, special);
		analyzedOrder = ta.Anaylze(chunkedOrder);
		System.out.println("형태소분석된 이름 : " + analyzedOrder);
		out.println("<p>형태소분석된 이름 : " + analyzedOrder + "</p>");
		
		// DB 연결..!
		if( !analyzedOrder.equals("NoResult") ) {
			SpaceDB db = new SpaceDB();
			db.CreateDB("jdbc:mysql://localhost/stardb?characterEncoding=UTF-8", "root", "");
			query = ta.getQuery();
			System.out.println("쿼리 : " + query);
			out.println("<p>쿼리 : " + query + "</p>");
		
			// DB 쿼리
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
		
		// 정답 받아옴
		if( answer.length() <= 0 ) {
			AnswerText at = new AnswerText();
			at.CreatAnswerPattern();
			answer = at.AnswerFromQuery(order, query, VALUE);
		}
	}
	// 정답 출력
	out.println("<p>정답 : " + answer + "</p>");
	System.out.println("정답 : " + answer);
%>
 