import com.sun.xml.internal.bind.v2.TODO;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

public class Person {
    double x;
    double y;
    int height = 2;
    int width = 2;
    double speed = 1.4;
    double angleDeg;
    double acceleration = 0;
    Color color;
    ArrayList <BufferedImage> personImages = new ArrayList<>();


    Person(Frame frame) throws IOException {
        x = new Random().nextInt(frame.mapBlock.width + 100) - 50  + frame.x0;

        if (x >= frame.x0 && x <= frame.x0 + frame.getWidth())
            y = new Random().nextInt(frame.mapBlock.height) - 50 + frame.y0;
        else y = new Random().nextInt(frame.mapBlock.height + 50) - 50 + frame.y0;




//        do {
//            x = frame.rand.nextInt(frame.mapBlock.width) + frame.x0;
//            y = frame.rand.nextInt(frame.mapBlock.height) + frame.y0;
//        } while (((frame.tram.currentRailBlock.direction == Direction.UP && x + width >= frame.tram.currentRailBlock.x - 2 && x <= frame.tram.currentRailBlock.x + RailBlock.width + 2 && y <= frame.tram.currentRailBlock.y && y + height >= frame.tram.currentRailBlock.y - RailBlock.width)
//                || (frame.tram.currentRailBlock.direction == Direction.RIGHT && x + width >= frame.tram.currentRailBlock.x && x <= frame.tram.currentRailBlock.x + RailBlock.width && y <= frame.tram.currentRailBlock.y + RailBlock.width + 2 && y + height >= frame.tram.currentRailBlock.y - 2)
//                || (frame.tram.currentRailBlock.direction == Direction.LEFT && x + width >= frame.tram.currentRailBlock.x - RailBlock.width && x <= frame.tram.currentRailBlock.x && y <= frame.tram.currentRailBlock.y + 2 && y + height >= frame.tram.currentRailBlock.y - RailBlock.width - 2)));
        angleDeg = frame.rand.nextInt(180);
        color = Color.orange;

        int randomPerson = new Random().nextInt(7);
                personImages.add(ImageIO.read(Person.class.getResourceAsStream("people.png")).getSubimage(0, 19 * randomPerson, 18, 17));
                personImages.add(ImageIO.read(Person.class.getResourceAsStream("people.png")).getSubimage(18, 19 * randomPerson, 15, 17));
                personImages.add(ImageIO.read(Person.class.getResourceAsStream("people.png")).getSubimage(33, 19 * randomPerson, 15, 17));


    }

    Person(double x, double y, double angleDeg) throws IOException {
        this.x = x;
        this.y = y;
        this.angleDeg = angleDeg;
        color = Color.orange;
    }

    public void checkCollision(Frame frame){
        Tram tram = frame.tram;
        if (x + width >= tram.x && x <= tram.x + tram.width && y + height >= tram.y && y <= tram.y + tram.height) {
            frame.peopleToBeRemoved.add(this);
            frame.deadPeople.add(this);
            color = Color.RED;
            frame.points -= 50;
            if (!frame.menu.gameOver) frame.menu.deadPerson();
        }
    }
}
