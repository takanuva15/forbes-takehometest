package com.forbes.takehometest.controller;

import com.forbes.takehometest.interfaces.IStoryService;
import com.forbes.takehometest.model.story.StoryContainerModel;
import com.forbes.takehometest.model.story.StoryCorrectionsModel;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * Serves the REST API for providing corrections for a given story.
 */
@RestController
public class StoryController {
	private final IStoryService storyService;

	public StoryController(IStoryService storyService) {
		this.storyService = storyService;
	}

	@PostMapping("/story")
	public StoryCorrectionsModel correctStory(@RequestBody StoryContainerModel storyContainerModel) {
		var corrections = storyService.getCorrections(storyContainerModel.getStoryWords());
		return new StoryCorrectionsModel(storyContainerModel.getStory(), corrections);
	}

}
