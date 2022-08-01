package org.rodney.trie;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class TrieTestData {

    public static List<String> seed_list = List.of(
            "The",
            "wall",
            "they're",
            "these",
            "those",
            "zoo",
            "wallace",
            "queen",
            "their",
            "zoology"
            );
    public static final int REPLICATION_COUNT = 3;

    public static List<String> test_words = IntStream.range(0,REPLICATION_COUNT)
            .boxed()
            .flatMap(i -> seed_list.stream())
            .collect(Collectors.toList())
            ;
    public static String joined_words = test_words.stream().collect(Collectors.joining());
    public static byte[] joined_buffer = joined_words.getBytes(StandardCharsets.US_ASCII);

    public static List<String> expected_words = seed_list.stream()
            .map(word -> word.replace("'",""))
            .map(String::toLowerCase)
            .sorted()
            .collect(Collectors.toList())
            ;

    public static void insertAllWords(TrieBuffer trie) {
        char next_trie_node = TrieBuffer.CHAR_0;
        for (int i=0;i<joined_buffer.length;i++) {
            next_trie_node = trie.parse_next_char(next_trie_node, joined_buffer[i]);
        }
        next_trie_node = trie.parse_next_char(next_trie_node, TrieBuffer.SPACE_CHAR);
    }
}
