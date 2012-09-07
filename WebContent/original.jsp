<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="analyzerpack.TextAnalyzer" %>
<%@page import="analyzerpack.NameFinder" %>
<%@page import="analyzerpack.Keyword" %>
<%@page import="analyzerpack.AnswerText" %>
<%@page import="dbpack.SpaceDB" %>
<%@page import="spacepack.Buildmap" %>
<%@page import="java.io.FileOutputStream" %>
<%@page import="java.io.OutputStreamWriter" %>
<%@page import="java.util.ArrayList" %>
<%@page import="java.util.Enumeration" %>
<%@page import="java.util.Calendar" %>
<%@page import="java.sql.*" %>
<%
	//request.setCharacterEncoding("utf-8");
	String order = new String(request.getParameter("order").getBytes("ISO-8859-1"), "UTF-8");
	//String order = request.getParameter("order");
	
	// IP 체크
	String USER_IP = request.getRemoteAddr();
	String answer = "";
	String query = "NoResult";
	String analyzedOrder = order;
	ArrayList<String> VALUE = new ArrayList<String>();
	ArrayList<String> gf = new ArrayList<String>();
	//long start = System.currentTimeMillis();
	boolean isStart = false;

	// 이름/숫자 청킹
	NameFinder nf = new NameFinder();
	nf.CreateMap();
	String chunkedOrder = nf.Find(order);
	out.println(chunkedOrder);
	
	// 청킹한 이름, 숫자, 특수문자를 리스트에 넣는다.
	ArrayList<Keyword> keyword = nf.getKeyword();
	ArrayList<Keyword> numberList = nf.getNumberList();
	ArrayList<Keyword> special = nf.getSpecial();
			
	// 형태소분석
	TextAnalyzer ta = new TextAnalyzer();
	ta.SetChunk(keyword, numberList, special);
	analyzedOrder = ta.Anaylze(chunkedOrder);
	//out.println("<p>" + analyzedOrder + "</p>");
	
%>