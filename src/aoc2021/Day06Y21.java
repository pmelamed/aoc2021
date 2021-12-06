package aoc2021;

import common.AocDay;
import common.Utils;

import java.util.Arrays;
import java.util.stream.LongStream;

public class Day06Y21 implements AocDay<Long, Long> {

    private final String name;
    private final long[] fishes;

    public static void main( String[] args ) {
        try {
            executeTasks( "input/Y21D06S1.dat", 5934L, 26984457539L );
            executeTasks( "input/Y21D06I.dat", null, null );
        } catch ( Throwable e ) {
            e.printStackTrace();
        }
    }

    public static void executeTasks( String fileName, Long expected1, Long expected2 ) {
        Utils.executeDay(
                new Day06Y21( fileName ),
                expected1,
                expected2
        );
    }

    public Day06Y21( String file ) {
        this.name = file;
        fishes = new long[9];
        Utils.lines( file )
             .flatMap( line -> Arrays.stream( line.split( "," ) ) )
             .mapToInt( Integer::parseInt )
             .forEach( timer -> ++fishes[timer] );
    }

    @Override
    public String sampleName() {
        return name;
    }

    public Long task1() {
        for ( int day = 0; day < 80; ++day ) {
            day();
        }
        return LongStream.of( fishes ).sum();
    }


    public Long task2() {
        for ( int day = 80; day < 256; ++day ) {
            day();
        }
        return LongStream.of( fishes ).sum();
    }

    private void day() {
        long pregnant = fishes[0];
        for ( int index = 1; index <= 8; ++index ) {
            fishes[index - 1] = fishes[index];
        }
        fishes[6] += pregnant;
        fishes[8] = pregnant;
    }
}
