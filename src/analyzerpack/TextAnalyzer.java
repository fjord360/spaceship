package analyzerpack;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.analysis.kr.morph.MorphAnalyzer;
import org.apache.lucene.analysis.kr.KoreanTokenizer;
import org.apache.lucene.analysis.Token;
import org.apache.lucene.analysis.kr.morph.AnalysisOutput;
import java.util.List;
import java.util.ArrayList;
import java.io.StringReader;

public class TextAnalyzer {
	
	ArrayList<Keyword> keyword;
	ArrayList<Keyword> numberList;
	ArrayList<Keyword> special;
	Log log;
	int numKeyword;
	int numNumberList;
	int numSpecial;
	String query;

	public TextAnalyzer() {
		keyword = new ArrayList<Keyword>();
		numberList = new ArrayList<Keyword>();
		special = new ArrayList<Keyword>();
		log = LogFactory.getLog("org.apache.lucene.analysis.kr");
	}
	
	// 청킹 리스트 세팅
	public void SetChunk(ArrayList<Keyword> Keyword, ArrayList<Keyword> NumberList, ArrayList<Keyword> Special) {
		keyword = Keyword;
		numberList = NumberList;
		special = Special;
	}
	
	// 형태소 분석한 결과의 HTML 코드를 String으로 리턴한다.
	public String Anaylze(String order) {
		String printOut = "";
		try {
			
			if( !"".equals(order) ) {
				log.info(order);
				
				// 형태소분석기 클래스 생성
				MorphAnalyzer analyzer = new MorphAnalyzer();
				KoreanTokenizer tokenizer = new KoreanTokenizer(new StringReader(order));
				Token token = null;
				
				// 문장 클래스 생성
				Sentence sentence = new Sentence(order);
				sentence.SetChunk(keyword, numberList, special);
				
				// '난 학생이다.'를 입력하면 token에서
				// '난' / '학생이다'로 나뉜다.
				while( ( token = tokenizer.next() ) != null ) {
					
					// 1. 한글과 영어, 숫자를 나눈다. 즉 토큰을 새로 짠다.
					// 영어, 숫자는 "7글자"부터 에러
					// 한글도 "8글자"부터는 에러남.
					// 특수문자는 무시
					
					try {
						// token.termText()를 하면 다음 토큰을 출력.
						String tokenText = token.termText();
						//analyzer.setExactCompound(false);
						
						// 각각의 토큰에 대한 results 리스트.
						List<AnalysisOutput> results = analyzer.analyze(tokenText);
						
						// 문장에 어절 추가.
						sentence.addWord(tokenText, results.get(0).toString(), numKeyword, numNumberList, numSpecial, 0, Repeat.NOMORE);
						numKeyword = sentence.GetKeyword();
						numNumberList = sentence.GetNumberList();
						
						// 각각의 results에 대한 결과값 출력.
						//for( AnalysisOutput o : results ) {
							
							/*html += "<div class='inner' style='font-size:10pt; color:blue'>";
							html += o.toString();
							html += "->";
							for( int i = 0 ; i < o.getCNounList().size() ; i++ ) {
								html += o.getCNounList().get(i).getWord();
								html += "/";
							}
							html += "<" + o.getScore() + ">";
							html += "</div>";
						
							html += "<div class='inner' style='font-size:10pt;'>getDinf : " + o.getDinf();
						
							// 어미
							html += "<br>getEomi (어미) : " + o.getEomi();
						
							// 어미. getEomi().charAt(0) 하면 어미의 첫음절 음절정보
							// 어미로 끝나는 경우 분석
							//if(eomiFlag) {
								//analysisWithEomi(stem,eomi,candidates);
							//}
						
							// 조사
							// 조사로 끝나는 경우 분석
							//if(josaFlag&&feature[SyllableUtil.IDX_JOSA1]=='1') {
	      						//analysisWithJosa(stem,eomi,candidates);
							//}
							html += "<br>getJosa (조사) : " + o.getJosa();
							
							// 패턴. PatternConstant
							// 체언 : 조사의 도움을 받아 문장에서 주체의 구실을 하는 단어. 명사, 대명사, 수사
							// PTN_N:  체언 (사랑) 1
							// PTN_NJ:  체언 + 조사 (사랑은) 2
							// PTN_NSM: 체언 + 용언화접미사 + 어미 (사랑받고) 3
							// PTN_NSMJ:  체언 + 용언화접미사 + 명사화접미사('음/기') + 조사 (사랑받기가) 4
							// PTN_NSMXM:  체언 + 용언화접미사 + '아/어' + 보조용언 + 어미 (사랑받아보다) 5
							// PTN_NJCM:  체언 + '에서/부터/에서부터' + '이' + 어미 (처음부터이다)
							
							// 용언 : 독립된 뜻을 가지고 어미를 활용하여 문장성분으로서 서술어의 기능을 하는 말.
							//          서술어의 기능을 하는 곳. 동사, 형용사가 있음
							// PTN_VM : 용언 + 어미 (돕다) 11
							// PTN_VMJ:  용언 + 명사화접미사('음/기') + 조사 (돕기가) 12
							// PTN_VMCM:  용언 + 명사화접미사('음/기') + '이' + 어미 (도움이다)
							// PTN_VMXM:  용언 + '아/어' + 보조용언 + 어미 (도와주다)
							// PTN_VMXMJ:  용언 + '아/어' + 보조용언 + 명사화접미사('음/기') + 조사 (도와주기가)
							// PTN_AID:  단일어 : 부사, 관형사, 감탄사 (그러나) 21
							// PTN_ADVJ:  부사 + 조사  (빨리도)
							String a = "";
							if( o.getPatn() == 1 ) a = "PTN_N 체언";
							if( o.getPatn() == 2 ) a = "PTN_NJ 체언 + 조사";
							if( o.getPatn() == 3 ) a = "PTN_NSM 체언 + 용언화접미사 + 어미";
							if( o.getPatn() == 4 ) a = "PTN_NSMJ 체언 + 용언화접미사 + 명사화접미사('음/기') + 조사";
							if( o.getPatn() == 5 ) a = "PTN_NSMXM 체언 + 용언화접미사 + '아/어' + 보조용언 + 어미";
							if( o.getPatn() == 6 ) a = "PTN_NJCM 체언 + '에서/부터/에서부터' + '이' + 어미";
							if( o.getPatn() == 11 ) a = "PTN_VM 용언 + 어미";
							if( o.getPatn() == 12 ) a = "PTN_VMJ 용언 + 명사화접미사('음/기') + 조사";
							if( o.getPatn() == 13 ) a = "PTN_VMCM 용언 + 명사화접미사('음/기') + '이' + 어미";
							if( o.getPatn() == 14 ) a = "PTN_VMXM 용언 + '아/어' + 보조용언 + 어미";
							if( o.getPatn() == 15 ) a = "PTN_VMXMJ 용언 + '아/어' + 보조용언 + 명사화접미사('음/기') + 조사";
							if( o.getPatn() == 21 ) a = "PTN_AID 단일어 : 부사, 관형사, 감탄사";
							if( o.getPatn() == 22 ) a = "PTN_ADVJ 부사 + 조사";
							html += "<br>getPatn (패턴) : " + a + " (" + o.getPatn() + ")";
							
							// ~들
							html += "<br>getNsfx (~들) : " + o.getNsfx();
							
							// 
							html += "<br>getPomi : " + o.getPomi();
							
							//
							html += "<br>getPos : " + o.getPos();
							
							//
							html += "<br>getPos2 : " + o.getPos2();
							
							// 점수
							html += "<br>getScore (점수) : " + o.getScore();
							
							// 
							html += "<br>getSource : " + o.getSource();
							
							// 어근
							html += "<br>getStem (어근) : " + o.getStem();
							
							// 
							html += "<br>getType : " + o.getType();
							
							// 용언화접미사
							html += "<br>getVtype (용언화접미사) : " + o.getVtype();
							
							// 보조용언
							html += "<br>getXverb (보조용언) : " + o.getXverb();
							
							// 명사 리스트
							html += "<br>CNounList (명사리스트) .Size = " + o.getCNounList().size();
							for( int i = 0 ; i < o.getCNounList().size() ; i++ ) {
								html += "<br>getCNounList-" + i + " : " + o.getCNounList().get(i).getWord();
							}
							
							// 명사화접미사('음/기')
							// 보조적연결어미('아/어')
							html += "<br>EList (명사화접미사(음/기), 보조적연결어미(아/어).Size = " + o.getElist().size();
							for( int i = 0 ; i < o.getElist().size() ; i++ ) {
								html += "<br>getElist-" + i + " : " + o.getElist().get(i);
							}
							
							// 
							html += "<br>JList.Size = " + o.getJlist().size();
							for( int i = 0 ; i < o.getJlist().size() ; i++ ) {
								html += "<br>getJlist-" + i + " : " + o.getJlist().get(i);
							}
							html += "</div>";*/
						//}
					} catch (Exception e) {					
						System.out.println(e.getMessage());					
						e.printStackTrace();
					}
				}

				// 출력..
				for( int i = 0 ; i < sentence.getWord().size() ; i++ ) {
					for( int j = 0 ; j < sentence.getWord(i).getLemma().size() ; j++ ) {
						printOut += sentence.getWord(i).getLemma(j).getText() + "/" + sentence.getWord(i).getLemma(j).getPos();
						if( j != sentence.getWord(i).getLemma().size() - 1 ) printOut += "+";
					}
					if( i != sentence.getWord().size()-1 ) printOut += "_";
				}
				
				// 의미분석 결과 (정호) -> 쿼리로 리턴됨.
				ResultAnalyzer ra = new ResultAnalyzer();
				query = ra.ExAnalyze_Frame(sentence);
				//System.out.println("query : " + query);
			}
		} catch(Exception e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
		return printOut;
	}
	
	// 쿼리 리턴
	public String getQuery() {
		return query;
	}
}
