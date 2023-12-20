// https://adventofcode.com/2023/day/11
package aoc2023;

import common.AocDay;
import common.Utils;

import java.util.ArrayList;
import java.util.List;

public class Day11Y23 implements AocDay<Long, Long> {

    private final String filename;
    private final long pathsLength;
    private final List<GalaxyLocation> galaxies = new ArrayList<>();

    public static void main( String[] args ) {
        try {
            Utils.executeSampleDay( new Day11Y23( "input/2023/Y23D11S1.DAT", 2 ), 374L, null );
            Utils.executeSampleDay( new Day11Y23( "input/2023/Y23D11S1.DAT", 10 ), null, 1030L );
            Utils.executeSampleDay( new Day11Y23( "input/2023/Y23D11S1.DAT", 100 ), null, 8410L );
            Utils.executeDay(
                    new Day11Y23( "input/2023/Y23D11I.DAT", 2 ),
                    new Day11Y23( "input/2023/Y23D11I.DAT", 1_000_000 ),
                    9627977L,
                    null
            );
        } catch ( Throwable e ) {
            e.printStackTrace();
        }
    }

    private Day11Y23( String filename, int expansion ) {
        this.filename = filename;
        List<String> lines = Utils.readLines( filename );
        int width = lines.get( 0 ).length();
        int[] colOffset = new int[width];
        int row = 0;
        List<GalaxyLocation> rawGalaxies = new ArrayList<>();
        for ( String line : lines ) {
            boolean hasGalaxies = false;
            char[] lineChars = line.toCharArray();
            for ( int col = 0; col < width; col++ ) {
                if ( lineChars[col] == '#' ) {
                    colOffset[col] = -1;
                    hasGalaxies = true;
                    rawGalaxies.add( new GalaxyLocation( row, col ) );
                }
            }
            if ( hasGalaxies ) {
                row++;
            } else {
                row += expansion;
            }
        }
        int realColumn = 0;
        for ( int col = 0; col < width; col++ ) {
            boolean hasGalaxies = colOffset[col] == -1;
            colOffset[col] = realColumn;
            if ( hasGalaxies ) {
                realColumn++;
            } else {
                realColumn += expansion;
            }
        }

        for ( GalaxyLocation galaxy : rawGalaxies ) {
            galaxies.add( new GalaxyLocation( galaxy.row(), colOffset[galaxy.col()] ) );
        }

        long sum = 0;
        for ( int src = 0; src < galaxies.size() - 1; src++ ) {
            GalaxyLocation srcGalaxy = galaxies.get( src );
            for ( int dst = src + 1; dst < galaxies.size(); dst++ ) {
                GalaxyLocation dstGalaxy = galaxies.get( dst );
                sum += Math.abs( srcGalaxy.row() - dstGalaxy.row() ) + Math.abs( srcGalaxy.col() - dstGalaxy.col() );
            }
        }
        pathsLength = sum;
    }

    @Override
    public String sampleName() {
        return filename;
    }

    @Override
    public Long task1() throws Throwable {
        return pathsLength;
    }

    @Override
    public Long task2() throws Throwable {
        return pathsLength;
    }

    private record GalaxyLocation( int row, int col ) {
    }
}
