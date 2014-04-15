package de.smileCrew.MrGrama;


import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class MrGrama extends Activity implements OnTouchListener{
	private static final int FONT_SIZE = 18;
	public static final String VOCABULARY = "de.smileCrew.MrGrama2.VOCABULARY";
	
	private int _xDelta;
	private int _yDelta;
	ViewGroup _root;
	private int wordPosition;
	private boolean correct;
	
	private LinearLayout textSentence;
	private PhraseLayout phraseLines;
	private TextView[] wordView;
	private TextView labelOption1;
	private TextView labelOption2;
	private TextView labelOption3;
	private TextView labelOption4;
	private ImageView nextButton;
	private ImageView prevButton;
	private TextView counterText;

	
	private Riddle riddle;

	final boolean DEBUG = true;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		if (DEBUG) System.out.println("startin");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_mr_grama);
		
		textSentence = (LinearLayout)findViewById(R.id.phraseLayout);
		labelOption1 = (TextView)findViewById(R.id.answer1);
		labelOption1.setTag(Integer.valueOf(0));
		labelOption2 = (TextView)findViewById(R.id.answer2);
		labelOption2.setTag(Integer.valueOf(1));
		labelOption3 = (TextView)findViewById(R.id.answer3);
		labelOption3.setTag(Integer.valueOf(2));
		labelOption4 = (TextView)findViewById(R.id.answer4);
		labelOption4.setTag(Integer.valueOf(3));
		
		labelOption1.setOnTouchListener(this);
		labelOption2.setOnTouchListener(this);
		labelOption3.setOnTouchListener(this);
		labelOption4.setOnTouchListener(this);
		
		phraseLines = new PhraseLayout(this);		
		_root = (ViewGroup)findViewById(R.id.rootLayout);
		
		nextButton = (ImageView) findViewById(R.id.forwardButton);
		prevButton = (ImageView) findViewById(R.id.backwardButton);
		
		counterText = (TextView) findViewById(R.id.counterText);

		nextButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				nextRiddle();
			}
		});
		prevButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				previousRiddle();
			}
		});
		textSentence.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				if (correct) nextRiddle();
			}
		});

		if (DEBUG) System.out.println("starting resource");
		Trainer.start(this);
