package com.vd.payments.UTIL.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class LocalDateSerializer extends StdSerializer<LocalDate>
{

    private static final long serialVersionUID = 1L;

    public LocalDateSerializer()
    {
        super(LocalDate.class);
    }

    @Override
    public void serialize(LocalDate value, JsonGenerator gen, SerializerProvider sp)
    {
        try
        {
            gen.writeString(value.format(DateTimeFormatter.ISO_LOCAL_DATE));
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}