package jokes_application.domain;


import lombok.Getter;
import lombok.Setter;

import lombok.Data;


public class Joke {
    private int id;
    private String type;
    private String setup;
    private String delivery;
    private String category; // Ensure this matches the API's category field

    // The @Data annotation eliminates the need for manual getters and setters.
    // If you need to add any additional logic in getters or setters, you can remove @Data and manually define them.


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSetup() {
        return setup;
    }

    public void setSetup(String setup) {
        this.setup = setup;
    }

    public String getDelivery() {
        return delivery;
    }

    public void setDelivery(String delivery) {
        this.delivery = delivery;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    @Override
    public String toString() {
        return "Joke{" +
                "type='" + type + '\'' +
                ", setup='" + setup + '\'' +
                ", delivery='" + delivery + '\'' +
                ", category='" + category + '\'' +
                '}';
    }
}
