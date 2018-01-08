package eu.fbk.dh.CAT_Tokenizer;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Properties;

/**
 * Created by giovannimoretti on 13/10/16.
 */
public class Main {
    public static void main(String[] args) {

        String folderPath = args[0];

        Path folder = Paths.get(folderPath);

        Properties props = new Properties();
        props.setProperty("ssplit.newlineIsSentenceBreak", "always");

        props.setProperty("annotators", "tokenize,ssplit");

        StanfordCoreNLP pipeline;
        pipeline = new StanfordCoreNLP(props);

        try {

            Files.walk(folder).parallel().forEach(path -> {
                try {
                    System.out.println(path.toString());
                    if (! path.toFile().isDirectory() && !path.startsWith(".")) {
                        String text = new String(Files.readAllBytes(path), StandardCharsets.UTF_8);


                        Annotation annotation = new Annotation(text);
                        pipeline.annotate(annotation);

                        StringBuffer tokenizedText = new StringBuffer();
                        for (CoreMap sentence : annotation.get(CoreAnnotations.SentencesAnnotation.class)) {
                            List<CoreLabel> tokens = sentence.get(CoreAnnotations.TokensAnnotation.class);
                            for (CoreLabel token : tokens) {
                                tokenizedText.append(token.originalText() + "\n");
                            }
                            tokenizedText.append("<eos>\n");

                        }
                        Path outputpath = Paths.get(path.getParent().toString(), path.getFileName() + ".tok");
                        Files.write(outputpath, tokenizedText.toString().getBytes("UTF-8"));
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
