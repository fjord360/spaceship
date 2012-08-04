package analyzerpack;

import java.util.ArrayList;
import java.util.StringTokenizer;

// 어절 클래스
public class Word {
	
	ArrayList<Lemma> lemma;		// 렘마 리스트
	String nativeText;			// 사용자가 입력한 어절
	String analysedText;		// 분석된 어절
	int numKeyword;				// 키워드 번호
	
	// 생성
	public Word(String NativeText, String AnalysedText, ArrayList<Keyword> keyword, int NumKeyword) {
		nativeText = NativeText;
		analysedText = AnalysedText;
		lemma = new ArrayList<Lemma>();
		numKeyword = NumKeyword;
		
		// 어절 클래스는 생성하면 곧바로 분석해서 렘마를 만든다.
		addLemma(analysedText, keyword);
	}
	
	// 렘마 추가
	public void addLemma(String text, Pos pos) {
		Lemma newLemma = new Lemma(text, pos);
		lemma.add(newLemma);
	}
	
	// 렘마 추가 (텍스트만 가지고 분석해서 추가)
	public void addLemma(String text, ArrayList<Keyword> keyword) {
		// 밝(V),은(e) 같은 형태로 들어오기 때문에 ","를 중심으로 토큰을 나눈다.
		// 그러면 "밝", "(V)", "은", "(e)"의 형태가 된다.
		// "쏭", "(N)"
		StringTokenizer st = new StringTokenizer(text, ",");
				
		// 렘마는 렘마 텍스트에, 품사정보는 "()괄호" 제거해서 렘마 품사정보에 넣는다.
		while( st.hasMoreTokens() ) {
			String next = st.nextToken();
			String lemmaText = "";
			Pos lemmaPos = null;
			
			if( next.length() > 2 )
			{
				for( int i = 0 ; i < next.length() - 2 ; i++ ) {
					// "(x)"의 형태만 품사정보로 인식.
					if( next.charAt(i) == '(' && next.charAt(i+2) == ')' ) {
						if( next.charAt(i+1) == 'V' ) lemmaPos = Pos.V;
						if( next.charAt(i+1) == 'N' ) lemmaPos = Pos.N;
						if( next.charAt(i+1) == 't' ) lemmaPos = Pos.t;
						if( next.charAt(i+1) == 'e' ) lemmaPos = Pos.e;
						if( next.charAt(i+1) == 'j' ) lemmaPos = Pos.j;
						if( next.charAt(i+1) == 'f' ) lemmaPos = Pos.f;
						if( next.charAt(i+1) == 'n' ) lemmaPos = Pos.n;
						if( next.charAt(i+1) == 'c' ) lemmaPos = Pos.c;
						if( next.charAt(i+1) == 'W' ) lemmaPos = Pos.W;
						if( next.charAt(i+1) == 'Z' ) lemmaPos = Pos.Z;
						if( next.charAt(i+1) == 'X' ) lemmaPos = Pos.X;
						if( next.charAt(i+1) == 'C' ) lemmaPos = Pos.C;
						if( next.charAt(i+1) == 'S' ) lemmaPos = Pos.S;

						// 나머진 내용
						lemmaText = next.substring(0, i);

						// 단, 쑹과 쏭에 대해서는 키워드 처리
						if( next.charAt(i+1) == 'N' && numKeyword < keyword.size() ) {
							if( next.substring(0, i).equals("쑹") ||
								next.substring(0, i).equals("쏭") ) {
								lemmaPos = keyword.get(numKeyword).pos;
								lemmaText = keyword.get(numKeyword).text;
								numKeyword++;
							}
						}
					}
					// 품사정보가 없으면 전부 내용으로 하고 품사는 명사로.
					else {
						lemmaText = next;
						lemmaPos = Pos.N;
					}
				}
			}
			else {
				lemmaText = next;
				lemmaPos = Pos.N;
			}
			
			// 렘마 리스트에 넣는다.
			Lemma newLemma = new Lemma(lemmaText, lemmaPos);
			lemma.add(newLemma);
		}
	}
	
	// 렘마 전체를 리턴
	public ArrayList<Lemma> getLemma() {
		return lemma;
	}
	
	// 렘마 리턴. 하나만
	public Lemma getLemma(int count) {
		return lemma.get(count);
	}
	
	// 사용자가 입력한 어절 리턴
	public String getNativeText() {
		return nativeText;
	}
	
	// 분석된 어절 리턴
	public String getAnalysedText() {
		return analysedText;
	}
	
	// 키워드번호 리턴
	public int getKeyword() {
		return numKeyword;
	}
}
