package org.rodney.trie;

import static org.rodney.trie.TrieBuffer.*;


public class WalkTrieNodes {

    private static final int LONGEST_ENGLISH_WORD_LENGTH = 50;
    TrieBuffer trie_local;
    char[] trie_buffer_local;
    int word_count;
    char[] char_stack;
    int stack_depth = 0;
    WordsAndCounts results;

    public WalkTrieNodes(
            TrieBuffer trie_buffer
    ) {
        trie_local = trie_buffer;
    }

    public void walk_trie_node_recurse(char node_index) {
        int node_start = compute_trie_buffer_offset(node_index);
        int word_count =
                (trie_buffer_local[node_start+LETTERS_ARRAY_SIZE]<<16)
                        +
                        (trie_buffer_local[node_start+LETTERS_ARRAY_SIZE+1])
                ;
        if (word_count!=0) {
            String word = new String(char_stack, 0, stack_depth);
            results.add(word, word_count);
        }

        for (int i=0;i<LETTERS_ARRAY_SIZE;i++) {
            char next_trie_node = trie_buffer_local[node_start+i];
            if (next_trie_node==CHAR_0) {
                continue;
            }
            // found a new letter ...
            //     push new letter onto char stack
            char_stack[stack_depth++] = (char)(LITTLE_A+i);

            //     *recurse* to new trie node
            walk_trie_node_recurse(next_trie_node);

            //     pop letter off char stack
            stack_depth--;
        } //endfor i
    }

    public WordsAndCounts walk_trie_nodes() {
        word_count = trie_local.word_count;
        trie_buffer_local = trie_local.trie_buffer;
        char_stack = new char[LONGEST_ENGLISH_WORD_LENGTH];
        results = new WordsAndCounts(word_count);

        walk_trie_node_recurse((char)0);
        results.sort_by_count_descending();
        return results;
    }
}
