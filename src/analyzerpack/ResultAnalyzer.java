package analyzerpack;

import java.io.*;
import java.util.ArrayList;
import java.util.StringTokenizer;

/* 
 * ���¼� �м� ����� �м����ִ� ���
 * �����ڿ��� ������ ������ ��
 * sentence�� Analyze�Լ��� ���ڷ� �޾ƿ� �м��մ�
 */
public class ResultAnalyzer {
	
	ArrayList<Sentence> Patterns;	// ���ϵ��� ����ϴ�.
	ArrayList<String> QueryForms;	// ���Ͽ� �ش��ϴ� �������� �����մϴ�. Patterns�� �����ϴ� ������ �����ϴ�.
	
	// ������
	// ������ �������ݴϴ�. ������Ʈ ����(krmorph)�� patterns.txt�� �־���մϴ�.
	// ������ ����ñ�... �ƴϸ� ����ȣ���� ã�ư�����
	public ResultAnalyzer() {
		Patterns = new ArrayList<Sentence>();
		QueryForms = new ArrayList<String>();
		OrganizePattern();
	}
	
	// private�̹Ƿ� OrganizePattern �ۿ��� ���θ��ϴ�.
	// �θ��� ���ð� �����ڿ��� ���� �����ϰ� ���ּ���!
	private void OrganizePattern()
	{
		try
		{
			// ������Ʈ ����(krmorph)�� �� �־���մϴ�.
			FileReader fr = new FileReader("patterns.txt");
			BufferedReader br = new BufferedReader(fr);
		
			String pattern;
			
			// ������ �� �پ� �����մϴ�.
			while( (pattern = br.readLine()) != null )
			{
				if( pattern.compareTo("") == 0 || pattern.substring(0, 2).compareTo("//") == 0 )
					continue;
				
				Sentence sentence = new Sentence("");
				String queryform;
				// ���� ������ �����մϴ�. :�� �������� �����մϴ�.
				StringTokenizer stColon = new StringTokenizer( pattern, ":");
				queryform = stColon.nextToken();
				pattern = pattern.substring(queryform.length()+1);
				QueryForms.add(queryform);
				
				ArrayList<String> tempWords = new ArrayList<String>();
				StringTokenizer stUnderbar = new StringTokenizer( pattern, "_");
				// �ӽ÷� _�� �������� �߶� �����մϴ�. word�����̹Ƿ� lemma������ �߶�����մϴ�.
				while( stUnderbar.countTokens() != 0 )
					tempWords.add( stUnderbar.nextToken() );
				
				// word������ lemma������ �ڸ��� ���� �ٽ� +�� �������� �ڸ��ϴ�.
				for( int i = 0; i < tempWords.size(); i++ )
				{
					String nativeText = "", analysedText = "";
					String tempWord = tempWords.get(i);
					StringTokenizer stPlus = new StringTokenizer( tempWord, "+" );
					int numofPlus = stPlus.countTokens();
					for( int cntPlus = 0; cntPlus < numofPlus; cntPlus++ )
					{
						String text, pos;
						StringTokenizer stSlash = new StringTokenizer( tempWord, "/" );
						text = stSlash.nextToken();
						tempWord = tempWord.substring(text.length()+1);
						stPlus = new StringTokenizer( tempWord, "+" );
						pos = stPlus.nextToken();
						if( cntPlus != numofPlus-1 )
							tempWord = tempWord.substring(pos.length()+1);
						if( cntPlus != 0 )
							analysedText += ",";
						
						nativeText += text;
						analysedText += text + "(" + pos + ")";
					}
					// lemma�� �ϼ��Ǵ� ��������. word������ ó���� �������ϴ�.
					sentence.addWord( nativeText, analysedText, 0, 0 );
				}
				// �� ������ sentence�� �ϳ��� ������ �˴ϴ�. ���� �����մϴ�.
				Patterns.add(sentence);
			}
			
			//System.out.println("���� ���� : " + Patterns.size());
			br.close();
			fr.close();
		}
		catch(IOException ioe)
		{
			ioe.printStackTrace();
			System.out.println(System.getProperty("user.dir"));
		}
		
		// ������ ����� �����ƴ��� �� �پ� ����ϸ鼭 �����մϴ�.
		//for( int iter = 0; iter < Patterns.size(); iter++ )
			//PrintSentence(Patterns.get(iter));
	}
	
