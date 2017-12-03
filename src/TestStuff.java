import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class TestStuff {

    public static void main ( String[] args ) {

        BufferedWriter writer;

        try {
            writer = new BufferedWriter ( new FileWriter ( "test.html" ) );
            writer.write ( "<html>\n" +
                    "<head>\n" +
                    "  <meta name=\"viewport\" content=\"width=device-width, initial-scale=1\">\n" +
                    "  <link rel=\"stylesheet\" href=\"http://maxcdn.bootstrapcdn.com/bootstrap/3.3.6/css/bootstrap.min.css\">\n" +
                    "  <script src=\"https://ajax.googleapis.com/ajax/libs/jquery/1.12.0/jquery.min.js\"></script>\n" +
                    "  <script src=\"http://maxcdn.bootstrapcdn.com/bootstrap/3.3.6/js/bootstrap.min.js\"></script>\n" +
                    "</head>\n" +
                    "<body>\n" +
                    "\n" +
                    "<div class=\"container\">\n" +
                    "  <h2>Simple Collapsible</h2>\n" +
                    "  <button type=\"button\" class=\"btn btn-info\" data-toggle=\"collapse\" data-target=\"#demo\">Simple collapsible</button>\n" +
                    "  <div id=\"demo\" class=\"collapse\">\n" +
                    "    Lorem ipsum dolor sit amet, consectetur adipisicing elit,\n" +
                    "    sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam,\n" +
                    "    quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat.\n" +
                    "  </div>\n" +
                    "</div>\n" +
                    "\n" +
                    "</body>\n" +
                    "</html>" );
            writer.close ();
        }
        catch ( IOException e ) {
            System.out.println ( "<exception describtion>" );
            System.out.println ( "Stack trace: " );
            e.printStackTrace ();
        }

    }
}
