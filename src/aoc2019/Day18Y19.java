package aoc2019;

import common.AocDay;
import common.Utils;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.IntStream;

public class Day18Y19 implements AocDay<Integer, Integer> {

    private final String name;
    private final char[][] field;
    private final FieldPosition[] keyPositions = new FieldPosition[27];

    public static void main( String[] args ) {
        try {
//            executeTasks( "input/2019/d18s01.dat", 8, null );
//            executeTasks( "input/2019/d18s02.dat", 86, null );
//            executeTasks( "input/2019/d18s03.dat", 132, null );
//            executeTasks( "input/2019/d18s04.dat", 136, null );
            executeTasks( "input/2019/d18i.dat", null, null );
        } catch ( Throwable e ) {
            e.printStackTrace();
        }
    }

    public static void executeTasks( String fileName, Integer expected1, Integer expected2 ) {
        Utils.executeDay(
                new Day18Y19( fileName ),
                expected1,
                expected2
        );
    }

    public Day18Y19( String file ) {
        this.name = file;
        field = Utils.lines( name ).map( String::toCharArray ).toArray( char[][]::new );
        for ( int rowIndex = 0; rowIndex < field.length; rowIndex++ ) {
            char[] row = field[rowIndex];
            for ( int colIndex = 0; colIndex < row.length; colIndex++ ) {
                char cell = row[colIndex];
                if ( cell == '@' ) {
                    keyPositions[26] = new FieldPosition( rowIndex, colIndex );
                }
                if ( cell >= 'a' && cell <= 'z' ) {
                    keyPositions[cell - 'a'] = new FieldPosition( rowIndex, colIndex );
                }
            }
        }
    }

    @Override
    public String sampleName() {
        return name;
    }

    public Integer task1() throws InterruptedException {
        CellAccess[][] accesses = Arrays.stream( keyPositions )
                                        .map( pt -> pt == null ? null : findKeysFromPoint( pt.row, pt.col ) )
                                        .toArray( CellAccess[][]::new );
        int allKeysMask = IntStream.range( 0, 26 )
                                   .filter( index -> keyPositions[index] != null )
                                   .map( index -> 1 << index )
                                   .sum();
        return findShortestPath(
                new SearchState( 26, 0, 0 ),
                accesses,
                allKeysMask,
                Integer.MAX_VALUE,
                ""
        );
    }

    public Integer task2() throws InterruptedException {
        return 0;
    }

    private CellAccess[] findKeysFromPoint( int row, int col ) {
        CellAccess[][] cells = new CellAccess[field.length][field[0].length];
        ArrayDeque<WavePoint> wave = new ArrayDeque<>();
        wave.add( new WavePoint( row, col, 0, 0 ) );
        while ( !wave.isEmpty() ) {
            WavePoint pt = wave.remove();
            int newDoors = updateAccessField( pt, cells );
            if ( newDoors == -1 ) {
                continue;
            }
            wave.add( new WavePoint( pt.row + 1, pt.col, newDoors, pt.distance + 1 ) );
            wave.add( new WavePoint( pt.row - 1, pt.col, newDoors, pt.distance + 1 ) );
            wave.add( new WavePoint( pt.row, pt.col + 1, newDoors, pt.distance + 1 ) );
            wave.add( new WavePoint( pt.row, pt.col - 1, newDoors, pt.distance + 1 ) );
        }
        return Arrays.stream( keyPositions )
                     .map( pos -> pos == null ? null : cells[pos.row][pos.col] )
                     .toArray( CellAccess[]::new );
    }

    private int updateAccessField( WavePoint point, CellAccess[][] cells ) {
        char cell = field[point.row][point.col];
        if ( cell == '#' ) {
            return -1;
        }
        final int doors = cell >= 'A' && cell <= 'Z'
                ? point.doors | ( 1 << ( cell - 'A' ) )
                : point.doors;
        CellAccess access = cells[point.row][point.col];
        if ( access == null || access.pathLength > point.distance ) {
            cells[point.row][point.col] = new CellAccess( doors, point.distance );
            return doors;
        }
        return -1;
    }

    private int findShortestPath(
            SearchState state,
            CellAccess[][] accesses,
            int allKeysMask,
            int minPath,
            String way
    ) {
        int pathLength = state.pathLength;
        if ( pathLength >= minPath ) {
            return Integer.MAX_VALUE;
        }
        int doorsOpen = state.doorsOpen;
        if ( ( doorsOpen & allKeysMask ) == allKeysMask ) {
            System.out.printf( "Min. way found: %s = %d%n", way, pathLength );
            return pathLength;
        }
        List<MoveTarget> moves = new ArrayList<>( 26 );
        for ( int keyIndex = 0; keyIndex < 26; keyIndex++ ) {
            if ( keyPositions[keyIndex] == null || ( doorsOpen & ( 1 << keyIndex ) ) != 0 ) {
                continue;
            }
            int minAccess = findFastestAccess( accesses[state.lastKey][keyIndex], doorsOpen );
            if ( minAccess > 0 ) {
                moves.add( new MoveTarget( keyIndex, minAccess ) );
            }
        }
        if ( moves.isEmpty() ) {
            return Integer.MAX_VALUE;
        }
        if ( moves.size() > 2 ) {
            moves.sort( Comparator.comparingInt( MoveTarget::distance ) );
        }
        for ( MoveTarget move : moves ) {
            int distance = move.distance;
            if ( pathLength + distance >= minPath ) {
                break;
            }
            int key = move.key;
            int length = findShortestPath(
                    new SearchState(
                            key,
                            doorsOpen | ( 1 << key ),
                            pathLength + distance
                    ),
                    accesses,
                    allKeysMask,
                    minPath,
                    way + (char) ( 'A' + key )
            );
            if ( minPath > length ) {
                minPath = length;
            }
        }
        return minPath;
    }

    private int findFastestAccess( CellAccess access, int doorsOpen ) {
        return access == null || ( ( access.doors & doorsOpen ) != access.doors )
                ? -1
                : access.pathLength;
    }

    private static record SearchState(
            int lastKey,
            int doorsOpen,
            int pathLength
    ) {
    }

    private static record CellAccess( int doors, int pathLength ) {
    }

    private static record WavePoint( int row, int col, int doors, int distance ) {
    }

    private static record FieldPosition( int row, int col ) {
    }

    private static record MoveTarget( int key, int distance ) {
    }
}
