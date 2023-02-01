package com.forbes.takehometest.util;

public class WordValidationUtils {
	private static final String INVALID_CHARACTER_PATTERN = ".*[^A-Za-z'-].*";
	private static final String WORD_START_SANITIZER = "^\\p{Punct}*";
	private static final String WORD_END_SANITIZER = "[^A-Za-z0-9']*$";

	private WordValidationUtils() {}

	/**
	 * Returns true if the given word has only alphabetic characters or ' or -
	 */
	public static boolean isValidDictionaryWord(String word) {
		return !word.matches(INVALID_CHARACTER_PATTERN);
	}

	/**
	 * Removes leading/trailing spaces.
	 * Removes all leading punctuation from the word.
	 * Removes all ending punctuation except for '
	 */
	public static String sanitizeWord(String word) {
		return word.trim().replaceAll(WORD_START_SANITIZER, "").replaceAll(WORD_END_SANITIZER, "");
	}

	/**
	 * Due to the limits of the Trie structure, if a word is too long, it would take too much time to index
	 * into the Trie. Thus, this method quickly checks if the word is short enough to be indexed.
	 */
	public static boolean canWordBeIndexed(String word) {
		return word.length() <= 22;
	}
}
