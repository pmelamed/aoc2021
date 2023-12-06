// https://adventofcode.com/2023/day/5
package aoc2023;

import common.AocDay;
import common.Utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Day05Y23 implements AocDay<Long, Long> {
    private final String filename;

    private final List<Mapping> mappings = new ArrayList<>();
    private final long[] seeds;

    private record MappingRange( long dstStart, long srcStart, long length ) {
        private boolean contains( long value ) {
            return srcStart <= value && value < srcStart + length;
        }

        private long mapValue( long value ) {
            return value - srcStart + dstStart;
        }
    }

    private record RangeEdge( long point, boolean start, long offset ) {
    }

    private record DestRange( long start, long end ) {
    }

    private static class Mapping {
        private final String name;
        private final TreeMap<Long, MappingRange> map = new TreeMap<>();
        private final List<RangeEdge> edges = new ArrayList<>();

        public Mapping( String name, Collection<MappingRange> ranges ) {
            this.name = name;
            for ( MappingRange range : ranges ) {
                map.put( range.srcStart, range );
                edges.add( new RangeEdge( range.srcStart, true, range.dstStart - range.srcStart ) );
                edges.add( new RangeEdge( range.srcStart + range.length, false, range.dstStart - range.srcStart ) );
            }
            edges.sort(
                    Comparator.comparing( RangeEdge::point )
                              .thenComparing( Comparator.comparing( RangeEdge::start ) )
            );
        }

        private long mapValue( long value ) {
            Map.Entry<Long, MappingRange> entry = map.floorEntry( value );
            return Optional.ofNullable( entry )
                           .map( Map.Entry::getValue )
                           .filter( range -> range.contains( value ) )
                           .map( range -> range.mapValue( value ) )
                           .orElse( value );
        }

        private Stream<DestRange> mapRange( DestRange range ) {
            ArrayList<DestRange> result = new ArrayList<>();
            long point = range.start();
            long end = range.end();
            long lastOffset = 0L;
            for ( RangeEdge edge : edges ) {
                if ( end < edge.point() ) {
                    break;
                }
                if ( point < edge.point() ) {
                    result.add( new DestRange( point + lastOffset, edge.point() + lastOffset ) );
                    point = edge.point();
                }
                lastOffset = edge.start() ? edge.offset() : 0L;
            }
            if ( point < end ) {
                result.add( new DestRange( point + lastOffset, range.end() + lastOffset ) );
            }
            return result.stream();
        }
    }

    public static void main( String[] args ) {
        try {
            Utils.executeDay( new Day05Y23( "input/2023/Y23D05S1.DAT" ), 35L, 46L );
            Utils.executeDay( new Day05Y23( "input/2023/Y23D05I.DAT" ), 662197086L, 52510809L );
        } catch ( Throwable e ) {
            e.printStackTrace();
        }
    }

    public Day05Y23( String filename ) {
        this.filename = filename;
        List<String> lines = Utils.readLines( filename );
        int line = 2;
        while ( line < lines.size() ) {
            String mappingName = lines.get( line ).split( " " )[0];
            line++;
            List<MappingRange> ranges = new ArrayList<>();
            while ( line < lines.size() && !lines.get( line ).isEmpty() ) {
                String[] values = lines.get( line ).split( " " );
                ranges.add( new MappingRange(
                        Long.parseLong( values[0] ),
                        Long.parseLong( values[1] ),
                        Long.parseLong( values[2] )
                ) );
                line++;
            }
            mappings.add( new Mapping( mappingName, ranges ) );
            line++;
        }
        seeds = Arrays.stream( lines.get( 0 ).split( " " ) )
                      .skip( 1 )
                      .mapToLong( Long::parseLong )
                      .toArray();
    }

    @Override
    public String sampleName() {
        return filename;
    }

    @Override
    public Long task1() throws Throwable {
        return Arrays.stream( seeds )
                     .map( this::fullMapping )
                     .min()
                     .orElseThrow();
    }

    @Override
    public Long task2() throws Throwable {
        List<DestRange> sources = new ArrayList<>();
        for ( int pair = 0; pair < seeds.length; pair += 2 ) {
            sources.add( new DestRange( seeds[pair], seeds[pair] + seeds[pair + 1] ) );
        }
        for ( Mapping mapping : mappings ) {
            sources = sources.stream()
                             .flatMap( mapping::mapRange )
                             .collect( Collectors.toList() );
        }
        return sources.stream().mapToLong( DestRange::start ).min().orElseThrow();
    }

    private long fullMapping( long seed ) {
        for ( Mapping mapping : mappings ) {
            seed = mapping.mapValue( seed );
        }
        return seed;
    }
}
