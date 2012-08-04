package analyzerpack;

import java.io.*;
import java.util.ArrayList;
import java.util.StringTokenizer;

public class ResultAnalyzer {
	
	ArrayList<Sentence> Patterns;
	ArrayList<String> QueryForms;
	
	public ResultAnalyzer() {
		Patterns = new ArrayList<Sentence>();
		QueryForms = new ArrayList<String>();
		OrganizePattern();
	}
	
	private void OrganizePattern()
	{
		try
		{
			FileReader fr = new FileReader("patterns.txt");
			BufferedReader br = new BufferedReader(fr);
		
			String pattern;
			
			while( (pattern = br.readLine()) != null )
			{
				Sentence sentence = new Sentence("");
				String queryform;
				StringTokenizer stColon = new StringTokenizer( pattern, ":");
				queryform = stColon.nextToken();
				pattern = pattern.substring(queryform.length()+1);
				QueryForms.add(queryform);
				
				while( pattern.length() != 0 )
				{
					String text, pos;
					
					StringTokenizer stSlash = new StringTokenizer( pattern, "/");
					text = stSlash.nextToken();
					pattern = pattern.substring(text.length()+1);
					
					StringTokenizer stUnderbar = new StringTokenizer( pattern, "_");
					pos = stUnderbar.nextToken();
					if( stUnderbar.countTokens() != 0)
						pattern = pattern.substring(pos.length()+1);
					else
						pattern = "";
					
					sentence.addWord(text, text+"("+pos+")", 0);
				}
				Patterns.add(sentence);
			}
			
			br.close();
			fr.close();
		}
		catch(IOException ioe)
		{
			ioe.printStackTrace();
		}
		
		for( int iter = 0; iter < Patterns.size(); iter++ )
			PrintSentence(Patterns.get(iter));
	}
	
	public String Analyze(Sentence sentence)
	{
		String result = "NoResult";
		String name ="";
		boolean noMatched = false;
		int numofLemma = CountLemma(sentence);
		for( int patternCnt = 0; patternCnt < Patterns.size(); patternCnt++ )
		{
			Sentence pattern = Patterns.get(patternCnt);
			int patternMatcherIndex = -1;
			int lastMatchedIndex = -1;
			noMatched = false;
			for( int wordCnt = pattern.getWord().size()-1; wordCnt >= 0; wordCnt-- )
			{
				Word word = pattern.getWord(wordCnt);
				for( int lemmaCnt = word.getLemma().size()-1; lemmaCnt >= 0; lemmaCnt-- )
				{
					Lemma lemma = word.getLemma(lemmaCnt);
					if( lemma.getText().compareTo("NONAME") == 0 )
					{
						patternMatcherIndex = DetectMatchingLemma(sentence, lastMatchedIndex, lemma.getPos());
						if( patternMatcherIndex > 0 && patternMatcherIndex <= sentence.getWord().size() )
							name = sentence.getWord(numofLemma-patternMatcherIndex).getNativeText();
						else
							name = "??";
					}
					else if( lemma.getPos() == Pos.X )
					{
						patternMatcherIndex = DetectMatchingLemma(sentence, lastMatchedIndex, lemma.getText());
					}
					else
					{
						patternMatcherIndex = DetectMatchingLemma(sentence, lastMatchedIndex, lemma);
					}
					if( patternMatcherIndex == -1 )
						noMatched = true;
					if( noMatched )
						break;
					lastMatchedIndex = patternMatcherIndex;
				}
				if( noMatched )
					break;
				if( wordCnt == 0 )
				{
					String patternResult = QueryForms.get(patternCnt);
					
					patternResult = patternResult.replaceAll("NONAME", name);
					if( result == "NoResult" )
						result = patternResult;
					else
						result += "|" + patternResult;
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
	
	private int CountLemma(Sentence sentence)
	{
		int counter = 0;
		for( int wordCnt = 0; wordCnt < sentence.getWord().size(); wordCnt++ )
			for( int lemmaCnt = 0; lemmaCnt < sentence.getWord(wordCnt).getLemma().size(); lemmaCnt++ )
				counter++;
		return counter;
	}
}
