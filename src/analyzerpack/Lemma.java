package analyzerpack;

// ���� Ŭ����
public class Lemma {
	
	String text;	// ���� ����
	Pos pos;		// ���� ǰ��
	int repeatID;	// �ݺ��� lemma�� ID �ݺ��� ���� ������ 0, �ݺ��� ���� 1�̻�, 1�̶�� 1�� �ݺ� �̷��� �ƴ�!! �����ϼ���!
	
	// ������
	public Lemma(String Text, Pos Partos) {
		text = Text;
		pos = Partos;
		repeatID = 0;
	}
	
	// ���� ����
	public String getText() {
		return text;
	}
	
	// ǰ�� ����
	public Pos getPos() {
		return pos;
	}
	
	// repeatID ����
	public int getRepeatID() {
		return repeatID;
	}
	
	public void setRepeatID( int id ) {
		repeatID = id;
	}
}
