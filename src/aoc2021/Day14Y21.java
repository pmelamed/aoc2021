package aoc2021;

import common.AocDay;
import common.Utils;

import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

public class Day14Y21 implements AocDay<Long, Long> {

    public static final Comparator<Pair> PAIR_COMPARATOR =
            Comparator.comparing( Pair::getA ).thenComparing( Pair::getB );

    record Rule(char a, char b, char target) {
        Rule( String line ) {
            this( line.charAt( 0 ), line.charAt( 1 ), line.charAt( 6 ) );
        }

        Rule( char a, char b ) {
            this( a, b, (char) 0 );
        }
    }

    record Appearance(char ch, long count) {
    }

    class Pair {
        final char a;
        final char b;
        final Rule rule;
        final boolean last;

        public Pair( char a, char b, boolean last, Map<Rule, Rule> rules ) {
            this.a = a;
            this.b = b;
            this.last = last;
            this.rule = rules.get( new Rule( a, b ) );
        }

        public char getA() {
            return a;
        }

        public char getB() {
            return b;
        }

        public boolean isLast() {
            return last;
        }

        public Rule getRule() {
            return rule;
        }
    }

    private static final Comparator<Rule> RULE_COMPARATOR = Comparator.comparing( Rule::a ).thenComparing( Rule::b );

    private final String name;

    public static void main( String[] args ) {
        try {
            executeTasks( "input/2021/Y21D14S1.dat", 1588L, 2188189693529L );
            executeTasks( "input/2021/Y21D14I.dat", null, null );
        } catch ( Throwable e ) {
            e.printStackTrace();
        }
    }

    public static void executeTasks( String fileName, Long expected1, Long expected2 ) {
        Utils.executeDay(
                new Day14Y21( fileName ),
                expected1,
                expected2
        );
    }

    public Day14Y21( String file ) {
        this.name = file;
    }

    @Override
    public String sampleName() {
        return name;
    }

    public Long task1() {
        List<String> input = Utils.readLines( name );
        LinkedList<Character> formula = parseFormula( input.get( 0 ) );
        TreeMap<Rule, Rule> rules = input.stream()
                                         .skip( 2 )
                                         .map( Rule::new )
                                         .collect( Collectors.toMap(
                                                 r -> r,
                                                 r -> r,
                                                 ( m1, m2 ) -> m1,
                                                 () -> new TreeMap<>( RULE_COMPARATOR )
                                         ) );
        for ( int steps = 0; steps < 10; ++steps ) {
            ListIterator<Character> iter = formula.listIterator();
            char prev = iter.next();
            while ( iter.hasNext() ) {
                char ch = iter.next();
                Rule rule = rules.get( new Rule( prev, ch ) );
                if ( rule != null ) {
                    iter.previous();
                    iter.add( rule.target );
                    iter.next();
                }
                prev = ch;
            }
        }
        Appearance[] appearances = formula.stream()
                                          .collect( Collectors.groupingBy( ch -> ch ) )
                                          .entrySet()
                                          .stream()
                                          .map( e -> new Appearance( e.getKey(), e.getValue().size() ) )
                                          .toArray( Appearance[]::new );
        Arrays.sort( appearances, Comparator.comparing( Appearance::count ) );
        return appearances[appearances.length - 1].count - appearances[0].count;
    }


    public Long task2() {
        List<String> input = Utils.readLines( name );
        LinkedList<Character> formula = parseFormula( input.get( 0 ) );
        TreeMap<Rule, Rule> rules = input.stream()
                                         .skip( 2 )
                                         .map( Rule::new )
                                         .collect( Collectors.toMap(
                                                 r -> r,
                                                 r -> r,
                                                 ( m1, m2 ) -> m1,
                                                 () -> new TreeMap<>( RULE_COMPARATOR )
                                         ) );
        TreeMap<Pair, Long> pairs = new TreeMap<>( PAIR_COMPARATOR );
        ListIterator<Character> iter = formula.listIterator();
        char prev = iter.next();
        while ( iter.hasNext() ) {
            char ch = iter.next();
            Pair pair = new Pair( prev, ch, !iter.hasNext(), rules );
            Long count2 = pairs.remove( pair );
            pairs.put( pair, count2 == null ? 1L : count2 + 1L );
            prev = ch;
        }
        for ( int steps = 0; steps < 40; ++steps ) {
            TreeMap<Pair, Long> newPairs = new TreeMap<>( PAIR_COMPARATOR );
            for ( Map.Entry<Pair, Long> entry : pairs.entrySet() ) {
                Pair pair = entry.getKey();
                Rule rule = pair.getRule();
                if ( rule != null ) {
                    Pair pair1 = new Pair( rule.a(), rule.target(), false, rules );
                    Pair pair2 = new Pair( rule.target(), rule.b(), pair.isLast(), rules );
                    newPairs.putIfAbsent( pair1, 0L );
                    newPairs.computeIfPresent( pair1, ( p, c ) -> c + entry.getValue() );
                    Long count2 = newPairs.remove( pair2 );
                    newPairs.put( pair2, entry.getValue() + ( count2 == null ? 0 : count2 ) );
                } else {
                    newPairs.putIfAbsent( pair, 0L );
                    newPairs.computeIfPresent( pair, ( p, c ) -> c + entry.getValue() );
                }
            }
            pairs = newPairs;
        }
        TreeMap<Character, Long> chars = new TreeMap<>();
        for ( var entry : pairs.entrySet() ) {
            Pair pair = entry.getKey();
            chars.putIfAbsent( pair.getA(), 0L );
            chars.computeIfPresent( pair.getA(), ( ch, count ) -> count + entry.getValue() );
            if ( pair.isLast() ) {
                chars.putIfAbsent( pair.getB(), 0L );
                chars.computeIfPresent( pair.getB(), ( ch, count ) -> count + 1L );
            }
        }
        Appearance[] appearances = chars.entrySet()
                                        .stream()
                                        .map( e -> new Appearance( e.getKey(), e.getValue() ) )
                                        .toArray( Appearance[]::new );
        Arrays.sort( appearances, Comparator.comparing( Appearance::count ) );
        return ( appearances[appearances.length - 1].count - appearances[0].count );
    }

    private LinkedList<Character> parseFormula( String formulaStr ) {
        char[] chars = formulaStr.toCharArray();
        LinkedList<Character> result = new LinkedList<>();
        for ( char ch : chars ) {
            result.add( ch );
        }
        return result;
    }
}
