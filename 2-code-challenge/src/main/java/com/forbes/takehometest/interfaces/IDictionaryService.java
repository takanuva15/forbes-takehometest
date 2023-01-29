package com.forbes.takehometest.interfaces;

import java.util.List;
import java.util.Optional;

public interface IDictionaryService {
	List<String> getWords();

	boolean addWord(String word);

	boolean removeWord(String word);

	boolean hasWord(String word);

	Optional<String> getClosestWord(String word);
}
