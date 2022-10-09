
import java.awt.*;
import java.awt.image.*;
import java.io.*;
import javax.swing.*;

//这个代码原版就很烂
public class ImageDisplay {

    JFrame frame;
    JLabel lbIm1;
    BufferedImage imgOne;
    BufferedImage foreGround;
    BufferedImage backGround;
    BufferedImage newImg;
    GridBagConstraints c;
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
                    b = (byte) (pix & 0xff);
                    g = (byte) (pix >> 8 & 0xff);
                    r = (byte) (pix >> 16 & 0xff);
//                   可以通过Byte.toUnSignedInt()获取真实的rgb值
                    //int pix = ((a << 24) + (r << 16) + (g << 8) + b);
                    img.setRGB(x, y, pix);
                    ind++;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void showAnImgWithoutNewFrame(String[] args) throws InterruptedException {

        // Read a parameter from command line
        // Read in the specified image
        imgOne = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        readImageRGB(width, height, args[0], imgOne);

        lbIm1 = new JLabel(new ImageIcon(imgOne));
        frame.getContentPane().add(lbIm1, c);

        frame.pack();
        frame.setVisible(true);
        Thread.sleep(25);
        frame.remove(lbIm1);
    }

    public void setUpFrame() {
        frame = new JFrame();
        GridBagLayout gLayout = new GridBagLayout();
        frame.getContentPane().setLayout(gLayout);
    }

    public void setUpC() {
        c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.anchor = GridBagConstraints.CENTER;
        c.weightx = 0.5;
        c.gridx = 0;
//        c.gridy = 0;
        c.gridy = 1;
    }

    public void getTwoPictures(String[] pics) {
        // Read a parameter from command line
        // Read in the specified image
        foreGround = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        readImageRGB(width, height, pics[0], foreGround);
        backGround = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        readImageRGB(width, height, pics[1], backGround);
    }

    public void combine2NewImage() {
        int foreGroundPix, r, g, b;
        newImg = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                foreGroundPix = foreGround.getRGB(x, y);
                b = foreGroundPix & 0xff;
                g = foreGroundPix >> 8 & 0xff;
                r = foreGroundPix >> 16 & 0xff;
//                如果是绿幕
                if (r > 0 && g > 0 && b > 0) {
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
////                   可以通过Byte.toUnSignedInt()获取真实的rgb值
//                //int pix = ((a << 24) + (r << 16) + (g << 8) + b);
//                img.setRGB(x, y, pix);
//                ind++;
            }
        }
    }

    public void showCombineImage(String[] pics) {
        getTwoPictures(pics);
        combine2NewImage();
        showAPicInNewFrame(newImg);
    }

    public void showTwoPictures(String[] pics) {
        getTwoPictures(pics);
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

    /*

          ImageDisplay ren = new ImageDisplay();
          ren.showPictures("C:\\Users\\bai\\Documents\\USA\\usc\\576\\hw2\\input\\foreground_3\\foreground_3.");
     */
    public void showSerialPicturesInOneFrame(ImageDisplay ren, String pos) throws InterruptedException {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < 480; i++) {
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
            ren.showAnImgWithoutNewFrame(arg1);
            builder.setLength(0);
//            Thread.sleep(42);
            System.out.println(i);
        }
    }

    public static void main(String[] args) throws InterruptedException {
        ImageDisplay ren = new ImageDisplay();
        ren.setUpFrame();
        ren.setUpC();
        ren.showSerialPicturesInOneFrame(ren, "C:\\Users\\bai\\Documents\\USA\\usc\\576\\hw2\\input\\foreground_3\\foreground_3.");
//        String[] arg1 = new String[]{"C:\\Users\\bai\\Documents\\USA\\usc\\576\\hw2\\input\\background_moving_1\\background_moving_1.0000.rgb"};
//        ren.showIms(arg1);
//        String[] arg2 = new String[]{"C:\\Users\\bai\\Documents\\USA\\usc\\576\\hw2\\input\\background_moving_1\\background_moving_1.0001.rgb"};
//        ren.showIms(arg2);
        ren.showTwoPictures(new String[]{"C:\\Users\\bai\\Documents\\USA\\usc\\576\\hw2\\input\\foreground_3\\foreground_3.0000.rgb"
                , "C:\\Users\\bai\\Documents\\USA\\usc\\576\\hw2\\input\\foreground_3\\foreground_3.0000.rgb"});
        ren.showTwoPictures(new String[]{"C:\\Users\\bai\\Documents\\USA\\usc\\576\\hw2\\input\\background_moving_1\\background_moving_1.0000.rgb"
                , "C:\\Users\\bai\\Documents\\USA\\usc\\576\\hw2\\input\\background_moving_1\\background_moving_1.0100.rgb"});
        ren.showCombineImage(new String[]{});
//        String[] arg3 = new String[]{"C:\\Users\\bai\\Documents\\USA\\usc\\576\\hw2\\input\\background_moving_1\\background_moving_1.0002.rgb"};
//        ren.showIms(arg3);
//        String[] arg4 = new String[]{"C:\\Users\\bai\\Documents\\USA\\usc\\576\\hw2\\input\\background_moving_1\\background_moving_1.0003.rgb"};
//        ren.showIms(arg4);
//        String[] arg5 = new String[]{"C:\\Users\\bai\\Documents\\USA\\usc\\576\\hw2\\input\\background_moving_1\\background_moving_1.0004.rgb"};
//        ren.showIms(arg5);

//        ren.showIms(arg1);
//        ren.showIms(arg2);
    }

}
