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
	
	// ûŷ ����Ʈ ����
	public void SetChunk(ArrayList<Keyword> Keyword, ArrayList<Keyword> NumberList, ArrayList<Keyword> Special) {
		keyword = Keyword;
		numberList = NumberList;
		special = Special;
	}
	
	// ���¼� �м��� ����� HTML �ڵ带 String���� �����Ѵ�.
	public String Anaylze(String order) {
		String printOut = "";
			
		if( !"".equals(order) ) {
			log.info(order);
			
			MorphAnalyzer analyzer = null;
			KoreanTokenizer tokenizer = null;
			Token token = null;
			
			try {
				// ���¼Һм��� Ŭ���� ����
				analyzer = new MorphAnalyzer();
				tokenizer = new KoreanTokenizer(new StringReader(order));
			} catch(Exception e) {
				order = "NoResult";
			}
				
			// ���� Ŭ���� ����
			Sentence sentence = new Sentence(order);
			sentence.SetChunk(keyword, numberList, special);
			
			try {
				// '�� �л��̴�.'�� �Է��ϸ� token����
				// '��' / '�л��̴�'�� ������.
				while( ( token = tokenizer.next() ) != null ) {	
					// 1. �ѱ۰� ����, ���ڸ� ������. �� ��ū�� ���� §��.
					// ����, ���ڴ� "7����"���� ����
					// �ѱ۵� "8����"���ʹ� ������.
					// Ư�����ڴ� ����
					
					// token.termText()�� �ϸ� ���� ��ū�� ���.
					String tokenText = token.termText();
					//analyzer.setExactCompound(false);
						
					// ������ ��ū�� ���� results ����Ʈ.
					List<AnalysisOutput> results = analyzer.analyze(tokenText);
						
					// ���忡 ���� �߰�.
					sentence.addWord(tokenText, results.get(0).toString(), numKeyword, numNumberList, numSpecial, 0, Repeat.NOMORE);
					numKeyword = sentence.GetKeyword();
					numNumberList = sentence.GetNumberList();
				}
			} catch (Exception e) {		
				printOut = "NoResult";
			}

			// ���..
			for( int i = 0 ; i < sentence.getWord().size() ; i++ ) {
				for( int j = 0 ; j < sentence.getWord(i).getLemma().size() ; j++ ) {
					printOut += sentence.getWord(i).getLemma(j).getText() + "/" + sentence.getWord(i).getLemma(j).getPos();
					if( j != sentence.getWord(i).getLemma().size() - 1 ) printOut += "+";
				}
				if( i != sentence.getWord().size()-1 ) printOut += "_";
			}
				
			// �ǹ̺м� ��� (��ȣ) -> ������ ���ϵ�.
			ResultAnalyzer ra = new ResultAnalyzer();
			query = ra.ExAnalyze_Frame(sentence);
			//System.out.println("query : " + query);
		}
		return printOut;
	}
	
	// ���� ����
	public String getQuery() {
		return query;
	}
}
