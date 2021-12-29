package aoc2021;

import common.AocDay;
import common.Utils;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class Day11Y21 implements AocDay<Long, Long> {

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
                new Day11Y21( fileName ),
                expected1,
                expected2
        );
    }

    public Day11Y21( String file ) {
        this.name = file;
    }

    @Override
    public String sampleName() {
        return name;
    }

    public Long task1() {
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
        long flashCount = 0;
        LinkedList<Position> queue = new LinkedList<>();
        for ( int step = 0; step < 100; ++step ) {
            for ( int row = 1; row <= 10; ++row ) {
                for ( int col = 1; col <= 10; ++col ) {
                    ++field[row][col];
                    if ( field[row][col] == 10 ) {
                        queue.push( new Position( row, col ) );
                        ++flashCount;
                    }
                }
            }
            while ( !queue.isEmpty() ) {
                Position pos = queue.pop();
                for ( int drow = -1; drow <= 1; ++drow ) {
                    for ( int dcol = -1; dcol <= 1; ++dcol ) {
                        if ( drow == 0 && dcol == 0 ) {
                            continue;
                        }
                        int nrow = pos.row + drow;
                        int ncol = pos.col + dcol;
                        ++field[nrow][ncol];
                        if ( field[nrow][ncol] == 10 ) {
                            queue.push( new Position( nrow, ncol ) );
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
        }
        return flashCount;
    }


    public Long task2() {
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
        LinkedList<Position> queue = new LinkedList<>();
        long step = 0;
        while ( true ) {
            ++step;
            long flashCount = 0;
            for ( int row = 1; row <= 10; ++row ) {
                for ( int col = 1; col <= 10; ++col ) {
                    ++field[row][col];
                    if ( field[row][col] == 10 ) {
                        queue.push( new Position( row, col ) );
                        ++flashCount;
                    }
                }
            }
            while ( !queue.isEmpty() ) {
                Position pos = queue.pop();
                for ( int drow = -1; drow <= 1; ++drow ) {
                    for ( int dcol = -1; dcol <= 1; ++dcol ) {
                        if ( drow == 0 && dcol == 0 ) {
                            continue;
                        }
                        int nrow = pos.row + drow;
                        int ncol = pos.col + dcol;
                        ++field[nrow][ncol];
                        if ( field[nrow][ncol] == 10 ) {
                            queue.push( new Position( nrow, ncol ) );
                            ++flashCount;
                        }
                    }
                }
            }
            if ( flashCount == 100 ) {
                return step;
            }
            for ( int row = 1; row <= 10; ++row ) {
                for ( int col = 1; col <= 10; ++col ) {
                    if ( field[row][col] >= 10 ) {
                        field[row][col] = 0;
                    }
                }
            }
        }
    }
}
