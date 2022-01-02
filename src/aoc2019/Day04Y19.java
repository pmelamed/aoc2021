package aoc2019;

import common.AocDay;
import common.Utils;

public class Day04Y19 implements AocDay<Long, Long> {
    private final int min;
    private final int max;

    public static void main( String[] args ) {
        try {
            executeTasks( "input/2019/d03i.dat", null, null );
        } catch ( Throwable e ) {
            e.printStackTrace();
        }
    }

    public static void executeTasks( String fileName, Long expected1, Long expected2 ) {
        Utils.executeDay( new Day04Y19( 245318, 765747 ), expected1, expected2 );
    }

    public Day04Y19( int min, int max ) {
        this.min = min;
        this.max = max;
    }

    @Override
    public String sampleName() {
        return "%d-%d".formatted( min, max );
    }

    public Long task1() {
        int count = 0;
        for ( int num = min; num <= max; ++num ) {
            if ( checkPasswordRules( num ) ) {
                ++count;
            }
        }
        return (long) count;
    }

    public Long task2() {
        int count = 0;
        for ( int num = min; num <= max; ++num ) {
            if ( checkPasswordRules2( num ) ) {
                ++count;
            }
        }
        return (long) count;
    }

    private boolean checkPasswordRules( int num ) {
        int divider = 10_000;
        boolean dbl = false;
        int prevDigit = num / 100_000;
        num %= 100_000;
        while ( divider > 0 ) {
            int digit = num / divider;
            num %= divider;
            if ( digit < prevDigit ) {
                return false;
            }
            if ( digit == prevDigit ) {
                dbl = true;
            }
            prevDigit = digit;
            divider /= 10;
        }
        return dbl;
    }

    private boolean checkPasswordRules2( int num ) {
        int divider = 10_000;
        boolean dbl = false;
        int repeats = 0;
        int prevDigit = num / 100_000;
        num %= 100_000;
        while ( divider > 0 ) {
            int digit = num / divider;
            num %= divider;
            if ( digit < prevDigit ) {
                return false;
            }
            if ( digit == prevDigit ) {
                ++repeats;
            } else {
                if ( repeats == 1 ) {
                    dbl = true;
                }
                repeats = 0;
            }
            prevDigit = digit;
            divider /= 10;
        }
        return repeats == 1 || dbl;
    }
}
