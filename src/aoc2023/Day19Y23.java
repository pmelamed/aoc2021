// https://adventofcode.com/2023/day/18
package aoc2023;

import common.AocDay;
import common.Utils;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.ToIntFunction;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Day19Y23 implements AocDay<Long, Long> {

    private static final Pattern PART_PATTERN = Pattern.compile( "\\{x=([0-9]+),m=([0-9]+),a=([0-9]+),s=([0-9]+)}" );
    private static final Pattern WORKFLOW_PATTERN = Pattern.compile(
            "([a-z]+)\\{(.+)}"
    );
    private static final Pattern RULE_PATTERN = Pattern.compile(
            "([xmas])([<>])([0-9]+):(A|R|[a-z]+)"
    );
    private static final Pattern RESULT_PATTERN = Pattern.compile(
            "A|R|[a-z]+"
    );

    private final String filename;

    public static void main( String[] args ) {
        try {
            Utils.executeSampleDay( new Day19Y23( "input/2023/Y23D19S1.dat" ), 19114L, null );
            Utils.executeDay( new Day19Y23( "input/2023/Y23D19I.dat" ), 368964L, null );
        } catch ( Throwable e ) {
            e.printStackTrace();
        }
    }

    public Day19Y23( String file ) {
        this.filename = file;
    }

    @Override
    public String sampleName() {
        return filename;
    }

    public Long task1() {
        List<String> lines = Utils.readLines( filename );
        Iterator<String> iterator = lines.iterator();
        Map<String, Workflow> workflows = new TreeMap<>();
        String line;
        for ( line = iterator.next(); !line.isEmpty(); line = iterator.next() ) {
            parseWorkflow( line, workflows );
        }
        for ( Workflow workflow : workflows.values() ) {
            workflow.resolveWorkflows( workflows );
        }
        Workflow initial = workflows.get( "in" );
        long result = 0;
        while ( iterator.hasNext() ) {
            line = iterator.next();
            Part part = parsePart( line );
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
        return null;
    }

    private static void parseWorkflow( String line, Map<String, Workflow> workflows ) {
        // ([a-z]+)\{(.+)}
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
        workflows.put( matcher.group( 1 ), new Workflow( matcher.group( 1 ), rules ) );
    }

    private static Rule parseRule( String str ) {
        // ([xmas])([<>])([0-9]+):(A|R|[a-z]+)
        Matcher matcher = RULE_PATTERN.matcher( str );
        if ( !matcher.find() ) {
            throw new IllegalArgumentException( "Bad check: <%s>".formatted( str ) );
        }
        ToIntFunction<Part> field = switch ( matcher.group( 1 ) ) {
            case "x" -> Part::x;
            case "m" -> Part::m;
            case "a" -> Part::a;
            case "s" -> Part::s;
            default ->
                    throw new IllegalArgumentException( "Bad field reference: <%s>".formatted( matcher.group( 1 ) ) );
        };
        int value = Integer.parseInt( matcher.group( 3 ) );
        CheckResult result = parseResult( matcher.group( 4 ) );
        return switch ( matcher.group( 2 ) ) {
            case "<" -> ruleLess( result, value, field );
            case ">" -> ruleGreater( result, value, field );
            default -> throw new IllegalArgumentException( "Bad comparison: <%s>".formatted( matcher.group( 2 ) ) );
        };
    }

    private static CheckResult parseResult( String str ) {
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

    private static Rule ruleLess( CheckResult result, int value, ToIntFunction<Part> field ) {
        return new RuleCompare( result, field ) {
            @Override
            protected boolean compare( int partField ) {
                return partField < value;
            }

            @Override
            public String toString() {
                return "<%d:%s".formatted( value, result.toString() );
            }
        };
    }

    private static Rule ruleGreater( CheckResult result, int value, ToIntFunction<Part> field ) {
        return new RuleCompare( result, field ) {
            @Override
            protected boolean compare( int partField ) {
                return partField > value;
            }

            @Override
            public String toString() {
                return ">%d:%s".formatted( value, result.toString() );
            }
        };
    }

    private static Rule ruleDefault( CheckResult result ) {
        return new Rule( result ) {
            @Override
            boolean check( Part part ) {
                return true;
            }

            @Override
            public String toString() {
                return "*:%s".formatted( result.toString() );
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

    private static CheckResult resultContinue( String workflow ) {
        return new ContinueCheckResult( workflow );
    }

    private record Part( int x, int m, int a, int s ) {
        public long getSum() {
            return x + m + a + s;
        }
    }

    private static class Workflow {
        private final String name;
        private final Rule[] rules;

        private Workflow( String name, Rule[] rules ) {
            this.name = name;
            this.rules = rules;
        }

        private String getName() {
            return name;
        }

        protected void resolveWorkflows( Map<String, Workflow> workflows ) {
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

        @Override
        public String toString() {
            return isAccepted() ? "A" : ( isRejected() ? "R" : getResultWorkflow().getName() );
        }
    }

    private static class ContinueCheckResult extends CheckResult {
        private final String workflowName;
        private Workflow workflow;

        private ContinueCheckResult( String workflowName ) {
            this.workflowName = workflowName;
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
}
