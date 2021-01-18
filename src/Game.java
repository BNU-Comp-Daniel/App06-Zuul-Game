import java.util.ArrayList;

/**
 *  This class is the main class of the "World of Zuul" application. 
 *  "World of Zuul" is a very simple, text based adventure game.  Users 
 *  can walk around some scenery. That's all. It should really be extended 
 *  to make it more interesting!
 * 
 *  To play this game, create an instance of this class and call the "play"
 *  method.
 * 
 *  This main class creates and initialises all the others: it creates all
 *  rooms, creates the parser and starts the game.  It also evaluates and
 *  executes the commands that the parser returns.
 * 
 * @author  Michael Kölling and David J. Barnes
 * @version 2016.02.29
 * 
 * Modified and extended by Derek and Andrei
 *
 * Modified by Daniel Hale 18/01/2021
 */

public class Game 
{
    private Parser parser;
    private Room currentRoom;
    Room outside, garage, motorHome, pitLane, analytics, mediaRoom, gym, pitWall;
    ArrayList<Item> inventory = new ArrayList<Item>();
    /**
     * Create the game and initialise its internal map.
     */
    public Game() 
    {
        parser = new Parser();
        createRooms();
        play();
    }

    public static void main(String[] args) {
        Game myGame = new Game();
        myGame.play();
    }

    /**
     *  Main play routine.  Loops until end of play.
     */
    public void play() 
    {            
        printWelcome();

        // Enter the main command loop.  Here we repeatedly read commands and
        // execute them until the game is over.
                
        boolean finished = false;
        
        while (! finished) 
        {
            Command command = parser.getCommand();
            finished = processCommand(command);
        }
        
        System.out.println("Thank you for playing.  Good bye.");
    }

    /**
     * Create all the rooms and link their exits together.
     */
    private void createRooms()
    {
        // create the rooms
        outside = new Room("outside");
        garage = new Room("in the garage");
        motorHome = new Room("in the motor home");
        pitLane = new Room("at the pit lane");
        analytics = new Room("in the analytics room");
        mediaRoom = new Room("in the media room");
        gym = new Room("in the gym");
        pitWall = new Room("on the pit wall");

        // initialise room exits
        outside.setExit("east", mediaRoom);
        outside.setExit("south", garage);
        outside.setExit("west", gym);
        outside.setExit("north", motorHome);

        motorHome.setExit("south", outside);

        mediaRoom.setExit("west", outside);

        gym.setExit("east", outside);

        garage.setExit("north", outside);
        garage.setExit("east", analytics);
        garage.setExit("south", pitLane);

        analytics.setExit("west", garage);

        pitLane.setExit("west", pitWall);

        pitWall.setExit("east", pitLane);

        currentRoom = outside;  // start game outside

        motorHome.setItem(new Item("Drill"));
    }

    /**
     * Print out the opening message for the player.
     */
    private void printWelcome()
    {
        System.out.println();
        System.out.println("Welcome to the McLaren Formula 1 pit crew!");
        System.out.println("One of our cars is leading the race and needs to come into the pits!");
        System.out.println("The driver and the pit crew need some items to complete the pit stop and win the race.");
        System.out.println("You need to gather these items and bring them to the pit lane!");
        System.out.println("The whole team is counting on you!");
        System.out.println("Type '" + CommandWord.HELP + "' if you need help.");
        System.out.println();
        System.out.println(currentRoom.getLongDescription());
    }

    /**
     * Given a command, process (that is: execute) the command.
     * @param command The command to be processed.
     * @return true If the command ends the game, false otherwise.
     */
    private boolean processCommand(Command command) 
    {
        boolean wantToQuit = false;

        CommandWord commandWord = command.getCommandWord();

        switch (commandWord) 
        {
            case UNKNOWN:
                System.out.println("I don't know what you mean...");
                break;

            case INVENTORY:
                    printInventory();
                    break;

            case GET:
                getItem(command);
                break;

            case HELP:
                printHelp();
                break;

            case DROP:
                dropItem(command);

            case GO:
                goRoom(command);
                break;

            case QUIT:
                wantToQuit = quit(command);
                break;
        }
        return wantToQuit;
    }

    private void dropItem(Command command)
    {
        if(!command.hasSecondWord())
        {
            // if there is no second word, we don't know what to drop...
            System.out.println("Drop what?");
            return;
        }

        String item = command.getSecondWord();

        // try to drop item in the room
        Item newItem = null;
        int index = 0;
        for (int i = 0; i < inventory.size(); i++) {
            if (inventory.get(i).getDescription().equals(item)) {
                newItem = inventory.get(i);
                index = i;
            }
        }

        if (newItem == null) {
            System.out.println("That item is not in your inventory!");
        }
        else {
            inventory.remove(index);
            currentRoom.setItem(new Item(item));
            System.out.println("Dropped:" + item);
        }
    }

    private void getItem(Command command)
    {
        if(!command.hasSecondWord())
        {
            // if there is no second word, we don't know what to get...
            System.out.println("Get what?");
            return;
        }

        String item = command.getSecondWord();

        // try to get item in the room
        Item newItem = currentRoom.getItem(item);

        if (newItem == null) {
            System.out.println("That item is not here!");
        }
        else {
            inventory.add(newItem);
            currentRoom.removeItem(item);
            System.out.println("Picked up:" + item);
        }
    }

    private void printInventory()
    {
        String output = "";
        for (int i = 0; i < inventory.size(); i++) {
            output += inventory.get(i).getDescription() + " ";
        }
        System.out.println("you are carrying");
        System.out.println(output);
    }

    // implementations of user commands:

    /**
     * Print out some help information.
     * Here we print some stupid, cryptic message and a list of the 
     * command words.
     */
    private void printHelp() 
    {
        System.out.println("You are lost. You are alone. You wander");
        System.out.println("around at the university.");
        System.out.println();
        System.out.println("Your command words are:");
        parser.showCommands();
    }

    /** 
     * Try to go in one direction. If there is an exit, enter the new
     * room, otherwise print an error message.
     */
    private void goRoom(Command command) 
    {
        if(!command.hasSecondWord()) 
        {
            // if there is no second word, we don't know where to go...
            System.out.println("Go where?");
            return;
        }

        String direction = command.getSecondWord();

        // Try to leave current room.
        Room nextRoom = currentRoom.getExit(direction);

        if (nextRoom == null) {
            System.out.println("There is no door!");
        }
        else {
            currentRoom = nextRoom;
            System.out.println(currentRoom.getLongDescription());
        }
    }

    /** 
     * "Quit" was entered. Check the rest of the command to see
     * whether we really quit the game.
     * @return true, if this command quits the game, false otherwise.
     */
    private boolean quit(Command command) 
    {
        if(command.hasSecondWord()) {
            System.out.println("Quit what?");
            return false;
        }
        else {
            return true;  // signal that we want to quit
        }
    }
}
