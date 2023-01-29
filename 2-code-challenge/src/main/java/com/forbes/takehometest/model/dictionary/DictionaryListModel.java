package com.forbes.takehometest.model.dictionary;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class DictionaryListModel {
	private Dictionary dictionary = new Dictionary();

	public DictionaryListModel() {}

	public DictionaryListModel(List<String> words) {
		dictionary.setList(words);
	}

	@Data
	public static class Dictionary {
		private List<String> list = new ArrayList<>();
	}
}
