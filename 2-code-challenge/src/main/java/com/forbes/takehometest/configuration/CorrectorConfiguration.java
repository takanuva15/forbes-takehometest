package com.forbes.takehometest.configuration;

import com.forbes.takehometest.dao.ITrieDao;
import com.forbes.takehometest.dao.IWordStorageDao;
import com.forbes.takehometest.dao.TrieDao;
import com.forbes.takehometest.dao.WordStorageDao;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Provides the internal "database" instances used for storing the dictionary and lookup Trie. If we moved to an
 * external DB, these could be configured to connect to such database.
 */
@Configuration
public class CorrectorConfiguration {
	@Bean
	public ITrieDao trieDao() {
		return new TrieDao();
	}

	@Bean
	public IWordStorageDao wordStorageDao() {
		return new WordStorageDao();
	}
}
