package aoc2022;

import common.AocDay;
import common.Utils;

import java.util.TreeSet;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Day09Y22 implements AocDay.DayInt {
    private final String fileName;

    public static void main( String[] args ) {
        try {
            executeTasks( "input/2022/Y22D09I.DAT", 6337, 2455 );
        } catch ( Throwable e ) {
            e.printStackTrace();
        }
    }

    public static void executeTasks( String name, Integer expected1, Integer expected2 ) {
        Utils.executeDay( new Day09Y22( name ), expected1, expected2 );
    }

    public Day09Y22( String fileName ) {
        this.fileName = fileName;
    }

    @Override
    public String sampleName() {
        return fileName;
    }

    public Integer task1() {
        return moveRope( new SimpleRope() );
    }

    public Integer task2() {
        return moveRope( new LongRope() );
    }

    private int moveRope( Rope rope ) {
        return Utils.lines( fileName )
                    .flatMap( cmd -> IntStream.range( 0, Integer.parseInt( cmd.substring( 2 ) ) )
                                              .mapToObj( i -> moveRopeHead( rope, cmd.charAt( 0 ) ) ) )
                    .collect( Collectors.toCollection( TreeSet::new ) )
                    .size();
    }

    private Position moveRopeHead( Rope rope, char command ) {
        return switch ( command ) {
            case 'R' -> rope.moveHeadBy( 1, 0 );
            case 'D' -> rope.moveHeadBy( 0, 1 );
            case 'L' -> rope.moveHeadBy( -1, 0 );
            case 'U' -> rope.moveHeadBy( 0, -1 );
            default -> throw new RuntimeException( "Bad command " + command );
        };
    }

    private static int sign( int a ) {
        return a == 0 ? 0 : ( a < 0 ? -1 : 1 );
    }

    private record Position( int x, int y ) implements Comparable<Position> {
        @Override
        public int compareTo( Position o ) {
            return y == o.y ? x - o.x : y - o.y;
        }
    }

    private interface Rope {

        Position moveHeadTo( int x, int y );

        int getHeadX();

        int getHeadY();

        default Position moveHeadBy( int dx, int dy ) {
            return moveHeadTo( getHeadX() + dx, getHeadY() + dy );
        }
    }

    private static class SimpleRope implements Rope {
        private int headX = 0;
        private int headY = 0;
        private int tailX = 0;
        private int tailY = 0;

        public int getHeadX() {
            return headX;
        }

        public int getHeadY() {
            return headY;
        }

        public Position moveHeadTo( int x, int y ) {
            headX = x;
            headY = y;
            if ( Math.abs( headX - tailX ) > 1 || Math.abs( headY - tailY ) > 1 ) {
                tailX += sign( headX - tailX );
                tailY += sign( headY - tailY );
            }
            return new Position( tailX, tailY );
        }

        public Position moveHeadBy( int dx, int dy ) {
            return moveHeadTo( headX + dx, headY + dy );
        }
    }

    private static class LongRope implements Rope {
        private final SimpleRope[] segments = IntStream.range( 0, 9 )
                                                       .mapToObj( i -> new SimpleRope() )
                                                       .toArray( SimpleRope[]::new );

        @Override
        public int getHeadX() {
            return segments[0].getHeadX();
        }

        @Override
        public int getHeadY() {
            return segments[0].getHeadY();
        }

        public Position moveHeadTo( int x, int y ) {
            Position knot = segments[0].moveHeadTo( x, y );
            for ( int index = 1; index < segments.length; ++index ) {
                knot = segments[index].moveHeadTo( knot.x(), knot.y() );
            }
            return knot;
        }
    }
}