	// ���������� sentence�� �޾ƿ� ���¼� �м������ �ٽ� �м��ؼ� ������ ����� �����ݴϴ�.
	// ���⼭ ���� ���ϵ�� �����մϴ�.
	// �ߺ� ���� �����ϸ�, ������ �ϳ��̻� ���� �����մϴ�. ����� |�� ���а����ϰ� �Ǿ��ֽ��ϴ�.
	// ����� String���� ��ȯ�˴ϴ�. ���Ŀ� �������� �迭�� ��ȯ�� ���ɼ��� �ֽ��ϴ�.
	public String Analyze(Sentence sentence)
	{
		// �Ƹ� �ϴٺ��� ���⼭ NoResult�� ����� ���� ���� �� �ִٰ� �����մϴ�.
		// �׷����� ���� ��ġ�� �����ϴ� ��찡 �Ǵµ���.
		// ������غ����� ���� boolean���� noMatched�� �ɸ��� ������ �ߴ����� ���
		// �ɸ��� �Ǹ� �� ��Ȳ���� � ������ �Ǵܹ̽��� ������ ���ø� �˴ϴ�.
		String result = "NoResult";
		String name = "";
		String value1 = "", value2 = "";
		boolean noMatched = false;
		int numofLemma = CountLemma(sentence);
		
		// ù ��° ���Ϻ��� ������� �˻��մϴ�.
		for( int patternCnt = 0; patternCnt < Patterns.size(); patternCnt++ )
		{
			Sentence pattern = Patterns.get(patternCnt);
			int patternMatcherIndex = -1;
			int lastMatchedIndex = -1;
			noMatched = false;
			
			// ������ ���� �޺κ� lemma���� �˻��ϴ� ��ƾ�Դϴ�.
			for( int wordCnt = pattern.getWord().size()-1; wordCnt >= 0; wordCnt-- )
			{
				Word word = pattern.getWord(wordCnt);
				for( int lemmaCnt = word.getLemma().size()-1; lemmaCnt >= 0; lemmaCnt-- )
				{
					Lemma lemma = word.getLemma(lemmaCnt);
					
					// �� �������� �˻����� �ֽ��ϴ�.
					// ù ��° ����� text�� �˻����� ������ ǰ�縦 �������� Matching���ִ� �Լ��� ����ϴ� ����Դϴ�.
					if( lemma.getText().compareTo("NAME") == 0 )
					{
						patternMatcherIndex = DetectMatchingLemma(sentence, lastMatchedIndex, lemma.getPos());
						// ������ 0�̻� ���°Ÿ� sentence�� �ش� index��° lemma���� ���ڸ��� ���̸��� ����� ���Դϴ�.
						// �׷��� name���ڿ��� �̸��� �����صӴϴ�.
						if( patternMatcherIndex > 0 )
						{
							int lemmaIndex= numofLemma-patternMatcherIndex;
							name = GetLemma( sentence, lemmaIndex ).getText();
						}
						// �� �� ������ �ε����� 0�̻��� ���;� �����ε�, else�� �ɷȴٸ� ��������� -1�� ��ȯ�� ���Դϴ�.
						// �̴� �����͸� ��ã�� ���̹Ƿ� ���α׷� § ����ȣ���� ������ �ֽ��ϴ�.
						// �� �����Ͻʽÿ�!
						else
							name = "??";
					}
					else if( lemma.getText().compareTo("VALUE1") == 0 )
					{
						patternMatcherIndex = DetectMatchingLemma(sentence, lastMatchedIndex, lemma.getPos());
						// ������ 0�̻� ���°Ÿ� sentence�� �ش� index��° lemma���� ���ڸ��� ���̸��� ����� ���Դϴ�.
						// �׷��� name���ڿ��� �̸��� �����صӴϴ�.
						if( patternMatcherIndex > 0 )
						{
							int lemmaIndex= numofLemma-patternMatcherIndex;
							value1 = GetLemma( sentence, lemmaIndex ).getText();
						}
						// �� �� ������ �ε����� 0�̻��� ���;� �����ε�, else�� �ɷȴٸ� ��������� -1�� ��ȯ�� ���Դϴ�.
						// �̴� �����͸� ��ã�� ���̹Ƿ� ���α׷� § ����ȣ���� ������ �ֽ��ϴ�.
						// �� �����Ͻʽÿ�!
						else
							value1 = "??";
					}
					else if( lemma.getText().compareTo("VALUE2") == 0 )
					{
						patternMatcherIndex = DetectMatchingLemma(sentence, lastMatchedIndex, lemma.getPos());
						// ������ 0�̻� ���°Ÿ� sentence�� �ش� index��° lemma���� ���ڸ��� ���̸��� ����� ���Դϴ�.
						// �׷��� name���ڿ��� �̸��� �����صӴϴ�.
						if( patternMatcherIndex > 0 )
						{
							int lemmaIndex= numofLemma-patternMatcherIndex;
							value2 = GetLemma( sentence, lemmaIndex ).getText();
						}
						// �� �� ������ �ε����� 0�̻��� ���;� �����ε�, else�� �ɷȴٸ� ��������� -1�� ��ȯ�� ���Դϴ�.
						// �̴� �����͸� ��ã�� ���̹Ƿ� ���α׷� § ����ȣ���� ������ �ֽ��ϴ�.
						// �� �����Ͻʽÿ�!
						else
							value2 = "??";
					}
					// �� ��° ����� ǰ��� �˻����� ������ text�� �������� Matching���ִ� �Լ��� ����ϴ� ����Դϴ�.
					else if( lemma.getPos() == Pos.X )
					{
						patternMatcherIndex = DetectMatchingLemma(sentence, lastMatchedIndex, lemma.getText());
					}
					// �� ��° ����� text�� ǰ�� ������ ��� ��ġ�ϴ� ��츸 ����ϴ� ����Դϴ�.
					else
					{
						patternMatcherIndex = DetectMatchingLemma(sentence, lastMatchedIndex, lemma);
					}
					// �� ���� ����� �ϳ��� �����ؼ� -1�� ���´ٸ� ���� �� ������ match�� �� ���ٰ� �Ǵ��� ���Դϴ�.
					// ���� ������ �ߴ��ϰ� ���� �������� �Ѿ���մϴ�.
					if( patternMatcherIndex == -1 || 
						( lastMatchedIndex != -1 && patternMatcherIndex != lastMatchedIndex+1 ) )
						noMatched = true;
					if( noMatched )
						break;
					// ���� ���� match�� �����ߴٸ� ���� �ֱٿ� ��ġ�� index���� �˱� ���� �����صӴϴ�.
					// �� index���� lemma matching�� �����ϰ� ���˴ϴ�.
					lastMatchedIndex = patternMatcherIndex;
				}
				// �� ���� ����� �ϳ��� �����ؼ� -1�� ���´ٸ� ���� �� ������ match�� �� ���ٰ� �Ǵ��� ���Դϴ�.
				// ���� ������ �ߴ��ϰ� ���� �������� �Ѿ���մϴ�.
				if( noMatched )
					break;
				
				// ���� ������ ������ �Ϸ�ƽ��ϴ�.
				// ���� �� ������ ��Ȯ�� �����ȿ� ������ ���⿡ �����߽��ϴ�.
				if( wordCnt == 0 )
				{
					String patternResult = QueryForms.get(patternCnt);
					
					patternResult = patternResult.replaceAll("NAME", name);
					patternResult = patternResult.replaceAll("VALUE1", value1);
					patternResult = patternResult.replaceAll("VALUE2", value2);
					if( result == "NoResult" )
						result = patternResult;
					else
						//result += "|" + patternResult;
						result = patternResult;
					if( numofLemma > lastMatchedIndex )
					{
						wordCnt = pattern.getWord().size();
						patternMatcherIndex = -1;
					}
				}
					
			}
		}
		
		return result;
	}
	
