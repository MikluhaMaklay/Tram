import com.sun.xml.internal.bind.v2.TODO;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.image.BufferStrategy;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Random;

public class Frame extends JFrame implements MouseWheelListener {

    double speed = 0;
    double acceleration = 2; // m / sec ^ 2


    double scaleSpeed = 0.03;
    double x0;
    double y0;
    Tram tram;
    MapBlock mapBlock;
    double currentScale;
    double targetScale;
    long prevTime;
    long dt;
    double dy = 0;
    ArrayList<Person> people;
    ArrayList<Person> peopleToBeRemoved = new ArrayList<>();
    RailBuilder railBuilder;


    Frame(){
        this.setSize(1000, 1000);
        this.setTitle("Tram");
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        this.setLocation(500, 50);
        this.setVisible(true);

        currentScale = targetScale = 20;
        mapBlock = new MapBlock(toMeters(getHeight()), toMeters(getWidth()));
        x0 = 75; // 75 m
        y0 = mapBlock.height - toMeters(this.getHeight()); // 50 m
        railBuilder = new RailBuilder((int) (x0 + this.getWidth() / 2 / currentScale - 2), (int) (y0 + this.getHeight() / currentScale - 5), Direction.UP); // x = 98 m, y = 195 m
        tram = new Tram(railBuilder.rail);
        prevTime = System.currentTimeMillis();

        people = new ArrayList<>();

//        for (int i = 0; i < 50; i++)
//        {
//            boolean isRotate = r.nextBoolean();
//            if (!isRotate)
//                railBuilder.move();
//            else railBuilder.rotate(new Random().nextBoolean());
//        }




        addMouseWheelListener(this);
        createBufferStrategy(2);
    }

