package aoc2021;

import common.AocDay;
import common.Utils;

import java.util.Arrays;
import java.util.NoSuchElementException;
import java.util.function.IntPredicate;

public class Day08Y21Optimized implements AocDay<Long, Long> {

    //+0 = 6  abcefg       1110111
    //+1 = 2* cf           0100100
    //+2 = 5  acdeg        1011101
    //+3 = 5  acdfg        1101101
    //+4 = 4* bcdf         0101110
    //+5 = 5  abdfg        1101011
    //+6 = 6  abdefg       1111011
    //+7 = 3* acf          0100101
    //+8 = 7* abcdefg      1111111
    //+9 = 6  abcdfg       1101111

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

    private static final int[] DIGITS = new int[128];

    private static class Combination {
        private final int digital;
        private final int length;

        private Combination( String str ) {
            this.length = str.length();
            this.digital = digitize( str );
        }
    }

    private final String name;

    public static void main( String[] args ) {
        try {
            executeTasks( "input/Y21D08S1.dat", 26L, 61229L );
            executeTasks( "input/Y21D08I.dat", 321L, 1028926L );
        } catch ( Throwable e ) {
            e.printStackTrace();
        }
    }

    public static void executeTasks( String fileName, Long expected1, Long expected2 ) {
        Utils.executeDay(
                new Day08Y21Optimized( fileName ),
                expected1,
                expected2
        );
    }

    public Day08Y21Optimized( String file ) {
        this.name = file;
    }

    @Override
    public String sampleName() {
        return name;
    }

    public Long task1() {
        return Utils.lines( name )
                    .map( l -> l.split( " \\| " )[1] )
                    .flatMap( l -> Arrays.stream( l.split( " " ) ) )
                    .filter( o -> isUniqueLength( o.length() ) )
                    .count();
    }

    public Long task2() {
        return Utils.lines( name )
                    .mapToLong( Day08Y21Optimized::calculateLine )
                    .sum();
    }

    private static boolean isUniqueLength( int length ) {
        return length == 2 || length == 3 || length == 4 || length == 7;
    }

    private static long calculateLine( String line ) {
        String[] parts = line.split( " \\| " );
        String[] outputs = parts[1].trim().split( " " );
        return guessDigits(
                Arrays.stream( parts[0].trim().split( " " ) )
                      .map( Combination::new )
                      .toArray( Combination[]::new ),
                digitize( outputs[0] ),
                digitize( outputs[1] ),
                digitize( outputs[2] ),
                digitize( outputs[3] )
        );
    }

    private static int digitize( String combination ) {
        int buf = 0;
        for ( byte b : combination.getBytes() ) {
            buf |= 1 << ( b - 'a' );
        }
        return buf;
    }

    private static long guessDigits( Combination[] list, int output0, int output1, int output2, int output3 ) {
        int d1 = find( list, 2 );
        int d4 = find( list, 4 );
        int d7 = find( list, 3 );
        int d8 = find( list, 7 );
        int d6 = find( list, 6, cmb -> ( cmb & d7 ) != d7 );
        int d3 = find( list, 5, cmb -> ( cmb & d7 ) == d7 );
        int d5 = find( list, 5, cmb -> cmb != d3 && ( cmb & ( d8 & ~d6 ) ) == 0 );
        int d2 = find( list, 5, cmb -> cmb != d3 && cmb != d5 );
        int d0 = find( list, 6, cmb -> cmb != d6 && ( cmb & ( d2 & ~d3 ) ) != 0 );
        int d9 = find( list, 6, cmb -> cmb != d6 && cmb != d0 );
        DIGITS[d0] = 0;
        DIGITS[d1] = 1;
        DIGITS[d2] = 2;
        DIGITS[d3] = 3;
        DIGITS[d4] = 4;
        DIGITS[d5] = 5;
        DIGITS[d6] = 6;
        DIGITS[d7] = 7;
        DIGITS[d8] = 8;
        DIGITS[d9] = 9;
        return DIGITS[output0] * 1000L + DIGITS[output1] * 100L + DIGITS[output2] * 10L + DIGITS[output3];
    }

    private static int find( Combination[] combinations, int length ) {
        for ( Combination combination : combinations ) {
            if ( combination.length == length ) {
                return combination.digital;
            }
        }
        throw new NoSuchElementException();
    }

    private static int find( Combination[] combinations, int length, IntPredicate predicate ) {
        for ( Combination combination : combinations ) {
            if ( combination.length == length && predicate.test( combination.digital ) ) {
                return combination.digital;
            }
        }
        throw new NoSuchElementException();
    }
}
