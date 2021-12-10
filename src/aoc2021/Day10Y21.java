package aoc2021;

import common.AocDay;
import common.Utils;

import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.Optional;

public class Day10Y21 implements AocDay<Long, Long> {

    private final String name;

    public static void main( String[] args ) {
        try {
            executeTasks( "input/Y21D10S1.dat", 26397L, 288957L );
            executeTasks( "input/Y21D10I.dat", null, null );
        } catch ( Throwable e ) {
            e.printStackTrace();
        }
    }

    public static void executeTasks( String fileName, Long expected1, Long expected2 ) {
        Utils.executeDay(
                new Day10Y21( fileName ),
                expected1,
                expected2
        );
    }

    public Day10Y21( String file ) {
        this.name = file;
    }

    @Override
    public String sampleName() {
        return name;
    }

    public Long task1() {
        return Utils.lines( name ).mapToLong( this::scoreCorruptedLine ).sum();
    }


    public Long task2() {
        long[] sorted = Utils.lines( name )
                             .map( this::scoreIncompleteLine )
                             .filter( Optional::isPresent )
                             .mapToLong( Optional::get )
                             .sorted()
                             .toArray();
        return sorted[sorted.length / 2];
    }

    private long scoreCorruptedLine( String line ) {
        char[] chars = line.toCharArray();
        long result = 0L;
        LinkedList<Character> stack = new LinkedList<>();
        for ( char ch : chars ) {
            if ( isOpening( ch ) ) {
                stack.push( ch );
            } else {
                if ( stack.isEmpty() ) {
                    throw new NoSuchElementException( "Unmatched ending symbol " + ch );
                }
                char opener = stack.pop();
                if ( ch != getMatchingEnder( opener ) ) {
                    result += getCharErrorScore( ch );
                }
            }
        }
        return result;
    }

    private Optional<Long> scoreIncompleteLine( String line ) {
        char[] chars = line.toCharArray();
        LinkedList<Character> stack = new LinkedList<>();
        for ( char ch : chars ) {
            if ( isOpening( ch ) ) {
                stack.push( getMatchingEnder( ch ) );
            } else {
                if ( stack.isEmpty() ) {
                    throw new NoSuchElementException( "Unmatched ending symbol " + ch );
                }
                char opener = stack.pop();
                if ( ch != opener ) {
                    return Optional.empty();
                }
            }
        }
        if ( stack.isEmpty() ) {
            return Optional.empty();
        }
        long result = 0L;
        while ( !stack.isEmpty() ) {
            result = result * 5 + getCharCompleteScore( stack.pop() );
        }
        return Optional.of( result );
    }

    private long getCharErrorScore( char ch ) {
        return switch ( ch ) {
            case ')' -> 3;
            case ']' -> 57;
            case '}' -> 1197;
            case '>' -> 25137;
            default -> 0;
        };
    }

    private long getCharCompleteScore( char ch ) {
        return switch ( ch ) {
            case ')' -> 1;
            case ']' -> 2;
            case '}' -> 3;
            case '>' -> 4;
            default -> 0;
        };
    }

    private char getMatchingEnder( char ch ) {
        return switch ( ch ) {
            case '(' -> ')';
            case '[' -> ']';
            case '{' -> '}';
            case '<' -> '>';
            default -> ' ';
        };
    }

    private boolean isOpening( char ch ) {
        return ch == '(' || ch == '[' || ch == '{' || ch == '<';
    }
}