//		if (DEBUG) System.out.println("loading resource");
		if (DEBUG) System.out.println("resource ready");
		
		try {
			riddle = Trainer.getRiddle();
		} catch (Exception e) {
			if (DEBUG) e.printStackTrace();
		}
		if (riddle == null) riddle = Riddle.emptyRiddle(" NO SUITIBLE PHRASES FOUND! ");

		if (!Trainer.hasPreviousRiddle()) prevButton.setVisibility(View.INVISIBLE);
		if (!Trainer.hasNextRiddle()) nextButton.setVisibility(View.INVISIBLE);
		startRiddle();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.mr_grama, menu);
		return true;
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle item selection
	    switch (item.getItemId()) {
	        case R.id.action_settings:
	        	Intent settingsIntend = new Intent(getBaseContext(), SettingsActivity.class);
	        	startActivity(settingsIntend);
	        	return true;
	        case R.id.action_vocabulary:
	        	Intent vocabularyIntend = new Intent(getBaseContext(), Vocabulary.class);
	        	startActivity(vocabularyIntend);
	            return true;
	        case R.id.shuffle:
	        	Trainer.shuffleList();
				try {
					riddle = Trainer.getRiddle();
				} catch (Exception e) {
					e.printStackTrace();
				}
				if (riddle == null) riddle = Riddle.emptyRiddle(" NO SUITIBLE PHRASES FOUND! ");
	        	startRiddle();
	            return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}

	public boolean onTouch(View view, MotionEvent event) {
	    final int X = (int) event.getRawX();
	    final int Y = (int) event.getRawY();
	    switch (event.getAction() & MotionEvent.ACTION_MASK) {
	        case MotionEvent.ACTION_DOWN:
	            RelativeLayout.LayoutParams lParams = (RelativeLayout.LayoutParams) view.getLayoutParams();
	            _xDelta = X - lParams.leftMargin;
	            _yDelta = Y - lParams.topMargin;
	            view.setAlpha(0.5f);
	            wordPosition = -1;
	            break;
	        case MotionEvent.ACTION_UP:
	            view.setAlpha(1);
	        	if (wordPosition > -1) {
		        	int option = ((Integer)view.getTag()).intValue();
	    				if (DEBUG) System.out.println("Choice: "+ option +" == " + riddle.getSolutionOption() + " && Position: " + wordPosition +" == "+ riddle.getSolutionPos());
	            	if (riddle.check(option, wordPosition)) {
	        			TextView solutionWord = new TextView(this);
	        			solutionWord.setText(riddle.getSolutionWord());
	        			LinearLayout.LayoutParams llp = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
	        		    llp.setMargins(5, 10, 5, 10); // llp.setMargins(left, top, right, bottom);
	        		    solutionWord.setLayoutParams(llp);
	        		    solutionWord.setTextSize(FONT_SIZE);

	        		    PhraseLayout currentLine = phraseLines;
	        		    while (wordPosition > currentLine.getLastWordIndex() + 1) currentLine = currentLine.nextLine;
	        			currentLine.addView(solutionWord, wordPosition - currentLine.getFirstWordIndex());
		            	LinearLayout.LayoutParams linLayoutParams = (LinearLayout.LayoutParams) wordView[wordPosition-1].getLayoutParams();
			            linLayoutParams.rightMargin = 5;
			            wordView[wordPosition-1].setLayoutParams(linLayoutParams);	            		

		            	textSentence.setBackgroundResource(R.drawable.phrase_correcto);
		            	labelOption1.setVisibility(View.INVISIBLE);
		            	labelOption2.setVisibility(View.INVISIBLE);
		            	labelOption3.setVisibility(View.INVISIBLE);
		            	labelOption4.setVisibility(View.INVISIBLE);
		            			            	
		            	correct = true;
		            	riddle.solved();
		            	Trainer.riddleSolved();

		            	break;
	            	}
	            	LinearLayout.LayoutParams linLayoutParams = (LinearLayout.LayoutParams) wordView[wordPosition-1].getLayoutParams();
		            linLayoutParams.rightMargin = 5;
		            wordView[wordPosition-1].setLayoutParams(linLayoutParams);	            		
	            	textSentence.setBackgroundResource(R.drawable.phrase_falso);	            	
            	}
	        	resetOptionPosition();
	            break;
	        case MotionEvent.ACTION_POINTER_DOWN:
	            break;
	        case MotionEvent.ACTION_POINTER_UP:
	            break;
	        case MotionEvent.ACTION_MOVE:
	            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) view.getLayoutParams();
	            layoutParams.leftMargin = X - _xDelta;
	            layoutParams.topMargin = Y - _yDelta;
	            layoutParams.rightMargin = -250;
	            layoutParams.bottomMargin = -250;
	            view.setLayoutParams(layoutParams);
	            
	            PhraseLayout currentLine = phraseLines;
	            int top = 0;
	            System.out.println("y, delta, txt, view, phr: "+ Y + ", "+_yDelta+", "+textSentence.getHeight()+", "+view.getHeight()+", "+phraseLines.getHeight());
            	int newWordPosition = -1;
	            if (Y > _yDelta - textSentence.getHeight() - view.getHeight()/2 && Y < _yDelta - view.getHeight()/2) {
		            while (currentLine != null) {
			            if (Y > _yDelta + top - textSentence.getHeight() - view.getHeight()/2 && Y < _yDelta + top - textSentence.getHeight() + currentLine.getHeight() - view.getHeight()/2) {
			            	int pos = -textSentence.getWidth()/2 - view.getWidth()/2;
			            	for (int i=currentLine.getFirstWordIndex(); i <= currentLine.getLastWordIndex(); i++) {
				            	LinearLayout.LayoutParams linLayoutParams = (LinearLayout.LayoutParams) wordView[i].getLayoutParams();
				            	int space = linLayoutParams.rightMargin - 5;
				            	pos += wordView[i].getWidth() + 10;
				            	pos += space/2;
				            	linLayoutParams.rightMargin = 5;
				            	if (X - _xDelta > pos - view.getWidth()/2 && X - _xDelta < pos + view.getWidth()/2) {
					            	linLayoutParams.rightMargin += view.getWidth() + 10;
					            	newWordPosition = i+1;
						            wordView[i].setLayoutParams(linLayoutParams);
					            	break;
				            	}
				            	pos += space/2;		            		
			            	}
			            	break;
			            }
			            top += currentLine.getHeight();
			            currentLine = currentLine.nextLine;
		            }
	            }
	            if (wordPosition > 0 && newWordPosition != wordPosition) {
            		LinearLayout.LayoutParams linLayoutParams = (LinearLayout.LayoutParams) wordView[wordPosition-1].getLayoutParams();
		            linLayoutParams.rightMargin = 5;
		            wordView[wordPosition-1].setLayoutParams(linLayoutParams);	            		
	            	wordPosition = -1;
	            }
	            wordPosition = newWordPosition;
	            if (DEBUG) System.out.println("wordPos "+ wordPosition);
	            break;
	    }
	    _root.invalidate();
	    return true;
	}

	private void nextRiddle() {
		Riddle newRiddle = null;
		try {
			newRiddle = Trainer.getNextRiddle();
		} catch (Exception e) {
			if (DEBUG) e.printStackTrace();
		}
		if (newRiddle != null) riddle = newRiddle;
		if (riddle == null)	riddle = Riddle.emptyRiddle(" NO SUITIBLE PHRASES FOUND! ");
		startRiddle();
		if (Trainer.hasPreviousRiddle()) prevButton.setVisibility(View.VISIBLE);
		if (!Trainer.hasNextRiddle()) nextButton.setVisibility(View.INVISIBLE);
	}
	private void previousRiddle() {
		Riddle newRiddle = null;
		try {
			newRiddle = Trainer.getPreviousRiddle();
		} catch (Exception e) {
			if (DEBUG) e.printStackTrace();
		}
		if (newRiddle != null) riddle = newRiddle;
		if (riddle == null)	riddle = Riddle.emptyRiddle(" NO SUITIBLE PHRASES FOUND! ");
		startRiddle();
		if (!Trainer.hasPreviousRiddle()) prevButton.setVisibility(View.INVISIBLE);
		if (Trainer.hasNextRiddle()) nextButton.setVisibility(View.VISIBLE);
	}
	
	private void startRiddle() {
		updateCounter();
		correct = riddle.alreadySolved();
		
		if (correct) {
			fillLineWith(riddle.getSentence());

			textSentence.setBackgroundResource(R.drawable.phrase_correcto);
        	labelOption1.setVisibility(View.INVISIBLE);
        	labelOption2.setVisibility(View.INVISIBLE);
        	labelOption3.setVisibility(View.INVISIBLE);
        	labelOption4.setVisibility(View.INVISIBLE);			
		}
		else {			
			fillLineWith(riddle.getRiddleSentence());
	
			resetOptionPosition();
			labelOption1.setText(" "+riddle.getOption1()+" ");
			labelOption2.setText(" "+riddle.getOption2()+" ");
			labelOption3.setText(" "+riddle.getOption3()+" ");
			labelOption4.setText(" "+riddle.getOption4()+" ");
			
	    	textSentence.setBackgroundResource(R.drawable.phrase);
	    	labelOption1.setVisibility(View.VISIBLE);
	    	labelOption2.setVisibility(View.VISIBLE);
	    	labelOption3.setVisibility(View.VISIBLE);
	    	labelOption4.setVisibility(View.VISIBLE);
		}
	}
	private void fillLineWith(String phrase) {
		String[] words = phrase.split(" ");

		textSentence.removeAllViews();
		phraseLines.removeAllViews();
		phraseLines.nextLine = null;
		textSentence.addView(phraseLines);
		
		Display display = getWindowManager().getDefaultDisplay(); 
		@SuppressWarnings("deprecation")
		int width = display.getWidth();
		
		wordView = new TextView[words.length];
		PhraseLayout currentLine = phraseLines;
		float totalWidth = 0;
		for (int i=0; i< words.length; i++) {
			wordView[i] = new TextView(this);
			wordView[i].setText(words[i]);
			LinearLayout.LayoutParams llp = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		    llp.setMargins(5, 10, 5, 10);
		    wordView[i].setLayoutParams(llp);
		    wordView[i].setTextSize(FONT_SIZE);
		    totalWidth += wordView[i].getPaint().measureText(words[i]) + 10;
		    if (totalWidth > width - 200) {
		    	currentLine.setLastWordIndex(i-1);
		    	currentLine.nextLine = new PhraseLayout(this, i);
		    	currentLine = currentLine.nextLine;
		    	textSentence.addView(currentLine);
		    	totalWidth = wordView[i].getPaint().measureText(words[i]) + 10;
		    }
		    currentLine.addView(wordView[i]);
		}
		currentLine.setLastWordIndex(words.length-1);		
	}
	private void updateCounter() {
		counterText.setText(Trainer.getListPosition() + "/"+ Trainer.getListSize());
	}
		
	private void resetOptionPosition() {
        RelativeLayout.LayoutParams layoutParams;
        layoutParams = (RelativeLayout.LayoutParams) labelOption1.getLayoutParams();
        layoutParams.setMargins(-140, 50, 0, 0);
        labelOption1.setLayoutParams(layoutParams);
        layoutParams = (RelativeLayout.LayoutParams) labelOption2.getLayoutParams();
        layoutParams.setMargins(20, 50, 0, 0);
        labelOption2.setLayoutParams(layoutParams);
        layoutParams = (RelativeLayout.LayoutParams) labelOption3.getLayoutParams();
        layoutParams.setMargins(-140, 130, 0, 0);
        labelOption3.setLayoutParams(layoutParams);
        layoutParams = (RelativeLayout.LayoutParams) labelOption4.getLayoutParams();
        layoutParams.setMargins(20, 130, 0, 0);
        labelOption4.setLayoutParams(layoutParams);
	}
		
	private class PhraseLayout extends LinearLayout {
		PhraseLayout nextLine;
		private int firstWordIndex;		
		private int lastWordIndex;		
		public PhraseLayout(Context context) {
			super(context);
			this.firstWordIndex=0;
		}
		public PhraseLayout(Context context, int index) {
			super(context);
			this.firstWordIndex = index;
		}
		public int getFirstWordIndex() { return firstWordIndex; }
		public int getLastWordIndex() { return lastWordIndex; }
		public void setLastWordIndex(int last) { this.lastWordIndex = last; }
	}
}
