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
	
	ArrayList<Sentence> SimplePatterns;	// ���ϵ��� ����ϴ�.
	ArrayList<String> SimpleQueryForms;	// ���Ͽ� �ش��ϴ� �������� �����մϴ�. Patterns�� �����ϴ� ������ �����ϴ�.
	ArrayList<Sentence> ComplicatedPatterns_core;	// 
	ArrayList<String> ComplicatedQueryForms_core;	// 
	ArrayList<ArrayList<Sentence>> ComplicatedPatterns_frame;	// 
	ArrayList<ArrayList<String>> ComplicatedQueryForms_frame_front;	// 
	ArrayList<String> ComplicatedQueryForms_frame_end;	//
	int patternMatcherIndex;
	int lastMatchedIndex;
	
	// ������
	// ������ �������ݴϴ�. ������Ʈ ����(krmorph)�� patterns.txt�� �־���մϴ�.
	// ������ ����ñ�... �ƴϸ� ����ȣ���� ã�ư�����
	public ResultAnalyzer() {
		SimplePatterns = new ArrayList<Sentence>();
		SimpleQueryForms = new ArrayList<String>();
		ComplicatedPatterns_core = new ArrayList<Sentence>();
		ComplicatedQueryForms_core = new ArrayList<String>();
		ComplicatedPatterns_frame = new ArrayList<ArrayList<Sentence>>();
		ComplicatedQueryForms_frame_front = new ArrayList<ArrayList<String>>();
		ComplicatedQueryForms_frame_end = new ArrayList<String>();
		OrganizePattern();
	}
	
	// private�̹Ƿ� OrganizePattern �ۿ��� ���θ��ϴ�.
	// �θ��� ���ð� �����ڿ��� ���� �����ϰ� ���ּ���!
	private void OrganizePattern()
	{
		try
		{
			// ������Ʈ ����(krmorph)�� �� �־���մϴ�.
			FileReader fr = new FileReader("simple_patterns.txt");
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
				SimpleQueryForms.add(queryform);
				
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
					int numofIgnore = 0;
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
						
						if( text.charAt(0) == '@' )
							numofIgnore = Integer.parseInt(text.substring(1));
						
						nativeText += text;
						analysedText += text + "(" + pos + ")";
					}
					// lemma�� �ϼ��Ǵ� ��������. word������ ó���� �������ϴ�.
					sentence.addWord( nativeText, analysedText, 0, 0, 0, numofIgnore, Repeat.NOMORE );
				}
				// �� ������ sentence�� �ϳ��� ������ �˴ϴ�. ���� �����մϴ�.
				SimplePatterns.add(sentence);
			}
			
			//System.out.println("���� ���� : " + SimplePatterns.size());
			br.close();
			fr.close();
		}
		catch(IOException ioe)
		{
			ioe.printStackTrace();
			System.out.println(System.getProperty("user.dir"));
		}
		
		try
		{
			// ������Ʈ ����(krmorph)�� �� �־���մϴ�.
			FileReader fr = new FileReader("complicated_patterns_core.txt");
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
				ComplicatedQueryForms_core.add(queryform);
				
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
					int numofIgnore = 0;
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
						
						if( text.charAt(0) == '@' )
							numofIgnore = Integer.parseInt(text.substring(1));
						
						nativeText += text;
						analysedText += text + "(" + pos + ")";
					}
					// lemma�� �ϼ��Ǵ� ��������. word������ ó���� �������ϴ�.
					sentence.addWord( nativeText, analysedText, 0, 0, 0, numofIgnore, Repeat.NOMORE );
				}
				// �� ������ sentence�� �ϳ��� ������ �˴ϴ�. ���� �����մϴ�.
				ComplicatedPatterns_core.add(sentence);
			}
			
			//System.out.println("���� ���� : " + ComplicatedPatterns_core.size());
			br.close();
			fr.close();
		}
		catch(IOException ioe)
		{
			ioe.printStackTrace();
			System.out.println(System.getProperty("user.dir"));
		}
		
		try
		{
			// ������Ʈ ����(krmorph)�� �� �־���մϴ�.
			FileReader fr = new FileReader("complicated_patterns_frame.txt");
			BufferedReader br = new BufferedReader(fr);
		
			String pattern;
			
			// ������ �� �پ� �����մϴ�.
			while( (pattern = br.readLine()) != null )
			{
				if( pattern.compareTo("") == 0 || pattern.substring(0, 2).compareTo("//") == 0 )
					continue;
				
				ArrayList<String> queryformList = new ArrayList<String>();
				ArrayList<Sentence> sentenceList = new ArrayList<Sentence>();
				//Sentence sentence = new Sentence("");
				String queryform, frontQuery, endQuery;
				// ���� ������ �����մϴ�. :�� �������� �����մϴ�.
				StringTokenizer stColon = new StringTokenizer( pattern, ":");
				queryform = stColon.nextToken();
				StringTokenizer stTide = new StringTokenizer( queryform, "~" );
				frontQuery = stTide.nextToken();
				endQuery = stTide.nextToken();
				pattern = pattern.substring(queryform.length()+1);
				ComplicatedQueryForms_frame_end.add(endQuery);
				
				StringTokenizer stBar = new StringTokenizer( frontQuery, "|" );
				int numofDivision = stBar.countTokens();
				for( int divisionCnt = 0; divisionCnt < numofDivision; divisionCnt++ )
				{
					Sentence sentence = new Sentence("");
					sentenceList.add(sentence);
				}
				int startIdx = frontQuery.indexOf('[');
				int endIdx = frontQuery.indexOf(']');
				if( startIdx != -1 && endIdx != -1)
				{
					String subcontents = frontQuery.substring(startIdx+1, endIdx);
					stBar = new StringTokenizer( subcontents, "|" );
					
					while( stBar.countTokens() != 0 )
					{
						String temp = frontQuery.substring( 0, startIdx+1 ) + frontQuery.substring( endIdx );
						endIdx = temp.indexOf(']');
						String queryform_divided = temp.substring( 0, startIdx+1 ) + stBar.nextToken() + temp.substring( endIdx );
						queryformList.add(queryform_divided);
					}
				}
				else
					queryformList.add(frontQuery);
					
				ComplicatedQueryForms_frame_front.add(queryformList);
				
				ArrayList<String> tempWordList = new ArrayList<String>();
				StringTokenizer stUnderbar = new StringTokenizer( pattern, "_");
				// �ӽ÷� _�� �������� �߶� �����մϴ�. word�����̹Ƿ� lemma������ �߶�����մϴ�.
				while( stUnderbar.countTokens() != 0 )
				{
					String token = stUnderbar.nextToken();
					
					if( token.charAt(0) == '[' ) 
					{
						while( token.charAt(token.length()-1) != '+' && token.charAt(token.length()-1) != '*' )
						{		
							String nextToken = stUnderbar.nextToken();
							token += "_" + nextToken;
						}
					}
					tempWordList.add( token );
				}
				
				ArrayList<ArrayList<Integer>> lemmaRepeatIDs = new ArrayList<ArrayList<Integer>>();
				int lemmaRepeatID = 0;
				// word������ lemma������ �ڸ��� ���� �ٽ� +�� �������� �ڸ��ϴ�.
				for( int i = 0; i < tempWordList.size(); i++ )
				{
					Repeat numofRepeat = Repeat.NOMORE;
					String tempWord = tempWordList.get(i);
					
					if( tempWord.charAt(0) == '[' )
					{
						if( tempWord.charAt(tempWord.length()-1) == '+' )		
							numofRepeat = Repeat.ONE_OR_MORE;
						else if( tempWord.charAt(tempWord.length()-1) == '*' )
							numofRepeat = Repeat.ZERO_OR_MORE;
						tempWord = tempWord.substring(1, tempWord.length()-2);
						
						ArrayList<String> tempWords = new ArrayList<String>();
						StringTokenizer stBrace = new StringTokenizer( tempWord, "|" );
						while( stBrace.countTokens() != 0 )
						{
							String tempWord_divided = stBrace.nextToken();
							tempWords.add( tempWord_divided.substring(1, tempWord_divided.length()-1) );
							lemmaRepeatIDs.add(new ArrayList<Integer>());
						}
						
						for( int tempWordCnt = 0; tempWordCnt < tempWords.size(); tempWordCnt++ )
						{
							lemmaRepeatID = 0;
							tempWord = tempWords.get(tempWordCnt);
							stUnderbar = new StringTokenizer( tempWord, "_");
							while( stUnderbar.countTokens() != 0 )
							{
								ArrayList<String> tempLemmas = new ArrayList<String>();
								StringTokenizer stPlus = new StringTokenizer( stUnderbar.nextToken(), "+" );
								String nativeText = "", analysedText = "";
								int numofPlus = stPlus.countTokens();
								int numofIgnore = 0;
	
								while( stPlus.countTokens() != 0 )
									tempLemmas.add( stPlus.nextToken() );
								
								for( int cntPlus = 0; cntPlus < numofPlus; cntPlus++ )
								{
									String text, pos;
									tempWord = tempLemmas.get(cntPlus);
									StringTokenizer stSlash = new StringTokenizer( tempWord, "/" );
									text = stSlash.nextToken();
									tempWord = tempWord.substring(text.length()+1);
									stPlus = new StringTokenizer( tempWord, "+" );
									pos = stPlus.nextToken();
//									if( cntPlus != numofPlus-1 )
//										tempWord = tempWord.substring(pos.length()+1);
									if( cntPlus != 0 )
										analysedText += ",";
									
									if( text.charAt(0) == '@' )
										numofIgnore = Integer.parseInt(text.substring(1));
									
									nativeText += text;
									analysedText += text + "(" + pos + ")";
									lemmaRepeatIDs.get(tempWordCnt).add(++lemmaRepeatID);
								}
								// lemma�� �ϼ��Ǵ� ��������. word������ ó���� �������ϴ�.
								sentenceList.get(tempWordCnt).addWord( nativeText, analysedText, 0, 0, 0, numofIgnore, numofRepeat );
							}
						}
					}
					else
					{
						StringTokenizer stPlus = new StringTokenizer( tempWord, "+" );
						String nativeText = "", analysedText = "";
						int numofPlus = stPlus.countTokens();
						int numofIgnore = 0;
						
						lemmaRepeatIDs.add(new ArrayList<Integer>());

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
							
							if( text.charAt(0) == '@' )
								numofIgnore = Integer.parseInt(text.substring(1));
							
							nativeText += text;
							analysedText += text + "(" + pos + ")";
							for( int sentenceCnt = 0;  sentenceCnt < sentenceList.size(); sentenceCnt++ )
								lemmaRepeatIDs.get(sentenceCnt).add(0);
						}
						// lemma�� �ϼ��Ǵ� ��������. word������ ó���� �������ϴ�.
						for( int sentenceCnt = 0;  sentenceCnt < sentenceList.size(); sentenceCnt++ )
							sentenceList.get(sentenceCnt).addWord( nativeText, analysedText, 0, 0, 0, numofIgnore, Repeat.NOMORE );
					}
				}
				for( int sentenceCnt = 0;  sentenceCnt < sentenceList.size(); sentenceCnt++ )
					sentenceList.get(sentenceCnt).setRepeatID(lemmaRepeatIDs.get(sentenceCnt));
				// �� ������ sentence�� �ϳ��� ������ �˴ϴ�. ���� �����մϴ�.
//				for( int divisionCnt = 0; divisionCnt < numofDivision; divisionCnt++ )
//				{
//					ComplicatedPatterns_frame.add(sentenceList.get(divisionCnt));
//				}
				ComplicatedPatterns_frame.add(sentenceList);
			}
			
			//System.out.println("���� ���� : " + ComplicatedPatterns_frame.size());
			br.close();
			fr.close();
		}
		catch(IOException ioe)
		{
			ioe.printStackTrace();
			System.out.println(System.getProperty("user.dir"));
		}
		
		// ������ ����� �����ƴ��� �� �پ� ����ϸ鼭 �����մϴ�.
		for( int iter = 0; iter < SimplePatterns.size(); iter++ )
			PrintSentence(SimplePatterns.get(iter));
		
		// ������ ����� �����ƴ��� �� �پ� ����ϸ鼭 �����մϴ�.
		for( int iter = 0; iter < ComplicatedPatterns_core.size(); iter++ )
			PrintSentence(ComplicatedPatterns_core.get(iter));
		
		// ������ ����� �����ƴ��� �� �پ� ����ϸ鼭 �����մϴ�.
		for( int iter = 0; iter < ComplicatedPatterns_frame.size(); iter++ )
			for( int iter2 = 0; iter2 < ComplicatedPatterns_frame.get(iter).size(); iter2++ )
				PrintSentence(ComplicatedPatterns_frame.get(iter).get(iter2));
	}
	
	public String ExAnalyze_Core(Sentence sentence)
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
		int initPatternMatcherIndex = patternMatcherIndex;
		int initLastMatchedIndex = lastMatchedIndex;
		// ù ��° ���Ϻ��� ������� �˻��մϴ�.
		for( int patternCnt = 0; patternCnt < ComplicatedPatterns_core.size(); patternCnt++ )
		{
			Sentence pattern = ComplicatedPatterns_core.get(patternCnt);
			patternMatcherIndex = initPatternMatcherIndex;
			lastMatchedIndex = initLastMatchedIndex;
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
						patternMatcherIndex = DetectMatchingLemma(sentence, lemma.getPos());
						// ������ 0�̻� ���°Ÿ� sentence�� �ش� index��° lemma���� ���ڸ��� ���̸��� ����� ���Դϴ�.
						// �׷��� name���ڿ��� �̸��� �����صӴϴ�.
						if( patternMatcherIndex > 0 )
						{
							int lemmaIndex = numofLemma-patternMatcherIndex;
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
						patternMatcherIndex = DetectMatchingLemma(sentence, lemma.getPos());
						// ������ 0�̻� ���°Ÿ� sentence�� �ش� index��° lemma���� ���ڸ��� ���̸��� ����� ���Դϴ�.
						// �׷��� name���ڿ��� �̸��� �����صӴϴ�.
						if( patternMatcherIndex > 0 )
						{
							int lemmaIndex = numofLemma-patternMatcherIndex;
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
						patternMatcherIndex = DetectMatchingLemma(sentence, lemma.getPos());
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
						if( lemma.getText().charAt(0) != '@' )
							patternMatcherIndex = DetectMatchingLemma(sentence, lemma.getText() );
						else if( word.getNumofIgnore() > 0 )
						{
							Word nextWord = pattern.getWord(wordCnt-1);
							patternMatcherIndex = DetectMatchingWord(sentence, nextWord );
							if( patternMatcherIndex == -1 )
							{
								lastMatchedIndex--;
								patternMatcherIndex = lastMatchedIndex+1;
								word.decreaseNumofIgnore();
								wordCnt++;
							}
							else
							{
								lastMatchedIndex = patternMatcherIndex-1;
								word.setNumofIgnore(0);
							}
						}
						else
							lastMatchedIndex = patternMatcherIndex-1;
							
					}
					// �� ��° ����� text�� ǰ�� ������ ��� ��ġ�ϴ� ��츸 ����ϴ� ����Դϴ�.
					else
					{
						patternMatcherIndex = DetectMatchingLemma(sentence, lemma);
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
					String patternResult = ComplicatedQueryForms_core.get(patternCnt);
					
					patternResult = patternResult.replaceAll("NAME", name);
					patternResult = patternResult.replaceAll("VALUE1", value1);
					patternResult = patternResult.replaceAll("VALUE2", value2);
					if( result == "NoResult" )
						result = patternResult;
					else
						//result += "|" + patternResult;
						result = patternResult;
					
					return result;
					
//					if( numofLemma > lastMatchedIndex )
//					{
//						wordCnt = pattern.getWord().size();
//						patternMatcherIndex = -1;
//					}
				}
					
			}
		}
		
		return result;		
		//for( int iter = 0; iter < Patterns.size(); iter++ )
			//PrintSentence(Patterns.get(iter));
	}
	
	// ���������� sentence�� �޾ƿ� ���¼� �м������ �ٽ� �м��ؼ� ������ ����� �����ݴϴ�.
	// ���⼭ ���� ���ϵ�� �����մϴ�.
	// �ߺ� ���� �����ϸ�, ������ �ϳ��̻� ���� �����մϴ�. ����� |�� ���а����ϰ� �Ǿ��ֽ��ϴ�.
	// ����� String���� ��ȯ�˴ϴ�. ���Ŀ� �������� �迭�� ��ȯ�� ���ɼ��� �ֽ��ϴ�.
	public String ExAnalyze_Frame(Sentence sentence)
	{
		String result = "NoResult";
		String name = "";
		String value1 = "", value2 = "";
		boolean noMatched = false;
		boolean repeated = false;
		int numofLemma = CountLemma(sentence);
		
		for( int patternCnt = 0; patternCnt < ComplicatedPatterns_frame.size(); patternCnt++ )
		{
			ArrayList<Sentence> asSamePatternList = ComplicatedPatterns_frame.get(patternCnt);
			ArrayList<String> CoreList = new ArrayList<String>();
			ArrayList<Integer> CoreListAssister = new ArrayList<Integer>();
			int repeatingPoint_word = 0;
			int repeatingPoint_lemma = 0;
			int repeatingPoint_lemmaIndex = 0;
			patternMatcherIndex = -1;
			lastMatchedIndex = -1;
			noMatched = false;
			for( int asSameCnt = 0; asSameCnt < asSamePatternList.size(); asSameCnt++ )
			{
				Sentence pattern = asSamePatternList.get(asSameCnt);
				int lemmaIndex = CountLemma( pattern );
				for( int wordCnt = pattern.getWord().size()-1; wordCnt >= 0; wordCnt-- )
				{
					if( repeated || ( noMatched && lastMatchedIndex != -1 ) )
					{
						wordCnt = pattern.getWord().size()-repeatingPoint_word-1;
						for( int i = pattern.getWord().size()-1; i > wordCnt; i-- )
							lemmaIndex -= pattern.getWord(i).getLemma().size();
						noMatched = false;
						if( asSameCnt == asSamePatternList.size()-1 )
							repeated = false;
					}
					Word word = pattern.getWord(wordCnt);
					for( int lemmaCnt = word.getLemma().size()-1; lemmaCnt >= 0; lemmaCnt-- )
					{
						lemmaIndex--;
						Lemma lemma = word.getLemma(lemmaCnt);
						
						if( lemma.getPos() == Pos.P )
						{
							//patternMatcherIndex++;
							String coreData = ExAnalyze_Core(sentence);
							if( coreData.compareTo("NoResult") == 0 )
								patternMatcherIndex = -1;
							else
							{
								CoreList.add(coreData);
								CoreListAssister.add(asSameCnt);
								lastMatchedIndex = patternMatcherIndex-1;
							}
						}
						// �� ��° ����� ǰ��� �˻����� ������ text�� �������� Matching���ִ� �Լ��� ����ϴ� ����Դϴ�.
						else if( lemma.getPos() == Pos.X )
						{
							if( lemma.getText().charAt(0) != '@' )
								patternMatcherIndex = DetectMatchingLemma(sentence, lemma.getText() );
							else if( word.getNumofIgnore() > 0 )
							{
								Word nextWord = pattern.getWord(wordCnt-1);
								patternMatcherIndex = DetectMatchingWord(sentence, nextWord );
								if( patternMatcherIndex == -1 )
								{
									lastMatchedIndex--;
									patternMatcherIndex = lastMatchedIndex+1;
									word.decreaseNumofIgnore();
									wordCnt++;
								}
								else
								{
									lastMatchedIndex = patternMatcherIndex-1;
									word.setNumofIgnore(0);
								}
							}
							else
								lastMatchedIndex = patternMatcherIndex-1;
								
						}
						// �� ��° ����� text�� ǰ�� ������ ��� ��ġ�ϴ� ��츸 ����ϴ� ����Դϴ�.
						else
						{
							patternMatcherIndex = DetectMatchingLemma(sentence, lemma);
						}
						
						if( lemma.getRepeatID() == 0 &&
							lemmaIndex != 0 && GetLemma( pattern, lemmaIndex-1).getRepeatID() != 0 )
						{
							repeatingPoint_word = pattern.getWord().size()-wordCnt;
							repeatingPoint_lemma = lemmaCnt;
							repeatingPoint_lemmaIndex = lemmaIndex;
						}
						else if( lemma.getRepeatID() == 1 && patternMatcherIndex != numofLemma )
						{
							lastMatchedIndex = patternMatcherIndex;
							wordCnt = pattern.getWord().size()-repeatingPoint_word;
							lemmaCnt = repeatingPoint_lemma;
							lemmaIndex = repeatingPoint_lemmaIndex;
							asSameCnt = -1;
							repeated = true;
						}
						
						if( patternMatcherIndex == -1 || 
								( lastMatchedIndex != -1 && patternMatcherIndex != lastMatchedIndex+1 ) )
							noMatched = true;
						if( noMatched || asSameCnt == -1 )
							break;
						// ���� ���� match�� �����ߴٸ� ���� �ֱٿ� ��ġ�� index���� �˱� ���� �����صӴϴ�.
						// �� index���� lemma matching�� �����ϰ� ���˴ϴ�.
						lastMatchedIndex = patternMatcherIndex;
						if( lastMatchedIndex == numofLemma )
							repeated = false;
					}
					if( noMatched || repeated )
					{
						break;
					}
					// ���� ������ ������ �Ϸ�ƽ��ϴ�.
					// ���� �� ������ ��Ȯ�� �����ȿ� ������ ���⿡ �����߽��ϴ�.
					if( wordCnt == 0 ||
						( wordCnt != 0 && pattern.getWord(wordCnt-1).getNumofRepeat() == Repeat.ZERO_OR_MORE && lastMatchedIndex == numofLemma) )
					{
						StringTokenizer stSquareBracket = new StringTokenizer( ComplicatedQueryForms_frame_front.get(patternCnt).get(asSameCnt), "[]" );
						String patternResult = stSquareBracket.nextToken();
						
						if( CoreList.size() != 1 )
						{
						// ���� ��ü
							for( int i = CoreList.size()-1; i >= 0; i-- )
							{
								stSquareBracket = new StringTokenizer( ComplicatedQueryForms_frame_front.get(patternCnt).get(CoreListAssister.get(i)), "[]" );
								String Repeater;
								stSquareBracket.nextToken();
								Repeater = stSquareBracket.nextToken();
								patternResult += CoreList.get(i);
								if( i != 0 )
									patternResult += Repeater;
							}
						}
						else if( CoreList.size() == 1 )
							patternResult += CoreList.get(0);
						
						patternResult += ComplicatedQueryForms_frame_end.get(patternCnt);
						
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
		}
		if( result.compareTo("NoResult") == 0 )
			result = Analyze(sentence);
		
		return result;
	}
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
		for( int patternCnt = 0; patternCnt < SimplePatterns.size(); patternCnt++ )
		{
			Sentence pattern = SimplePatterns.get(patternCnt);
			patternMatcherIndex = -1;
			lastMatchedIndex = -1;
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
						patternMatcherIndex = DetectMatchingLemma(sentence, lemma.getPos());
						// ������ 0�̻� ���°Ÿ� sentence�� �ش� index��° lemma���� ���ڸ��� ���̸��� ����� ���Դϴ�.
						// �׷��� name���ڿ��� �̸��� �����صӴϴ�.
						if( patternMatcherIndex > 0 )
						{
							int lemmaIndex = numofLemma-patternMatcherIndex;
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
						patternMatcherIndex = DetectMatchingLemma(sentence, lemma.getPos());
						// ������ 0�̻� ���°Ÿ� sentence�� �ش� index��° lemma���� ���ڸ��� ���̸��� ����� ���Դϴ�.
						// �׷��� name���ڿ��� �̸��� �����صӴϴ�.
						if( patternMatcherIndex > 0 )
						{
							int lemmaIndex = numofLemma-patternMatcherIndex;
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
						patternMatcherIndex = DetectMatchingLemma(sentence, lemma.getPos());
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
						if( lemma.getText().charAt(0) != '@' )
							patternMatcherIndex = DetectMatchingLemma(sentence, lemma.getText() );
						else if( word.getNumofIgnore() > 0 )
						{
							Word nextWord = pattern.getWord(wordCnt-1);
							patternMatcherIndex = DetectMatchingWord(sentence, nextWord );
							if( patternMatcherIndex == -1 )
							{
								lastMatchedIndex--;
								patternMatcherIndex = lastMatchedIndex+1;
								word.decreaseNumofIgnore();
								wordCnt++;
							}
							else
							{
								lastMatchedIndex = patternMatcherIndex-1;
								word.setNumofIgnore(0);
							}
						}
						else
							lastMatchedIndex = patternMatcherIndex-1;
							
					}
					// �� ��° ����� text�� ǰ�� ������ ��� ��ġ�ϴ� ��츸 ����ϴ� ����Դϴ�.
					else
					{
						patternMatcherIndex = DetectMatchingLemma(sentence, lemma);
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
					String patternResult = SimpleQueryForms.get(patternCnt);
					
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
				result += lemma.getText() + "/" + lemma.getPos() + "(" + lemma.getRepeatID() + ")";
				if( lemmaCnt != word.getLemma().size()-1 )
					result += "+";
			}
			if( wordCnt != sentence.getWord().size()-1 )
				result += "_";
		}
		//System.out.println(result);
	}
	
	// ù ��° lemma matching ���� �ռ��Դϴ�.
	private int DetectMatchingLemma( Sentence sentence, Pos pos )
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
	private int DetectMatchingLemma( Sentence sentence, String text )
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
	private int DetectMatchingWord( Sentence sentence, Word nextWord )
	{
		int counter = 0;
		boolean havePattern = false;
		for( int i = 0; i < nextWord.getLemma().size(); i++ )
			if( nextWord.getLemma(i).getPos() == Pos.P )
				havePattern = true;
		
		for( int wordCnt = sentence.getWord().size()-1; wordCnt >= 0; wordCnt-- )
		{
			boolean findMatchedWord = true;
			Word word = sentence.getWord(wordCnt);
			if( word.getLemma().size() != nextWord.getLemma().size() )
			{
				counter += word.getLemma().size();
				findMatchedWord = false;
			}
			else
			{
				for( int lemmaCnt = word.getLemma().size()-1; lemmaCnt >= 0; lemmaCnt-- )
				{
					counter++;
					if( findMatchedWord == false )
						continue;
					
					boolean findMatchedLemma = false;
					Lemma leftLemma = nextWord.getLemma(lemmaCnt);
					Lemma rightLemma = word.getLemma(lemmaCnt);
					if( leftLemma.getText().compareTo("NAME") == 0 )
					{
						if( rightLemma.getPos() == Pos.C || rightLemma.getPos() == Pos.S )
							findMatchedLemma = true;
					}
					else if( leftLemma.getText().compareTo("VALUE1") == 0 )
					{
						if( rightLemma.getPos() == Pos.I )
							findMatchedLemma = true;
					}
					else if( leftLemma.getText().compareTo("VALUE2") == 0 )
					{
						if( rightLemma.getPos() == Pos.I )
							findMatchedLemma = true;
					}
					else if( leftLemma.getPos() == Pos.X )
					{
						if( rightLemma.getText().compareTo(leftLemma.getText()) == 0 )
							findMatchedLemma = true;
					}
					else if( leftLemma.getPos() == Pos.P )
					{
						findMatchedLemma = true;
					}
					else
					{
						if( rightLemma.getText().compareTo(leftLemma.getText()) == 0 && rightLemma.getPos() == leftLemma.getPos() )
							findMatchedLemma = true;
					}
					if( findMatchedLemma == false )
						findMatchedWord = false;
				}
			}
			if( counter > lastMatchedIndex )
			{
				if( findMatchedWord )
					return counter-word.getLemma().size();
				else
				{
					lastMatchedIndex = counter;
					return -1;
				}
			}
				
		}
		return -1;
	}
	// �� ��° lemma matching ���� �ռ��Դϴ�.
	private int DetectMatchingLemma( Sentence sentence, Lemma paramlemma )
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
		
		//System.out.println("������ lemma�� ��µ˴ϴ�.");
		return lemma;
	}
}
