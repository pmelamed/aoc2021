package aoc2021;

import common.AocDay;
import common.Utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Day04Y21 implements AocDay<Long, Long> {

    private static class Board {
        private final List<Set<Integer>> lines;
        private final Set<Integer> board = new HashSet<>();

        private Board( Collection<String> boardStrings ) {
            int[][] numbers = boardStrings.stream().map( this::parseBoardLine ).toArray( int[][]::new );

            lines = IntStream.range( 0, 10 )
                             .mapToObj( i -> new HashSet<Integer>() )
                             .collect( Collectors.toList() );

            for ( int row = 0; row < 5; ++row ) {
                for ( int col = 0; col < 5; ++col ) {
                    board.add( numbers[row][col] );
                    lines.get( row ).add( numbers[row][col] );
                    lines.get( 5 + col ).add( numbers[row][col] );
                }
            }
        }

        private int[] parseBoardLine( String line ) {
            return Arrays.stream( line.split( " " ) )
                         .filter( Predicate.not( String::isBlank ) )
                         .mapToInt( Integer::parseInt )
                         .toArray();
        }

        private boolean numberCalled( int number ) {
            lines.forEach( line -> line.remove( number ) );
            board.remove( number );
            return lines.stream().anyMatch( Set::isEmpty );
        }

        private int unmarkedSum() {
            return board.stream()
                        .mapToInt( Integer::intValue )
                        .sum();
        }
    }

    private final String name;
    private final int[] called;
    private final List<Board> boards;

    public static void main( String[] args ) {
        try {
            executeTasks( "input/Y21D04S1.dat", 4512L, 1924L );
            executeTasks( "input/Y21D04I.dat", null, null );
        } catch ( Throwable e ) {
            e.printStackTrace();
        }
    }

    public static void executeTasks( String fileName, Long expected1, Long expected2 ) {
        Utils.executeDay(
                new Day04Y21( fileName ),
                new Day04Y21( fileName ),
                expected1,
                expected2
        );
    }

    public Day04Y21( String file ) {
        this.name = file;
        List<String> lines = Utils.lines( file ).collect( Collectors.toList() );
        called = Arrays.stream( lines.get( 0 ).split( "," ) ).mapToInt( Integer::parseInt ).toArray();
        boards = new ArrayList<>();
        for ( int line = 2; line < lines.size(); line += 6 ) {
            boards.add( new Board(
                    lines.stream()
                         .skip( line )
                         .limit( 5 )
                         .collect( Collectors.toList() )
            ) );
        }
    }

    @Override
    public String sampleName() {
        return name;
    }

    public Long task1() {
        for ( int number : called ) {
            for ( Board board : boards ) {
                if ( board.numberCalled( number ) ) {
                    return (long) number * board.unmarkedSum();
                }
            }
        }
        return 0L;
    }

    public Long task2() {
        Board lastBoard;
        for ( int number : called ) {
            lastBoard = boards.get( 0 );
            boards.removeIf( board -> board.numberCalled( number ) );
            if ( boards.isEmpty() ) {
                return (long) number * lastBoard.unmarkedSum();
            }
        }
        return 0L;
    }
}
