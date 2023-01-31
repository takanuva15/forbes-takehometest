package com.forbes.takehometest.interfaces;

import java.util.Set;

/**
 * Defines the methods that a Trie DAO should support for reading/writing to a Trie.
 */
public interface ITrieDao {

	void addWord(String word);

	boolean removeWord(String word);

	boolean hasWord(String word);

	Set<String> getClosestMatchesFor(String word);

}
