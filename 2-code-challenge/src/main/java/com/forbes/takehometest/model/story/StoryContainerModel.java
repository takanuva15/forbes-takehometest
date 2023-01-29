package com.forbes.takehometest.model.story;

import lombok.Data;

import java.util.List;

@Data
public class StoryContainerModel {
	private String story;

	/**
	 * Convert the story text into a list of words based on spaces in the story.
	 * Leading/trailing spaces & punctuation are removed as well.
	 */
	public List<String> getStoryWords() {
		var words = story.trim().split("\\s+");
		for (int i = 0; i < words.length; i++) {
			words[i] = words[i].trim().replaceAll("^\\p{Punct}", "").replaceAll("\\p{Punct}$", "");
		}
		return List.of(words);
	}
}
