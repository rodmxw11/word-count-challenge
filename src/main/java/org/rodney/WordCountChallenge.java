package org.rodney;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Stream;

public class WordCountChallenge {
    private static final String input_file = "src/main/resources/kjvbible.htm";
    public static void main(String[] args) {
        //read file into stream, try-with-resources
        try (Stream<String> stream = Files.lines(Paths.get(input_file))) {

            long line_count =  stream.count();
            System.out.println("line_count="+line_count);

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