	// sentence������ ������ִ� �Լ��Դϴ�. 
	// �ܺο��� ����� �� ���� �����մϴ�.
	// ... ���Ϸ� ����մϱ�.. ����..
	private void PrintSentence(Sentence sentence)
	{
		String result = "";
		
		for( int wordCnt = 0; wordCnt < sentence.getWord().size(); wordCnt++ )
		{
			Word word = sentence.getWord(wordCnt);
			for( int lemmaCnt = 0; lemmaCnt < word.getLemma().size(); lemmaCnt++ )
			{
				Lemma lemma = word.getLemma(lemmaCnt);
				result += lemma.getText() + "/" + lemma.getPos();
				if( lemmaCnt != word.getLemma().size()-1 )
					result += "+";
			}
			if( wordCnt != sentence.getWord().size()-1 )
				result += "_";
		}
		System.out.println(result);
	}
	
	// ù ��° lemma matching ���� �ռ��Դϴ�.
	private int DetectMatchingLemma( Sentence sentence, int lastMatchedIndex, Pos pos )
	{
		int counter = 0;
		for( int wordCnt = sentence.getWord().size()-1; wordCnt >= 0; wordCnt-- )
		{
			Word word = sentence.getWord(wordCnt);
			for( int lemmaCnt = word.getLemma().size()-1; lemmaCnt >= 0; lemmaCnt-- )
			{
				counter++;
				Lemma lemma = word.getLemma(lemmaCnt);
				if( lemma.getPos() == pos && counter > lastMatchedIndex )
					return counter;
			}
		}
		return -1;
	}
	
