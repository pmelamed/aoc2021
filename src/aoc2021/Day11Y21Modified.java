package aoc2021;

import common.AocDay;
import common.Utils;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.LongStream;

public class Day11Y21Modified implements AocDay<Long, Long> {

    public static final LinkedList<Position> QUEUE = new LinkedList<>();

    private static record Position(int row, int col) {
    }

    private final String name;

    public static void main( String[] args ) {
        try {
            executeTasks( "input/2021/Y21D11S1.dat", 1656L, 195L );
            executeTasks( "input/2021/Y21D11I.dat", null, null );
        } catch ( Throwable e ) {
            e.printStackTrace();
        }
    }

    public static void executeTasks( String fileName, Long expected1, Long expected2 ) {
        Utils.executeDay(
                new Day11Y21Modified( fileName ),
                expected1,
                expected2
        );
    }

    public Day11Y21Modified( String file ) {
        this.name = file;
    }

    @Override
    public String sampleName() {
        return name;
    }

    public Long task1() {
        int[][] field = initField();
        return IntStream.range( 0, 100 )
                        .mapToLong( step -> doStep( field ) )
                        .sum();
    }


    public Long task2() {
        int[][] field = initField();
        return LongStream.iterate( 1, i -> i + 1 )
                         .dropWhile( step -> doStep( field ) != 100 )
                         .findAny()
                         .getAsLong();
    }

    private long doStep( int[][] field ) {
        long flashCount = 0;
        for ( int row = 1; row <= 10; ++row ) {
            for ( int col = 1; col <= 10; ++col ) {
                ++field[row][col];
                if ( field[row][col] == 10 ) {
                    QUEUE.push( new Position( row, col ) );
                    ++flashCount;
                }
            }
        }
        while ( !QUEUE.isEmpty() ) {
            Position pos = QUEUE.pop();
            for ( int drow = -1; drow <= 1; ++drow ) {
                for ( int dcol = -1; dcol <= 1; ++dcol ) {
                    if ( ( drow | dcol ) == 0 ) {
                        continue;
                    }
                    int nrow = pos.row + drow;
                    int ncol = pos.col + dcol;
                    ++field[nrow][ncol];
                    if ( field[nrow][ncol] == 10 ) {
                        QUEUE.push( new Position( nrow, ncol ) );
                        ++flashCount;
                    }
                }
            }
        }
        for ( int row = 1; row <= 10; ++row ) {
            for ( int col = 1; col <= 10; ++col ) {
                if ( field[row][col] >= 10 ) {
                    field[row][col] = 0;
                }
            }
        }
        return flashCount;
    }

    private int[][] initField() {
        int[][] field = new int[12][12];
        for ( int[] row : field ) {
            Arrays.fill( row, 11 );
        }
        List<String> fileLines = Utils.readLines( name );
        for ( int row = 0; row < 10; ++row ) {
            String lineStr = fileLines.get( row );
            for ( int col = 0; col < 10; ++col ) {
                field[row + 1][col + 1] = lineStr.charAt( col ) - '0';
            }
        }
        return field;
    }
}
