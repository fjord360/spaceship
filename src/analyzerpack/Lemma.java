package analyzerpack;

// 렘마 클래스
public class Lemma {
	
	String text;	// 렘마 내용
	Pos pos;		// 렘마 품사
	int repeatID;	// 반복될 lemma의 ID 반복이 되지 않으면 0, 반복될 때는 1이상, 1이라고 1번 반보 이런거 아님!! 주의하세요!
	
	// 생성자
	public Lemma(String Text, Pos Partos) {
		text = Text;
		pos = Partos;
		repeatID = 0;
	}
	
	// 내용 리턴
	public String getText() {
		return text;
	}
	
	// 품사 리턴
	public Pos getPos() {
		return pos;
	}
	
	// repeatID 리턴
	public int getRepeatID() {
		return repeatID;
	}
	
	public void setRepeatID( int id ) {
		repeatID = id;
	}
}
