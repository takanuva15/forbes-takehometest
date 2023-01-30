package com.forbes.takehometest.model.story;

import lombok.Data;

import java.util.List;

/**
 * API model for returning a list of corrections for a story.
 */
@Data
public class StoryCorrectionsModel {
	private final String story;
	private final List<WordCorrection> unmatchedWords;
}
