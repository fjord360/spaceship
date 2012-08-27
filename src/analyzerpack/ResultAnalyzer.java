package analyzerpack;

import java.io.*;
import java.util.ArrayList;
import java.util.StringTokenizer;

/* 
 * 형태소 분석 결과를 분석해주는 모듈
 * 생성자에서 패턴을 생성한 후
 * sentence를 Analyze함수의 인자로 받아와 분석합니
 */



public class ResultAnalyzer {
	
	ArrayList<Sentence> SimplePatterns;	// 패턴들을 담습니다.
	ArrayList<String> SimpleQueryForms;	// 패턴에 해당하는 쿼리문을 저장합니다. Patterns에 저장하는 순서가 같습니다.
	ArrayList<Sentence> ComplicatedPatterns_core;	// 
	ArrayList<String> ComplicatedQueryForms_core;	// 
	ArrayList<ArrayList<Sentence>> ComplicatedPatterns_frame;	// 
	ArrayList<ArrayList<String>> ComplicatedQueryForms_frame_front;	// 
	ArrayList<String> ComplicatedQueryForms_frame_end;	//
	int patternMatcherIndex;
	int lastMatchedIndex;
	
	// 생성자
	// 패턴을 구성해줍니다. 프로젝트 폴더(krmorph)에 patterns.txt가 있어야합니다.
	// 없으면 만드시길... 아니면 김정호씨를 찾아가세요
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
	
	// private이므로 OrganizePattern 밖에서 못부릅니다.
	// 부르지 마시고 생성자에서 패턴 생성하게 해주세요!
	private void OrganizePattern()
	{
		try
		{
			// 프로젝트 폴더(krmorph)에 꼭 있어야합니다.
			FileReader fr = new FileReader("simple_patterns.txt");
			BufferedReader br = new BufferedReader(fr);
		
			String pattern;
			
			// 패턴을 한 줄씩 수집합니다.
			while( (pattern = br.readLine()) != null )
			{
				if( pattern.compareTo("") == 0 || pattern.substring(0, 2).compareTo("//") == 0 )
					continue;
				
				Sentence sentence = new Sentence("");
				String queryform;
				// 먼저 쿼리를 수집합니다. :를 기준으로 수집합니다.
				StringTokenizer stColon = new StringTokenizer( pattern, ":");
				queryform = stColon.nextToken();
				pattern = pattern.substring(queryform.length()+1);
				SimpleQueryForms.add(queryform);
				
				ArrayList<String> tempWords = new ArrayList<String>();
				StringTokenizer stUnderbar = new StringTokenizer( pattern, "_");
				// 임시로 _를 기준으로 잘라서 보관합니다. word단위이므로 lemma단위로 잘라줘야합니다.
				while( stUnderbar.countTokens() != 0 )
					tempWords.add( stUnderbar.nextToken() );
				
				// word단위를 lemma단위로 자르기 위해 다시 +를 기준으로 자릅니다.
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
					// lemma가 완성되는 순간이죠. word단위의 처리가 끝났습니다.
					sentence.addWord( nativeText, analysedText, 0, 0, 0, numofIgnore, Repeat.NOMORE );
				}
				// 다 돌고나온 sentence는 하나의 패턴이 됩니다. 패턴 수집합니다.
				SimplePatterns.add(sentence);
			}
			
			//System.out.println("패턴 종류 : " + SimplePatterns.size());
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
			// 프로젝트 폴더(krmorph)에 꼭 있어야합니다.
			FileReader fr = new FileReader("complicated_patterns_core.txt");
			BufferedReader br = new BufferedReader(fr);
		
			String pattern;
			
			// 패턴을 한 줄씩 수집합니다.
			while( (pattern = br.readLine()) != null )
			{
				if( pattern.compareTo("") == 0 || pattern.substring(0, 2).compareTo("//") == 0 )
					continue;
				
				Sentence sentence = new Sentence("");
				String queryform;
				// 먼저 쿼리를 수집합니다. :를 기준으로 수집합니다.
				StringTokenizer stColon = new StringTokenizer( pattern, ":");
				queryform = stColon.nextToken();
				pattern = pattern.substring(queryform.length()+1);
				ComplicatedQueryForms_core.add(queryform);
				
				ArrayList<String> tempWords = new ArrayList<String>();
				StringTokenizer stUnderbar = new StringTokenizer( pattern, "_");
				// 임시로 _를 기준으로 잘라서 보관합니다. word단위이므로 lemma단위로 잘라줘야합니다.
				while( stUnderbar.countTokens() != 0 )
					tempWords.add( stUnderbar.nextToken() );
				
				// word단위를 lemma단위로 자르기 위해 다시 +를 기준으로 자릅니다.
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
					// lemma가 완성되는 순간이죠. word단위의 처리가 끝났습니다.
					sentence.addWord( nativeText, analysedText, 0, 0, 0, numofIgnore, Repeat.NOMORE );
				}
				// 다 돌고나온 sentence는 하나의 패턴이 됩니다. 패턴 수집합니다.
				ComplicatedPatterns_core.add(sentence);
			}
			
