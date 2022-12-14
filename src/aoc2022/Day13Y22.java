package aoc2022;

import common.AocDay;
import common.Utils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Day13Y22 implements AocDay.DayInt {

    private static final Value PACKET_2 = new ListValue(
            new Value[]{ new ListValue( new Value[]{ new SingleValue( 2 ) } ) }
    );
    private static final Value PACKET_6 = new ListValue(
            new Value[]{ new ListValue( new Value[]{ new SingleValue( 6 ) } ) }
    );

    private final String fileName;

    public static void main( String[] args ) {
        try {
            executeTasks( "input/2022/Y22D13I.DAT", 4734, 21836 );
        } catch ( Throwable e ) {
            e.printStackTrace();
        }
    }

    public static void executeTasks( String name, Integer expected1, Integer expected2 ) {
        Utils.executeDay( new Day13Y22( name ), expected1, expected2 );
    }

    public Day13Y22( String fileName ) {
        this.fileName = fileName;
    }

    @Override
    public String sampleName() {
        return fileName;
    }

    public Integer task1() {
        List<String> lines = Utils.readLines( fileName );
        int pairIndex = 1;
        int result = 0;
        for ( Iterator<String> itr = lines.iterator(); itr.hasNext(); ) {
            Value left = parseValue( itr.next() );
            Value right = parseValue( itr.next() );
            int compare = left.compareTo( right );
            if ( compare < 1 ) {
                result += pairIndex;
            }
            if ( compare == 0 ) {
                System.out.printf( "Equal values %d(%d):%n  %s%n  %s%n", pairIndex, pairIndex * 3 - 2, left, right );
            }
            pairIndex++;
            if ( itr.hasNext() ) {
                itr.next();
            }
        }
        return result;
    }

    public Integer task2() {
        ArrayList<Value> sorted = Stream.concat(
                                                Utils.lines( fileName )
                                                     .filter( Predicate.not( String::isBlank ) )
                                                     .map( this::parseValue ),
                                                Stream.of( PACKET_2, PACKET_6 )
                                        )
                                        .sorted()
                                        .collect( Collectors.toCollection( ArrayList::new ) );
        return ( sorted.indexOf( PACKET_2 ) + 1 ) * ( sorted.indexOf( PACKET_6 ) + 1 );
    }

    private Value parseValue( String str ) {
        return parseValue( new Buffer( str.toCharArray() ) );
    }

    private Value parseValue( Buffer buffer ) {
        char ch = buffer.get();
        if ( ch == '[' ) {
            List<Value> values = new LinkedList<>();
            if ( buffer.peek() != ']' ) {
                values.add( parseValue( buffer ) );
                while ( buffer.get() != ']' ) {
                    values.add( parseValue( buffer ) );
                }
            } else {
                buffer.get();
            }
            return new ListValue( values.toArray( Value[]::new ) );
        } else {
            int value = 0;
            while ( isDigit( ch ) ) {
                value = value * 10 + ( ch - '0' );
                ch = buffer.get();
            }
            buffer.unshift();
            return new SingleValue( value );
        }
    }

    private boolean isDigit( char ch ) {
        return ch >= '0' && ch <= '9';
    }

    interface Value extends Comparable<Value> {
        ListValue asList();
    }

    private record SingleValue( int value ) implements Value {
        @Override
        public int compareTo( Value v ) {
            if ( v instanceof SingleValue sv ) {
                return Integer.compare( value, sv.value );
            }
            return this.asList().compareTo( v );
        }

        @Override
        public ListValue asList() {
            return new ListValue( new Value[]{ this } );
        }

        @Override
        public String toString() {
            return Integer.toString( value );
        }
    }

    private record ListValue( Value[] values ) implements Value {
        @Override
        public int compareTo( Value v ) {
            if ( v instanceof ListValue lv ) {
                int indexLeft = 0;
                int indexRight = 0;
                while ( indexLeft < values.length && indexRight < lv.values.length ) {
                    int result = values[indexLeft++].compareTo( lv.values[indexRight++] );
                    if ( result != 0 ) {
                        return result;
                    }
                }
                return Integer.compare( values.length, lv.values.length );
            }
            return compareTo( v.asList() );
        }

        @Override
        public ListValue asList() {
            return this;
        }

        @Override
        public String toString() {
            return Stream.of( values )
                         .map( Value::toString )
                         .collect( Collectors.joining( ",", "[", "]" ) );
        }
    }

    private static class Buffer {
        private int position;
        private final char[] chars;

        public Buffer( char[] chars ) {
            this.chars = chars;
            this.position = 0;
        }

        private char get() {
            return chars[position++];
        }

        private char peek() {
            return chars[position];
        }

        private void unshift() {
            position--;
        }
    }
}

