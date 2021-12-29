package aoc2021;

import common.AocDay;
import common.Utils;

import java.util.Arrays;
import java.util.List;

public class Day15Y21 implements AocDay<Long, Long> {

    private static final int[] NEIGHBOUR_ROWS1 = new int[]{ 0, +1, 0, -1 };
    private static final int[] NEIGHBOUR_COLS1 = new int[]{ +1, 0, -1, 0 };
    private static final int[] NEIGHBOUR_ROWS2 = new int[]{ +1, 0, -1, 0 };
    private static final int[] NEIGHBOUR_COLS2 = new int[]{ 0, +1, 0, -1 };

    private final String name;
    private final int height;
    private final int width;
    private final int[][] risks;

    public static void main( String[] args ) {
        try {
            executeTasks( "input/2021/Y21D15S1.dat", 40L, 315L );
            executeTasks( "input/2021/Y21D15I.dat", 621L, 2904L );
        } catch ( Throwable e ) {
            e.printStackTrace();
        }
    }

    public static void executeTasks( String fileName, Long expected1, Long expected2 ) {
        Utils.executeDay(
                new Day15Y21( fileName ),
                expected1,
                expected2
        );
    }

    public Day15Y21( String file ) {
        this.name = file;
        List<String> lines = Utils.readLines( name );
        height = lines.size();
        width = lines.get( 0 ).length();
        risks = new int[height][width];
        for ( int row = 0; row < height; ++row ) {
            char[] line = lines.get( row ).toCharArray();
            for ( int col = 0; col < width; ++col ) {
                risks[row][col] = line[col] - '0';
            }
        }
    }

    @Override
    public String sampleName() {
        return name;
    }

    public Long task1() {
        return findMinimalWay( height, width, risks );
    }

    public Long task2() {
        int[][] scaledRisks = new int[height * 5][width * 5];
        for ( int repeatr = 0; repeatr < 5; ++repeatr ) {
            for ( int repeatc = 0; repeatc < 5; ++repeatc ) {
                for ( int row = 0; row < height; ++row ) {
                    for ( int column = 0; column < width; ++column ) {
                        scaledRisks[repeatr * height + row][repeatc * width + column] =
                                ( risks[row][column] - 1 + repeatc + repeatr ) % 9 + 1;
                    }
                }
            }
        }
        return findMinimalWay( height * 5, width * 5, scaledRisks );
    }

    private Long findMinimalWay( int height, int width, int[][] risks ) {
        long[][] minWays = new long[height][width];
        for ( long[] minRow : minWays ) {
            Arrays.fill( minRow, Long.MAX_VALUE );
        }
        scanWays( 0, 0, 0L, risks, minWays, height - 1, width - 1 );
        return minWays[height - 1][width - 1];
    }

    private void scanWays(
            int row,
            int col,
            long risk,
            int[][] risks,
            long[][] minWays,
            int lastRow,
            int lastColumn
    ) {
        minWays[row][col] = risk;
        if ( row == lastRow && col == lastColumn ) {
            return;
        }
        for ( int index = 0; index < 4; ++index ) {
            int neighbourRow = row + ( col > row ? NEIGHBOUR_ROWS2[index] : NEIGHBOUR_ROWS1[index] );
            int neighbourCol = col + ( col > row ? NEIGHBOUR_COLS2[index] : NEIGHBOUR_COLS1[index] );
            if ( neighbourRow >= 0 && neighbourCol >= 0
                    && neighbourRow <= lastRow && neighbourCol <= lastColumn ) {
                long neighbourRisk = risk + risks[neighbourRow][neighbourCol];
                if ( neighbourRisk < minWays[neighbourRow][neighbourCol]
                        && neighbourRisk < minWays[lastRow][lastColumn] ) {
                    scanWays( neighbourRow, neighbourCol, neighbourRisk, risks, minWays, lastRow, lastColumn );
                }
            }
        }
    }
}
