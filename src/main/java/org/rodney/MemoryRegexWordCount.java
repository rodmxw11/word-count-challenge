package org.rodney;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.Map;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class MemoryRegexWordCount {
    private static final String input_file = "src/main/resources/kjvbible.txt";
    private static final Pattern word_pattern = Pattern.compile("(?ms)\\s+");
    public static void main(String[] args) throws Exception {
        Path filePath = Path.of(input_file);
        String content = Files.readString(filePath).toLowerCase();
        System.out.println("content.length="+content.length());
        Map<String,Long> word_counts = word_pattern.splitAsStream(content)
                .collect(
                        Collectors.groupingBy(
                                Function.identity(),
                                Collectors.counting()
                        ));

        word_counts.entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .limit(40)
                .forEach(entry -> System.out.println(
                        String.format("%8d  %s",entry.getValue(), entry.getKey())
                ));
    }
}
