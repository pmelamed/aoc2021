package aoc2019;

import common.AocDay;
import common.Utils;

public class Day01Y19 implements AocDay<Long, Long> {

    private final String name;

    public static void main( String[] args ) {
        try {
            executeTasks( "input/2019/d01i.dat", null, null );
        } catch ( Throwable e ) {
            e.printStackTrace();
        }
    }

    public static void executeTasks( String fileName, Long expected1, Long expected2 ) {
        Utils.executeDay( new Day01Y19( fileName ), expected1, expected2 );
    }

    public Day01Y19( String file ) {
        this.name = file;
    }

    @Override
    public String sampleName() {
        return name;
    }

    public Long task1() {
        return Utils.lines( name ).mapToLong( Long::parseLong ).map( size -> size / 3 - 2 ).sum();
    }

    public Long task2() {
        return Utils.lines( name ).mapToLong( Long::parseLong ).map( this::moduleFuel ).sum();
    }

    private long moduleFuel( long moduleMass ) {
        long fuel = moduleMass / 3 - 2;
        long remainder = fuel;
        do {
            remainder = remainder / 3 - 2;
            if ( remainder > 0 ) {
                fuel += remainder;
            }
        } while ( remainder > 0 );
        return fuel;
    }
}
