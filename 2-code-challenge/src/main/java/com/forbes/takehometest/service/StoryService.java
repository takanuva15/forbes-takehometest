package com.forbes.takehometest.service;

import com.forbes.takehometest.interfaces.IDictionaryService;
import com.forbes.takehometest.interfaces.IStoryService;
import com.forbes.takehometest.model.story.WordCorrection;
import com.forbes.takehometest.util.WordValidationUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Serves as an intermediary for correcting a given story by communicating with the dictionary service.
 */
@Slf4j
@Service
public class StoryService implements IStoryService {
	private final IDictionaryService dictionaryService;

	public StoryService(IDictionaryService dictionaryService) {
		this.dictionaryService = dictionaryService;
	}

	/**
	 * Returns corrections for any words that are not in the dictionary.
	 *
	 * If the given word is blank, we skip it and don't return any correction.
	 * If the given word is not a valid word (eg has digits or punctuation besides ' or -), we add a correction to an
	 * empty string. Likewise, if no close matches are found for the given word (eg "xxxx"), then we correct it to an
	 * empty string as well. (This can be handled by the UI to show "Invalid with no matches found").
	 *
	 * If the word is already in the dictionary, then we move on to the next word.
	 */
	@Override
	public List<WordCorrection> getCorrections(List<String> words) {
		var corrections = new ArrayList<WordCorrection>();
		for (var word : words) {
			if (word.isBlank()) {
				log.warn("Empty word provided for correction: '{}'. Skipping...", word);
				continue;
			}
			if (!WordValidationUtils.isValidDictionaryWord(word)) {
				log.warn("Invalid word provided for correction: '{}'. Will not attempt to correct...", word);
				corrections.add(new WordCorrection(word, ""));
				continue;
			}
			var wordLowerCase = WordValidationUtils.sanitizeWord(word).toLowerCase();
			if (dictionaryService.hasWord(wordLowerCase)) {
				log.debug("Word '{}' found in dictionary. No correction required.", word);
				continue;
			}
			log.debug("Word '{}' not found in dictionary. Searching for correction...", word);
			var correction = dictionaryService.getClosestWord(wordLowerCase).orElse("");
			if (!correction.isEmpty() && Character.isUpperCase(word.charAt(0))) {
				correction = correction.substring(0, 1).toUpperCase() + correction.substring(1);
			}
			log.debug("Final correction for '{}' is '{}'.", word, correction);
			corrections.add(new WordCorrection(word, correction));
		}
		return corrections;
	}
}
