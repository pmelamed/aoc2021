package aoc2021;

import common.AocDay;
import common.Utils;

import java.util.List;

public class Day25Y21 implements AocDay<Long, Long> {
    private final String name;
    private final char[][] field;
    private final int width;
    private final int height;

    public static void main( String[] args ) {
        try {
            executeTasks( "input/2021/Y21D25S1.dat", 58L, null );
            executeTasks( "input/2021/Y21D25I.dat", null, null );
        } catch ( Throwable e ) {
            e.printStackTrace();
        }
    }

    public static void executeTasks( String fileName, Long expected1, Long expected2 ) {
        Utils.executeDay(
                new Day25Y21( fileName ),
                expected1,
                expected2
        );
    }

    public Day25Y21( String file ) {
        this.name = file;
        List<String> lines = Utils.readLines( file );
        field = new char[lines.size()][];
        for ( int index = 0; index < field.length; ++index ) {
            field[index] = lines.get( index ).toCharArray();
        }
        height = field.length;
        width = field[0].length;
    }

    @Override
    public String sampleName() {
        return name;
    }

    public Long task1() {
        char[][] current = field;
        int moveNo = 0;
        do {
            ++moveNo;
            current = move( current );
        } while ( current != null );
        return (long) moveNo;
    }

    public Long task2() {
        return 0L;
    }

    private char[][] move( char[][] prev ) {
        char[][] result1 = new char[height][width];
        boolean moved = false;
        // dumpField( prev );
        for ( int row = 0; row < height; ++row ) {
            for ( int col = 0; col < width; ++col ) {
                if ( prev[row][col] == '>' && prev[row][( col + 1 ) % width] == '.' ) {
                    result1[row][col] = '.';
                    result1[row][( col + 1 ) % width] = '>';
                    moved = true;
                } else if ( result1[row][col] == 0 ) {
                    result1[row][col] = prev[row][col];
                }
            }
        }
        prev = result1;
        // dumpField( prev );
        result1 = new char[height][width];
        for ( int row = 0; row < height; ++row ) {
            for ( int col = 0; col < width; ++col ) {
                if ( prev[row][col] == 'v' && prev[( row + 1 ) % height][col] == '.' ) {
                    result1[row][col] = '.';
                    result1[( row + 1 ) % height][col] = 'v';
                    moved = true;
                } else if ( result1[row][col] == 0 ) {
                    result1[row][col] = prev[row][col];
                }
            }
        }
        return moved ? result1 : null;
    }

    private void dumpField( char[][] field ) {
        System.out.println();
        for ( char[] line : field ) {
            System.out.println( new String( line ) );
        }
    }
}
