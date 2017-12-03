import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Used to track exceptions encountered
 * during the execution of the program.
 *
 * @version 1.4
 */
public class ErrorWriter implements CommonConstants {

    private static BufferedWriter errorTracker;

    //opened at the begging of Main.main()
    public static void openFile () {

        try {
            errorTracker = new BufferedWriter ( new FileWriter ( "data\\errors-in-" + INPUT_FILE + ".txt" ) );
            errorTracker.write ( "Errors encountered when processing \"" + INPUT_FILE + ".csv\":\n\n" );
        }
        catch ( IOException e ) {
            System.out.println ("Failed to open error tracking file.");
            System.out.println ("OR failed to write first statement.");
            System.out.println ("Stack trace:");
            e.printStackTrace ();
        }

    }

    //whenever an exception is encountered
    public static void log ( String error ) {

        try {
            errorTracker.write ( error + "\n" );//add a new line for better readability
        }
        catch ( IOException e ) {
            System.out.println ("Failed to write error.");
            System.out.println ("Stack trace:");
            e.printStackTrace ();
        }

    }

    //closed in the finally block in Main.main()
    public static void closeFile () {

        log ( "(Single empty fields were not recorded.)" );
        try {
            errorTracker.close ();
        }
        catch ( IOException e ) {
            System.out.println ("Failed to close error tracking file.");
            System.out.println ("Stack trace:");
            e.printStackTrace ();
        }
    }

}
