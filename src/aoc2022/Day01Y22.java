package aoc2022;

import common.AocDay;
import common.Utils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.TreeSet;

public class Day01Y22 implements AocDay<Long, Long> {

    private final String name;
    private final long[] data;

    public static void main( String[] args ) {
        try {
            executeTasks( "input/2022/Y22D01I.dat", null, null );
        } catch ( Throwable e ) {
            e.printStackTrace();
        }
    }

    public static void executeTasks( String fileName, Long expected1, Long expected2 ) {
        Utils.executeDay( new Day01Y22( fileName ), expected1, expected2 );
    }

    public Day01Y22( String file ) {
        this.name = file;
        data = Utils.lines( file ).mapToLong( l -> l.isEmpty() ? -1 : Long.parseLong( l ) ).toArray();
    }

    @Override
    public String sampleName() {
        return name;
    }

    public Long task1() {
        long current = 0;
        long maximum = 0;
        for ( long cal : data ) {
            if ( cal == -1 ) {
                maximum = Math.max( current, maximum );
                current = 0;
            } else {
                current += cal;
            }
        }
        return Math.max( current, maximum );
    }

    public Long task2() {
        long current = 0;
        ArrayList<Long> all = new ArrayList<>();
        for ( long cal : data ) {
            if ( cal == -1 ) {
                all.add( current );
                current = 0;
            } else {
                current += cal;
            }
        }
        all.add( current );
        all.sort( Comparator.reverseOrder() );
        return all.get( 0 ) + all.get( 1 ) + all.get( 2 );
    }
}
