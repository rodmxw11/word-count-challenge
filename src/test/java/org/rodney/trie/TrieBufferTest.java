package org.rodney.trie;

import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.rodney.trie.TrieBuffer.*;
import static org.rodney.trie.TrieTestData.*;

public class TrieBufferTest {
    @Test
    public void ctorTest() {
        TrieBuffer trie = new TrieBuffer(1);
        assertNotNull(trie);
        assertEquals(1, trie.capacity);
        assertNotNull(trie.trie_buffer);
        assertEquals(0, trie.word_count);
        assertEquals(TRIE_ENTRY_ARRAY_SIZE, trie.trie_buffer.length);
    }

    @Test
    public void to_lower_caseTest() {
        // lower case letters are not changed
        for (byte c=LITTLE_A;c<=LITTLE_Z;c++) {
            assertEquals(c, to_lower_case(c));
        }
        // upper case letters are converted to lower case
        for (byte c=BIG_A;c<=BIG_Z;c++) {
            assertEquals(Character.toLowerCase(c), to_lower_case(c) );
        }
        // all other characters return 0
        for (char c=0;c<256;c++) {
            byte b = (byte)c;
            if (b>=LITTLE_A && c<=LITTLE_Z) {
                continue;
            }
            if (b>=BIG_A && b<=BIG_Z) {
                continue;
            }
            assertEquals(CHAR_0, to_lower_case(b));
        }
    }

    @Test
    public void get_next_trieTest_0001() {
        TrieBuffer trie = new TrieBuffer(2);
        char next_trie_node_index = trie.get_next_trie((char)0, (byte)'a');
        assertEquals(1, next_trie_node_index);
        assertEquals(2, trie.next_trie_node_allocation_index);
        assertEquals(1, trie.trie_buffer[0]); // letter 'a' points to trie node #1
        for (int i=1;i<trie.trie_buffer.length;i++) {
            assertEquals(0, trie.trie_buffer[i]);
        }

        // can not allocate a trie node past capacity
        try {
            char next_trie = trie.get_next_trie(next_trie_node_index, (byte)'b');
            fail();
        } catch (ArrayIndexOutOfBoundsException ex) {
        }
    }

    @Test
    public void increment_trie_node_countTest() {
        TrieBuffer trie = new TrieBuffer(2);
        char next_trie_node_index = trie.get_next_trie((char)0, (byte)'i');
        trie.increment_trie_node_count(next_trie_node_index);
        assertEquals(1, trie.trie_buffer[next_trie_node_index*TRIE_ENTRY_ARRAY_SIZE+COUNTER_LOW_OFFSET]);
        assertEquals(1, trie.word_count);
    }

    @Test void insert_word_test0001() {
        TrieBuffer trie = new TrieBuffer(2);
        char trie_index = trie.insert_word("I");
        assertEquals(1, trie_index);
        assertEquals(trie_index, trie.trie_buffer['i'-LITTLE_A]);
        assertEquals(1, trie.trie_buffer[trie_index*TRIE_ENTRY_ARRAY_SIZE+COUNTER_LOW_OFFSET]);
        assertEquals(1, trie.word_count);
    }

    @Test void insert_word_test0002() {
        TrieBuffer trie = new TrieBuffer(20);
        char trie_index = trie.insert_word("The");
        assertEquals(3, trie_index);
        assertEquals(1, trie.trie_buffer[trie_index*TRIE_ENTRY_ARRAY_SIZE+COUNTER_LOW_OFFSET]);
        assertEquals(1, trie.word_count);
    }

    @Test void insert_word_test0003() {
        TrieBuffer trie = new TrieBuffer(20);
        char trie_index_1 = trie.insert_word("The");
        char trie_index_2 = trie.insert_word("There");
        char trie_index_3 = trie.insert_word("their");

        assertEquals(1, trie.trie_buffer[compute_trie_node_count_low_offset(trie_index_1)]);
        assertEquals(1, trie.trie_buffer[compute_trie_node_count_low_offset(trie_index_2)]);
        assertEquals(1, trie.trie_buffer[compute_trie_node_count_low_offset(trie_index_3)]);
        assertEquals(3, trie.word_count);

        char trie_index_4 = trie.insert_word("There");
        assertEquals(trie_index_2, trie_index_4);
        assertEquals(2, trie.trie_buffer[compute_trie_node_count_low_offset(trie_index_2)]);
        assertEquals(3, trie.word_count);

        char trie_index_5 = trie.insert_word("the");
        assertEquals(trie_index_1, trie_index_5);
        assertEquals(2, trie.trie_buffer[compute_trie_node_count_low_offset(trie_index_1)]);
        assertEquals(3, trie.word_count);
    }

    @Test
    public void insert_wordTest0002() {
        TrieBuffer trie = new TrieBuffer(20);
        char trie_index = CHAR_0;
        for (int i=0;i<Character.MAX_VALUE;i++) {
            trie_index = trie.insert_word("the");
        }
        assertEquals(1, trie.word_count);
        assertEquals(Character.MAX_VALUE, trie.trie_buffer[compute_trie_node_count_low_offset(trie_index)]);
        assertEquals(0, trie.trie_buffer[compute_trie_node_count_hi_offset(trie_index)]);

        trie_index = trie.insert_word("THE");
        assertEquals(1, trie.word_count);
        assertEquals(0, trie.trie_buffer[compute_trie_node_count_low_offset(trie_index)]);
        assertEquals(1, trie.trie_buffer[compute_trie_node_count_hi_offset(trie_index)]);
    }

    @Test
    public void is_spaceTest() {
        for (int i=0;i<255;i++) {
            if (i>=0 && i<=SPACE_CHAR) {
                assertTrue(is_space((byte)i),"expected TRUE i="+i);
            } else {
                assertFalse(is_space((byte)i), "expected FALSE i="+i);
            }
        }
    }

    @Test
    public void parse_next_charTest0001() {
        TrieBuffer trie = new TrieBuffer(200);
        char curr_trie_node_index = 0;
        for (int i=0;i<joined_buffer.length;i++) {
            curr_trie_node_index = trie.parse_next_char(
                    curr_trie_node_index,
                    joined_buffer[i]
            );
        }
        // assume the last char in joined_buffer is not space char
        assertNotEquals(0, curr_trie_node_index);

        int current_word_count = trie.word_count;
        curr_trie_node_index = trie.parse_next_char(curr_trie_node_index, SPACE_CHAR);
        assertEquals(current_word_count+1, trie.word_count);
        assertEquals(0, curr_trie_node_index);

        for (int i=0;i<200;i++) {
            curr_trie_node_index = trie.parse_next_char(curr_trie_node_index,SPACE_CHAR);
            assertEquals(0, curr_trie_node_index);
            assertEquals(current_word_count+1, trie.word_count);
        }


    }
}
