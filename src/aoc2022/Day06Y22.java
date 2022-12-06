package aoc2022;

import common.AocDay;
import common.Utils;

import java.util.Arrays;

public class Day06Y22 implements AocDay<Integer, Integer> {

    private final String name;
    private final String data;

    public static void main( String[] args ) {
        try {
            executeTasks( "S1", "mjqjpqmgbljsphdztnvjfqwrcgsmlb", 7, 19 );
            executeTasks( "S2", "bvwbjplbgvbhsrlpgdmjqwftvncz", 5, 23 );
            executeTasks( "S3", "nppdvjthqldpwncqszvftbrmjlhg", 6, 23 );
            executeTasks( "S4", "nznrnfrfntjfmvfwmzdfjlvtqnbhcprsg", 10, 29 );
            executeTasks( "S5", "zcfzfwzzqfrljwzlrfnpqdbhtmscgvjw", 11, 26 );
            executeTasks( "My Input", Utils.readFirstLine( "input/2022/Y22D06I.DAT" ), 1766, 2383 );
        } catch ( Throwable e ) {
            e.printStackTrace();
        }
    }

    public static void executeTasks( String name, String data, Integer expected1, Integer expected2 ) {
        Utils.executeDay( new Day06Y22( name, data ), expected1, expected2 );
    }

    public Day06Y22( String name, String data ) {
        this.name = name;
        this.data = data;
    }

    @Override
    public String sampleName() {
        return name;
    }

    public Integer task1() {
        return detectUniqueSequence( 4 );
    }


    public Integer task2() {
        return detectUniqueSequence( 14 );
    }

    private int detectUniqueSequence( int desiredLength ) {
        int[] positions = new int[128];
        Arrays.fill( positions, -desiredLength - 1 );
        char[] chars = data.toCharArray();
        int packetStart = 0;
        int position = 0;
        for ( position = 0; position < chars.length && position - packetStart < desiredLength; ++position ) {
            char currentChar = chars[position];
            if ( position < positions[currentChar] + desiredLength ) {
                if ( packetStart <= positions[currentChar] ) {
                    packetStart = positions[currentChar] + 1;
                }
            }
            positions[currentChar] = position;
        }
        return position;
    }
}

