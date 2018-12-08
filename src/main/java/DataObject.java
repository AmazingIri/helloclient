import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class DataObject {
    int id;
    Long date;
    String name;
    List<String> items;
    Map<String, Double> courses;
    Set<Integer> requires;
    @JsonCreator
    public DataObject(@JsonProperty("id") int id, @JsonProperty("date") Long date, @JsonProperty("name") String name, @JsonProperty("items") List<String> items, @JsonProperty("courses") Map<String, Double> courses, @JsonProperty("requires") Set<Integer> requires){
        this.id = id;
        this.date = date;
        this.name = name;
        this.items = items;
        this.courses = courses;
        this.requires = requires;
    }
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Long getDate() {
        return date;
    }

    public void setDate(Long date) {
        this.date = date;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getItems() {
        return items;
    }

    public void setItems(List<String> items) {
        this.items = items;
    }

    public Map<String, Double> getCourses() {
        return courses;
    }

    public void setCourses(Map<String, Double> courses) {
        this.courses = courses;
    }

    public Set<Integer> getRequires() {
        return requires;
    }

    public void setRequires(Set<Integer> requires) {
        this.requires = requires;
    }
}
