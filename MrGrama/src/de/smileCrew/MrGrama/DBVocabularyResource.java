package de.smileCrew.MrGrama;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBVocabularyResource implements AbstractVocabularyResource {
    private static final String DATABASE_NAME = "data";
    private static final int DATABASE_VERSION = 2;
    
    private static final String VOC_TABLE = "vocabulary";
    private static final String PHRASE_TABLE = "phrases";
    private static final String FILTER_TABLE = "filter";
    private static final String PERM_TABLE = "permutation";
//  private static final String STATS_TABLE = "statistics";

    private static DBHelper dbHelper;
    private static SQLiteDatabase db;
	private static Random zufall;

    private static class DBHelper extends SQLiteOpenHelper {
    	private Context cntxt;
    	
		DBHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
            cntxt = context;
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("CREATE TABLE "+ VOC_TABLE +"(_id integer primary key autoincrement, voc text unique not null, active boolean default(0), lastLoad integer);");
            db.execSQL("CREATE TABLE "+ PHRASE_TABLE +"(_id integer primary key autoincrement, voc_id integer not null, phrase text not null);");
            db.execSQL("CREATE TABLE "+ FILTER_TABLE +"(_id integer primary key autoincrement, filter text unique not null);");
            db.execSQL("CREATE TABLE "+ PERM_TABLE +"(_id integer primary key autoincrement, phrase_id integer unique not null, solved boolean default(0));");
            
            HashMap<String,ArrayList<String>> map = Trainer.loadDefaultPhrases(cntxt);
            
            ArrayList<String> filter = map.remove("fltr");
            Iterator<String> it = filter.iterator();
            ContentValues values;
            while (it.hasNext()) {
            	values = new ContentValues();
                values.put("filter", it.next());
                db.insert(FILTER_TABLE, null, values);
            }
            ArrayList<String> vocabulary = map.remove("vcs");
            Iterator<String> vocsIt = vocabulary.iterator();
            String voc, phrase;
            long vocId;
            while (vocsIt.hasNext()) {
            	voc = vocsIt.next();
                values = new ContentValues();
                values.put("voc", voc);
                vocId = db.insert(VOC_TABLE, null, values);
                it = map.get(voc).iterator();
                while (it.hasNext()) {
                	phrase = it.next();
                	if (Trainer.searchFilterWords(phrase)[0][0] == 0) continue;
                    values = new ContentValues();
                    values.put("voc_id", vocId);
                    values.put("phrase", phrase);
                    db.insert(PHRASE_TABLE, null, values);
                }
            }
        }

        /*
         * TODO Save and reinsert content
         */
        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
//            Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
//                    + newVersion + ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS "+ VOC_TABLE);
            db.execSQL("DROP TABLE IF EXISTS "+ PHRASE_TABLE);
            db.execSQL("DROP TABLE IF EXISTS "+ FILTER_TABLE);
            db.execSQL("DROP TABLE IF EXISTS "+ PERM_TABLE);
            
            onCreate(db);
        }
    }
    
	private int sequencePos;
	private int[] randomSequence;
	private boolean[] solved;
	
	public void open(Context ct) throws SQLException {
		dbHelper = new DBHelper(ct);
		db = dbHelper.getWritableDatabase();
	}
    public void close() {
    	dbHelper.close();
    }

	@Override
	public int getFilterSize() {
		Cursor mc = db.rawQuery("SELECT COUNT(*) FROM "+ FILTER_TABLE, null);
		return mc.getInt(0);
	}
	@Override
	public String getFilter(int id) {
		Cursor mCursor = db.query(FILTER_TABLE, new String[] {"filter"}, "_id=" + id, null,null,null,null);
        if (mCursor != null) {
        	if (mCursor.moveToFirst()) return mCursor.getString(0);
        }
        return null;
	}
	@Override
	public String[] getAllFilters() {
		Cursor mCursor = db.query(FILTER_TABLE, new String[] {"_id","filter"}, null, null,null,null,null);		
        if (mCursor != null) {
        	if (!mCursor.moveToFirst()) return null;
        	String[] filter = new String[mCursor.getCount()];
        	int i=0;
        	do {
        		filter[i++] = mCursor.getString(1);
        	} while (mCursor.moveToNext());        	
    		return filter; 
        }
		return null;
	}
	@Override
    public int addFilter(String filter) {
        ContentValues values = new ContentValues();
        values.put("filter", filter);

        return (int) db.insert(FILTER_TABLE, null, values);
    }
	@Override
	public boolean removeFilter(int id) {
        return db.delete(FILTER_TABLE, "_id=" + id, null) > 0;
	}
	@Override
	public int getVocabularySize() {
		Cursor mc = db.rawQuery("SELECT COUNT(*) FROM "+ VOC_TABLE, null);
		return mc.getInt(0);
	}
	@Override
	public String[] getAllVocabularies(ArrayList<Integer> ids, ArrayList<Boolean> active) {
		Cursor mCursor = db.query(VOC_TABLE, new String[] {"_id", "voc", "active"}, null, null,null,null,null);
        if (mCursor != null) {
        	if (!mCursor.moveToFirst()) return null;
        	String[] vocs = new String[mCursor.getCount()];
        	int i=0;
        	do {
        		vocs[i++] = mCursor.getString(1);
        		if (ids != null) ids.add(Integer.valueOf(mCursor.getInt(0)));
        		if (active != null) active.add(Boolean.valueOf(mCursor.getShort(2)!=0));
        	} while (mCursor.moveToNext());        	
    		return vocs; 
        }
		return null;
	}
