import com.datastax.driver.mapping.annotations.UDT;

import java.util.List;
import java.util.Map;
import java.util.Set;

@UDT(name = "info")
public class Student {
    //int id;
    Long date;
    String name;
    List<String> items;
    Map<String, Double> courses;
    Set<Integer> requires;
    public Student(Long date, String name, List<String> items, Map<String, Double> courses, Set<Integer> requires){
        this.date = date;
        this.name = name;
        this.items = items;
        this.courses = courses;
        this.requires = requires;
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