			//System.out.println("패턴 종류 : " + ComplicatedPatterns_core.size());
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
			// 프로젝트 폴더(krmorph)에 꼭 있어야합니다.
			FileReader fr = new FileReader("complicated_patterns_frame.txt");
			BufferedReader br = new BufferedReader(fr);
		
			String pattern;
			
			// 패턴을 한 줄씩 수집합니다.
			while( (pattern = br.readLine()) != null )
			{
				if( pattern.compareTo("") == 0 || pattern.substring(0, 2).compareTo("//") == 0 )
					continue;
				
				ArrayList<String> queryformList = new ArrayList<String>();
				ArrayList<Sentence> sentenceList = new ArrayList<Sentence>();
				//Sentence sentence = new Sentence("");
				String queryform, frontQuery, endQuery;
				// 먼저 쿼리를 수집합니다. :를 기준으로 수집합니다.
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
				// 임시로 _를 기준으로 잘라서 보관합니다. word단위이므로 lemma단위로 잘라줘야합니다.
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
				// word단위를 lemma단위로 자르기 위해 다시 +를 기준으로 자릅니다.
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
								// lemma가 완성되는 순간이죠. word단위의 처리가 끝났습니다.
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
						// lemma가 완성되는 순간이죠. word단위의 처리가 끝났습니다.
						for( int sentenceCnt = 0;  sentenceCnt < sentenceList.size(); sentenceCnt++ )
							sentenceList.get(sentenceCnt).addWord( nativeText, analysedText, 0, 0, 0, numofIgnore, Repeat.NOMORE );
					}
				}
				for( int sentenceCnt = 0;  sentenceCnt < sentenceList.size(); sentenceCnt++ )
					sentenceList.get(sentenceCnt).setRepeatID(lemmaRepeatIDs.get(sentenceCnt));
				// 다 돌고나온 sentence는 하나의 패턴이 됩니다. 패턴 수집합니다.