    @Override
    public void paint(Graphics g) {
        BufferStrategy bufferStrategy = getBufferStrategy();        // Обращаемся к стратегии буферизации
        if (bufferStrategy == null) {                               // Если она еще не создана
            createBufferStrategy(2);                                // то создаем ее
            bufferStrategy = getBufferStrategy();                   // и опять обращаемся к уже наверняка созданной стратегии
        }
        g = bufferStrategy.getDrawGraphics();                       // Достаем текущую графику (текущий буфер)

        super.paint(g);

        dy += y0 - (tram.y - this.getHeight() / 2.0 / currentScale);


//        System.out.println("tram.y: " + tram.y);
//        System.out.println("getHeight: " + toMeters(getHeight()));
        if (tram.y < toMeters(getHeight()) / 2.0) y0 = tram.y - this.getHeight() / 2.0 / currentScale;
        x0 = (tram.x - this.getWidth() / 2.0 / currentScale + RailBlock.width / 2.0);


        Random  r = new Random();
        while (railBuilder.rail.getLast().y > y0 - 2 * RailBlock.length)
        {
            boolean isRotate = r.nextBoolean();
            if (!isRotate)
                railBuilder.move();
            else railBuilder.rotate(new Random().nextBoolean());
        }

        while (railBuilder.rail.getFirst().y > y0 + toMeters(getHeight())){
            railBuilder.deleteFirst();
        }

//        System.out.println(railBuilder.rail.size());

//        if (dy > RailBlock.length * 5){
//            railBuilder.rail.removeFirst();
//            for (int i = 0; i < 5; i++)
//                railBuilder.move();
//            dy = 0;
//        }



        if(currentScale > targetScale + scaleSpeed)
            currentScale -= scaleSpeed;
        else if (currentScale < targetScale - scaleSpeed)
            currentScale += scaleSpeed;


        for (int m = 0; m < mapBlock.height; m += 5)
            for(int n = 0; n < mapBlock.width; n += 5) {
                if ((m % 10 == 0 && n % 10 != 0) || (m % 10 != 0 && n % 10 == 0))
                    g.setColor(new Color(161, 208, 73));
                else g.setColor(new Color(169, 214, 81));

                int xStart = toPixels(n - x0);
                int yStart = toPixels(m - y0);

                System.out.println("xStart: " + xStart);
                System.out.println("yStart: " + yStart);



                g.fillRect(xStart, yStart , (int) ((n + 5 - x0) * currentScale) - xStart,(int) ((m + 5 - x0) * currentScale) - yStart);
            }

        // рельса
        g.setColor(Color.black);

         for (RailBlock railBlock: railBuilder.rail){
            g.setColor(Color.black);
            if (!railBlock.isRotate) {
                if (railBlock.direction == Direction.UP) {
                    g.drawLine(toPixels(railBlock.x - x0), toPixels(railBlock.y - y0 - RailBlock.length), toPixels(railBlock.x - x0), toPixels(railBlock.y - y0));
                    g.drawLine(toPixels(railBlock.x - x0 + RailBlock.width) - 1, toPixels(railBlock.y - y0 - RailBlock.length), toPixels(railBlock.x - x0 + RailBlock.width) - 1, toPixels(railBlock.y - y0));
                }
                else if (railBlock.direction == Direction.DOWN){
                    g.drawLine(toPixels(railBlock.x - x0), toPixels(railBlock.y - y0), toPixels(railBlock.x - x0), toPixels(railBlock.y - y0 + RailBlock.length));
                    g.drawLine(toPixels(railBlock.x - x0 - RailBlock.width) - 1, toPixels(railBlock.y - y0), toPixels(railBlock.x - x0 - RailBlock.width) - 1, toPixels(railBlock.y - y0 + RailBlock.length));
                }
                else if (railBlock.direction == Direction.RIGHT){
                    g.drawLine(toPixels(railBlock.x - x0), toPixels(railBlock.y - y0), toPixels(railBlock.x - x0 + RailBlock.length), toPixels(railBlock.y - y0));
                    g.drawLine(toPixels(railBlock.x - x0), toPixels(railBlock.y - y0 + RailBlock.width) - 1, toPixels(railBlock.x - x0 + RailBlock.length), toPixels(railBlock.y - y0 + RailBlock.width) - 1);
                }
                else {
                    g.drawLine(toPixels(railBlock.x - x0), toPixels(railBlock.y - y0), toPixels(railBlock.x - x0 - RailBlock.length), toPixels(railBlock.y - y0));
                    g.drawLine(toPixels(railBlock.x - x0), toPixels(railBlock.y - y0 - RailBlock.width) - 1, toPixels(railBlock.x - x0 - RailBlock.length), toPixels(railBlock.y - y0 - RailBlock.width) - 1);
                }
            }
            else {
                g.drawArc(toPixels(railBlock.xCenter - RailBlock.width - RailBuilder.R - x0), toPixels(railBlock.yCenter - RailBlock.width - RailBuilder.R - y0), toPixels(RailBlock.width + RailBuilder.R) * 2, toPixels(RailBlock.width + RailBuilder.R) * 2, railBlock.ang1, railBlock.ang2);
                g.drawArc(toPixels(railBlock.xCenter - RailBuilder.R - x0), toPixels(railBlock.yCenter - RailBuilder.R - y0), toPixels(RailBuilder.R) * 2, toPixels(RailBuilder.R) * 2, railBlock.ang1, railBlock.ang2);
            }

         }

        // трамвайка
        g.setColor(Color.cyan);
        {
            double startX = ((tram.x - x0) * currentScale);
            double startY = ((tram.y - y0) * currentScale);
            double widthX = (tram.width * currentScale);
            double heightY = (tram.height * currentScale);
            int x1 = (int) startX;
            int y1 = (int) startY;
            int x2 = (int) (startX + widthX);
            int y2 = (int) (startY + heightY);
//            g.fillRect(x1, y1, x2 - x1, y2 - y1);
            // TODO: вернуть трамвай
        }

        dt = System.currentTimeMillis() - prevTime;


        tram.y -= speed * dt / 1000 + acceleration * dt / 1000 * dt / 1000 / 2;
        if (speed <= 17) speed += acceleration * dt / 1000;



        // Людишки(бедолаги)
        synchronized (people) {
            for (Person person: people){
                person.checkCollision(this);

                g.fillOval((int) ((person.x - x0) * currentScale), (int) ((person.y - y0) * currentScale), (int) (person.width * currentScale), (int) (person.height * currentScale));
                person.x += dt * 1.0 / 1000 * Math.cos(Math.toRadians(person.angleDeg)) * person.speed + person.acceleration * dt * dt / 1000 / 1000 / 2;
                person.y += dt * 1.0 / 1000 * Math.sin(Math.toRadians(person.angleDeg)) * person.speed + person.acceleration * dt * dt / 1000 / 1000 / 2;



                if (person.x < 0 || person.x > mapBlock.width || person.y < 0 || person.y > mapBlock.height){
                    peopleToBeRemoved.add(person);
                }

            }
        }
        people.removeAll(peopleToBeRemoved);






        g.dispose();                // Освободить все временные ресурсы графики (после этого в нее уже нельзя рисовать)
        bufferStrategy.show();      // Сказать буферизирующей стратегии отрисовать новый буфер (т.е. поменять показываемый и обновляемый буферы местами)

        prevTime = System.currentTimeMillis();

        repaint();
    }

    int toPixels(double xMeters){
        return (int) (xMeters * currentScale);
    }

    int toMeters(int xPixels){
        return (int) (xPixels / currentScale);
    }



    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        if (e.getScrollType() == MouseWheelEvent.WHEEL_UNIT_SCROLL){
            if (targetScale <= 30) targetScale -= e.getWheelRotation() * e.getScrollAmount() / 100.0;
            else targetScale = 30;
            if (targetScale >= 10) targetScale -= e.getWheelRotation() * e.getScrollAmount() / 100.0;
            else targetScale = 10;
            targetScale = Math.round(targetScale * 100.0) / 100.0;
        }
    }
}
