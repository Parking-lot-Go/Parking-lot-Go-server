package car.demo.global.utils;

public class GeoUtil {

  private static final double EARTH_RADIUS_KM_PER_DEGREE = 111.0;

  public static BoundingBox calculateBoundingBox(double centerLat, double centerLng, double radiusKm) {
    double latRange = radiusKm / EARTH_RADIUS_KM_PER_DEGREE;
    double lngRange = radiusKm / (EARTH_RADIUS_KM_PER_DEGREE * Math.cos(Math.toRadians(centerLat)));

    double swLat = centerLat - latRange;
    double neLat = centerLat + latRange;

    double swLng = centerLng - lngRange;
    double neLng = centerLng + lngRange;

    return new BoundingBox(swLat, swLng, neLat, neLng);
  }

  public static class BoundingBox {
    private final double swLat;
    private final double swLng;
    private final double neLat;
    private final double neLng;

    public BoundingBox(double swLat, double swLng, double neLat, double neLng) {
      this.swLat = swLat;
      this.swLng = swLng;
      this.neLat = neLat;
      this.neLng = neLng;
    }

    public double getSwLat() { return swLat; }
    public double getSwLng() { return swLng; }
    public double getNeLat() { return neLat; }
    public double getNeLng() { return neLng; }
  }
}
