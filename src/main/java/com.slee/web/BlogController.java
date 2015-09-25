package com.slee.web;

import com.slee.BlogEntry;
import com.slee.dao.BlogEntryDao;
import org.pegdown.PegDownProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.Errors;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

@Controller
public class BlogController {
    @Autowired
    private BlogEntryDao blogEntryDao;

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @RequestMapping(value={"/blog", "", "/", "/index"})
    public String blog(@ModelAttribute BlogEntry blogEntry, Model model){
        return getAllblogEntries(blogEntry, model);
    }

    @RequestMapping(value="/postCreate", method= RequestMethod.GET)
    public String postCreate(Model model){
        model.addAttribute("blogEntry", new BlogEntry());
        return "postCreate";
    }

    @RequestMapping(value="/postCreate", method= RequestMethod.POST)
    public String postSubmit(@ModelAttribute BlogEntry blogEntry, Model model){
        log.info("PostCreate-Input Markdown Content: ");
        log.info(blogEntry.getMarkdownContent());
        PegDownProcessor processor=new PegDownProcessor();
        String html = processor.markdownToHtml(blogEntry.getMarkdownContent());
        blogEntry.setHtmlContent(html);
        log.info("PostCreate-Output HTML Content: ");
        log.info(blogEntry.getHtmlContent());
        model.addAttribute("blogEntry", blogEntry);
        blogEntryDao.insertBlogEntry(blogEntry);

        return getAllblogEntries(blogEntry, model);
    }

    @RequestMapping(value="/posts/{id}/{postUrl}", method= RequestMethod.GET)
    public String seePost(@PathVariable("id") String id, @PathVariable("postUrl") String postUrl, @ModelAttribute BlogEntry blogEntry, Model model, HttpServletRequest request){
        blogEntry = blogEntryDao.findPostById(id);
        log.info("posts/{id}/{postUrl}-Requesting post id: " + id);
//        Redirect if postUrls don't match
        if(blogEntry.getPostUrl().equals(postUrl)){
            model.addAttribute("blogEntry", blogEntry);
            return "post";
        }else{
            return "redirect:/posts/" + id + "/" + blogEntry.getPostUrl();
        }
    }

    @RequestMapping(value="/delete/posts/{id}/{postUrl}")
    public String deletePost(@PathVariable("id") String id, @PathVariable("postUrl") String postUrl, @ModelAttribute BlogEntry blogEntry, Model model){
        blogEntryDao.deletePostById(id);
        return "redirect:/blog";
    }

    @RequestMapping(value="/edit/posts/{id}/{postUrl}", method= RequestMethod.GET)
    public String editPostGet(@PathVariable("id") String id, @PathVariable("postUrl") String postUrl, @ModelAttribute BlogEntry blogEntry, Model model, HttpServletRequest request){
        blogEntry = blogEntryDao.findPostById(id);
        log.info("edit/posts/{id}/{postUrl}-Editing post id: " + id);
//        Redirect if postUrls don't match
        if(blogEntry.getPostUrl().equals(postUrl)){
            model.addAttribute("blogEntry", blogEntry);
            return "postCreate";
        }else{
            return "redirect:/edit/posts/" + id + "/" + blogEntry.getPostUrl();
        }
    }

    @RequestMapping(value="/edit/posts/{id}/{postUrl}", method= RequestMethod.POST)
    public String editPostPost(@PathVariable("id") String id, @PathVariable("postUrl") String postUrl,
                               @ModelAttribute @Valid BlogEntry blogEntry, BindingResult bindingResult, Model model, HttpServletRequest request){
        if(bindingResult.hasErrors()){
            return "postCreate";
        }
        blogEntryDao.updatePost(blogEntry);
        return "redirect:/posts/" + id + "/" + blogEntry.getPostUrl();
    }

    private String getAllblogEntries(@ModelAttribute BlogEntry blogEntry, Model model){
        List<BlogEntry> blogEntries = blogEntryDao.findAll();
        log.info("Number of posts requested: " + String.valueOf(blogEntries.size()));
        model.addAttribute("blogEntries", blogEntries);
        return "blog";
    }
}