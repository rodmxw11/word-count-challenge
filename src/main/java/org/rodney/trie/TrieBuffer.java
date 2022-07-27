package org.rodney.trie;

/**
 * Implement a Trie data structure in order to perform a word count for a body of text.
 * <br/>
 * Here a Trie node is represented as a contiguous array of 26 unsigned shorts (implemented as char[26] array),
 * followed by a 32 bit unsigned integer (implemented as a char[2] array) that represents a word count.
 * <br/>
 * All of the Trie nodes are embedded in a large char[] array.
 * <br/>
 * Only the lower case letters of each word are stored in this Trie.
 * <br/>
 * Each Trie node has an array entry the next letters in a word. This array entry contains the index
 * of the next Trie node for that letter.
 * <br/>
 * The indexed Trie node will contain a word count for the terminal letter in a word.
 * <br/>
 * <b>NOTE:</b>  This implementation only stores lower case versions of the alphabetic characters in words.
 * Numbers and punctuation characters will be skipped.
 */
public class TrieBuffer {

    private static final boolean CHECKS = true;
    protected static final byte CHAR_0 = 0;
    protected static final byte BIG_A = (byte)'A';
    protected static final byte BIG_Z = (byte)'Z';
    
    protected static final byte LITTLE_A = (byte)'a';
    protected static final byte LITTLE_Z = (byte)'z';

    protected static final byte SPACE_CHAR = (byte)' ';
    protected static final int LETTERS_ARRAY_SIZE = LITTLE_Z-LITTLE_A+1; // 26*2 === 52 bytes next letter indices
    protected static final int COUNTER_ARRAY_SIZE = 2; // 2*2 === 4 bytes word counter
    
    protected static final int COUNTER_LOW_OFFSET = LETTERS_ARRAY_SIZE+COUNTER_ARRAY_SIZE-1;
    protected static final int COUNTER_HI_OFFSET = LETTERS_ARRAY_SIZE+COUNTER_ARRAY_SIZE-2;
    protected static final int TRIE_ENTRY_ARRAY_SIZE = LETTERS_ARRAY_SIZE+COUNTER_ARRAY_SIZE;
    
    protected final int capacity;

    // logical concatenation of all of the Trie node arrays
    protected final char[] trie_buffer;

    // how many words are contained in this TrieBuffer
    protected int word_count = 0;


    // index of next Trie node to be allocated in trie_buffer.
    // the actual offset of the allocated trie node is next_trie_node_allocation_index*TRIE_ENTRY_ARRAY_SIZE
    protected char next_trie_node_allocation_index = 1;

    /**
     * Initialize the TrieBuffer
     * @param capacity max number of trie nodes in this buffer.
     */
    public TrieBuffer(int capacity) {
        this.capacity = capacity;
        this.trie_buffer = new char[capacity*TRIE_ENTRY_ARRAY_SIZE];
    }

    /**
     * Given a logic trie node index, compute its offset in trie_buffer array
     * @param trie_index
     * @return
     */
    public static final int compute_trie_buffer_offset(char trie_index) {
        return trie_index * TRIE_ENTRY_ARRAY_SIZE;
    }

    /**
     * Given a trie node index and a lower case letter, compute the offset in trie_buffer of
     * that letter's next trie node index
     * @param trie_index
     * @param lower_letter a lower case letter 'a'..'z'
     * @return
     */
    public static final int compute_trie_node_letter_offset(char trie_index, byte lower_letter) {
        return compute_trie_buffer_offset(trie_index)+(lower_letter-LITTLE_A);
    }

    /**
     * Given a trie node index, compute the offset in trie_buffer of the lower 2 bytes of the
     * word count.
     * @param trie_index
     * @return
     */
    public static final int compute_trie_node_count_low_offset(char trie_index) {
        return compute_trie_buffer_offset(trie_index)+COUNTER_LOW_OFFSET;
    }

