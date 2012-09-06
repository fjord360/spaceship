<%@ page language="java" contentType="text/html; charset=EUC-KR" pageEncoding="EUC-KR"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
	<title>Spaceship</title>
	<script language="JavaScript">
		var xmlHttp;
		
		// AJAX XMLHTTP 리퀘스트
		function createXMLHttpRequest() {
			if(window.ActiveXObject) {
				xmlHttp = new ActiveXObject("Microsoft.XMLHTTP");
			} else if(window.XMLHttpRequest) {
				xmlHttp = new XMLHttpRequest();
			}
		}
		
		// 키를 입력했을 때 처리
		function onKeyUp() {
			// 엔터키 누르면 결과 출력
			if( event.keyCode == 13 ) {
				request();
			}
		}
		
		// 결과를 출력하기 위해 result.jsp에 결과값 요청
		function request() {
			createXMLHttpRequest();
			param = orderform.order.value;
			var url = "result_test.jsp?order=" + encodeURIComponent(param);
			xmlHttp.onreadystatechange = result;
			xmlHttp.open("GET", url, true);
			xmlHttp.send(null);
		}
		
		// result.jsp로부터 받아온 결과(html)를 'content' div에 출력
		function result() {
			if( xmlHttp.readyState == 4 ) {
				if( xmlHttp.status == 200 ) {
					temp = xmlHttp.responseText;

					document.getElementById("content").innerHTML = temp;
				}
			}
		}
	</script>
</head>
<body>

<!-- 입력 부분 -->
<form method="post" name="orderform">
<div style="text-align:center">
	<!-- 명령을 내리는 텍스트박스. 키를 누를때마다 onKeyUp 함수 호출 -->
	<input type="text" name="order" size=100 onkeyup="onKeyUp()" />
	<!-- 텍스트박스가 1개면 엔터키 눌렀을 때 refresh가 되서 숨겨진 텍스트박스를 하나 더 만들었 -->
	<input type="text" name="hiddentext" size=0 style="display:none" />
</div>
</form>

<!-- 여기에 결과값이 출력된다 -->
<div id="content" style="text-align:center">
</div>

</body>

</html>