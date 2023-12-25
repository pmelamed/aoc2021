// https://adventofcode.com/2023/day/19
package aoc2023;

import common.AocDay;
import common.Utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.ToIntFunction;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Day19Y23 implements AocDay<Long, Long> {

    private static final Pattern PART_PATTERN = Pattern.compile( "\\{x=([0-9]+),m=([0-9]+),a=([0-9]+),s=([0-9]+)}" );
    private static final Pattern WORKFLOW_PATTERN = Pattern.compile( "([a-z]+)\\{(.+)}" );
    private static final Pattern RULE_PATTERN = Pattern.compile( "([xmas])([<>])([0-9]+):(A|R|[a-z]+)" );
    private static final Pattern RESULT_PATTERN = Pattern.compile( "A|R|[a-z]+" );

    private final String filename;
    private final Map<String, Workflow> workflows = new TreeMap<>();
    private final List<Part> parts = new ArrayList<>();

    public static void main( String[] args ) {
        try {
            Utils.executeSampleDay( new Day19Y23( "input/2023/Y23D19S1.dat" ), 19114L, 167_409_079_868_000L );
            Utils.executeDay( new Day19Y23( "input/2023/Y23D19I.dat" ), 368964L, 127_675_188_176_682L );
        } catch ( Throwable e ) {
            e.printStackTrace();
        }
    }

    public Day19Y23( String file ) {
        this.filename = file;
        List<String> lines = Utils.readLines( file );
        Iterator<String> iterator = lines.iterator();
        String line;
        for ( line = iterator.next(); !line.isEmpty(); line = iterator.next() ) {
            parseWorkflow( line, workflows );
        }
        for ( Workflow workflow : workflows.values() ) {
            workflow.resolveWorkflows( workflows );
        }
        iterator.forEachRemaining( part -> parts.add( parsePart( part ) ) );
    }

    @Override
    public String sampleName() {
        return filename;
    }

    public Long task1() {
        Workflow initial = workflows.get( "in" );
        long result = 0L;
        for ( Part part : parts ) {
            CheckResult checkResult = initial.process( part );
            while ( !( checkResult.isAccepted() || checkResult.isRejected() ) ) {
                checkResult = checkResult.getResultWorkflow().process( part );
            }
            if ( checkResult.isAccepted() ) {
                result += part.getSum();
            }
        }
        return result;
    }

    public Long task2() {
        long volume = 0L;
        LinkedList<RangeResult> queue = new LinkedList<>();
        queue.add( new RangeResult( Range.fullRange(), resultContinue( "in" ) ) );
        while ( !queue.isEmpty() ) {
            RangeResult rangeResult = queue.removeFirst();
            Range range = rangeResult.range();
            CheckResult result = rangeResult.result();
            if ( result.isAccepted() ) {
                volume += range.getVolume();
            } else if ( !result.isRejected() ) {
                Workflow workflow = result.getResultWorkflow();
                for ( Rule rule : workflow.rules ) {
                    Range passRange = rule.getPassRange( range );
                    if ( passRange.isValid() ) {
                        queue.add( new RangeResult( passRange, rule.getResult() ) );
                    }
                    range = rule.getNoPassRange( range );
                }
            }
        }
        return volume;
    }

    private void parseWorkflow( String line, Map<String, Workflow> workflows ) {
        Matcher matcher = WORKFLOW_PATTERN.matcher( line );
        if ( !matcher.find() ) {
            throw new IllegalArgumentException( "Bad workflow: <%s>".formatted( line ) );
        }
        String[] ruleLines = matcher.group( 2 ).split( "," );
        Rule[] rules = new Rule[ruleLines.length];
        for ( int index = 0; index < ruleLines.length - 1; index++ ) {
            rules[index] = parseRule( ruleLines[index] );
        }
        rules[ruleLines.length - 1] = ruleDefault( parseResult( ruleLines[ruleLines.length - 1] ) );
        workflows.put( matcher.group( 1 ), new Workflow( rules ) );
    }

    private Rule parseRule( String str ) {
        Matcher matcher = RULE_PATTERN.matcher( str );
        if ( !matcher.find() ) {
            throw new IllegalArgumentException( "Bad check: <%s>".formatted( str ) );
        }
        String fieldName = matcher.group( 1 );
        ToIntFunction<Part> fieldGetter = Part.getter( fieldName );
        RangeSetter lowerSetter = Range.lowerSetter( fieldName );
        RangeSetter higherSetter = Range.higherSetter( fieldName );
        int value = Integer.parseInt( matcher.group( 3 ) );
        CheckResult result = parseResult( matcher.group( 4 ) );
        return switch ( matcher.group( 2 ) ) {
            case "<" -> ruleLess( result, value, fieldGetter, lowerSetter, higherSetter );
            case ">" -> ruleGreater( result, value, fieldGetter, lowerSetter, higherSetter );
            default -> throw new IllegalArgumentException( "Bad comparison: <%s>".formatted( matcher.group( 2 ) ) );
        };
    }

    private CheckResult parseResult( String str ) {
        Matcher matcher = RESULT_PATTERN.matcher( str );
        if ( !matcher.find() ) {
            throw new IllegalArgumentException( "Bad result: <%s>".formatted( str ) );
        }
        return switch ( matcher.group() ) {
            case "A" -> resultAccept();
            case "R" -> resultReject();
            default -> resultContinue( matcher.group() );
        };
    }

    private static Part parsePart( String line ) {
        Matcher matcher = PART_PATTERN.matcher( line );
        if ( !matcher.find() ) {
            throw new IllegalArgumentException( "Bad part: <%s>".formatted( line ) );
        }
        return new Part(
                Integer.parseInt( matcher.group( 1 ) ),
                Integer.parseInt( matcher.group( 2 ) ),
                Integer.parseInt( matcher.group( 3 ) ),
                Integer.parseInt( matcher.group( 4 ) )
        );
    }

    private static Rule ruleLess(
            CheckResult result,
            int value,
            ToIntFunction<Part> field,
            RangeSetter lowerSetter,
            RangeSetter higherSetter
    ) {
        return new RuleCompare( result, field ) {
            @Override
            protected boolean compare( int partField ) {
                return partField < value;
            }

            @Override
            protected Range getPassRange( Range src ) {
                return Range.updated( src, higherSetter, value - 1 );
            }

            @Override
            protected Range getNoPassRange( Range src ) {
                return Range.updated( src, lowerSetter, value );
            }
        };
    }

    private static Rule ruleGreater(
            CheckResult result,
            int value,
            ToIntFunction<Part> field,
            RangeSetter lowerSetter,
            RangeSetter higherSetter
    ) {
        return new RuleCompare( result, field ) {
            @Override
            protected boolean compare( int partField ) {
                return partField > value;
            }

            @Override
            protected Range getPassRange( Range src ) {
                return Range.updated( src, lowerSetter, value + 1 );
            }

            @Override
            protected Range getNoPassRange( Range src ) {
                return Range.updated( src, higherSetter, value );
            }
        };
    }

    private static Rule ruleDefault( CheckResult result ) {
        return new Rule( result ) {
            @Override
            boolean check( Part part ) {
                return true;
            }
        };
    }

    private static CheckResult resultAccept() {
        return new CheckResult() {
            @Override
            protected boolean isAccepted() {
                return true;
            }
        };
    }

    private static CheckResult resultReject() {
        return new CheckResult() {
            @Override
            protected boolean isRejected() {
                return true;
            }
        };
    }

    private CheckResult resultContinue( String workflow ) {
        return new ContinueCheckResult( workflow, workflows.get( workflow ) );
    }

    private record Part( int x, int m, int a, int s ) {
        private static ToIntFunction<Part> getter( String field ) {
            return switch ( field ) {
                case "x" -> Part::x;
                case "m" -> Part::m;
                case "a" -> Part::a;
                case "s" -> Part::s;
                default -> throw new IllegalArgumentException( "Bad field reference: <%s>".formatted( field ) );
            };
        }

        private long getSum() {
            return x + m + a + s;
        }
    }

    private interface RangeSetter {
        void set( Range range, int value );
    }

    private static class Range {
        int xl;
        int xh;
        int ml;
        int mh;
        int al;
        int ah;
        int sl;
        int sh;

        private Range( int xl, int xh, int ml, int mh, int al, int ah, int sl, int sh ) {
            this.xl = xl;
            this.xh = xh;
            this.ml = ml;
            this.mh = mh;
            this.al = al;
            this.ah = ah;
            this.sl = sl;
            this.sh = sh;
        }

        private Range( Range src ) {
            this.xl = src.xl;
            this.xh = src.xh;
            this.ml = src.ml;
            this.mh = src.mh;
            this.al = src.al;
            this.ah = src.ah;
            this.sl = src.sl;
            this.sh = src.sh;
        }

        private static Range fullRange() {
            return new Range( 1, 4000, 1, 4000, 1, 4000, 1, 4000 );
        }

        private static Range emptyRange() {
            return new Range( 1, -1, 1, -1, 1, -1, 1, -1 );
        }

        private static Range updated( Range src, RangeSetter setter, int value ) {
            Range range = new Range( src );
            setter.set( range, value );
            return range;
        }

        private static RangeSetter lowerSetter( String field ) {
            return switch ( field ) {
                case "x" -> Range::setXl;
                case "m" -> Range::setMl;
                case "a" -> Range::setAl;
                case "s" -> Range::setSl;
                default -> throw new IllegalArgumentException( "Bad field reference: <%s>".formatted( field ) );
            };
        }

        private static RangeSetter higherSetter( String field ) {
            return switch ( field ) {
                case "x" -> Range::setXh;
                case "m" -> Range::setMh;
                case "a" -> Range::setAh;
                case "s" -> Range::setSh;
                default -> throw new IllegalArgumentException( "Bad field reference: <%s>".formatted( field ) );
            };
        }

        public void setXl( int xl ) {
            this.xl = xl;
        }

        public void setXh( int xh ) {
            this.xh = xh;
        }

        public void setMl( int ml ) {
            this.ml = ml;
        }

        public void setMh( int mh ) {
            this.mh = mh;
        }

        public void setAl( int al ) {
            this.al = al;
        }

        public void setAh( int ah ) {
            this.ah = ah;
        }

        public void setSl( int sl ) {
            this.sl = sl;
        }

        public void setSh( int sh ) {
            this.sh = sh;
        }

        private boolean isValid() {
            return xh >= xl && mh >= ml && ah >= al && sh >= sl;
        }

        public long getVolume() {
            return (long) ( xh - xl + 1 ) * ( mh - ml + 1 ) * ( ah - al + 1 ) * ( sh - sl + 1 );
        }
    }

    private record Workflow( Rule[] rules ) {
        private void resolveWorkflows( Map<String, Workflow> workflows ) {
            Arrays.stream( rules )
                  .forEach( rule -> rule.resolveWorkflow( workflows ) );
        }

        private CheckResult process( Part part ) {
            for ( Rule rule : rules ) {
                if ( rule.check( part ) ) {
                    return rule.getResult();
                }
            }
            throw new IllegalStateException( "Couldn't reach here" );
        }
    }

    private static class Rule {
        private final CheckResult result;

        private Rule( CheckResult result ) {
            this.result = result;
        }

        boolean check( Part part ) {
            return false;
        }

        private CheckResult getResult() {
            return result;
        }

        protected void resolveWorkflow( Map<String, Workflow> workflows ) {
            result.resolveWorkflow( workflows );
        }

        protected Range getPassRange( Range src ) {
            return src;
        }

        protected Range getNoPassRange( Range src ) {
            return Range.emptyRange();
        }
    }

    private static class RuleCompare extends Rule {
        private final ToIntFunction<Part> field;

        public RuleCompare( CheckResult result, ToIntFunction<Part> field ) {
            super( result );
            this.field = field;
        }

        @Override
        protected boolean check( Part part ) {
            return compare( field.applyAsInt( part ) );
        }

        protected boolean compare( int partField ) {
            return false;
        }
    }

    private static class CheckResult {
        protected boolean isAccepted() {
            return false;
        }

        protected boolean isRejected() {
            return false;
        }

        protected Workflow getResultWorkflow() {
            return null;
        }

        protected void resolveWorkflow( Map<String, Workflow> workflows ) {
        }
    }

    private static class ContinueCheckResult extends CheckResult {
        private final String workflowName;
        private Workflow workflow;

        private ContinueCheckResult( String workflowName, Workflow workflow ) {
            this.workflowName = workflowName;
            this.workflow = workflow;
        }

        @Override
        protected Workflow getResultWorkflow() {
            return workflow;
        }

        @Override
        protected void resolveWorkflow( Map<String, Workflow> workflows ) {
            workflow = workflows.get( workflowName );
        }
    }

    private record RangeResult( Range range, CheckResult result ) {
    }
}
