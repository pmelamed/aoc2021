// https://adventofcode.com/2023/day/15
package aoc2023;

import common.AocDay;
import common.Utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

public class Day15Y23 implements AocDay<Long, Long> {
    private static final Pattern COMMAND_PATTERN = Pattern.compile( "([a-z]+)([=-])([1-9])?" );
    private final String filename;

    public static void main( String[] args ) {
        try {
            Utils.executeSampleDay( new Day15Y23( "input/2023/Y23D15S1.DAT" ), 1320L, 145L );
            Utils.executeDay( new Day15Y23( "input/2023/Y23D15I.DAT" ), 506269L, 264021L );
        } catch ( Throwable e ) {
            e.printStackTrace();
        }
    }

    private Day15Y23( String filename ) {
        this.filename = filename;
    }

    @Override
    public String sampleName() {
        return filename;
    }

    @Override
    public Long task1() throws Throwable {
        return Arrays.stream( Utils.readFirstLine( filename ).split( "," ) )
                     .mapToLong( this::stringHash )
                     .sum();
    }

    @Override
    public Long task2() throws Throwable {
        Box[] boxes = IntStream.range( 0, 256 )
                               .mapToObj( index -> new Box() )
                               .toArray( Box[]::new );
        Arrays.stream( Utils.readFirstLine( filename ).split( "," ) )
              .forEach( line -> applyLens( line, boxes ) );
        return IntStream.range( 0, 256 )
                        .flatMap( index -> boxes[index].getLensPowers( index ) )
                        .mapToLong( Long::valueOf ).sum();
    }

    private int stringHash( String str ) {
        int result = 0;
        for ( int code : str.toCharArray() ) {
            result = ( result + code ) * 17 & 0xFF;
        }
        return result;
    }

    private void applyLens( String cmd, Box[] boxes ) {
        Matcher matcher = COMMAND_PATTERN.matcher( cmd );
        if ( !matcher.find() ) {
            throw new IllegalArgumentException( "Bad command <%s>".formatted( cmd ) );
        }
        String label = matcher.group( 1 );
        Box box = boxes[stringHash( label )];
        switch ( matcher.group( 2 ).charAt( 0 ) ) {
            case '=':
                box.put( label, matcher.group( 3 ).charAt( 0 ) - '0' );
                break;
            case '-':
                box.remove( label );
                break;
            default:
                throw new IllegalArgumentException(
                        "Bad command sign <%s> in command <%s>".formatted( matcher.group( 2 ), cmd )
                );
        }
    }

    private static int getLensPower( int boxIndex, int lensIndex, int focus ) {
        return focus * ( boxIndex + 1 ) * ( lensIndex + 1 );
    }

    private static class Box {
        private final LinkedHashMap<String, Integer> lenses = new LinkedHashMap<>();

        private void put( String label, int focus ) {
            lenses.put( label, focus );
        }

        private void remove( String label ) {
            lenses.remove( label );
        }

        private IntStream getLensPowers( int boxIndex ) {
            ArrayList<Integer> lensFocuses = new ArrayList<>( lenses.values() );
            return IntStream.range( 0, lenses.size() )
                            .map( slot -> getLensPower(
                                    boxIndex,
                                    slot,
                                    lensFocuses.get( slot )
                            ) );
        }
    }
}
