import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Static utility class that is responsible for transforming the images.
 * Each function (or at least most functions) take in an Image and return
 * a transformed image.
 */
public class ImageManipulator {
    /**
     * Loads the image at the given path
     * @param path path to image to load
     * @return an Img object that has the given image loaded
     * @throws IOException
     */
    public static Img LoadImage(String path) throws IOException {
        Img image = new Img(path);
        return image;
    }

    /**
     * Saves the image to the given file location
     * @param image image to save
     * @param path location in file system to save the image
     * @throws IOException
     */
    public static void SaveImage(Img image, String path) throws IOException {
        image.Save("png", path);
    }

    /**
     * Converts the given image to grayscale (black, white, and gray). This is done
     * by finding the average of the RGB channel values of each pixel and setting
     * each channel to the average value.
     * @param image image to transform
     * @return the image transformed to grayscale
     */
    public static Img ConvertToGrayScale(Img image) {
        for(int x = 0; x < image.getWidth(); x++) {
            for (int y = x; y < image.getHeight(); y++) {
                RGB value = image.GetRGB(x, y);
                int avg = (value.GetBlue() + value.GetGreen() + value.GetRed()) / 3;
                value.SetRed(avg);
                value.SetGreen(avg);
                value.SetBlue(avg);
                image.SetRGB(x, y, value);
            }
        }
        return image;
    }

    /**
     * Inverts the image. To invert the image, for each channel of each pixel, we get
     * its new value by subtracting its current value from 255. (r = 255 - r)
     * @param image image to transform
     * @return image transformed to inverted image
     */
    public static Img InvertImage(Img image) {
        for(int x = 0; x < image.getWidth(); x++){
            for(int y = x; y < image.getHeight(); y++){
                RGB value = image.GetRGB(x, y);
                value.SetBlue(225 - value.GetBlue());
                value.SetRed(225 - value.GetRed());
                value.SetGreen(225 - value.GetGreen());
                image.SetRGB(x, y, value);
            }
        }
        return image;
    }

    /**
     * Converts the image to sepia. To do so, for each pixel, we use the following equations
     * to get the new channel values:
     * r = .393r + .769g + .189b
     * g = .349r + .686g + .168b
     * b = 272r + .534g + .131b
     * @param image image to transform
     * @return image transformed to sepia
     */
    public static Img ConvertToSepia(Img image) {
        for(int x = 0; x < image.getWidth(); x++) {
            for (int y = x; y < image.getHeight(); y++) {
                RGB v = image.GetRGB(x, y);
                double r = .393 * v.GetRed() + .769 * v.GetGreen() + .189 * v.GetBlue();
                double g = .349 * v.GetRed() + .686 * v.GetGreen() + .168 * v.GetBlue();
                double b = 272 * v.GetRed() + .534 * v.GetGreen() + .131 * v.GetBlue();
                v.SetRed((int) (r));
                v.SetGreen((int) (g));
                v.SetBlue((int) (b));
                image.SetRGB(x, y, v);
            }
        }
        return image;
    }

    /**
     * Creates a stylized Black/White image (no gray) from the given image. To do so:
     * 1) calculate the luminance for each pixel. Luminance = (.299 r^2 + .587 g^2 + .114 b^2)^(1/2)
     * 2) find the median luminance
     * 3) each pixel that has luminance >= median_luminance will be white changed to white and each pixel
     *      that has luminance < median_luminance will be changed to black
     * @param image image to transform
     * @return black/white stylized form of image
     */
    public static Img ConvertToBW(Img image) {
        ArrayList<Double> lum = new ArrayList<Double>();
        for (int x = 0; x < image.getWidth(); x++) {
            for (int y = x; y < image.getHeight(); y++) {
                RGB value = image.GetRGB(x, y);
                double luminance = Math.sqrt(.299 * Math.pow(value.GetRed(), 2) + .587 * Math.pow(value.GetGreen(), 2) + .114 * Math.pow(value.GetBlue(), 2));
                lum.add(luminance);
            }
        }
        Collections.sort(lum);//sorts lum

        int median = lum.size() / 2; //middle index of lum

        for (int x = 0; x < image.getWidth(); x++) {
            for (int y = x; y < image.getHeight(); y++) {
                RGB value = image.GetRGB(x, y);
                image.SetRGB(x, y, value);
                double luminance = Math.sqrt(.299 * Math.pow(value.GetRed(), 2) + .587 * Math.pow(value.GetGreen(), 2) + .114 * Math.pow(value.GetBlue(), 2));

                if (lum.get(y) < lum.get(median)) {
                    lum.set(y, 255.0);
                } else {
                    lum.set(y, 0.0);
                }
            }
        }
            return image;
        }


