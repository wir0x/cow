package ch.swissbytes.module.shared.utils;

import ch.swissbytes.module.buho.app.position.model.Position;

import java.util.Comparator;
import java.util.Date;
import java.util.List;

/****
 * Util positions
 */
public class PositionUtil {

    private static Double rad(Double point) {
        return point * Math.PI / 180;
    }

    /**
     * Get distance (on meters) between two positions
     *
     * @param position1
     * @param position2
     * @return
     */
    public static Double distance(Position position1, Position position2) {
        int _R = 6378137;
        double dLat = rad(position2.getLatitude() - position1.getLatitude());
        double dLng = rad(position2.getLongitude() - position1.getLongitude());
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(rad(position1.getLatitude())) * Math.cos(rad(position2.getLatitude())) *
                        Math.sin(dLng / 2) * Math.sin(dLng / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double d = _R * c;
        return LongUtil.decimalFormat(d, 2);
    }

    /**
     * Get total distance (on kilometers) by position list.
     *
     * @param positionList
     * @return
     */
    public static Double distance(List<Position> positionList) {
        Double distance = 0.0;

        if (positionList.size() == 1) {
            return distance;
        }

        Position positionTmp = positionList.get(0);

        for (Position position : positionList) {
            distance += distance(positionTmp, position);
            positionTmp = position;
        }

        return LongUtil.decimalFormat((distance / 1000), 2);
    }

    /**
     * Get max speed by position list.
     *
     * @param positionList
     * @return
     */
    public static Double maxSpeed(List<Position> positionList) {
        return positionList.stream()
                .max(Comparator.comparing(Position::getSpeed))
                .get()
                .getSpeed();
    }

    /**
     * Get Average speed by position list.
     *
     * @param positionList
     * @return
     */
    public static Double avgSpeed(List<Position> positionList) {
        return LongUtil.decimalFormat(positionList.stream()
                .mapToDouble(Position::getSpeed)
                .sum() / positionList.size(), 2);
    }

    /***
     * Get Date when device started to parker.
     *
     * @param positionList
     * @return
     */
    public static Date startParked(List<Position> positionList) {
        return positionList.get(0).getTime();
    }

    /**
     * Get Date when device stopped travel.
     *
     * @param positionList
     * @return
     */
    public static Date endParked(List<Position> positionList) {
        if (positionList.size() == 1) {
            return positionList.get(0).getTime();
        }
        return positionList.get(1).getTime();
    }

    /**
     * Get travel time when started to travel.
     *
     * @param positionList
     * @return
     */
    public static Date startTraveled(List<Position> positionList) {
        if (positionList.size() == 1) {
            startParked(positionList);
        }
        return endParked(positionList);
    }

    /**
     * Get travel time when ended to travel.
     *
     * @param positionList
     * @return
     */
    public static Date endTraveled(List<Position> positionList) {
        return positionList.get(positionList.size() - 1).getTime();
    }

    /**
     * Get stopped time.
     *
     * @param positionList
     * @return
     */
    public static Double timeParked(List<Position> positionList) {
        if (positionList.size() == 1) {
            System.out.println("size is only 1");
        }

        Date p1 = positionList.get(0).getTime();
        Date p2 = positionList.get(1).getTime();

        Long timeParkedMill = p2.getTime() - p1.getTime();
        // calculate time parked in minutes.
        Long stanTimeMin = (timeParkedMill / 1000) / 60;
        return stanTimeMin.doubleValue();
    }

    /***
     * Get travel time.
     *
     * @param positionList
     * @return
     */
    public static Double timeTraveled(List<Position> positionList) {
        Date firstTP = positionList.get(1).getTime();
        Date lastTP = positionList.get(positionList.size() - 1).getTime();

        Long timeTraveledMill = lastTP.getTime() - firstTP.getTime();
        // calculate time parked in minutes.
        Long stanTimeMin = (timeTraveledMill / 1000) / 60;
        return stanTimeMin.doubleValue();
    }
}
