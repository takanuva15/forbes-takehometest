package com.forbes.takehometest.configuration;

import com.forbes.takehometest.dao.TrieDao;
import com.forbes.takehometest.dao.WordStorageDao;
import com.forbes.takehometest.interfaces.ITrieDao;
import com.forbes.takehometest.interfaces.IWordStorageDao;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Provides the internal "database" instances used for storing the dictionary and lookup Trie. If we moved to an
 * external DB, these could be configured to connect to such database.
 */
@Slf4j
@Configuration
public class CorrectorConfiguration {
	@Bean
	public ITrieDao trieDao() {
		log.debug("Initiating TrieDao instance...");
		return new TrieDao();
	}

	@Bean
	public IWordStorageDao wordStorageDao() {
		log.debug("Initiating WordStorageDao instance...");
		return new WordStorageDao();
	}
}
