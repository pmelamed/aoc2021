// https://adventofcode.com/2023/day/6
package aoc2023;

import common.AocDay;
import common.Utils;

import java.util.List;
import java.util.Map;

public class Day10Y23 implements AocDay<Long, Long> {
    private static final int NORTH = 1;
    private static final int EAST = 2;
    private static final int SOUTH = 4;
    private static final int WEST = 8;
    private static final int NS = NORTH | SOUTH;
    private static final int EW = EAST | WEST;
    private static final int NE = NORTH | EAST;
    private static final int NW = NORTH | WEST;
    private static final int SW = SOUTH | WEST;
    private static final int SE = SOUTH | EAST;

    private static final int GROUND = 0;
    private static final int START = NORTH | EAST | SOUTH | WEST;
    private static final int DIRS_COUNT = START + 1;

    public static final int MODE_OUTER = 1;
    public static final int MODE_INNER = 2;
    public static final int MODE_ENTER_LINE_UP = 3;
    public static final int MODE_ENTER_LINE_DOWN = 4;
    public static final int MODE_EXIT_LINE_UP = 5;
    public static final int MODE_EXIT_LINE_DOWN = 6;
    public static final int MODES_COUNT = 7;

    private static final Map<Integer, Integer> DIRECTIONS = Map.of(
            (int) '|', NS,
            (int) '-', EW,
            (int) 'L', NE,
            (int) 'J', NW,
            (int) '7', SW,
            (int) 'F', SE,
            (int) '.', GROUND,
            (int) 'S', START
    );

    //    private static final Map<Integer, Character> IMAGE = Map.of(
//            NS, '│',
//            EW, '─',
//            NE, '└',
//            NW, '┘',
//            SW, '┐',
//            SE, '┌',
//            GROUND, '.',
//            START, 'S'
//    );
//
    private static final int[][] FSM = new int[MODES_COUNT][DIRS_COUNT];

    static {
        FSM[MODE_OUTER][GROUND] = MODE_OUTER;
        FSM[MODE_OUTER][NS] = MODE_INNER;
        FSM[MODE_OUTER][SE] = MODE_ENTER_LINE_UP;
        FSM[MODE_OUTER][NE] = MODE_ENTER_LINE_DOWN;

        FSM[MODE_ENTER_LINE_UP][EW] = MODE_ENTER_LINE_UP;
        FSM[MODE_ENTER_LINE_UP][NW] = MODE_INNER;
        FSM[MODE_ENTER_LINE_UP][SW] = MODE_OUTER;

        FSM[MODE_ENTER_LINE_DOWN][EW] = MODE_ENTER_LINE_DOWN;
        FSM[MODE_ENTER_LINE_DOWN][NW] = MODE_OUTER;
        FSM[MODE_ENTER_LINE_DOWN][SW] = MODE_INNER;

        FSM[MODE_INNER][GROUND] = MODE_INNER;
        FSM[MODE_INNER][NS] = MODE_OUTER;
        FSM[MODE_INNER][SE] = MODE_EXIT_LINE_UP;
        FSM[MODE_INNER][NE] = MODE_EXIT_LINE_DOWN;

        FSM[MODE_EXIT_LINE_UP][EW] = MODE_EXIT_LINE_UP;
        FSM[MODE_EXIT_LINE_UP][NW] = MODE_OUTER;
        FSM[MODE_EXIT_LINE_UP][SW] = MODE_INNER;

        FSM[MODE_EXIT_LINE_DOWN][EW] = MODE_EXIT_LINE_DOWN;
        FSM[MODE_EXIT_LINE_DOWN][NW] = MODE_INNER;
        FSM[MODE_EXIT_LINE_DOWN][SW] = MODE_OUTER;
    }

    private static final int[] ROW_DELTA = { 1, -1, 0, 0 };
    private static final int[] COLUMN_DELTA = { 0, 0, 1, -1 };
    private static final int[] MY_SIDE = { SOUTH, NORTH, EAST, WEST };
    private static final int[] PEER_SIDE = { NORTH, SOUTH, WEST, EAST };
    private static final int[] PEER_DIR = { 1, 0, 3, 2 };

    private final String filename;
    private final int[][] field;
    //    private final char[][] image;
    private final int[][] outline;
    private final int startRow;
    private final int startCol;

