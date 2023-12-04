package com.vd.payments.UTIL.serializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.time.LocalDate;

public class LocalDateDeserializer extends StdDeserializer<LocalDate>
{

    private static final long serialVersionUID = 1L;

    protected LocalDateDeserializer() {
        super(LocalDate.class);
    }


    @Override
    public LocalDate deserialize(JsonParser jp, DeserializationContext ctxt)
    {
        LocalDate rta = null;

        try
        {
            rta = LocalDate.parse(jp.readValueAs(String.class));
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return rta;
    }

}
