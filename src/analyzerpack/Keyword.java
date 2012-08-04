package analyzerpack;

public class Keyword {
	public String text = "";
	public Pos pos;
	public int index = 0;
	public Keyword(String Text, Pos POS, int Index) {
		text = Text;
		pos = POS;
		index = Index;
	}
}
