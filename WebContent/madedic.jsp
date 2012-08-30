<%@ page language="java" contentType="text/html; charset=EUC-KR" pageEncoding="EUC-KR"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@page import="java.sql.*" %>
<%@page import="java.io.FileOutputStream" %>
<%@page import="java.io.OutputStreamWriter" %>
<html>
<head>
</head>
<body>
<%

String URL = "jdbc:mysql://localhost/stardb?characterEncoding=UTF-8";
String USER = "root";
String PASS = "";

Connection conn = null;
Statement stmt = null;
ResultSet rs = null;


try {
	
	Class.forName("org.gjt.mm.mysql.Driver");
	conn = DriverManager.getConnection(URL, USER, PASS);
	stmt = conn.createStatement();
	rs = stmt.executeQuery("select * from º°");
	
	FileOutputStream fos = new FileOutputStream("WebContent/Dictionary/star_dic.txt");
	OutputStreamWriter osw = new OutputStreamWriter(fos, "UTF-8");
	
	while( rs.next() ) {
		String name = rs.getString("ÀÌ¸§");
		String attachedName = name.replaceAll("\\s", "");
		osw.write(attachedName + "@" + name + "\n");
	}
	
	osw.close();
} catch(Exception e) {
	System.out.println(e.getMessage());
	e.printStackTrace();
} finally {
	
}
%>
</body>
</html> 