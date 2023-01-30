package com.forbes.takehometest.dao;

import java.util.List;

/**
 * Defines the methods that a Trie DAO should support for reading/writing to a Trie.
 */
public interface ITrieDao {

	void addWord(String word);

	boolean removeWord(String word);

	boolean hasWord(String word);

	List<String> getClosestMatchesFor(String word);

}
