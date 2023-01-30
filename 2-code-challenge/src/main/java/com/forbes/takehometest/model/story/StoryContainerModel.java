package com.forbes.takehometest.model.story;

import lombok.Data;

import java.util.List;

/**
 * API model for sending a story to be corrected.
 */
@Data
public class StoryContainerModel {
	private String story;

	/**
	 * Convert the story text into a list of words based on spaces in the story. No sanitization is done.
	 */
	public List<String> getStoryWords() {
		return List.of(story.trim().split("\\s+"));
	}
}
