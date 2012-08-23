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
		try {
			
			if( !"".equals(order) ) {
				log.info(order);
				
				// ���¼Һм��� Ŭ���� ����
				MorphAnalyzer analyzer = new MorphAnalyzer();
				KoreanTokenizer tokenizer = new KoreanTokenizer(new StringReader(order));
				Token token = null;
				
				// ���� Ŭ���� ����
				Sentence sentence = new Sentence(order);
				sentence.SetChunk(keyword, numberList, special);
				
				// '�� �л��̴�.'�� �Է��ϸ� token����
				// '��' / '�л��̴�'�� ������.
				while( ( token = tokenizer.next() ) != null ) {
					
					// 1. �ѱ۰� ����, ���ڸ� ������. �� ��ū�� ���� §��.
					// ����, ���ڴ� "7����"���� ����
					// �ѱ۵� "8����"���ʹ� ������.
					// Ư�����ڴ� ����
					
					try {
						// token.termText()�� �ϸ� ���� ��ū�� ���.
						String tokenText = token.termText();
						//analyzer.setExactCompound(false);
						
						// ������ ��ū�� ���� results ����Ʈ.
						List<AnalysisOutput> results = analyzer.analyze(tokenText);
						
						// ���忡 ���� �߰�.
						sentence.addWord(tokenText, results.get(0).toString(), numKeyword, numNumberList, numSpecial, 0, Repeat.NOMORE);
						numKeyword = sentence.GetKeyword();
						numNumberList = sentence.GetNumberList();
						
						// ������ results�� ���� ����� ���.
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
						
							// ���
							html += "<br>getEomi (���) : " + o.getEomi();
						
							// ���. getEomi().charAt(0) �ϸ� ����� ù���� ��������
							// ��̷� ������ ��� �м�
							//if(eomiFlag) {
								//analysisWithEomi(stem,eomi,candidates);
							//}
						
							// ����
							// ����� ������ ��� �м�
							//if(josaFlag&&feature[SyllableUtil.IDX_JOSA1]=='1') {
	      						//analysisWithJosa(stem,eomi,candidates);
							//}
							html += "<br>getJosa (����) : " + o.getJosa();
							
							// ����. PatternConstant
							// ü�� : ������ ������ �޾� ���忡�� ��ü�� ������ �ϴ� �ܾ�. ���, ����, ����
							// PTN_N:  ü�� (���) 1
							// PTN_NJ:  ü�� + ���� (�����) 2
							// PTN_NSM: ü�� + ���ȭ���̻� + ��� (����ް�) 3
							// PTN_NSMJ:  ü�� + ���ȭ���̻� + ���ȭ���̻�('��/��') + ���� (����ޱⰡ) 4
							// PTN_NSMXM:  ü�� + ���ȭ���̻� + '��/��' + ������� + ��� (����޾ƺ���) 5
							// PTN_NJCM:  ü�� + '����/����/��������' + '��' + ��� (ó�������̴�)
							
							// ��� : ������ ���� ������ ��̸� Ȱ���Ͽ� ���强�����μ� �������� ����� �ϴ� ��.
							//          �������� ����� �ϴ� ��. ����, ����簡 ����
							// PTN_VM : ��� + ��� (����) 11
							// PTN_VMJ:  ��� + ���ȭ���̻�('��/��') + ���� (���Ⱑ) 12
							// PTN_VMCM:  ��� + ���ȭ���̻�('��/��') + '��' + ��� (�����̴�)
							// PTN_VMXM:  ��� + '��/��' + ������� + ��� (�����ִ�)
							// PTN_VMXMJ:  ��� + '��/��' + ������� + ���ȭ���̻�('��/��') + ���� (�����ֱⰡ)
							// PTN_AID:  ���Ͼ� : �λ�, ������, ��ź�� (�׷���) 21
							// PTN_ADVJ:  �λ� + ����  (������)
							String a = "";
							if( o.getPatn() == 1 ) a = "PTN_N ü��";
							if( o.getPatn() == 2 ) a = "PTN_NJ ü�� + ����";
							if( o.getPatn() == 3 ) a = "PTN_NSM ü�� + ���ȭ���̻� + ���";
							if( o.getPatn() == 4 ) a = "PTN_NSMJ ü�� + ���ȭ���̻� + ���ȭ���̻�('��/��') + ����";
							if( o.getPatn() == 5 ) a = "PTN_NSMXM ü�� + ���ȭ���̻� + '��/��' + ������� + ���";
							if( o.getPatn() == 6 ) a = "PTN_NJCM ü�� + '����/����/��������' + '��' + ���";
							if( o.getPatn() == 11 ) a = "PTN_VM ��� + ���";
							if( o.getPatn() == 12 ) a = "PTN_VMJ ��� + ���ȭ���̻�('��/��') + ����";
							if( o.getPatn() == 13 ) a = "PTN_VMCM ��� + ���ȭ���̻�('��/��') + '��' + ���";
							if( o.getPatn() == 14 ) a = "PTN_VMXM ��� + '��/��' + ������� + ���";
							if( o.getPatn() == 15 ) a = "PTN_VMXMJ ��� + '��/��' + ������� + ���ȭ���̻�('��/��') + ����";
							if( o.getPatn() == 21 ) a = "PTN_AID ���Ͼ� : �λ�, ������, ��ź��";
							if( o.getPatn() == 22 ) a = "PTN_ADVJ �λ� + ����";
							html += "<br>getPatn (����) : " + a + " (" + o.getPatn() + ")";
							
							// ~��
							html += "<br>getNsfx (~��) : " + o.getNsfx();
							
							// 
							html += "<br>getPomi : " + o.getPomi();
							
							//
							html += "<br>getPos : " + o.getPos();
							
							//
							html += "<br>getPos2 : " + o.getPos2();
							
							// ����
							html += "<br>getScore (����) : " + o.getScore();
							
							// 
							html += "<br>getSource : " + o.getSource();
							
							// ���
							html += "<br>getStem (���) : " + o.getStem();
							
							// 
							html += "<br>getType : " + o.getType();
							
							// ���ȭ���̻�
							html += "<br>getVtype (���ȭ���̻�) : " + o.getVtype();
							
							// �������
							html += "<br>getXverb (�������) : " + o.getXverb();
							
							// ��� ����Ʈ
							html += "<br>CNounList (��縮��Ʈ) .Size = " + o.getCNounList().size();
							for( int i = 0 ; i < o.getCNounList().size() ; i++ ) {
								html += "<br>getCNounList-" + i + " : " + o.getCNounList().get(i).getWord();
							}
							
							// ���ȭ���̻�('��/��')
							// ������������('��/��')
							html += "<br>EList (���ȭ���̻�(��/��), ������������(��/��).Size = " + o.getElist().size();
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
		} catch(Exception e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
		return printOut;
	}
	
	// ���� ����
	public String getQuery() {
		return query;
	}
}
