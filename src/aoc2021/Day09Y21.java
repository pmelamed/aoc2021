package aoc2021;

import common.AocDay;
import common.Utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class Day09Y21 implements AocDay<Long, Long> {

    private final String name;
    private final List<String> map;
    private final int width;
    private final int height;
    private final List<Integer> lowX = new ArrayList<>();
    private final List<Integer> lowY = new ArrayList<>();
    private final boolean[][] basinBuffer;

    public static void main( String[] args ) {
        try {
            executeTasks( "input/2021/Y21D09S1.dat", 15L, 1134L );
            executeTasks( "input/2021/Y21D09I.dat", 448L, 1417248L );
        } catch ( Throwable e ) {
            e.printStackTrace();
        }
    }

    public static void executeTasks( String fileName, Long expected1, Long expected2 ) {
        Utils.executeDay(
                new Day09Y21( fileName ),
                expected1,
                expected2
        );
    }

    public Day09Y21( String file ) {
        this.name = file;
        this.map = Utils.lines( name )
                        .filter( l -> !l.isEmpty() )
                        .map( l -> "9" + l + "9" )
                        .collect( Collectors.toCollection( ArrayList::new ) );
        width = this.map.get( 0 ).length() - 2;
        height = this.map.size();
        char[] border = new char[width + 2];
        Arrays.fill( border, '9' );
        this.map.add( 0, new String( border ) );
        this.map.add( new String( border ) );
        basinBuffer = new boolean[width + 2][height + 2];
    }

    @Override
    public String sampleName() {
        return name;
    }

    public Long task1() {
        long count = 0;
        for ( int y = 1; y <= height; ++y ) {
            String row = map.get( y );
            for ( int x = 1; x <= width; ++x ) {
                char val = row.charAt( x );
                if ( isMinimal(
                        val,
                        map.get( y - 1 ).charAt( x ),
                        map.get( y + 1 ).charAt( x ),
                        row.charAt( x - 1 ),
                        row.charAt( x + 1 )
                ) ) {
                    count += val - '0' + 1;
                    lowX.add( x );
                    lowY.add( y );
                }
            }
        }
        return count;
    }


    public Long task2() {
        LinkedList<Long> basins = new LinkedList<>();
        for ( int lowIndex = 0; lowIndex < lowX.size(); ++lowIndex ) {
            basins.add( getBasin( lowX.get( lowIndex ), lowY.get( lowIndex ) ) );
        }
        basins.sort( Long::compare );
        return basins.pollLast() * basins.pollLast() * basins.pollLast();
    }

    private boolean isMinimal( char val, char n1, char n2, char n3, char n4 ) {
        return val < n1 && val < n2 && val < n3 && val < n4;
    }

    private long getBasin( int x, int y ) {
        int[][] neighbours = { { -1, 0 }, { 1, 0 }, { 0, -1 }, { 0, 1 } };
        LinkedList<Integer> waveX = new LinkedList<>();
        LinkedList<Integer> waveY = new LinkedList<>();
        waveX.add( x );
        waveY.add( y );
        basinBuffer[x][y] = true;
        int size = 1;
        while ( !waveX.isEmpty() ) {
            int curX = waveX.pollFirst();
            int curY = waveY.pollFirst();
            char val = map.get( curY ).charAt( curX );
            for ( int[] offset : neighbours ) {
                int checkX = curX + offset[0];
                int checkY = curY + offset[1];
                if ( !basinBuffer[checkX][checkY] ) {
                    char checkVal = map.get( checkY ).charAt( checkX );
                    if ( checkVal < '9' && checkVal > val ) {
                        waveX.add( checkX );
                        waveY.add( checkY );
                        basinBuffer[checkX][checkY] = true;
                        ++size;
                    }
                }
            }
        }
        return size;
    }
}
