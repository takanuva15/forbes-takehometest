package com.forbes.takehometest.dao;

import java.util.List;

/**
 * Defines the methods that a Word Storage DAO should support for reading/writing to a standard word storage table.
 */
public interface IWordStorageDao {
	void addWord(String word);

	void removeWord(String word);

	List<String> getWords();

	long getWordIndex(String word);
}
