package com.forbes.takehometest.controller;

import com.forbes.takehometest.interfaces.IStoryService;
import com.forbes.takehometest.model.story.StoryContainerModel;
import com.forbes.takehometest.model.story.StoryCorrectionsModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * Serves the REST API for providing corrections for a given story.
 */
@Slf4j
@RestController
public class StoryController {
	private final IStoryService storyService;

	public StoryController(IStoryService storyService) {
		this.storyService = storyService;
	}

	@PostMapping("/story")
	public StoryCorrectionsModel correctStory(@RequestBody StoryContainerModel storyContainerModel) {
		var words = storyContainerModel.getStoryWords();
		log.info("Correcting story with {} words...", words.size());
		var corrections = storyService.getCorrections(words);
		return new StoryCorrectionsModel(storyContainerModel.getStory(), corrections);
	}

}
