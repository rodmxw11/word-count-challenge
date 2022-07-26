package org.rodney.trie;

import java.util.Arrays;
import java.util.stream.IntStream;

/**
 * Accumulate words and word counts during a TrieBuffer node walk.
 * Once all of the words and counts have been pushed into this class,
 * then sort by descending word count order.
 */
public class WordsAndCounts {
    protected final String[] words;
    protected final int[] counts;

    protected Integer[] count_descending_indices;
    int word_index = 0;

    /**
     * Initialize the word and count buffers
     * @param size
     */
    public WordsAndCounts(int size) {
        words = new String[size];
        counts = new int[size];
    }

    /**
     * Add a word and word count into the buffer
     * @param word
     * @param count
     */
    public void add(String word, int count) {
        words[word_index] = word;
        counts[word_index] = count;
        word_index++;
    }

    /**
     * Sort by descending word count order.
     * Only call this method once all of the words have been added.
     */
    public void sort_by_count_descending() {
        // we will sort the *index* of the word counts instead of the actual word counts.
        // that way, we can use the sorted index to access either the word or the count from
        // the associated arrays.
        count_descending_indices = new Integer[word_index];

        // initialize the index array with ascending index numbers
        IntStream.range(0,word_index)
                .forEach(i -> count_descending_indices[i]=i);

        // sort the index array in descending word count order.
        Arrays.sort(
                count_descending_indices,
                (index_A, index_B) -> counts[index_B]-counts[index_A]
                );
    }

    /**
     * Returns the Nth word that has been sorted in descending word count order.
     *
     * Only call this method <b>after</b> calling sort_by_count_descending() to perform the sorting.
     * @param index
     * @return
     */
    public String get_sorted_word(int index) {
        return words[count_descending_indices[index]];
    }

    /**
     * Returns the Nth word coungt that has been sorted in descending word count order.
     *
     * Only call this method <b>after</b> calling sort_by_count_descending() to perform the sorting.
     * @param index
     * @return
     */
    public int get_sorted_count(int index) {
        return counts[count_descending_indices[index]];
    }
}
