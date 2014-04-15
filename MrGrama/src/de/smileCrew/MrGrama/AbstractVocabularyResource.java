package de.smileCrew.MrGrama;

import java.util.ArrayList;

public interface AbstractVocabularyResource {
	
	/**
	 * Returns the total number of filter words
	 * @return the number of filter words
	 */
	public int getFilterSize();
	/**
	 * Returns the filter word with the given ID
	 * @param id the ID of the filter word
	 * @return the filter word with the given ID or null if not found
	 */
	public String getFilter(int id);
	/**
	 * Returns all filter words in a ListAdapter
	 * @return all filter words in a ListAdapter
	 */
	public String[] getAllFilters();
	/**
	 * Add a new filter word to the resource
	 * @param the filter word to be added
	 * @return the new filter words ID
	 */
	public int addFilter(String filter);
	/**
	 * Remove the filter word with the given ID from the resource
	 * @param id the ID of the filter word
	 * @return true on success of deletion, false otherwise
	 */
	public boolean removeFilter(int id);
	
	
	/**
	 * Returns the total number of vocabularies
	 * @return the number of vocabularies
	 */
	public int getVocabularySize();
	/**
	 * Returns the vocabulary with the given ID
	 * @param id the ID of the vocabulary
	 * @return the vocabulary with the given ID
	 */
	public String getVocabulary(int id);
	/**
	 * Returns all vocabularies in a String Array
	 * @param ids is automatically filled with the IDs of the returned vocabularies
	 * @param active is automatically filled with the corresponding active values
	 * @return all vocabularies in a ListAdapter
	 */
	public String[] getAllVocabularies(ArrayList<Integer> ids, ArrayList<Boolean> active);
//	/**
//	 * Returns all vocabularies in a ListAdapter
//	 * @param context The context where the ListView associated with this SimpleListItemFactory is running
//	 * @param layout resource identifier of a layout file that defines the views for this list item. The layout file should include at least the named view defined in "to"
//	 * @param to The view that should display the vocabularies. Should be a TextView.
//	 * @return all vocabularies in a ListAdapter
//	 */
//	public ListAdapter getAllVocabularies(Context context, int layout, int to);
	/**
	 * Adds a new vocabulary to the resource
	 * @param voc the new vocabulary
	 * @return the new ID of the vocabulary in the resource
	 */
	public int addVocabulary(String voc);
	/**
	 * Remove the vocabulary with the given ID from the resource
	 * @param id the ID of the vocabulary
	 * @return true on success of deletion, false otherwise
	 */
	public boolean removeVocabulary(int id);
	
	/**
	 * Returns the total number of phrases 
	 * @return the total number of phrases 
	 */
	public int getPhraseSize();
	/**
	 * Returns the number of phrases belonging to the vocabulary with the given ID
	 * @param vocID the ID of the vocabulary
	 * @return the number of phrases belonging to the vocabulary with the given ID
	 */
	public int getPhraseSize(int vocID);
	/**
	 * Returns the phrase with the given ID
	 * @param id the ID of the phrase
	 * @return the phrase with the given ID
	 */
	public String getPhrase(int id);
	/**
	 * Returns all phrases that belong to the vocabulary with the given ID in a String Array
	 * @param vocID the ID of the vocabulary
	 * @param ids is automatically filled with the IDs of the returned phrases
	 * @return all phrases that belong to the vocabulary with the given ID in a String Array
	 */
	public String[] getAllPhrases(int vocID, ArrayList<Integer> ids);
//	/**
//	 * Returns all phrases that belong to the vocabulary with the given ID in a ListAdapter
//	 * @param context The context where the ListView associated with this SimpleListItemFactory is running
//	 * @param vocID the ID of the vocabulary
//	 * @param layout resource identifier of a layout file that defines the views for this list item. The layout file should include at least the named view defined in "to"
//	 * @param to The view that should display the vocabularies. Should be a TextView.
//	 * @return all phrases that belong to the vocabulary with the given ID in a ListAdapter
//	 */
//	public ListAdapter getAllPhrases(Context context, int vocID, int layout, int to);
	/**
	 * Adds a new phrase belonging to a vocabulary to the resource
	 * @param vocId the ID of the vocabulary the phrase belongs to
	 * @param phrase the new phrase
	 * @return the new ID of the phrase in the resource
	 */
	public int addPhrase(int vocId, String phrase);
	/**
	 * Remove the phrase with the given ID from the resource
	 * @param id the ID of the phrase
	 * @return true on success of deletion, false otherwise
	 */
	public boolean removePhrase(int id);
}
