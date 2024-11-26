package uet.oop.bomberman.screen;
import uet.oop.bomberman.Game;
import uet.oop.bomberman.gui.GameScreen;
import uet.oop.bomberman.input.Keyboard;
import uet.oop.bomberman.utils.EGameControl;
import uet.oop.bomberman.utils.Global;
import java.awt.*;
import java.util.ArrayList;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class DeadScreen extends GameScreen {
    ArrayList<String> options = new ArrayList<>();
    int selectorIndex = 0;
    private Game game;
    private BufferedImage restartIcon;
    private BufferedImage homeIcon;
    private BufferedImage backgroundImage;
    private BufferedImage gameover;
    public DeadScreen(Game game) {
        this.game = game;   
        options.add("Restart");
        options.add("Back Home");
        try {
            gameover = ImageIO.read(getClass().getResource("/menu/gameover.png"));
            restartIcon = ImageIO.read(getClass().getResource("/menu/restart.png"));
            homeIcon = ImageIO.read(getClass().getResource("/menu/9165683_home_house_icon.png"));
            backgroundImage = ImageIO.read(getClass().getResource("/menu/forest_by_forheksed_d9q4k94-fullview 1.png"));
            homeIcon = resizeImage(homeIcon, 11 * Game.SCALE, 11 * Game.SCALE);
            restartIcon = resizeImage(restartIcon, 11 * Game.SCALE, 11 * Game.SCALE);
            restartIcon = colorizeIcon(restartIcon, Color.black);
            homeIcon = colorizeIcon(homeIcon, Color.black);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void setInput() {
        Keyboard.i().keyboardInputCallback = java.util.Optional.of(new Keyboard.KeyboardInputCallback() {
            @Override
            public void onKeyPressed(EGameControl gameControl) {
                switch (gameControl) {
                    case LEFT:
                        selectorIndex--;
                        break;
                    case RIGHT:
                        selectorIndex++;
                        break;
                    case ENTER:
                        if (selectorIndex == 0) {
                            game.restartGame();
                        } else if (selectorIndex == 1) {
                            game.startNewGame();
                        }
                        break;
                }

                if (selectorIndex < 0) {
                    selectorIndex = options.size() - 1;
                } else if (selectorIndex > options.size() - 1) {
                    selectorIndex = 0;
                }
            }
        });
    }
    private BufferedImage resizeImage(BufferedImage originalImage, int targetWidth, int targetHeight) {
        BufferedImage resizedImage = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = resizedImage.createGraphics();
        g2d.drawImage(originalImage, 0, 0, targetWidth, targetHeight, null);
        g2d.dispose();
        return resizedImage;
    }
    // Rest of the class remains unchanged
    private BufferedImage colorizeIcon(BufferedImage icon, Color color) {
        BufferedImage newIcon = new BufferedImage(icon.getWidth(), icon.getHeight(), BufferedImage.TYPE_INT_ARGB);
        for (int x = 0; x < icon.getWidth(); x++) {
            for (int y = 0; y < icon.getHeight(); y++) {
                int argb = icon.getRGB(x, y);
                if ((argb >> 24) != 0x00) { // Check if pixel is not transparent
                    newIcon.setRGB(x, y, color.getRGB());
                } else {
                    newIcon.setRGB(x, y, argb);
                }
            }
        }
        return newIcon;
    }

    @Override
    public void drawScreen(Graphics g) {
        
        if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, Global.screenWidth, Global.screenHeight, null);
        } else {
            g.setColor(Color.BLACK);
            g.fillRect(0, 0, Global.screenWidth, Global.screenHeight);
        }
        drawTitle(g);
        drawPOINTS(g, game.getBoard().getGameInfoManager().getPoints());
        drawTIMES(g, game.getBoard().getGameInfoManager().getTime());
        drawOptions(g);
        drawSelector(g);
        g.drawImage(gameover, Global.screenWidth/3 + 40, Global.screenHeight/2 + 80, 50*Game.SCALE, 50*Game.SCALE, null);
    }

    private void drawTitle(Graphics g) {
        String title = "GAME OVER";
        Font font = new Font("Arial", Font.BOLD, 22 * Game.SCALE);
        g.setFont(font);
        g.setColor(Color.BLACK);

        FontMetrics fm = g.getFontMetrics();
        int x = (Global.screenWidth - fm.stringWidth(title)) / 2;
        int marginTop = 180;
        int y = marginTop + fm.getAscent();

        g.drawString(title, x, y);
    }
    private void drawPOINTS(Graphics g,int points)
    {
       String Point = "POINTS: " + points;
        Font font = new Font("Arial", Font.BOLD, 6 * Game.SCALE);
        g.setFont(font);
        g.setColor(Color.BLACK);

        FontMetrics fm = g.getFontMetrics();
        int textWidth = fm.stringWidth(Point);
        int x = (Global.screenWidth - textWidth) / 2; // Vị trí x để chuỗi ở giữa màn hình
        int marginTop = 255;
        int y = marginTop + fm.getAscent();

        g.drawString(Point, x, y);

    }
    private void drawTIMES(Graphics g,int times)
    {
       String Point = "TIME : " + times;
        Font font = new Font("Arial", Font.BOLD, 6 * Game.SCALE);
        g.setFont(font);
        g.setColor(Color.BLACK);

        FontMetrics fm = g.getFontMetrics();
        int textWidth = fm.stringWidth(Point);
        int x = (Global.screenWidth - textWidth) / 2; // Vị trí x để chuỗi ở giữa màn hình
        int marginTop = 280;
        int y = marginTop + fm.getAscent();

        g.drawString(Point, x, y);

    }
    private void drawOptions(Graphics g) {
        int w = Global.screenWidth;
        int h = Global.screenHeight;
        int iconHeight = restartIcon.getHeight();
        int marginTop = (h - iconHeight + 50) / (2); 
    
        int spacing = 70; 
        int totalOptionsWidth = restartIcon.getWidth() + spacing + homeIcon.getWidth();
    
        int startX = (w - totalOptionsWidth) / 2;
    
        g.drawImage(restartIcon, startX, marginTop, null);
    
        int homeIconX = startX + restartIcon.getWidth() + spacing;
        g.drawImage(homeIcon, homeIconX, marginTop, null);
    }
    
    private void drawSelector(Graphics g) {
        int w = Global.screenWidth;
        int h = Global.screenHeight;
        int iconHeight = restartIcon.getHeight();
        int marginTop = (h - iconHeight) / 2; 
    
        int spacing = 80; 
        int totalOptionsWidth = restartIcon.getWidth() + spacing + homeIcon.getWidth();
    
        int startX = (w - totalOptionsWidth) / 2;
    
        int selectorX = selectorIndex == 0 ? startX - 20 : startX + restartIcon.getWidth() + spacing - 30;
        int y = marginTop + (iconHeight / 2)  + 35;
    
        g.drawString(">", selectorX, y);
    }
    

    @Override
    public void update() {
    }
    @Override
    public void onDestroy() {
    }
}
