package analyzerpack;
import java.util.ArrayList;

public class Sentence {
	
	ArrayList<Word> word;			// ���� ����Ʈ
	ArrayList<Keyword> keyword;		// ����� Ű���� ����Ʈ
	ArrayList<Keyword> numberList;	// ����� ���� ����Ʈ
	String text;					// ����ڰ� �Է��� ����
	int numKeyword;					// Ű���� ��ȣ
	int numNumberList;				// ���ڸ���Ʈ ��ȣ
	
	// ������
	public Sentence(String text) {
		text = this.text;
		word = new ArrayList<Word>();
		keyword = new ArrayList<Keyword>();
		numberList = new ArrayList<Keyword>();
		numKeyword = 0;
		numNumberList = 0;
	}
	
	// Ű���� ����
	public void SetKeyword(ArrayList<Keyword> k) {
		keyword = k;
	}
	
	// ���ڸ���Ʈ ����
	public void SetNumberList(ArrayList<Keyword> k) {
		numberList = k;
	}
	
	// ����Ʈ�� ���� �߰� (����ڰ� �Է��� ����, �м��� ���� ��� ����)
	public void addWord(String nativeText, String analysedText, int NumKeyword, int NumNumberList, int NumofIgnore, Repeat NumofRepeat ) {
		Word newWord = new Word(nativeText, analysedText, keyword, NumKeyword, numberList, NumNumberList, NumofIgnore, NumofRepeat);
		numKeyword = newWord.getKeyword();
		numNumberList = newWord.getNumberList();
		word.add(newWord);
	}
	
	// ���� ��ü ����
	public String getText() {
		return text;
	}
	
	// ���� ��ü ����
	public ArrayList<Word> getWord() {
		return word;
	}
	 
	// ���� ����. �ϳ���
	public Word getWord(int count) {
		return word.get(count);
	}
	
	// Ű���� ��ȣ ����.
	public int GetKeyword() {
		return numKeyword;
	}
	
	// ���ڸ���Ʈ ��ȣ ����.
	public int GetNumberList() {
		return numNumberList;
	}
	
	public void setRepeatID( ArrayList<Integer> repeatIDs )	{
		int index = 0;
		for( int wordCnt = 0; wordCnt < getWord().size(); wordCnt++ )
		{
			Word word = getWord(wordCnt);
			for( int lemmaCnt = 0; lemmaCnt < word.getLemma().size(); lemmaCnt++ )
			{
				Lemma lemma = word.getLemma(lemmaCnt);
				lemma.setRepeatID(repeatIDs.get(index++));
			}
		}
	}
}
