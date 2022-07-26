package org.rodney.trie;

import java.util.Arrays;
import java.util.stream.IntStream;

public class WordsAndCounts {
    protected final String[] words;
    protected final int[] counts;

    protected int[] count_descending_indices;
    int word_index = 0;
    public WordsAndCounts(int size) {
        words = new String[size];
        counts = new int[size];
        count_descending_indices = new int[size];
        IntStream.range(0,size)
                .forEach(i -> count_descending_indices[i]=i);
    }

    public void push(String word, int count) {
        words[word_index] = word;
        counts[word_index] = count;
        word_index++;
    }

    // sort by counts descending
    public void sort_by_count_descending() {
        Arrays.sort(
                count_descending_indices)
    }

}
