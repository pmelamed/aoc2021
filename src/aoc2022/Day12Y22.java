package aoc2022;

import common.AocDay;
import common.Utils;

import java.util.Arrays;

public class Day12Y22 implements AocDay.DayInt {
    private final String fileName;
    private final PathFinder pathFinder;

    public static void main( String[] args ) {
        try {
            executeTasks( "input/2022/Y22D12I.DAT", 339, 332 );
        } catch ( Throwable e ) {
            e.printStackTrace();
        }
    }

    public static void executeTasks( String name, Integer expected1, Integer expected2 ) {
        Utils.executeDay( new Day12Y22( name ), expected1, expected2 );
    }

    public Day12Y22( String fileName ) {
        this.fileName = fileName;
        char[][] alts = Utils.lines( fileName ).map( String::toCharArray ).toArray( char[][]::new );
        int width = alts[0].length;
        int height = alts.length;
        int startX = -1;
        int startY = -1;
        int targetX = -1;
        int targetY = -1;
        for ( int x = 0; x < width; x++ ) {
            for ( int y = 0; y < height; y++ ) {
                if ( alts[y][x] == 'S' ) {
                    startX = x;
                    startY = y;
                } else if ( alts[y][x] == 'E' ) {
                    targetX = x;
                    targetY = y;
                }
            }
        }
        pathFinder = new PathFinder( alts, startX, startY, targetX, targetY );
    }

    @Override
    public String sampleName() {
        return fileName;
    }

    public Integer task1() {
        return pathFinder.scanStart();
    }

    public Integer task2() {
        return pathFinder.scanAll();
    }

    private static class PathFinder {
        private final char[][] alts;
        private final int startX;
        private final int startY;
        private final int endX;
        private final int endY;
        private final int width;
        private final int height;
        private int pathLength;
        private int shortest = Integer.MAX_VALUE;
        private final int[][] visited;

        private PathFinder( char[][] alts, int startX, int startY, int endX, int endY ) {
            this.startX = startX;
            this.startY = startY;
            this.endX = endX;
            this.endY = endY;
            this.width = alts[0].length;
            this.height = alts.length;
            this.alts = new char[height][width];
            for ( int lineIndex = 0; lineIndex < height; lineIndex++ ) {
                System.arraycopy( alts[lineIndex], 0, this.alts[lineIndex], 0, width );
            }
            this.alts[startY][startX] = 'a';
            this.alts[endY][endX] = 'z';
            this.visited = new int[alts.length][alts[0].length];
            for ( int[] arr : visited ) {
                Arrays.fill( arr, Integer.MAX_VALUE );
            }
        }

        private int scanStart() {
            scanTail( startX, startY );
            return shortest;
        }

        private int scanAll() {
            for ( int y = 0; y < height; y++ ) {
                for ( int x = 0; x < width; x++ ) {
                    if ( alts[y][x] == 'a' ) {
                        scanTail( x, y );
                    }
                }
            }
            return shortest;
        }

        private void scanTail( int x, int y ) {
            // System.out.printf( "%d:%d%n", x, y );
            if ( visited[y][x] <= pathLength ) {
                return;
            }
            if ( endX == x && endY == y ) {
                shortest = pathLength;
                return;
            }
            if ( pathLength + 1 == shortest ) {
                return;
            }
            visited[y][x] = pathLength;
            pathLength++;
            char alt = alts[y][x];
            if ( pointAvailable( x + 1, y, alt ) ) {
                scanTail( x + 1, y );
            }
            if ( pointAvailable( x - 1, y, alt ) ) {
                scanTail( x - 1, y );
            }
            if ( pointAvailable( x, y + 1, alt ) ) {
                scanTail( x, y + 1 );
            }
            if ( pointAvailable( x, y - 1, alt ) ) {
                scanTail( x, y - 1 );
            }
            pathLength--;
        }

        private boolean pointAvailable( int x, int y, char alt ) {
            if ( x >= 0 && y >= 0 && x < width && y < height ) {
                return alt + 1 >= alts[y][x];
            }
            return false;
        }
    }
}

