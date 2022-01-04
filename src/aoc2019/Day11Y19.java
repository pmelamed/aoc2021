package aoc2019;

import common.AocDay;
import common.Utils;

import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;

import static aoc2019.IntComputer.channelInput;
import static aoc2019.IntComputer.channelOutput;

public class Day11Y19 implements AocDay<Integer, Long> {
    private record Point( int x, int y ) {
    }

    private final String name;
    private final IntComputer.Ram ram;

    public static void main( String[] args ) {
        try {
            executeTasks( "input/2019/d11i.dat", 1709, 0L );
        } catch ( Throwable e ) {
            e.printStackTrace();
        }
    }

    public static void executeTasks( String fileName, Integer expected1, Long expected2 ) {
        Utils.executeDay(
                new Day11Y19( fileName ),
                expected1,
                expected2
        );
    }

    public Day11Y19( String file ) {
        this.name = file;
        ram = new IntComputer.Ram( Utils.readLines( file ).get( 0 ) );
    }

    @Override
    public String sampleName() {
        return name;
    }

    public Integer task1() throws InterruptedException {
        return paintCode( Boolean.FALSE ).size();
    }

    public Long task2() throws InterruptedException {
        Map<Point, Boolean> image = paintCode( Boolean.TRUE );
        int minX = Integer.MAX_VALUE;
        int minY = Integer.MAX_VALUE;
        int maxX = Integer.MIN_VALUE;
        int maxY = Integer.MIN_VALUE;
        for ( Point point : image.keySet() ) {
            minX = Math.min( minX, point.x );
            minY = Math.min( minY, point.y );
            maxX = Math.max( maxX, point.x );
            maxY = Math.max( maxY, point.y );
        }
        System.out.println();
        for ( int y = minY; y <= maxY; y++ ) {
            for ( int x = minX; x <= maxX; x++ ) {
                System.out.print( image.getOrDefault( new Point( x, y ), Boolean.FALSE ) ? "##" : "  " );
            }
            System.out.println();
        }
        return 0L;
    }

    private Map<Point, Boolean> paintCode( Boolean startingCellColor ) throws InterruptedException {
        Map<Point, Boolean> painted = new TreeMap<>( Comparator.comparing( Point::x ).thenComparing( Point::y ) );
        painted.put( new Point( 0, 0 ), startingCellColor );
        int x = 0;
        int y = 0;
        int dx = 0;
        int dy = -1;
        BlockingQueue<Long> inputChannel = new ArrayBlockingQueue<>( 1 );
        BlockingQueue<Long> outputChannel = new ArrayBlockingQueue<>( 1 );
        CompletableFuture<Void> cpu = IntComputer.fromRam( ram )
                                                 .asyncInterpret(
                                                         channelInput( inputChannel ),
                                                         channelOutput( outputChannel )
                                                 );
        while ( true ) {
            inputChannel.put( getCellColor( painted, x, y ) );
            long color = IntComputer.queuedInput( outputChannel, cpu );
            if ( cpu.isDone() ) {
                break;
            }
            setCellColor( painted, x, y, color );
            long direction = outputChannel.take();
            int tx = dx;
            if ( direction == 0 ) {
                dx = dy;
                dy = -tx;
            } else {
                dx = -dy;
                dy = tx;
            }
            x += dx;
            y += dy;
        }
        return painted;
    }

    private long getCellColor( Map<Point, Boolean> cells, int x, int y ) {
        return cells.getOrDefault( new Point( x, y ), Boolean.FALSE ) ? 1 : 0;
    }

    private void setCellColor( Map<Point, Boolean> cells, int x, int y, long color ) {
        cells.put( new Point( x, y ), color == 1 );
    }
}
