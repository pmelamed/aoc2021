package aoc2021;

import common.AocDay;
import common.Utils;

import java.util.Arrays;
import java.util.stream.LongStream;

public class Day07Y21 implements AocDay<Long, Long> {

    private final String name;
    private final long[] crabs;
    private final long min;
    private final long max;

    public static void main( String[] args ) {
        try {
            executeTasks( "input/Y21D07S1.dat", 37L, 168L );
            executeTasks( "input/Y21D07I.dat", null, null );
        } catch ( Throwable e ) {
            e.printStackTrace();
        }
    }

    public static void executeTasks( String fileName, Long expected1, Long expected2 ) {
        Utils.executeDay(
                new Day07Y21( fileName ),
                expected1,
                expected2
        );
    }

    public Day07Y21( String file ) {
        this.name = file;
        this.crabs = Utils.lines( file )
                          .flatMap( line -> Arrays.stream( line.split( "," ) ) )
                          .mapToLong( Long::parseLong )
                          .toArray();
        this.min = LongStream.of( this.crabs ).min().orElse( 0L );
        this.max = LongStream.of( this.crabs ).max().orElse( 0L );
    }

    @Override
    public String sampleName() {
        return name;
    }

    public Long task1() {
        return LongStream.rangeClosed( min, max )
                         .map( this::fuelUsageAt )
                         .min()
                         .orElse( 0L );
    }


    public Long task2() {
        return LongStream.rangeClosed( min, max )
                         .map( this::fuelUsageAt2 )
                         .min()
                         .orElse( 0L );
    }

    private long fuelUsageAt( long pos ) {
        return LongStream.of( crabs )
                         .map( crab -> Math.abs( pos - crab ) )
                         .sum();
    }

    private long fuelUsageAt2( long pos ) {
        return LongStream.of( crabs )
                         .map( crab -> movementCost( Math.abs( pos - crab ) ) )
                         .sum();
    }

    private long movementCost( long distance ) {
        return ( 1 + distance ) * distance / 2;
    }
}
