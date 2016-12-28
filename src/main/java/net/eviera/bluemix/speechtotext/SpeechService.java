package net.eviera.bluemix.speechtotext;

import com.ibm.watson.developer_cloud.speech_to_text.v1.SpeechToText;
import com.ibm.watson.developer_cloud.speech_to_text.v1.model.RecognizeOptions;
import com.ibm.watson.developer_cloud.speech_to_text.v1.model.SpeechResults;
import com.ibm.watson.developer_cloud.speech_to_text.v1.model.Transcript;
import com.ibm.watson.developer_cloud.speech_to_text.v1.websocket.BaseRecognizeCallback;
import com.sun.istack.internal.NotNull;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class SpeechService {

    enum Lang {
        en("en-US_BroadbandModel"),
        es("es-ES_BroadbandModel");

        private String model;

        Lang(String model) {
            this.model = model;
        }

        public String getModel() {
            return model;
        }
    }

    enum Content {
        flac("audio/flac"),
        wav("audio/wav"),
        ogg("audio/ogg; codecs=vorbis");

        private String type;

        Content(String type) {
            this.type = type;
        }

        public String getType() {
            return type;
        }
    }


    public void translate(String user, String pass, Lang language, String inputFile, String outputFile) {
        try {
            SpeechToText service = new SpeechToText();
            service.setUsernameAndPassword(user, pass);

            Content contentType = parseContentTypeOnFileExtension(inputFile);
            final InputStream fis = new FileInputStream(inputFile);

            RecognizeOptions options = new RecognizeOptions.Builder()
                    .model(language.getModel())
                    .contentType(contentType.getType())
                    .continuous(true)
                    .interimResults(true)
                    .build();

            BaseRecognizeCallback callback = new BaseRecognizeCallback() {

                private String result = "";

                @Override
                public void onConnected() {
                    super.onConnected();
                    System.out.print("Writing to [" + outputFile + "] ");
                }

                @Override
                public void onTranscription(SpeechResults speechResults) {
                    System.out.print(".");
                    List<Transcript> results = speechResults.getResults();
                    result = results.get(0).getAlternatives().get(0).getTranscript();
                }

                @Override
                public void onDisconnected() {
                    try {
                        fis.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    try {
                        FileUtils.writeStringToFile(new File(outputFile), result, StandardCharsets.UTF_8);

                    } catch (IOException e) {
                        System.err.println("Error writing the output file [" + outputFile + "]");
                        e.printStackTrace();
                    }

                    System.out.println("\nFinished");

                    System.exit(0);
                }
            };

            service.recognizeUsingWebSocket(fis, options, callback);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private Content parseContentTypeOnFileExtension(@NotNull String filePath) {
        return Content.valueOf(FilenameUtils.getExtension(filePath));
    }
}
