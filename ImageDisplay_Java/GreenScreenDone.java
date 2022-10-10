import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;

public class GreenScreenDone {

    public static int frameNum = 480;

    JFrame frame;
    JLabel lbIm1;
    BufferedImage imgOne;
    BufferedImage foreGround;
    BufferedImage backGround;
    BufferedImage nextForeGround;

    public GreenScreenDone() {
        this.combinedImages = new ArrayList<>();
        frame = new JFrame();
        GridBagLayout gLayout = new GridBagLayout();
        frame.getContentPane().setLayout(gLayout);
        c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.anchor = GridBagConstraints.CENTER;
        c.weightx = 0.5;
        c.gridx = 0;
//        c.gridy = 0;
        c.gridy = 1;
    }

    BufferedImage newImg;
    GridBagConstraints c;
    ArrayList<BufferedImage> combinedImages;
    int width = 640; // default image width and height
    int height = 480;

    /**
     * Read Image RGB
     * Reads the image of given width and height at the given imgPath into the provided BufferedImage.
     */
    private void readImageRGB(int width, int height, String imgPath, BufferedImage img) {
        try {
            int frameLength = width * height * 3;

            File file = new File(imgPath);
            RandomAccessFile raf = new RandomAccessFile(file, "r");
            raf.seek(0);

            long len = frameLength;
            byte[] bytes = new byte[(int) len];

            raf.read(bytes);

            int ind = 0;
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    byte a = 0;
                    byte r = bytes[ind];
                    byte g = bytes[ind + height * width];
                    byte b = bytes[ind + height * width * 2];

                    int pix = 0xff000000 | ((r & 0xff) << 16) | ((g & 0xff) << 8) | (b & 0xff);
//                    b = (byte) (pix & 0xff);
//                    g = (byte) (pix >> 8 & 0xff);
//                    r = (byte) (pix >> 16 & 0xff);
                    //int pix = ((a << 24) + (r << 16) + (g << 8) + b);
                    img.setRGB(x, y, pix);
                    ind++;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void showAnImgFromDiskWithoutNewFrame(String[] imgPath) throws InterruptedException {

        // Read a parameter from command line
        // Read in the specified image
        imgOne = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        readImageRGB(width, height, imgPath[0], imgOne);

        lbIm1 = new JLabel(new ImageIcon(imgOne));
        frame.getContentPane().add(lbIm1, c);

        frame.pack();
        frame.setVisible(true);
        Thread.sleep(25);
        frame.remove(lbIm1);
    }

//    public void setUpFrame() {
//        frame = new JFrame();
//        GridBagLayout gLayout = new GridBagLayout();
//        frame.getContentPane().setLayout(gLayout);
//    }
//
//    public void setUpC() {
//        c = new GridBagConstraints();
//        c.fill = GridBagConstraints.HORIZONTAL;
//        c.anchor = GridBagConstraints.CENTER;
//        c.weightx = 0.5;
//        c.gridx = 0;
////        c.gridy = 0;
//        c.gridy = 1;
//    }

    public void getPictures(String[] pics, BufferedImage[] imgs) {
        // Read a parameter from command line
        // Read in the specified image
        for (int i = 0; i < pics.length; i++) {
            readImageRGB(width, height, pics[i], imgs[i]);
        }
    }

    public void extract(double hdown, double hup, double sdown, double vdown) {
        int foreGroundPix, r, g, b;
        newImg = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                foreGroundPix = foreGround.getRGB(x, y);
                b = foreGroundPix & 0xff;
                g = foreGroundPix >> 8 & 0xff;
                r = foreGroundPix >> 16 & 0xff;
                double[] hsv = RGB_HSV_Converter.rgb2hsv(new int[]{r, g, b});

                if (hsv[0] >= hdown && hsv[0] <= hup && hsv[1] >= sdown && hsv[2] >= vdown) {
                    newImg.setRGB(x, y, Integer.MAX_VALUE);
                } else {
                    newImg.setRGB(x, y, foreGroundPix);
                }
            }
        }
        showAPicInNewFrame(newImg);
    }

