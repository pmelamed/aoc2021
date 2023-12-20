// https://adventofcode.com/2023/day/9
package aoc2023;

import common.AocDay;
import common.Utils;

import java.util.Arrays;

public class Day09Y23 implements AocDay<Long, Long> {
    private final String filename;

    public static void main( String[] args ) {
        try {
            Utils.executeSampleDay( new Day09Y23( "input/2023/Y23D09S1.DAT" ), 114L, 2L );
            Utils.executeDay( new Day09Y23( "input/2023/Y23D09I.DAT" ), 1702218515L, 925L );
        } catch ( Throwable e ) {
            e.printStackTrace();
        }
    }

    private Day09Y23( String filename ) {
        this.filename = filename;
    }

    @Override
    public String sampleName() {
        return filename;
    }

    @Override
    public Long task1() throws Throwable {
        return Utils.lines( filename )
                    .map( this::decodeLine )
                    .mapToLong( values -> predictLastValue( values, values.length ) )
                    .sum();
    }

    @Override
    public Long task2() throws Throwable {
        return Utils.lines( filename )
                    .map( this::decodeLine )
                    .mapToLong( values -> predictFirstValue( values, values.length ) )
                    .sum();
    }

    private long[] decodeLine( String line ) {
        return Arrays.stream( line.split( " " ) )
                     .mapToLong( Long::parseLong )
                     .toArray();
    }

    private long predictLastValue( long[] values, int length ) {
        int last = length - 1;
        while ( !Arrays.stream( values ).limit( last + 1 ).allMatch( v -> v == 0 ) ) {
            for ( int index = 0; index < last; index++ ) {
                values[index] = values[index + 1] - values[index];
            }
            last--;
        }
        return Arrays.stream( values ).skip( last ).sum();
    }

    private long predictFirstValue( long[] values, int length ) {
        int first = 0;
        while ( !Arrays.stream( values ).skip( first ).allMatch( v -> v == 0 ) ) {
            for ( int index = length - 1; index > first; index-- ) {
                values[index] = values[index] - values[index - 1];
            }
            first++;
        }
        long aggregate = 0;
        for ( int index = first - 1; index >= 0; index-- ) {
            aggregate = values[index] - aggregate;
        }
        return aggregate;
    }
}
