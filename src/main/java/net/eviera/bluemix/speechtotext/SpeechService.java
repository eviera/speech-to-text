package net.eviera.bluemix.speechtotext;

import com.ibm.watson.developer_cloud.speech_to_text.v1.SpeechToText;
import com.ibm.watson.developer_cloud.speech_to_text.v1.model.RecognizeOptions;
import com.ibm.watson.developer_cloud.speech_to_text.v1.model.SpeechResults;
import com.ibm.watson.developer_cloud.speech_to_text.v1.websocket.BaseRecognizeCallback;

import java.util.Properties;

public class SpeechService {

    enum Lang {
        en,
        es
    }

    public void translate(String user, String pass, Lang language, String filePath) {
        try {
            SpeechToText service = new SpeechToText();

            Properties properties = new Properties();
            properties.load(getClass().getClassLoader().getResourceAsStream("no-commitear-config.properties"));
            service.setUsernameAndPassword(properties.getProperty("username"), properties.getProperty("password"));

            RecognizeOptions options = new RecognizeOptions.Builder()
                    .model("en-US_BroadbandModel")
                    .contentType("audio/flac").continuous(true)
                    .interimResults(true).maxAlternatives(3)
                    .keywords(new String[]{"colorado", "tornado", "tornadoes"})
                    .keywordsThreshold(0.5).build();

            BaseRecognizeCallback callback = new BaseRecognizeCallback() {
                @Override
                public void onTranscription(SpeechResults speechResults) {
                    System.out.println(speechResults);
                }

                @Override
                public void onDisconnected() {
                    System.exit(0);
                }
            };


            service.recognizeUsingWebSocket(getClass().getClassLoader().getResourceAsStream("audio-file.flac"), options, callback);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
