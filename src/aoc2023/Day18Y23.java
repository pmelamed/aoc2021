package aoc2023;

import common.AocDay;
import common.Utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Day18Y23 implements AocDay<Long, Long> {
    private static final int DIR_NORTH = 0;
    private static final int DIR_EAST = 1;
    private static final int DIR_SOUTH = 2;
    private static final int DIR_WEST = 3;

    private static final int LINE_NONE = 0;
    private static final int LINE_UP = 1;
    private static final int LINE_DOWN = 2;
    private static final int LINE_HORIZONTAL = 3;

    private static final int FILL = 4;

    private static final int[] DX = { 0, 1, 0, -1 };
    private static final int[] DY = { -1, 0, 1, 0 };
    private static final Pattern COMMAND_PATTERN = Pattern.compile( "([UDLR]) ([0-9]+) \\(#([0-9a-z]{6})\\)" );

    // Fill FSM
    private static final int MODE_EMPTY = 0;
    private static final int MODE_ENTER_LINE_UP = 1;
    private static final int MODE_EXIT_LINE_UP = 2;
    private static final int MODE_ENTER_LINE_DOWN = 3;
    private static final int MODE_EXIT_LINE_DOWN = 4;

    private static final int[][] FSM = new int[5][4];

    static {
        FSM[MODE_EMPTY][LINE_NONE] = MODE_EMPTY;
        FSM[MODE_EMPTY][LINE_UP] = MODE_ENTER_LINE_UP;
        FSM[MODE_EMPTY][LINE_DOWN] = MODE_ENTER_LINE_DOWN;

        FSM[MODE_ENTER_LINE_UP][LINE_UP] = MODE_ENTER_LINE_UP;
        FSM[MODE_ENTER_LINE_UP][LINE_HORIZONTAL] = MODE_ENTER_LINE_UP;
        FSM[MODE_ENTER_LINE_UP][LINE_NONE] = MODE_ENTER_LINE_UP;
        FSM[MODE_ENTER_LINE_UP][LINE_DOWN] = MODE_EXIT_LINE_DOWN;

        FSM[MODE_ENTER_LINE_DOWN][LINE_DOWN] = MODE_ENTER_LINE_DOWN;
        FSM[MODE_ENTER_LINE_DOWN][LINE_HORIZONTAL] = MODE_ENTER_LINE_DOWN;
        FSM[MODE_ENTER_LINE_DOWN][LINE_NONE] = MODE_ENTER_LINE_DOWN;
        FSM[MODE_ENTER_LINE_DOWN][LINE_UP] = MODE_EXIT_LINE_UP;

        FSM[MODE_EXIT_LINE_UP][LINE_UP] = MODE_EXIT_LINE_UP;
        FSM[MODE_EXIT_LINE_UP][LINE_HORIZONTAL] = MODE_EXIT_LINE_UP;
        FSM[MODE_EXIT_LINE_UP][LINE_NONE] = MODE_EMPTY;
        FSM[MODE_EXIT_LINE_UP][LINE_DOWN] = MODE_ENTER_LINE_DOWN;

        FSM[MODE_EXIT_LINE_DOWN][LINE_DOWN] = MODE_EXIT_LINE_DOWN;
        FSM[MODE_EXIT_LINE_DOWN][LINE_HORIZONTAL] = MODE_EXIT_LINE_DOWN;
        FSM[MODE_EXIT_LINE_DOWN][LINE_NONE] = MODE_EMPTY;
        FSM[MODE_EXIT_LINE_DOWN][LINE_UP] = MODE_ENTER_LINE_UP;
    }

    private final String filename;

    public static void main( String[] args ) {
        try {
            Utils.executeSampleDay( new Day18Y23( "input/2023/Y23D18S1.dat" ), 62L, 952_408_144_115L );
//            Utils.executeSampleDay( new Day18Y23( "input/2023/Y23D18S2.dat" ), null, null );
            Utils.executeDay( new Day18Y23( "input/2023/Y23D18I.dat" ), 62573L, null );
        } catch ( Throwable e ) {
            e.printStackTrace();
        }
    }

    public Day18Y23( String file ) {
        this.filename = file;
    }

    @Override
    public String sampleName() {
        return filename;
    }

    public Long task1() {
        Command[] commands = Utils.lines( filename )
                                  .map( this::parseCommand )
                                  .toArray( Command[]::new );
        int minx = 0;
        int maxx = 0;
        int miny = 0;
        int maxy = 0;
        int x = 0;
        int y = 0;
        for ( Command cmd : commands ) {
            x += DX[cmd.dir] * cmd.length;
            y += DY[cmd.dir] * cmd.length;
            minx = Math.min( minx, x );
            maxx = Math.max( maxx, x );
            miny = Math.min( miny, y );
            maxy = Math.max( maxy, y );
        }
        int width = maxx - minx + 1;
        int height = maxy - miny + 1;
        int[][] lines = new int[height][width];
        x = -minx;
        y = -miny;
        for ( Command cmd : commands ) {
            int lineType = switch ( cmd.dir() ) {
                case DIR_NORTH -> LINE_UP;
                case DIR_SOUTH -> LINE_DOWN;
                default -> LINE_HORIZONTAL;
            };
            if ( cmd.dir() == DIR_NORTH || cmd.dir() == DIR_SOUTH ) {
                lines[y][x] = lineType;
            }
            for ( int i = 0; i < cmd.length(); i++ ) {
                x += DX[cmd.dir()];
                y += DY[cmd.dir()];
                lines[y][x] = lineType;
            }
        }
        long count = 0;
        for ( int row = 0; row < height; row++ ) {
            int mode = MODE_EMPTY;
            for ( int col = 0; col < width; col++ ) {
                mode = FSM[mode][lines[row][col]];
                if ( mode != MODE_EMPTY ) {
                    count++;
                }
            }
        }
        return count;
    }

    public Long task2() {
        Command[] commands = Utils.lines( filename )
                                  .map( this::parseColorCommand )
                                  .toArray( Command[]::new );
        List<Edge> verticalEdges = new ArrayList<>();
        List<Edge> horizontalEdges = new ArrayList<>();
        int x = 0;
        int y = 0;
        for ( Command cmd : commands ) {
            int xnext = x + cmd.length() * DX[cmd.dir()];
            int ynext = y + cmd.length() * DY[cmd.dir()];
            switch ( cmd.dir() ) {
                case DIR_NORTH:
                case DIR_SOUTH:
                    horizontalEdges.add( new Edge( x, xnext, y, cmd.dir() ) );
                    break;
                case DIR_EAST:
                case DIR_WEST:
                    verticalEdges.add( new Edge( y, ynext, x, cmd.dir() ) );
                    break;
            }
            x = xnext;
            y = ynext;
        }
        Row[] verticalRows = makeRows( verticalEdges );
        Row[] horizontalRows = makeRows( horizontalEdges );
        int[][] field = new int[verticalRows.length][horizontalRows.length];
        return null;
    }

    private Row[] makeRows( Collection<Edge> edges ) {
        TreeSet<Integer> lineEdges = new TreeSet<>();
        for ( Edge edge : edges ) {
            lineEdges.add( edge.start );
            lineEdges.add( edge.end );
        }
        List<Row> rows = new ArrayList<>();
        int prevValue = lineEdges.first();
        rows.add( new Row( prevValue, 1 ) );
        for ( int value : lineEdges.stream().skip( 1 ).mapToInt( Integer::intValue ).toArray() ) {
            if ( value - prevValue > 1 ) {
                rows.add( new Row( prevValue + 1, value - prevValue - 1 ) );
            }
            rows.add( new Row( value, 1 ) );
        }
        return rows.toArray( Row[]::new );
    }

    private Command parseCommand( String line ) {
        Matcher matcher = COMMAND_PATTERN.matcher( line );
        if ( !matcher.find() ) {
            throw new IllegalArgumentException( "Bad command <%s>".formatted( line ) );
        }
        int dir = switch ( matcher.group( 1 ).charAt( 0 ) ) {
            case 'U' -> DIR_NORTH;
            case 'D' -> DIR_SOUTH;
            case 'L' -> DIR_EAST;
            case 'R' -> DIR_WEST;
            default -> throw new IllegalArgumentException(
                    "Bad command <%s>: direction %s".formatted( line, matcher.group( 1 ) )
            );
        };
        return new Command(
                dir,
                Integer.parseInt( matcher.group( 2 ) )
        );
    }

    private Command parseColorCommand( String line ) {
        Matcher matcher = COMMAND_PATTERN.matcher( line );
        if ( !matcher.find() ) {
            throw new IllegalArgumentException( "Bad command <%s>".formatted( line ) );
        }
        int code = Integer.parseInt( matcher.group( 2 ), 0x10 );
        int dir = switch ( code & 0xF ) {
            case 0 -> DIR_WEST;
            case 1 -> DIR_SOUTH;
            case 2 -> DIR_EAST;
            case 3 -> DIR_NORTH;
            default -> throw new IllegalArgumentException(
                    "Bad command <%s>: direction %s".formatted( line, matcher.group( 1 ) )
            );
        };
        return new Command(
                dir,
                code >> 4
        );
    }

    private record Command( int dir, int length ) {
    }

    private record Edge( int start, int end, int coord, int dir ) {
    }

    private record Row( int start, int size ) {
    }
}
