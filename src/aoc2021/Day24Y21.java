package aoc2021;

import common.AocDay;
import common.Utils;

import java.util.Map;
import java.util.TreeMap;
import java.util.function.BiPredicate;

public class Day24Y21 implements AocDay<Long, Long> {
    private static final int[] DELTA_X = { 10, 13, 12, -12, 11, -13, -9, -12, 14, -9, 15, 11, -16, -2 };
    private static final int[] DELTA_Y = { 5, 9, 4, 4, 10, 14, 14, 12, 14, 14, 5, 10, 8, 15 };
    private final String name;

    public static void main( String[] args ) {
        try {
            executeTasks( "input/Y21D24I.dat", 99919692496939L, 81914111161714L );
        } catch ( Throwable e ) {
            e.printStackTrace();
        }
    }

    public static void executeTasks( String fileName, Long expected1, Long expected2 ) {
        Utils.executeDay(
                new Day24Y21( fileName ),
                expected1,
                expected2
        );
    }

    public Day24Y21( String file ) {
        this.name = file;
    }

    @Override
    public String sampleName() {
        return name;
    }

    public Long task1() {
        return getExtremeNumber( ( a, b ) -> a > b );
    }

    public Long task2() {
        return getExtremeNumber( ( a, b ) -> a < b );
    }

    private static long getExtremeNumber( BiPredicate<Long, Long> replacePredicate ) {
        System.out.println();
        Map<Long, Long> result = new TreeMap<>();
        result.put( 0L, 0L );
        for ( int cascade = 0; cascade <= 13; ++cascade ) {
            result = getCascadeResults( cascade, result, replacePredicate );
            System.out.printf( "Cascade %d - entries %d%n", cascade + 1, result.size() );
        }
        return result.get( 0L );
    }

    private static Map<Long, Long> getCascadeResults(
            int cascade,
            Map<Long, Long> prev,
            BiPredicate<Long, Long> replacePredicate
    ) {
        Map<Long, Long> result = new TreeMap<>();
        for ( Map.Entry<Long, Long> entry : prev.entrySet() ) {
            long z = entry.getKey();
            long newNumber = entry.getValue() * 10 + 1;
            for ( int digit = 1; digit <= 9; ++digit, ++newNumber ) {
                long zout = calculateCascade( z, digit, cascade );
                Long curEntry = result.get( zout );
                if ( curEntry == null || replacePredicate.test( newNumber, curEntry ) ) {
                    result.put( zout, newNumber );
                }
            }
        }
        return result;
    }

    private static long calculateCascade( long z, int input, int cascade ) {
        if ( DELTA_X[cascade] > 0 ) {
            z = z * 26 + DELTA_Y[cascade] + input;
        } else {
            boolean xfit = z % 26 + DELTA_X[cascade] == input;
            z /= 26;
            if ( !xfit ) {
                z = z * 26 + DELTA_Y[cascade] + input;
            }
        }
        return z;
    }
}
