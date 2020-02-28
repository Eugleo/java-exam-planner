package com.wybitul.examplanner;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class OptionParser {
    Map<String, ThrowingConsumer<String>> optConsumers = new HashMap<>();
    Map<String, ThrowingRunnable> flagActions = new HashMap<>();

    void addOption(String option, ThrowingConsumer<String> f) {
        optConsumers.put(option, f);
    }

    void addFlag(String flag, ThrowingRunnable f) {
        flagActions.put(flag, f);
        optConsumers.put(flag, s -> {
            switch (s.toLowerCase()) {
                case "true":
                    f.run();
                    break;
                case "false":
                    break;
            }
        });
    }

    private void parseFlag(String line) throws IncorrectConfigFileException {
        Pattern nameP = Pattern.compile("^-\\s*([^:\\s]+)\\s*$");
        Matcher nameM = nameP.matcher(line);

        if (!nameM.find()) { throw new IncorrectConfigFileException("Incorrectly specified flag"); }

        String name = nameM.group(1);

        if (!flagActions.containsKey(name)) { throw new IncorrectConfigFileException("Unknown flag"); }

        try {
            flagActions.get(name).run();
        } catch (Exception e) {
            throw new IncorrectConfigFileException(e.getMessage());
        }
    }

    private void parseOption(String line) throws IncorrectConfigFileException {
        Pattern nameP = Pattern.compile("^-\\s*([^:\\s]+):");
        Matcher nameM = nameP.matcher(line);

        if (!nameM.find()) { throw new IncorrectConfigFileException("Incorrectly specified option name"); }

        String name = nameM.group(1);
        Pattern valueP = Pattern.compile(":\\s*([^:]+)\\s*$");
        Matcher valueM = valueP.matcher(line);

        if (!valueM.find()) { throw new IncorrectConfigFileException("Incorrectly specified option value"); }

        String value = valueM.group(1);

        if (!optConsumers.containsKey(name)) { throw new IncorrectConfigFileException("Unknown option"); }

        try {
            optConsumers.get(name).accept(value);
        } catch (Exception e) {
            throw new IncorrectConfigFileException(e.getMessage());
        }
    }

    public void parse(String line) throws IncorrectConfigFileException {
        if (line.matches("^-.*:.*$")) {
            parseOption(line);
        } else if (line.matches("^-[^:]*$")) {
            parseFlag(line);
        } else {
            throw new IncorrectConfigFileException("Incorrectly specified option");
        }
    }
}
