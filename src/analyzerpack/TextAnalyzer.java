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
			
		if( !"".equals(order) ) {
			log.info(order);
			
			MorphAnalyzer analyzer = null;
			KoreanTokenizer tokenizer = null;
			Token token = null;
			
			try {
				// 형태소분석기 클래스 생성
				analyzer = new MorphAnalyzer();
				tokenizer = new KoreanTokenizer(new StringReader(order));
			} catch(Exception e) {
				order = "NoResult";
			}
				
			// 문장 클래스 생성
			Sentence sentence = new Sentence(order);
			sentence.SetChunk(keyword, numberList, special);
			
			try {
				// '난 학생이다.'를 입력하면 token에서
				// '난' / '학생이다'로 나뉜다.
				while( ( token = tokenizer.next() ) != null ) {	
					// 1. 한글과 영어, 숫자를 나눈다. 즉 토큰을 새로 짠다.
					// 영어, 숫자는 "7글자"부터 에러
					// 한글도 "8글자"부터는 에러남.
					// 특수문자는 무시
					
					// token.termText()를 하면 다음 토큰을 출력.
					String tokenText = token.termText();
					//analyzer.setExactCompound(false);
						
					// 각각의 토큰에 대한 results 리스트.
					List<AnalysisOutput> results = analyzer.analyze(tokenText);
						
					// 문장에 어절 추가.
					sentence.addWord(tokenText, results.get(0).toString(), numKeyword, numNumberList, numSpecial, 0, Repeat.NOMORE);
					numKeyword = sentence.GetKeyword();
					numNumberList = sentence.GetNumberList();
				}
			} catch (Exception e) {		
				printOut = "NoResult";
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
		return printOut;
	}
	
	// 쿼리 리턴
	public String getQuery() {
		return query;
	}
}
