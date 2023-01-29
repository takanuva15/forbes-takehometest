package com.forbes.takehometest.dao;

import com.forbes.takehometest.triedb.Trie;

import java.util.List;

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
