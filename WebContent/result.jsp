<%@page language="java" contentType="text/html; charset=EUC-KR" pageEncoding="EUC-KR"%>
<%@page import="analyzerpack.NameFinder" %>
<%@page import="analyzerpack.Keyword" %>
<%@page import="analyzerpack.TextAnalyzer" %>
<%@page import="analyzerpack.ResultAnalyzer" %>
<%@page import="analyzerpack.AnswerText" %>
<%@page import="analyzerpack.Pos" %>
<%@page import="dbpack.SpaceDB" %>
<%@page import="java.util.ArrayList" %>
<%@page import="java.io.FileOutputStream" %>
<%@page import="java.io.OutputStreamWriter" %>
<%@page import="java.util.Calendar" %>
<%
	request.setCharacterEncoding("utf-8");
	String order = request.getParameter("order");
%>

<%
	long start = System.currentTimeMillis();
	
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
	String analyzedOrder = ta.Anaylze(chunkedOrder);
	
	// DB 연결..!
	String query = "NoResult";
	ArrayList<String> VALUE = new ArrayList<String>();
	ArrayList<String> gf = new ArrayList<String>();
	if( !analyzedOrder.equals("NoResult") ) {
		SpaceDB db = new SpaceDB();
		db.CreateDB("jdbc:mysql://localhost/stardb?characterEncoding=UTF-8", "root", "");
		query = ta.getQuery();
		//System.out.println(query);
	
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
	AnswerText at = new AnswerText();
	at.CreatAnswerPattern();
	String answer = at.AnswerFromQuery(query, VALUE, gf.size());
	out.println(answer);
	
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
	osw.write("USER_QUERY : " + order + "\n");
	osw.write("SENTENCE : " + analyzedOrder + "\n");
	osw.write("QUERY : " + query + "\n");
	osw.write("ANSWER : " + answer + "\n");
	osw.write("\n");
	osw.close();
%>
 