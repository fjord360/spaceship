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
	out.println("ûŷ��� : " + order);
	
	ArrayList<Keyword> keyword = nf.getKeyword();
	for( int i = 0 ; i < keyword.size() ; i++ ) {
		if( keyword.get(i).pos == Pos.S ) out.println("<p>����� Ű���� : " + keyword.get(i).text + " (S/" + keyword.get(i).index + ")</p>");
		if( keyword.get(i).pos == Pos.C ) out.println("<p>����� Ű���� : " + keyword.get(i).text + " (C/" + keyword.get(i).index + ")</p>");
	}
	
	TextAnalyzer ta = new TextAnalyzer();
	ta.SetKeyword(keyword);
	out.println("anaylze : " + ta.Anaylze(order));
	
	SpaceDB db = new SpaceDB();
	db.CreateDB("jdbc:mysql://localhost/stardb?characterEncoding=UTF-8", "root", "");
	String query = ta.getQuery();
	System.out.println("query : " + query);
	out.println("query : " + query);
	
	out.println("<div class='query' style='font-size:10pt; color:black'>");
	out.println("<p>" + query + "</p>");
	
	if( !query.equals("NoResult") ) {
		db.Query(query);
		ArrayList<String> gf = db.getField(query);
		System.out.println("field : " + gf);
		while( db.getDB().next()) {
			for( int i = 0 ; i < gf.size() ; i++ ) {
				out.println(gf.get(i) + " : " + db.getDB().getString(gf.get(i)) + "<br>");
			}
		}
		out.println("</div>");
	}

	/*FileOutputStream fos = new FileOutputStream("WebContent/log.txt");
	OutputStreamWriter osw = new OutputStreamWriter(fos, "UTF-8");	    
	osw.write(order);
	osw.close();*/
		
%>
</body>
</html> 