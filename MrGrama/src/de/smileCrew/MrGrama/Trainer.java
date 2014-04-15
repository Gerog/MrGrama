package de.smileCrew.MrGrama;


import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import android.content.Context;
import android.content.res.AssetManager;

class Trainer {
	protected static boolean DEBUG = true;

	protected static String[] filter;
//	protected static String[] vocabulary;
//	protected static HashMap<String,ArrayList<String>> phrasebook;
	protected static DBVocabularyResource resource;
	private static Random zufall;

	public static void start(Context context) {
		resource = new DBVocabularyResource();
		resource.open(context);
		getFilter();
		zufall = new Random();
	}
	private static void getFilter() {
		filter = resource.getAllFilters();
	}
	@SuppressWarnings("unchecked")
	public static HashMap<String,ArrayList<String>> loadDefaultPhrases(Context context) {
		String[] filter;
		String[] vocabulary;
		HashMap<String,ArrayList<String>> phrasebook;
		InputStream fis;
		ObjectInputStream in;
		
		zufall = new Random();

		AssetManager asm = context.getAssets();
		

		try {
			fis = asm.open("MisterGrama.flt");
			in = new ObjectInputStream(fis);
			filter = (String[])in.readObject();
			if (filter.length == 0) filter = new String[] {"mich", "mir", "dich", "dir", "sich", "ihn", "ihr", "ihm", "uns", "euch", "sie", "ihnen"};
			in.close();
			if (DEBUG) System.out.println("Trainer: .flt read successfully - " + filter.length);
		}
		catch(IOException ex) {
			if (DEBUG) ex.printStackTrace();
			filter = new String[] {"mich", "mir", "dich", "dir", "sich", "ihn", "ihr", "ihm", "uns", "euch", "sie", "ihnen"};
		}
		catch(ClassNotFoundException ex) {
			if (DEBUG) ex.printStackTrace();
			filter = new String[] {"mich", "mir", "dich", "dir", "sich", "ihn", "ihr", "ihm", "uns", "euch", "sie", "ihnen"};
		}
		
		try {
			fis = asm.open("MisterGrama.phr");
			in = new ObjectInputStream(fis);
			phrasebook = (HashMap<String,ArrayList<String>>)in.readObject();
			in.close();
			//mainwin.startButtonActionPerformed(null);
			if (DEBUG) System.out.println("Trainer: .phr read successfully - " + phrasebook.size());
		}
		catch(IOException ex) {
			if (DEBUG) ex.printStackTrace();
			phrasebook = new HashMap<String,ArrayList<String>> ();
		}
		catch(ClassNotFoundException ex) {
			if (DEBUG) ex.printStackTrace();
			phrasebook = new HashMap<String,ArrayList<String>> ();
		}
		try {
			fis = asm.open("MisterGrama.voc");
			in = new ObjectInputStream(fis);
			vocabulary = (String[])in.readObject();
			in.close();
			if (DEBUG) System.out.println("Trainer: .voc read successfully - " + vocabulary.length);
		}
		catch(IOException ex) {
			if (DEBUG) ex.printStackTrace();
			vocabulary = new String[] {"machen", "sagen", "geben"};
//			updatePhraseBook();
		}
		catch(ClassNotFoundException ex) {
			if (DEBUG) ex.printStackTrace();
			vocabulary = new String[] {"machen", "sagen", "geben"};
//			updatePhraseBook();
		}

		ArrayList<String> fltr = new ArrayList<String>();
		for (int i=0; i<filter.length; i++) {
			fltr.add(filter[i]);
		}
		ArrayList<String> vocs = new ArrayList<String>();
		for (int i=0; i<vocabulary.length; i++) {
			vocs.add(vocabulary[i]);
		}
		phrasebook.put("fltr", fltr);
		phrasebook.put("vcs", vocs);
		
		return phrasebook;
//		shuffle();
	}
	
//	public void updateVocabulary(String[] vocabulary) {
//		this.vocabulary = vocabulary;
//		
//		updatePhraseBook();
//	}
//	public void updateFilter(String[] filter) {
//		this.filter = filter;
//	}
	
//	private void updatePhraseBook() {
//		phrasebook.clear();
//		
//		for (int i=0; i<vocabulary.length; i++) {
//			if (DEBUG) System.out.println("vocabulary["+i+"]="+vocabulary[i]);
//			phrasebook.put(vocabulary[i], phraseParser.getPhrases(vocabulary[i]));
//		}		
//	}
//
//	public boolean wordExistence(String word) {
//		return phraseParser.wordExists(word);
//	}
	
//	public static String getRandomSentence() {
//		if (phrasebook == null || phrasebook.isEmpty()) return null;
//		int vocIndex = zufall.nextInt(vocabulary.length);
//			if (DEBUG) System.out.println("Trainer: vocabulary.length="+vocabulary.length);
//			if (DEBUG) System.out.println("Trainer: vocIndex="+vocIndex);
//		ArrayList<String> phraselist = phrasebook.get(vocabulary[zufall.nextInt(vocabulary.length)]);
//		if (phraselist == null || phraselist.isEmpty()) return null;
//		int phrIndex = zufall.nextInt(phraselist.size());
//			if (DEBUG) System.out.println("Trainer: phrIndex="+phrIndex);
//		return phraselist.get(phrIndex);
//	}
	public static String[] getVocabularyList() {
		return resource.getAllVocabularies(null, null);
	}
	public static String[] getVocabularyList(ArrayList<Integer> ids, ArrayList<Boolean> checked) {
		return resource.getAllVocabularies(ids, checked);
	}
//	public static ListAdapter connectVocabularyList(Context context, int layout, int to) {
//		return resource.getAllVocabularies(context, layout, to);
//	}
	public static void setVocabularyActivated(int id, boolean checked) {
		if (DEBUG) System.out.println("Setting "+ id + " " + ((checked)? "checked":"unchecked"));
		resource.updateVocabularyActivated(id, checked);
	}
	public static void shuffleList() {
		resource.shuffle();
	}
	
