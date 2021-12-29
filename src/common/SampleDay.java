package common;

public class SampleDay implements AocDay<Integer, Integer> {

    private final String name;

    public static void main( String[] args ) {
        try {
            executeTasks( "input/2021/Y21D01S01.dat", null, null );
            executeTasks( "input/2021/Y21D01I.dat", null, null );
        } catch ( Throwable e ) {
            e.printStackTrace();
        }
    }

    public static void executeTasks( String fileName, Integer expected1, Integer expected2 ) {
        Utils.executeDay( new SampleDay( fileName ), expected1, expected2 );
    }

    public SampleDay( String file ) {
        this.name = file;
    }

    @Override
    public String sampleName() {
        return name;
    }

    public Integer task1() {
        return null;
    }

    public Integer task2() {
        return null;
    }
}
