package aoc2022;

import common.AocDay;
import common.Utils;

import java.util.Arrays;
import java.util.List;
import java.util.TreeMap;

public class Day03Y22 implements AocDay<Integer, Integer> {
    private static final int[] PRIORITIES = new int[128];

    static {
        for ( char symbol = 'a'; symbol <= 'z'; ++symbol ) {
            PRIORITIES[symbol] = symbol - 'a' + 1;
        }
        for ( char symbol = 'A'; symbol <= 'Z'; ++symbol ) {
            PRIORITIES[symbol] = symbol - 'A' + 27;
        }
    }

    private static final class SymbolSet {
        private final boolean[] present = new boolean[128];

        private void add( char symbol ) {
            present[symbol] = true;
        }

        private boolean get( char symbol ) {
            return present[symbol];
        }

        private void clear() {
            Arrays.fill( present, false );
        }
    }


    private final String name;

    public static void main( String[] args ) {
        try {
            executeTasks( "input/2022/Y22D03I.dat", 7908, 2838 );
        } catch ( Throwable e ) {
            e.printStackTrace();
        }
    }

    public static void executeTasks( String fileName, Integer expected1, Integer expected2 ) {
        Utils.executeDay( new Day03Y22( fileName ), expected1, expected2 );
    }

    public Day03Y22( String file ) {
        this.name = file;
    }

    @Override
    public String sampleName() {
        return name;
    }

    public Integer task1() {
        SymbolSet set = new SymbolSet();
        return Utils.lines( name )
                    .filter( s -> !s.isEmpty() )
                    .mapToInt( r -> getRucksackPriority( set, r ) )
                    .sum();
    }

    public Integer task2() {
        SymbolSet set1 = new SymbolSet();
        SymbolSet set2 = new SymbolSet();
        List<String> lines = Utils.readLines( name );
        int result = 0;
        for ( int line = 0; line < lines.size(); line += 3 ) {
            result += getBadgePriority( set1, set2, lines.get( line ), lines.get( line + 1 ), lines.get( line + 2 ) );
        }
        return result;
    }

    private int getRucksackPriority( SymbolSet set, String contents ) {
        set.clear();
        char[] symbols = contents.toCharArray();
        int half = symbols.length / 2;
        for ( int i = 0; i < half; ++i ) {
            set.add( symbols[i] );
        }
        for ( int i = half; i < symbols.length; ++i ) {
            if ( set.get( symbols[i] ) ) {
                return PRIORITIES[symbols[i]];
            }
        }
        throw new RuntimeException( "Error not found" );
    }

    private int getBadgePriority(
            SymbolSet set1,
            SymbolSet set2,
            String elf1,
            String elf2,
            String elf3
    ) {
        set1.clear();
        for ( char s : elf1.toCharArray() ) {
            set1.add( s );
        }
        set2.clear();
        for ( char s : elf2.toCharArray() ) {
            if ( set1.get( s ) ) {
                set2.add( s );
            }
        }
        for ( char s : elf3.toCharArray() ) {
            if ( set2.get( s ) ) {
                return PRIORITIES[s];
            }
        }
        throw new RuntimeException( "Badge not found" );
    }
}

