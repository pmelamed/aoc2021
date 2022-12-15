package aoc2022;

import common.AocDay;
import common.Utils;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Objects;
import java.util.OptionalLong;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

public class Day15Y22 implements AocDay<Integer, Long> {
    private static final Pattern SENSOR_PATTERN = Pattern.compile(
            "Sensor at x=(-?\\d+), y=(-?\\d+): closest beacon is at x=(-?\\d+), y=(-?\\d+)"
    );

    private final String fileName;
    private final Sensor[] sensors;
    private final int cutCoord;
    private final int maxCoord;

    public static void main( String[] args ) {
        try {
            Utils.executeDay(
                    new Day15Y22( "input/2022/Y22D15I.DAT", 2_000_000, 4_000_000 ),
                    4560025,
                    12_480_406_634_249L
            );
        } catch ( Throwable e ) {
            e.printStackTrace();
        }
    }

    public Day15Y22( String fileName, int cutCoord, int maxCoord ) {
        this.fileName = fileName;
        this.cutCoord = cutCoord;
        this.maxCoord = maxCoord;
        this.sensors = Utils.lines( fileName )
                            .map( this::parseSensor )
                            .toArray( Sensor[]::new );
    }

    @Override
    public String sampleName() {
        return fileName;
    }

    public Integer task1() {
        Range[] ranges = Arrays.stream( sensors )
                               .map( s -> getSensorRange( s, cutCoord ) )
                               .filter( Objects::nonNull )
                               .sorted( Comparator.comparing( Range::start ) )
                               .toArray( Range[]::new );
        int result = 0;
        int nextRangeStart = Integer.MIN_VALUE;
        for ( Range range : ranges ) {
            int addRangeStart = Math.max( nextRangeStart, range.start() );
            if ( range.end() >= addRangeStart ) {
                result += range.end() - addRangeStart + 1;
                nextRangeStart = range.end() + 1;
            }
        }
        long beaconsOnLine = Arrays.stream( sensors )
                                   .filter( s -> s.beaconY() == cutCoord )
                                   .mapToInt( Sensor::beaconX )
                                   .distinct()
                                   .count();
        return result - (int) beaconsOnLine;
    }

    public Long task2() {
        return IntStream.range( 0, maxCoord )
                        .mapToObj( this::scanLine )
                        .filter( OptionalLong::isPresent )
                        .findFirst()
                        .orElseThrow( () -> new RuntimeException( "Not found!" ) )
                        .orElseThrow( () -> new RuntimeException( "Not found!" ) );
    }

    private static Range getSensorRange( Sensor sensor, int cutCoord ) {
        int width = sensor.radius - Math.abs( sensor.y - cutCoord );
        if ( width < 0 ) {
            return null;
        }
        return new Range( sensor.x - width, sensor.x + width );
    }

    private OptionalLong scanLine( int y ) {
        Range[] ranges = Arrays.stream( sensors )
                               .map( s -> getSensorRange( s, y ) )
                               .filter( Objects::nonNull )
                               .filter( r -> r.end() >= 0 )
                               .sorted( Comparator.comparing( Range::start ) )
                               .toArray( Range[]::new );
        int nextRangeStart = 0;
        for ( Range range : ranges ) {
            if ( range.start() > nextRangeStart ) {
                return OptionalLong.of( nextRangeStart * 4_000_000L + y );
            }
            nextRangeStart = Math.max( nextRangeStart, range.end() + 1 );
        }
        return OptionalLong.empty();
    }

    private Sensor parseSensor( String str ) {
        Matcher matcher = SENSOR_PATTERN.matcher( str );
        if ( !matcher.find() ) {
            throw new RuntimeException( "Bad sensor description" );
        }
        int sensorX = Integer.parseInt( matcher.group( 1 ) );
        int sensorY = Integer.parseInt( matcher.group( 2 ) );
        int beaconX = Integer.parseInt( matcher.group( 3 ) );
        int beaconY = Integer.parseInt( matcher.group( 4 ) );
        return new Sensor(
                sensorX,
                sensorY,
                beaconX,
                beaconY,
                Math.abs( beaconX - sensorX ) + Math.abs( beaconY - sensorY )
        );
    }

    private record Sensor( int x, int y, int beaconX, int beaconY, int radius ) {
    }

    private record Range( int start, int end ) {
    }
}

