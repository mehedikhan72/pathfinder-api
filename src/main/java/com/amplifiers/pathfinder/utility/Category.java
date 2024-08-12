package com.amplifiers.pathfinder.utility;

public enum Category {
    Basic_Programming("Basic Programming"),
    Data_Structures_and_Algorithms("Data Structures and Algorithms"),
    Web_Development("Web Development"),
    Mobile_App_Development("Mobile App Development"),
    Database_Management("Database Management"),
    Software_Engineering("Software Engineering"),
    Artificial_Intelligence("Artificial Intelligence"),
    Machine_Learning("Machine Learning"),
    Cybersecurity("Cybersecurity"),
    Cloud_Computing("Cloud Computing"),
    DevOps("DevOps"),
    Computer_Networks("Computer Networks"),
    Human_Computer_Interaction("Human-Computer Interaction"),
    Computer_Graphics("Computer Graphics"),
    Embedded_Systems("Embedded Systems"),
    Operating_Systems("Operating Systems"),
    Others("Others");

    private final String displayName;

    Category(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static Category fromString(String text) {
        for (Category category : Category.values()) {
            if (category.displayName.equalsIgnoreCase(text)) {
                return category;
            }
        }
        return null;
    }
}
