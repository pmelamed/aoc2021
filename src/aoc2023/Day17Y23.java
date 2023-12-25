// https://adventofcode.com/2023/day/17
package aoc2023;

import common.AocDay;
import common.Utils;

import java.util.Arrays;
import java.util.LinkedList;

public class Day17Y23 implements AocDay<Long, Long> {
    private static final int DIR_NORTH = 0;
    private static final int DIR_EAST = 1;
    private static final int DIR_SOUTH = 2;
    private static final int DIR_WEST = 3;

    private static final int[] DX = { 0, 1, 0, -1 };
    private static final int[] DY = { -1, 0, 1, 0 };

    private static final int[][] TURNS = {
            { DIR_EAST, DIR_WEST },
            { DIR_SOUTH, DIR_NORTH },
            { DIR_WEST, DIR_EAST },
            { DIR_NORTH, DIR_SOUTH }
    };

    private final String filename;
    private final int[][] field;
    private final int width;
    private final int height;

    public static void main( String[] args ) {
        try {
            Utils.executeSampleDay( new Day17Y23( "input/2023/Y23D17S1.dat" ), 102L, 94L );
            Utils.executeSampleDay( new Day17Y23( "input/2023/Y23D17S2.dat" ), null, 71L );
            Utils.executeDay( new Day17Y23( "input/2023/Y23D17I.dat" ), 1004L, 1171L );
        } catch ( Throwable e ) {
            e.printStackTrace();
        }
    }

    public Day17Y23( String file ) {
        this.filename = file;
        field = Utils.lines( filename )
                     .map( String::chars )
                     .map( chars -> chars.map( v -> v - '0' ).toArray() )
                     .toArray( int[][]::new );
        height = field.length;
        width = field[0].length;
    }

    @Override
    public String sampleName() {
        return filename;
    }

    public Long task1() {
        return getOptimalWay( 1, 3 );
    }

    public Long task2() {
        return getOptimalWay( 4, 10 );
    }

    private long getOptimalWay( int minStraight, int maxStraight ) {
        LinkedList<Path> activePaths = new LinkedList<>();
        long[][][][] losses = new long[4][maxStraight][height][width];
        long minPath = Long.MAX_VALUE;
        for ( long[][][] l1 : losses ) {
            for ( long[][] l2 : l1 ) {
                for ( long[] l3 : l2 ) {
                    Arrays.fill( l3, Long.MAX_VALUE );
                }
            }
        }
        activePaths.add( new Path( 0, 0, DIR_EAST ) );
        activePaths.add( new Path( 0, 0, DIR_SOUTH ) );
        while ( !activePaths.isEmpty() ) {
            Path path = activePaths.removeFirst();
            path.x += DX[path.dir];
            path.y += DY[path.dir];
            path.straight++;
            if ( path.x < 0 || path.x >= width
                    || path.y < 0 || path.y >= height ) {
                continue;
            }
            path.lost += field[path.y][path.x];
            if ( path.lost >= minPath || path.lost >= losses[path.dir][path.straight - 1][path.y][path.x] ) {
                continue;
            }
            losses[path.dir][path.straight - 1][path.y][path.x] = path.lost;
            if ( path.x == width - 1 && path.y == height - 1 && path.straight >= minStraight ) {
                minPath = path.lost;
                continue;
            }
            if ( path.straight < maxStraight ) {
                activePaths.add( path );
            }
            if ( path.straight >= minStraight ) {
                activePaths.add( new Path( path, TURNS[path.dir][0] ) );
                activePaths.add( new Path( path, TURNS[path.dir][1] ) );
            }
        }
        return minPath;
    }

    private static class Path {
        private int x;
        private int y;
        private final int dir;
        private int straight;
        private long lost;

        private Path( Path prev, int dir ) {
            this( prev.x, prev.y, dir, 0, prev.lost );
        }

        private Path( int x, int y, int dir ) {
            this( x, y, dir, 0, 0 );
        }

        private Path( int x, int y, int dir, int straight, long lost ) {
            this.x = x;
            this.y = y;
            this.dir = dir;
            this.straight = straight;
            this.lost = lost;
        }
    }
}
