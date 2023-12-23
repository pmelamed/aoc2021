// https://adventofcode.com/2023/day/6
package aoc2023;

import common.AocDay;
import common.Utils;

import java.util.List;
import java.util.stream.IntStream;

public class Day06Y23 implements AocDay<Long, Long> {
    private final String filename;

    public static void main( String[] args ) {
        try {
            Utils.executeDay( new Day06Y23( "input/2023/Y23D06S1.DAT" ), 288L, 71503L );
            Utils.executeDay( new Day06Y23( "input/2023/Y23D06I.DAT" ), 211904L, 43364472L );
        } catch ( Throwable e ) {
            e.printStackTrace();
        }
    }

    private Day06Y23( String filename ) {
        this.filename = filename;
    }

    @Override
    public String sampleName() {
        return filename;
    }

    @Override
    public Long task1() throws Throwable {
        List<String> lines = Utils.readLines( filename );
        long[] times = getValues( lines.get( 0 ) );
        long[] records = getValues( lines.get( 1 ) );
        return IntStream.range( 0, times.length )
                        .mapToLong( index -> winningRangeWidth( times[index], records[index] ) )
                        .reduce( 1L, ( a, b ) -> a * b );
    }

    @Override
    public Long task2() throws Throwable {
        long[] values = Utils.lines( filename )
                             .mapToLong( this::getBigValue )
                             .toArray();
        return winningRangeWidth( values[0], values[1] );
    }

    private long winningRangeWidth( double time, double record ) {
        double d = time * time - 4 * record;
        if ( d <= 0 ) {
            return 0L;
        }
        double dsqrt = Math.sqrt( d );
        double x1 = Math.ceil( Math.nextUp( ( time - dsqrt ) / 2 ) );
        double x2 = Math.floor( Math.nextDown( ( time + dsqrt ) / 2 ) );
        return (long) Math.max( 0, x2 - x1 + 1 );
    }

    private long[] getValues( String line ) {
        return Utils.matchGroups( line, "[0-9]+" )
                    .mapToLong( Long::parseUnsignedLong )
                    .toArray();
    }

    private long getBigValue( String line ) {
        return line.chars().filter( c -> c >= '0' && c <= '9' )
                   .mapToLong( c -> c - '0' )
                   .reduce( 0L, ( r, d ) -> r * 10 + d );
    }
}
