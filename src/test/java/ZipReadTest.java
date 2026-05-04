import com.codeborne.pdftest.PDF;
import com.opencsv.CSVReader;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static org.junit.jupiter.api.Assertions.*;

public class ZipReadTest {

    @Test
    void checkFilesInZip() throws Exception {

       try (InputStream is = getClass().getClassLoader().getResourceAsStream("test_files.zip");
       ZipInputStream zis = new ZipInputStream(is)) {

            ZipEntry entry;
            while ((entry= zis.getNextEntry()) != null) {
                String fileName = entry.getName();
                System.out.println("Проверяю файл: " + fileName);

               if (fileName.endsWith(".pdf")) {
                   PDF pdf = new PDF(zis);
                   assertFalse(pdf.text.isEmpty(), "PDF не должен быть пустым");
                   System.out.println("PDF проверен, текст найден. Длина текста: " + pdf.text.length());
               }

               else if (fileName.endsWith(".xlsx")) {
                   XSSFWorkbook workbook = new XSSFWorkbook(zis);
                   var cell = workbook.getSheetAt(0).getRow(0).getCell(0);
                   String cellValue = cell.getStringCellValue();
                   assertTrue(cellValue.equalsIgnoreCase("заголовок"),
                           "Ожидалось 'заголовок', но получено: " + cellValue);
               }

               else if (fileName.endsWith(".csv")) {
                   CSVReader reader = new CSVReader(new InputStreamReader(zis));
                   String[] firstRow = reader.readNext();
                   assertNotNull(firstRow, "CSV не должен быть пустым");
                   assertArrayEquals(new String[]{"name", "age"}, firstRow,
                           "Заголовок CSV должен быть 'name,age'");
                   System.out.println("CSV проверерн, заголовок " + String.join(",", firstRow));
               }
            }
        }
    }
}