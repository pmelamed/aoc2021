package aoc2022;

import common.AocDay;
import common.Utils;

import java.util.stream.Stream;

public class Day14Y22 implements AocDay.DayInt {
    private final String fileName;
    private final char[][] wall1 = new char[1000][1000];
    private final char[][] wall2 = new char[1000][1000];
    private int xLowest = 500;
    private int xHighest = 500;
    private int yLowest = 0;
    private int yHighest = 0;

    public static void main( String[] args ) {
        try {
            executeTasks( "input/2022/Y22D14I.DAT", 892, 27155 );
        } catch ( Throwable e ) {
            e.printStackTrace();
        }
    }

    public static void executeTasks( String name, Integer expected1, Integer expected2 ) {
        Utils.executeDay( new Day14Y22( name ), expected1, expected2 );
    }

    public Day14Y22( String fileName ) {
        this.fileName = fileName;
        Utils.lines( fileName ).forEach( this::parseLine );
    }

    @Override
    public String sampleName() {
        return fileName;
    }

    public Integer task1() {
        int result = 0;
        while ( simulateAbyss() ) {
            result++;
        }
        return result;
    }

    public Integer task2() {
        int result = 0;
        while ( simulateFloor() ) {
            result++;
        }
        // The last stone is also counted
        return result + 1;
    }

    private void parseLine( String line ) {
        CoordsPair[] pairs = Stream.of( line.split( " -> " ) )
                                   .map( this::parseCoords )
                                   .toArray( CoordsPair[]::new );
        for ( int index = 1; index < pairs.length; index++ ) {
            drawRock( pairs[index - 1], pairs[index] );
        }
    }

    private void drawRock( CoordsPair start, CoordsPair end ) {
        int startX = Math.min( start.x(), end.x() );
        int endX = Math.max( start.x(), end.x() );
        int startY = Math.min( start.y(), end.y() );
        int endY = Math.max( start.y(), end.y() );
        xLowest = Math.min( startX, xLowest );
        xHighest = Math.max( endX, xHighest );
        yLowest = Math.min( startY, yLowest );
        yHighest = Math.max( endY, yHighest );
        for ( int x = startX; x <= endX; x++ ) {
            for ( int y = startY; y <= endY; y++ ) {
                wall1[x][y] = wall2[x][y] = '#';
            }
        }
    }

    private boolean simulateAbyss() {
        int x = 500;
        int y = 0;
        while ( y <= yHighest ) {
            if ( wall1[x][y + 1] == 0 ) {
                y++;
            } else if ( wall1[x - 1][y + 1] == 0 ) {
                y++;
                x--;
            } else if ( wall1[x + 1][y + 1] == 0 ) {
                y++;
                x++;
            } else {
                wall1[x][y] = 'O';
                return true; // Got to rest
            }
        }
        return false; // Fallen to the abyss
    }

    private boolean simulateFloor() {
        int x = 500;
        int y = 0;
        while ( y < yHighest + 1 ) {
            if ( wall2[x][y + 1] == 0 ) {
                y++;
            } else if ( wall2[x - 1][y + 1] == 0 ) {
                y++;
                x--;
            } else if ( wall2[x + 1][y + 1] == 0 ) {
                y++;
                x++;
            } else {
                wall2[x][y] = 'O';
                return y > 0; // Got to rest
            }
        }
        wall2[x][y] = 'O';
        return true; // At least one step
    }

    private CoordsPair parseCoords( String coords ) {
        int delim = coords.indexOf( ',' );
        return new CoordsPair(
                Integer.parseInt( coords.substring( 0, delim ) ),
                Integer.parseInt( coords.substring( delim + 1 ) )
        );
    }

    private record CoordsPair( int x, int y ) {
    }
}

