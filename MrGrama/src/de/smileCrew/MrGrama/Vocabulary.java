package de.smileCrew.MrGrama;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.app.Activity;
import android.app.ListActivity;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.BaseAdapter;
import android.widget.CheckedTextView;
import android.widget.ListView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.support.v4.app.NavUtils;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;

public class Vocabulary extends ListActivity {
    private static final int ACTIVITY_EDIT=1;
    private static final int NEW_VOCABULARY=2;
    private CheckedArrayAdapter adapter;
    public static final String VOC = "voc";
    public static final String ID = "id";

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_vocabulary);
		// Show the Up button in the action bar.
		setupActionBar();

		ArrayList<Integer> ids = new ArrayList<Integer>();
		ArrayList<Boolean> checked = new ArrayList<Boolean>();
		String[] vocabulary = Trainer.getVocabularyList(ids, checked);
		adapter = new CheckedArrayAdapter(this, vocabulary, ids, checked);
		
		ListView list = this.getListView();
		list.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
		list.setAdapter(adapter);
		
		for (int i=0; i<checked.size(); i++) {
			if (checked.get(i)) list.setItemChecked(i, true);
		}
		
		registerForContextMenu(list);
	}

	/**
	 * Set up the {@link android.app.ActionBar}, if the API is available.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void setupActionBar() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			getActionBar().setDisplayHomeAsUpEnabled(true);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.vocabulary, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// This ID represents the Home or Up button. In the case of this
			// activity, the Up button is shown. Use NavUtils to allow users
			// to navigate up one level in the application structure. For
			// more details, see the Navigation pattern on Android Design:
			//
			// http://developer.android.com/design/patterns/navigation.html#up-vs-back
			//
			NavUtils.navigateUpFromSameTask(this);
			return true;
		case R.id.addItem:
//			Trainer.addVocabulary("Test "+TESTNUM++);
			Intent intent = new Intent(this, VocTitle.class);
			startActivityForResult(intent, NEW_VOCABULARY);
			
//			ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.voc_list_row, R.id.vocTitle, Trainer.getVocabularyList());
//			ListView list = this.getListView();
//			list.setAdapter(adapter);
		}
		return super.onOptionsItemSelected(item);
	}
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        getMenuInflater().inflate(R.menu.vocabulary_context, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterContextMenuInfo voc = (AdapterContextMenuInfo) item.getMenuInfo();
        switch(item.getItemId()) {
            case R.id.editItem:
            	Intent i = new Intent(this, VocPhrases.class);
                i.putExtra("vocId", voc.id);
                startActivityForResult(i, ACTIVITY_EDIT);
                return true;
            case R.id.removeItem:
            	if (Trainer.resource.removeVocabulary((int)voc.id)) {
                	adapter.removeItem((int)voc.id);
                	adapter.notifyDataSetChanged();
            	}
            	
                //AdapterContextMenuInfo voc = (AdapterContextMenuInfo) item.getMenuInfo();
                //Trainer.removeVocabulary(voc.id);
            	
    			
//        		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.voc_list_row, R.id.vocTitle, Trainer.getVocabularyList());
//    			ListView list = this.getListView();
//    			list.setAdapter(adapter);
                return true;
        }
        return super.onContextItemSelected(item);
    }
    
    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
    	super.onListItemClick(l,v,position,id);
	    CheckedTextView tv = (CheckedTextView)v;
	    
		Trainer.setVocabularyActivated(((Integer)tv.getTag()).intValue(), tv.isChecked());
		
//    	long[] checked = getListView().getCheckedItemIds();
//    	getListView().
//        boolean hasCheckedElement = false;
// 
//        if (hasCheckedElement) {
//            if (mMode == null) {
//                mMode = startActionMode(new ModeCallback());
//            }
//        } else {
//            if (mMode != null) {
//                mMode.finish();
//            }
//        }  	
//        super.onListItemClick(l, v, position, id);
//        Intent i = new Intent(this, VocPhrases.class);
//        i.putExtra("id", id);
//        startActivityForResult(i, ACTIVITY_EDIT);
    }
    
    protected void onActivityResult (int requestCode, int resultCode, Intent data){
    	switch (requestCode) {
    		case(NEW_VOCABULARY):
    			if (resultCode != RESULT_OK) return;
    			String voc = data.getExtras().getString(VOC);
    			int id = data.getExtras().getInt(ID);
    			adapter.addItem(voc, id);
    			adapter.notifyDataSetChanged();
    			break;
    		default:
    	}
    }
    
    private class CheckedArrayAdapter extends BaseAdapter {
    	private final Activity context;
    	private final List<String> vocs;
    	private final List<Integer> ids;
    	private final List<Boolean> checked;
//    	private ListView list;

    	public CheckedArrayAdapter(Activity context, String[] vocs, ArrayList<Integer> ids, ArrayList<Boolean> checked) {
    		super();
    		this.context = context;
    		this.vocs = new ArrayList<String>();
    		if (vocs != null) for (int i=0; i<vocs.length; i++) this.vocs.add(vocs[i]);
    		this.ids = ids;
    		this.checked = checked;
    	}

    	@Override
    	public View getView(int position, View convertView, ViewGroup parent) {
    		View view = null;
    		if (convertView == null) {
    			LayoutInflater inflator = context.getLayoutInflater();
    			view = inflator.inflate(android.R.layout.simple_list_item_multiple_choice, null);
//    			CheckedTextView textview = (CheckedTextView) view.findViewById(android.R.id.text1);
//    			textview.setOnTouchListener(new View.OnTouchListener() {
//					@Override
//					public boolean onTouch(View v, MotionEvent event) {
//						System.out.println("PAssiert " + event.getAction());
//					    if ((event.getAction() & MotionEvent.ACTION_MASK) == MotionEvent.ACTION_UP) {
//							System.out.println("was");
//						    CheckedTextView tv = (CheckedTextView)v;
//							tv.setChecked(!tv.isChecked());
//							Trainer.setVocabularyActivated(((Integer)tv.getTag()).intValue(), tv.isChecked());
//							return true;
//					    }
//						return false;					    	
//					}
//    			});
    		} else {
    			view = convertView;
    		}
    		CheckedTextView textview = (CheckedTextView) view.findViewById(android.R.id.text1);
    		textview.setText(vocs.get(position));
    		textview.setChecked(checked.get(position));
//    		System.out.println("Vokabel "+ vocs[position] + " is " + ((checked.get(position))? "active" : "inactive"));
    		textview.setTag(ids.get(position));
    		return view;
    	}
    	
    	@Override
    	public boolean hasStableIds() {
    		return true;
    	}
    	
    	@Override
    	public long getItemId(int position) {
    		return (ids!=null && position >= 0 && position < ids.size())? ids.get(position) : -1;
    	}

		@Override
		public int getCount() {
			return (vocs != null)? vocs.size() : -1;
		}

		@Override
		public Object getItem(int position) {
			return (vocs != null && position < vocs.size() && position >= 0)? vocs.get(position) : null;
		}
		
		public void addItem(String voc, int id) {
			vocs.add(voc);
			ids.add(Integer.valueOf(id));
			checked.add(Boolean.TRUE);
		}
		public void removeItem(int id) {
			int position = ids.indexOf(id);
			vocs.remove(position);
			ids.remove(position);
			checked.remove(position);
		}
    }
}
