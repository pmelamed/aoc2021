package aoc2021;

import common.AocDay;
import common.Utils;

public class Day17Y21 implements AocDay<Long, Long> {

    private final int xmin;
    private final int ymin;
    private final int xmax;
    private final int ymax;

    public static void main( String[] args ) {
        try {
            executeTasks( 20, 30, -10, -5, 45L, 112L );
            executeTasks( 277, 318, -92, -53, null, null );
        } catch ( Throwable e ) {
            e.printStackTrace();
        }
    }

    public static void executeTasks( int xmin, int xmax, int ymin, int ymax, Long expected1, Long expected2 ) {
        Utils.executeDay(
                new Day17Y21( xmin, xmax, ymin, ymax ),
                expected1,
                expected2
        );
    }

    public Day17Y21( int xmin, int xmax, int ymin, int ymax ) {
        this.xmin = xmin;
        this.ymin = ymin;
        this.xmax = xmax;
        this.ymax = ymax;
    }

    @Override
    public String sampleName() {
        return String.format( "(%d,%d) - (%d,%d)", xmin, xmax, ymin, ymax );
    }

    public Long task1() {
        return (long) getTopY( -ymin - 1 );
    }

    public Long task2() {
        long count = 0;
        for ( int dx = 0; dx <= xmax; ++dx ) {
            for ( int dy = ymin; -dy > ymin; ++dy ) {
                if ( trace( dx, dy, xmin, xmax, ymin, ymax ) ) {
                    ++count;
                }
            }
        }
        return count;
    }

    private int getTopY( int dy ) {
        return ( dy + 1 ) * dy / 2;
    }

    private boolean trace( int dx, int dy, int xmin, int xmax, int ymin, int ymax ) {
        int y = 0;
        int x = 0;
        while ( y >= ymin && x <= xmax ) {
            y += dy;
            x += dx;
            if ( ( x >= xmin ) && ( x <= xmax ) && ( y >= ymin ) && ( y <= ymax ) ) {
                return true;
            }
            if ( dx > 0 ) {
                --dx;
            }
            --dy;
        }
        return false;
    }
}
