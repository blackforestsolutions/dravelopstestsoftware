package de.blackforestsolutions.dravelopstestsoftware.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import de.blackforestsolutions.dravelopsdatamodel.Point;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.LinkedList;
import java.util.List;

@Getter
@JsonDeserialize(builder = Polygon.PolygonBuilder.class)
public final class Polygon {

    private final List<Point> points;

    private Polygon(PolygonBuilder polygonBuilder) {
        this.points = polygonBuilder.getPoints();
    }

    @Setter
    @Getter
    @Accessors(chain = true)
    @JsonPOJOBuilder(withPrefix = "set")
    public static class PolygonBuilder {

        private List<Point> points = new LinkedList<>();

        public PolygonBuilder() {

        }

        public Polygon build() {
            return new Polygon(this);
        }
    }
}