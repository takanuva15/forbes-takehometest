package com.forbes.takehometest.interfaces;

import com.forbes.takehometest.model.story.WordCorrection;

import java.util.List;

/**
 * Defines the methods that a story service should support for correcting a story
 */
public interface IStoryService {
	List<WordCorrection> getCorrections(List<String> words);
}
