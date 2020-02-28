package com.wybitul.examplanner;

import java.io.*;
import java.time.LocalDate;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ConfigWriter {
    private static PrintStream out;
    private static int defaultYear;

    private ConfigWriter() { }

    public static boolean write(Config config, String path) {
        try (PrintStream ps = new PrintStream(new FileOutputStream(path))) {
            out = ps;

            // Get the most common year
            defaultYear = config.classOptions.stream()
                    .flatMap(opt -> opt.examDates.stream())
                    .map(d -> d.getYear())
                    .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()))
                    .entrySet()
                    .stream()
                    .max(Comparator.comparing(Map.Entry::getValue))
                    .map(e -> e.getKey())
                    .orElse(-1);

            comment("Tento konfigurační soubor byl vytvořen " +
                    Utils.formatDate(LocalDate.now(), -1, ""));
            newline();

            if (defaultYear != -1) {
                comment("Pokud u dat v tomto souboru není uveden rok, jedná se o rok " + defaultYear);
                writeOption("rok", defaultYear);
            }
            newline();
            comment("Datum začátku učení");
            writeOption("začátek", Utils.formatDate(config.beginning, defaultYear, "nezadáno"));
            newline();
            writeWeightsConfig(config.weightsConfig);
            out.println("+++");
            newline();
            comment("Globální parametry");
            comment("Platí pro každý předmět, u kterého nejsou přepsány");
            comment("Pro jejich vysvětlení viz README");
            writeClassOptions(config.userDefaultOpts, Config.defaultClassOptions, false);
            comment("Parametry specifické pro předměty");
            config.classOptions.stream()
                    .sorted(Comparator.comparing(opt -> opt.classInfo.name))
                    .forEach(opt -> {
                        writeClassInfo(opt.classInfo);
                        writeClassOptions(opt, config.userDefaultOpts, true);
                    });
            return true;
        } catch (FileNotFoundException e) {
            System.out.println("Can't open file " + path);
            return false;
        }
    }

    private static void writeWeightsConfig(WeightsConfig cfg) {
        comment("Nastavení parametrů důležitosti");
        comment("důležitost předmětu = v * váha + k * kredity + s * st(status)");
        writeOption("v", cfg.w);
        writeOption("k", cfg.c);
        writeOption("s", cfg.s);
        newline();
        comment("St je funkce přiřazující číslo každému ze statusů (P/PVP/V)");
        comment("zadání: st(P), st(PVP), st(V)");
        Stream<String> str = Arrays.stream(new int[] {cfg.st.p, cfg.st.pvp, cfg.st.v}).mapToObj(String::valueOf);
        writeOption("st", String.join(", ", str.collect(Collectors.toList())));
        newline();
    }

    private static void writeClassInfo(ClassInfo info) {
        out.printf("= %s (%s)\n", info.name, info.id.str);
    }

    private static void writeClassOptions(ClassOptions opts1, ClassOptions opts2, boolean writeStatus) {
        List<String> exams1 = opts1.examDates.stream()
                .sorted(Comparator.naturalOrder())
                .map(d -> Utils.formatDate(d, defaultYear, "x"))
                .collect(Collectors.toList());

        List<String> exams2 = opts2.examDates.stream()
                .sorted(Comparator.naturalOrder())
                .map(d -> Utils.formatDate(d, defaultYear, "x"))
                .collect(Collectors.toList());

        Map<String, Object> optionMap1 = getOptionMap(exams1, opts1);
        Map<String, Object> optionMap2 = getOptionMap(exams2, opts2);

        optionMap1.forEach((opt, val) -> {
            boolean condition = writeStatus ? opt.equals("status") || opt.equals("příprava") : false;
            if (condition || !optionMap2.get(opt).equals(val)) {
                writeOption(opt, val);
            }
        });

        if (opts1.classInfo != null && opts1.classInfo.type == Type.COLLOQUIUM) {
            writeFlag("zápočet");
        }
        if (opts1.ignore) { writeFlag("ignorovat"); }
        newline();
    }

    private static Map<String, Object> getOptionMap(List<String> exams, ClassOptions opts) {
        WordFormatter days = new WordFormatter("dní", "den", "dny");
        Map<String, Object> options = Map.of(
                "kredity", opts.credits,
                "status", opts.status.name(),
                "váha", opts.weight,
                "termíny", opts.backupTries,
                "příprava", days.format(opts.idealPrepTime),
                "minimum", days.format(opts.minPrepTime),
                "datum", String.format("%s - %s",
                        Utils.formatDate(opts.lowBound, defaultYear, "x"),
                        Utils.formatDate(opts.highBound, defaultYear, "x")),
                "zkoušky", String.join(", ", exams)
        );
        return options;
    }

    private static void writeOption(String name, Object value) {
        out.printf("- %s: %s\n", name, value.toString());
    }

    private static void writeFlag(String name) {
        out.printf("- %s\n", name);
    }

    private static void comment(String str) {
        out.println("; " + str);
    }

    private static void newline() {
        out.println();
    }
}