//	@Override
//	public ListAdapter getAllVocabularies(Context context, int layout, int to) {
//		Cursor mCursor = db.query(VOC_TABLE, new String[] {"_id", "voc", "active"}, null, null,null,null,null);
//        if (mCursor != null) {
//        	((Activity)context).startManagingCursor(mCursor);
//            // Now create a simple cursor adapter and set it to display
//            SimpleCursorAdapter notes = new SimpleCursorAdapter(context, layout, mCursor, new String[] {"voc"}, new int[] {to});
//        }
//		return null;
//	}
	@Override
    public int addVocabulary(String voc) {
        ContentValues values = new ContentValues();
        values.put("voc", voc);

        return (int) db.insert(VOC_TABLE, null, values);
    }
	@Override
	public String getVocabulary(int id) {
		Cursor mCursor = db.query(VOC_TABLE, new String[] {"voc"}, "_id=" + id, null,null,null,null);
        if (mCursor != null) {
        	if (mCursor.moveToFirst()) return mCursor.getString(0);
        }
        return null;
	}
	public boolean getVocabularyActive(int id) {
		Cursor mCursor = db.query(VOC_TABLE, new String[] {"active"}, "_id=" + id, null,null,null,null);
        if (mCursor != null) {
        	if (mCursor.moveToFirst()) return mCursor.getShort(0) != 0;
        }
        return false;
	}
	public boolean updateVocabularyActivated(int id, boolean checked) {
		ContentValues args = new ContentValues();
        args.put("active", checked);

        return db.update(VOC_TABLE, args, "_id=" + id, null) > 0;
	}
	@Override
	public boolean removeVocabulary(int id) {
        return db.delete(VOC_TABLE, "_id=" + id, null) > 0;
	}
	@Override
	public int getPhraseSize() {
		Cursor mc = db.rawQuery("SELECT COUNT(*) FROM "+ PHRASE_TABLE, null);
		return mc.getInt(0);
	}
	@Override
	public int getPhraseSize(int vocID) {
		Cursor mc = db.rawQuery("SELECT COUNT(*) FROM "+ PHRASE_TABLE +" WHERE voc_id=? ", new String[] {""+vocID});
		return mc.getInt(0);
	}
	@Override
	public String[] getAllPhrases(int vocID, ArrayList<Integer> ids) {
		Cursor mCursor = db.query(PHRASE_TABLE, new String[] {"_id", "phrase"}, "voc_id="+vocID, null,null,null,null);
        if (mCursor != null) {
        	if (!mCursor.moveToFirst()) return null;
        	String[] phrases = new String[mCursor.getCount()];
        	int i=0;
        	do {
        		phrases[i++] = mCursor.getString(1);
        		if (ids != null) ids.add(Integer.valueOf(mCursor.getInt(0)));
        	} while (mCursor.moveToNext());
        	return phrases;
        }
		return null;
	}
