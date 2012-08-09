package analyzerpack;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.Map;
import java.util.ArrayList;

class SpaceText {
	public String text = "";
	public int type = 0;
	public SpaceText(String Text, int Type) {
		text = Text;
		type = Type;
	}
}

class Lineup {
	public String text = "";
	public Pos pos;
	public float concordance = 0;
	public Lineup(String Text, Pos POS, float Concordance) {
		text = Text;
		pos = POS;
		concordance = Concordance;
	}
}

public class NameFinder {
	
	Map<String, String> star;
	Map<String, String> constellation;
	ArrayList<Keyword> keyword;
	
	// ������
	public NameFinder() {
		star = new HashMap<String, String>();
		constellation = new HashMap<String, String>();
		keyword = new ArrayList<Keyword>();
	}
	
	// �̸� ������ �ҷ��ͼ� �ؽ��ʿ� ����
	public void CreateMap() {
		
		try {
			String file = "WebContent/Dictionary/constellation_dic.txt";
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
			    constellation.put(line, "");
			}
			
		} catch(Exception e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
		
		try {
			String file = "WebContent/Dictionary/star_dic.txt";
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
			    star.put(line, "");
			}
			
		} catch(Exception e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
	}
	
	public String Find(String order) {
		
		// �ҹ��ڴ� ��� �빮�ڷ� ����
		order = order.toUpperCase();
		
		// ���� ������ �̸� �����صд�.
		ArrayList<SpaceText> adhere = new ArrayList<SpaceText>();
		String []split = order.split(" ");
		for( int i = 0 ; i < split.length ; i++ ) {
			for( int j = 0 ; j < split[i].length() ; j++ ) {
				String sub = split[i].substring(j, j+1);
				// �ڿ� ���Ⱑ ������ �ܾ��� type�� 1
				if( i < split.length && j == split[i].length() - 1 ) {
					adhere.add(new SpaceText(sub, 1));
				}
				// �ڿ� ���Ⱑ ������ �ʴ� �ܾ��� type�� 0
				else {
					adhere.add(new SpaceText(sub, 0));
				}
			}
		}
		for( int i = 0 ; i < adhere.size() ; i++ ) {
			System.out.println(adhere.get(i).text + "/" + adhere.get(i).type);
		}
		
		int start_index = 0;
				
		while( start_index < adhere.size() - 1 ) {
			// �� ���ھ� �˻�.
			String preOrder = adhere.get(start_index).text;
			System.out.println("�˻� : " + start_index + "(" + preOrder + ")");
			
			// ����� n��° ���ڷ� �����ϴ� �̸��� ������ �ִٸ� �˻� �ĺ��� �߰�..!
			// �� ��Ͽ� �ִ��� Ȯ��
			ArrayList<Lineup> lineup = new ArrayList<Lineup>();
			Set<String>set = star.keySet();
			Iterator<String> it = set.iterator();
			while( it.hasNext() ) {
				String prefix = it.next();
				
				if( prefix.substring(0, 1).equals(preOrder) ) {
					lineup.add(new Lineup(prefix, Pos.S, 0.0f));
				}
			}
			
			// ���ڸ� ��Ͽ� �ִ��� Ȯ���� ������ �߰�
			set = constellation.keySet();
			it = set.iterator();
			while( it.hasNext() ) {
				String prefix = it.next();
				
				if( prefix.substring(0, 1).equals(preOrder) ) {
					lineup.add(new Lineup(prefix, Pos.C, 0.0f));
				}
			}
			
			// �� ���ھ� ã�ƺ��鼭 ��ġ���� �����Ѵ�.
			for( int i = 0 ; i < lineup.size() ; i++ ) {
				float max_con = 0.0f;
				String comp = "";
				for( int j = start_index ; j < adhere.size() ; j++ ) {
					comp += adhere.get(j).text;
					float approx = Concordance(lineup.get(i).text, comp);
					if( approx > max_con ) max_con = approx;
				}
				lineup.get(i).concordance = max_con;
			}
			
			int choice = -1;
			int max_length = -1;
			for( int i = 0 ; i < lineup.size() ; i++ ) {
				// ��ġ���� 100%�� �ĺ��� ��÷. 100%�ΰ� 2�� �̻��̸� �� ��� Ű����� ��÷.
				if( lineup.get(i).concordance >= 1.0f ) {
					if( lineup.get(i).text.length() > max_length ) {
						choice = i;
						max_length = lineup.get(i).text.length();
					}
				}
				System.out.println("�ձ��� ���� �ĺ��� : " + lineup.get(i).text + "(" + lineup.get(i).concordance + ")");
			}
			
			// ��Ȯ���� 100%�� �ĺ��� Ű����� ����.
			if( lineup.size() > choice && choice >= 0 ) {
				System.out.println("* ��÷ : " + lineup.get(choice).text + "(" + lineup.get(choice).concordance + ")");
				keyword.add(new Keyword(lineup.get(choice).text, lineup.get(choice).pos, start_index));
				start_index += (lineup.get(choice).text.length() - 1);
			}
			start_index++;
		}
		
		int rep_index = 0;
		while( rep_index < adhere.size() ) {
			// ����ó�� : "��"�̶�� ���ڰ� ������ �� ���� ������ ����
			if( rep_index > 0 && adhere.get(rep_index).text.equals("��") ) {
				if( adhere.get(rep_index-1).type == 0 ) {
					adhere.get(rep_index-1).type = 1;
				}
			}
			
			// ����� Ű���� �κ��� ��ü��� �� �ٲ۴�.
			// �� : �� // ���ڸ� : ��
			for( int j = 0 ; j < keyword.size() ; j++ ) {
				
				if( rep_index == keyword.get(j).index ) {			
					// Ű���� ���� ������ ���⸦ �Ѵ�.
					if( rep_index > 0 && adhere.get(rep_index-1).type == 0 ) {
						adhere.get(rep_index-1).type = 1;
					}
					// Ű���带 ��ü���� �ٲٰ� Ű���� ������ ����� ��� ������. �������� ����.
					for( int k = 0 ; k < keyword.get(j).text.length() ; k++ ) {
						if( keyword.get(j).pos == Pos.S ) adhere.get(rep_index+k).text = "��";
						if( keyword.get(j).pos == Pos.C ) adhere.get(rep_index+k).text = "��";
						if( k != keyword.get(j).text.length() - 1 ) adhere.get(rep_index+k).type = 0;
					}
					rep_index += (keyword.get(j).text.length() - 1);
				}
			}
			rep_index++;
		}
		
		// ���ڸ� �����ؼ� ��� '��'���� �ٲ۴�.
		String addnumber = "";
		for( int i = 0 ; i < adhere.size() ; i++ ) {
			// ���ڸ� ĳġ.
			if( Character.getType(adhere.get(i).text.charAt(0)) == 9 ) {
				addnumber += adhere.get(i).text.charAt(0);
				adhere.get(i).text = "��";
				
				// ���� ���� ������ ����
				if( i != 0 ) {
					if( adhere.get(i-1).type == 0 ) adhere.get(i-1).type = 1;
				}
			}
			else {
				// ���ڰ� ������ �� �������� ��Ʈ���� ���� ����Ʈ�� �ִ´�.
				if( addnumber.length() > 0 ) {
					keyword.add(new Keyword(addnumber, Pos.I, i));
					addnumber = "";
					
					// ���� ������ ������ ����
					if( i != 0 ) {
						if( adhere.get(i-1).type == 0 ) adhere.get(i-1).type = 1;
					}
				}
			}
		}
		
		// ���� ���⿡ ���缭 order�� �������Ѵ�.
		String newOrder = "";
		for( int i = 0 ; i < adhere.size() ; i++ ) {
			// ���ӵ� "��"�� ����. �� "������"�� "��"���� �����.
			if( i != adhere.size()-1 ) {
				if( !(adhere.get(i).text == "��" && adhere.get(i+1).text == "��") && 
					!(adhere.get(i).text == "��" && adhere.get(i+1).text == "��") &&
					!(adhere.get(i).text == "��" && adhere.get(i+1).text == "��") ) {
					newOrder += adhere.get(i).text;
				}
			} else {
				newOrder += adhere.get(i).text;
			}
			
			// ������ ���� ������ ����Ǿ� �־��ٸ� �״�� ����
			if( adhere.get(i).type == 1 ) {
				newOrder += " ";
			}
		}
		
		return newOrder;
	}
	
