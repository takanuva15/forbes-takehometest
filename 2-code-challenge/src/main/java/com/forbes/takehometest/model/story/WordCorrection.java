package com.forbes.takehometest.model.story;

import lombok.Data;

/**
 * API model for storing a word with its correction.
 */
@Data
public class WordCorrection {
	private final String word;
	private final String closeMatch;
}
