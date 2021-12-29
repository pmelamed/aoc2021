package aoc2021;

import common.AocDay;
import common.Utils;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Day05Y21 implements AocDay<Long, Long> {

    private static class Line {
        private final int x1;
        private final int y1;
        private final int x2;
        private final int y2;

        public Line( String line ) {
            String[] split = line.split( ",| -> " );
            x1 = Integer.parseInt( split[0] );
            y1 = Integer.parseInt( split[1] );
            x2 = Integer.parseInt( split[2] );
            y2 = Integer.parseInt( split[3] );
        }

        private boolean isStraight() {
            return x1 == x2 || y1 == y2;
        }

        private void plot( int[][] field ) {
            int dx = Integer.compare( x2, x1 );
            int dy = Integer.compare( y2, y1 );
            int x = x1;
            int y = y1;
            int len = Math.max( Math.abs( x2 - x1 ), Math.abs( y2 - y1 ) );
            for ( int i = 0; i <= len; ++i, x += dx, y += dy ) {
                ++field[x][y];
            }
        }

    }

    private final String name;
    private final List<Line> straightLines;
    private final List<Line> diagonalLines;
    private final int[][] field;

    public static void main( String[] args ) {
        try {
            executeTasks( "input/2021/Y21D05S1.dat", 5L, 12L );
            executeTasks( "input/2021/Y21D05I.dat", 5306L, 17787L );
        } catch ( Throwable e ) {
            e.printStackTrace();
        }
    }

    public static void executeTasks( String fileName, Long expected1, Long expected2 ) {
        Utils.executeDay(
                new Day05Y21( fileName ),
                expected1,
                expected2
        );
    }

    public Day05Y21( String file ) {
        this.name = file;
        List<Line> lines = Utils.lines( file )
                                .map( Line::new )
                                .collect( Collectors.toList() );
        int xmax = lines.stream()
                        .mapToInt( l -> Math.max( l.x1, l.x2 ) )
                        .max()
                        .orElse( 0 );
        int ymax = lines.stream()
                        .mapToInt( l -> Math.max( l.y1, l.y2 ) )
                        .max()
                        .orElse( 0 );
        field = new int[xmax + 1][ymax + 1];

        Map<Boolean, List<Line>> partitioned = lines.stream()
                                                    .collect( Collectors.partitioningBy( Line::isStraight ) );
        straightLines = partitioned.get( true );
        diagonalLines = partitioned.get( false );
    }

    @Override
    public String sampleName() {
        return name;
    }

    public Long task1() {
        straightLines.forEach( l -> l.plot( field ) );
        return scanField();
    }

    public Long task2() {
        diagonalLines.forEach( l -> l.plot( field ) );
        return scanField();
    }

    private long scanField() {
        return Arrays.stream( field )
                     .flatMapToInt( Arrays::stream )
                     .filter( v -> v > 1 )
                     .count();
    }
}
