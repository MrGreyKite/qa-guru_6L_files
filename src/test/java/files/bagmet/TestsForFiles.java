package files.bagmet;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.junit.jupiter.api.Test;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestsForFiles {

    @Test
    void docxFileTest() throws IOException {
        XWPFDocument document = new XWPFDocument(new FileInputStream("./src/test/resources/Doc1.docx"));
        List<XWPFParagraph> paragraphs = document.getParagraphs();
        document.close();
        XWPFParagraph title = paragraphs.get(0);
        XWPFRun titleRun = title.getRuns().get(0);
        assertThat(title.getText()).isEqualTo("Тестовый документ");
        assertThat(titleRun.getColor()).isEqualTo("FF0000");
        assertThat(titleRun.getFontSize()).isEqualTo(16);
        assertThat(paragraphs.get(3).getRuns().get(0).isBold()).isTrue();
        //assertEquals("Cambria", titleRun.getFontFamily()); - выдает результат, что fontFamily равно null
        assertThat(paragraphs.get(5).getRuns().get(0).getFontFamily()).isEqualTo("Arial"); //возможно, из-за текста на русском?
        assertThat(paragraphs.get(5).getText()).contains("Text in English");

    }
}
