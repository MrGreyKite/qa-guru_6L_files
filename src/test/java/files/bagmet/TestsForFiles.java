package files.bagmet;

import com.codeborne.pdftest.PDF;
import static com.codeborne.pdftest.assertj.Assertions.assertThat;
import com.opencsv.CSVReader;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.contentOf;
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

    @Test
    void txtFileTest() throws NullPointerException {
        File textFile = new File(getClass().getClassLoader().getResource("text.txt").getFile());
        assertThat(contentOf(textFile)).contains("Java automation");
        assertThat(contentOf(textFile)).endsWith(".");
    }

    @Test
    void downloadCVS() throws Exception {
        open("https://sample-videos.com/download-sample-csv.php");
        File downloadedCSV = $("a[data='1']").download();
        try (CSVReader reader = new CSVReader(new FileReader(downloadedCSV))) {
            List<String[]> strings = reader.readAll();
            assertThat(strings).contains(
                    new String[] {"1", "Eldon Base for stackable storage shelf, platinum",
                            "Muhammed MacIntyre","3",
                            "-213.25","38.94","35","Nunavut","Storage & Organization","0.8"},
                    new String[]{"10","Xerox 198","Dorothy Badders","678",
                            "-226.36","4.98","8.33","Nunavut","Paper","0.38"}
            );

        }
    }

    @Test
    void pdfFileTest() throws IOException {
        PDF pdf = new PDF(getClass().getClassLoader().getResource("Software Testing - Base Course.pdf"));
        assertThat(pdf.author).isEqualTo("Святослав Куликов");
        assertThat(pdf.title).contains("Тестирование программного обеспечения");
        assertThat(pdf).containsExactText("Виды и направления тестирования");
        assertThat(pdf.numberOfPages).isEqualTo(300);
    }
}
