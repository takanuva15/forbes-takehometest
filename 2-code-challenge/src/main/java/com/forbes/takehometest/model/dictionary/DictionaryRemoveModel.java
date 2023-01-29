package com.forbes.takehometest.model.dictionary;

import lombok.Data;

import java.util.List;

@Data
public class DictionaryRemoveModel {
	private Dictionary dictionary;
	private String error;

	public List<String> getWordsToRemove() {
		return dictionary.remove;
	}

	public DictionaryRemoveModel setWordsToRemove(List<String> words) {
		dictionary.remove = words;
		return this;
	}

	public DictionaryRemoveModel withErrorWordNotFound() {
		error = "entry not found";
		return this;
	}

	@Data
	static class Dictionary {
		private List<String> remove;
	}
}
