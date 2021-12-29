package aoc2021;

import common.AocDay;
import common.Utils;

public class Day01Y21 implements AocDay<Long, Long> {

    private final String name;
    private final long[] data;

    public static void main( String[] args ) {
        try {
            executeTasks( "input/2021/Y21D01I.dat", null, null );
        } catch ( Throwable e ) {
            e.printStackTrace();
        }
    }

    public static void executeTasks( String fileName, Long expected1, Long expected2 ) {
        Utils.executeDay( new Day01Y21( fileName ), expected1, expected2 );
    }

    public Day01Y21( String file ) {
        this.name = file;
        data = Utils.lines( file ).mapToLong( Long::parseLong ).toArray();
    }

    @Override
    public String sampleName() {
        return name;
    }

    public Long task1() {
        long increases = 0;
        for ( int index = 1; index < data.length; ++index ) {
            if ( data[index] > data[index - 1] ) {
                ++increases;
            }
        }
        return increases;
    }

    public Long task2() {
        long increases = 0;
        for ( int index = 3; index < data.length; ++index ) {
            if ( data[index] > data[index - 3] ) {
                ++increases;
            }
        }
        return increases;
    }
}
