package aoc2021;

import common.AocDay;
import common.Utils;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.TreeSet;

public class Day13Y21 implements AocDay<Long, Long> {

    static class Point implements Comparable<Point> {
        int x;
        int y;

        public Point( String str ) {
            String[] xy = str.split( "," );
            this.x = Integer.parseInt( xy[0] );
            this.y = Integer.parseInt( xy[1] );
        }

        void foldX( int xfold ) {
            if ( x > xfold ) {
                x = xfold - ( x - xfold );
            }
        }

        void foldY( int yfold ) {
            if ( y > yfold ) {
                y = yfold - ( y - yfold );
            }
        }

        public int getX() {
            return x;
        }

        public int getY() {
            return y;
        }

        @Override
        public int compareTo( Point o ) {
            return Comparator.comparing( Point::getX ).thenComparing( Point::getY ).compare( this, o );
        }
    }

    private final String name;

    public static void main( String[] args ) {
        try {
            executeTasks( "input/Y21D13S1.dat", 17L, 0L );
            executeTasks( "input/Y21D13I.dat", null, null );
        } catch ( Throwable e ) {
            e.printStackTrace();
        }
    }

    public static void executeTasks( String fileName, Long expected1, Long expected2 ) {
        Utils.executeDay(
                new Day13Y21( fileName ),
                expected1,
                expected2
        );
    }

    public Day13Y21( String file ) {
        this.name = file;
    }

    @Override
    public String sampleName() {
        return name;
    }

    public Long task1() {
        TreeSet<Point> unique = foldPoints( true );
        return (long) unique.size();
    }


    public Long task2() {
        TreeSet<Point> unique = foldPoints( false );
        int maxX = unique.stream().mapToInt( Point::getX ).max().getAsInt();
        int maxY = unique.stream().mapToInt( Point::getY ).max().getAsInt();
        char[][] code = new char[maxY + 1][maxX + 1];
        for ( char[] line : code ) {
            Arrays.fill( line, ' ' );
        }
        unique.forEach( p -> code[p.y][p.x] = '#' );
        System.out.println();
        for ( char[] line : code ) {
            System.out.println( new String( line ) );
        }
        return (long) unique.size();
    }

    private TreeSet<Point> foldPoints( boolean limit ) {
        List<String> lines = Utils.readLines( name );
        Point[] points = lines.stream().takeWhile( l -> !l.isEmpty() ).map( Point::new ).toArray( Point[]::new );
        lines.stream()
             .dropWhile( l -> !l.isEmpty() )
             .skip( 1 )
             .limit( limit ? 1 : lines.size() )
             .forEach( l -> doFold( points, l ) );
        return new TreeSet<>( List.of( points ) );
    }

    private void doFold( Point[] points, String fold ) {
        String[] parts = fold.split( "[ =]" );
        int edge = Integer.parseInt( parts[3] );
        switch ( parts[2] ) {
            case "x":
                Arrays.stream( points ).forEach( p -> p.foldX( edge ) );
                break;
            case "y":
                Arrays.stream( points ).forEach( p -> p.foldY( edge ) );
                break;
        }
    }

}
