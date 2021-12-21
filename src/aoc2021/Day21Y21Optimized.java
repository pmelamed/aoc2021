package aoc2021;

import common.AocDay;
import common.Utils;

import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;

public class Day21Y21Optimized implements AocDay<Long, Long> {
    private static final Comparator<Universe> UNIVERSE_COMPARATOR = Comparator.comparing( Universe::packed );

    private static final int[] DICE3_RESULTS = new int[10];

    static {
        for ( int d1 = 1; d1 <= 3; ++d1 ) {
            for ( int d2 = 1; d2 <= 3; ++d2 ) {
                for ( int d3 = 1; d3 <= 3; ++d3 ) {
                    ++DICE3_RESULTS[d1 + d2 + d3];
                }
            }
        }
    }

    private static record Universe(int position1, int position2, int score1, int score2, int player, long packed) {
        public Universe( int position1, int position2, int score1, int score2, int player ) {
            this(
                    position1,
                    position2,
                    score1,
                    score2,
                    player,
                    ( ( ( player * 11L + position1 ) * 11L + position2 ) * 22L + score1 ) * 22L + score2
            );
        }

        public Universe next( int die ) {
            if ( player == 1 ) {
                int nextPos1 = incPosition( position1, die );
                int nextScore1 = score1 + nextPos1;
                if ( nextScore1 >= 21 ) {
                    return new Universe( 0, 0, 21, 0, 0 );
                }
                return new Universe( nextPos1, position2, nextScore1, score2, 2 );
            }
            int nextPos2 = incPosition( position2, die );
            int nextScore2 = score2 + nextPos2;
            if ( nextScore2 >= 21 ) {
                return new Universe( 0, 0, 0, 21, 0 );
            }
            return new Universe( position1, nextPos2, score1, nextScore2, 1 );
        }

        public boolean player1Won() {
            return score1 == 21;
        }

        public boolean player2Won() {
            return score2 == 21;
        }
    }

    private final int initial1;
    private final int initial2;

    public static void main( String[] args ) {
        try {
            executeTasks( 4, 8, 739785L, 444356092776315L );
            executeTasks( 3, 10, 713328L, 92399285032143L );
        } catch ( Throwable e ) {
            e.printStackTrace();
        }
    }

    public static void executeTasks( int init1, int init2, Long expected1, Long expected2 ) {
        Utils.executeDay(
                new Day21Y21Optimized( init1, init2 ),
                expected1,
                expected2
        );
    }

    public Day21Y21Optimized( int initial1, int initial2 ) {
        this.initial1 = initial1;
        this.initial2 = initial2;
    }

    @Override
    public String sampleName() {
        return "%d/%d".formatted( initial1, initial2 );
    }

    public Long task1() {
        int die = 1;
        int rollCount = 0;
        int position1 = initial1;
        int position2 = initial2;
        int score1 = 0;
        int score2 = 0;
        while ( true ) {
            rollCount += 3;
            position1 = incPosition( position1, die * 3 + 3 );
            die = incDie( die );
            score1 += position1;
            if ( score1 >= 1000 ) {
                return (long) score2 * rollCount;
            }
            rollCount += 3;
            position2 = incPosition( position2, die * 3 + 3 );
            die = incDie( die );
            score2 += position2;
            if ( score2 >= 1000 ) {
                return (long) score1 * rollCount;
            }
        }
    }

    public Long task2() {
        Map<Universe, Long> generation = new TreeMap<>( UNIVERSE_COMPARATOR );
        generation.put( new Universe( initial1, initial2, 0, 0, 1 ), 1L );
        long win1 = 0;
        long win2 = 0;
        while ( !generation.isEmpty() ) {
            Map<Universe, Long> nextGen = new TreeMap<>( UNIVERSE_COMPARATOR );
            for ( Map.Entry<Universe, Long> universeEntry : generation.entrySet() ) {
                Universe current = universeEntry.getKey();
                long count = universeEntry.getValue();
                for ( int diceResult = 3; diceResult <= 9; ++diceResult ) {
                    long resultsCount = DICE3_RESULTS[diceResult] * count;
                    Universe next = current.next( diceResult );
                    if ( next.player1Won() ) {
                        win1 += resultsCount;
                    } else if ( next.player2Won() ) {
                        win2 += resultsCount;
                    } else {
                        nextGen.put( next, nextGen.getOrDefault( next, 0L ) + resultsCount );
                    }
                }
            }
            generation = nextGen;
        }
        return Math.max( win1, win2 );
    }

    private static int incPosition( int pos, int move ) {
        return ( pos - 1 + move ) % 10 + 1;
    }

    private static int incDie( int value ) {
        return ( value + 2 ) % 100 + 1;
    }
}
