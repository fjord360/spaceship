<%@ page language="java" contentType="text/html; charset=EUC-KR" pageEncoding="EUC-KR"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@page import="java.io.FileOutputStream" %>
<%@page import="java.io.OutputStreamWriter" %>
<%@page import="java.sql.*" %>
<%@page import="org.apache.commons.logging.Log" %>
<%@page import="org.apache.commons.logging.LogFactory" %>
<%@page import="org.apache.lucene.analysis.kr.morph.MorphAnalyzer" %>
<%@page import="org.apache.lucene.analysis.kr.KoreanTokenizer" %>
<%@page import="org.apache.lucene.analysis.Token" %>
<%@page import="org.apache.lucene.analysis.kr.morph.AnalysisOutput" %>
<%@page import="java.util.List" %>
<%@page import="java.io.StringReader" %>
<html>
<head>
</head>
<body>
<%

	// DB 생성
	Connection conn = null;
	Statement stmt = null;
	ResultSet rs = null;
	
	try{
		Class.forName("org.gjt.mm.mysql.Driver");
		conn = DriverManager.getConnection("jdbc:mysql://localhost/space", "root", "");
		stmt = conn.createStatement();
	} catch(Exception e) {
		System.out.println(e.getMessage());
		e.printStackTrace();
	}
	
	out.println("<table border=1 style='font-size:10pt;color:black'");
	
	rs = stmt.executeQuery("select * from stars");
	
	while( rs.next() ) {
		out.println("<tr>");
		out.println("<td width=300'>");
		String order = "책상별로 가주세요";
		out.println(order);

		if( !"".equals(order) ) {
			// 형태소분석기 클래스 생성
			MorphAnalyzer analyzer = new MorphAnalyzer();
			KoreanTokenizer tokenizer = new KoreanTokenizer(new StringReader(order));
			Token token = null;
		
			while( ( token = tokenizer.next() ) != null ) {

				out.println("<td width=150 style='color:blue'>");
				out.println(token.termText());
				out.println("</td>");
			
				try {
					// token.termText()를 하면 다음 토큰을 출력.
					String tokenText = token.termText();
					analyzer.setExactCompound(false);
				
					List<AnalysisOutput> results = analyzer.analyze(tokenText);
				
					for( AnalysisOutput o : results ) {
						out.println("<td width=200>");
						out.println(o.toString());
						out.println("</td>");
					}
				
				} catch (Exception e) {					
					System.out.println(e.getMessage());					
					e.printStackTrace();
				}
			}
		}
		out.println("</tr>");
	}

%>
</body>
</html> 