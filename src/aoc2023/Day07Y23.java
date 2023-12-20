// https://adventofcode.com/2023/day/7
package aoc2023;

import common.AocDay;
import common.Utils;

import java.util.Arrays;
import java.util.Comparator;
import java.util.function.Function;
import java.util.stream.IntStream;

public class Day07Y23 implements AocDay<Long, Long> {
    private static final int[] FREQ_BUFFER = new int[14];
    private static final int[] ASC_HAND_BUFFER = new int[5];
    private static final int[] CHAR_TO_WEIGHT = new int[127];

    static {
        CHAR_TO_WEIGHT['2'] = 1;
        CHAR_TO_WEIGHT['3'] = 2;
        CHAR_TO_WEIGHT['4'] = 3;
        CHAR_TO_WEIGHT['5'] = 4;
        CHAR_TO_WEIGHT['6'] = 5;
        CHAR_TO_WEIGHT['7'] = 6;
        CHAR_TO_WEIGHT['8'] = 7;
        CHAR_TO_WEIGHT['9'] = 8;
        CHAR_TO_WEIGHT['T'] = 9;
        CHAR_TO_WEIGHT['J'] = 10;
        CHAR_TO_WEIGHT['Q'] = 11;
        CHAR_TO_WEIGHT['K'] = 12;
        CHAR_TO_WEIGHT['A'] = 13;
    }

    private final String filename;

    @FunctionalInterface
    interface HandTypeDetector {
        boolean detect( int[] hand, int first, int second );
    }

    @FunctionalInterface
    interface HandTypeJokerDetector {
        boolean detect( int[] hand, int first, int second, int joker );
    }

    enum HandType {
        FIVE(
                ( hand, first, second ) -> first == 5,
                ( hand, first, second, joker ) -> first + joker == 5
        ),
        FOUR(
                ( hand, first, second ) -> first == 4,
                ( hand, first, second, joker ) -> first + joker == 4
        ),
        FULL_HOUSE(
                ( hand, first, second ) -> first == 3 && second == 2,
                ( hand, first, second, joker ) -> first + second + joker == 5
        ),
        THREE(
                ( hand, first, second ) -> first == 3,
                ( hand, first, second, joker ) -> first + joker == 3
        ),
        TWO_PAIRS(
                ( hand, first, second ) -> first == 2 && second == 2,
                ( hand, first, second, joker ) -> first == 2 && second == 2
        ),
        PAIR(
                ( hand, first, second ) -> first == 2,
                ( hand, first, second, joker ) -> first + joker == 2
        ),
        STREET(
                ( hand, first, second ) -> {
                    for ( int i = 1; i < hand.length; ++i ) {
                        if ( hand[i] - hand[i - 1] != 1 ) {
                            return false;
                        }
                    }
                    return true;
                },
                ( hand, first, second, joker ) -> {
                    for ( int i = 1; i < hand.length; ++i ) {
                        if ( hand[i] - hand[i - 1] != 1 ) {
                            return false;
                        }
                    }
                    return true;
                }
        ),
        NONE(
                ( hand, first, second ) -> true,
                ( hand, first, second, joker ) -> true
        );

        private final HandTypeDetector detector;
        private final HandTypeJokerDetector jokerDetector;

        HandType( HandTypeDetector detector, HandTypeJokerDetector jokerDetector ) {
            this.detector = detector;
            this.jokerDetector = jokerDetector;
        }
    }

    private record Hand( String handSrc, int[] hand, long bet, HandType type ) {
        private static int[] charsToHand( String hand ) {
            return hand.chars().map( c -> CHAR_TO_WEIGHT[c] ).toArray();
        }

        private static HandType detectHandType( int[] hand ) {
            Arrays.fill( FREQ_BUFFER, 0 );
            Arrays.stream( hand ).forEach( c -> FREQ_BUFFER[c]++ );
            Arrays.sort( FREQ_BUFFER );
            return Arrays.stream( HandType.values() )
                         .filter( type -> type.detector.detect(
                                 hand,
                                 FREQ_BUFFER[FREQ_BUFFER.length - 1],
                                 FREQ_BUFFER[FREQ_BUFFER.length - 2]
                         ) )
                         .findFirst()
                         .orElseThrow();
        }

        private static HandType detectJokerHandType( int[] hand ) {
            Arrays.fill( FREQ_BUFFER, 0 );
            Arrays.stream( hand ).forEach( c -> FREQ_BUFFER[c]++ );
            Arrays.sort( FREQ_BUFFER, 1, FREQ_BUFFER.length );
            return Arrays.stream( HandType.values() )
                         .filter( type -> type.jokerDetector.detect(
                                 hand,
                                 FREQ_BUFFER[FREQ_BUFFER.length - 1],
                                 FREQ_BUFFER[FREQ_BUFFER.length - 2],
                                 FREQ_BUFFER[0]
                         ) )
                         .findFirst()
                         .orElseThrow();
        }
    }

    public static void main( String[] args ) {
        try {
            Utils.executeDay( new Day07Y23( "input/2023/Y23D07S1.DAT" ), 6440L, 5905L );
            Utils.executeDay( new Day07Y23( "input/2023/Y23D07I.DAT" ), 247815719L, 248747492L );
        } catch ( Throwable e ) {
            e.printStackTrace();
        }
    }

    private Day07Y23( String filename ) {
        this.filename = filename;
    }

    @Override
    public String sampleName() {
        return filename;
    }

    @Override
    public Long task1() throws Throwable {
        CHAR_TO_WEIGHT['J'] = 10;
        return calculateEarning( Hand::detectHandType );
    }

    @Override
    public Long task2() throws Throwable {
        CHAR_TO_WEIGHT['J'] = 0;
        return calculateEarning( Hand::detectJokerHandType );
    }

    private long calculateEarning( Function<int[], HandType> detector ) {
        Hand[] hands = Utils.lines( filename )
                            .map( line -> this.parseHand( line, detector ) )
                            .toArray( Hand[]::new );
        Arrays.sort(
                hands,
                Comparator.comparing( Hand::type )
                          .reversed()
                          .thenComparing( Hand::hand, Arrays::compare )
        );
        return IntStream.range( 0, hands.length )
                        .mapToLong( index -> ( index + 1 ) * hands[index].bet )
                        .sum();
    }

    private Hand parseHand( String line, Function<int[], HandType> detector ) {
        String[] split = line.split( " " );
        int[] hand = Hand.charsToHand( split[0] );
        return new Hand( split[0], hand, Long.parseLong( split[1] ), detector.apply( hand ) );
    }
}
