package de.smileCrew.MrGrama;

public class Riddle {
	private String sentence;
	private String[] options;
	private int solutionOption;
	private int solutionPos;
	private String riddleSentence;
	private boolean solved;
	
	public Riddle(String sentence) {
		this.setSentence(sentence);
	}
	public Riddle(String sentence, boolean solved) {
		this.setSentence(sentence);
		this.solved = solved;
	}
	
	public static Riddle emptyRiddle(String show) {
		Riddle emptyRiddle = new Riddle(show);
		emptyRiddle.setOption(new String[] {"", "", "", ""});
		emptyRiddle.setRiddleSentence(show);
		return emptyRiddle;
	}

	public String getSentence() {
		return sentence;
	}
	public void setSentence(String sentence) {
		this.sentence = sentence;
	}

	public String getRiddleSentence() {
		return riddleSentence;
	}
	public void setRiddleSentence(String riddleSentence) {
		this.riddleSentence = riddleSentence;
	}

	public String[] getOptions() {
		return options;
	}
	public void setOption(String[] options) {
		this.options = options;
	}
	public int getSolutionOption() {
		return solutionOption;
	}
	public void setSolutionOption(int solutionOption) {
		this.solutionOption = solutionOption;
	}

	public int getSolutionPos() {
		return solutionPos;
	}
	public void setSolutionPos(int solutionPos) {
		this.solutionPos = solutionPos;
	}

	public String getSolutionWord() {
		return options[solutionOption];
	}
	public boolean check(int solOption, int solPos) {
		return solOption==solutionOption && solPos == solutionPos;
	}

	public String getOption1() {
		return options[0];
	}
	public String getOption2() {
		return options[1];
	}
	public String getOption3() {
		return options[2];
	}
	public String getOption4() {
		return options[3];
	}
	
	public boolean alreadySolved() {
		return solved;
	}
	public void solved() {
		this.solved = true;
	}
}
