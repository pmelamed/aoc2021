package aoc2022;

import common.AocDay;
import common.Utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Day04Y22 implements AocDay<Long, Long> {

    private static final Pattern PAIR_PARSER = Pattern.compile( "(\\d+)-(\\d+),(\\d+)-(\\d+)" );
    private final String name;

    public static void main( String[] args ) {
        try {
            executeTasks( "input/2022/Y22D04I.dat", null, null );
        } catch ( Throwable e ) {
            e.printStackTrace();
        }
    }

    public static void executeTasks( String fileName, Long expected1, Long expected2 ) {
        Utils.executeDay( new Day04Y22( fileName ), expected1, expected2 );
    }

    public Day04Y22( String file ) {
        this.name = file;
    }

    @Override
    public String sampleName() {
        return name;
    }

    public Long task1() {
        return Utils.lines( name )
                    .filter( line -> isMatching( line, this::isLeftContained ) )
                    .count();
    }

    public Long task2() {
        return Utils.lines( name )
                    .filter( line -> isMatching( line, this::isLeftOverlapped ) )
                    .count();
    }


    private boolean isMatching( String line, RangesPredicate predicate ) {
        Matcher matcher = PAIR_PARSER.matcher( line );
        if ( !matcher.find() ) {
            throw new RuntimeException( "Bad line format " + line );
        }
        int leftStart = Integer.parseInt( matcher.group( 1 ) );
        int leftEnd = Integer.parseInt( matcher.group( 2 ) );
        int rightStart = Integer.parseInt( matcher.group( 3 ) );
        int rightEnd = Integer.parseInt( matcher.group( 4 ) );
        return predicate.test( leftStart, leftEnd, rightStart, rightEnd )
                || predicate.test( rightStart, rightEnd, leftStart, leftEnd );
    }

    private boolean isLeftContained(
            int leftStart,
            int leftEnd,
            int rightStart,
            int rightEnd
    ) {
        return leftStart >= rightStart && leftEnd <= rightEnd;
    }

    private boolean isLeftOverlapped(
            int leftStart,
            int leftEnd,
            int rightStart,
            int rightEnd
    ) {
        return leftStart >= rightStart && leftStart <= rightEnd
                || leftEnd >= rightStart && leftEnd <= rightEnd;
    }

    interface RangesPredicate {
        boolean test( int leftStart, int leftEnd, int rightStart, int rightEnd );
    }
}

