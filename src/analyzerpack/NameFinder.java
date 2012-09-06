package analyzerpack;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.Map;
import java.util.ArrayList;

// ���� Ŭ���� (�̸�, Ÿ��)
// type0 -> �� ���� ������ ���Ⱑ �ƴ�
// type1 -> �� ���� ������ ������
class SpaceText {
	public String text = "";
	public int type = 0;
	public SpaceText(String Text, int Type) {
		text = Text;
		type = Type;
	}
}

// �̸� �ĺ� Ŭ���� (�̸�, ǰ��, �����̸���ȣ, ��ġ��)
class Lineup {
	public String text = "";
	public Pos pos;
	public int original;
	public float concordance = 0;
	public Lineup(String Text, Pos POS, int Original, float Concordance) {
		text = Text;
		pos = POS;
		original = Original;
		concordance = Concordance;
	}
}

// �̸����� Ŭ���� (���������� �̸�, ���� �̸�, ���Ǿ��Ʈ)
class NameDic {
	public String attachedName;
	public String originalName;
	public ArrayList<String> synonym;
	public NameDic(String AttachedName, String OriginalName, ArrayList<String> Synonym) {
		attachedName = AttachedName;
		originalName = OriginalName;
		synonym = Synonym;
	}
}

// ���ڸ�� Ŭ����
class NumNoun {
	public String name;
	public String num;
	public NumNoun(String Name, String Num) {
		name = Name;
		num = Num;
	}
}

public class NameFinder {
	
	ArrayList<NameDic> star;			// �� �̸�����
	ArrayList<NameDic> constellation;	// ���ڸ� �̸�����
	ArrayList<Keyword> keyword;			// Ű���� ûŷ ����Ʈ
	ArrayList<Keyword> numberList;		// ���� ûŷ ����Ʈ
	ArrayList<Keyword> special;			// Ư������ ûŷ ����Ʈ
	
	// ������
	public NameFinder() {
		star = new ArrayList<NameDic>();
		constellation = new ArrayList<NameDic>();
		keyword = new ArrayList<Keyword>();
		numberList = new ArrayList<Keyword>();
		special = new ArrayList<Keyword>();
	}
	
	// �̸� ������ �ҷ��ͼ� �ؽ��ʿ� ����
	public void CreateMap() {
		
		try {
			String file = "www/Dictionary/constellation_dic.txt";
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
			    
			    // ��� �ҹ��ڸ� �빮�ڷ� ����
			    line = line.toUpperCase();
			    String nameEntity = line;
			    ArrayList<String> syn = new ArrayList<String>();
			    
			    // "="�� �������� ���� ���� �̸�
			    if( line.indexOf("=") >= 0 ) {
			    	String []split = line.split("=");
			    	nameEntity = split[0];
			    	
			    	// "=" �ڴ� ���Ǿ�. ","�� �������� �߶� ���Ǿ� ����Ʈ�� ����.
			    	if( split[1].length() > 0 ) {
			    		String []synsplit = split[1].split(",");
			    		for( int i = 0 ; i < synsplit.length ; i++ ) {
			    			syn.add(synsplit[i]);
			    		}
			    	}
			    }
			    
			    // "@"�� �������� �տ��� ���� ������ �̸�, �ڿ��� �����̸�
		    	if( nameEntity.indexOf("@") >= 0 ) {
		    		String []nameSplit = line.split("@");
		    		constellation.add(new NameDic(nameSplit[0], nameSplit[1], syn));
		    	}
		    	else {
		    		constellation.add(new NameDic(nameEntity, nameEntity, syn));
		    	}
			}
			
		} catch(Exception e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
		
		try {
			String file = "www/Dictionary/star_dic.txt";
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
			    
			    // ��� �ҹ��ڸ� �빮�ڷ� ����
			    line = line.toUpperCase();
			    String nameEntity = line;
			    ArrayList<String> syn = new ArrayList<String>();
			    
			    // "="�� �������� ���� ���� �̸�
			    if( line.indexOf("=") >= 0 ) {
			    	String []split = line.split("=");
			    	nameEntity = split[0];
			    	
			    	// "=" �ڴ� ���Ǿ�. ","�� �������� �߶� ���Ǿ� ����Ʈ�� ����.
			    	if( split[1].length() > 0 ) {
			    		String []synsplit = split[1].split(",");
			    		for( int i = 0 ; i < synsplit.length ; i++ ) {
			    			syn.add(synsplit[i]);
			    		}
			    	}
			    }
			    
			    // "@"�� �������� �տ��� ���� ������ �̸�, �ڿ��� �����̸�
		    	if( nameEntity.indexOf("@") >= 0 ) {
		    		String []nameSplit = line.split("@");
		    		star.add(new NameDic(nameSplit[0], nameSplit[1], syn));
		    	}
		    	else {
		    		star.add(new NameDic(nameEntity, nameEntity, syn));
		    	}
			}
			
		} catch(Exception e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
	}
	
