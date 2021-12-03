package aoc2021;

import common.AocDay;
import common.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Day03Y21 implements AocDay<Long, Long> {

    private final String name;
    private final List<String> data;

    public static void main( String[] args ) {
        try {
            executeTasks( "input/Y21D03S1.dat", 198L, 230L );
            executeTasks( "input/Y21D03I.dat", null, null );
        } catch ( Throwable e ) {
            e.printStackTrace();
        }
    }

    public static void executeTasks( String fileName, Long expected1, Long expected2 ) {
        Utils.executeDay( new Day03Y21( fileName ), expected1, expected2 );
    }

    public Day03Y21( String file ) {
        this.name = file;
        data = Utils.lines( file ).collect( Collectors.toList() );
    }

    @Override
    public String sampleName() {
        return name;
    }

    public Long task1() {
        var codeLength = data.get( 0 ).length();
        var commonThreshold = data.size() / 2;
        long[] ones = new long[codeLength];
        for ( var value : data ) {
            for ( int bit = 0; bit < codeLength; ++bit ) {
                if ( value.charAt( bit ) == '1' ) {
                    ones[bit]++;
                }
            }
        }
        long gamma = 0L;
        long epsilon = 0L;
        for ( int bit = 0; bit < codeLength; ++bit ) {
            gamma <<= 1;
            epsilon <<= 1;
            if ( ones[bit] > commonThreshold ) {
                ++gamma;
            } else {
                ++epsilon;
            }
        }
        return gamma * epsilon;
    }

    public Long task2() {
        List<String> oxygenList = new ArrayList<>( data );
        List<String> scrubberList = new ArrayList<>( data );
        int bit = 0;
        do {
            oxygenList = getCommon( separate( oxygenList, bit ) );
            ++bit;
        } while ( oxygenList.size() > 1 );
        bit = 0;
        do {
            scrubberList = getLeast( separate( scrubberList, bit ) );
            ++bit;
        } while ( scrubberList.size() > 1 );
        String oxygen = oxygenList.get( 0 );
        String scrubber = scrubberList.get( 0 );
        return Long.parseLong( oxygen, 2 ) * Long.parseLong( scrubber, 2 );
    }

    private Map<Character, List<String>> separate( List<String> values, int bit ) {
        return values.stream().collect( Collectors.groupingBy( value -> value.charAt( bit ) ) );
    }

    private List<String> getCommon( Map<Character, List<String>> map ) {
        List<String> zeros = map.get( '0' );
        List<String> ones = map.get( '1' );
        return zeros.size() > ones.size() ? zeros : ones;
    }

    private List<String> getLeast( Map<Character, List<String>> map ) {
        List<String> zeros = map.get( '0' );
        List<String> ones = map.get( '1' );
        return zeros.size() <= ones.size() ? zeros : ones;
    }

}
