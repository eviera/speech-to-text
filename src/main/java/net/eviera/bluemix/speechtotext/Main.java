package net.eviera.bluemix.speechtotext;

import org.apache.commons.cli.*;

public class Main {

    public static void main(String[] args) {
        Options options = new Options();

        Option userOption = new Option("u", "user", true, "Username for Bluemix");
        userOption.setRequired(true);
        options.addOption(userOption);

        Option passOption = new Option("p", "pass", true, "Password for Bluemix");
        passOption.setRequired(true);
        options.addOption(passOption);

        Option languageOption = new Option("l", "language", true, "Language: 'es' or 'en'");
        languageOption.setRequired(true);
        options.addOption(languageOption);

        Option filePathOption = new Option("f", "file", true, "Audio input file path");
        filePathOption.setRequired(true);
        options.addOption(filePathOption);

        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();
        CommandLine cmd;

        try {
            cmd = parser.parse(options, args);
        } catch (ParseException e) {
            System.out.println(e.getMessage());
            formatter.printHelp("speech-to-text", options);
            System.exit(1);
            return;
        }

        String user = cmd.getOptionValue("u");
        String pass = cmd.getOptionValue("p");
        SpeechService.Lang language = SpeechService.Lang.valueOf(cmd.getOptionValue("l"));
        String filePath = cmd.getOptionValue("f");

        SpeechService service = new SpeechService();
        service.translate(user, pass, language, filePath);
    }

}
