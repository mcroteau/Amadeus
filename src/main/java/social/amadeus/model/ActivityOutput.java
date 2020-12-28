package social.amadeus.model;

import social.amadeus.model.Account;
import social.amadeus.model.Post;

import java.util.List;

public class ActivityOutput {
    String message;
    List<Post> posts;
    List<Account> accounts;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<Post> getPosts() {
        return posts;
    }

    public void setPosts(List<Post> posts) {
        this.posts = posts;
    }

    public List<Account> getAccounts() {
        return accounts;
    }

    public void setAccounts(List<Account> accounts) {
        this.accounts = accounts;
    }
}
