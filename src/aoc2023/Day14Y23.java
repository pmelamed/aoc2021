// https://adventofcode.com/2023/day/9
package aoc2023;

import common.AocDay;
import common.Utils;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.TreeMap;

public class Day14Y23 implements AocDay<Long, Long> {
    private static final long CYCLES_COUNT = 1_000_000_000L;
    private final String filename;
    private char[][] platform;
    private final int height;
    private final int width;
    private final char[][] initialState;

    public static void main( String[] args ) {
        try {
            Utils.executeSampleDay( new Day14Y23( "input/2023/Y23D14S1.DAT" ), 136L, 64L );
            Utils.executeDay( new Day14Y23( "input/2023/Y23D14I.DAT" ), 109833L, 99875L );
        } catch ( Throwable e ) {
            e.printStackTrace();
        }
    }

    private Day14Y23( String filename ) {
        this.filename = filename;
        initialState = Utils.lines( filename )
                            .map( String::toCharArray )
                            .toArray( char[][]::new );
        height = initialState.length;
        width = initialState[0].length;
    }

    @Override
    public String sampleName() {
        return filename;
    }

    @Override
    public Long task1() throws Throwable {
        resetPlatform();
        return getTiltedLoad();
    }

    @Override
    public Long task2() throws Throwable {
        resetPlatform();
        TreeMap<BigInteger, Long> states = new TreeMap<>();
        List<Long> loads = new ArrayList<>();
        BigInteger encoded = encodePlatform();
        states.put( encoded, 0L );
        loads.add( getLoad() );
        for ( long cycle = 0; cycle < CYCLES_COUNT; cycle++ ) {
            makeCycle();
            encoded = encodePlatform();
            loads.add( getLoad() );
            if ( states.containsKey( encoded ) ) {
                long cycleStart = states.get( encoded );
                long cycleLength = cycle - cycleStart;
                // System.out.printf( "Cycle found: start = %d, length = %d%n", cycleStart, cycleLength );
                return loads.get(
                        (int) ( cycleStart + ( CYCLES_COUNT - cycleStart ) % cycleLength )
                );
            }
            states.put( encoded, cycle );
        }
        return null;
    }

    private void makeCycle() {
        tiltPlatform( 0, 0, 0, 1, height, 1, 0, width ); // NORTH
        tiltPlatform( 0, 0, 1, 0, width, 0, 1, height ); // WEST
        tiltPlatform( 0, height - 1, 0, -1, height, 1, 0, width ); // SOUTH
        tiltPlatform( width - 1, 0, -1, 0, width, 0, 1, height ); // EAST
        // printPlatform();
    }

    private void tiltPlatform(
            int startX,
            int startY,
            int innerDeltaX,
            int innerDeltaY,
            int rowLength,
            int outerDeltaX,
            int outerDeltaY,
            int rowsCount
    ) {
        int x = startX;
        int y = startY;
        for ( int rowIndex = 0; rowIndex < rowsCount; rowIndex++, x += outerDeltaX, y += outerDeltaY ) {
            tiltRow( x, y, innerDeltaX, innerDeltaY, rowLength );
        }
    }

    private void tiltRow( int startX, int startY, int innerDeltaX, int innerDeltaY, int rowLength ) {
        int x = startX;
        int y = startY;
        int targetX = startX;
        int targetY = startY;
        for ( int cellIndex = 0; cellIndex < rowLength; cellIndex++, x += innerDeltaX, y += innerDeltaY ) {
            switch ( platform[y][x] ) {
                case 'O':
                    platform[y][x] = '.';
                    platform[targetY][targetX] = 'O';
                    targetX += innerDeltaX;
                    targetY += innerDeltaY;
                    break;
                case '#':
                    targetX = x + innerDeltaX;
                    targetY = y + innerDeltaY;
                    break;
            }
        }
    }

    private long getLoad() {
        long load = 0;
        for ( int x = 0; x < width; x++ ) {
            load += getRowLoad( x );
        }
        return load;
    }

    private long getRowLoad( int startX ) {
        long load = 0;
        for ( int y = 0; y < height; y++ ) {
            if ( platform[y][startX] == 'O' ) {
                load += height - y;
            }
        }
        return load;
    }

    private long getTiltedLoad() {
        long load = 0;
        for ( int x = 0; x < width; x++ ) {
            load += getTiltedRowLoad( x );
        }
        return load;
    }

    private long getTiltedRowLoad( int startX ) {
        long load = 0;
        long rockWeight = height;
        for ( int y = 0; y < height; y++ ) {
            switch ( platform[y][startX] ) {
                case 'O':
                    load += rockWeight;
                    rockWeight--;
                    break;
                case '#':
                    rockWeight = height - y - 1;
                    break;
            }
        }
        return load;
    }

    private BigInteger encodePlatform() {
        byte[] encoded = new byte[( height * width + 7 ) / 8];
        byte mask = 1;
        int byteIndex = 0;
        for ( int y = 0; y < height; y++ ) {
            char[] row = platform[y];
            for ( int x = 0; x < width; x++ ) {
                if ( row[x] == 'O' ) {
                    encoded[byteIndex] |= mask;
                }
                mask <<= 1;
                if ( mask == 0 ) {
                    byteIndex++;
                    mask = 1;
                }
            }
        }
        return new BigInteger( encoded );
    }

    private void resetPlatform() {
        platform = Arrays.stream( initialState )
                         .map( row -> Arrays.copyOf( row, row.length ) )
                         .toArray( char[][]::new );
    }

    private void printPlatform() {
        System.out.println();
        System.out.printf( "LOAD = %d%n", getLoad() );
        for ( char[] row : platform ) {
            for ( char cell : row ) {
                System.out.print( cell );
            }
            System.out.println();
        }
    }
}
