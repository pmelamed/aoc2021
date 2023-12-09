package aoc2019;

import common.AocDay;
import common.Utils;

import java.util.ArrayList;

import static aoc2019.IntComputer.listOutput;
import static aoc2019.IntComputer.singleInput;

public class Day05Y19 implements AocDay<Long, Long> {

    private final String name;
    private final IntComputer.Ram initialState;
    private final long input;

    public static void main( String[] args ) {
        try {
            executeTasks( "input/2019/d05i.dat", 9025675L, 11981754L );
        } catch ( Throwable e ) {
            e.printStackTrace();
        }
    }

    public static void executeTasks( String fileName, Long expected1, Long expected2 ) {
        Utils.executeDay(
                new Day05Y19( fileName, 1L ),
                new Day05Y19( fileName, 5L ),
                expected1,
                expected2,
                false
        );
    }

    public Day05Y19( String file, long input ) {
        this.name = file;
        this.initialState = new IntComputer.Ram( Utils.readLines( file ).get( 0 ) );
        this.input = input;
    }

    @Override
    public String sampleName() {
        return name;
    }

    public Long task1() {
        ArrayList<Long> output = new ArrayList<>();
        IntComputer.fromRam( initialState )
                   .interpret( singleInput( input ), listOutput( output ) );
        for ( int index = 0; index < output.size() - 1; ++index ) {
            if ( output.get( index ) != 0 ) {
                throw new IllegalStateException( "Non-zero output" );
            }
        }
        return output.get( output.size() - 1 );
    }

    public Long task2() {
        ArrayList<Long> output = new ArrayList<>();
        IntComputer.fromRam( initialState )
                   .interpret( singleInput( input ), listOutput( output ) );
        if ( output.size() != 1 ) {
            throw new IllegalStateException( "Too many outputs" );
        }
        return output.get( 0 );
    }
}
