package files.bagmet;

import com.codeborne.pdftest.PDF;
import static com.codeborne.pdftest.assertj.Assertions.assertThat;

import com.codeborne.xlstest.XLS;
import com.opencsv.CSVReader;
import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.net.URISyntaxException;
import java.util.List;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;
import static org.apache.commons.io.FilenameUtils.getBaseName;
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
    void pdfFileTest() throws Exception {
        try (InputStream stream = getClass().getClassLoader().getResourceAsStream("Software Testing - Base Course.pdf")) {
            PDF pdf = new PDF(stream);
            assertThat(pdf.author).isEqualTo("Святослав Куликов");
            assertThat(pdf.title).contains("Тестирование программного обеспечения");
            assertThat(pdf).containsExactText("Виды и направления тестирования");
            assertThat(pdf.numberOfPages).isEqualTo(300);
        }
    }

    @Test
    void ExcelFileTest() throws Exception {
            XLS xlsxFile = new XLS(getClass().getClassLoader().getResource("Report 2021-10-03.xlsx"));
            File xlsx = new File(getClass().getClassLoader().getResource("Report 2021-10-03.xlsx").getFile());

            String dateOfReport = "2021-10-03";

            assertThat(xlsxFile.excel.getSheetAt(0).getRow(0).getCell(0).getStringCellValue()).isEqualTo("Дата формирования отчета");
            assertThat(xlsxFile.excel.getSheetAt(0).getRow(1).getCell(0).getStringCellValue()).isEqualTo(dateOfReport);
            assertThat(getBaseName(xlsx.toString())).isEqualTo("Report%20" + dateOfReport);
    }

    @Test
    void ZipPasswordFileTest() throws IOException, URISyntaxException {
        String source = "./src/test/resources/textZipPassword.zip";
        String destination = "./src/test/resources/unzip";
        String password = "fireburn2";
        File destinationFolder = new File(destination);
        int numberOfExpectedEntries = 1;

        ZipFile zip = new ZipFile(source, password.toCharArray());
            assertThat(zip.isEncrypted()).isTrue();
            assertThat(zip.getFileHeaders().size()).as("Число файлов внутри архива")
                    .isEqualTo(numberOfExpectedEntries);

        zip.extractAll(destination); // ИЛИ destinationFolder.getPath()

        assertThat(destinationFolder.listFiles()).hasSize(numberOfExpectedEntries);

        File[] filesInDestinationFolder = destinationFolder.listFiles();

        for (File file : filesInDestinationFolder) {
            File sourceFile = new File(getClass().getClassLoader().getResource(file.getName()).toURI());
            assertThat(file.length()).isEqualTo(sourceFile.length());
        }

    }

}
