package net.eviera.bluemix.speechtotext;

import com.ibm.watson.developer_cloud.speech_to_text.v1.SpeechToText;
import com.ibm.watson.developer_cloud.speech_to_text.v1.model.RecognizeOptions;
import com.ibm.watson.developer_cloud.speech_to_text.v1.model.SpeechResults;
import com.ibm.watson.developer_cloud.speech_to_text.v1.websocket.BaseRecognizeCallback;
import com.sun.istack.internal.NotNull;
import org.apache.commons.io.FilenameUtils;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

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


    public void translate(String user, String pass, Lang language, String filePath) {
        try {
            SpeechToText service = new SpeechToText();
            service.setUsernameAndPassword(user, pass);

            Content contentType = parseContentTypeOnFileExtension(filePath);
            final InputStream fis = new FileInputStream(filePath);

            RecognizeOptions options = new RecognizeOptions.Builder()
                    .model(language.getModel())
                    .contentType(contentType.getType())
                    .continuous(true)
                    .interimResults(true)
                    .maxAlternatives(3)
                    .keywords(new String[]{"colorado", "tornado", "tornadoes"})
                    .keywordsThreshold(0.5)
                    .build();

            BaseRecognizeCallback callback = new BaseRecognizeCallback() {
                @Override
                public void onTranscription(SpeechResults speechResults) {
                    System.out.println(speechResults);
                }

                @Override
                public void onDisconnected() {
                    try {
                        fis.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
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
