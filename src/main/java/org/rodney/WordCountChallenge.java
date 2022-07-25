package org.rodney;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class WordCountChallenge {
    private static final String input_file = "src/main/resources/kjvbible.txt";
    public static void main(String[] args) {
        //read file into stream, try-with-resources
        try (Stream<String> stream = Files.lines(Paths.get(input_file))) {

            Map<String,Long> word_counts = stream
                    .map(String::toLowerCase)
                    .flatMap(line -> Arrays.stream(line.split("\\s+")))
                    .filter(word -> word.length()>0)
                            .collect(
                                    Collectors.groupingBy(
                                            Function.identity(),
                                            Collectors.counting()
                                            ));
            word_counts.entrySet().stream()
                    .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                    .limit(20)
                    .forEach(entry -> System.out.println(
                            String.format("%8d  %s",entry.getValue(), entry.getKey())
                    ));

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
