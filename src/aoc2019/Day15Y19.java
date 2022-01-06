package aoc2019;

import common.AocDay;
import common.Utils;

import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;

public class Day15Y19 implements AocDay<Integer, Integer> {

    public static final int[][] DIRECTIONS = new int[][]{ { 1, 0 }, { -1, 0 }, { 0, 1 }, { 0, -1 } };
    private static final int STATUS_WALL = 0;
    private static final int STATUS_ROOM = 1;
    private static final int STATUS_FOUND = 2;

    private static final int[] OPPOSITE_DIRECTION = { 0, 2, 1, 4, 3 };
    public static final Comparator<Position> POSITION_COMPARATOR = Comparator.comparing( Position::x ).thenComparing(
            Position::y );
    private final SearchStatus searchStatus;

    private static record Position( int x, int y ) {
        private Position move( int dir ) {
            return switch ( dir ) {
                case 1 -> new Position( x, y - 1 );
                case 2 -> new Position( x, y + 1 );
                case 3 -> new Position( x - 1, y );
                case 4 -> new Position( x + 1, y );
                default -> throw new IllegalArgumentException( "Bad direction " + dir );
            };
        }
    }

    private final String name;
    private final IntComputer.Ram ram;

    public static void main( String[] args ) {
        try {
            executeTasks( "input/2019/d15i.dat", 204, null );
        } catch ( Throwable e ) {
            e.printStackTrace();
        }
    }

    public static void executeTasks( String fileName, Integer expected1, Integer expected2 ) {
        Utils.executeDay(
                new Day15Y19( fileName ),
                expected1,
                expected2
        );
    }

    private Day15Y19( String file ) {
        this.name = file;
        ram = new IntComputer.Ram( Utils.readLines( file ).get( 0 ) );
        searchStatus = new SearchStatus();
    }

    @Override
    public String sampleName() {
        return name;
    }

    public Integer task1() throws InterruptedException {
        CompletableFuture<Void> future = IntComputer.fromRam( ram )
                                                    .asyncInterpretUnsafe(
                                                            searchStatus::handleInput,
                                                            searchStatus::handleOutput
                                                    );
        Position startingPoint = new Position( 0, 0 );
        searchStatus.updatePathLength( startingPoint, 0 );
        discoverPath( searchStatus, startingPoint, 0 );
        future.cancel( true );
        return searchStatus.minPath;
    }

    public Integer task2() throws InterruptedException {
        searchStatus.prepareForFill();
        searchStatus.updateFill( searchStatus.foundPosition, 0 );
        fillRecursively( searchStatus.foundPosition, 0 );
        return searchStatus.maxFill;
    }

    private void fillRecursively( Position pt, int time ) {
        for ( int dir = 1; dir <= 4; dir++ ) {
            Position moved = pt.move( dir );
            int movedTime = time + 1;
            if ( searchStatus.updateFill( moved, movedTime ) ) {
                fillRecursively( moved, movedTime );
            }
        }
    }

    private void discoverPath( SearchStatus status, Position pt, int length ) {
        for ( int dir = 1; dir <= 4; dir++ ) {
            Position moved = pt.move( dir );
            int moveStatus = status.exchange( dir );
            switch ( moveStatus ) {
                case STATUS_WALL:
                    status.markWall( moved );
                    break;
                case STATUS_ROOM:
                case STATUS_FOUND:
                    if ( moveStatus == STATUS_FOUND ) {
                        status.updateMinPath( moved, length + 1 );
                    }
                    if ( status.updatePathLength( moved, length + 1 ) ) {
                        discoverPath( status, moved, length + 1 );
                    }
                    status.exchange( OPPOSITE_DIRECTION[dir] );
                    break;
                default:
                    throw new IllegalStateException( "Bad status " + moveStatus );
            }
        }
    }

    private static class SearchStatus {
        private final BlockingQueue<Long> direction = new ArrayBlockingQueue<>( 1 );
        private final BlockingQueue<Long> status = new ArrayBlockingQueue<>( 1 );
        private final Map<Position, Integer> discovered = new TreeMap<>( POSITION_COMPARATOR );
        private Position foundPosition = null;
        private int minPath = Integer.MAX_VALUE;
        private int maxFill = 0;

        private void markWall( Position pt ) {
            discovered.put( pt, -1 );
        }

        private boolean updatePathLength( Position pt, int length ) {
            long minimum = discovered.getOrDefault( pt, Integer.MAX_VALUE );
            if ( minimum > length ) {
                discovered.put( pt, length );
                return true;
            }
            return false;
        }

        private void updateMinPath( Position foundAt, int pathLength ) {
            if ( foundPosition == null ) {
                foundPosition = foundAt;
            }
            if ( pathLength < minPath ) {
                minPath = pathLength;
            }
        }

        private void prepareForFill() {
            discovered.entrySet()
                      .stream()
                      .filter( e -> e.getValue() >= 0 )
                      .forEach( e -> discovered.compute( e.getKey(), ( p, v ) -> Integer.MAX_VALUE ) );
        }

        private boolean updateFill( Position pt, int time ) {
            long minimum = discovered.getOrDefault( pt, Integer.MAX_VALUE );
            if ( minimum > time ) {
                discovered.put( pt, time );
                if ( maxFill < time ) {
                    maxFill = time;
                }
                return true;
            }
            return false;
        }

        private int exchange( long dir ) {
            try {
                direction.put( dir );
                return status.take().intValue();
            } catch ( InterruptedException e ) {
                throw new IllegalStateException( "Unexpected interrupt" );
            }
        }

        private long handleInput() throws InterruptedException {
            return direction.take();
        }

        private void handleOutput( long value ) throws InterruptedException {
            status.put( value );
        }
    }
}
