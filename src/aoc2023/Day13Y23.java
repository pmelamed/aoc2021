// https://adventofcode.com/2023/day/9
package aoc2023;

import common.AocDay;
import common.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class Day13Y23 implements AocDay<Long, Long> {
    private final String filename;
    private final List<LandscapePattern> patterns = new ArrayList<>();

    public static void main( String[] args ) {
        try {
            Utils.executeSampleDay( new Day13Y23( "input/2023/Y23D13S1.DAT" ), 405L, 400L );
            Utils.executeDay( new Day13Y23( "input/2023/Y23D13I.DAT" ), 37113L, 30449L );
        } catch ( Throwable e ) {
            e.printStackTrace();
        }
    }

    private Day13Y23( String filename ) {
        this.filename = filename;
        List<String> lines = Utils.readLines( filename );
        for ( int patternStart = 0; patternStart < lines.size(); ) {
            char[][] pattern = readPattern( patternStart, lines );
            patterns.add( new LandscapePattern( pattern ) );
            patternStart += pattern.length + 1;
        }
    }

    @Override
    public String sampleName() {
        return filename;
    }

    @Override
    public Long task1() throws Throwable {
        return patterns.stream()
                       .mapToLong( this::processPattern )
                       .sum();
    }

    @Override
    public Long task2() throws Throwable {
        return patterns.stream()
                       .mapToLong( this::processSmudgedPattern )
                       .sum();
    }

    private long processPattern( LandscapePattern pattern ) {
        return 100 * pattern.findRowsReflection() + pattern.findColumnsReflection();
    }

    private long processSmudgedPattern( LandscapePattern pattern ) {
        return 100 * pattern.findSmudgedRowsReflection() + pattern.findSmudgedColumnsReflection();
    }

    private char[][] readPattern( int start, List<String> file ) {
        return file.stream()
                   .skip( start )
                   .takeWhile( Predicate.not( String::isEmpty ) )
                   .map( String::toCharArray )
                   .toArray( char[][]::new );
    }

    private static class LandscapePattern {
        private final char[][] cells;
        private final int width;
        private final int height;

        private LandscapePattern( char[][] cells ) {
            this.cells = cells;
            this.height = cells.length;
            this.width = cells[0].length;
        }

        private long findRowsReflection() {
            for ( int rowIndex = 0; rowIndex < height - 1; rowIndex++ ) {
                if ( compareRows( rowIndex, rowIndex + 1 ) && proveRowsReflection( rowIndex ) ) {
                    return rowIndex + 1;
                }
            }
            return 0;
        }

        private long findColumnsReflection() {
            for ( int column = 0; column < width - 1; column++ ) {
                if ( compareColumns( column, column + 1 ) && proveColumnsReflection( column ) ) {
                    return column + 1;
                }
            }
            return 0;
        }

        private long findSmudgedRowsReflection() {
            for ( int row = 0; row < height - 1; row++ ) {
                int rowAbove = row;
                int rowBelow = row + 1;
                int diff = 0;
                while ( diff < 2 && rowAbove >= 0 && rowBelow < height ) {
                    diff += compareSmudgedRows( rowAbove, rowBelow );
                    rowAbove--;
                    rowBelow++;
                }
                if ( diff == 1 ) {
                    return row + 1;
                }
            }
            return 0;
        }

        private long findSmudgedColumnsReflection() {
            for ( int column = 0; column < width - 1; column++ ) {
                int columnLeft = column;
                int columnRight = column + 1;
                int diff = 0;
                while ( diff < 2 && columnLeft >= 0 && columnRight < width ) {
                    diff += compareSmudgedColumns( columnLeft, columnRight );
                    columnLeft--;
                    columnRight++;
                }
                if ( diff == 1 ) {
                    return column + 1;
                }
            }
            return 0;
        }

        private boolean proveRowsReflection( int centralRow ) {
            int row1 = centralRow - 1;
            int row2 = centralRow + 2;
            while ( row1 >= 0 && row2 < height ) {
                if ( !compareRows( row1, row2 ) ) {
                    return false;
                }
                row1--;
                row2++;
            }
            return true;
        }

        private boolean proveColumnsReflection( int centralColumn ) {
            int column1 = centralColumn - 1;
            int column2 = centralColumn + 2;
            while ( column1 >= 0 && column2 < width ) {
                if ( !compareColumns( column1, column2 ) ) {
                    return false;
                }
                column1--;
                column2++;
            }
            return true;
        }

        private boolean compareRows( int rowIndex1, int rowIndex2 ) {
            char[] row1 = cells[rowIndex1];
            char[] row2 = cells[rowIndex2];
            for ( int col = 0; col < width; col++ ) {
                if ( row1[col] != row2[col] ) {
                    return false;
                }
            }
            return true;
        }

        private boolean compareColumns( int columnIndex1, int columnIndex2 ) {
            for ( int row = 0; row < height; row++ ) {
                if ( cells[row][columnIndex1] != cells[row][columnIndex2] ) {
                    return false;
                }
            }
            return true;
        }

        private int compareSmudgedRows( int rowIndex1, int rowIndex2 ) {
            char[] row1 = cells[rowIndex1];
            char[] row2 = cells[rowIndex2];
            int diff = 0;
            for ( int col = 0; col < width; col++ ) {
                if ( row1[col] != row2[col] ) {
                    if ( diff > 0 ) {
                        return 2;
                    }
                    diff = 1;
                }
            }
            return diff;
        }

        private int compareSmudgedColumns( int columnIndex1, int columnIndex2 ) {
            int diff = 0;
            for ( int row = 0; row < height; row++ ) {
                if ( cells[row][columnIndex1] != cells[row][columnIndex2] ) {
                    if ( diff > 0 ) {
                        return 2;
                    }
                    diff++;
                }
            }
            return diff;
        }

    }
}
