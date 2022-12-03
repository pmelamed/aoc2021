package aoc2022;

import common.AocDay;
import common.Utils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.TreeMap;
import java.util.TreeSet;

public class Day02Y22 implements AocDay<Integer, Integer> {

    // Rock        A X  R   1
    // Paper       B Y  P   2
    // Scissors    C Z  S   3

    public static final int ROCK = 1;
    public static final int PAPER = 2;
    public static final int SCISSORS = 3;

    public static final int LOOSE = 0;
    public static final int DRAW = 3;
    public static final int WIN = 6;

    public static final TreeMap<Character, Character> SYMBOLS = new TreeMap<>();

    static {
        SYMBOLS.put( 'A', 'R' );
        SYMBOLS.put( 'B', 'P' );
        SYMBOLS.put( 'C', 'S' );

        SYMBOLS.put( 'X', 'R' );
        SYMBOLS.put( 'Y', 'P' );
        SYMBOLS.put( 'Z', 'S' );
    }

    private static final TreeMap<String, Integer> SCORE = new TreeMap<>();

    static {
        SCORE.put( "RR", ROCK + DRAW );
        SCORE.put( "RP", PAPER + WIN );
        SCORE.put( "RS", SCISSORS + LOOSE );
        SCORE.put( "PR", ROCK + LOOSE );
        SCORE.put( "PP", PAPER + DRAW );
        SCORE.put( "PS", SCISSORS + WIN );
        SCORE.put( "SR", ROCK + WIN );
        SCORE.put( "SP", PAPER + LOOSE );
        SCORE.put( "SS", SCISSORS + DRAW );
    }

    private static final TreeMap<String, Integer> RESULT = new TreeMap<>();

    static {
        RESULT.put( "A X", SCISSORS + LOOSE );
        RESULT.put( "A Y", ROCK + DRAW );
        RESULT.put( "A Z", PAPER + WIN );
        RESULT.put( "B X", ROCK + LOOSE );
        RESULT.put( "B Y", PAPER + DRAW );
        RESULT.put( "B Z", SCISSORS + WIN );
        RESULT.put( "C X", PAPER + LOOSE );
        RESULT.put( "C Y", SCISSORS + DRAW );
        RESULT.put( "C Z", ROCK + WIN );
    }

    private final String name;

    public static void main( String[] args ) {
        try {
            executeTasks( "input/2022/Y22D02I.dat", 13268, 15508 );
        } catch ( Throwable e ) {
            e.printStackTrace();
        }
    }

    public static void executeTasks( String fileName, Integer expected1, Integer expected2 ) {
        Utils.executeDay( new Day02Y22( fileName ), expected1, expected2 );
    }

    public Day02Y22( String file ) {
        this.name = file;
    }

    @Override
    public String sampleName() {
        return name;
    }

    public Integer task1() {
        int[] line = { 0 };
        return Utils.lines( name )
                    .filter( s -> !s.isEmpty() )
                    .map( this::mapPair )
                    .map( SCORE::get )
                    .mapToInt( Integer::intValue )
                    .sum();
    }

    public Integer task2() {
        int[] line = { 0 };
        return Utils.lines( name )
                    .map( RESULT::get )
                    .mapToInt( Integer::intValue )
                    .sum();
    }

    private String mapPair( String pair ) {
        return new String( new char[]{ SYMBOLS.get( pair.charAt( 0 ) ), SYMBOLS.get( pair.charAt( 2 ) ) } );
    }
}

