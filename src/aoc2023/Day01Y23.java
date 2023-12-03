package aoc2023;

import common.AocDay;
import common.Utils;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Day01Y23 implements AocDay<Long, Long> {
    private static final Pattern DIGITS_PATTERN =
            Pattern.compile( "[0-9]|one|two|three|four|five|six|seven|eight|nine" );
    private final String filename;

    public static final Map<String, Long> word2digit = new HashMap<>( Map.of(
            "one", 1L,
            "two", 2L,
            "three", 3L,
            "four", 4L,
            "five", 5L,
            "six", 6L,
            "seven", 7L,
            "eight", 8L,
            "nine", 9L
    ) );

    static {
        word2digit.putAll( Map.of(
                "1", 1L,
                "2", 2L,
                "3", 3L,
                "4", 4L,
                "5", 5L,
                "6", 6L,
                "7", 7L,
                "8", 8L,
                "9", 9L
        ) );
    }

    public Day01Y23( String filename ) {
        this.filename = filename;
    }

    public static void main( String[] args ) {
        try {
            Utils.executeDay( new Day01Y23( "input/2023/Y23D01S1.DAT" ), 142L, null );
            Utils.executeDay( new Day01Y23( "input/2023/Y23D01S2.DAT" ), null, 281L );
            Utils.executeDay( new Day01Y23( "input/2023/Y23D01I.DAT" ), 55123L, null );
        } catch ( Throwable e ) {
            e.printStackTrace();
        }
    }

    @Override
    public String sampleName() {
        return filename;
    }

    @Override
    public Long task1() throws Throwable {
        return Files.readAllLines( Path.of( filename ) ).stream().mapToLong( this::lineToNumber ).sum();
    }

    private long lineToNumber( String line ) {
        long first = -1;
        long last = 0;
        for ( char ch : line.toCharArray() ) {
            if ( ch >= '0' && ch <= '9' ) {
                if ( first == -1 ) {
                    first = ch - '0';
                }
                last = ch - '0';
            }
        }
        return first * 10 + last;
    }

    @Override
    public Long task2() throws Throwable {
        return Files.readAllLines( Path.of( filename ) ).stream().mapToLong( this::lineToNumberExt ).sum();
    }

    private long lineToNumberExt( String line ) {
        long first = -1;
        long last = 0;
        int start = 0;
        Matcher matcher = DIGITS_PATTERN.matcher( line );
        while ( matcher.find( start ) ) {
            long digit = word2digit.get( matcher.group() );
            if ( first == -1 ) {
                first = digit;
            }
            last = digit;
            start = matcher.start() + 1;
        }
        return first * 10 + last;
    }
}