    public void combine2NewImageGS(double hdown, double hup, double sdown, double vdown) {
        int foreGroundPix, r, g, b;
        newImg = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                foreGroundPix = foreGround.getRGB(x, y);
                b = foreGroundPix & 0xff;
                g = foreGroundPix >> 8 & 0xff;
                r = foreGroundPix >> 16 & 0xff;
                double[] hsv = RGB_HSV_Converter.rgb2hsv(new int[]{r, g, b});

                if (hsv[0] >= hdown && hsv[0] <= hup && hsv[1] >= sdown && hsv[2] >= vdown) {
                    newImg.setRGB(x, y, backGround.getRGB(x, y));
                } else {
                    newImg.setRGB(x, y, foreGroundPix);
                }
//                byte a = 0;
//                byte r = bytes[ind];
//                byte g = bytes[ind + height * width];
//                byte b = bytes[ind + height * width * 2];
//
//                int pix = 0xff000000 | ((r & 0xff) << 16) | ((g & 0xff) << 8) | (b & 0xff);
//                b = (byte) (pix & 0xff);
//                g = (byte) (pix>>8 & 0xff);
//                r = (byte)(pix>>16 &0xff);
//                //int pix = ((a << 24) + (r << 16) + (g << 8) + b);
//                img.setRGB(x, y, pix);
//                ind++;
            }
        }
        combinedImages.add(newImg);
    }

    public void combine2NewImageStratEndVersion(double hError, double sError, double vError) {
        int foreGroundPix, nextForeGroundPix, r, g, b, nr, ng, nb;
        int startx=0,endx=0;
        double[] hsv, nhsv;
        newImg = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                foreGroundPix = foreGround.getRGB(x, y);
                b = foreGroundPix & 0xff;
                g = foreGroundPix >> 8 & 0xff;
                r = foreGroundPix >> 16 & 0xff;
                nextForeGroundPix = nextForeGround.getRGB(x, y);
                nb = nextForeGroundPix & 0xff;
                ng = nextForeGroundPix >> 8 & 0xff;
                nr = nextForeGroundPix >> 16 & 0xff;
                hsv = RGB_HSV_Converter.rgb2hsv(new int[]{r, g, b});
                nhsv = RGB_HSV_Converter.rgb2hsv(new int[]{nr, ng, nb});

                if (Math.abs(hsv[0] - nhsv[0]) <= hError && Math.abs(hsv[1] - nhsv[1]) <= sError && Math.abs(hsv[2] - nhsv[2]) <= vError) {
                    newImg.setRGB(x, y, backGround.getRGB(x, y));
                } else {
                    startx = x;
                    break;
                }
            }
            for (int x = width-1; x >-1 ; x--) {
                foreGroundPix = foreGround.getRGB(x, y);
                b = foreGroundPix & 0xff;
                g = foreGroundPix >> 8 & 0xff;
                r = foreGroundPix >> 16 & 0xff;
                nextForeGroundPix = nextForeGround.getRGB(x, y);
                nb = nextForeGroundPix & 0xff;
                ng = nextForeGroundPix >> 8 & 0xff;
                nr = nextForeGroundPix >> 16 & 0xff;
                hsv = RGB_HSV_Converter.rgb2hsv(new int[]{r, g, b});
                nhsv = RGB_HSV_Converter.rgb2hsv(new int[]{nr, ng, nb});

                if (Math.abs(hsv[0] - nhsv[0]) <= hError && Math.abs(hsv[1] - nhsv[1]) <= sError && Math.abs(hsv[2] - nhsv[2]) <= vError) {
                    newImg.setRGB(x, y, backGround.getRGB(x, y));
                } else {
                    endx = x;
                    break;
                }
            }
            for (int i = startx; i <=endx ; i++) {
                newImg.setRGB(i, y, foreGround.getRGB(i, y));
            }
        }
        combinedImages.add(newImg);
    }
    public void combine2NewImage(double hError, double sError, double vError) {
        int foreGroundPix, nextForeGroundPix, r, g, b, nr, ng, nb;
        double[] hsv, nhsv;
        newImg = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                foreGroundPix = foreGround.getRGB(x, y);
                b = foreGroundPix & 0xff;
                g = foreGroundPix >> 8 & 0xff;
                r = foreGroundPix >> 16 & 0xff;
                nextForeGroundPix = nextForeGround.getRGB(x, y);
                nb = nextForeGroundPix & 0xff;
                ng = nextForeGroundPix >> 8 & 0xff;
                nr = nextForeGroundPix >> 16 & 0xff;
                hsv = RGB_HSV_Converter.rgb2hsv(new int[]{r, g, b});
                nhsv = RGB_HSV_Converter.rgb2hsv(new int[]{nr, ng, nb});

                if (Math.abs(hsv[0] - nhsv[0]) <= hError && Math.abs(hsv[1] - nhsv[1]) <= sError && Math.abs(hsv[2] - nhsv[2]) <= vError) {
                    newImg.setRGB(x, y, backGround.getRGB(x, y));
                } else {
//                    startx = x;
//                    break;
                    newImg.setRGB(x, y, foreGroundPix);
                }
            }
        }
        combinedImages.add(newImg);
    }

    public void combineImageGS(String[] pics) throws InterruptedException {
        foreGround = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        backGround = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        getPictures(pics, new BufferedImage[]{foreGround, backGround});
        combine2NewImageGS(100, 176, 0.16, 0.33);
//        showCombinedImgWithoutNewFrame();
    }

    public void combineImage(String[] pics) throws InterruptedException {
        foreGround = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        backGround = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        nextForeGround = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        getPictures(pics, new BufferedImage[]{foreGround, backGround, nextForeGround});
//        combine2NewImage(0.000000000000001, 0.000000000000001, 0.000000000000001);
//        combine2NewImageStratEndVersion(5, 3, 3);
        combine2NewImage(0, 0, 0);
//        showCombinedImgWithoutNewFrame();
    }

    public void showTwoPictures(String[] pics) {
        foreGround = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        backGround = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        getPictures(pics, new BufferedImage[]{foreGround, backGround});
        showAPicInNewFrame(foreGround);
        showAPicInNewFrame(backGround);
    }

    public void showAPicInNewFrame(BufferedImage image) {
        lbIm1 = new JLabel(new ImageIcon(image));
        JFrame thisFrame = new JFrame();
        GridBagLayout gLayout = new GridBagLayout();
        thisFrame.getContentPane().setLayout(gLayout);
        thisFrame.getContentPane().add(lbIm1, c);
        thisFrame.pack();
        thisFrame.setVisible(true);
    }

    public void showAPicWithoutNewFrame(BufferedImage image) throws InterruptedException {
        lbIm1 = new JLabel(new ImageIcon(image));
        frame.getContentPane().add(lbIm1, c);
        frame.pack();
        frame.setVisible(true);
        Thread.sleep((long) 41.667);
        frame.remove(lbIm1);
    }

    /*

          ImageDisplay ren = new ImageDisplay();
          ren.showPictures("C:\\Users\\bai\\Documents\\USA\\usc\\576\\hw2\\input\\foreground_3\\foreground_3.");
     */
    public void showSerialPicturesInOneFrame(GreenScreenDone ren, String pos) throws InterruptedException {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < frameNum; i++) {
            int numofZ = 4 - Integer.toString(i).length();
//            builder.append("C:\\Users\\bai\\Documents\\USA\\usc\\576\\hw2\\input\\background_moving_2\\background_moving_2.");
            builder.append(pos);
//            builder.append("C:\\Users\\bai\\Documents\\USA\\usc\\576\\hw2\\input\\foreground_3\\foreground_3.");
            for (int j = 0; j < numofZ; j++) {
                builder.append(0);
            }
            builder.append(i);
            builder.append(".rgb");
            String[] arg1 = new String[]{builder.toString()};
            ren.showAnImgFromDiskWithoutNewFrame(arg1);
            builder.setLength(0);
//            Thread.sleep(42);
            System.out.println(i);
        }
    }

    public void showSerialCombinedPicturesInOneFrameGS(String[] pos) throws InterruptedException {
        StringBuilder builder1;
        StringBuilder builder2;
        for (int i = 0; i < frameNum; i++) {
            builder1 = new StringBuilder(pos[0]);
            builder2 = new StringBuilder(pos[1]);
            int numofZ = 4 - Integer.toString(i).length();
            for (int j = 0; j < numofZ; j++) {
                builder1.append(0);
                builder2.append(0);
            }
            builder1.append(i);
            builder2.append(i);
            builder1.append(".rgb");
            builder2.append(".rgb");
            String[] arg1 = new String[]{builder1.toString(), builder2.toString()};
            combineImageGS(arg1);
            builder1.setLength(0);
            builder2.setLength(0);
            System.out.println(i + "th frame done.");
        }
        for (BufferedImage combinedImage : combinedImages) {
            showAPicWithoutNewFrame(combinedImage);
        }
    }

    public void showSerialCombinedPicturesInOneFrame(String[] pos) throws InterruptedException {
        StringBuilder currentForeBuilder;
        StringBuilder currentBackBuilder;
        StringBuilder nextForeBuilder;
        for (int i = 0; i + 1 < frameNum; i++) {
            currentForeBuilder = new StringBuilder(pos[0]);
            currentBackBuilder = new StringBuilder(pos[1]);
            nextForeBuilder = new StringBuilder(pos[0]);
            int numofZ = 4 - Integer.toString(i).length();
            int numofZn = 4 - Integer.toString(i + 1).length();
            for (int j = 0; j < numofZ; j++) {
                currentForeBuilder.append(0);
                currentBackBuilder.append(0);
            }
            for (int j = 0; j < numofZn; j++) {
                nextForeBuilder.append(0);
            }
            currentForeBuilder.append(i);
            currentBackBuilder.append(i);
            nextForeBuilder.append(i + 1);
            currentForeBuilder.append(".rgb");
            currentBackBuilder.append(".rgb");
            nextForeBuilder.append(".rgb");
            String[] arg1 = new String[]{currentForeBuilder.toString(), currentBackBuilder.toString(), nextForeBuilder.toString()};
            combineImage(arg1);
            if (i+1 == frameNum-1){
                combineImage(arg1);
                System.out.println(i + "th frame done.");
                i+=1;
            }
            currentForeBuilder.setLength(0);
            currentBackBuilder.setLength(0);
            nextForeBuilder.setLength(0);
            System.out.println(i + "th frame done.");
        }
        for (BufferedImage combinedImage : combinedImages) {
            showAPicWithoutNewFrame(combinedImage);
        }
    }

    public static void main(String[] args) throws InterruptedException {
        String fore = args[0];
        String back = args[1];
        int ifHasGreenS = Integer.parseInt(args[2]);
        String[] temp = fore.split("\\\\");
        String foreFileName = temp[temp.length - 1];
        temp = back.split("\\\\");
        String backFileName = temp[temp.length - 1];
        GreenScreenDone ren = new GreenScreenDone();
        if (ifHasGreenS == 1) {
            ren.showSerialCombinedPicturesInOneFrameGS(new String[]{fore + "\\" + foreFileName + ".", back + "\\" + backFileName + "."});
        } else if (ifHasGreenS == 0) {
            ren.showSerialCombinedPicturesInOneFrame(new String[]{fore + "\\" + foreFileName + ".", back + "\\" + backFileName + "."});
        }
    }


}
