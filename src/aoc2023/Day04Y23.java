package aoc2023;

import common.AocDay;
import common.Utils;

import java.util.Arrays;

public class Day04Y23 implements AocDay<Long, Long> {
    private static final boolean[] WINNING_BUFFER = new boolean[100];

    private final String filename;

    public static void main( String[] args ) {
        try {
            Utils.executeDay( new Day04Y23( "input/2023/Y23D04S1.DAT" ), 13L, 30L );
            Utils.executeDay( new Day04Y23( "input/2023/Y23D04I.DAT" ), 28538L, 9425061L );
        } catch ( Throwable e ) {
            e.printStackTrace();
        }
    }

    public Day04Y23( String filename ) {
        this.filename = filename;
    }

    @Override
    public String sampleName() {
        return filename;
    }

    @Override
    public Long task1() throws Throwable {
        return Utils.lines( filename )
                    .mapToLong( this::calculateCard )
                    .map( r -> 1L << r >> 1 )
                    .sum();
    }

    @Override
    public Long task2() throws Throwable {
        long[] matches = Utils.lines( filename )
                              .mapToLong( this::calculateCard )
                              .toArray();
        long[] copies = new long[matches.length];
        Arrays.fill( copies, 1L );
        long result = 0;
        for ( int checkedCard = 0; checkedCard < matches.length; ++checkedCard ) {
            long checkedCopies = copies[checkedCard];
            result += checkedCopies;
            long checkedMatches = matches[checkedCard];
            for ( int wonCard = checkedCard + 1; wonCard <= checkedCard + checkedMatches; ++wonCard ) {
                copies[wonCard] += checkedCopies;
            }
        }
        return result;
    }

    private long calculateCard( String card ) {
        String[] parts = card.split( "(: +)|( \\| +)" );
        Arrays.fill( WINNING_BUFFER, false );
        Arrays.stream( parts[1].split( " +" ) )
              .mapToInt( Integer::parseInt )
              .forEach( n -> WINNING_BUFFER[n] = true );
        return Arrays.stream( parts[2].split( " +" ) )
                     .mapToInt( Integer::parseInt )
                     .filter( n -> WINNING_BUFFER[n] )
                     .count();
    }
}
