/**
 * Constants that are used by more than one class.
 *
 * @version 3.0
 */
public interface CommonConstants {

    //these two constants are used when a column is missing
    int NOT_IN_FILE = -1;
    String NOT_DEFINED = "<not defined>";
    String EMPTY_FIELD = "<empty field>";
    String NOTHING = "";//for default values

    //used for durations
    int BAD_DATA = -10;

    //names of columns in input file
    String SAMPLE_ID = "sample_id";
    String SAMPLE_HOLDER_NO = "sample_holderno";
    String SPECTROMETER_NAME = "spectrometer_name";
    String GROUP_NAME = "group_name";
    String SOLVENT_NAME = "solvent_name";
    String EXP_NAME = "exp_name";
    String DURATION = "sample_duration";

    //Main.main () adds the ".csv" extension
    //different usages of this constant add different Strings to it
    String INPUT_FILE = "data-small";

}