	// �� ��° lemma matching ���� �ռ��Դϴ�.
	private int DetectMatchingLemma( Sentence sentence, int lastMatchedIndex, String text )
	{
		int counter = 0;
		for( int wordCnt = sentence.getWord().size()-1; wordCnt >= 0; wordCnt-- )
		{
			Word word = sentence.getWord(wordCnt);
			for( int lemmaCnt = word.getLemma().size()-1; lemmaCnt >= 0; lemmaCnt-- )
			{
				counter++;
				Lemma lemma = word.getLemma(lemmaCnt);
				if( lemma.getText().compareTo(text) == 0 && counter > lastMatchedIndex )
					return counter;
			}
		}
		return -1;
	}
	
	// �� ��° lemma matching ���� �ռ��Դϴ�.
	private int DetectMatchingLemma( Sentence sentence, int lastMatchedIndex, Lemma paramlemma )
	{
		int counter = 0;
		for( int wordCnt = sentence.getWord().size()-1; wordCnt >= 0; wordCnt-- )
		{
			Word word = sentence.getWord(wordCnt);
			for( int lemmaCnt = word.getLemma().size()-1; lemmaCnt >= 0; lemmaCnt-- )
			{
				counter++;
				Lemma lemma = word.getLemma(lemmaCnt);
				if( lemma.getText().compareTo(paramlemma.getText()) == 0 && lemma.getPos() == paramlemma.getPos() && counter > lastMatchedIndex )
					return counter;
			}
		}
		return -1;
	}
	
	// sentence�� lemma�� �� �� ����ִ��� ���� �Լ��Դϴ�.
	private int CountLemma( Sentence sentence )
	{
		int counter = 0;
		for( int wordCnt = 0; wordCnt < sentence.getWord().size(); wordCnt++ )
			for( int lemmaCnt = 0; lemmaCnt < sentence.getWord(wordCnt).getLemma().size(); lemmaCnt++ )
				counter++;
		return counter;
	}
	
	// �ش� index��° lemma�� �ҷ��ɴϴ�. 0���� count�մϴ�.
	private Lemma GetLemma( Sentence sentence, int lemmaIndex )
	{
		int counter = 0;
		Lemma lemma = new Lemma("������Lemma", Pos.X);
		for( int wordCnt = 0; wordCnt < sentence.getWord().size(); wordCnt++ )
		{
			Word word = sentence.getWord(wordCnt);
			for( int lemmaCnt = 0; lemmaCnt < word.getLemma().size(); lemmaCnt++ )
			{
				if( lemmaIndex == counter )
				{
					lemma = word.getLemma(lemmaCnt);
					return lemma;
				}
				counter++;
			}
		}
		
		System.out.println("������ lemma�� ��µ˴ϴ�.");
		return lemma;
	}
}
