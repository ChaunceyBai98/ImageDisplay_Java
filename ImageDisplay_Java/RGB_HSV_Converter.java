import java.util.Arrays;

import static jdk.nashorn.internal.objects.Global.print;

public class RGB_HSV_Converter {
    public static double[] rgb2hsv(int[] rgb) {
        double h = 0, s,v,min;
        min = Arrays.stream(rgb).min().getAsInt();
        v = Arrays.stream(rgb).max().getAsInt();
        if (v != 0) {
            s = (v - min) / v;
        } else {
            s = 0;
        }
        if (v - min > 0) {
            if (v == rgb[0]) {
                h = 60 * (rgb[1] - rgb[2]) / (v - min);
            } else if (v == rgb[1]) {
                h = 120 + 60 * (rgb[2] - rgb[0]) / (v - min);
            } else if (v == rgb[2]) {
                h = 240 + 60 * (rgb[0] - rgb[1]) / (v - min);
            }
        } else {
            h = 0;
        }

        return new double[]{h, s, v/255};
    }

    public static int[] hsv2rgb(int[] hsv) {

        return new int[]{};
    }

    public static void main(String[] args) {
        double[] hsv = RGB_HSV_Converter.rgb2hsv(new int[]{43, 225, 10});
        for (double i : hsv) {
            System.out.println(i);
        }
    }
}
