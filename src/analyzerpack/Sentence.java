package analyzerpack;
import java.util.ArrayList;

public class Sentence {
	
	ArrayList<Word> word;			// 어절 리스트
	ArrayList<Keyword> keyword;		// 추출된 키워드 리스트
	ArrayList<Keyword> numberList;	// 추출된 숫자 리스트
	ArrayList<Keyword> special;		// 추출된 특수문자 리스트
	String text;					// 사용자가 입력한 내용
	int numKeyword;					// 키워드 번호
	int numNumberList;				// 숫자리스트 번호
	int numSpecial;					// 특수문자리스트 번호
	
	// 생성자
	public Sentence(String text) {
		text = this.text;
		word = new ArrayList<Word>();
		keyword = new ArrayList<Keyword>();
		numberList = new ArrayList<Keyword>();
		special = new ArrayList<Keyword>();
		numKeyword = 0;
		numNumberList = 0;
		numSpecial = 0;
	}
	
	// 청킹 리스트 세팅
	public void SetChunk(ArrayList<Keyword> Keyword, ArrayList<Keyword> NumberList, ArrayList<Keyword> Special) {
		keyword = Keyword;
		numberList = NumberList;
		special = Special;
	}
	
	// 리스트에 어절 추가 (사용자가 입력한 내용, 분석된 내용 모두 받음)
	public void addWord(String nativeText, String analysedText, int NumKeyword, int NumNumberList, int NumSpecial, int NumofIgnore, Repeat NumofRepeat ) {
		Word newWord = new Word(nativeText, analysedText, keyword, NumKeyword, numberList, NumNumberList, special, NumSpecial, NumofIgnore, NumofRepeat);
		numKeyword = newWord.getKeyword();
		numNumberList = newWord.getNumberList();
		numSpecial = newWord.getSpeical();
		word.add(newWord);
	}
	
	// 문장 전체 리턴
	public String getText() {
		return text;
	}
	
	// 어절 전체 리턴
	public ArrayList<Word> getWord() {
		return word;
	}
	 
	// 어절 리턴. 하나만
	public Word getWord(int count) {
		return word.get(count);
	}
	
	// 키워드 번호 리턴.
	public int GetKeyword() {
		return numKeyword;
	}
	
	// 숫자리스트 번호 리턴.
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
