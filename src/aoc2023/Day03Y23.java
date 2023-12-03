package aoc2023;

import common.AocDay;
import common.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class Day03Y23 implements AocDay<Long, Long> {
    private static final Pattern NUMBER_PATTERN = Pattern.compile( "[0-9]+" );

    private final String filename;

    private final char[][] lines;
    private final long[][] numbers;
    private final int width;
    private final int height;

    public static void main( String[] args ) {
        try {
            Utils.executeDay( new Day03Y23( "input/2023/Y23D03S1.DAT" ), 4361L, 467835L );
            Utils.executeDay( new Day03Y23( "input/2023/Y23D03I.DAT" ), 553079L, 84363105L );
        } catch ( Throwable e ) {
            e.printStackTrace();
        }
    }

    public Day03Y23( String filename ) {
        this.filename = filename;
        List<String> strings = Utils.readLines( filename );
        lines = strings.stream()
                       .map( String::toCharArray ).toArray( char[][]::new );
        numbers = strings.stream()
                         .map( this::lineToNumbers )
                         .toArray( long[][]::new );
        height = lines.length;
        width = lines[0].length;
    }

    @Override
    public String sampleName() {
        return filename;
    }

    @Override
    public Long task1() throws Throwable {
        long result = 0;
        for ( int row = 0; row < lines.length; ++row ) {
            char[] line = lines[row];
            for ( int col = 0; col < line.length; ++col ) {
                char ch = line[col];
                if ( ch == '.' || ( ch >= '0' && ch <= '9' ) ) {
                    continue;
                }
                if ( row > 0 ) {
                    result += sumLine( numbers[row - 1], col );
                }
                result += sumLine( numbers[row], col );
                if ( row + 1 < height ) {
                    result += sumLine( numbers[row + 1], col );
                }
            }
        }
        return result;
    }

    @Override
    public Long task2() throws Throwable {
        long result = 0;
        ArrayList<Long> neighbors = new ArrayList<>( 6 );
        for ( int row = 0; row < lines.length; ++row ) {
            char[] line = lines[row];
            for ( int col = 0; col < line.length; ++col ) {
                char ch = line[col];
                if ( ch != '*' ) {
                    continue;
                }
                if ( row > 0 ) {
                    addNeighbors( numbers[row - 1], col, neighbors );
                }
                addNeighbors( numbers[row], col, neighbors );
                if ( row + 1 < height ) {
                    addNeighbors( numbers[row + 1], col, neighbors );
                }
                if ( neighbors.size() == 2 ) {
                    result += neighbors.get( 0 ) * neighbors.get( 1 );
                }
                neighbors.clear();
            }
        }
        return result;
    }

    private long[] lineToNumbers( String line ) {
        long[] result = new long[line.length()];
        NUMBER_PATTERN.matcher( line ).results().forEach( number -> {
            long value = Long.parseLong( number.group() );
            for ( int index = number.start(); index < number.end(); ++index ) {
                result[index] = value;
            }
        } );
        return result;
    }

    private long sumLine( long[] row, int col ) {
        long result = 0;
        if ( row[col] != 0 ) {
            return row[col];
        }
        if ( col > 0 ) {
            result += row[col - 1];
        }
        if ( col + 1 < width ) {
            result += row[col + 1];
        }
        return result;
    }

    private void addNeighbors( long[] row, int col, List<Long> neighbors ) {
        if ( row[col] != 0 ) {
            neighbors.add( row[col] );
            return;
        }
        if ( col > 0 && row[col - 1] > 0 ) {
            neighbors.add( row[col - 1] );
        }
        if ( col + 1 < width && row[col + 1] > 0 ) {
            neighbors.add( row[col + 1] );
        }
    }
}
