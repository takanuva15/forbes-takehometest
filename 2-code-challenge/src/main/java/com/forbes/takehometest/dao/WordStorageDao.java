package com.forbes.takehometest.dao;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Implements methods for reading/writing to a Word Storage. Since we are not using an external DB, the Word Storage
 * instance is stored within this class.
 *
 * To store words in order, we use a LinkedHashMap that maps a word to its sequence number, which identifies when the
 * word was added. This allowed word-deletes to happen in O(1) time, although this also causes word-gets to happen in
 * O(n) time. This is a reasonable trade-off to make since it's unlikely we will need to constantly read the entire
 * dictionary off the web server in production.
 */
public class WordStorageDao implements IWordStorageDao {
	private final AtomicLong seqNumber = new AtomicLong();

	private final Map<String, Long> words = new LinkedHashMap<>();

	@Override
	public void addWord(String word) {
		words.put(word, seqNumber.getAndIncrement());
	}

	@Override
	public void removeWord(String word) {
		words.remove(word);
	}

	@Override
	public List<String> getWords() {
		return new ArrayList<>(words.keySet());
	}

	@Override
	public long getWordIndex(String word) {
		return words.get(word);
	}
}
