package aoc2019;

import common.AocDay;
import common.Utils;

import java.util.Arrays;

public class Day08Y19 implements AocDay<Integer, Integer> {

    private final String name;
    private final int width;
    private final int height;
    private final String data;

    public static void main( String[] args ) {
        try {
            executeTasks( "input/2019/d08i.dat", null, null );
        } catch ( Throwable e ) {
            e.printStackTrace();
        }
    }

    public static void executeTasks( String fileName, Integer expected1, Integer expected2 ) {
        Utils.executeDay( new Day08Y19( fileName, 25, 6 ), expected1, expected2 );
    }

    public Day08Y19( String file, int width, int height ) {
        this.name = file;
        this.width = width;
        this.height = height;
        this.data = Utils.readLines( file ).get( 0 );
    }

    @Override
    public String sampleName() {
        return name;
    }

    public Integer task1() {
        char[] chars = data.toCharArray();
        int layerSize = width * height;
        int[] counter = new int[3];
        int minZeroCount = Integer.MAX_VALUE;
        int result = 0;
        for ( int charIndex = 0; charIndex < chars.length; ) {
            Arrays.fill( counter, 0 );
            for ( int layerChar = 0; layerChar < layerSize; layerChar++, charIndex++ ) {
                counter[chars[charIndex] - '0']++;
            }
            if ( minZeroCount > counter[0] ) {
                minZeroCount = counter[0];
                result = counter[1] * counter[2];
            }
        }
        return result;
    }

    public Integer task2() {
        char[] chars = data.toCharArray();
        char[][] result = new char[height][width];
        for ( char[] line : result ) {
            Arrays.fill( line, '2' );
        }
        for ( int charIndex = 0; charIndex < chars.length; ) {
            for ( int row = 0; row < height; row++ ) {
                for ( int col = 0; col < width; col++, charIndex++ ) {
                    if ( result[row][col] == '2' ) {
                        result[row][col] = chars[charIndex];
                    }
                }
            }
        }
        System.out.println();
        for ( char[] line : result ) {
            for ( char c : line ) {
                System.out.print(
                        switch ( c ) {
                            case '0' -> "..";
                            case '1' -> "##";
                            default -> "  ";
                        }
                );
            }
            System.out.println();
        }
        return null;
    }


}
