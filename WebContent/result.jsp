<%@ page language="java" contentType="text/html; charset=EUC-KR" pageEncoding="EUC-KR"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@page import="analyzerpack.NameFinder" %>
<%@page import="analyzerpack.Keyword" %>
<%@page import="analyzerpack.TextAnalyzer" %>
<%@page import="analyzerpack.ResultAnalyzer" %>
<%@page import="analyzerpack.Pos" %>
<%@page import="dbpack.SpaceDB" %>
<%@page import="java.util.ArrayList" %>
<%
	request.setCharacterEncoding("utf-8");
	String order = request.getParameter("order");
%>
<html>
<head>
</head>
<body>
<%	
	NameFinder nf = new NameFinder();
	nf.CreateMap();
	order = nf.Find(order);
	out.println("청킹결과 : " + order);
	
	ArrayList<Keyword> keyword = nf.getKeyword();
	for( int i = 0 ; i < keyword.size() ; i++ ) {
		if( keyword.get(i).pos == Pos.S ) out.println("<p>추출된 키워드 : " + keyword.get(i).text + " (S/" + keyword.get(i).index + ")</p>");
		if( keyword.get(i).pos == Pos.C ) out.println("<p>추출된 키워드 : " + keyword.get(i).text + " (C/" + keyword.get(i).index + ")</p>");
	}
	
	TextAnalyzer ta = new TextAnalyzer();
	ta.SetKeyword(keyword);
	out.println(ta.Anaylze(order));
	
	SpaceDB db = new SpaceDB();
	db.CreateDB("jdbc:mysql://localhost/space?characterEncoding=UTF-8", "root", "");
	String query = ta.getQuery();
	out.println(query);
	db.Query(query);
	
	while( db.getDB().next() ) {
		out.println("<p>결과 : " + db.getDB().getString("별밝기") + "</p>");
	}

	/*FileOutputStream fos = new FileOutputStream("WebContent/log.txt");
	OutputStreamWriter osw = new OutputStreamWriter(fos, "UTF-8");	    
	osw.write(order);
	osw.close();*/
		
%>
</body>
</html> 