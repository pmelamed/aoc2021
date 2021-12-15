package aoc2021;

import common.AocDay;
import common.Utils;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.TreeSet;

public class Day13Y21Modified implements AocDay<Long, Long> {

    public static final Comparator<Point> POINT_COMPARATOR = Comparator.comparing( Point::getX )
                                                                       .thenComparing( Point::getY );

    static class Point {
        int x;
        int y;

        public Point( String str ) {
            String[] xy = str.split( "," );
            this.x = Integer.parseInt( xy[0] );
            this.y = Integer.parseInt( xy[1] );
        }

        void foldX( int xfold ) {
            if ( x > xfold ) {
                x = ( xfold << 1 ) - x;
            }
        }

        void foldY( int yfold ) {
            if ( y > yfold ) {
                y = ( yfold << 1 ) - y;
            }
        }

        public int getX() {
            return x;
        }

        public int getY() {
            return y;
        }
    }

    private final String name;

    public static void main( String[] args ) {
        try {
            executeTasks( "input/Y21D13S1.dat", 17L, null );
            executeTasks( "input/Y21D13I.dat", null, null );
            executeTasks( "input/Y21D13SReddit.dat", null, null );
        } catch ( Throwable e ) {
            e.printStackTrace();
        }
    }

    public static void executeTasks( String fileName, Long expected1, Long expected2 ) {
        Utils.executeDay(
                new Day13Y21Modified( fileName ),
                expected1,
                expected2
        );
    }

    public Day13Y21Modified( String file ) {
        this.name = file;
    }

    @Override
    public String sampleName() {
        return name;
    }

    public Long task1() {
        Point[] points = foldPoints( true );
        TreeSet<Point> unique = new TreeSet<>( POINT_COMPARATOR );
        unique.addAll( List.of( points ) );
        return (long) unique.size();
    }


    public Long task2() {
        Point[] points = foldPoints( false );

        int minX = Integer.MAX_VALUE;
        int minY = Integer.MAX_VALUE;
        int maxX = Integer.MIN_VALUE;
        int maxY = Integer.MIN_VALUE;
        for ( Point point : points ) {
            if ( point.x < minX ) {
                minX = point.x;
            }
            if ( point.x > maxX ) {
                maxX = point.x;
            }
            if ( point.y < minY ) {
                minY = point.y;
            }
            if ( point.y > maxY ) {
                maxY = point.y;
            }
        }
        char[][] code = new char[maxY - minY + 1][maxX - minX + 1];
        for ( char[] line : code ) {
            Arrays.fill( line, ' ' );
        }
        for ( Point point : points ) {
            code[point.y - minY][point.x - minX] = '\u2588';
        }
        System.out.println();
        for ( char[] line : code ) {
            for ( char ch : line ) {
                System.out.print( ch );
                System.out.print( ch );
            }
            System.out.println();
        }
        return 0L;
    }

    private Point[] foldPoints( boolean limit ) {
        List<String> lines = Utils.readLines( name );
        Point[] points = lines.stream()
                              .takeWhile( l -> !l.isEmpty() )
                              .map( Point::new )
                              .toArray( Point[]::new );
        lines.stream()
             .dropWhile( l -> !l.isEmpty() )
             .dropWhile( String::isEmpty )
             .limit( limit ? 1 : lines.size() )
             .forEach( l -> doFold( points, l ) );
        return points;
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
