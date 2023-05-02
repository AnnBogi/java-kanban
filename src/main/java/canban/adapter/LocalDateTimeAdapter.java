package canban.adapter;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class LocalDateTimeAdapter extends TypeAdapter<LocalDateTime> {

    @Override
    public void write(final JsonWriter jsonWriter, final LocalDateTime localDate) throws IOException {
        var value = Objects.nonNull(localDate) ? localDate : LocalDateTime.now();
        jsonWriter.value(value.format(DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss.SSS")));
    }

    @Override
    public LocalDateTime read(final JsonReader jsonReader) throws IOException {
        return LocalDateTime.parse(jsonReader.nextString(),DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss.SSS"));
    }

}
