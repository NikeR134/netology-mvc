package ru.netology;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.stereotype.Repository;

@Repository
public class PostRepository {
    private final ConcurrentHashMap<Long, Post> posts = new ConcurrentHashMap<>();
    private final AtomicLong nextId = new AtomicLong(0);

    public List<Post> all() {
        List<Post> result = new ArrayList<>();

        for (Post post : posts.values()) {
            if (!post.isDeleted()) {
                result.add(post);
            }
        }

        return result;
    }

    public Post getById(long id) {
        Post post = posts.get(id);

        if (post == null || post.isDeleted()) {
            return null;
        }

        return post;
    }

    public synchronized Post save(Post post) {
        if (post.getId() == 0) {
            long id = nextId.incrementAndGet();
            Post created = new Post(id, post.getTitle(), post.getContent());
            posts.put(id, created);
            return created;
        }

        Post existing = posts.get(post.getId());

        if (existing == null || existing.isDeleted()) {
            throw new IllegalArgumentException(
                "Post with id=" + post.getId() + " not found"
            );
        }

        Post updated = new Post(
            post.getId(),
            post.getTitle(),
            post.getContent()
        );

        posts.put(post.getId(), updated);
        return updated;
    }

    public boolean removeById(long id) {
        Post post = posts.get(id);

        if (post == null || post.isDeleted()) {
            return false;
        }

        post.setDeleted(true);
        return true;
    }
}
