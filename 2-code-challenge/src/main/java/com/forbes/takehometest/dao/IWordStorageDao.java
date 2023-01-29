package com.forbes.takehometest.dao;

import java.util.List;

public interface IWordStorageDao {
	void addWord(String word);

	void removeWord(String word);

	List<String> getWords();

	long getWordIndex(String word);
}
