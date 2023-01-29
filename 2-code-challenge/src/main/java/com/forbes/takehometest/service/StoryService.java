package com.forbes.takehometest.service;

import com.forbes.takehometest.interfaces.IDictionaryService;
import com.forbes.takehometest.interfaces.IStoryService;
import com.forbes.takehometest.model.story.WordCorrection;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class StoryService implements IStoryService {
	private final IDictionaryService dictionaryService;

	public StoryService(IDictionaryService dictionaryService) {
		this.dictionaryService = dictionaryService;
	}

	@Override
	public List<WordCorrection> getCorrections(List<String> words) {
		var corrections = new ArrayList<WordCorrection>();
		for (var word : words) {
			var wordLowerCase = word.toLowerCase();
			if (dictionaryService.hasWord(wordLowerCase)) {
				continue;
			}
			var correction = dictionaryService.getClosestWord(wordLowerCase).orElse("");
			if (!correction.isEmpty() && Character.isUpperCase(word.charAt(0))) {
				correction = correction.substring(0, 1).toUpperCase() + correction.substring(1);
			}
			corrections.add(new WordCorrection(word, correction));
		}
		return corrections;
	}
}
