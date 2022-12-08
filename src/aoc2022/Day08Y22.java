package aoc2022;

import common.AocDay;
import common.Utils;

import java.util.Arrays;

public class Day08Y22 implements AocDay.DayLong {
    private static final int ABOVE = 1;
    private static final int BELOW = 2;
    private static final int LEFT = 4;
    private static final int RIGHT = 8;
    private static final int HIDDEN = ABOVE | BELOW | LEFT | RIGHT;

    private final String fileName;
    private final int[][] treeHeights;
    private final int rows;
    private final int cols;

    public static void main( String[] args ) {
        try {
            executeTasks( "input/2022/Y22D08I.DAT", 1854L, 527340L );
        } catch ( Throwable e ) {
            e.printStackTrace();
        }
    }

    public static void executeTasks( String name, Long expected1, Long expected2 ) {
        Utils.executeDay( new Day08Y22( name ), expected1, expected2 );
    }

    public Day08Y22( String fileName ) {
        this.fileName = fileName;
        treeHeights = Utils.lines( this.fileName )
                           .map( line -> line.chars().map( c -> c - '0' ).toArray() )
                           .toArray( int[][]::new );
        rows = treeHeights.length;
        cols = treeHeights[0].length;
    }

    @Override
    public String sampleName() {
        return fileName;
    }

    public Long task1() {
        int[][] hidden = new int[rows][cols];
        for ( int col = 0; col < cols; ++col ) {
            scanHidden( 0, col, rows, col + 1, 1, 1, ABOVE, hidden );
            scanHidden( rows - 1, col, -1, col + 1, -1, 1, BELOW, hidden );
        }
        for ( int row = 0; row < rows; ++row ) {
            scanHidden( row, 0, row + 1, cols, 1, 1, LEFT, hidden );
            scanHidden( row, cols - 1, row + 1, -1, 1, -1, RIGHT, hidden );
        }
        return Arrays.stream( hidden )
                     .flatMapToInt( Arrays::stream )
                     .filter( h -> h != HIDDEN )
                     .count();
    }


    public Long task2() {
        long max = 0;
        for ( int row = 0; row < rows; ++row ) {
            for ( int col = 0; col < cols; ++col ) {
                int index = scanSpecularIndex( row, col );
                if ( max < index ) {
                    max = index;
                }
            }
        }
        return max;
    }

    private void scanHidden(
            int rowFrom,
            int colFrom,
            int rowTo,
            int colTo,
            int rowDir,
            int colDir,
            int hiddenFlag,
            int[][] hiddenData
    ) {
        int highest = -1;
        for ( int row = rowFrom; row != rowTo; row += rowDir ) {
            for ( int col = colFrom; col != colTo; col += colDir ) {
                if ( highest < treeHeights[row][col] ) {
                    highest = treeHeights[row][col];
                } else {
                    hiddenData[row][col] |= hiddenFlag;
                }
            }
        }
    }

    private int scanSpecularIndex( int row, int col ) {
        int height = treeHeights[row][col];
        return scanSpecularDirection( row, col - 1, row + 1, -1, 1, -1, height )
                * scanSpecularDirection( row, col + 1, row + 1, cols, 1, 1, height )
                * scanSpecularDirection( row - 1, col, -1, col + 1, -1, 1, height )
                * scanSpecularDirection( row + 1, col, rows, col + 1, 1, 1, height );
    }

    private int scanSpecularDirection(
            int rowFrom,
            int colFrom,
            int rowTo,
            int colTo,
            int rowDir,
            int colDir,
            int height
    ) {
        int distance = 0;
        for ( int row = rowFrom; row != rowTo; row += rowDir ) {
            for ( int col = colFrom; col != colTo; col += colDir ) {
                if ( treeHeights[row][col] >= height ) {
                    return distance + 1;
                }
                ++distance;
            }
        }
        return distance;
    }
}

