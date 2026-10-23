package com.smartpantry.manager.models;

public class Recipe {

    private int id;
    private String name;
    private String instructions;
    private int prepTimeMinutes;

    public Recipe(
            int id,
            String name,
            String instructions,
            int prepTimeMinutes) {

        this.id = id;
        this.name = name;
        this.instructions = instructions;
        this.prepTimeMinutes = prepTimeMinutes;
    }

    public Recipe(
            String name,
            String instructions,
            int prepTimeMinutes) {

        this(0, name, instructions, prepTimeMinutes);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getInstructions() {
        return instructions;
    }

    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }

    public int getPrepTimeMinutes() {
        return prepTimeMinutes;
    }

    public void setPrepTimeMinutes(int prepTimeMinutes) {
        this.prepTimeMinutes = prepTimeMinutes;
    }
}