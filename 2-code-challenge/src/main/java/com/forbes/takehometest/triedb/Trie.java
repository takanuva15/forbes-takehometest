package com.forbes.takehometest.triedb;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.*;

/**
 * Implements the Trie structure used to look for word matches and "closest" matches
 */
@Data
public class Trie {
	public static final char WILDCARD = '*';
	private TrieNode root = new TrieNode();

	/**
	 * Adds a word to the Trie
	 */
	public void addWord(String word) {
		if (word.isEmpty()) {
			throw new IllegalArgumentException("Can not add empty word!");
		}
		addWordFromNodeAndIndex(word, root, 0);
	}

	/**
	 * Adds a word to the Trie recursively. If we are at index 0, we will add word[0] to the current node. In addition,
	 * to help facilitate looking for a "closest" match, we will also add a "wildcard" node to the current node and
	 * then iterate recursively with: 1) the new node associated with word[0], and 2) the new node associated with the
	 * wildcard.
	 */
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
		if (!node.hasChild(WILDCARD)) {
			node.children.put(WILDCARD, new TrieNode());
		}
		addWordFromNodeAndIndex(word, node.getChild(WILDCARD), index + 1);
	}

	/**
	 * Checks if the given word is contained within the Trie exactly (no wildcard matches).
	 */
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

	/**
	 * Deletes a word from the Trie.
	 */
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

	/**
	 * Deletes a word from the Trie recursively. If we are at index 0, we will first recursively delete the next
	 * character from our child first. If, after deletion, our child is empty, that means we deleted the only character
	 * available within the child, and thus we'll delete the entire child node as well.
	 * The same logic is executed for the "wildcard" match node as well.
	 */
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
		deleteWordFromNodeAndIndex(word, node.getChild(WILDCARD), index + 1);
		if (node.getChild(WILDCARD).wordsEndingHere.isEmpty()) {
			node.children.remove(WILDCARD);
		}
	}

	/**
	 * Returns a list of the closest matches for the given word. By default, we limit the maximum number of errors
	 * allowed to 50% of the word (rounded up).
	 * Example: If the word is 3 characters, we allow up to 2 errors. If the word is 4 characters, we allow up to 2
	 * errors.
	 * This is done to make the search faster and prevent "unlikely" suggestions, such as correcting "xxxx" to "boat".
	 *
	 * To perform the search, we declare a PriorityQueue that holds instances of [MatchState]. Each match state
	 * records the current index of the word being considered, the node it is on, and the number of errors
	 * accumulated so far during the search. Match states with fewer errors are considered first in the PQ.
	 */
	public List<String> closestMatches(String word) {
		var maxErrorsAllowed = (word.length() + 1) / 2;
		List<String> bestMatches = new ArrayList<>();
		var currMatches = new PriorityQueue<>(List.of(new MatchState(0, root, 0)));
		while (!currMatches.isEmpty()) {
			var currState = currMatches.poll();
			var index = currState.index;
			var node = currState.node;

			// do not attempt to autocorrect the word if it causes more errors than the max allowed errors
			if (currState.numErrors > maxErrorsAllowed) {
				continue;
			}

			// if we're at the end of the word, we check whether we found a word that ends at our current node.
			if (index == word.length()) {
				if (node.wordsEndingHere.isEmpty()) {
					continue;
				}
				// if we found a word and accumulated fewer errors than what was originally the best match, we clear
				// out the old matches and update the best error limit to our new limit.
				if (currState.numErrors < maxErrorsAllowed) {
					bestMatches.clear();
					maxErrorsAllowed = currState.numErrors;
				}
				bestMatches.addAll(node.wordsEndingHere);
				continue;
			}

			var c = word.charAt(index);
			// if we find a perfect match for the current letter, we'll proceed normally.
			if (node.hasChild(c)) {
				currMatches.add(new MatchState(index + 1, node.getChild(c), currState.numErrors));
			}

			// even if we find a perfect match, it's possible that the rest of the word isn't in the dictionary and the
			// current letter being considered is actually a typo. Thus, we'll want to pre-emptively check whether
			// ignoring or skipping this letter leads to a better dictionary match.

			// assume the user made a typo and swapped one letter for another. We'll proceed with the wildcard match.
			if (node.hasChild(WILDCARD)) {
				currMatches.add(new MatchState(index + 1, node.getChild(WILDCARD), currState.numErrors + 1));
			}
			// try assuming the user added an extra letter. We stay on the current node and ignore the current letter
			// by moving on to the next letter
			currMatches.add(new MatchState(index + 1, node, currState.numErrors + 1));
			// try assuming the user forgot a letter. We'll stay on the current letter in the word, but proceed with
			// the wildcard match
			if (node.hasChild(WILDCARD)) {
				currMatches.add(new MatchState(index, node.getChild(WILDCARD), currState.numErrors + 1));
			}
		}
		return bestMatches;
	}

	/**
	 * Stores a match state to use with close-matching. Can be compared to another MatchState based on the number of
	 * errors.
	 */
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

	/**
	 * Represents an individual node in the Trie. Stores a map of children and any words that end here (there may be
	 * multiple entries for wildcard matches)
	 */
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
