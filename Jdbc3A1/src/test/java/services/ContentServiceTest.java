package services;

import models.Content;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;


class ContentServiceTest {

    @Test
    void addContent() {
        Content c1 = new Content("Dealing with Anxiety", "A helpful video about anxiety.", "Video", "https://youtu.be/fe4XNbLWEs4?si=3jbvZKv3VmWwQmNE", "https://i.ytimg.com/vi/fe4XNbLWEs4/hqdefault.jpg?sqp=-oaymwEnCNACELwBSFryq4qpAxkIARUAAIhCGAHYAQHiAQoIGBACGAY4AUAB&rs=AOn4CLA9vyDF6bqPoieOjeva4vU1aQYQ8Q", "Mental Health");
        ContentService cs = new ContentService();
        cs.addContent(c1);


    }

    @Test
    void getAllContent() {
    }

    @Test
    void updateContent() {
    }

    @Test
    void deleteContent() {
    }
}