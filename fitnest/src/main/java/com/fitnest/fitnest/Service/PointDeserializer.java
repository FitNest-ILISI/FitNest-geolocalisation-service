package com.fitnest.fitnest.Service;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;

import java.io.IOException;

public class PointDeserializer extends JsonDeserializer<Point> {
    private final GeometryFactory geometryFactory = new GeometryFactory();

    @Override
    public Point deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        String text = p.getText();
        String[] coords = text.split(",");
        double latitude = Double.parseDouble(coords[0].trim());
        double longitude = Double.parseDouble(coords[1].trim());
        return geometryFactory.createPoint(new Coordinate(longitude, latitude));
    }
}
