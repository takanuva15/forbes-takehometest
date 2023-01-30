package com.forbes.takehometest.model.dictionary;

import lombok.Data;

import java.util.List;

/**
 * API model for adding words to a dictionary
 */
@Data
public class DictionaryAddModel {
	private Dictionary dictionary;
	private String error;

	public List<String> getWordsToAdd() {
		return dictionary.add;
	}

	public DictionaryAddModel setWordsToAdd(List<String> words) {
		dictionary.add = words;
		return this;
	}

	public DictionaryAddModel withErrorDuplicateEntry() {
		error = "duplicate entry";
		return this;
	}

	@Data
	static class Dictionary {
		private List<String> add;
	}
}
