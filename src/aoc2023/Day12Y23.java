// https://adventofcode.com/2023/day/12
package aoc2023;

import common.AocDay;
import common.Utils;

import java.util.Arrays;

public class Day12Y23 implements AocDay<Long, Long> {
    private static final boolean PRINT_LINES = false;
    private static final boolean PRINT_MATRIX = false;
    private final String filename;

    public static void main( String[] args ) {
        try {
            Utils.executeSampleDay( new Day12Y23( "input/2023/Y23D12S1.DAT" ), 21L, 525152L );
            Utils.executeDay( new Day12Y23( "input/2023/Y23D12I.DAT" ), 7402L, 3384337640277L );
        } catch ( Throwable e ) {
            e.printStackTrace();
        }
    }

    private Day12Y23( String filename ) {
        this.filename = filename;
    }

    @Override
    public String sampleName() {
        return filename;
    }

    @Override
    public Long task1() throws Throwable {
        return solve( 1 );
    }

    @Override
    public Long task2() throws Throwable {
        return solve( 5 );
    }

    private long solve( int copies ) {
        if ( PRINT_MATRIX || PRINT_LINES ) {
            System.out.println();
        }
        return Utils.lines( filename )
                    .map( line -> new LineData( line, copies ) )
                    .mapToLong( LineData::getCombinationsCount )
                    .peek( r -> {
                        if ( PRINT_LINES ) {
                            System.out.println( r );
                        }
                    } )
                    .sum();
    }

    private static class LineData {
        private final int length;
        private final char[] mask;
        private final int[] groups;
        private final int[] nearestSpring;
        private final int firstSpring;

        public LineData( String txt, int replicas ) {
            String[] parts = txt.split( " " );

            parts[0] = Utils.repeat( parts[0], replicas, "?" );
            parts[1] = Utils.repeat( parts[1], replicas, "," );

            length = parts[0].length();

            mask = parts[0].toCharArray();
            nearestSpring = new int[mask.length];
            int prevSpring = mask.length;
            for ( int position = mask.length - 1; position >= 0; position-- ) {
                nearestSpring[position] = prevSpring;
                if ( mask[position] == '#' ) {
                    prevSpring = position;
                }
            }
            firstSpring = prevSpring;
            groups = Arrays.stream( parts[1].split( "," ) )
                           .mapToInt( Integer::valueOf )
                           .toArray();
        }

        private long getCombinationsCount() {
            long[][] counter = new long[groups.length][length];
            for ( int groupIndex = 0; groupIndex < groups.length; groupIndex++ ) {
                long[] row = counter[groupIndex];
                int groupLength = groups[groupIndex];
                for ( int position = 0; position < length; position++ ) {
                    row[position] = checkMask( position, groupLength ) ? 1 : 0;
                }
            }
            long[] prevRow = counter[groups.length - 1];
            int lastGroupLength = groups[groups.length - 1];
            for ( int position = 0;
                  position + lastGroupLength < length && nearestSpring[position + lastGroupLength] < length;
                  position++ ) {
                prevRow[position] = 0;
            }
            printMatrix( counter );
            for ( int groupIndex = groups.length - 2; groupIndex >= 0; groupIndex-- ) {
                long[] row = counter[groupIndex];
                int groupOffset = groups[groupIndex] + 1;
                for ( int position = length - groupOffset; position >= 0; position-- ) {
                    if ( row[position] == 1 ) {
                        row[position] = getAggregate(
                                prevRow,
                                position + groupOffset,
                                Math.min( length - 1, nearestSpring[position + groupOffset - 1] )
                        );
                    }
                }
                Arrays.fill( row, length - groupOffset + 1, length, 0 );
                prevRow = row;
            }
            printMatrix( counter );
            return getAggregate( counter[0], 0, Math.min( length - 1, firstSpring ) );
        }

        private long getAggregate( long[] row, int start, int end ) {
            long result = 0;
            for ( int pos = start; pos <= end; pos++ ) {
                result += row[pos];
            }
            return result;
        }

        private void printMatrix( long[][] counter ) {
            if ( PRINT_MATRIX ) {
                System.out.println();
                System.out.print( "             " );
                for ( char c : mask ) {
                    System.out.printf( " %6c", c );
                }
                System.out.println();
                for ( int groupIndex = 0; groupIndex < counter.length; groupIndex++ ) {
                    System.out.printf( "%3d = %3d -> ", groupIndex, groups[groupIndex] );
                    long[] row = counter[groupIndex];
                    for ( long val : row ) {
                        System.out.printf( " %6d", val );
                    }
                    System.out.println();
                }
            }
        }

        private boolean checkMask( int position, int length ) {
            if ( position + length > mask.length ) {
                return false;
            }
            if ( position > 0 && mask[position - 1] == '#' ) {
                return false;
            }
            if ( position + length < mask.length && mask[position + length] == '#' ) {
                return false;
            }
            for ( int bit = position; bit < position + length; bit++ ) {
                if ( mask[bit] == '.' ) {
                    return false;
                }
            }
            return true;
        }
    }
}