	public static int getListSize() {
		return resource.getSequenceSize();
	}
	public static int getListPosition() {
		return resource.getSequencePosition()+1;
	}
	
	public static int[][] searchFilterWords(String sentence){
		// Searching and comparing words;
		if (sentence == null || sentence.length() == 0) return null;
		int[][] founds = new int[wordCount(sentence)+1][2];	// Creating array of filter appearances (size is at most word count)
		int l=0;
		for(int i=0; i<filter.length; i++) {	// for each filter word 
			int j=0;
			while (sentence.indexOf(" "+filter[i]+" ", j) != -1) {
				j = sentence.indexOf(filter[i], j);	// get next appearance
				founds[1+l][0] = i;					// filter word index
				founds[1+l++][1] = j;					// position
				j++;
			}
		}
		founds[0][0] = l;
		return founds;
	}
	public static boolean hasNextRiddle() { return resource.hasNext(); }
	public static boolean hasPreviousRiddle() { return resource.hasPrevious(); }
	public static Riddle getNextRiddle() {
		String sentence = resource.getNextSentence();
		if (sentence == null) return null;
		if (resource.getCurrentSolved()) return new Riddle(sentence, true);
		else return getRiddle(sentence);
	}
	public static Riddle getPreviousRiddle() {
		String sentence = resource.getPreviousSentence();
		if (sentence == null) return null;
		if (resource.getCurrentSolved()) return new Riddle(sentence, true);
		else return getRiddle(sentence);
	}
	public static Riddle getRiddle() {
		String sentence = resource.getSentence();
		if (sentence == null) return null;
		if (resource.getCurrentSolved()) return new Riddle(sentence, true);
		return getRiddle(sentence);
	}
	public static Riddle getRiddle(String sentence) {
		int[][] founds = null;
		if (filter.length < 4) return null;
		if (sentence == null || sentence.length() == 0) return null;
			if (DEBUG) System.out.println("Trainer: " +sentence);
		
		founds = searchFilterWords(sentence);

		Riddle riddle = new Riddle(sentence);
		int f = zufall.nextInt(founds[0][0])+1;
			if (DEBUG) System.out.println("Trainer: "+sentence);
			if (DEBUG) System.out.println("Trainer: l:"+founds[0][0]+" ,f:"+f);
			if (DEBUG) System.out.println("Trainer: "+filter[founds[f][0]]);
		riddle.setRiddleSentence(stripDoubleSpace(sentence.substring(0, founds[f][1]) + sentence.substring(founds[f][1]+filter[founds[f][0]].length(), sentence.length())));
			if (DEBUG) System.out.println("Trainer: "+riddle.getRiddleSentence());
			
		int solutionIndex = zufall.nextInt(4);
		riddle.setSolutionOption(solutionIndex);
		
		String[] options = new String[4]; 
		options[solutionIndex] = filter[founds[f][0]];
			if (DEBUG) System.out.println("Trainer: Loesung: "+solutionIndex+" "+ filter[founds[f][0]]);
		int option1 = zufall.nextInt(filter.length - 1);
		if (option1 >= founds[f][0]) option1++;
		int option2 = zufall.nextInt(filter.length - 2);
		if (option2 >= Math.min(founds[f][0], option1)) option2++;
		if (option2 >= Math.max(founds[f][0], option1)) option2++;
		int option3 = zufall.nextInt(filter.length - 3);
		int min = Math.min(founds[f][0], Math.min(option1,option2));
		int max = Math.max(founds[f][0], Math.max(option1,option2));
		if (option3 >= min) option3++;
		if (option3 >= founds[f][0] + option1 + option2 - min - max) option3++;
		if (option3 >= max) option3++;
			if (DEBUG) System.out.println("Trainer: Antwort "+ (((solutionIndex>0)? 0 : 1)) + " ist "+ filter[option1]);
			if (DEBUG) System.out.println("Trainer: Antwort "+ (1 + ((solutionIndex>0)? 0 : 1)) + " ist "+ filter[option2]);
			if (DEBUG) System.out.println("Trainer: Antwort "+ (2 + ((solutionIndex>0)? 0 : 1)) + " ist "+ filter[option3]);
		options[((solutionIndex>0)? 0 : 1)] = filter[option1];
		options[1 + ((solutionIndex>1)? 0 : 1)] = filter[option2];
		options[2 + ((solutionIndex>2)? 0 : 1)] = filter[option3];
		riddle.setOption(options);
		riddle.setSolutionPos(wordCount(sentence.substring(0, founds[f][1]))); 
		return riddle;
	}
	public static void riddleSolved() {
		resource.setCurrentSolved(true);
	}
	
	private static int charCount(String s, char c) {
		int l=0;
		for(int i=0; i<s.length(); i++) if (s.charAt(i)==' ') l++;
		
		return l;
	}
	public static int wordCount(String s)  {
		return charCount(stripDoubleSpace(s), ' ') + 1;
	}
	
	public static String stripDoubleSpace(String str) {
		while (str.contains("  ")) str = str.replace("  ", " ");
		return str.trim();
	}
}
