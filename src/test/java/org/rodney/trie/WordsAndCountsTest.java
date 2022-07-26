package org.rodney.trie;

import org.junit.jupiter.api.Test;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;


public class WordsAndCountsTest {

    @Test
    public void ctorTest() {
       WordsAndCounts word_count = new WordsAndCounts(2);
       assertNotNull(word_count);
       assertNotNull(word_count.words);
       assertEquals(2,word_count.words.length);
       assertNotNull(word_count.counts);
       assertEquals(2,word_count.counts.length);
       assertEquals(0, word_count.word_index);
    }

    @Test
    public void pushTest0001() {
        WordsAndCounts word_count = new WordsAndCounts(2);
        word_count.add("the", 20);
        assertEquals(1, word_count.word_index);
        assertEquals("the", word_count.words[0]);
        assertEquals(20, word_count.counts[0]);

        word_count.add("quick", 9);
        assertEquals(2, word_count.word_index);
        assertEquals("quick", word_count.words[1]);
        assertEquals(9, word_count.counts[1]);

        try {
            word_count.add("brown", 2000);
            fail();
        } catch (ArrayIndexOutOfBoundsException ex) {
        }
    }

    @Test
    public void sort_by_count_descendingTest001() {
        WordsAndCounts word_count = new WordsAndCounts(2);
        word_count.sort_by_count_descending();
        assertNotNull(word_count.count_descending_indices);
        assertEquals(0, word_count.count_descending_indices.length);

        word_count.add("the", 22);
        word_count.sort_by_count_descending();
        assertNotNull(word_count.count_descending_indices);
        assertEquals(1, word_count.count_descending_indices.length);
        assertEquals(0,word_count.count_descending_indices[0]);

        word_count.add("quick", 999);
        word_count.sort_by_count_descending();
        assertNotNull(word_count.count_descending_indices);
        assertEquals(2, word_count.count_descending_indices.length);
        assertEquals(1,word_count.count_descending_indices[0]);
        assertEquals(0,word_count.count_descending_indices[1]);
    }

    @Test
    public void sort_by_count_descendingTest002() {
        WordsAndCounts word_count = new WordsAndCounts(15);
        List<String> test_words = List.of(
                "1",
                "999999999",
                "",
                "7777777",
                "22",
                "55555",
                "333",
                "88888888",
                "4444",
                "666666"
        );
        test_words.stream()
                .forEach(word -> word_count.add(word,word.length()));
        assertEquals(test_words.size(), word_count.word_index);
        word_count.sort_by_count_descending();
        assertEquals("999999999", word_count.get_sorted_word(0));
        assertEquals(9, word_count.get_sorted_count(0));

        List<String> sorted_test_words = test_words.stream()
                .sorted(Comparator.reverseOrder())
                .collect(Collectors.toList());
        IntStream.range(0,sorted_test_words.size())
                .forEach( i -> {
                    assertEquals(sorted_test_words.get(i), word_count.get_sorted_word(i));
                    assertEquals(
                            sorted_test_words.get(i).length(),
                            word_count.get_sorted_count(i)
                    );
                });
    }
}
