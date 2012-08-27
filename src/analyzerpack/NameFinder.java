package analyzerpack;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.Map;
import java.util.ArrayList;

// 띄어쓰기 클래스 (이름, 타입)
// type0 -> 이 글자 다음이 띄어쓰기가 아님
// type1 -> 이 글자 다음이 띄어쓰기임
class SpaceText {
	public String text = "";
	public int type = 0;
	public SpaceText(String Text, int Type) {
		text = Text;
		type = Type;
	}
}

// 이름 후보 클래스 (이름, 품사, 정식이름번호, 일치율)
class Lineup {
	public String text = "";
	public Pos pos;
	public int original;
	public float concordance = 0;
	public Lineup(String Text, Pos POS, int Original, float Concordance) {
		text = Text;
		pos = POS;
		original = Original;
		concordance = Concordance;
	}
}

// 이름사전 클래스 (정식이름, 동의어리스트)
class NameDic {
	public String name;
	public ArrayList<String> synonym;
	public NameDic(String Name, ArrayList<String> Synonym) {
		name = Name;
		synonym = Synonym;
	}
}

public class NameFinder {
	
	ArrayList<NameDic> star;			// 별 이름사전
	ArrayList<NameDic> constellation;	// 별자리 이름사전
	ArrayList<Keyword> keyword;			// 키워드 청킹 리스트
	ArrayList<Keyword> numberList;		// 숫자 청킹 리스트
	ArrayList<Keyword> special;			// 특수문자 청킹 리스트
	
	// 생성자
	public NameFinder() {
		star = new ArrayList<NameDic>();
		constellation = new ArrayList<NameDic>();
		keyword = new ArrayList<Keyword>();
		numberList = new ArrayList<Keyword>();
		special = new ArrayList<Keyword>();
	}
	
	// 이름 사전을 불러와서 해쉬맵에 저장
	public void CreateMap() {
		
		try {
			String file = "WebContent/Dictionary/constellation_dic.txt";
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
			    
			    // 모든 소문자를 대문자로 변경
			    line = line.toUpperCase();
			    
			    // "="을 기준으로 앞은 정식 이름
			    String []split = line.split("=");
			    
			    // "=" 뒤는 동의어. ","를 기준으로 잘라서 동의어 리스트에 넣음.
			    ArrayList<String> syn = new ArrayList<String>();
			    if( split.length > 1 ) {
			    	String []synsplit = split[1].split(",");
			    	for( int i = 0 ; i < synsplit.length ; i++ ) {
			    		syn.add(synsplit[i]);
			    	}
			    }
			    
			    constellation.add(new NameDic(split[0], syn));
			}
			
		} catch(Exception e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
		
		try {
			String file = "WebContent/Dictionary/star_dic.txt";
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
			    
			    // 모든 소문자를 대문자로 변경
			    line = line.toUpperCase();
			    
			    // "="을 기준으로 앞은 정식 이름
			    String []split = line.split("=");
			    
			    // "=" 뒤는 동의어. ","를 기준으로 잘라서 동의어 리스트에 넣음.
			    ArrayList<String> syn = new ArrayList<String>();
			    if( split.length > 1 ) {
			    	String []synsplit = split[1].split(",");
			    	for( int i = 0 ; i < synsplit.length ; i++ ) {
			    		syn.add(synsplit[i]);
			    	}
			    }
			    
			    star.add(new NameDic(split[0], syn));
			}
			
		} catch(Exception e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
	}
	
