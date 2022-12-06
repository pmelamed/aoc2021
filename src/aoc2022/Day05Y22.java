package aoc2022;

import common.AocDay;
import common.Utils;

import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Day05Y22 implements AocDay<String, String> {

    private static final Pattern MOVE_PARSER = Pattern.compile( "move (\\d+) from (\\d) to (\\d)" );
    private final String name;
    private final String stacksFile;
    private final String movesFile;

    public static void main( String[] args ) {
        try {
            executeTasks( "input/2022/Y22D05I", "HNSNMTLHQ", "RNLFDJMCT" );
        } catch ( Throwable e ) {
            e.printStackTrace();
        }
    }

    public static void executeTasks( String fileName, String expected1, String expected2 ) {
        Utils.executeDay( new Day05Y22( fileName ), expected1, expected2 );
    }

    public Day05Y22( String file ) {
        this.name = file;
        stacksFile = name + "-stacks.DAT";
        movesFile = name + "-moves.DAT";
    }

    @Override
    public String sampleName() {
        return name;
    }

    public String task1() {
        List<LinkedList<Character>> stacks = readStacks();
        Utils.lines( movesFile ).forEach( l -> applyMove( stacks, l, this::stackCrates ) );
        return getResult( stacks );
    }

    public String task2() {
        List<LinkedList<Character>> stacks = readStacks();
        LinkedList<Character> tmp = new LinkedList<>();
        Utils.lines( movesFile )
             .forEach( l -> applyMove( stacks, l, ( from, to, count ) -> moveCrates( from, to, tmp, count ) ) );
        return getResult( stacks );
    }

    private List<LinkedList<Character>> readStacks() {
        return Utils.lines( stacksFile )
                    .map( this::readStack )
                    .collect( Collectors.toList() );
    }

    private LinkedList<Character> readStack( String stackLine ) {
        return stackLine.chars()
                        .mapToObj( i -> (char) i )
                        .collect( Collectors.toCollection( LinkedList::new ) );
    }

    private void applyMove( List<LinkedList<Character>> stacks, String moveLine, Mover mover ) {
        Matcher matcher = MOVE_PARSER.matcher( moveLine );
        if ( !matcher.find() ) {
            throw new RuntimeException( "Bad line: <" + moveLine + ">" );
        }
        mover.move(
                stacks.get( matcher.group( 2 ).charAt( 0 ) - '1' ),
                stacks.get( matcher.group( 3 ).charAt( 0 ) - '1' ),
                Integer.parseInt( matcher.group( 1 ) )
        );
    }

    private void moveCrates(
            LinkedList<Character> from,
            LinkedList<Character> to,
            LinkedList<Character> tmp,
            int count
    ) {
        stackCrates( from, tmp, count );
        stackCrates( tmp, to, count );
    }

    private void stackCrates(
            LinkedList<Character> from,
            LinkedList<Character> to,
            int count
    ) {
        for ( int i = 0; i < count; ++i ) {
            to.addLast( from.removeLast() );
        }
    }

    private static String getResult( List<LinkedList<Character>> stacks ) {
        return stacks.stream()
                     .map( LinkedList::peekLast )
                     .reduce( new StringBuilder(), StringBuilder::append, StringBuilder::append )
                     .toString();
    }

    interface Mover {
        void move( LinkedList<Character> from, LinkedList<Character> to, int count );
    }
}

