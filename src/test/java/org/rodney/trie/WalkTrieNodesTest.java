package org.rodney.trie;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static org.rodney.trie.TrieBuffer.*;
import static org.rodney.trie.TrieTestData.*;

public class WalkTrieNodesTest {
    @Test
    public void ctorTest() {
        TrieBuffer trie = new TrieBuffer(200);
        WalkTrieNodes walker = new WalkTrieNodes(trie);
        assertNotNull(walker);
        assertSame(trie, walker.trie_local);
        WordsAndCounts words = walker.walk_trie_nodes();
        assertNotNull(words);
        assertEquals(0, walker.word_count);
        assertNotNull(walker.trie_buffer_local);
        assertNotNull(walker.char_stack);
        assertTrue(walker.char_stack.length > 10);
    }

    @Test
    public void oneWordTest() {
        TrieBuffer trie = new TrieBuffer(200);
        WalkTrieNodes walker = new WalkTrieNodes(trie);
        trie.insert_word("hello");
        WordsAndCounts words = walker.walk_trie_nodes();
        assertEquals(1, walker.word_count);
        assertNotNull(walker.trie_buffer_local);
        assertNotNull(walker.char_stack);
        assertEquals("hello",words.get_sorted_word(0));
        assertEquals(1, words.get_sorted_count(0));

        // add same word again
        trie.insert_word("hello");
        words = walker.walk_trie_nodes();
        assertEquals(1, walker.word_count);
        assertNotNull(walker.trie_buffer_local);
        assertNotNull(walker.char_stack);
        assertEquals("hello",words.get_sorted_word(0));
        assertEquals(2, words.get_sorted_count(0));

        // add same word again
        trie.insert_word("hello");
        words = walker.walk_trie_nodes();
        assertEquals(1, walker.word_count);
        assertNotNull(walker.trie_buffer_local);
        assertNotNull(walker.char_stack);
        assertEquals("hello",words.get_sorted_word(0));
        assertEquals(3, words.get_sorted_count(0));
    }

    @Test
    public void multiWordTest() {
        TrieBuffer trie = new TrieBuffer(1000);
        WalkTrieNodes walker = new WalkTrieNodes(trie);
        insertAllWords(trie);
        WordsAndCounts words = walker.walk_trie_nodes();
        assertEquals(seed_list.size(), words.getWordCount());

    }
}
