package aoc2019;

import common.AocDay;
import common.Utils;

import java.util.Arrays;
import java.util.List;

public class Day03Y19 implements AocDay<Long, Long> {

    interface IntersectionConsumer {
        void consume( int x, int y, int way1, int way2 );
    }

    interface LineConsumer {
        void consume( int x1, int y1, int x2, int y2, int dx, int dy, int length );
    }

    private static class MinMaxConsumer implements LineConsumer {
        int minX;
        int maxX;
        int minY;
        int maxY;

        @Override
        public void consume( int x1, int y1, int x2, int y2, int dx, int dy, int length ) {
            if ( minX > x2 ) {
                minX = x2;
            }
            if ( minY > y2 ) {
                minY = y2;
            }
            if ( maxX < x2 ) {
                maxX = x2;
            }
            if ( maxY < y2 ) {
                maxY = y2;
            }
        }
    }

    private class MarkerConsumer implements LineConsumer {
        int way = 0;
        int[][] route;

        private MarkerConsumer( int[][] route ) {
            this.route = route;
        }

        @Override
        public void consume( int x1, int y1, int x2, int y2, int dx, int dy, int length ) {
            for ( int i = 0; i < length; ++i ) {
                x1 += dx;
                y1 += dy;
                ++way;
                if ( route[y1 - minY][x1 - minX] > way ) {
                    route[y1 - minY][x1 - minX] = way;
                }
            }
        }
    }

    private static class ManhattanConsumer implements IntersectionConsumer {
        private int minDistance = Integer.MAX_VALUE;

        @Override
        public void consume( int x, int y, int way1, int way2 ) {
            int distance = Math.abs( x ) + Math.abs( y );
            if ( minDistance > distance ) {
                minDistance = distance;
            }
        }
    }

    private static class WayConsumer implements IntersectionConsumer {
        private int minWay = Integer.MAX_VALUE;

        @Override
        public void consume( int x, int y, int way1, int way2 ) {
            int way = way1 + way2;
            if ( minWay > way ) {
                minWay = way;
            }
        }
    }

    private final String name;
    private final int[][] route1;
    private final int[][] route2;
    private final int minX;
    private final int minY;
    private final int maxX;
    private final int maxY;

    public static void main( String[] args ) {
        try {
            executeTasks( "input/2019/d03i.dat", null, null );
        } catch ( Throwable e ) {
            e.printStackTrace();
        }
    }

    public static void executeTasks( String fileName, Long expected1, Long expected2 ) {
        Utils.executeDay( new Day03Y19( fileName ), expected1, expected2 );
    }

    public Day03Y19( String file ) {
        this.name = file;
        List<String> lines = Utils.readLines( file );
        String[] wire1 = lines.get( 0 ).split( "," );
        String[] wire2 = lines.get( 1 ).split( "," );
        MinMaxConsumer minMaxTracer = new MinMaxConsumer();
        traceWire( wire1, minMaxTracer );
        traceWire( wire2, minMaxTracer );
        minX = minMaxTracer.minX;
        maxX = minMaxTracer.maxX;
        minY = minMaxTracer.minY;
        maxY = minMaxTracer.maxY;
        route1 = new int[maxY - minY + 1][maxX - minX + 1];
        markWire( route1, wire1 );
        route2 = new int[maxY - minY + 1][maxX - minX + 1];
        markWire( route2, wire2 );
    }

    private void markWire( int[][] route, String[] wire ) {
        for ( int[] line : route ) {
            Arrays.fill( line, Integer.MAX_VALUE );
        }
        traceWire( wire, new MarkerConsumer( route ) );
    }

    @Override
    public String sampleName() {
        return name;
    }

    public Long task1() {
        ManhattanConsumer consumer = new ManhattanConsumer();
        enumIntersections( consumer );
        return (long) consumer.minDistance;
    }

    public Long task2() {
        WayConsumer consumer = new WayConsumer();
        enumIntersections( consumer );
        return (long) consumer.minWay;
    }

    private void traceWire( String[] wire, LineConsumer consumer ) {
        int x = 0;
        int y = 0;
        for ( String command : wire ) {
            int distance = Integer.parseInt( command.substring( 1 ) );
            switch ( command.charAt( 0 ) ) {
                case 'L' -> {
                    consumer.consume( x, y, x - distance, y, -1, 0, distance );
                    x -= distance;
                }
                case 'R' -> {
                    consumer.consume( x, y, x + distance, y, 1, 0, distance );
                    x += distance;
                }
                case 'D' -> {
                    consumer.consume( x, y, x, y - distance, 0, -1, distance );
                    y -= distance;
                }
                case 'U' -> {
                    consumer.consume( x, y, x, y + distance, 0, 1, distance );
                    y += distance;
                }
            }
        }
    }

    private void enumIntersections( IntersectionConsumer consumer ) {
        for ( int y = minY; y <= maxY; ++y ) {
            for ( int x = minX; x <= maxX; ++x ) {
                int w1 = route1[y - minY][x - minX];
                int w2 = route2[y - minY][x - minX];
                if ( w1 != Integer.MAX_VALUE && w2 != Integer.MAX_VALUE ) {
                    consumer.consume( x, y, w1, w2 );
                }
            }
        }
    }
}
