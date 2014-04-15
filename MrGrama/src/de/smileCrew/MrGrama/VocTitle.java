package de.smileCrew.MrGrama;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;

public class VocTitle extends Activity {
	TextView vocabulary;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_voc_title);
		
		vocabulary = (TextView)findViewById(R.id.vocText);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.voc_title, menu);
		return true;
	}
    public void onClickOk(View v) {
    	String voc = vocabulary.getText().toString().trim();
    	int id = Trainer.resource.addVocabulary(voc);
    	Intent data = new Intent();
    	data.putExtra(Vocabulary.VOC, voc);
    	data.putExtra(Vocabulary.ID, id);
    	setResult(RESULT_OK, data);
        finish();
    }
}
