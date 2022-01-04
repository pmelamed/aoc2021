package aoc2019;

import common.AocDay;
import common.Utils;

import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;

public class Day11Y19 implements AocDay<Integer, Long> {
    private record Point( int x, int y ) {
    }

    private static class Robot {
        private int x = 0;
        private int y = 0;
        private int dx = 0;
        private int dy = -1;

        private void turn( int direction ) {
            int tx = dx;
            if ( direction == 0 ) {
                dx = dy;
                dy = -tx;
            } else {
                dx = -dy;
                dy = tx;
            }
            x += dx;
            y += dy;
        }
    }

    private final String name;
    private final IntComputer.Ram ram;

    public static void main( String[] args ) {
        try {
            executeTasks( "input/2019/d11i.dat", 1709, 0L );
        } catch ( Throwable e ) {
            e.printStackTrace();
        }
    }

    public static void executeTasks( String fileName, Integer expected1, Long expected2 ) {
        Utils.executeDay(
                new Day11Y19( fileName ),
                expected1,
                expected2
        );
    }

    public Day11Y19( String file ) {
        this.name = file;
        ram = new IntComputer.Ram( Utils.readLines( file ).get( 0 ) );
    }

    @Override
    public String sampleName() {
        return name;
    }

    public Integer task1() throws InterruptedException {
        return paintCode( Boolean.FALSE ).size();
    }

    public Long task2() throws InterruptedException {
        Map<Point, Boolean> image = paintCode( Boolean.TRUE );
        int minX = Integer.MAX_VALUE;
        int minY = Integer.MAX_VALUE;
        int maxX = Integer.MIN_VALUE;
        int maxY = Integer.MIN_VALUE;
        for ( Point point : image.keySet() ) {
            minX = Math.min( minX, point.x );
            minY = Math.min( minY, point.y );
            maxX = Math.max( maxX, point.x );
            maxY = Math.max( maxY, point.y );
        }
        System.out.println();
        for ( int y = minY; y <= maxY; y++ ) {
            for ( int x = minX; x <= maxX; x++ ) {
                System.out.print( image.getOrDefault( new Point( x, y ), Boolean.FALSE ) ? "##" : "  " );
            }
            System.out.println();
        }
        return 0L;
    }

    private Map<Point, Boolean> paintCode( Boolean startingCellColor ) {
        Map<Point, Boolean> painted = new TreeMap<>( Comparator.comparing( Point::x ).thenComparing( Point::y ) );
        painted.put( new Point( 0, 0 ), startingCellColor );
        Robot robot = new Robot();
        int[] phase = { 0 };
        IntComputer.fromRam( ram )
                   .interpret(
                           () -> getCellColor( painted, robot.x, robot.y ),
                           value -> {
                               if ( phase[0] == 0 ) {
                                   setCellColor( painted, robot.x, robot.y, value );
                               } else {
                                   robot.turn( value.intValue() );
                               }
                               phase[0] = 1 - phase[0];
                           }
                   );
        return painted;
    }

    private long getCellColor( Map<Point, Boolean> cells, int x, int y ) {
        return cells.getOrDefault( new Point( x, y ), Boolean.FALSE ) ? 1 : 0;
    }

    private void setCellColor( Map<Point, Boolean> cells, int x, int y, long color ) {
        cells.put( new Point( x, y ), color == 1 );
    }
}
