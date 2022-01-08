package aoc2019;

import common.AocDay;
import common.Utils;

import java.util.Arrays;

public class Day16Y19 implements AocDay<String, String> {

    private final String name;
    private final int[] source;

    public static void main( String[] args ) {
        try {
            executeTasks( "input/2019/d16i.dat", "40921727", "89950138" );
        } catch ( Throwable e ) {
            e.printStackTrace();
        }
    }

    public static void executeTasks( String fileName, String expected1, String expected2 ) {
        Utils.executeDay(
                new Day16Y19( fileName ),
                expected1,
                expected2
        );
    }

    public Day16Y19( String file ) {
        this.name = file;
        char[] chars = Utils.readLines( file ).get( 0 ).toCharArray();
        this.source = new int[chars.length];
        for ( int index = 0; index < chars.length; index++ ) {
            this.source[index] = chars[index] - '0';
        }
    }

    @Override
    public String sampleName() {
        return name;
    }

    public String task1() throws InterruptedException {
        int[] result = Arrays.copyOf( source, source.length );
        int[] buffer = new int[result.length];
        for ( int round = 0; round < 100; round++ ) {
            for ( int dstIndex = 0; dstIndex < result.length; ++dstIndex ) {
                buffer[dstIndex] = calculateRow( dstIndex + 1, result.length, result );
            }
            System.arraycopy( buffer, 0, result, 0, result.length );
        }
        return getRangeString( result, 0, 8 );
    }

    public String task2() throws InterruptedException {
        int length = source.length;
        int[] result = new int[length * 10_000];
        for ( int index = 0; index < 10_000; index++ ) {
            System.arraycopy( source, 0, result, index * length, length );
        }
        int offset = Integer.parseInt( getRangeString( source, 0, 7 ) );
        for ( int step = 0; step < 100; step++ ) {
            for ( int index = result.length - 2; index >= offset; index-- ) {
                result[index] = ( result[index] + result[index + 1] ) % 10;
            }
        }
        return getRangeString( result, offset, 8 );
    }

    private int calculateRow( int position, int length, int[] data ) {
        int srcIndex = -1;
        long value = 0;
        while ( srcIndex < length ) {
            srcIndex += position;
            for ( int index = 0; index < position && srcIndex < length; index++, srcIndex++ ) {
                value += data[srcIndex];
            }
            srcIndex += position;
            for ( int index = 0; index < position && srcIndex < length; index++, srcIndex++ ) {
                value -= data[srcIndex];
            }
        }
        return (int) ( Math.abs( value ) % 10 );
    }

    private String getRangeString( int[] src, int start, int length ) {
        char[] chars = new char[length];
        for ( int index = 0; index < length; index++ ) {
            chars[index] = (char) ( '0' + src[start + index] );
        }
        return new String( chars );
    }
}
