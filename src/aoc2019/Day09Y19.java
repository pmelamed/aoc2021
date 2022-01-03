package aoc2019;

import common.AocDay;
import common.Utils;

import static aoc2019.IntComputer.arrayOutput;
import static aoc2019.IntComputer.singleInput;

public class Day09Y19 implements AocDay<Long, Long> {

    private final String name;
    private final IntComputer.Ram ram;

    public static void main( String[] args ) {
        try {
            executeTasks( "input/2019/d09i.dat", null, null );
        } catch ( Throwable e ) {
            e.printStackTrace();
        }
    }

    public static void executeTasks( String fileName, Long expected1, Long expected2 ) {
        Utils.executeDay(
                new Day09Y19( fileName ),
                expected1,
                expected2
        );
    }

    public Day09Y19( String file ) {
        this.name = file;
        ram = new IntComputer.Ram( Utils.readLines( file ).get( 0 ) );
    }

    @Override
    public String sampleName() {
        return name;
    }

    public Long task1() {
        long[] output = new long[1];
        IntComputer.fromRam( ram )
                   .interpret(
                           singleInput( 1L ),
                           arrayOutput( output )
                   );
        return output[0];
    }

    public Long task2() {
        long[] output = new long[1];
        IntComputer.fromRam( ram )
                   .interpret(
                           singleInput( 2L ),
                           arrayOutput( output )
                   );
        return output[0];
    }
}
