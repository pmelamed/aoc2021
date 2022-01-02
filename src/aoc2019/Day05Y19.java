package aoc2019;

import common.AocDay;
import common.Utils;

import java.util.List;

public class Day05Y19 implements AocDay<Long, Long> {

    private final String name;
    private final IntComputer initialState;

    public static void main( String[] args ) {
        try {
            executeTasks( "input/2019/d05i.dat", 9025675L, 11981754L );
        } catch ( Throwable e ) {
            e.printStackTrace();
        }
    }

    public static void executeTasks( String fileName, Long expected1, Long expected2 ) {
        Utils.executeDay(
                new Day05Y19( fileName, "1" ),
                new Day05Y19( fileName, "5" ),
                expected1,
                expected2
        );
    }

    public Day05Y19( String file, String input ) {
        this.name = file;
        this.initialState = new IntComputer(
                Utils.readLines( file ).get( 0 ),
                input
        );
    }

    @Override
    public String sampleName() {
        return name;
    }

    public Long task1() {
        List<Integer> output = initialState.copyState().interpret().getOutput();
        for ( int index = 0; index < output.size() - 1; ++index ) {
            if ( output.get( index ) != 0 ) {
                throw new IllegalStateException( "Non-zero output" );
            }
        }
        return (long) output.get( output.size() - 1 );
    }

    public Long task2() {
        List<Integer> output = initialState.copyState().interpret().getOutput();
        if ( output.size() != 1 ) {
            throw new IllegalStateException( "Too many outputs" );
        }
        return (long) output.get( 0 );
    }
}
