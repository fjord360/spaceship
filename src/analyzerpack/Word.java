package analyzerpack;

import java.util.ArrayList;
import java.util.StringTokenizer;

// ���� Ŭ����
public class Word {
	
	ArrayList<Lemma> lemma;		// ���� ����Ʈ
	String nativeText;			// ����ڰ� �Է��� ����
	String analysedText;		// �м��� ����
	int numKeyword;				// Ű���� ��ȣ
	
	// ����
	public Word(String NativeText, String AnalysedText, ArrayList<Keyword> keyword, int NumKeyword) {
		nativeText = NativeText;
		analysedText = AnalysedText;
		lemma = new ArrayList<Lemma>();
		numKeyword = NumKeyword;
		
		// ���� Ŭ������ �����ϸ� ��ٷ� �м��ؼ� ������ �����.
		addLemma(analysedText, keyword);
	}
	
	// ���� �߰�
	public void addLemma(String text, Pos pos) {
		Lemma newLemma = new Lemma(text, pos);
		lemma.add(newLemma);
	}
	
	// ���� �߰� (�ؽ�Ʈ�� ������ �м��ؼ� �߰�)
	public void addLemma(String text, ArrayList<Keyword> keyword) {
		// ��(V),��(e) ���� ���·� ������ ������ ","�� �߽����� ��ū�� ������.
		// �׷��� "��", "(V)", "��", "(e)"�� ���°� �ȴ�.
		StringTokenizer st = new StringTokenizer(text, ",");
				
		// ������ ���� �ؽ�Ʈ��, ǰ�������� "()��ȣ" �����ؼ� ���� ǰ�������� �ִ´�.
		while( st.hasMoreTokens() ) {
			String next = st.nextToken();
			String lemmaText = "";
			Pos lemmaPos = null;
			
			if( next.length() > 2 )
			{
				for( int i = 0 ; i < next.length() - 2 ; i++ ) {
					// "(x)"�� ���¸� ǰ�������� �ν�.
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
						if( next.charAt(i+1) == 'I' ) lemmaPos = Pos.I;

						// ������ ����
						lemmaText = next.substring(0, i);

						// ��, ��ü���ڿ� ���ؼ��� Ű���� ó��
						if( next.charAt(i+1) == 'N' && numKeyword < keyword.size() ) {
							if( next.substring(0, i).equals("��") ||
								next.substring(0, i).equals("��") ||
								next.substring(0, i).equals("��") ) {
								lemmaPos = keyword.get(numKeyword).pos;
								lemmaText = keyword.get(numKeyword).text;
								numKeyword++;
							}
						}
					}
					// ǰ�������� ������ ���� �������� �ϰ� ǰ��� ����.
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
			
			// ���� ����Ʈ�� �ִ´�.
			Lemma newLemma = new Lemma(lemmaText, lemmaPos);
			lemma.add(newLemma);
		}
	}
	
	// ���� ��ü�� ����
	public ArrayList<Lemma> getLemma() {
		return lemma;
	}
	
	// ���� ����. �ϳ���
	public Lemma getLemma(int count) {
		return lemma.get(count);
	}
	
	// ����ڰ� �Է��� ���� ����
	public String getNativeText() {
		return nativeText;
	}
	
	// �м��� ���� ����
	public String getAnalysedText() {
		return analysedText;
	}
	
	// Ű�����ȣ ����
	public int getKeyword() {
		return numKeyword;
	}
}
