package com.forbes.takehometest.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * Implements methods for reading/writing to a Word Storage. Since we are not using an external DB, the Word Storage
 * instance is stored within this class.
 *
 * To store words in order, we use a TreeSet that stores words in sorted order. This allowed word-deletes to happen in
 * O(log n) time, although this also causes word-gets to happen in O(n log n) time. This is a reasonable trade-off to
 * make since it's unlikely we will need to constantly read the entire dictionary off the web server in production.
 * (If we used an array, deletes would take O(n) time and word-gets would be O(n) time)
 */
public class WordStorageDao implements IWordStorageDao {
	private final Set<String> words = new TreeSet<>();

	@Override
	public void addWord(String word) {
		words.add(word);
	}

	@Override
	public void removeWord(String word) {
		words.remove(word);
	}

	@Override
	public List<String> getWords() {
		return new ArrayList<>(words);
	}
}
