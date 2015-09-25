package com.slee.dao;

import com.slee.BlogEntry;

import java.util.List;

/**
 * Created by stephen on 8/21/15.
 */
public interface BlogEntryDao {
    void insertBlogEntry(BlogEntry blogEntry);

    void updatePost(BlogEntry blogEntry);

    BlogEntry findPostById(String id);

    void deletePostById(String id);

    BlogEntry findPostLatest();

    List<BlogEntry> findAll();
}
