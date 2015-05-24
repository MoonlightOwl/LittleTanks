package main.moonlightowl.java;

// Little Tanks resources

import main.moonlightowl.java.sound.Sound;

import java.io.File;
import java.io.IOException;
import java.io.FileNotFoundException;

import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.FontFormatException;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.AffineTransformOp;

import javax.swing.JPanel;
import javax.imageio.ImageIO;

public class Assets {
    public static Font ftitle, fmenu, fgui, fsmall;
    public static FontMetrics fmtitle, fmmenu, fmgui, fmsmall;

    public static BufferedImage iwall, igrass, ibox, ishield,
        ihole, ispawn, itrack, iexpldec, ibomb_on, ibomb_off,
        ishadowr, ishadowd, imetal, igrid, isplash_text, ibarrel,
        irocket, iconcrete, isand, ibush, ikey, ibarrelside,
        ishadowLS, ishadowLB, ishadowRS, ishadowRB,
        icandy, iturret_base, iturret_tower;
    public static BufferedImage[] iexplosion = new BufferedImage[4],
        isandstone = new BufferedImage[6], ibullet = new BufferedImage[3],
        itank = new BufferedImage[4], ismoke = new BufferedImage[4],
        isparkle = new BufferedImage[4], idoor = new BufferedImage[6],
        isafe = new BufferedImage[2], ibutton = new BufferedImage[2];

    // load resourses
    public static void load(JPanel board){
        try{
            // fonts
            ftitle = Font.createFont(Font.TRUETYPE_FONT, new File("resources/fonts/Rockwell.ttf")).deriveFont(120.0f);
            fmtitle = board.getFontMetrics(ftitle);
            fmenu = Font.createFont(Font.TRUETYPE_FONT, new File("resources/fonts/SVBasicManual.ttf")).deriveFont(60.0f);
            fmmenu = board.getFontMetrics(fmenu);
            fgui = Font.createFont(Font.TRUETYPE_FONT, new File("resources/fonts/secrcode.ttf")).deriveFont(40.0f);
            fmgui = board.getFontMetrics(fgui);
            fsmall = Font.createFont(Font.TRUETYPE_FONT, new File("resources/fonts/SVBasicManual.ttf")).deriveFont(40.0f);
            fmsmall = board.getFontMetrics(fsmall);

            // images
            igrass = ImageIO.read(new File("resources/images/grass.png"));
            iwall = ImageIO.read(new File("resources/images/wall.png"));
            ibox = ImageIO.read(new File("resources/images/box.png"));
            ihole = ImageIO.read(new File("resources/images/hole.png"));
            itrack = ImageIO.read(new File("resources/images/track.png"));
            ispawn = ImageIO.read(new File("resources/images/spawn.png"));
            imetal = ImageIO.read(new File("resources/images/metal.png"));
            igrid = ImageIO.read(new File("resources/images/grid.png"));
            iexpldec = ImageIO.read(new File("resources/images/explosiondec.png"));
            isplash_text = ImageIO.read(new File("resources/images/splash_text.png"));
            ibomb_on = ImageIO.read(new File("resources/images/bomb_on.png"));
            ibomb_off = ImageIO.read(new File("resources/images/bomb_off.png"));
            ishadowr = ImageIO.read(new File("resources/images/shadowr.png"));
            ishadowd = ImageIO.read(new File("resources/images/shadowd.png"));

            fillArray(iexplosion, "expl");
            fillArray(ismoke, "smoke");
            fillArray(isparkle, "sparkle");
            fillArray(isandstone, "sandstone");
            fillArray(ibullet, "bullet", 1, ibullet.length);
            fillArray(itank, "tank", 1, itank.length);
            fillArray(idoor, "door");
            fillArray(isafe, "safe");
            fillArray(ibutton, "button");

            ibarrel = ImageIO.read(new File("resources/images/barrel.png"));
            ibarrelside = ImageIO.read(new File("resources/images/barrel2.png"));
            ishield = ImageIO.read(new File("resources/images/shield2.png"));
            irocket = ImageIO.read(new File("resources/images/rocket.png"));
            iconcrete = ImageIO.read(new File("resources/images/concrete.png"));
            isand = ImageIO.read(new File("resources/images/sand.png"));
            ibush = ImageIO.read(new File("resources/images/bush.png"));
            ikey = ImageIO.read(new File("resources/images/key.png"));

            ishadowLS = ImageIO.read(new File("resources/images/shadowback.png"));
            ishadowRS = getScaledInstance(ishadowLS, -1, 1);
            ishadowLB = getScaledInstance(ishadowLS, 1, 2);
            ishadowRB = getScaledInstance(ishadowLB, -1, 1);

            icandy = ImageIO.read(new File("resources/images/candy.png"));
            iturret_base = ImageIO.read(new File("resources/images/turret_base.png"));
            iturret_tower = ImageIO.read(new File("resources/images/turret_tower.png"));

            // sounds
            Sound.init();

        } catch(NullPointerException e){
            System.out.print("Error =(\n");
        } catch(FileNotFoundException e){
            System.out.print("Error!\n");
            e.printStackTrace();
        } catch(FontFormatException e){
            System.out.print("Font format error!\n");
        } catch(IOException e){
            System.out.print("IO error!\n");
        }
    }

    private static void fillArray(BufferedImage[] array, String filename){
        fillArray(array, filename, 0, array.length);
    }
    private static void fillArray(BufferedImage[] array, String filename, int first, int last){
        try {
            for(int i = first; i < last; i++){
                array[i] = ImageIO.read(new File("resources/images/"+filename+Integer.toString(i)+".png"));
            }
        } catch(IOException e){
            System.out.print("IO error when filling array '"+filename+"'!\n");
        }
    }
    private static BufferedImage getScaledInstance(BufferedImage image, float sx, float sy){
        AffineTransform tx = AffineTransform.getScaleInstance(sx, sy);
        tx.translate(sx>=0 ? 0 : sx*image.getWidth(null),
                     sy>=0 ? 0 : sy*image.getHeight(null));
        AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
        BufferedImage result = new BufferedImage((int)(image.getWidth()*Math.abs(sx)),
                     (int)(image.getHeight()*Math.abs(sy)),
                     BufferedImage.TYPE_INT_ARGB);
        op.filter(image, result);
        return result;
    }
}
