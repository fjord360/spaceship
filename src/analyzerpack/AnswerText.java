package analyzerpack;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

class AnswerPattern {
	public String name;
	public ArrayList<String> text;
	
	public AnswerPattern(String Name, ArrayList<String> Text) {
		name = Name;
		text = Text;
	}
	public void AddString(String Text) {
		text.add(Text);
	}
}

public class AnswerText {
	
	ArrayList<AnswerPattern> answerPattern = new ArrayList<AnswerPattern>();
	ArrayList<String> NAME = new ArrayList<String>();
	ArrayList<String> FIELD = new ArrayList<String>();
	ArrayList<String> VALUE = new ArrayList<String>();
	String FROM = "";
	String NUM = "";
	
	public void CreatAnswerPattern() {
		try {
			String file = "WebContent/Dictionary/answer.txt";
			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"));
		
			boolean first = true;
			String line = "";
			while (( line = br.readLine()) != null ) {
				if (first) {
					if( line.startsWith("\uFEFF")) {
						line = line.substring(1);
					}
				}
				first = false;
				
				// 정답 패턴 추가..
				if( line.charAt(0) == '@' ) {
					String[] split = line.split("=");
					String patternName = split[0].substring(1);
					
					// 필드별로 패턴을 추가함.
					boolean original_flag = false;
					for( int i = 0 ; i < answerPattern.size() ; i++ ) {
						if( patternName.equals(answerPattern.get(i).name) ) {
							original_flag = true;
							answerPattern.get(i).AddString(split[1]);
						}
					}
					// 기존 패턴이름에 없으면 새로운 패턴 추가..
					if( !original_flag ) {
						ArrayList<String> tList = new ArrayList<String>();
						tList.add(split[1]);
						AnswerPattern newAP = new AnswerPattern(patternName, tList);
						answerPattern.add(newAP);
					}
				}
			}
			
		} catch(Exception e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
	}
	
	// 쿼리를 대화형 정답 텍스트로 변환..
	public String AnswerFromQuery(String order, String query, ArrayList<String> value) {
		String Answer = "";
		FROM = "";
		NUM = "";
		if( value != null ) {
			VALUE = value;
			NUM = Integer.toString(VALUE.size());
		}
		
		if( query.charAt(0) == '@' ) {
			Answer = RandomPattern(query.substring(1, query.length()));
		}
		// 쿼리 형태일 경우
		else {
			// 필드값 추출
			if( query.indexOf("FROM") >= 0 ) {
				String fromsplit[] = query.split("FROM");
			
				// FROM을 통해 가져오는 테이블 이름 확인
				if( fromsplit.length > 2 ) {
					if( fromsplit[1].indexOf("별자리") >= 0 ) FROM = "C";
					else {
						if( fromsplit[1].indexOf("별") >= 0 ) FROM = "S";
					}
				}
			
				// SELECT와 FROM 사이
				String split[] = fromsplit[0].split("SELECT");
				if( split.length > 1 ) {
					split[1] = split[1].trim();
					FIELD.add(split[1]);
				}
		
				// 답이 이름일 경우
				if( FIELD.get(0).equals("이름") ) {
					Answer = RandomPattern("다중검색");
					for( int i = 0 ; i < VALUE.size() ; i++ ) {
						Answer += ("@" + FROM + ":" + VALUE.get(i));
					}
				}
		
				// 답이 값일 경우
				else {
					// NAME 추출
					String[] querySplit = query.split("WHERE 이름='");
					if( querySplit.length > 1 ) {
						String[] nameSplit = querySplit[1].split("'");
						NAME.add(nameSplit[0]);
					}
				
					// 필드정보 패턴이 정답
					Answer = RandomPattern("필드정보");
				
					// 값에 -12345가 들어가 있으면..
					if( value.size() > 0 ) {
						if( value.get(0).equals("-12345") ) {
							Answer = RandomPattern("필드몰라요");
						}
					}
				
					VALUE = value;
				}
			}
			else {
				Answer = RandomPattern("몰라요");
			}
		}
		
		// 태그 추출
		int chk = 0;
		String tag = "";
		boolean extract = false;
		while ( chk <= Answer.length() - 3 ) {
			// >가 나올때까지 추출
			if( extract ) {
				if( Answer.charAt(chk) == '>' ) {
					String original = "<" + tag + ">";
					String replace = ReplaceTag(tag);
					Answer = Answer.replaceFirst(original, replace);
					tag = "";
					extract = false;
					chk += (replace.length() - original.length());
				}
				else {
					tag += Answer.charAt(chk);
					chk++;
				}
			}
			// 태그는 <로 시작함
			else {
				if( Answer.charAt(chk) == '<' ) {
					extract = true;
				}
				chk++;
			}
		}
		
		return Answer;
	}
	
	// 여러가지 정답 패턴 중에서 랜덤으로 뽑아옴
	String RandomPattern(String field) {
		String Text = "";
		for( int i = 0 ; i < answerPattern.size() ; i++ ) {
			if( answerPattern.get(i).name.equals(field) ) {
				int randomPattern = (int)(Math.random() * answerPattern.get(i).text.size());
				Text = answerPattern.get(i).text.get(randomPattern);
			}
		}
		return Text;
	}
	
	// 태그를 원래 값으로 변경
	String ReplaceTag(String tag) {
		String value = "";
		String[] split = tag.split(":");
		
		// ":"로 나뉘어진 정상적인 태그일 경우에만 변경 
		if( split.length >= 2 ) {
			if( split[0].equals("FIELD") ) value = FIELD.get(Integer.parseInt(split[1]));
			if( split[0].equals("NAME") ) value = NAME.get(Integer.parseInt(split[1]));
			if( split[0].equals("VALUE") ) value = VALUE.get(Integer.parseInt(split[1]));
			
			// 조사 있음
			if( split.length >= 3 ) {
				value = GetJosa(value, split[2].charAt(0));
			}
		}
		else {
			if( tag.equals("NUM") ) value = NUM;
		}
		
		return value;
	}
	
	// 받침에 맞는 조사를 얻어옴
	String GetJosa(String text, char type) {
		int code = text.codePointAt(text.length() - 1) - 44032;

		// 원본 문구가 없을때는 빈 문자열 반환
		if( text.length() == 0 ) return "";

		// 한글이 아닐때
		if( code < 0 || code > 11171) return text;

		if( code % 28 == 0 ) return text + Josa(type, false);
		else return text + Josa(type, true);
	}
	char Josa(char josa, boolean jong) {
		// jong : true면 받침있음, false면 받침없음
		if (josa == '을' || josa == '를') return (jong?'을':'를');
		if (josa == '이' || josa == '가') return (jong?'이':'가');
		if (josa == '은' || josa == '는') return (jong?'은':'는');
		if (josa == '와' || josa == '과') return (jong?'와':'과');
		// 알 수 없는 조사
		return ' ';
	}
}
