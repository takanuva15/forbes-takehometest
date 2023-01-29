package com.forbes.takehometest.triedb;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.*;

@Data
public class Trie {
	private TrieNode root = new TrieNode();

	public void addWord(String word) {
		if (word.isEmpty()) {
			throw new IllegalArgumentException("Can not add empty word!");
		}
		addWordFromNodeAndIndex(word, root, 0);
	}

	private void addWordFromNodeAndIndex(String word, TrieNode node, int index) {
		if (node == null) {
			throw new IllegalStateException("Node must not be null!");
		}
		if (index > word.length()) {
			throw new IllegalArgumentException("Index can not be greater the length of word!");
		}
		if (index == word.length()) {
			node.addWordEndingHere(word);
			return;
		}
		var c = word.charAt(index);
		if (!node.hasChild(c)) {
			node.children.put(c, new TrieNode());
		}
		addWordFromNodeAndIndex(word, node.getChild(c), index + 1);
		if (!node.hasChild('*')) {
			node.children.put('*', new TrieNode());
		}
		addWordFromNodeAndIndex(word, node.getChild('*'), index + 1);
	}

	public boolean contains(String word) {
		var curr = root;
		for (char c : word.toCharArray()) {
			if (!curr.hasChild(c)) {
				return false;
			}
			curr = curr.getChild(c);
		}
		return curr.wordsEndingHere.contains(word);
	}

	public boolean deleteWord(String word) {
		if (word.isEmpty()) {
			throw new IllegalArgumentException("Can not add delete empty word!");
		}
		if (!contains(word)) {
			return false;
		}
		deleteWordFromNodeAndIndex(word, root, 0);
		return true;
	}

	private void deleteWordFromNodeAndIndex(String word, TrieNode node, int index) {
		if (node == null) {
			throw new IllegalStateException("Node must not be null!");
		}
		if (index > word.length()) {
			throw new IllegalArgumentException("Index can not be greater the length of word!");
		}
		if (index == word.length()) {
			node.wordsEndingHere.remove(word);
			return;
		}
		var c = word.charAt(index);
		deleteWordFromNodeAndIndex(word, node.getChild(c), index + 1);
		if (node.getChild(c).wordsEndingHere.isEmpty()) {
			node.children.remove(c);
		}
		deleteWordFromNodeAndIndex(word, node.getChild('*'), index + 1);
		if (node.getChild('*').wordsEndingHere.isEmpty()) {
			node.children.remove('*');
		}
	}

	public List<String> closestMatches(String word) {
		var maxErrorsAllowed = (word.length() + 1) / 2;
		List<String> bestMatches = new ArrayList<>();
		var currMatches = new PriorityQueue<>(List.of(new MatchState(0, root, 0)));
		while (!currMatches.isEmpty()) {
			var currState = currMatches.poll();
			var index = currState.index;
			var node = currState.node;

			if (index == word.length()) {
				if (node.wordsEndingHere.isEmpty()) {
					continue;
				}
				if (currState.numErrors < maxErrorsAllowed) {
					bestMatches.clear();
					maxErrorsAllowed = currState.numErrors;
				}
				bestMatches.addAll(node.wordsEndingHere);
				continue;
			}

			var c = word.charAt(index);
			// found a perfect match for the current letter
			if (node.hasChild(c)) {
				currMatches.add(new MatchState(index + 1, node.getChild(c), currState.numErrors));
			}
			// do not attempt to autocorrect the word if it causes more errors than the max allowed errors
			if (currState.numErrors == maxErrorsAllowed) {
				continue;
			}
			// assume the user made a typo and swapped one letter for another
			if (node.hasChild('*')) {
				currMatches.add(new MatchState(index + 1, node.getChild('*'), currState.numErrors + 1));
			}
			// try assuming the user added an extra letter
			currMatches.add(new MatchState(index + 1, node, currState.numErrors + 1));
			// try assuming the user forgot a letter
			if (node.hasChild('*')) {
				currMatches.add(new MatchState(index, node.getChild('*'), currState.numErrors + 1));
			}
		}
		return bestMatches;
	}

	@AllArgsConstructor
	private static class MatchState implements Comparable<MatchState> {
		private int index;
		private TrieNode node;
		private int numErrors;

		@Override
		public int compareTo(MatchState otherMatchState) {
			return Integer.compare(numErrors, otherMatchState.numErrors);
		}
	}

	@Data
	static class TrieNode {
		private Set<String> wordsEndingHere = new HashSet<>();
		private Map<Character, TrieNode> children = new HashMap<>();

		boolean hasChild(char c) {
			return children.containsKey(c);
		}

		TrieNode getChild(char c) {
			return children.get(c);
		}

		void addWordEndingHere(String word) {
			wordsEndingHere.add(word);
		}
	}
}
