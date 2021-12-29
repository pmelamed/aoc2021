package aoc2021;

import common.AocDay;
import common.Utils;

import java.util.Arrays;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class Day08Y21 implements AocDay<Long, Long> {

    //+0 = 6  abcefg       1110111
    //+1 = 2* cf           0100100+
    //+2 = 5  acdeg        1011101
    //+3 = 5  acdfg        1101101+
    //+4 = 4* bcdf         0101110+
    //+5 = 5  abdfg        1101011
    //+6 = 6  abdefg       1111011+
    //+7 = 3* acf          0100101+
    //+8 = 7* abcdefg      1111111+
    // 9 = 6  abcdfg       1101111

    // a = 7 & ~1
    // c = 8 & ~6
    // e = 2 & ~3
    // 0 =? l6 & e != 0
    // 1 = l2
    // 2 = l5 != 3 && l5 != 5
    // 3 =? l5 & 7 == 7
    // 4 = l4
    // 5 =? l5 & c == 0
    // 6 =? l6 & 7 != 7
    // 7 = l3
    // 8 = l7
    // 9 =? l6 != 0

    private static class Combination {
        private final String string;
        private final int digital;
        private final int length;

        private Combination( String str ) {
            this.string = str;
            this.length = str.length();
            this.digital = digitize( str );
        }

        public int getDigital() {
            return digital;
        }

        public int getLength() {
            return length;
        }

        @Override
        public String toString() {
            return String.format( "%8s %d", string, digital );
        }
    }

    private static class Line {
        private final Combination[] combinations;
        private final Combination[] output;
        private final Map<Integer, Integer> digits = new IdentityHashMap<>();

        private Line( String line ) {
            String[] parts = line.split( " \\| " );
            combinations = Arrays.stream( parts[0].trim().split( " " ) )
                                 .filter( c -> !c.isEmpty() )
                                 .map( Combination::new )
                                 .toArray( Combination[]::new );
            output = Arrays.stream( parts[1].trim().split( " " ) )
                           .filter( c -> !c.isEmpty() )
                           .map( Combination::new )
                           .toArray( Combination[]::new );
            guessDigits();
        }

        private void guessDigits() {
            var byLength = Arrays.stream( combinations )
                                 .collect( Collectors.groupingBy( Combination::getLength ) );
            // 1 = L2
            // 4 = L4
            // 7 = L3
            // 8 = L7
            // a = 7 & ~1
            // 6 =? L6 & 7 != 7
            // 3 =? L5 & 7 == 7
            // c = 8 & ~6
            // 5 =? L5 & c == 0
            // 2 = L5
            // e = 2 & ~3
            // 0 =? L6 & e != 0
            // 9 =? L6
            var d1 = byLength.get( 2 ).get( 0 );
            var d4 = byLength.get( 4 ).get( 0 );
            var d7 = byLength.get( 3 ).get( 0 );
            var d8 = byLength.get( 7 ).get( 0 );
            var a = d7.digital & ~d1.digital;
            var d6 = find( byLength.get( 6 ), cmb -> ( cmb.digital & d7.digital ) != d7.digital );
            var d3 = find( byLength.get( 5 ), cmb -> ( cmb.digital & d7.digital ) == d7.digital );
            var c = d8.digital & ~d6.digital;
            var d5 = find( byLength.get( 5 ), cmb -> ( cmb.digital & c ) == 0 );
            var d2 = find( byLength.get( 5 ), cmb -> cmb.digital != d3.digital && cmb.digital != d5.digital );
            var e = d2.digital & ~d3.digital;
            var d0 = find( byLength.get( 6 ), cmb -> cmb.digital != d6.digital && ( cmb.digital & e ) != 0 );
            var d9 = find( byLength.get( 6 ), cmb -> cmb.digital != d0.digital && cmb.digital != d6.digital );
            digits.put( d0.digital, 0 );
            digits.put( d1.digital, 1 );
            digits.put( d2.digital, 2 );
            digits.put( d3.digital, 3 );
            digits.put( d4.digital, 4 );
            digits.put( d5.digital, 5 );
            digits.put( d6.digital, 6 );
            digits.put( d7.digital, 7 );
            digits.put( d8.digital, 8 );
            digits.put( d9.digital, 9 );
        }

        private Combination find( List<Combination> combinations, Predicate<Combination> predicate ) {
            return combinations.stream().filter( predicate ).findFirst().orElseThrow();
        }

        private long getOutputNumber() {
            return digits.get( output[0].digital ) * 1000L
                    + digits.get( output[1].digital ) * 100L
                    + digits.get( output[2].digital ) * 10L
                    + digits.get( output[3].digital );
        }
    }

    private final String name;
    private final Line[] lines;

    public static void main( String[] args ) {
        try {
            executeTasks( "input/2021/Y21D08S1.dat", 26L, 61229L );
            executeTasks( "input/2021/Y21D08I.dat", null, null );
        } catch ( Throwable e ) {
            e.printStackTrace();
        }
    }

    public static void executeTasks( String fileName, Long expected1, Long expected2 ) {
        Utils.executeDay(
                new Day08Y21( fileName ),
                expected1,
                expected2
        );
    }

    public Day08Y21( String file ) {
        this.name = file;
        lines = Utils.lines( file ).map( Line::new ).toArray( Line[]::new );
    }

    @Override
    public String sampleName() {
        return name;
    }

    public Long task1() {
        return Arrays.stream( lines )
                     .flatMap( l -> Arrays.stream( l.output ) )
                     .filter( o -> isUniqueLength( o.getLength() ) )
                     .count();
    }

    public Long task2() {
        return Arrays.stream( lines )
                     .mapToLong( Line::getOutputNumber )
                     .sum();
    }

    private boolean isUniqueLength( int length ) {
        return length == 2 || length == 3 || length == 4 || length == 7;
    }

    private static int digitize( String combination ) {
        return combination.chars().map( ch -> 1 << ( ch - 'a' ) ).sum();
    }
}
