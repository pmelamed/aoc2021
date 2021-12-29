package aoc2021;

import common.AocDay;
import common.Utils;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;

public class Day12Y21Optimized implements AocDay<Long, Long> {

    private static record Cave(
            String name,
            Set<Cave> connections,
            boolean large,
            boolean start,
            boolean end,
            int hash
    ) {
        private Cave( String name ) {
            this(
                    name,
                    new HashSet<>(),
                    name.toUpperCase().equals( name ),
                    name.equals( "start" ),
                    name.equals( "end" ),
                    name.hashCode()
            );
        }

        @Override
        public int hashCode() {
            return hash;
        }
    }

    private final String name;

    public static void main( String[] args ) {
        try {
            executeTasks( "input/2021/Y21D12S1.dat", 10L, 36L );
            executeTasks( "input/2021/Y21D12I.dat", null, null );
        } catch ( Throwable e ) {
            e.printStackTrace();
        }
    }

    public static void executeTasks( String fileName, Long expected1, Long expected2 ) {
        Utils.executeDay(
                new Day12Y21Optimized( fileName ),
                expected1,
                expected2
        );
    }

    public Day12Y21Optimized( String file ) {
        this.name = file;
    }

    @Override
    public String sampleName() {
        return name;
    }

    public Long task1() {
        return task( this::findAllPaths );
    }

    public Long task2() {
        return task( ( from, visitedSmall ) -> findAllPaths2( from, visitedSmall, null ) );
    }

    private long task( BiFunction<Cave, Set<Cave>, Long> pathFinder ) {
        Cave start = initCavesMap();
        Set<Cave> visited = new HashSet<>();
        visited.add( start );
        return pathFinder.apply( start, visited );
    }

    private Cave initCavesMap() {
        Map<String, Cave> caveByName = new HashMap<>();
        Utils.lines( name ).forEach( line -> {
            String[] caves = line.split( "-" );
            Cave cave1 = caveByName.computeIfAbsent( caves[0], Cave::new );
            Cave cave2 = caveByName.computeIfAbsent( caves[1], Cave::new );
            cave1.connections.add( cave2 );
            cave2.connections.add( cave1 );
        } );
        return caveByName.get( "start" );
    }

    private long findAllPaths( Cave from, Set<Cave> visitedSmall ) {
        long count = 0;
        for ( Cave to : from.connections() ) {
            if ( to.end() ) {
                ++count;
            } else if ( to.large() ) {
                count += findAllPaths( to, visitedSmall );
            } else if ( !visitedSmall.contains( to ) ) {
                visitedSmall.add( to );
                count += findAllPaths( to, visitedSmall );
                visitedSmall.remove( to );
            }
        }
        return count;
    }

    private long findAllPaths2( Cave from, Set<Cave> visitedSmall, Cave visitedTwice ) {
        long count = 0;
        for ( Cave to : from.connections() ) {
            if ( to.end() ) {
                ++count;
            } else if ( to.large() ) {
                count += findAllPaths2( to, visitedSmall, visitedTwice );
            } else if ( !visitedSmall.contains( to ) ) {
                visitedSmall.add( to );
                count += findAllPaths2( to, visitedSmall, visitedTwice );
                visitedSmall.remove( to );
            } else if ( visitedTwice == null && !to.start() ) {
                count += findAllPaths2( to, visitedSmall, to );
            }
        }
        return count;
    }

}
