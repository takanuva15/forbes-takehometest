package com.forbes.takehometest.dao;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

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