	// �� ���ڿ��� ��ġ�� ����ϴ� �Լ�
	float Concordance(String arg0, String arg1) {
		 float con = 0.0f;
		 int matches = 0;
		 int same = 0;
		 	
		 if( arg0.length() > arg1.length() ) {
		 	matches = arg0.length();
		 	for( int i = 0 ; i < matches ; i++ ) {
		 		if( i < arg1.length() ) {
		 			if( arg0.substring(i, i+1).equals(arg1.substring(i, i+1)) ) same++;
		 		}
		 	}
		 }
		 else if( arg0.length() < arg1.length() ) {
		 	matches = arg1.length();
		 	for( int i = 0 ; i < matches ; i++ ) {
		 		if( i < arg0.length() ) {
		 			if( arg1.substring(i, i+1).equals(arg0.substring(i, i+1)) ) same++;
		 		}
		 	}
		 }
		 else {
		 	matches = arg0.length();
		 	for( int i = 0 ; i < matches ; i++ ) {
		 		if( arg0.substring(i, i+1).equals(arg1.substring(i, i+1)) ) same++;
		 	}
		}
		con = (float)same / (float)matches;
		 	
		return con;
	}
	
	// Ű���� ���� �Լ�
	public ArrayList<Keyword> getKeyword() {
		return keyword;
	}
	public Keyword getKeyword(int index) {
		return keyword.get(index);
	}
}
