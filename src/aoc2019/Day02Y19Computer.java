package aoc2019;

import common.AocDay;
import common.Utils;

import java.util.Map;

import static aoc2019.IntComputer.nullInput;
import static aoc2019.IntComputer.nullOutput;

public class Day02Y19Computer implements AocDay<Long, Long> {

    private final String name;
    private final IntComputer.Ram initialState;

    public static void main( String[] args ) {
        try {
            executeTasks( "input/2019/d02i.dat", 8017076L, 3146L );
        } catch ( Throwable e ) {
            e.printStackTrace();
        }
    }

    public static void executeTasks( String fileName, Long expected1, Long expected2 ) {
        Utils.executeDay( new Day02Y19Computer( fileName ), expected1, expected2 );
    }

    public Day02Y19Computer( String file ) {
        this.name = file;
        initialState = new IntComputer.Ram( Utils.readLines( file ).get( 0 ) );
    }

    @Override
    public String sampleName() {
        return name;
    }

    public Long task1() {
        return (long) IntComputer.fromRam( initialState )
                                 .fixMemory( Map.of( 1, 12L, 2, 2L ) )
                                 .interpret( nullInput(), nullOutput() )
                                 .getMemory( 0 );
    }

    public Long task2() {
        for ( long noun = 0; noun <= 99; ++noun ) {
            for ( long verb = 0; verb <= 99; ++verb ) {
                if ( IntComputer.fromRam( initialState )
                                .fixMemory( Map.of( 1, noun, 2, verb ) )
                                .interpret( nullInput(), nullOutput() )
                                .getMemory( 0 ) == 19690720L ) {
                    return 100L * noun + verb;
                }
            }
        }
        return 0L;
    }
}
