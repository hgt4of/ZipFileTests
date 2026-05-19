package models;

import com.codeborne.pdftest.PDF;
import com.opencsv.CSVReader;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static org.junit.jupiter.api.Assertions.*;

public class ZipReadTest {

    @ParameterizedTest
    @CsvSource(delimiter = '|', textBlock = """
            test1.pdf   | PDF   | !empty
            test2.xlsx  | Excel | Заголовок
            test3.csv   | CSV   | name,age
            """)
    void checkFilesInZip(String fileName, String fileType, String expectedContent) throws Exception {
        boolean fileFound = false;

        try (InputStream is = getClass().getClassLoader().getResourceAsStream("test_files.zip");
             ZipInputStream zis = new ZipInputStream(is)) {

            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                if (entry.getName().equals(fileName)) {
                    fileFound = true;
                    System.out.println("Проверяю файл: " + fileName);

                    switch (fileType) {
                        case "PDF":
                            PDF pdf = new PDF(zis);
                            assertFalse(pdf.text.isEmpty(), "PDF не должен быть пустым");
                            System.out.println("PDF проверен, длина текста: " + pdf.text.length());
                            break;

                        case "Excel":
                            XSSFWorkbook workbook = new XSSFWorkbook(zis);
                            String cellValue = workbook
                                    .getSheetAt(0)
                                    .getRow(0)
                                    .getCell(0)
                                    .getStringCellValue();
                            assertTrue(cellValue.equalsIgnoreCase(expectedContent),
                                    "Ожидалось: " + expectedContent + ", но получено: " + cellValue);
                            System.out.println("Excel проверен, значение: " + cellValue);
                            break;

                        case "CSV":
                            CSVReader reader = new CSVReader(new InputStreamReader(zis));
                            String[] firstRow = reader.readNext();
                            assertNotNull(firstRow);
                            assertEquals(expectedContent, String.join(",", firstRow),
                                    "Заголовок CSV не совпадает");
                            System.out.println("CSV проверен, заголовок: " + String.join(",", firstRow));
                            break;

                        default:
                            fail("Неизвестный тип файла: " + fileType);
                    }
                    break;
                }
            }
        }

        assertTrue(fileFound, "Файл " + fileName + " не найден в архиве!");
    }
}