//				for( int divisionCnt = 0; divisionCnt < numofDivision; divisionCnt++ )
//				{
//					ComplicatedPatterns_frame.add(sentenceList.get(divisionCnt));
//				}
				ComplicatedPatterns_frame.add(sentenceList);
			}
			
			//System.out.println("패턴 종류 : " + ComplicatedPatterns_frame.size());
			br.close();
			fr.close();
		}
		catch(IOException ioe)
		{
			ioe.printStackTrace();
			System.out.println(System.getProperty("user.dir"));
		}
		
		// 패턴이 제대로 구성됐는지 한 줄씩 출력하면서 검증합니다.
		for( int iter = 0; iter < SimplePatterns.size(); iter++ )
			PrintSentence(SimplePatterns.get(iter));
		
		// 패턴이 제대로 구성됐는지 한 줄씩 출력하면서 검증합니다.
		for( int iter = 0; iter < ComplicatedPatterns_core.size(); iter++ )
			PrintSentence(ComplicatedPatterns_core.get(iter));
		
		// 패턴이 제대로 구성됐는지 한 줄씩 출력하면서 검증합니다.
		for( int iter = 0; iter < ComplicatedPatterns_frame.size(); iter++ )
			for( int iter2 = 0; iter2 < ComplicatedPatterns_frame.get(iter).size(); iter2++ )
				PrintSentence(ComplicatedPatterns_frame.get(iter).get(iter2));
	}
	
	public String ExAnalyze_Core(Sentence sentence)
	{
		// 아마 하다보면 여기서 NoResult가 결과로 많이 나올 수 있다고 생각합니다.
		// 그런경우는 패턴 매치가 실패하는 경우가 되는데요.
		// 디버깅해보려면 현재 boolean값인 noMatched로 걸리는 구간에 중단점을 찍고
		// 걸리게 되면 그 상황에서 어떤 값에서 판단미스가 났는지 보시면 됩니다.
		String result = "NoResult";
		String name = "";
		String value1 = "", value2 = "";
		boolean noMatched = false;
		int numofLemma = CountLemma(sentence);
		int initPatternMatcherIndex = patternMatcherIndex;
		int initLastMatchedIndex = lastMatchedIndex;
		// 첫 번째 패턴부터 순서대로 검사합니다.
		for( int patternCnt = 0; patternCnt < ComplicatedPatterns_core.size(); patternCnt++ )
		{
			Sentence pattern = ComplicatedPatterns_core.get(patternCnt);
			patternMatcherIndex = initPatternMatcherIndex;
			lastMatchedIndex = initLastMatchedIndex;
			noMatched = false;
			
			// 패턴중 가장 뒷부분 lemma부터 검사하는 루틴입니다.
			for( int wordCnt = pattern.getWord().size()-1; wordCnt >= 0; wordCnt-- )
			{
				Word word = pattern.getWord(wordCnt);
				for( int lemmaCnt = word.getLemma().size()-1; lemmaCnt >= 0; lemmaCnt-- )
				{
					Lemma lemma = word.getLemma(lemmaCnt);
					
					// 총 세종류의 검사방법이 있습니다.
					// 첫 번째 방법은 text는 검사하지 않지만 품사를 기준으로 Matching해주는 함수를 사용하는 방법입니다.
					if( lemma.getText().compareTo("NAME") == 0 )
					{
						patternMatcherIndex = DetectMatchingLemma(sentence, lemma.getPos());
						// 패턴이 0이상 나온거면 sentence의 해당 index번째 lemma에서 별자리나 별이름이 검출된 것입니다.
						// 그래서 name문자열에 이름을 저장해둡니다.
						if( patternMatcherIndex > 0 )
						{
							int lemmaIndex = numofLemma-patternMatcherIndex;
							name = GetLemma( sentence, lemmaIndex ).getText();
						}
						// 이 때 무조건 인덱스가 0이상이 나와야 정상인데, else에 걸렸다면 결과값으로 -1이 반환된 것입니다.
						// 이는 데이터를 못찾은 것이므로 프로그램 짠 김정호에게 문제가 있습니다.
						// 꼭 문의하십시오!
						else
							name = "??";
					}
					else if( lemma.getText().compareTo("VALUE1") == 0 )
					{
						patternMatcherIndex = DetectMatchingLemma(sentence, lemma.getPos());
						// 패턴이 0이상 나온거면 sentence의 해당 index번째 lemma에서 별자리나 별이름이 검출된 것입니다.
						// 그래서 name문자열에 이름을 저장해둡니다.
						if( patternMatcherIndex > 0 )
						{
							int lemmaIndex = numofLemma-patternMatcherIndex;
							value1 = GetLemma( sentence, lemmaIndex ).getText();
						}
						// 이 때 무조건 인덱스가 0이상이 나와야 정상인데, else에 걸렸다면 결과값으로 -1이 반환된 것입니다.
						// 이는 데이터를 못찾은 것이므로 프로그램 짠 김정호에게 문제가 있습니다.
						// 꼭 문의하십시오!
						else
							value1 = "??";
					}
					else if( lemma.getText().compareTo("VALUE2") == 0 )
					{
						patternMatcherIndex = DetectMatchingLemma(sentence, lemma.getPos());
						// 패턴이 0이상 나온거면 sentence의 해당 index번째 lemma에서 별자리나 별이름이 검출된 것입니다.
						// 그래서 name문자열에 이름을 저장해둡니다.
						if( patternMatcherIndex > 0 )
						{
							int lemmaIndex= numofLemma-patternMatcherIndex;
							value2 = GetLemma( sentence, lemmaIndex ).getText();
						}
						// 이 때 무조건 인덱스가 0이상이 나와야 정상인데, else에 걸렸다면 결과값으로 -1이 반환된 것입니다.
						// 이는 데이터를 못찾은 것이므로 프로그램 짠 김정호에게 문제가 있습니다.
						// 꼭 문의하십시오!
						else
							value2 = "??";
					}
					// 두 번째 방법은 품사는 검사하지 않지만 text를 기준으로 Matching해주는 함수를 사용하는 방법입니다.
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
					// 세 번째 방법은 text와 품사 데이터 모두 일치하는 경우만 허용하는 방법입니다.
					else
					{
						patternMatcherIndex = DetectMatchingLemma(sentence, lemma);
					}
					// 세 가지 방법중 하나라도 검출해서 -1이 나온다면 현재 이 패턴은 match될 수 없다고 판단한 것입니다.
					// 현재 패턴을 중단하고 다음 패턴으로 넘어가야합니다.
					if( patternMatcherIndex == -1 || 
						( lastMatchedIndex != -1 && patternMatcherIndex != lastMatchedIndex+1 ) )
						noMatched = true;
					if( noMatched )
						break;
					// 만약 패턴 match에 성공했다면 가장 최근에 매치된 index값을 알기 위해 저장해둡니다.
					// 이 index값이 lemma matching에 유용하게 사용됩니다.
					lastMatchedIndex = patternMatcherIndex;
				}
				// 세 가지 방법중 하나라도 검출해서 -1이 나온다면 현재 이 패턴은 match될 수 없다고 판단한 것입니다.
				// 현재 패턴을 중단하고 다음 패턴으로 넘어가야합니다.
				if( noMatched )
					break;
				
				// 패턴 검출이 완전히 완료됐습니다.
				// 현재 이 패턴은 정확히 문구안에 있으며 검출에 성공했습니다.
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
	
	// 본격적으로 sentence로 받아온 형태소 분석결과를 다시 분석해서 쿼리로 만들어 보내줍니다.
	// 여기서 많은 패턴들로 점검합니다.
	// 중복 패턴 가능하며, 쿼리는 하나이상 전달 가능합니다. 현재는 |로 구분가능하게 되어있습니다.
	// 결과는 String으로 반환됩니다. 차후에 쿼리들의 배열로 반환될 가능성도 있습니다.
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
						// 두 번째 방법은 품사는 검사하지 않지만 text를 기준으로 Matching해주는 함수를 사용하는 방법입니다.
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
						// 세 번째 방법은 text와 품사 데이터 모두 일치하는 경우만 허용하는 방법입니다.
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
						// 만약 패턴 match에 성공했다면 가장 최근에 매치된 index값을 알기 위해 저장해둡니다.
						// 이 index값이 lemma matching에 유용하게 사용됩니다.
						lastMatchedIndex = patternMatcherIndex;
						if( lastMatchedIndex == numofLemma )
							repeated = false;
					}
					if( noMatched || repeated )
					{
						break;
					}
					// 패턴 검출이 완전히 완료됐습니다.
					// 현재 이 패턴은 정확히 문구안에 있으며 검출에 성공했습니다.
					if( wordCnt == 0 ||
						( wordCnt != 0 && pattern.getWord(wordCnt-1).getNumofRepeat() == Repeat.ZERO_OR_MORE && lastMatchedIndex == numofLemma) )
					{
						StringTokenizer stSquareBracket = new StringTokenizer( ComplicatedQueryForms_frame_front.get(patternCnt).get(asSameCnt), "[]" );
						String patternResult = stSquareBracket.nextToken();
						
						if( CoreList.size() != 1 )
						{
						// 쿼리 교체
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
		// 아마 하다보면 여기서 NoResult가 결과로 많이 나올 수 있다고 생각합니다.
		// 그런경우는 패턴 매치가 실패하는 경우가 되는데요.
		// 디버깅해보려면 현재 boolean값인 noMatched로 걸리는 구간에 중단점을 찍고
		// 걸리게 되면 그 상황에서 어떤 값에서 판단미스가 났는지 보시면 됩니다.
		String result = "NoResult";
		String name = "";
		String value1 = "", value2 = "";
		boolean noMatched = false;
		int numofLemma = CountLemma(sentence);
		
		// 첫 번째 패턴부터 순서대로 검사합니다.
		for( int patternCnt = 0; patternCnt < SimplePatterns.size(); patternCnt++ )
		{
			Sentence pattern = SimplePatterns.get(patternCnt);
			patternMatcherIndex = -1;
			lastMatchedIndex = -1;
			noMatched = false;
			
			// 패턴중 가장 뒷부분 lemma부터 검사하는 루틴입니다.
			for( int wordCnt = pattern.getWord().size()-1; wordCnt >= 0; wordCnt-- )
			{
				Word word = pattern.getWord(wordCnt);
				for( int lemmaCnt = word.getLemma().size()-1; lemmaCnt >= 0; lemmaCnt-- )
				{
					Lemma lemma = word.getLemma(lemmaCnt);
					
					// 총 세종류의 검사방법이 있습니다.
					// 첫 번째 방법은 text는 검사하지 않지만 품사를 기준으로 Matching해주는 함수를 사용하는 방법입니다.
					if( lemma.getText().compareTo("NAME") == 0 )
					{
						patternMatcherIndex = DetectMatchingLemma(sentence, lemma.getPos());
						// 패턴이 0이상 나온거면 sentence의 해당 index번째 lemma에서 별자리나 별이름이 검출된 것입니다.
						// 그래서 name문자열에 이름을 저장해둡니다.
						if( patternMatcherIndex > 0 )
						{
							int lemmaIndex = numofLemma-patternMatcherIndex;
							name = GetLemma( sentence, lemmaIndex ).getText();
						}
						// 이 때 무조건 인덱스가 0이상이 나와야 정상인데, else에 걸렸다면 결과값으로 -1이 반환된 것입니다.
						// 이는 데이터를 못찾은 것이므로 프로그램 짠 김정호에게 문제가 있습니다.
						// 꼭 문의하십시오!
						else
							name = "??";
					}
					else if( lemma.getText().compareTo("VALUE1") == 0 )
					{
						patternMatcherIndex = DetectMatchingLemma(sentence, lemma.getPos());
						// 패턴이 0이상 나온거면 sentence의 해당 index번째 lemma에서 별자리나 별이름이 검출된 것입니다.
						// 그래서 name문자열에 이름을 저장해둡니다.
						if( patternMatcherIndex > 0 )
						{
							int lemmaIndex = numofLemma-patternMatcherIndex;
							value1 = GetLemma( sentence, lemmaIndex ).getText();
						}
						// 이 때 무조건 인덱스가 0이상이 나와야 정상인데, else에 걸렸다면 결과값으로 -1이 반환된 것입니다.
						// 이는 데이터를 못찾은 것이므로 프로그램 짠 김정호에게 문제가 있습니다.
						// 꼭 문의하십시오!
						else
							value1 = "??";
					}
					else if( lemma.getText().compareTo("VALUE2") == 0 )
					{
						patternMatcherIndex = DetectMatchingLemma(sentence, lemma.getPos());
						// 패턴이 0이상 나온거면 sentence의 해당 index번째 lemma에서 별자리나 별이름이 검출된 것입니다.
						// 그래서 name문자열에 이름을 저장해둡니다.
						if( patternMatcherIndex > 0 )
						{
							int lemmaIndex= numofLemma-patternMatcherIndex;
							value2 = GetLemma( sentence, lemmaIndex ).getText();
						}
						// 이 때 무조건 인덱스가 0이상이 나와야 정상인데, else에 걸렸다면 결과값으로 -1이 반환된 것입니다.
						// 이는 데이터를 못찾은 것이므로 프로그램 짠 김정호에게 문제가 있습니다.
						// 꼭 문의하십시오!
						else
							value2 = "??";
					}
					// 두 번째 방법은 품사는 검사하지 않지만 text를 기준으로 Matching해주는 함수를 사용하는 방법입니다.
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
					// 세 번째 방법은 text와 품사 데이터 모두 일치하는 경우만 허용하는 방법입니다.
					else
					{
						patternMatcherIndex = DetectMatchingLemma(sentence, lemma);
					}
					// 세 가지 방법중 하나라도 검출해서 -1이 나온다면 현재 이 패턴은 match될 수 없다고 판단한 것입니다.
					// 현재 패턴을 중단하고 다음 패턴으로 넘어가야합니다.
					if( patternMatcherIndex == -1 || 
						( lastMatchedIndex != -1 && patternMatcherIndex != lastMatchedIndex+1 ) )
						noMatched = true;
					if( noMatched )
						break;
					// 만약 패턴 match에 성공했다면 가장 최근에 매치된 index값을 알기 위해 저장해둡니다.
					// 이 index값이 lemma matching에 유용하게 사용됩니다.
					lastMatchedIndex = patternMatcherIndex;
				}
				// 세 가지 방법중 하나라도 검출해서 -1이 나온다면 현재 이 패턴은 match될 수 없다고 판단한 것입니다.
				// 현재 패턴을 중단하고 다음 패턴으로 넘어가야합니다.
				if( noMatched )
					break;
				
				// 패턴 검출이 완전히 완료됐습니다.
				// 현재 이 패턴은 정확히 문구안에 있으며 검출에 성공했습니다.
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
	
	// sentence단위로 출력해주는 함수입니다. 
	// 외부에서 사용할 수 없게 설계합니다.
	// ... 뭐하러 출력합니까.. 굳이..
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
	
	// 첫 번째 lemma matching 검출 합수입니다.
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
	
	// 두 번째 lemma matching 검출 합수입니다.
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
	
	// 두 번째 lemma matching 검출 합수입니다.
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
	// 세 번째 lemma matching 검출 합수입니다.
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
	
	// sentence에 lemma가 몇 개 들어있는지 세는 함수입니다.
	private int CountLemma( Sentence sentence )
	{
		int counter = 0;
		for( int wordCnt = 0; wordCnt < sentence.getWord().size(); wordCnt++ )
			for( int lemmaCnt = 0; lemmaCnt < sentence.getWord(wordCnt).getLemma().size(); lemmaCnt++ )
				counter++;
		return counter;
	}
	
	// 해당 index번째 lemma를 불러옵니다. 0부터 count합니다.
	private Lemma GetLemma( Sentence sentence, int lemmaIndex )
	{
		int counter = 0;
		Lemma lemma = new Lemma("수상한Lemma", Pos.X);
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
		
		//System.out.println("수상한 lemma가 출력됩니다.");
		return lemma;
	}
}
