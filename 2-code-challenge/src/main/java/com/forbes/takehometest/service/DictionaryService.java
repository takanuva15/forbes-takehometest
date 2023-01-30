package com.forbes.takehometest.service;

import com.forbes.takehometest.dao.ITrieDao;
import com.forbes.takehometest.dao.IWordStorageDao;
import com.forbes.takehometest.interfaces.IDictionaryService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Serves as an intermediary for handling reads/writes to the dictionary.
 */
@Service
public class DictionaryService implements IDictionaryService {
	private final ITrieDao trieDao;
	private final IWordStorageDao wordStorageDao;

	public DictionaryService(ITrieDao trieDao, IWordStorageDao wordStorageDao) {
		this.trieDao = trieDao;
		this.wordStorageDao = wordStorageDao;
	}

	@Override
	public List<String> getWords() {
		return wordStorageDao.getWords();
	}

	@Override
	public boolean addWord(String word) {
		if (hasWord(word)) {
			return false;
		}
		trieDao.addWord(word);
		wordStorageDao.addWord(word);
		return true;
	}

	@Override
	public boolean removeWord(String word) {
		wordStorageDao.removeWord(word);
		return trieDao.removeWord(word);
	}

	@Override
	public boolean hasWord(String word) {
		return trieDao.hasWord(word);
	}

	/**
	 * Iterates over all close-matches found for a given word and returns the one that would appear first in the
	 * dictionary first based on its sorted order.
	 */
	@Override
	public Optional<String> getClosestWord(String word) {
		var possibleWords = trieDao.getClosestMatchesFor(word);
		return possibleWords.stream().sorted().findFirst();
	}
}
