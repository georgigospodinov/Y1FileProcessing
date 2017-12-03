import java.io.*;
import java.util.PriorityQueue;

public class Main implements CommonConstants {

    //with these constants it is easy to find information
    //about other spectrometers and experiments
    private static final String TRACED_SPECTROMETER = "Alec";
    private static final String TRACED_EXPERIMENT = "proton.a.and";

    //(hh*60+mm )*60 +ss
    private static final int START_SHORT_DURATION = ((99 * 60) + 99) * 60 + 99;//outside of upper data range
    private static final int START_LONG_DURATION = -1;//outside of lower data range
    private static final String NOT_DEFINED_ID = "<not defined id>";

    //this method is always called with a valid argument (valid range = 0 - 1)
    private static double convertToPercentage(double numberToTransform) {

        numberToTransform *= 100;//to percentage

        int num = (int) (numberToTransform * 1000);//for rounding
        if (num % 10 >= 5) //if the last digit is 5 or more
            num += 10;//round up
        num /= 10;//remove last digit

        numberToTransform = num / 100.0;

        return numberToTransform;

    }

    private static String convertToTime(int duration) {

        String d = "";

        int ss = duration % 60;
        duration /= 60;
        int mm = duration % 60;
        duration /= 60;
        int hh = duration % 60;

        if (hh < 10)
            d += "0";
        d += hh + ":";

        if (mm < 10)
            d += "0";
        d += mm + ":";

        if (ss < 10)
            d += "0";
        d += ss;

        return d;

    }

    //adds the stack trace of an exception to a String
    private static String stackTraceToString(String msg, Exception e) {

        msg += "Stack trace:\n";

        //generates a String from the StackTrace
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);

        msg += sw.toString();

