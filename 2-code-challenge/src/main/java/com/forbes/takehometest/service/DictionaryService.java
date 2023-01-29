package com.forbes.takehometest.service;

import com.forbes.takehometest.dao.ITrieDao;
import com.forbes.takehometest.dao.IWordStorageDao;
import com.forbes.takehometest.interfaces.IDictionaryService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

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

	@Override
	public Optional<String> getClosestWord(String word) {
		var possibleWords = trieDao.getClosestMatchesFor(word);
		String wordToPick = null;
		long earliestEntry = Long.MAX_VALUE;
		for (var possibleWord : possibleWords) {
			var wordIndex = wordStorageDao.getWordIndex(possibleWord);
			if (wordIndex < earliestEntry) {
				wordToPick = possibleWord;
				earliestEntry = wordIndex;
			}
		}
		return Optional.ofNullable(wordToPick);
	}
}
