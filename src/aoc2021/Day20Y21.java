package aoc2021;

import common.AocDay;
import common.Utils;

import java.util.List;

public class Day20Y21 implements AocDay<Long, Long> {

    private final String name;

    public static void main( String[] args ) {
        try {
            executeTasks( "input/Y21D20S1.dat", 35L, 3351L );
            executeTasks( "input/Y21D20I.dat", null, null );
        } catch ( Throwable e ) {
            e.printStackTrace();
        }
    }

    public static void executeTasks( String fileName, Long expected1, Long expected2 ) {
        Utils.executeDay(
                new Day20Y21( fileName ),
                expected1,
                expected2
        );
    }

    public Day20Y21( String file ) {
        this.name = file;
    }

    @Override
    public String sampleName() {
        return name;
    }

    public Long task1() {
        return enhanceImageTimes( 2 );
    }


    public Long task2() {
        return enhanceImageTimes( 50 );
    }

    private long enhanceImageTimes( int times ) {
        List<String> input = Utils.readLines( name );
        byte[] enhancer = readEnhancer( input.get( 0 ) );
        int sourceHeight = input.size() - 2;
        int sourceWidth = input.get( 2 ).length();
        int offset = times + 2;
        int fullHeight = sourceHeight + offset * 2;
        int fullWidth = sourceWidth + offset * 2;
        byte[][] image = new byte[fullHeight][fullWidth];
        for ( int row = 0; row < sourceHeight; ++row ) {
            char[] rowStr = input.get( row + 2 ).toCharArray();
            for ( int col = 0; col < sourceWidth; ++col ) {
                image[row + offset][col + offset] = (byte) ( rowStr[col] == '#' ? 1 : 0 );
            }
        }
//        dumpImage( image );
        for ( int time = 0; time < times; ++time ) {
            image = enhanceImage( image, fullWidth, fullHeight, enhancer );
//            dumpImage( image );
        }
        return scanImage( image );
    }

    private byte[] readEnhancer( String enhancerStr ) {
        byte[] enhancer = new byte[enhancerStr.length()];
        int index = 0;
        for ( char ch : enhancerStr.toCharArray() ) {
            enhancer[index++] = (byte) ( ch == '#' ? 1 : 0 );
        }
        return enhancer;
    }

    private byte[][] enhanceImage( byte[][] image, int width, int height, byte[] enhancer ) {
        byte[][] result = new byte[height][width];
        for ( int row = 0; row < height; ++row ) {
            for ( int col = 0; col < width; ++col ) {
                result[row][col] = enhancePixel( image, row, col, enhancer );
            }
        }
        return result;
    }

    private byte enhancePixel( byte[][] image, int row, int col, byte[] enhancer ) {
        int buffer = 0;
        int height = image.length;
        int width = image[0].length;
        for ( int r = row - 1; r <= row + 1; ++r ) {
            for ( int c = col - 1; c <= col + 1; ++c ) {
                byte pixel = r > 0 && c > 0 && r < height && c < width ? image[r][c] : image[row][col];
                buffer = ( buffer << 1 ) + pixel;
            }
        }
        return enhancer[buffer];
    }

    private long scanImage( byte[][] image ) {
        long count = 0;
        for ( byte[] row : image ) {
            for ( byte pixel : row ) {
                count += pixel;
            }
        }
        return count;
    }

    private void dumpImage( byte[][] image ) {
        System.out.println();
        for ( byte[] row : image ) {
            for ( byte pixel : row ) {
                System.out.print( pixel == 1 ? '#' : '.' );
            }
            System.out.println();
        }
    }
}
