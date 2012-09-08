package analyzerpack;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

class AnswerPattern {
	public String name;
	public ArrayList<String> text;
	
	public AnswerPattern(String Name, ArrayList<String> Text) {
		name = Name;
		text = Text;
	}
	public void AddString(String Text) {
		text.add(Text);
	}
}

public class AnswerText {
	
	ArrayList<AnswerPattern> answerPattern = new ArrayList<AnswerPattern>();
	ArrayList<String> NAME = new ArrayList<String>();
	ArrayList<String> FIELD = new ArrayList<String>();
	ArrayList<String> VALUE = new ArrayList<String>();
	String FROM = "";
	String NUM = "";
	
	public void CreatAnswerPattern() {
		try {
			String file = "WebContent/Dictionary/answer.txt";
			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"));
		
			boolean first = true;
			String line = "";
			while (( line = br.readLine()) != null ) {
				if (first) {
					if( line.startsWith("\uFEFF")) {
						line = line.substring(1);
					}
				}
				first = false;
				
				// ���� ���� �߰�..
				if( line.charAt(0) == '@' ) {
					String[] split = line.split("=");
					String patternName = split[0].substring(1);
					
					// �ʵ庰�� ������ �߰���.
					boolean original_flag = false;
					for( int i = 0 ; i < answerPattern.size() ; i++ ) {
						if( patternName.equals(answerPattern.get(i).name) ) {
							original_flag = true;
							answerPattern.get(i).AddString(split[1]);
						}
					}
					// ���� �����̸��� ������ ���ο� ���� �߰�..
					if( !original_flag ) {
						ArrayList<String> tList = new ArrayList<String>();
						tList.add(split[1]);
						AnswerPattern newAP = new AnswerPattern(patternName, tList);
						answerPattern.add(newAP);
					}
				}
			}
			
		} catch(Exception e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
	}
	
	// ������ ��ȭ�� ���� �ؽ�Ʈ�� ��ȯ..
	public String AnswerFromQuery(String order, String query, ArrayList<String> value) {
		String Answer = "";
		FROM = "";
		NUM = "";
		if( value != null ) {
			VALUE = value;
			NUM = Integer.toString(VALUE.size());
		}
		
		if( query.charAt(0) == '@' ) {
			Answer = RandomPattern(query.substring(1, query.length()));
		}
		// ���� ������ ���
		else {
			// �ʵ尪 ����
			if( query.indexOf("FROM") >= 0 ) {
				String fromsplit[] = query.split("FROM");
			
				// FROM�� ���� �������� ���̺� �̸� Ȯ��
				if( fromsplit.length > 2 ) {
					if( fromsplit[1].indexOf("���ڸ�") >= 0 ) FROM = "C";
					else {
						if( fromsplit[1].indexOf("��") >= 0 ) FROM = "S";
					}
				}
			
				// SELECT�� FROM ����
				String split[] = fromsplit[0].split("SELECT");
				if( split.length > 1 ) {
					split[1] = split[1].trim();
					FIELD.add(split[1]);
				}
		
				// ���� �̸��� ���
				if( FIELD.get(0).equals("�̸�") ) {
					Answer = RandomPattern("���߰˻�");
					for( int i = 0 ; i < VALUE.size() ; i++ ) {
						Answer += ("@" + FROM + ":" + VALUE.get(i));
					}
				}
		
				// ���� ���� ���
				else {
					// NAME ����
					String[] querySplit = query.split("WHERE �̸�='");
					if( querySplit.length > 1 ) {
						String[] nameSplit = querySplit[1].split("'");
						NAME.add(nameSplit[0]);
					}
				
					// �ʵ����� ������ ����
					Answer = RandomPattern("�ʵ�����");
				
					// ���� -12345�� �� ������..
					if( value.size() > 0 ) {
						if( value.get(0).equals("-12345") ) {
							Answer = RandomPattern("�ʵ�����");
						}
					}
				
					VALUE = value;
				}
			}
			else {
				Answer = RandomPattern("�����");
			}
		}
		
		// �±� ����
		int chk = 0;
		String tag = "";
		boolean extract = false;
		while ( chk <= Answer.length() - 3 ) {
			// >�� ���ö����� ����
			if( extract ) {
				if( Answer.charAt(chk) == '>' ) {
					String original = "<" + tag + ">";
					String replace = ReplaceTag(tag);
					Answer = Answer.replaceFirst(original, replace);
					tag = "";
					extract = false;
					chk += (replace.length() - original.length());
				}
				else {
					tag += Answer.charAt(chk);
					chk++;
				}
			}
			// �±״� <�� ������
			else {
				if( Answer.charAt(chk) == '<' ) {
					extract = true;
				}
				chk++;
			}
		}
		
		return Answer;
	}
	
	// �������� ���� ���� �߿��� �������� �̾ƿ�
	String RandomPattern(String field) {
		String Text = "";
		for( int i = 0 ; i < answerPattern.size() ; i++ ) {
			if( answerPattern.get(i).name.equals(field) ) {
				int randomPattern = (int)(Math.random() * answerPattern.get(i).text.size());
				Text = answerPattern.get(i).text.get(randomPattern);
			}
		}
		return Text;
	}
	
	// �±׸� ���� ������ ����
	String ReplaceTag(String tag) {
		String value = "";
		String[] split = tag.split(":");
		
		// ":"�� �������� �������� �±��� ��쿡�� ���� 
		if( split.length >= 2 ) {
			if( split[0].equals("FIELD") ) value = FIELD.get(Integer.parseInt(split[1]));
			if( split[0].equals("NAME") ) value = NAME.get(Integer.parseInt(split[1]));
			if( split[0].equals("VALUE") ) value = VALUE.get(Integer.parseInt(split[1]));
			
			// ���� ����
			if( split.length >= 3 ) {
				value = GetJosa(value, split[2].charAt(0));
			}
		}
		else {
			if( tag.equals("NUM") ) value = NUM;
		}
		
		return value;
	}
	
	// ��ħ�� �´� ���縦 ����
	String GetJosa(String text, char type) {
		int code = text.codePointAt(text.length() - 1) - 44032;

		// ���� ������ �������� �� ���ڿ� ��ȯ
		if( text.length() == 0 ) return "";

		// �ѱ��� �ƴҶ�
		if( code < 0 || code > 11171) return text;

		if( code % 28 == 0 ) return text + Josa(type, false);
		else return text + Josa(type, true);
	}
	char Josa(char josa, boolean jong) {
		// jong : true�� ��ħ����, false�� ��ħ����
		if (josa == '��' || josa == '��') return (jong?'��':'��');
		if (josa == '��' || josa == '��') return (jong?'��':'��');
		if (josa == '��' || josa == '��') return (jong?'��':'��');
		if (josa == '��' || josa == '��') return (jong?'��':'��');
		// �� �� ���� ����
		return ' ';
	}
}
