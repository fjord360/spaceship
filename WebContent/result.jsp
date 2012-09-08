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
	request.setCharacterEncoding("utf-8");
	//String order = new String(request.getParameter("order").getBytes("ISO-8859-1"), "UTF-8");
	String order = request.getParameter("order");
	
	// IP 체크
	String USER_IP = request.getRemoteAddr();
	String answer = "";
	String query = "NoResult";
	String chunkedOrder = "";
	String analyzedOrder = order;
	ArrayList<String> VALUE = new ArrayList<String>();
	ArrayList<String> gf = new ArrayList<String>();
	//long start = System.currentTimeMillis();
	boolean isStart = false;

	// 이름/숫자 청킹
	if( order.charAt(0) != '@' ) {
		NameFinder nf = new NameFinder();
		nf.CreateMap();
		chunkedOrder = nf.Find(order);
		//out.println("<p>" + chunkedOrder + "</p>");
	
		// 청킹한 이름, 숫자, 특수문자를 리스트에 넣는다.
		ArrayList<Keyword> keyword = nf.getKeyword();
		ArrayList<Keyword> numberList = nf.getNumberList();
		ArrayList<Keyword> special = nf.getSpecial();
		
		// 형태소분석
		TextAnalyzer ta = new TextAnalyzer();
		ta.SetChunk(keyword, numberList, special);
		//out.println("<p>" + "chunkedOrder : " + chunkedOrder + "</p>");
		analyzedOrder = ta.Anaylze(chunkedOrder);
		//out.println("<p>" + "analyzedOrder : " + analyzedOrder + "</p>");
		query = ta.getQuery();
	}
	else {
		query = "NoResult";
	}
		
	SpaceDB db = new SpaceDB();
	//db.CreateDB("jdbc:mysql://localhost/spaceteam?characterEncoding=EUCKR", "spaceteam", "tlstmddms1");
	db.CreateDB("jdbc:mysql://localhost/stardb?characterEncoding=EUCKR", "root", "root");
	
	// 비쿼리
	if( query.charAt(0) == '@' ) order = query;
	//out.println("<p>" + "query : " + query + "</p>");
	
	// 쿼리형태의 답이 나왔을 때
	if( !query.equals("NoResult") && query.charAt(0) != '@' ) {
		db.Query(query);
		gf = db.getField(query);
		while( db.getDB().next() ) {
			for( int i = 0 ; i < gf.size() ; i++ ) {
				String dbValue = db.getDB().getString(gf.get(i));
				if( dbValue == null ) VALUE.add("-12345");
				else 				  VALUE.add(dbValue);
			}
		}
		
		// 정답 받아옴
		AnswerText at = new AnswerText();
		at.CreatAnswerPattern();
		answer = at.AnswerFromQuery(order, query, VALUE);
	}
	// 쿼리형태의 답이 아닐 
	else {
		if( order.indexOf("@INFORMATION") >= 0 ) {
			String infosplit[] = order.split("=");
			String infotable = infosplit[1];
			String infoname = infosplit[2];
			if( infoname.indexOf("'") >= 0 ) infoname.replaceAll("'", "");
			
			//out.println(infoname);
			
			// 이름 정보
			if( infoname.equals("START_TALK") ) {
				infoname = "지구";
				isStart = true;
			}
			VALUE.add(infoname);
			String frompos = "";
			
			// 별자리에 대한 정보
			if( infotable.equals("C") ) {
				frompos = "별자리";
				
				// 별자리에 대한 정보를 요청했으므로 해당 별자리의 정보를 DB에서 검색
				ArrayList<String> infofield = new ArrayList<String>();
				infofield.add("이름");
				infofield.add("적경_시");
				infofield.add("적경_분");
				infofield.add("적위_도");
				infofield.add("적위_분");
				//infofield.add("유래");
				
				query = "SELECT ";
				for( int i = 0 ; i < infofield.size() ; i++ ) {
					query += infofield.get(i);
					if( i != infofield.size()-1 ) query += ",";
				}
				query += " FROM " + frompos + " WHERE 이름='" + infoname + "'";
				db.Query(query);
				gf = db.getField(query);
				answer = infoname + "에 대한 정보입니다.$";
				
				// 사용자가 입력한 기준별과 주변별 정보를 전송..
				//answer += "@" + Integer.toString(numNearStar+1);
				//for( int i = 0 ; i < numNearStar+1 ; i++ ) {
					//answer += bm.getStarInfo(0, i);
				//}
			}
			
			// 별에 대한 정보
			if( infotable.equals("S") ) {
				frompos = "별";
			
				// 별에 대한 정보를 요청했으므로 해당 별의 정보를 DB에서 검색
				ArrayList<String> infofield = new ArrayList<String>();
				infofield.add("이름");
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
				infofield.add("어미항성");
				infofield.add("궤도거리");
				infofield.add("공전주기");
				infofield.add("텍스쳐");
				infofield.add("고리");
				query = "SELECT ";
				for( int i = 0 ; i < infofield.size() ; i++ ) {
					query += infofield.get(i);
					if( i != infofield.size()-1 ) query += ",";
				}
				query += " FROM " + frompos + " WHERE 이름='" + infoname + "'";
				db.Query(query);
				gf = db.getField(query);
				if( isStart ) answer = "안녕하세요. 저는 JOURNEY에요. 우주에 대해 무엇이든 물어보세요.";
				else answer = infoname + "에 대한 정보입니다.";
				
				// 사용자가 요청한 기준별 먼저 불러옴
				Buildmap bm = new Buildmap();
				bm.addStar(infoname);
				while( db.getDB().next() ) {
					for( int i = 0 ; i < gf.size() ; i++ ) {
						String dbval = db.getDB().getString(gf.get(i));
						bm.addInfo(0, infofield.get(i), dbval);
					}
				}
				
				// 주변별이 있는지 DB에서 검색
				String near_query = "";
				int numNearStar = 0;
				int nearFirst = 0;
				if( bm.star.get(0).getType().equals("star") ) {
					near_query = "SELECT ";
					for( int i = 0 ; i < infofield.size() ; i++ ) {
						near_query += infofield.get(i);
						if( i != infofield.size()-1 ) near_query += ",";
					}
					near_query += " FROM 별 WHERE ((적경_시*100)+(적경_분))>=" + (bm.star.get(0).getAscCoord()-100.0f) + " AND ";
					near_query += "((적경_시*100)+(적경_분))<=" + (bm.star.get(0).getAscCoord()+100.0f) + " AND ";
					near_query += "((적위_도*100)+(적위_분))>=" + (bm.star.get(0).getDecCoord()-100.0f) + " AND ";
					near_query += "((적위_도*100)+(적위_분))<=" + (bm.star.get(0).getDecCoord()+100.0f) + " AND ";
					near_query += "거리>=" + (bm.star.get(0).getDistance()-10.0f) + " AND ";
					near_query += "거리<=" + (bm.star.get(0).getDistance()+10.0f) + " AND ";
					near_query += "타입='star' AND ";
					near_query += "이름 NOT IN('" + infoname + "')";
					db.Query(near_query);
					gf = db.getField(near_query);
			
					// 검색된 데이터가 주변 별들이니 이 별들의 정보도 얻어옴
					numNearStar = 0;
					while( db.getDB().next() ) {
						numNearStar++;
						for( int i = 0 ; i < gf.size() ; i++ ) {
							String dbval = db.getDB().getString(gf.get(i));
							//out.println(dbval);
							if( gf.get(i).equals("이름") ) {
								bm.addStar(dbval);
							}
							else {
								bm.addInfo(numNearStar, infofield.get(i), dbval);
							}
						}
					}
				}
				
				// 별에 대한 행성이 있는지 DB에서 검색
				near_query = "SELECT ";
				for( int i = 0 ; i < infofield.size() ; i++ ) {
					near_query += infofield.get(i);
					if( i != infofield.size()-1 ) near_query += ",";
				}
				
				// 기준별이 항성이면 이 별을 어미항성으로 하는 행성을 찾음
				near_query += " FROM 별 WHERE (이름='" + bm.star.get(0).getMotherStr() + "' OR ";
				near_query += "어미항성='" + infoname + "' OR ";
				near_query += "어미항성='" + bm.star.get(0).getMotherStr() + "') AND ";
				near_query += "이름 NOT IN('" + infoname + "') order by 타입 desc";
				//out.println(near_query);
				db.Query(near_query);
				gf = db.getField(near_query);
			
				// 찾은 별들의 정보도 얻어옴
				while( db.getDB().next() ) {
					numNearStar++;
					for( int i = 0 ; i < gf.size() ; i++ ) {
						String dbval = db.getDB().getString(gf.get(i));
						if( gf.get(i).equals("이름") ) {
							bm.addStar(dbval);
						}
						else {
							bm.addInfo(numNearStar, infofield.get(i), dbval);
						}
					}
				}
				
				// 사용자가 입력한 기준별과 주변별 정보를 전송..
				answer += "@" + Integer.toString(numNearStar+1);
				for( int i = 0 ; i < numNearStar+1 ; i++ ) {
					answer += bm.getStarInfo(0, i);
				}
			}
		}
		else {
			// 아무것도 아님
			AnswerText at = new AnswerText();
			at.CreatAnswerPattern();
			answer = at.AnswerFromQuery(order, query, VALUE);
		}
	}
	
	out.println(answer);
	
%>