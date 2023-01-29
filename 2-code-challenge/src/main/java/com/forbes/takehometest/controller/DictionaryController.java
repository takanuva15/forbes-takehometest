package com.forbes.takehometest.controller;

import com.forbes.takehometest.interfaces.IDictionaryService;
import com.forbes.takehometest.model.dictionary.DictionaryAddModel;
import com.forbes.takehometest.model.dictionary.DictionaryListModel;
import com.forbes.takehometest.model.dictionary.DictionaryRemoveModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

/**
 * Serves the REST API for reading and modifying the internal dictionary.
 */
@RestController
public class DictionaryController {
	private final IDictionaryService dictionaryService;

	public DictionaryController(IDictionaryService dictionaryService) {
		this.dictionaryService = dictionaryService;
	}

	@GetMapping("/dictionary")
	public DictionaryListModel getAllWords() {
		var words = dictionaryService.getWords();
		return new DictionaryListModel(words);
	}

	/**
	 * Iterates over each word requesting to be added to the dictionary.
	 * If the word has any digits, it will be ignored.
	 * If the word has any internal punctuation aside from "'" or "-", it will be ignored.
	 * The word is then converted to lowercase and added to the dictionary. If the word is already present, a 200
	 * response will be generated with the words that were already present.
	 * Otherwise an empty 202 response is returned.
	 */
	@PostMapping("/dictionary")
	public ResponseEntity<Object> addWord(@RequestBody DictionaryAddModel addModel) {
		if (addModel.getDictionary() == null || addModel.getWordsToAdd() == null) {
			return ResponseEntity.badRequest().build();
		}
		var duplicateWords = new ArrayList<String>();
		for (var word : addModel.getWordsToAdd()) {
			// ignore word if it has digits
			if (word.matches(".*\\d+.*")) {
				continue;
			}
			// remove leading/trailing spaces and punctuation from the word if present. Convert to lowercase.
			word = word.trim().replaceAll("^\\p{Punct}", "").replaceAll("\\p{Punct}$", "").toLowerCase();
			if (!dictionaryService.addWord(word)) {
				duplicateWords.add(word);
			}
		}
		if(!duplicateWords.isEmpty()) {
			addModel.setWordsToAdd(duplicateWords);
			return ResponseEntity.ok(addModel.withErrorDuplicateEntry());
		}
		return ResponseEntity.accepted().build();
	}

	@DeleteMapping("/dictionary")
	public ResponseEntity<Object> deleteWord(@RequestBody DictionaryRemoveModel removeModel) {
		var notFoundWords = new ArrayList<String>();
		for (var word : removeModel.getWordsToRemove()) {
			if (!dictionaryService.removeWord(word)) {
				notFoundWords.add(word);
			}
		}
		if(!notFoundWords.isEmpty()) {
			removeModel.setWordsToRemove(notFoundWords);
			return new ResponseEntity<>(removeModel.withErrorWordNotFound(), HttpStatus.NOT_FOUND);
		}
		return ResponseEntity.accepted().build();
	}
}
