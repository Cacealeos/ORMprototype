package Models;


import annotations.Column;
import annotations.Table;

@Table(name = "dog")
public class Dog {

    @Column(name = "d_id", primaryKey = true)
    private int id;

    @Column(name = "name")
    private String name;

    @Column(name = "age")
    private String age;

    @Column(name = "breed")
    private String breed;

    @Column(name = "personality")
    private String personality;
    @Column(name = "furcolor")
    private String furcolor;

    public Dog () {

    }

    public Dog(String name, String age, String breed, String personality, String furcolor) {
        this.name = name;
        this.age = age;
        this.breed = breed;
        this.personality = personality;
        this.furcolor = furcolor;
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

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getBreed() {
        return breed;
    }

    public void setBreed(String breed) {
        this.breed = breed;
    }

    public String getPersonality() {
        return personality;
    }

    public void setPersonality(String personality) {
        this.personality = personality;
    }

    public String getFurcolor() {
        return furcolor;
    }

    public void setFurcolor(String furcolor) {
        this.furcolor = furcolor;
    }

    @Override
    public String toString() {
        return "Dog{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", age='" + age + '\'' +
                ", breed='" + breed + '\'' +
                ", personality='" + personality + '\'' +
                ", furcolor='" + furcolor + '\'' +
                '}';
    }
}