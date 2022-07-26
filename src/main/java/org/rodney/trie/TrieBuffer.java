package org.rodney.trie;

public class TrieBuffer {

    private static final boolean CHECKS = true;
    protected static final char CHAR_0 = (char)0;
    protected static final char BIG_A = 'A';
    protected static final char BIG_Z = 'Z';
    
    protected static final char LITTLE_A = 'a';
    protected static final char LITTLE_Z = 'z';
    
    protected static final int LETTERS_ARRAY_SIZE = LITTLE_Z-LITTLE_A+1; // 26*2 === 52 bytes next letter indices
    protected static final int COUNTER_ARRAY_SIZE = 2; // 2*2 === 4 bytes word counter
    
    protected static final int COUNTER_LOW_OFFSET = LETTERS_ARRAY_SIZE+COUNTER_ARRAY_SIZE-1;
    protected static final int COUNTER_HI_OFFSET = LETTERS_ARRAY_SIZE+COUNTER_ARRAY_SIZE-2;
    protected static final int TRIE_ENTRY_ARRAY_SIZE = LETTERS_ARRAY_SIZE+COUNTER_ARRAY_SIZE;
    
    protected final int capacity;
    protected final char[] trie_buffer;

    protected int word_count = 0;
    
    protected char next_trie_node_allocation_index = 1;
    
    public TrieBuffer(int capacity) {
        this.capacity = capacity;
        this.trie_buffer = new char[capacity*TRIE_ENTRY_ARRAY_SIZE];
    }

    public static final int compute_trie_buffer_offset(char trie_index) {
        return trie_index * TRIE_ENTRY_ARRAY_SIZE;
    }

    public static final int compute_trie_node_letter_offset(char trie_index, char lower_letter) {
        return compute_trie_buffer_offset(trie_index)+(lower_letter-LITTLE_A);
    }

    public static final int compute_trie_node_count_low_offset(char trie_index) {
        return compute_trie_buffer_offset(trie_index)+COUNTER_LOW_OFFSET;
    }

    public static final int compute_trie_node_count_hi_offset(char trie_index) {
        return compute_trie_buffer_offset(trie_index)+COUNTER_HI_OFFSET;
    }

    /**
     * Converts a character to a lower case character.
     * Characters between 'A' and 'Z' are converted to lower case.
     * Characters between 'a' and 'z' are returned as is
     * All other characters return 0
     * @param c
     * @return CHAR_0, or 'a'..'z'
     */
    public static final char to_lower_case(char c) {
        char c_lower = c;
        if (c_lower>=BIG_A && c_lower<=BIG_Z) {
            c_lower = (char)(LITTLE_A + (c_lower-BIG_A));
        }
        if (c_lower<LITTLE_A || c_lower>LITTLE_Z) {
            c_lower = CHAR_0;
        }
        return c_lower;
    }

    public char get_next_trie(char current_trie_node_index, char next_letter) {
        // convert next_letter to lower case
        char next_letter_lower = to_lower_case(next_letter);

        // if next_letter is not valid, do not advance to next trie node
        if (next_letter_lower==CHAR_0) {
            return current_trie_node_index;
        }

        if (CHECKS) assert current_trie_node_index<capacity;

        // compute offset of next trie node index in trie_buffer
        int next_letter_trie_node_index = compute_trie_node_letter_offset(
                current_trie_node_index,
                next_letter_lower
        );

        char next_trie_node_index = trie_buffer[next_letter_trie_node_index];

        // if there is no entry at next_trie_node_index, allocate new trie node
        if (next_trie_node_index==CHAR_0) {
            next_trie_node_index = next_trie_node_allocation_index++;
            if (next_trie_node_index>=capacity) {
                throw new ArrayIndexOutOfBoundsException();
            }
            trie_buffer[next_letter_trie_node_index] = next_trie_node_index;
        }
        if (CHECKS) assert next_trie_node_index!=0;
        return next_trie_node_index;
    }

    public char insert_word(String word) {
        char[] chars = word.toCharArray();
        char curr_trie_index = 0;
        for (int i=0;i<chars.length;i++) {
            curr_trie_index = get_next_trie(curr_trie_index, chars[i]);
        }
        increment_trie_node_count(curr_trie_index);
        return curr_trie_index;
    }

    public void increment_trie_node_count(char current_trie_node_index) {
        if (CHECKS) assert current_trie_node_index<capacity;
        int trie_node_buff_offset = compute_trie_node_count_low_offset(current_trie_node_index);
        char new_node_count = ++trie_buffer[trie_node_buff_offset];
        if (new_node_count==0) {
            trie_buffer[trie_node_buff_offset-1]++;
        } else if (new_node_count==1) {
            word_count++;
        }
    }

    public static class WordsAndCounts {
        protected final String[] words;
        protected final int[] counts;
        int word_index = 0;
        public WordsAndCounts(int size) {
            words = new String[size];
            counts = new int[size];
        }

        public void push(String word, int count) {
            words[word_index] = word;
            counts[word_index] = count;
            word_index++;
        }
    }
    public Object[] get_words_and_counts() {
        char[] word_buffer = new char[256];
        char[] trie_node_stack = new char[256];
        int[] index_counts = new int[256];
        int current_index_offset = 0;


        

        return new Object[] {words, counts};
    }
}