    public static void main( String[] args ) {
        try {
            Utils.executeSampleDay( new Day10Y23( "input/2023/Y23D10S1.DAT" ), 4L, null );
            Utils.executeSampleDay( new Day10Y23( "input/2023/Y23D10S2.DAT" ), 8L, null );
            Utils.executeDay( new Day10Y23( "input/2023/Y23D10S3.DAT" ), null, 4L );
            Utils.executeDay( new Day10Y23( "input/2023/Y23D10S4.DAT" ), null, 8L );
            Utils.executeDay( new Day10Y23( "input/2023/Y23D10S5.DAT" ), null, 10L );
            Utils.executeDay( new Day10Y23( "input/2023/Y23D10I.DAT" ), 6842L, 393L );
        } catch ( Throwable e ) {
            e.printStackTrace();
        }
    }

    private Day10Y23( String filename ) {
        this.filename = filename;
        List<String> lines = Utils.readLines( filename );
        field = new int[lines.size() + 2][lines.get( 0 ).length() + 2];
//        image = new char[field.length][field[0].length];
        int srow = 0;
        int scol = 0;
        for ( int row = 1; row < field.length - 1; row++ ) {
            char[] chars = lines.get( row - 1 ).toCharArray();
            for ( int col = 1; col < field[0].length - 1; col++ ) {
                field[row][col] = DIRECTIONS.get( (int) chars[col - 1] );
//                image[row][col] = IMAGE.get( field[row][col] );
                if ( chars[col - 1] == 'S' ) {
                    srow = row;
                    scol = col;
                }
            }
        }
        startRow = srow;
        startCol = scol;
        outline = new int[field.length][field[0].length];
        System.out.printf( "START: %d:%d%n", startRow, startCol );
    }

    @Override
    public String sampleName() {
        return filename;
    }

    @Override
    public Long task1() throws Throwable {
        int row = startRow;
        int col = startCol;
        int prevDir = -1;
        long step = 0;
        do {
            int dir = 0;
            for ( ; dir < ROW_DELTA.length; dir++ ) {
                if ( dir != prevDir &&
                        ( field[row][col] & MY_SIDE[dir] ) != 0 &&
                        ( field[row + ROW_DELTA[dir]][col + COLUMN_DELTA[dir]] & PEER_SIDE[dir] ) != 0 ) {
                    outline[row][col] = dir + 1;
                    row += ROW_DELTA[dir];
                    col += COLUMN_DELTA[dir];
                    prevDir = PEER_DIR[dir];
                    step++;
                    break;
                }
            }
            if ( dir == ROW_DELTA.length ) {
                throw new IllegalStateException( "Couldn't find further move from %d:%d".formatted( row, col ) );
            }
        } while ( row != startRow || col != startCol );
        field[startRow][startCol] = MY_SIDE[outline[startRow][startCol] - 1] | MY_SIDE[prevDir];
//        image[startRow][startCol] = IMAGE.get( field[startRow][startCol] );
        return step / 2;
    }

    @Override
    public Long task2() throws Throwable {
//        System.out.println();
//        for ( int row = 0; row < field.length; row++ ) {
//            for ( int col = 0; col < field[0].length; col++ ) {
//                System.out.print( outline[row][col] > 0 ? image[row][col] : "." );
//            }
//            System.out.println();
//        }
        long result = 0;
        for ( int rowIndex = 1; rowIndex < field.length - 1; rowIndex++ ) {
            int[] row = field[rowIndex];
            int mode = MODE_OUTER;
            for ( int colIndex = 1; colIndex < row.length - 1; colIndex++ ) {
                int cell = outline[rowIndex][colIndex] == 0 ? GROUND : row[colIndex];
                if ( mode == MODE_INNER && cell == GROUND ) {
                    result++;
                }
                int prevMode = mode;
                mode = FSM[mode][cell];
                if ( mode == 0 ) {
                    throw new IllegalStateException(
                            "Bad state combination at line %d, col %d: MODE=%d CELL=%c".formatted(
                                    rowIndex,
                                    colIndex,
                                    prevMode,
                                    field[rowIndex][colIndex]
                            )
                    );
                }
            }
            if ( mode != MODE_OUTER ) {
                throw new IllegalStateException(
                        "Line %d ended with mode %d".formatted( rowIndex, mode )
                );
            }
        }
        return result;
    }
}