        return msg;

    }

    //counts the different elements in an ordered queue and writes them into a String
    //returned String is used when writing those in the output file
    private static String priorityQueueToString(PriorityQueue<String> pq) {

        String result = "";

        while (!pq.isEmpty()) {

            String currentToCount = pq.peek();
            int count = 1;
            pq.remove();
            int s = pq.size();

            for (int i = 0; i < s; i++) {

                String w = pq.peek();
                if (w.equals(currentToCount)) {
                    pq.remove();
                    count++;
                }

            }

            result += currentToCount + " - " + count + "<br>";
            result += "\n";

        }

        return result;

    }

    public static void main(String[] args) {

        BufferedReader reader;
        BufferedWriter writer;
        ErrorWriter.openFile();

        //big try block because the try is for opening
        //both input and output file
        try {

            //define files and header
            reader = new BufferedReader(new FileReader("data\\" + INPUT_FILE + ".csv"));
            String line = reader.readLine();// first/header line
            FileHeader header = new FileHeader(line);
            if (header.isLost()) return;//no data to process

            writer = new BufferedWriter(new FileWriter("data\\formatted-" + INPUT_FILE + ".html"));
            writer.write("<html>\n");
            writer.write("<header>\n");
            //for collapse functionality
            writer.write("<meta name=\"viewport\" content=\"width=device-width, initial-scale=1\">\n" +
                    "<link rel=\"stylesheet\" href=\"http://maxcdn.bootstrapcdn.com/bootstrap/3.3.6/css/bootstrap.min.css\">\n" +
                    "<script src=\"https://ajax.googleapis.com/ajax/libs/jquery/1.12.0/jquery.min.js\"></script>\n" +
                    "<script src=\"http://maxcdn.bootstrapcdn.com/bootstrap/3.3.6/js/bootstrap.min.js\"></script>\n");
            writer.write("<font size=\"7\" color=\"blue\"><center><b>");
            writer.write("Formatted-" + INPUT_FILE);
            writer.write("</b></center></font>\n</header>\n");
            writer.write("<body>\n");

            //duration trackers
            int shortestDuration = START_SHORT_DURATION;
            String shortestDurationID = NOT_DEFINED_ID;
            int longestDuration = START_LONG_DURATION;
            String longestDurationID = NOT_DEFINED_ID;
            int totalDuration = 0;

            //counters
            int totalNumberOfSamples = 0;
            int numberOfSamplesOnTracedExperiment = 0;
            int numberOfSamplesOnTracedSpectrometer = 0;
            int numberOfSamplesByTracedGroup = 0;
            int lineCount = 2;//used for error logging

            //counting for 2nd extension (statistics)
            PriorityQueue<String> researchGroups = new PriorityQueue<>();
            PriorityQueue<String> spectrometers = new PriorityQueue<>();
            PriorityQueue<String> solvents = new PriorityQueue<>();

            //do operations on each line of data
            line = reader.readLine();//reads first record
            while (line != null) {

                //count a sample
                totalNumberOfSamples++;
                Record record = new Record(lineCount, line, header);
                String sampleBlock = record.getSampleBlock();
                writer.write(sampleBlock);

                //count usages of spectrometer
                if (record.getSpectrometerName().equals(TRACED_SPECTROMETER))
                    numberOfSamplesOnTracedSpectrometer++;

                //count usages of experiment
                if (record.getExperimentName().equals(TRACED_EXPERIMENT))
                    numberOfSamplesOnTracedExperiment++;

                //check for new duration records
                int currentDuration = record.getDurationValue();
                if (currentDuration != BAD_DATA) {

                    totalDuration += currentDuration;

                    if (currentDuration < shortestDuration) {

                        shortestDuration = currentDuration;
                        shortestDurationID = record.getSampleId();

                    }
                    if (currentDuration > longestDuration) {

                        longestDuration = currentDuration;
                        longestDurationID = record.getSampleId();

                    }

                }

                //for 2nd extension (statistics)
                String gn = record.getGroupName();
                if (!gn.equals(NOT_DEFINED) && !gn.equals(EMPTY_FIELD) && !gn.equals(NOTHING))
                    researchGroups.add(gn);

                String spectro = record.getSpectrometerName();
                if (!spectro.equals(NOT_DEFINED) && !spectro.equals(EMPTY_FIELD) && !spectro.equals(NOTHING))
                    spectrometers.add(spectro);

                String solv = record.getSolventName();
                if (!solv.equals(NOT_DEFINED) && !solv.equals(EMPTY_FIELD) && !solv.equals(NOTHING))
                    solvents.add(solv);

                lineCount++;
                line = reader.readLine();

            }

            //durations
            if (header.getDurationIndex() != NOT_IN_FILE) {

                writer.write("<p>\n");
                writer.write("Shortest recorded duration is: ");
                if (shortestDurationID.equals(NOT_DEFINED_ID))//no record
                    writer.write(" not found.");
                else writer.write(convertToTime(shortestDuration) +
                        " [Sample " + shortestDurationID + "]");
                writer.write("<br>\n");

                writer.write("Longest recorded duration is: ");
                if (shortestDurationID.equals(NOT_DEFINED_ID))//no record
                    writer.write(" not found.");
                else writer.write(convertToTime(longestDuration) +
                        " [Sample " + longestDurationID + "]");
                writer.write("<br>\n");

                writer.write("Total duration for all samples is: ");
                writer.write(convertToTime(totalDuration) + "\n</p>\n");

            }
            else {
                writer.write("No durations were found.<br>\n");
            }

            double percentage;
            //traced spectrometer
            percentage = ((double) numberOfSamplesOnTracedSpectrometer) / ((double) totalNumberOfSamples);
            percentage = convertToPercentage(percentage);
            writer.write("<p>\nNumber of samples ran on spectrometer \"" + TRACED_SPECTROMETER);
            writer.write("\": " + numberOfSamplesOnTracedSpectrometer + " [" + percentage + "%]\n</p>\n");

            //traced experiment
            percentage = ((double) numberOfSamplesOnTracedExperiment) / ((double) totalNumberOfSamples);
            percentage = convertToPercentage(percentage);
            writer.write("<p>\nPercentage of samples, using experiment \"" + TRACED_EXPERIMENT);
            writer.write("\": " + percentage + "% [total: " + numberOfSamplesOnTracedExperiment + "]\n</p>\n");

            //2nd extension
            writer.write("<div class=\"container\">");
            writer.write("<button type=\"button\" class=\"btn btn-info\" data-toggle=\"collapse\"");
            writer.write(" data-target=\"#more\">More Statistics</button>\n");

            writer.write("<div id=\"more\" class=\"collapse\">\n");
            String samplesPerResearchGroup = "Number of samples per research group:<br>\n";
            samplesPerResearchGroup += priorityQueueToString(researchGroups) + "<br>\n";
            writer.write(samplesPerResearchGroup);

            String samplesPerSpectrometer = "Number of samples per spectrometer:<br>\n";
            samplesPerSpectrometer += priorityQueueToString(spectrometers) + "<br>\n";
            writer.write(samplesPerSpectrometer);

            String samplesPerSolvent = "Number of samples per solvent:<br>\n";
            samplesPerSolvent += priorityQueueToString(solvents) + "<br>\n";
            writer.write(samplesPerSolvent);
            writer.write("</div></div>");

            writer.write("\n</body>\n</html>");
            writer.close();
            reader.close();

        }
        catch (FileNotFoundException e) {

            e.printStackTrace();
            System.out.println("Input file not found! Please, check the file path.");

            String msg = "Input file not found!\n";
            msg = stackTraceToString(msg, e);

            ErrorWriter.log(msg);

        }
        catch (IOException e) {

            System.out.println("Failed to read/write");
            String msg = "Failed to read/write\n";
            msg = stackTraceToString(msg, e);

            ErrorWriter.log(msg);

        }
        finally {
            ErrorWriter.closeFile();
        }

    }

}
