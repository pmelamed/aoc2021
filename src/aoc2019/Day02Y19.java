package aoc2019;

import common.AocDay;
import common.Utils;

import java.util.Arrays;
import java.util.stream.Stream;

public class Day02Y19 implements AocDay<Long, Long> {

    private final String name;

    public static void main( String[] args ) {
        try {
            executeTasks( "input/2019/d02i.dat", null, null );
        } catch ( Throwable e ) {
            e.printStackTrace();
        }
    }

    public static void executeTasks( String fileName, Long expected1, Long expected2 ) {
        Utils.executeDay( new Day02Y19( fileName ), expected1, expected2 );
    }

    public Day02Y19( String file ) {
        this.name = file;
    }

    @Override
    public String sampleName() {
        return name;
    }

    public Long task1() {
        return interpretProgram( fixProgram( readProgram(), 12, 2 ) );
    }

    private long[] readProgram() {
        return Utils.lines( name )
                    .limit( 1 )
                    .flatMap( l -> Stream.of( l.split( "," ) ) )
                    .mapToLong( Long::parseLong ).toArray();
    }

    public Long task2() {
        long[] source = readProgram();
        for ( long noun = 0; noun <= 99; ++noun ) {
            for ( long verb = 0; verb <= 99; ++verb ) {
                if ( interpretProgram( fixProgram( Arrays.copyOf( source, source.length ), noun, verb ) ) ==
                        19690720L ) {
                    return 100 * noun + verb;
                }
            }
        }
        return 0L;
    }

    private long[] fixProgram( long[] program, long noun, long verb ) {
        program[1] = noun;
        program[2] = verb;
        return program;
    }

    private long interpretProgram( long[] program ) {
        int ptr = 0;
        while ( program[ptr] != 99 ) {
            int pos1 = (int) program[ptr + 1];
            int pos2 = (int) program[ptr + 2];
            int pos3 = (int) program[ptr + 3];
            switch ( (int) program[ptr] ) {
                case 1 -> program[pos3] = program[pos1] + program[pos2];
                case 2 -> program[pos3] = program[pos1] * program[pos2];
                default -> throw new IllegalStateException( "bad code " + program[ptr] + " at " + ptr );
            }
            ptr += 4;
        }
        return program[0];
    }
}
