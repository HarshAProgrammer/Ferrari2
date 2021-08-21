package com.rackluxury.ferrari.reddit.events;

import com.rackluxury.ferrari.reddit.Flair;

public class FlairSelectedEvent {
    public long viewPostDetailFragmentId;
    public Flair flair;

    public FlairSelectedEvent(long viewPostDetailFragmentId, Flair flair) {
        this.viewPostDetailFragmentId = viewPostDetailFragmentId;
        this.flair = flair;
    }
}
