package aoc2019;

import common.AocDay;
import common.Utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class Day19Y19 implements AocDay<Integer, Integer> {

    private static long[] INPUT_BUFFER = { 0, 0 };
    private static long[] OUTPUT_BUFFER = { 0 };
    private final String name;
    private final int size;
    private final IntComputer.Ram ram;

    public static void main( String[] args ) {
        try {
            // executeTasks( "input/2019/d19i.dat", 10, 27, null );
            executeTasks( "input/2019/d19i.dat", 50, null, null );
        } catch ( Throwable e ) {
            e.printStackTrace();
        }
    }

    public static void executeTasks( String fileName, int size, Integer expected1, Integer expected2 ) {
        Utils.executeDay(
                new Day19Y19( fileName, size ),
                expected1,
                expected2
        );
    }

    private Day19Y19( String file, int size ) {
        this.name = file;
        this.size = size;
        ram = new IntComputer.Ram( Utils.readLines( file ).get( 0 ) );
    }

    @Override
    public String sampleName() {
        return "%d x %d".formatted( size, size );
    }

    public Integer task1() throws InterruptedException {
        int count = 0;
        System.out.println();
        for ( int row = 0; row < size; row++ ) {
            for ( int col = 0; col < size; col++ ) {
                INPUT_BUFFER[0] = col;
                INPUT_BUFFER[1] = row;
                IntComputer.fromRam( ram ).interpret(
                        IntComputer.arrayInput( INPUT_BUFFER ),
                        IntComputer.arrayOutput( OUTPUT_BUFFER )
                );
                if ( OUTPUT_BUFFER[0] != 0 ) {
                    count++;
                }
                System.out.print( OUTPUT_BUFFER[0] != 0 ? "#" : "." );
            }
            System.out.println();
        }
        return count;
    }

    public Integer task2() throws InterruptedException {
        List<BeamRange> ray = new ArrayList<>( 200 );
        int row;
        for ( row = 0; row < 100; row++ ) {
            ray.add( scanLine( row ) );
        }
        while ( true ) {
            BeamRange range = scanLine( row );
            ray.add( range );
            if( ray.get( row - 99 ).end >= range.start + 99 ) {
                return range.start * 10_000 + row - 99;
            }
            row++;
        }
    }

    private BeamRange scanLine( int row ) {
        int x1 = 0;
        while ( !scanPoint( row, x1 ) && x1 < row * 2 ) {
            x1++;
        }
        if ( x1 >= row * 2 ) {
            return new BeamRange( -1, -1 );
        }
        int x2 = x1 + 1;
        while ( scanPoint( row, x2 ) ) {
            x2++;
        }
        return new BeamRange( x1, x2 - 1 );
    }

    private boolean scanPoint( int row, int x ) {
        INPUT_BUFFER[0] = x;
        INPUT_BUFFER[1] = row;
        IntComputer.fromRam( ram ).interpret(
                IntComputer.arrayInput( INPUT_BUFFER ),
                IntComputer.arrayOutput( OUTPUT_BUFFER )
        );
        return OUTPUT_BUFFER[0] != 0;
    }

    private static record BeamRange( int start, int end ) {
    }
}
