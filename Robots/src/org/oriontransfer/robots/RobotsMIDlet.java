package org.oriontransfer.robots;

import java.io.IOException;
import javax.microedition.midlet.*;
import javax.microedition.lcdui.*;

public class RobotsMIDlet extends MIDlet implements CommandListener {
    private GameWorld world;

    public RobotsMIDlet() {
    }

    public void startApp() {
        System.err.println("Starting Application");
        
        try {
            // Display.getDisplay(this).setCurrent(world);
            world = new GameWorld();

            Command exitCommand = new Command("Exit", Command.EXIT, 0);
            world.addCommand(exitCommand);
            world.setCommandListener(this);

            world.start();
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        Display.getDisplay(this).setCurrent(world);
    }

    public void pauseApp() {
    }

    public void destroyApp(boolean unconditional) {
        if (world != null) {
            world.stop();
        }
    }

    public void commandAction(Command c, Displayable s) {
        if (c.getCommandType() == Command.EXIT) {
            destroyApp(true);
            notifyDestroyed();
        }
    }
}
