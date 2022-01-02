package aoc2019;

import common.AocDay;
import common.Utils;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Day06Y19 implements AocDay<Long, Long> {

    enum Targets {
        YOU, SAN
    }

    private final String name;
    private final Map<String, List<String>> linksCenterOrbit;


    public static void main( String[] args ) {
        try {
            executeTasks( "input/2019/d06i.dat", null, null );
        } catch ( Throwable e ) {
            e.printStackTrace();
        }
    }

    public static void executeTasks( String fileName, Long expected1, Long expected2 ) {
        Utils.executeDay(
                new Day06Y19( fileName ),
                expected1,
                expected2
        );
    }

    public Day06Y19( String file ) {
        this.name = file;
        linksCenterOrbit = Utils.lines( file )
                                .map( line -> line.split( "\\)" ) )
                                .collect( Collectors.toMap(
                                        pair -> pair[0],
                                        pair -> List.of( pair[1] ),
                                        this::mergeLists
                                ) );
    }

    @Override
    public String sampleName() {
        return name;
    }

    public Long task1() {
        return getLinksCount( "COM", 0 );
    }

    public Long task2() {
        EnumMap<Targets, List<String>> targetWays = new EnumMap<>( Targets.class );
        buildWayToYouAndSanta( "COM", new ArrayList<>(), targetWays );
        int lastCommonParentPos = 0;
        List<String> wayToYou = targetWays.get( Targets.YOU );
        List<String> wayToSanta = targetWays.get( Targets.SAN );
        while ( lastCommonParentPos < wayToYou.size()
                && lastCommonParentPos < wayToSanta.size()
                && wayToYou.get( lastCommonParentPos ).equals( wayToSanta.get( lastCommonParentPos ) ) ) {
            ++lastCommonParentPos;
        }
        return (long) ( ( wayToYou.size() - lastCommonParentPos - 1 ) +
                ( wayToSanta.size() - lastCommonParentPos - 1 ) );
    }

    private long getLinksCount( String center, int linksToHere ) {
        return linksToHere + linksCenterOrbit.getOrDefault( center, List.of() )
                                             .stream()
                                             .mapToLong( orbit -> getLinksCount( orbit, linksToHere + 1 ) )
                                             .sum();
    }

    private void buildWayToYouAndSanta( String center, List<String> way, Map<Targets, List<String>> targetWays ) {
        way.add( center );
        try {
            if ( center.equals( Targets.YOU.name() ) ) {
                targetWays.put( Targets.YOU, new ArrayList<>( way ) );
            }
            if ( center.equals( Targets.SAN.name() ) ) {
                targetWays.put( Targets.SAN, new ArrayList<>( way ) );
            }
            List<String> children = linksCenterOrbit.getOrDefault( center, List.of() );
            for ( int index = 0; index < children.size() && targetWays.size() < 2; ++index ) {
                buildWayToYouAndSanta( children.get( index ), way, targetWays );
            }
        } finally {
            way.remove( way.size() - 1 );
        }
    }

    private ArrayList<String> mergeLists( List<String> list1, List<String> list2 ) {
        ArrayList<String> merged = new ArrayList<>( list1 );
        merged.addAll( list2 );
        return merged;
    }
}
