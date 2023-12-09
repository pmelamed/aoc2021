// https://adventofcode.com/2023/day/6
package aoc2023;

import common.AocDay;
import common.Utils;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Day08Y23 implements AocDay<Long, Long> {
    private static final Pattern NODE_PATTERN = Pattern.compile( "([A-Z0-9]{3}) = \\(([A-Z0-9]{3}), ([A-Z0-9]{3})\\)" );
    private final String filename;

    private final List<Node> nodes = new ArrayList<>();
    private final Function<Node, Node>[] route;
    private final Node camelStartNode;

    public static void main( String[] args ) {
        try {
            Utils.executeSampleDay( new Day08Y23( "input/2023/Y23D08S1.DAT" ), 2L, null );
            Utils.executeSampleDay( new Day08Y23( "input/2023/Y23D08S2.DAT" ), 6L, null );
            Utils.executeSampleDay( new Day08Y23( "input/2023/Y23D08S3.DAT" ), null, 6L );
            Utils.executeDay( new Day08Y23( "input/2023/Y23D08I.DAT" ), 16271L, 14265111103729L );
        } catch ( Throwable e ) {
            e.printStackTrace();
        }
    }

    private Day08Y23( String filename ) {
        this.filename = filename;
        List<String> lines = Utils.readLines( filename );
        List<String> left = new ArrayList<>();
        List<String> right = new ArrayList<>();
        Map<String, Node> nodesMap = new TreeMap<>();
        route = lines.get( 0 )
                     .chars()
                     .mapToObj( dir -> {
                                    Function<Node, Node> result =
                                            switch ( dir ) {
                                                case 'R' -> Node::getRight;
                                                case 'L' -> Node::getLeft;
                                                default -> throw new IllegalArgumentException( "Bad direction " + dir );
                                            };
                                    return result;
                                }
                     )
                     .toArray( n -> (Function<Node, Node>[]) Array.newInstance( Function.class, n ) );
        lines.stream().skip( 2 ).forEach( line -> {
            Matcher matcher = NODE_PATTERN.matcher( line );
            if ( matcher.find() ) {
                String name = matcher.group( 1 );
                Node node = new Node( name );
                nodes.add( node );
                nodesMap.put( name, node );
                left.add( matcher.group( 2 ) );
                right.add( matcher.group( 3 ) );
            } else {
                throw new IllegalArgumentException( "Bad input: <%s>".formatted( line ) );
            }
        } );
        for ( int index = 0; index < nodes.size(); index++ ) {
            nodes.get( index ).setLeft( nodesMap.get( left.get( index ) ) );
            nodes.get( index ).setRight( nodesMap.get( right.get( index ) ) );
        }
        camelStartNode = nodesMap.get( "AAA" );
    }

    @Override
    public String sampleName() {
        return filename;
    }

    @Override
    public Long task1() throws Throwable {
        Node node = camelStartNode;
        int dirIndex = 0;
        long steps = 0;
        while ( !node.isCamelTarget() ) {
            steps++;
            node = route[dirIndex].apply( node );
            dirIndex = ( dirIndex + 1 ) % route.length;
        }
        return steps;
    }

    @Override
    public Long task2() throws Throwable {
        Node[] currentNodes = nodes.stream()
                                   .filter( Node::isCamelStart )
                                   .toArray( Node[]::new );
        long[] steps = new long[currentNodes.length];
        long[] deltas = new long[currentNodes.length];
        Map<Source, Target> nearestTarget = new TreeMap<>(
                Comparator.comparing( Source::startStep )
                          .thenComparing( src -> System.identityHashCode( src.node() ) )
        );
        for ( int index = 0; index < currentNodes.length; index++ ) {
            Target nextNode = getNextNode( 0, currentNodes[index] );
            currentNodes[index] = nextNode.node();
            deltas[index] = steps[index] = nextNode.steps();
        }
//        while ( !checkAllEqual( steps ) ) {
//            int index = findMinimumIndex( steps );
//            Target next = nearestTarget.computeIfAbsent(
//                    new Source( steps[index] % route.length, currentNodes[index] ),
//                    src -> getNextNode( src.startStep(), src.node() )
//            );
//            if( deltas[index] != next.steps() ) {
//                System.out.println( "!=" );
//            }
//            steps[index] += next.steps();
//            currentNodes[index] = next.node();
//        }
//        return steps[0];
        return Utils.mvp( steps ); // Ad-hoc solution
    }

    private boolean checkAllEqual( long[] steps ) {
        return Arrays.stream( steps )
                     .skip( 1 )
                     .allMatch( s -> s == steps[0] );
    }

    private int findMinimumIndex( long[] steps ) {
        long min = steps[0];
        int minIndex = 0;
        for ( int index = 1; index < steps.length; index++ ) {
            if ( steps[index] < min ) {
                min = steps[index];
                minIndex = index;
            }
        }
        return minIndex;
    }

    private Target getNextNode( long step, Node start ) {
        Node node = start;
        long nextStep = step;
        do {
            node = route[(int) ( nextStep % route.length )].apply( node );
            nextStep++;
        } while ( !node.isGhostTarget() );
        return new Target( nextStep - step, node );
    }

    private static class Node {
        private final String name;
        private Node left;
        private Node right;
        private final boolean ghostTarget;
        private final boolean camelTarget;

        private Node( String name ) {
            this.name = name;
            ghostTarget = name.charAt( 2 ) == 'Z';
            camelTarget = name.equals( "ZZZ" );
        }

        private Node getLeft() {
            return left;
        }


        private void setLeft( Node left ) {
            this.left = left;
        }

        private Node getRight() {
            return right;
        }

        private void setRight( Node right ) {
            this.right = right;
        }

        private boolean isGhostTarget() {
            return ghostTarget;
        }

        private boolean isCamelStart() {
            return name.charAt( 2 ) == 'A';
        }

        private boolean isCamelTarget() {
            return camelTarget;
        }
    }

    private record Source( long startStep, Day08Y23.Node node ) {
    }

    private record Target( long steps, Day08Y23.Node node ) {
    }
}
