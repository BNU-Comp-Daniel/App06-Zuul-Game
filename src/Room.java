import java.util.ArrayList;
import java.util.Set;
import java.util.HashMap;

/**
 * Class Room - a room in an adventure game.
 *
 * This class is part of the "World of Zuul" application. 
 * "World of Zuul" is a very simple, text based adventure game.  
 *
 * A "Room" represents one location in the scenery of the game.  It is 
 * connected to other rooms via exits.  For each existing exit, the room 
 * stores a reference to the neighboring room.
 * 
 * @author  Michael Kölling and David J. Barnes
 * @version 2016.02.29
 *
 * Modified by Daniel Hale 18/01/2021
 */

public class Room 
{
    private String description;
    // String is the key to a room in that direction
    // east would be an exit that goes to the Room
    private HashMap<String, Room> exits;
    ArrayList<Item> Items = new ArrayList<Item>();

    /**
     * Create a room described "description". Initially, it has
     * no exits. "description" is something like "a kitchen" or
     * "an open court yard".
     * @param description The room's description.
     */
    public Room(String description) 
    {
        this.description = description;
        exits = new HashMap<>();
    }

    /**
     * Define an exit from this room.
     * @param direction The direction of the exit.
     * @param neighbor  The room to which the exit leads.
     */
    public void setExit(String direction, Room neighbor) 
    {
        exits.put(direction, neighbor);
    }

    /**
     * @return The short description of the room
     * (the one that was defined in the constructor).
     */
    public String getShortDescription()
    {
        return description;
    }

    /**
     * Return a description of the room in the form:
     *     You are in the kitchen.
     *     Exits: north west
     * @return A long description of this room
     */
    public String getLongDescription()
    {
        return "You are " + description + ".\n" + getExitString();
    }

    /**
     * Return a string describing the room's exits, for example
     * "Exits: north west".
     * @return Details of the room's exits.
     */
    private String getExitString()
    {
        String returnString = "Exits:";
        Set<String> keys = exits.keySet();
        
        for(String exit : keys) 
        {
            returnString += " " + exit;
        }
        returnString+= "\nItems in the room:\n";
        returnString+= getRoomItems() + "\n";
        return returnString;
    }

    /**
     * Return the room that is reached if we go from this room in direction
     * "direction". If there is no room in that direction, return null.
     * @param direction The exit's direction.
     * @return The room in the given direction.
     */
    public Room getExit(String direction) 
    {
        return exits.get(direction);
    }

    /**
     * Get the items in the room
     */
    public Item getItem(int index) {
        return Items.get(index);
    }

    /**
     * Get the items in the room
     */
    public Item getItem(String itemName) {
        for (int i = 0; i < Items.size(); i++) {
            if (Items.get(i).getDescription().equals(itemName)) {
                return Items.get(i);
            }
        }
        return null;
    }

    /**
     * Remove the items from the room.
     */
    public void removeItem(String itemName)
    {
        for (int i = 0; i < Items.size(); i++)
        {
            if (Items.get(i).getDescription().equals(itemName))
                Items.remove(i);
        }
    }

    /**
     * Set an item in the room
     * @param newItem
     */
    public void setItem(Item newItem) {
        Items.add(newItem);
    }

    /**
     * Getting a description for the items held in that room.
     * @return
     */
    public String getRoomItems() {
        String output = "";
        for (int i = 0; i < Items.size(); i++) {
            output += Items.get(i).getDescription() + " ";
        }
        return output;
    }
}

