package com.forbes.takehometest.dao;

import java.util.List;

public interface ITrieDao {

	void addWord(String word);

	boolean removeWord(String word);

	boolean hasWord(String word);

	List<String> getClosestMatchesFor(String word);

}