	public String Find(String order) {
		
		// 소문자는 모두 대문자로 변경
		order = order.toUpperCase();
		
		// 띄어쓰기 정보를 미리 저장해둔다.
		ArrayList<SpaceText> adhere = new ArrayList<SpaceText>();
		String []split = order.split(" ");
		for( int i = 0 ; i < split.length ; i++ ) {
			for( int j = 0 ; j < split[i].length() ; j++ ) {
				String sub = split[i].substring(j, j+1);
				// 뒤에 띄어쓰기가 나오는 단어의 type은 1
				if( i < split.length && j == split[i].length() - 1 ) {
					adhere.add(new SpaceText(sub, 1));
				}
				// 뒤에 띄어쓰기가 나오지 않는 단어의 type은 0
				else {
					adhere.add(new SpaceText(sub, 0));
				}
			}
		}
		//for( int i = 0 ; i < adhere.size() ; i++ ) {
			//System.out.println(adhere.get(i).text + "/" + adhere.get(i).type);
		//}
		
		int start_index = 0;
				
		while( start_index < adhere.size() - 1 ) {
			// 한 글자씩 검사.
			String preOrder = adhere.get(start_index).text;
			//System.out.println("검사 : " + start_index + "(" + preOrder + ")");
			
			// 명령의 n번째 글자로 시작하는 이름이 사전에 있다면 검색 후보에 추가..!
			// 별 목록에 있는지 확인
			ArrayList<Lineup> lineup = new ArrayList<Lineup>();
			for( int i = 0 ; i < star.size() ; i++ ) {
				String prefix = star.get(i).name;
				if( prefix.substring(0, 1).equals(preOrder) ) {
					lineup.add(new Lineup(prefix, Pos.S, -1, 0.0f));
				}
				
				for( int j = 0 ; j < star.get(i).synonym.size() ; j++ ) {
					prefix = star.get(i).synonym.get(j);
					if( prefix.substring(0, 1).equals(preOrder) ) {
						lineup.add(new Lineup(prefix, Pos.S, i, 0.0f));
					}
				}
			}
			
			// 별자리 목록에 있는지 확인해 사전에 추가
			for( int i = 0 ; i < constellation.size() ; i++ ) {
				String prefix = constellation.get(i).name;
				if( prefix.substring(0, 1).equals(preOrder) ) {
					lineup.add(new Lineup(prefix, Pos.C, -1, 0.0f));
				}
				
				for( int j = 0 ; j < constellation.get(i).synonym.size() ; j++ ) {
					prefix = constellation.get(i).synonym.get(j);
					if( prefix.substring(0, 1).equals(preOrder) ) {
						lineup.add(new Lineup(prefix, Pos.C, i, 0.0f));
					}
				}
			}
			
			// 한 글자씩 찾아보면서 일치율을 결정한다.
			for( int i = 0 ; i < lineup.size() ; i++ ) {
				float max_con = 0.0f;
				String comp = "";
				for( int j = start_index ; j < adhere.size() ; j++ ) {
					comp += adhere.get(j).text;
					float approx = Concordance(lineup.get(i).text, comp);
					if( approx > max_con ) max_con = approx;
				}
				lineup.get(i).concordance = max_con;
			}
			
			int choice = -1;
			int max_length = -1;
			for( int i = 0 ; i < lineup.size() ; i++ ) {
				// 일치율이 100%인 후보가 당첨. 100%인게 2개 이상이면 더 긴게 키워드로 당첨.
				if( lineup.get(i).concordance >= 1.0f ) {
					if( lineup.get(i).text.length() > max_length ) {
						choice = i;
						max_length = lineup.get(i).text.length();
					}
				}
				//System.out.println("앞글자 같은 후보들 : " + lineup.get(i).text + "(" + lineup.get(i).concordance + ")");
			}
			
			// 정확도가 100%인 후보만 키워드로 추출.
			if( lineup.size() > choice && choice >= 0 ) {
				//System.out.println("* 당첨 : " + lineup.get(choice).text + "(" + lineup.get(choice).concordance + ")");
				
				// 키워드에는 정식이름으로 넣는다.
				String formal = lineup.get(choice).text;
				
				// 동의어가 채택되었으면 정식이름을 키워드 리스트에 넣는다.
				if( lineup.get(choice).original != -1 ) {
					int ori = lineup.get(choice).original;
					if( lineup.get(choice).pos == Pos.C ) formal = constellation.get(ori).name;
					if( lineup.get(choice).pos == Pos.S ) formal = star.get(ori).name;
					//System.out.println("original : " + formal);
				}
				keyword.add(new Keyword(formal, lineup.get(choice).pos, start_index, lineup.get(choice).text.length()));
				start_index += (lineup.get(choice).text.length() - 1);
			}
			start_index++;
		}
		
		int rep_index = 0;
		while( rep_index < adhere.size() ) {
			// 예외처리 : "별"이라는 문자가 나오면 그 앞은 무조건 띄어쓰기
			if( rep_index > 0 && adhere.get(rep_index).text.equals("별") ) {
				if( adhere.get(rep_index-1).type == 0 ) {
					adhere.get(rep_index-1).type = 1;
				}
			}
			
			// 추출된 키워드 부분은 대체명사 로 바꾼다.
			// 별 : 쑹 // 별자리 : 쏭
			for( int j = 0 ; j < keyword.size() ; j++ ) {
				
				if( rep_index == keyword.get(j).index ) {			
					// 키워드 앞은 무조건 띄어쓰기를 한다.
					if( rep_index > 0 && adhere.get(rep_index-1).type == 0 ) {
						adhere.get(rep_index-1).type = 1;
					}
					// 키워드를 대체명사로 바꾸고 키워드 사이의 띄어쓰기는 모두 없엔다. 마지막은 제외.
					for( int k = 0 ; k < keyword.get(j).originalLength ; k++ ) {
						if( keyword.get(j).pos == Pos.S ) adhere.get(rep_index+k).text = "쑹";
						if( keyword.get(j).pos == Pos.C ) adhere.get(rep_index+k).text = "쏭";
						if( k != keyword.get(j).text.length() - 1 ) adhere.get(rep_index+k).type = 0;
					}
					rep_index += (keyword.get(j).text.length() - 1);
				}
			}
			rep_index++;
		}
		
		// 숫자를 추출해서 모두 '쯩'으로 바꾼다.
		String addnumber = "";
		for( int i = 0 ; i < adhere.size() ; i++ ) {
			// 숫자를 캐치.
			if( Character.getType(adhere.get(i).text.charAt(0)) == 9 ) {
				addnumber += adhere.get(i).text.charAt(0);
				adhere.get(i).text = "쯩";
				
				// 숫자 앞은 무조건 띄어쓰기
				if( i != 0 ) {
					if( adhere.get(i-1).type == 0 ) adhere.get(i-1).type = 1;
				}
			}
			else {
				// 숫자가 끝나면 그 전까지의 스트링을 숫자 리스트에 넣는다.
				if( addnumber.length() > 0 ) {
					numberList.add(new Keyword(addnumber, Pos.I, i, addnumber.length()));
					addnumber = "";
					
					// 숫자 다음은 무조건 띄어쓰기
					if( i != 0 ) {
						if( adhere.get(i-1).type == 0 ) adhere.get(i-1).type = 1;
					}
				}
			}
		}
		
		// 특수문자를 추출해서 모두 '뚤'으로 바꾼다.
		String addspecial = "";
		for( int i = 0 ; i < adhere.size() ; i++ ) {
			// 특수문자를 캐치. 일단 ','만..
			if( adhere.get(i).text.charAt(0) == ',' ) {
				addspecial += adhere.get(i).text.charAt(0);
				adhere.get(i).text = "뚤";
				
				// 특수문자 앞은 무조건 띄어쓰기
				if( i != 0 ) {
					if( adhere.get(i-1).type == 0 ) adhere.get(i-1).type = 1;
				}
			}
			else {
				// 숫자가 끝나면 그 전까지의 스트링을 숫자 리스트에 넣는다.
				if( addspecial.length() > 0 ) {
					special.add(new Keyword(addspecial, Pos.A, i, addspecial.length()));
					addspecial = "";
					
					// 숫자 다음은 무조건 띄어쓰기
					if( i != 0 ) {
						if( adhere.get(i-1).type == 0 ) adhere.get(i-1).type = 1;
					}
				}
			}
		}
		
		// 이제 띄어쓰기에 맞춰서 order를 재정렬한다.
		String newOrder = "";
		for( int i = 0 ; i < adhere.size() ; i++ ) {
			// 연속된 "쑹"은 무시. 즉 "쑹쑹쑹"을 "쑹"으로 만든다.
			if( i != adhere.size()-1 ) {
				if( !(adhere.get(i).text == "쑹" && adhere.get(i+1).text == "쑹") && 
					!(adhere.get(i).text == "쏭" && adhere.get(i+1).text == "쏭") &&
					!(adhere.get(i).text == "쯩" && adhere.get(i+1).text == "쯩") &&
					!(adhere.get(i).text == "뚤" && adhere.get(i+1).text == "뚤")) {
					newOrder += adhere.get(i).text;
				}
			} else {
				newOrder += adhere.get(i).text;
			}
			
			// 기존에 띄어쓰기 정보가 저장되어 있었다면 그대로 띄어쓰기
			if( adhere.get(i).type == 1 ) {
				newOrder += " ";
			}
		}
		
		return newOrder;
	}
	