    /**
     * Given a trie node index, compute the offset in trie_buffer of the upper 2 bytes of the
     * word count.
     * @param trie_index
     * @return
     */
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
    public static final byte to_lower_case(byte c) {
        byte c_lower = c;
        if (c_lower>=BIG_A && c_lower<=BIG_Z) {
            c_lower = (byte)(LITTLE_A + (c_lower-BIG_A));
        }
        if (c_lower<LITTLE_A || c_lower>LITTLE_Z) {
            c_lower = CHAR_0;
        }
        return c_lower;
    }

    /**
     * Given a trie node logical index and an Ascii character,
     * return the logical index of the next trie node.
     * <br/>
     * A new trie node will be allocated if one does not exist in the current trie node
     * for the next_letter.
     * @param current_trie_node_index logical index of current trie node
     * @param next_letter will be converted to a lower case Ascii letter
     * @return 0 if next_letter was not alphabetic, else the logical index of the next trie node
     */
    public char get_next_trie(char current_trie_node_index, byte next_letter) {
        // convert next_letter to lower case
        byte next_letter_lower = to_lower_case(next_letter);

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

        // get logical index of next trie node for this letter ...
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

    public static final boolean is_space(byte b) {
        return b<=SPACE_CHAR;
    }

    /**
     * Inserts next character into the TrieBuffer. If the next_character
     * <br/>
     * Note that if parse_next_char() returns a non-zero return value on the last character in a buffer,
     * then you will have to call increment_trie_node_count() on the return value for the last character.
     * @param current_trie_index 0 for first character in a word else the logical trie node index returned by the previous
     *                   invocation of parse_next_char()
     * @param next_char the next character
     * @return logical trie node index after inserting next_char;
     */
    public char parse_next_char(char current_trie_index, byte next_char) {
        // skip over spaces
        if (is_space(next_char)) {
            if (current_trie_index!=CHAR_0) {
                // if space char and we are in a word ... then increment word count and reset current_trie_index to zero
                increment_trie_node_count(current_trie_index);
                current_trie_index = 0;
            } //endif current_trie_index!=CHAR_0
            // else skip this space char
        } else {
            // not a space char add it to trie buffer
            current_trie_index = get_next_trie(current_trie_index, next_char);
        } //endelse is_space(b)
        return current_trie_index;
    }

    /**
     * Insert a word into the TrieBuffer
     * @param word a word; alphabetic characters are converted to lower case; non-alphabetic characters are skipped
     * @return logical index of trie node for the last character in the word; This is the trie node that contains
     * the word count for this word.
     */
    public char insert_word(String word) {
        // for each character in word ...
        char[] chars = word.toCharArray();
        char curr_trie_index = 0;
        for (int i=0;i<chars.length;i++) {
            // insert it into the TrieBuffer
            curr_trie_index = get_next_trie(curr_trie_index, (byte)chars[i]);
        }
        // curr_trie_index is the trie node for the last character in the word;
        // this is where the word's word count will be maintained
        increment_trie_node_count(curr_trie_index);
        return curr_trie_index;
    }

    /**
     * Increment a word count in the trie node for the last character of a word
     * @param current_trie_node_index logical index of trie node for last characters of a word
     */
    public void increment_trie_node_count(char current_trie_node_index) {
        if (CHECKS) assert current_trie_node_index<capacity;
        // compute offset to lower 2 bytes of word count
        int trie_node_buff_offset = compute_trie_node_count_low_offset(current_trie_node_index);
        // increment lower 2 bytes of word count
        char new_node_count = ++trie_buffer[trie_node_buff_offset];

        if (new_node_count==0) {
            // if lower 2 bytes overflows back to zero, then increment upper 2 bytes of word count
            trie_buffer[trie_node_buff_offset-1]++;
        } else if (new_node_count==1) {
            // this is a new word in TrieBuffer if the new_node_count is 1
            word_count++;
        }
    }
}
