package com.rackluxury.ferrari.reddit.events;

public class ChangeSpoilerBlurEvent {
    public boolean needBlurSpoiler;

    public ChangeSpoilerBlurEvent(boolean needBlurSpoiler) {
        this.needBlurSpoiler = needBlurSpoiler;
    }
}
