package com.slee.dao;

import com.slee.BlogEntry;
import org.pegdown.PegDownProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by stephen on 8/21/15.
 */
@Repository
public class BlogEntryDaoImpl implements BlogEntryDao {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private SimpleJdbcInsert insertBlogEntry;

    @Autowired
    public void setNamedParameterJdbcTemplate(NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }
    @Autowired
    public void setDataSource(DataSource dataSource) {
        this.insertBlogEntry = new SimpleJdbcInsert(dataSource)
                .withTableName("blogEntries")
                .usingGeneratedKeyColumns("id");
    }

    @Override
    public void insertBlogEntry(BlogEntry blogEntry) {
        Map<String, Object> params = new HashMap<String, Object>(2);
        params.put("name", blogEntry.getName());
        params.put("title", blogEntry.getTitle());
        params.put("summary", blogEntry.getSummary());
        params.put("postUrl", blogEntry.getTitle());
        params.put("backgroundUrl", blogEntry.getBackgroundUrl());
        params.put("markdownContent", blogEntry.getMarkdownContent());
        params.put("htmlContent", blogEntry.getHtmlContent());
        insertBlogEntry.executeAndReturnKey(params);
    }



    @Override
    public BlogEntry findPostById(String id) {
        Map<String, Object> params = new HashMap<String, Object>(2);
        String sql = "SELECT * FROM blogEntries WHERE id=:varId";
        params.put("varId", id);
        log.info("query id from db: " + id);
        return namedParameterJdbcTemplate.queryForObject(sql, params, new BlogMapper());
    }

    @Override
    public void deletePostById(String id) {
        Map<String, Object> params = new HashMap<String, Object>(2);
        String sql = "DELETE FROM blogEntries WHERE id=:varId";
        params.put("varId", id);
        log.info("deleting blog entry where post id = " + id);
        namedParameterJdbcTemplate.update(sql, params);
    }

    @Override
    public BlogEntry findPostLatest() {
        Map<String, Object> params = new HashMap<String, Object>(2);
        String sql = "SELECT * FROM blogEntries ORDER BY id DESC LIMIT 1";
        return namedParameterJdbcTemplate.queryForObject(sql, params, new BlogMapper());
    }

    private static final class BlogMapper implements RowMapper<BlogEntry> {

        public BlogEntry mapRow(ResultSet rs, int rowNum) throws SQLException {
            BlogEntry entry = new BlogEntry();
            entry.setId(rs.getInt("id"));
            entry.setName(rs.getString("name"));
            entry.setTitle(rs.getString("title"));
            entry.setSummary(rs.getString("summary"));
            entry.setHtmlContent(rs.getString("htmlContent"));
            entry.setMarkdownContent(rs.getString("markdownContent"));
            entry.setPostUrl(rs.getString("postUrl"));
            entry.setBackgroundUrl(rs.getString("backgroundUrl"));
            entry.setAddedDate(rs.getTimestamp("addedDate"));
            entry.setUpdatedDate(rs.getTimestamp("updatedDate"));
            return entry;
        }
    }

    @Override
    public List<BlogEntry> findAll() {
        Map<String, Object> params = new HashMap<String, Object>();
        String sql = "SELECT * FROM blogEntries ORDER BY id DESC LIMIT 100";
        List<BlogEntry> result = namedParameterJdbcTemplate.query(sql, params, new BlogMapper());
        return result;
    }

    @Override
    public void updatePost(BlogEntry blogEntry) {
        PegDownProcessor processor=new PegDownProcessor();
        String htmlContent = processor.markdownToHtml(blogEntry.getMarkdownContent());

        Map<String, Object> params = new HashMap<String, Object>();
        params.put("varId", blogEntry.getId());
        params.put("varName", blogEntry.getName());
        params.put("varTitle", blogEntry.getTitle());
        params.put("varPostUrl", blogEntry.getTitle());
        params.put("varSummary", blogEntry.getSummary());
        params.put("varHtmlContent", htmlContent);
        params.put("varMarkdownContent", blogEntry.getMarkdownContent());


        String sql = "UPDATE blogEntries" +
                " SET title=:varTitle," +
                " name=:varName," +
                " postUrl=:varPostUrl," +
                " summary=:varSummary," +
                " htmlContent=:varHtmlContent," +
                " markdownContent=:varMarkdownContent" +
                " WHERE id=:varId";

        namedParameterJdbcTemplate.update(sql, params);
    }
}
