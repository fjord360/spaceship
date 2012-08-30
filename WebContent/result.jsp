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
				if( infofield.get(i).equals("온도") ) kelvin = bm.ParseValueAverage(dbval);
				if( infofield.get(i).equals("타입") ) type = dbval;
				if( infofield.get(i).equals("형태") ) spec = dbval;
				answer_info += ("@" + infofield.get(i) + " : " + dbval);
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
		
		// 청킹한 이름, 숫자, 특수문자를 리스트에 넣는다.
		ArrayList<Keyword> keyword = nf.getKeyword();
		ArrayList<Keyword> numberList = nf.getNumberList();
		ArrayList<Keyword> special = nf.getSpecial();
		
		// 형태소분석
		TextAnalyzer ta = new TextAnalyzer();
		ta.SetChunk(keyword, numberList, special);
		analyzedOrder = ta.Anaylze(chunkedOrder);
		System.out.println(analyzedOrder);
		
		// DB 연결..!
		if( !analyzedOrder.equals("NoResult") ) {
			SpaceDB db = new SpaceDB();
			db.CreateDB("jdbc:mysql://localhost/stardb?characterEncoding=UTF-8", "root", "");
			query = ta.getQuery();
			System.out.println(query);
		
			// DB 쿼리
			if( !query.equals("NoResult") ) {
				db.Query(query);
				gf = db.getField(query);
				while( db.getDB().next() ) {
					for( int i = 0 ; i < gf.size() ; i++ ) {
						String dbValue = db.getDB().getString(gf.get(i));
						if( dbValue == null ) VALUE.add("-12345");
						else 				  VALUE.add(dbValue);
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
	out.println(answer);
	System.out.println(answer);
	
	// 현재 날짜
	Calendar calendar = Calendar.getInstance();
	String YY = Integer.toString(calendar.get(Calendar.YEAR));
	String MM = Integer.toString(calendar.get(Calendar.MONTH));
	String DD = Integer.toString(calendar.get(Calendar.DAY_OF_MONTH));
	String H = Integer.toString(calendar.get(Calendar.HOUR_OF_DAY));
	String M = Integer.toString(calendar.get(Calendar.MINUTE));
	String S = Integer.toString(calendar.get(Calendar.SECOND));
	String nowDate = YY + "/" + MM + "/" + DD + " " + H + ":" + M + ":" + S;
	
	// 로그 저장
	FileOutputStream fos = new FileOutputStream("log/log" + YY + MM + DD + ".txt", true);
	OutputStreamWriter osw = new OutputStreamWriter(fos, "UTF-8");
	osw.write(nowDate + "\n");
	osw.write("IP : " + USER_IP + "\n");
	osw.write("USER_QUERY : " + order + "\n");
	osw.write("SENTENCE : " + analyzedOrder + "\n");
	osw.write("QUERY : " + query + "\n");
	osw.write("ANSWER : " + answer + "\n");
	osw.write("\n");
	osw.close();
%>
 