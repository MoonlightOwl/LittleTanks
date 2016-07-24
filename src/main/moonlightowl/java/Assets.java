package main.moonlightowl.java;

/** Little Tanks resources */

import main.moonlightowl.java.sound.Sound;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public class Assets {
    public static Font ftitle, fmenu, fgui, fsmall;
    public static FontMetrics fmtitle, fmmenu, fmgui, fmsmall;

    public static BufferedImage iwall, igrass, ibox, ishield,
        ihole, ispawn, itrack, iexpldec, ibomb_on, ibomb_off,
        ishadowr, ishadowd, imetal, igrid, isplash_text, ibarrel,
        irocket, iconcrete, isand, ibush, ikey, ibarrelside,
        ishadowLS, ishadowLB, ishadowRS, ishadowRB, isnow, irustblock,
        icandy, iturret_base, iturret_tower, ifloor_tiles, icobblestone,
        itankshadow, ibeamh, ibeamv, idrygrass, iquestion, iturret_fire,
        icactus, iplastic;
    public static BufferedImage[] iexplosion = new BufferedImage[4],
        isandstone = new BufferedImage[6], ibullet = new BufferedImage[3],
        itank = new BufferedImage[5], ismoke = new BufferedImage[4],
        isparkle = new BufferedImage[4], idoor = new BufferedImage[6],
        isafe = new BufferedImage[2], ibutton = new BufferedImage[2],
        isnowcap = new BufferedImage[2], isnowflake = new BufferedImage[2];

    // resource paths
    public static File imagesPath = new File("resources/images/");
    public static File fontsPath = new File("resources/fonts/");

    // load resourses
    public static BufferedImage loadImage(String filename) throws IOException {
        return ImageIO.read(new File(imagesPath, filename));
    }
    public static Font loadFont(String filename, float size) throws IOException, FontFormatException {
        return Font.createFont(Font.TRUETYPE_FONT, new File(fontsPath, filename)).deriveFont(size);
    }

    public static void load(JPanel board){
        try{
            // fonts
            ftitle = loadFont("Rockwell.ttf", 120.0f);
            fmtitle = board.getFontMetrics(ftitle);
            fmenu = loadFont("SVBasicManual.ttf", 60.0f);
            fmmenu = board.getFontMetrics(fmenu);
            fgui = loadFont("secrcode.ttf", 40.0f);
            fmgui = board.getFontMetrics(fgui);
            fsmall = loadFont("SVBasicManual.ttf", 40.0f);
            fmsmall = board.getFontMetrics(fsmall);

            // images
            igrass = loadImage("grass.png");
            iwall = loadImage("wall.png");
            ibox = loadImage("box.png");
            ihole = loadImage("hole.png");
            itrack = loadImage("track.png");
            ispawn = loadImage("spawn.png");
            imetal = loadImage("metal.png");
            igrid = loadImage("grid.png");
            iexpldec = loadImage("explosiondec.png");
            isplash_text = loadImage("splash_text.png");
            ibomb_on = loadImage("bomb_on.png");
            ibomb_off = loadImage("bomb_off.png");
            ishadowr = loadImage("shadowr.png");
            ishadowd = loadImage("shadowd.png");
            itankshadow = loadImage("tankshadow.png");

            fillArray(iexplosion, "expl");
            fillArray(ismoke, "smoke");
            fillArray(isparkle, "sparkle");
            fillArray(isandstone, "sandstone");
            fillArray(ibullet, "bullet", 1, ibullet.length);
            fillArray(itank, "tank", 1, itank.length);
            fillArray(idoor, "door");
            fillArray(isafe, "safe");
            fillArray(ibutton, "button");
            fillArray(isnowcap, "snowcap");
            fillArray(isnowflake, "snowflake");

            ibarrel = loadImage("barrel.png");
            ibarrelside = loadImage("barrel2.png");
            ishield = loadImage("shield2.png");
            irocket = loadImage("rocket.png");
            iconcrete = loadImage("concrete.png");
            isand = loadImage("sand.png");
            ibush = loadImage("bush.png");
            ikey = loadImage("key.png");

            ishadowLS = loadImage("shadowback.png");
            ishadowRS = getScaledInstance(ishadowLS, -1, 1);
            ishadowLB = getScaledInstance(ishadowLS, 1, 2);
            ishadowRB = getScaledInstance(ishadowLB, -1, 1);

            icandy = loadImage("candy.png");
            iturret_base = loadImage("turret_base.png");
            iturret_tower = loadImage("turret_tower.png");
            iturret_fire = loadImage("turret_fire.png");
            ifloor_tiles = loadImage("plates0.png");
            icobblestone = loadImage("stone.png");
            isnow = loadImage("snow.png");
            irustblock = loadImage("rustblock.png");

            ibeamv = loadImage("beam.png");
            ibeamh = new BufferedImage(ibeamv.getHeight(), ibeamv.getWidth(),
                    BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = ibeamh.createGraphics();
            g.rotate(-Math.PI/2);
            g.drawImage(ibeamv, -ibeamv.getWidth(), 0, null);
            g.dispose();

            idrygrass = loadImage("drygrass.png");
            iquestion = loadImage("question.png");
            icactus = loadImage("cactus.png");
            iplastic = loadImage("plastic.png");

            // sounds
            Sound.init();

        } catch(NullPointerException e){
            Logger.error("NPE when loading game assets. WTF?", e);
        } catch(FileNotFoundException e){
            Logger.error("Asset file not found!", e);
        } catch(FontFormatException e){
            Logger.error("Font format error!", e);
        } catch(IOException e){
            Logger.error("IO error in Assets.load!", e);
        }
    }

    private static void fillArray(BufferedImage[] array, String filename){
        fillArray(array, filename, 0, array.length);
    }
    private static void fillArray(BufferedImage[] array, String filename, int first, int last){
        try {
            for(int i = first; i < last; i++){
                array[i] = loadImage(filename + Integer.toString(i) + ".png");
            }
        } catch(IOException e){
            Logger.error("IO error when loading texture array '"+filename+"'!");
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