	// 두 문자열의 일치율 계산하는 함수
	float Concordance(String arg0, String arg1) {
		 float con = 0.0f;
		 int matches = 0;
		 int same = 0;
		 	
		 if( arg0.length() > arg1.length() ) {
		 	matches = arg0.length();
		 	for( int i = 0 ; i < matches ; i++ ) {
		 		if( i < arg1.length() ) {
		 			if( arg0.substring(i, i+1).equals(arg1.substring(i, i+1)) ) same++;
		 		}
		 	}
		 }
		 else if( arg0.length() < arg1.length() ) {
		 	matches = arg1.length();
		 	for( int i = 0 ; i < matches ; i++ ) {
		 		if( i < arg0.length() ) {
		 			if( arg1.substring(i, i+1).equals(arg0.substring(i, i+1)) ) same++;
		 		}
		 	}
		 }
		 else {
		 	matches = arg0.length();
		 	for( int i = 0 ; i < matches ; i++ ) {
		 		if( arg0.substring(i, i+1).equals(arg1.substring(i, i+1)) ) same++;
		 	}
		}
		con = (float)same / (float)matches;
		 	
		return con;
	}
	
	// 키워드 리턴 함수
	public ArrayList<Keyword> getKeyword() {
		return keyword;
	}
	public Keyword getKeyword(int index) {
		return keyword.get(index);
	}
	
	// 숫자리스트 리턴 함수
	public ArrayList<Keyword> getNumberList() {
		return numberList;
	}
	public Keyword getNumberList(int index) {
		return numberList.get(index);
	}
	
	// 특수문자 리턴 함수
	public ArrayList<Keyword> getSpecial() {
		return special;
	}
	public Keyword getSpecial(int index) {
		return special.get(index);
	}
}
