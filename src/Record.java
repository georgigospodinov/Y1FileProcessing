/**
 * Used to read the data from a single record in the input file.
 * Reports any errors associated with it to the ErrorWriter class.
 *
 * @see ErrorWriter
 * @version 2.2
 */
public class Record implements CommonConstants {

    private String sampleId = NOTHING;
    private String sampleHolderNumber = NOTHING;
    private String spectrometerName = NOTHING;
    private String groupName = NOTHING;
    private String solventName = NOTHING;
    private String experimentName = NOTHING;

    private String sampleBlock = NOTHING;

    private String duration = NOTHING;
    private int durationValue = BAD_DATA;

    private String[] fields;
    private int inFileLine;

    private String getFieldAt ( int index ) {

        if ( index == NOT_IN_FILE )
            return NOT_DEFINED;

        else {

            String field = fields[ index ];

            if ( field.length () == 0 )
                return EMPTY_FIELD;//this way all Strings will have values

            else return field;

        }

    }

    //defines the sample block that is to be entered in the output file
    private void createSampleBlock () {

        sampleBlock += "<div class=\"container\">";
        sampleBlock += "<center>\n";

        //collapse button
        sampleBlock += "<button type=\"button\" class=\"btn btn-info\" data-toggle=\"collapse\"";
        sampleBlock += " data-target=\"#block" + getSampleId () + "\">";
        sampleBlock += "<b>[Sample " + getSampleId () + "]</b></button><br>\n";

        //collapse id
        sampleBlock += "<div id=\"block" + getSampleId () + "\"";
        sampleBlock += " class=\"collapse\">\n";

        sampleBlock += "Holder number: " + getSampleHolderNumber () + "<br>\n";
        sampleBlock += "Spectrometer: " + getSpectrometerName () + "<br>\n";
        sampleBlock += "Research group: " + getGroupName () + "<br>\n";
        sampleBlock += "Solvent: " + getSolventName () + "<br>\n";
        sampleBlock += "Experiment name: " + getExperimentName () + "<br>\n";
        sampleBlock += "</center></div></div><br>\n";

    }


    //deal with bad formats
    private void reportBadNumberFormat ( String field, String toCheck ) {

        if ( toCheck.equals ( NOT_DEFINED ) )//this error is already logged by FileHeader
            return;

        try {
            Integer.parseInt ( toCheck );
        }
        catch ( NumberFormatException e ) {

            String msg = "Line: " + inFileLine + ", field: " + field + "\n";

            if ( toCheck.equals ( EMPTY_FIELD ) )
                msg += "Field lacks data.\n";

            else msg += "Expected a number but found: \"" + toCheck + "\"\n";

            ErrorWriter.log ( msg );

        }

    }

    //common among duration error logging
    private String addFormat ( String msg ) {

        msg += "Bad duration format.\n";
        msg += "Expected format is hh:mm:ss. ";
        msg += "Found \"" + duration + "\"\n";

        return msg;

    }
    private void handleDuration () {

        //correct duration format is hh:mm:ss
        String msg = "Line: " + inFileLine + ", field: " + DURATION + "\n";

        if ( duration.equals ( EMPTY_FIELD ) ) {

            msg += "Field lacks data.\n";
            ErrorWriter.log ( msg );

        }

        else if ( duration.equals ( NOT_DEFINED ) )
            return;//this error is already reported by ErrorWriter

        else if ( duration.length () != 8 ||
                duration.charAt ( 2 ) != ':' || duration.charAt ( 5 ) != ':' ) {

            msg = addFormat ( msg );
            ErrorWriter.log ( msg );

        }

        else {

            String hh = duration.substring ( 0, 2 );
            String mm = duration.substring ( 3, 5 );
            String ss = duration.substring ( 6, 8 );

            int h = BAD_DATA, m = BAD_DATA, s = BAD_DATA;//minus ones to check after try-catch

            try {
                h = Integer.parseInt ( hh );
                m = Integer.parseInt ( mm );
                s = Integer.parseInt ( ss );
            }
            catch ( NumberFormatException e ) {

                msg = addFormat ( msg );
                ErrorWriter.log ( msg );

            }

            // if ( try was successful )
            if ( h != BAD_DATA && m != BAD_DATA && s != BAD_DATA &&
                    //AND time is acceptable
                h >= 0 && m >= 0 && m < 60 && s >= 0 && s < 60 )
                    durationValue = ( (h*60) + m )*60 + s;

        }

    }


    private void assignValues ( FileHeader fileHeader ) {

        sampleId = getFieldAt ( fileHeader.getSampleIdIndex () );
        reportBadNumberFormat ( SAMPLE_ID, sampleId );

        sampleHolderNumber = getFieldAt ( fileHeader.getSampleHolderNumberIndex () ) ;
        reportBadNumberFormat ( SAMPLE_HOLDER_NO, sampleHolderNumber );

        spectrometerName = getFieldAt ( fileHeader.getSpectrometerNameIndex () );
        groupName = getFieldAt ( fileHeader.getGroupNameIndex () );
        solventName = getFieldAt ( fileHeader.getSolventNameIndex () );
        experimentName = getFieldAt ( fileHeader.getExperimentNameIndex () );

        duration = getFieldAt ( fileHeader.getDurationIndex () );
        handleDuration ();

        createSampleBlock ();

    }

    public Record ( int inFileLine, String line, FileHeader fileHeader ) {

        fields = line.split ( "," );
        this.inFileLine = inFileLine;

        if ( fields.length > 0 ) //the record is not empty
            assignValues ( fileHeader );

        else {

            String msg = "Bad record data at line " + inFileLine + ".\n";
            msg += "Record is ignored.\n";
            ErrorWriter.log ( msg );

        }

    }

    //get different values
    public String getSampleId () {
        return sampleId;
    }
    public String getSampleHolderNumber () {
        return sampleHolderNumber;
    }
    public String getSpectrometerName () {
        return spectrometerName;
    }
    public String getGroupName () {
        return groupName;
    }
    public String getSolventName () {
        return solventName;
    }
    public String getExperimentName () {
        return experimentName;
    }
    public int getDurationValue () {
        return durationValue;
    }
    public String getSampleBlock () {
        return sampleBlock;
    }

}
