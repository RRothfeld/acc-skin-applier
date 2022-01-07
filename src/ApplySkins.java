import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class ApplySkins {
    static List<Integer> carModelTypes = IntStream.rangeClosed(0, 61).boxed().collect(Collectors.toList());
    static int carModelTypeException = 54; // carModelType == 54 does not exist
    static int auxLightException = 13; // does not have aux lights

    static String raceLabel = "\"raceNumber\": ";
    static String modelLabel = "\"carModelType\": ";
    static String endOfLineDelimiter = ",";

    public static void main(String[] args) throws IOException {
        String content = Files.readString(Path.of(args.length > 0 ? args[0] : "ApplySkins.json"), StandardCharsets.UTF_16LE);
        System.out.println(content);

        int start = content.indexOf(raceLabel) + raceLabel.length();
        int end = content.indexOf(endOfLineDelimiter, start);
        String raceNumber = content.substring(start, end);

        start = content.indexOf(modelLabel) + modelLabel.length();
        end = content.indexOf(endOfLineDelimiter, start);
        String modelNumber = content.substring(start, end);

        for (Integer carModel : carModelTypes) {
            if (carModel == carModelTypeException || carModel == Integer.parseInt(modelNumber))
                continue;

            // Replace logic: replace car model
            String curContent = content;
            curContent.replaceFirst(modelLabel + modelNumber, modelLabel + carModel);
            curContent.replace("\"skinTemplateKey\": .+,", "\"skinTemplateKey\": 100,");

            if (carModel == auxLightException)
                curContent.replace("\"auxLightKey\": .+,", "\"auxLightKey\": 0,");
            else
                curContent.replace("\"auxLightKey\": .+,", "\"auxLightKey\": 1,");

            String date = java.time.LocalDate.now().toString().replace("-","").substring(2);
            FileWriter writer = new FileWriter(raceNumber + "-" + date + "-" + String.format("%06d", carModel) + ".json");
            writer.write(curContent);
            writer.close();
        }
    }
}
