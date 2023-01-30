package com.forbes.takehometest.dao;

import com.forbes.takehometest.triedb.Trie;

import java.util.List;

/**
 * Implements methods for reading/writing to a Trie. Since we are not using an external DB, the Trie instance is
 * stored within this class.
 */
public class TrieDao implements ITrieDao {
	private final Trie trie = new Trie();

	@Override
	public void addWord(String word) {
		trie.addWord(word);
	}

	@Override
	public boolean removeWord(String word) {
		return trie.deleteWord(word);
	}

	@Override
	public boolean hasWord(String word) {
		return trie.contains(word);
	}

	@Override
	public List<String> getClosestMatchesFor(String word) {
		return trie.closestMatches(word);
	}
}
