// https://adventofcode.com/2023/day/2
package aoc2023;

import common.AocDay;
import common.Utils;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class Day02Y23 implements AocDay<Long, Long> {
    private static final Pattern GAME_PATTERN = Pattern.compile( "Game ([0-9]+):" );
    private static final Pattern COLOR_PATTERN = Pattern.compile( "([0-9]+) (red|green|blue)" );

    private record ShowedSet( long red, long green, long blue ) {
        private boolean isPossible() {
            return red <= 12 && green <= 13 && blue <= 14;
        }

        private long power() {
            return red * green * blue;
        }

        private static ShowedSet max( ShowedSet a, ShowedSet b ) {
            return new ShowedSet(
                    Math.max( a.red, b.red ),
                    Math.max( a.green, b.green ),
                    Math.max( a.blue, b.blue )
            );
        }
    }

    private final String filename;

    public Day02Y23( String filename ) {
        this.filename = filename;
    }

    public static void main( String[] args ) {
        try {
            Utils.executeDay( new Day02Y23( "input/2023/Y23D02S1.DAT" ), 8L, 2286L );
            Utils.executeDay( new Day02Y23( "input/2023/Y23D02I.DAT" ), 3059L, 65371L );
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
        return Files.readAllLines( Path.of( filename ) )
                    .stream()
                    .map( this::gameIdIfPossible )
                    .filter( Objects::nonNull )
                    .mapToLong( Long::valueOf )
                    .sum();
    }

    @Override
    public Long task2() throws Throwable {
        return Files.readAllLines( Path.of( filename ) )
                    .stream()
                    .mapToLong( this::gamePower )
                    .sum();
    }

    private Long gameIdIfPossible( String game ) {
        Matcher matcher = GAME_PATTERN.matcher( game );
        matcher.find();
        return Stream.of( game.substring( matcher.end() ).split( ";" ) )
                     .map( this::handToSet )
                     .allMatch( ShowedSet::isPossible )
                ? Long.parseLong( matcher.group( 1 ) )
                : null;
    }

    private long gamePower( String game ) {
        Matcher matcher = GAME_PATTERN.matcher( game );
        matcher.find();
        return Stream.of( game.substring( matcher.end() ).split( ";" ) )
                     .map( this::handToSet )
                     .reduce( ShowedSet::max )
                     .map( ShowedSet::power )
                     .orElseThrow();
    }

    private ShowedSet handToSet( String hand ) {
        long red = 0;
        long green = 0;
        long blue = 0;
        for ( String color : hand.split( "," ) ) {
            Matcher matcher = COLOR_PATTERN.matcher( color );
            matcher.find();
            long number = Long.parseLong( matcher.group( 1 ) );
            switch ( matcher.group( 2 ) ) {
                case "red":
                    red = number;
                    break;
                case "green":
                    green = number;
                    break;
                case "blue":
                    blue = number;
                    break;
                default:
                    throw new IllegalArgumentException(
                            "Something went wrong (bad color): color = <%s> from hand = <%s>".formatted(
                                    color,
                                    hand
                            )
                    );
            }
        }
        return new ShowedSet( red, green, blue );
    }
}