//	@Override
//	public ListAdapter getAllPhrases(Context context, int vocID, int layout, int to) {
//		Cursor mCursor = db.query(PHRASE_TABLE, new String[] {"_id", "phrase"}, "voc_id="+vocID, null,null,null,null);
//        if (mCursor != null) {
//        	((Activity)context).startManagingCursor(mCursor);
//            // Now create a simple cursor adapter and set it to display
//            SimpleCursorAdapter notes = new SimpleCursorAdapter(context, layout, mCursor, new String[] {"phrase"}, new int[] {to});
//        }
//		return null;
//	}
	@Override
	public String getPhrase(int id) {
		Cursor mCursor = db.query(PHRASE_TABLE, new String[] {"phrase"}, "_id=" + id, null,null,null,null);
        if (mCursor != null && mCursor.moveToFirst()) return mCursor.getString(0);
        return null;
	}
	@Override
    public int addPhrase(int vocId, String phrase) {
        ContentValues values = new ContentValues();
        values.put("voc_id", vocId);
        values.put("phrase", phrase);

        return (int) db.insert(PHRASE_TABLE, null, values);
    }	@Override
	public boolean removePhrase(int id) {
        return db.delete(PHRASE_TABLE, "_id=" + id, null) > 0;
	}
        
    public boolean hasNext() { return (randomSequence != null)? sequencePos < randomSequence.length-1 : false; }
    public boolean hasPrevious() { return sequencePos > 0; }
    public String getNextSentence() {
    	if (sequencePos < randomSequence.length-1) {
    		sequencePos++;
    		return getSentence(sequencePos);
    	}
    	return null;
    }
    public String getPreviousSentence() {
		if (sequencePos > 0) {
			sequencePos--;
	    	return getSentence(sequencePos);
		}
		return null;
    }
    public String getSentence() {
    	return getSentence(sequencePos);
    }
	public String getSentence(int sequencePos) {
		if (randomSequence==null) loadRandomSequence();
//		if (sequencePos >= randomSequence.length) shuffle();
		if (randomSequence == null || randomSequence.length==0) return null;
		return this.getPhrase(randomSequence[sequencePos]);
	}
	public boolean getCurrentSolved() {
		if (solved==null) loadRandomSequence();
		if (sequencePos >= solved.length) shuffle();
		return solved[sequencePos];
	}
	public boolean setCurrentSolved(boolean v) {
		solved[sequencePos] = v;
		ContentValues args = new ContentValues();
        args.put("solved", v);

        return db.update(PERM_TABLE, args, "phrase_id=" + randomSequence[sequencePos], null) > 0;
	}
	public int getSequenceSize() { return (randomSequence == null)? -1 : randomSequence.length; }
	public int getSequencePosition() { return sequencePos; }
	private void loadRandomSequence() {
//	System.out.println("trying to load");
		Cursor mCursor = db.query(PERM_TABLE, new String[] {"phrase_id, solved"}, null, null, null, null, null);
		if (mCursor == null || !mCursor.moveToFirst()) shuffle();
		else {
//	System.out.println("loading...");
			int phraseCount = mCursor.getCount();
			randomSequence = new int[phraseCount];
			solved = new boolean[phraseCount];
        	int i=0;
        	do {
        		randomSequence[i] = mCursor.getInt(0);
        		solved[i] = mCursor.getShort(1) > 0;
        		i++;
//                System.out.println("Reading" + mCursor.getInt(0));
        	} while (mCursor.moveToNext());			
        	sequencePos = 0;
		}
	}
	public void shuffle() {
		Cursor mCursor = db.rawQuery("SELECT p._id FROM "+PHRASE_TABLE+" AS p INNER JOIN "+VOC_TABLE+" AS v ON p.voc_id=v._id WHERE active=1", null); 
		if (mCursor != null && mCursor.moveToFirst()) {
			int phraseCount = mCursor.getCount();
			randomSequence = new int[phraseCount];
			solved = new boolean[phraseCount];
        	int i=0;
        	do {
        		randomSequence[i++] = mCursor.getInt(0);        		
        	} while (mCursor.moveToNext());			
			permuteArray(randomSequence);
			sequencePos = 0;
//			db.execSQL("TRUNCATE TABLE " + PERM_TABLE);
			db.execSQL("DELETE FROM " + PERM_TABLE);
//			db.execSQL("ALTER TABLE " + PERM_TABLE + " AUTO_INCREMENT = 1");
			for (i=0; i<randomSequence.length; i++) {
                ContentValues values = new ContentValues();
                values.put("phrase_id", randomSequence[i]);

                long id = db.insert(PERM_TABLE, null, values);
                System.out.println("inserted " + id + " " + randomSequence[i]);
			}
		}
	}
	private void permuteArray(int[] list) {
		if (zufall == null) zufall = new Random();
		int n = list.length;
		int z;
		int tmp;
		for (int i=0; i<n-2; i++) {
			z = zufall.nextInt(n-i) + i;
			tmp = list[i];
			list[i] = list[z];
			list[z] = tmp;
		}
	}
}