        /**
         * Rotates the image 90 degrees clockwise.
         * @param image image to transform
         * @return image rotated 90 degrees clockwise
         */
        public static Img RotateImage (Img image){
            Img img = new Img(image.getWidth(), image.getHeight());
            double degrees = 90;
            for(int x = 0; x < image.getX(); x++){
                for(int y = x; y < image.getY(); y++){
                    img.rotate(Math.toRadians(degrees));
                }
            }
            return image;
        }

        /**
         * Applies an Instagram-like filter to the image. To do so, we apply the following transformations:
         * 1) We apply a "warm" filter. We can produce warm colors by reducing the amount of blue in the image
         *      and increasing the amount of red. For each pixel, apply the following transformation:
         *          r = r * 1.2
         *          g = g
         *          b = b / 1.5
         * 2) We add a vignette (a black gradient around the border) by combining our image with
         *      an image of a halo (you can see the image at resources/halo.png). We take 65% of our
         *      image and 35% of the halo image. For example:
         *          r = .65 * r_image + .35 * r_halo
         * 3) We add decorative grain by combining our image with a decorative grain image
         *      (resources/decorative_grain.png). We will do this at a .95 / .5 ratio.
         * @param image image to transform
         * @return image with a filter
         * @throws IOException
         */
        public static Img InstagramFilter (Img image) throws IOException {
            for (int x = 0; x < image.getWidth(); x++) {
                for (int y = x; x < image.getHeight(); y++) {
                    RGB value = image.GetRGB(x, y);
                    int r = value.GetRed();
                    int g = value.GetGreen();
                    int b = value.GetBlue();

                    //warm filter
                    value.SetBlue((int) (b / 1.5));
                    value.SetRed((int) (r * 1.2));
                    value.SetGreen(g);

                    //vignette
                    Img halo = new Img("halo.png");
                    value.SetRed((int) (.65 * r_image + .35 * r_halo));
                    value.SetGreen((int) (.65 * g_image + .35 * g_halo));
                    value.SetBlue((int) (.65 * b_image + .35 * b_halo));

                    //decorative grain
                    Img deco = new Img("decorative_grain.png");
                    value.SetRed((int) (.95 / .5));
                    value.SetBlue((int) (.95 / .5));
                    value.SetGreen((int) (.95 / .5));
                }
            }
            return image;
        }

        /**
         * Sets the given hue to each pixel image. Hue can range from 0 to 360. We do this
         * by converting each RGB pixel to an HSL pixel, Setting the new hue, and then
         * converting each pixel back to an RGB pixel.
         * @param image image to transform
         * @param hue amount of hue to add
         * @return image with added hue
         */
        public static Img SetHue (Img image,int hue){
            for (int x = 0; x < image.getWidth(); x++) {
                for (int y = x; x < image.getHeight(); y++) {
                    HSL hPixel = image.GetRGB(x, y).ConvertToHSL();
                    hPixel.SetHue(hue);
                    RGB rPixel = hPixel.GetRGB();
                    image.SetRGB(x, y, rPixel);
                }
            }
            return image;
        }

        /**
         * Sets the given saturation to the image. Saturation can range from 0 to 1. We do this
         * by converting each RGB pixel to an HSL pixel, setting the new saturation, and then
         * converting each pixel back to an RGB pixel.
         * @param image image to transform
         * @param saturation amount of saturation to add
         * @return image with added hue
         */
        public static Img SetSaturation (Img image,double saturation){
            for (int x = 0; x < image.getWidth(); x++) {
                for (int y = x; x < image.getHeight(); y++) {
                    HSL hPixel = image.GetRGB(x, y).ConvertToHSL();
                    hPixel.SetSaturation(saturation);
                    RGB rPixel = hPixel.GetRGB();
                    image.SetRGB(x, y, rPixel);
                }
            }
            return image;
        }

        /**
         * Sets the lightness to the image. Lightness can range from 0 to 1. We do this
         * by converting each RGB pixel to an HSL pixel, setting the new lightness, and then
         * converting each pixel back to an RGB pixel.
         * @param image image to transform
         * @param lightness amount of hue to add
         * @return image with added hue
         */
        public static Img SetLightness (Img image,double lightness){
            for (int x = 0; x < image.getWidth(); x++) {
                for (int y = x; x < image.getHeight(); y++) {
                    HSL hPixel = image.GetRGB(x, y).ConvertToHSL();
                    hPixel.SetLightness(lightness);
                    RGB rPixel = hPixel.GetRGB();
                    image.SetRGB(x, y, rPixel);
                }
            }
            return image;
        }
    }
