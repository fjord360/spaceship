package analyzerpack;

public class Keyword {
	public String text = "";
	public Pos pos;
	public int index = 0;
	public int originalLength = 0;
	public Keyword(String Text, Pos POS, int Index, int OriginalLength) {
		text = Text;
		pos = POS;
		index = Index;
		originalLength = OriginalLength;
	}
}
