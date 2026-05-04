import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class JsonParseTest {

    @Test
    void parseBookJsonTest() throws Exception{
        try (InputStream is = getClass().getClassLoader().getResourceAsStream
                ("books.json")) {
            ObjectMapper mapper = new ObjectMapper();

            Library library = mapper.readValue(is, Library.class);

            List<Book> books = library.getBooks();

            assertNotNull(books);
            assertEquals(3, books.size());

            Book first = books.get(0);
            assertEquals("Stalker", first.getTitle());
            assertEquals("Andrei Tarkovsky", first.getAuthor());
            assertEquals(1979, first.getYear());
            assertTrue(first.getTags().contains("detective"));

            Book second = books.get(1);
            assertEquals("John Wick", second.getTitle());
            assertEquals("Keanu Reeves", second.getAuthor());
            assertEquals(2014, second.getYear());
            assertTrue(second.getTags().contains("action"));

            Book third = books.get(2);
            assertEquals("Fight Club", third.getTitle());
            assertEquals("Robert Paulson", third.getAuthor());
            assertEquals(1999, third.getYear());
            assertTrue(third.getTags().contains("box"));
        }
    }
}
