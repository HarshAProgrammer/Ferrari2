package com.rackluxury.ferrari.reddit.events;

import com.rackluxury.ferrari.reddit.post.Post;

public class PostUpdateEventToPostDetailFragment {
    public final Post post;

    public PostUpdateEventToPostDetailFragment(Post post) {
        this.post = post;
    }
}