	public String Find(String order) {
		// �ҹ��ڴ� ��� �빮�ڷ� ����
		order = order.toUpperCase();
		
		// ���ڸ�� �Է�
		ArrayList<NumNoun> numNoun = new ArrayList<NumNoun>();
		numNoun.add(new NumNoun("ù", "1"));
		numNoun.add(new NumNoun("��", "1"));
		numNoun.add(new NumNoun("��", "2"));
		numNoun.add(new NumNoun("��", "3"));
		numNoun.add(new NumNoun("��", "4"));
		numNoun.add(new NumNoun("�ټ�", "5"));
		numNoun.add(new NumNoun("����", "6"));
		numNoun.add(new NumNoun("�ϰ�", "7"));
		numNoun.add(new NumNoun("����", "8"));
		numNoun.add(new NumNoun("��ȩ", "9"));
		numNoun.add(new NumNoun("��", "10"));
		numNoun.add(new NumNoun("�̽�", "20"));
		numNoun.add(new NumNoun("����", "20"));
		numNoun.add(new NumNoun("��", "50"));
		numNoun.add(new NumNoun("����", "50"));
		numNoun.add(new NumNoun("��", "100"));
		
		// ���ڸ�� ûŷ
		order = NumNounChunk(order, "��°", numNoun);
		order = NumNounChunk(order, "��", numNoun);
		order = NumNounChunk(order, "��", numNoun);
		
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
		
		int start_index = 0;
		
		while( start_index < adhere.size() - 1 ) {
			// �� ���ھ� �˻�.
			String preOrder = adhere.get(start_index).text;
			//System.out.println("�˻� : " + start_index + "(" + preOrder + ")");
			
			// ����� n��° ���ڷ� �����ϴ� �̸��� ������ �ִٸ� �˻� �ĺ��� �߰�..!
			// �� ��Ͽ� �ִ��� Ȯ��
			ArrayList<Lineup> lineup = new ArrayList<Lineup>();
			for( int i = 0 ; i < star.size() ; i++ ) {
				String prefix = star.get(i).attachedName;
				if( prefix.substring(0, 1).equals(preOrder) ) {
					lineup.add(new Lineup(prefix, Pos.S, i, 0.0f));
				}
				
				for( int j = 0 ; j < star.get(i).synonym.size() ; j++ ) {
					prefix = star.get(i).synonym.get(j);
					if( prefix.substring(0, 1).equals(preOrder) ) {
						lineup.add(new Lineup(prefix, Pos.S, i, 0.0f));
					}
				}
			}
			
			// ���ڸ� ��Ͽ� �ִ��� Ȯ���� ������ �߰�
			for( int i = 0 ; i < constellation.size() ; i++ ) {
				String prefix = constellation.get(i).attachedName;
				if( prefix.substring(0, 1).equals(preOrder) ) {
					lineup.add(new Lineup(prefix, Pos.C, i, 0.0f));
				}
				
				for( int j = 0 ; j < constellation.get(i).synonym.size() ; j++ ) {
					prefix = constellation.get(i).synonym.get(j);
					if( prefix.substring(0, 1).equals(preOrder) ) {
						lineup.add(new Lineup(prefix, Pos.C, i, 0.0f));
					}
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
				//System.out.println("�ձ��� ���� �ĺ��� : " + lineup.get(i).text + "(" + lineup.get(i).concordance + ")");
			}
			
			// ��Ȯ���� 100%�� �ĺ��� Ű����� ����.
			if( lineup.size() > choice && choice >= 0 ) {
				//System.out.println("* ��÷ : " + lineup.get(choice).text + "(" + lineup.get(choice).concordance + ")");
				
				// Ű���忡�� �����̸����� �ִ´�.
				int ori = lineup.get(choice).original;
				String formal = "";
				if( lineup.get(choice).pos == Pos.C ) formal = constellation.get(ori).originalName;
				else formal = star.get(ori).originalName;
				
				keyword.add(new Keyword(formal, lineup.get(choice).pos, start_index, lineup.get(choice).text.length()));
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
					for( int k = 0 ; k < keyword.get(j).originalLength ; k++ ) {
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
					numberList.add(new Keyword(addnumber, Pos.I, i, addnumber.length()));
					addnumber = "";
					
					// ���� ������ ������ ����
					if( i != 0 ) {
						if( adhere.get(i-1).type == 0 ) adhere.get(i-1).type = 1;
					}
				}
			}
		}
		
		// Ư�����ڸ� �����ؼ� ��� '��'���� �ٲ۴�.
		String addspecial = "";
		for( int i = 0 ; i < adhere.size() ; i++ ) {
			// Ư�����ڸ� ĳġ. �ϴ� ','��..
			if( adhere.get(i).text.charAt(0) == ',' ) {
				addspecial += adhere.get(i).text.charAt(0);
				adhere.get(i).text = "��";
				
				// Ư������ ���� ������ ����
				if( i != 0 ) {
					if( adhere.get(i-1).type == 0 ) adhere.get(i-1).type = 1;
				}
			}
			else {
				// ���ڰ� ������ �� �������� ��Ʈ���� ���� ����Ʈ�� �ִ´�.
				if( addspecial.length() > 0 ) {
					special.add(new Keyword(addspecial, Pos.A, i, addspecial.length()));
					addspecial = "";
					
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
					!(adhere.get(i).text == "��" && adhere.get(i+1).text == "��") &&
					!(adhere.get(i).text == "��" && adhere.get(i+1).text == "��")) {
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
	
	// ���� ��� ûŷ
	String NumNounChunk(String order, String keyword, ArrayList<NumNoun> numNoun) {
		String chunkOrder = order;
		
		int chkey = chunkOrder.indexOf(keyword, 0);
		while( chkey > 0 ) {
			boolean success = false;
			int gap = 1;
			for( int i = 0 ; i < numNoun.size() ; i++ ) {
				int nounlength = numNoun.get(i).name.length();

				if( chkey - nounlength >= 0 ) {
					for( int j = 1 ; j < numNoun.get(i).name.length() + 1 ; j++ ) {
						if( chunkOrder.charAt(chkey-j) == numNoun.get(i).name.charAt(nounlength-1) ) {
							success = true;
						}
						else {
							success = false;
						}
						if( chkey - (nounlength+1) >= 0 ) {
							if( chunkOrder.charAt(chkey-j-1) == numNoun.get(i).name.charAt(nounlength-1) ) {
								success = true;
							}
						}
						nounlength--;
					}
					// ûŷ ����
					if( success ) {
						chunkOrder = chunkOrder.replaceFirst(numNoun.get(i).name, numNoun.get(i).num);
						gap += (numNoun.get(i).num.length() - numNoun.get(i).name.length());
					}
				}
			}
			chkey = chunkOrder.indexOf(keyword, chkey+gap);
		}
		
		return chunkOrder;
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
	
	// ���ڸ���Ʈ ���� �Լ�
	public ArrayList<Keyword> getNumberList() {
		return numberList;
	}
	public Keyword getNumberList(int index) {
		return numberList.get(index);
	}
	
	// Ư������ ���� �Լ�
	public ArrayList<Keyword> getSpecial() {
		return special;
	}
	public Keyword getSpecial(int index) {
		return special.get(index);
	}
}
