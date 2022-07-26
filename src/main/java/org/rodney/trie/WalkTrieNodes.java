package org.rodney.trie;

import static org.rodney.trie.TrieBuffer.*;


public class WalkTrieNodes {
    char[] trie_buffer_local;
    int word_count;
    char[] char_stack;
    int stack_depth = 0;
    WordsAndCounts results;

    public WalkTrieNodes(
            TrieBuffer trie_buffer
    ) {
        trie_buffer_local = trie_buffer.trie_buffer;
        word_count = trie_buffer.word_count;
        char_stack = new char[word_count];
        results = new WordsAndCounts(word_count);
    }

    public void walk_trie_node_recurse(char node_index) {
        if (node_index==CHAR_0) {
            return;
        }
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
        walk_trie_node_recurse((char)0);
        return results;
    }
}
