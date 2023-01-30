package com.forbes.takehometest.controller;

import com.forbes.takehometest.interfaces.IDictionaryService;
import com.forbes.takehometest.model.dictionary.DictionaryAddModel;
import com.forbes.takehometest.model.dictionary.DictionaryListModel;
import com.forbes.takehometest.model.dictionary.DictionaryRemoveModel;
import com.forbes.takehometest.util.WordValidationUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

/**
 * Serves the REST API for reading and modifying the internal dictionary.
 */
@Slf4j
@RestController
public class DictionaryController {
	private final IDictionaryService dictionaryService;

	public DictionaryController(IDictionaryService dictionaryService) {
		this.dictionaryService = dictionaryService;
	}

	@GetMapping("/dictionary")
	public DictionaryListModel getAllWords() {
		log.info("Received request to get words");
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
			log.error("Invalid request received: {}", addModel);
			return ResponseEntity.badRequest().build();
		}
		var duplicateWords = new ArrayList<String>();
		for (var word : addModel.getWordsToAdd()) {
			word = WordValidationUtils.sanitizeWord(word).toLowerCase();
			// ignore word if it's empty or has digits
			if (word.isEmpty() || !WordValidationUtils.isValidDictionaryWord(word)) {
				log.warn("Invalid word found: '{}'. Will not add to dictionary...", word);
				continue;
			}
			if (!dictionaryService.addWord(word)) {
				log.debug("Word already exists: '{}'. Skipping...", word);
				duplicateWords.add(word);
			}
		}
		if(!duplicateWords.isEmpty()) {
			addModel.setWordsToAdd(duplicateWords);
			return ResponseEntity.ok(addModel.withErrorDuplicateEntry());
		}
		return ResponseEntity.accepted().build();
	}

	/**
	 * Deletes the given words from the dictionary if valid and present. (Valid means that there's no miscellaneous
	 * punctuation or digits aside from ' and -.
	 *
	 * We do not convert to lowercase here to be safe in case the user was expecting to delete an uppercase word.
	 */
	@DeleteMapping("/dictionary")
	public ResponseEntity<Object> deleteWord(@RequestBody DictionaryRemoveModel removeModel) {
		var notFoundWords = new ArrayList<String>();
		for (var word : removeModel.getWordsToRemove()) {
			if (!WordValidationUtils.isValidDictionaryWord(word)) {
				log.warn("Invalid word provided: '{}'. Will not delete from dictionary...", word);
				continue;
			}
			if (!dictionaryService.removeWord(word)) {
				log.debug("Word not found in dictionary: '{}'. Skipping...", word);
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
