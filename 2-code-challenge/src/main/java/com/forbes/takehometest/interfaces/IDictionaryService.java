package com.forbes.takehometest.interfaces;

import java.util.List;
import java.util.Optional;

/**
 * Defines the methods that a dictionary service should support for reading/writing to a dictionary
 */
public interface IDictionaryService {
	List<String> getWords();

	boolean addWord(String word);

	boolean removeWord(String word);

	boolean hasWord(String word);

	Optional<String> getClosestWord(String word);
}
