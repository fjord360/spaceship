package analyzerpack;

// 렘마 클래스
public class Lemma {
	
	String text;	// 렘마 내용
	Pos pos;		// 렘마 품사
	
	// 생성자
	public Lemma(String Text, Pos Partos) {
		text = Text;
		pos = Partos;
	}
	
	// 내용 리턴
	public String getText() {
		return text;
	}
	
	// 품사 리턴
	public Pos getPos() {
		return pos;
	}
}
