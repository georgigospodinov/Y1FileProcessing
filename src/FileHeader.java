/**
 * Used only on the first line of the input file.
 * Finds and saves the indexes of the required data.
 * Reports any errors associated with it to the ErrorWriter class.
 *
 * @see CommonConstants
 * @see ErrorWriter
 * @version 1.3
 */
public class FileHeader implements CommonConstants {

    private String[] data;
    private int numberOfFields;

    //indexes:
    private int sampleIdIndex;
    private int sampleHolderNumberIndex;
    private int spectrometerNameIndex;
    private int groupNameIndex;
    private int solventNameIndex;
    private int experimentNameIndex;
    private int durationIndex;
    private boolean lost = true;

    //checks to see if at least one column was found
    //otherwise it's probably bad format.
    private void reportLost() {

        if ( lost ) {

            String msg = "No columns have been found. ";
            msg += "No output file will be generated.\n";
            msg += "Please, make sure that the input file is correctly formatted.\n";
            ErrorWriter.log ( msg );

        }

    }

    //this constructor defines all values
    public FileHeader ( String line ) {

        this.data = line.split ( "," );
        numberOfFields = data.length;

        sampleIdIndex = find ( SAMPLE_ID );
        sampleHolderNumberIndex = find ( SAMPLE_HOLDER_NO );
        spectrometerNameIndex = find ( SPECTROMETER_NAME );
        groupNameIndex = find ( GROUP_NAME );
        solventNameIndex = find ( SOLVENT_NAME );
        experimentNameIndex = find ( EXP_NAME );
        durationIndex = find ( DURATION );

        reportLost();

    }

    //Finds the index of a field
    private int find ( String fieldToFind ) {

        int index = NOT_IN_FILE;

        for ( int i = 0; i < numberOfFields; i++ ) {

            String s = data[i];
            if ( s.equals ( fieldToFind ) ) {

                index = i;
                lost = false;
                break;

            }

        }

        if ( index == NOT_IN_FILE ) {

            String msg = "";

            if ( fieldToFind.equals ( DURATION ) ) {

                msg += "The \"duration\" column was not found.\n";
                msg += "No durations will be tracked.\n";

            }

            if ( !fieldToFind.equals ( DURATION ) ) {

                msg += "Column \"" + fieldToFind + "\" was not found in input file.\n";
                msg += "Output file will have \"" + NOT_DEFINED + "\" values.\n";

            }

            msg += "MAY LEAD TO UNINFORMATIVE \"formatted\" file!!!\n";
            msg += "MAY CAUSE FURTHER ERRORS TO BE LOGGED IN THIS FILE!(due to column mismatches)\n";
            ErrorWriter.log ( msg );

        }

        return index;

    }

    //get indexes
    public int getSampleIdIndex () {
        return sampleIdIndex;
    }
    public int getSampleHolderNumberIndex () {
        return sampleHolderNumberIndex;
    }
    public int getSpectrometerNameIndex () {
        return spectrometerNameIndex;
    }
    public int getGroupNameIndex () {
        return groupNameIndex;
    }
    public int getSolventNameIndex () {
        return solventNameIndex;
    }
    public int getExperimentNameIndex () {
        return experimentNameIndex;
    }
    public int getDurationIndex () {
        return durationIndex;
    }
    public boolean isLost () {
        return lost;
    }
}
