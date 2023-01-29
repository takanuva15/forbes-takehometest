package com.forbes.takehometest.interfaces;

import com.forbes.takehometest.model.story.WordCorrection;

import java.util.List;

public interface IStoryService {
	List<WordCorrection> getCorrections(List<String> words);
}
