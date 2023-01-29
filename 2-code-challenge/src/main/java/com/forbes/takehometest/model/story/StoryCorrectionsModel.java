package com.forbes.takehometest.model.story;

import lombok.Data;

import java.util.List;

@Data
public class StoryCorrectionsModel {
	private final String story;
	private final List<WordCorrection> unmatchedWords;
}
