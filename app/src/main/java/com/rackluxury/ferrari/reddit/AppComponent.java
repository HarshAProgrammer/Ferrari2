package com.rackluxury.ferrari.reddit;

import javax.inject.Singleton;

import dagger.Component;
import com.rackluxury.ferrari.reddit.activities.RedditAccountPostsActivity;
import com.rackluxury.ferrari.reddit.activities.AccountSavedThingActivity;
import com.rackluxury.ferrari.reddit.activities.AnonymousSubscriptionsActivity;
import com.rackluxury.ferrari.reddit.activities.RedditCommentActivity;
import com.rackluxury.ferrari.reddit.activities.CreateMultiRedditActivity;
import com.rackluxury.ferrari.reddit.activities.CustomThemeListingActivity;
import com.rackluxury.ferrari.reddit.activities.RedditCustomThemePreviewActivity;
import com.rackluxury.ferrari.reddit.activities.CustomizePostFilterActivity;
import com.rackluxury.ferrari.reddit.activities.RedditCustomizeThemeActivity;
import com.rackluxury.ferrari.reddit.activities.RedditEditCommentActivity;
import com.rackluxury.ferrari.reddit.activities.RedditEditMultiRedditActivity;
import com.rackluxury.ferrari.reddit.activities.RedditEditPostActivity;
import com.rackluxury.ferrari.reddit.activities.FetchRandomSubredditOrPostActivity;
import com.rackluxury.ferrari.reddit.activities.RedditFilteredPostsActivity;
import com.rackluxury.ferrari.reddit.activities.FullMarkdownActivity;
import com.rackluxury.ferrari.reddit.activities.GiveAwardActivity;
import com.rackluxury.ferrari.reddit.activities.RedditInboxActivity;
import com.rackluxury.ferrari.reddit.activities.RedditLinkResolverActivity;
import com.rackluxury.ferrari.reddit.activities.RedditLockScreenActivity;
import com.rackluxury.ferrari.reddit.activities.RedditLoginActivity;
import com.rackluxury.ferrari.reddit.activities.RedditMainActivity;
import com.rackluxury.ferrari.reddit.activities.MultiredditSelectionActivity;
import com.rackluxury.ferrari.reddit.activities.RedditPostFilterPreferenceActivity;
import com.rackluxury.ferrari.reddit.activities.PostFilterUsageListingActivity;
import com.rackluxury.ferrari.reddit.activities.RedditPostGalleryActivity;
import com.rackluxury.ferrari.reddit.activities.RedditPostImageActivity;
import com.rackluxury.ferrari.reddit.activities.RedditPostLinkActivity;
import com.rackluxury.ferrari.reddit.activities.RedditPostTextActivity;
import com.rackluxury.ferrari.reddit.activities.RedditPostVideoActivity;
import com.rackluxury.ferrari.reddit.activities.RedditRPANActivity;
import com.rackluxury.ferrari.reddit.activities.RedditRulesActivity;
import com.rackluxury.ferrari.reddit.activities.RedditSearchActivity;
import com.rackluxury.ferrari.reddit.activities.RedditSearchUsersResultActivity;
import com.rackluxury.ferrari.reddit.activities.RedditSettingsActivity;
import com.rackluxury.ferrari.reddit.activities.RedditSubredditMultiselectionActivity;
import com.rackluxury.ferrari.reddit.activities.RedditTrendingActivity;
import com.rackluxury.ferrari.reddit.activities.RedditViewPostDetailActivity;
import com.rackluxury.ferrari.reddit.activities.RedditViewPrivateMessagesActivity;
import com.rackluxury.ferrari.reddit.activities.RedditViewRedditGalleryActivity;
import com.rackluxury.ferrari.reddit.activities.RedditViewVideoActivity;
import com.rackluxury.ferrari.reddit.activities.ReportActivity;
import com.rackluxury.ferrari.reddit.activities.SearchResultActivity;
import com.rackluxury.ferrari.reddit.activities.RedditSearchSubredditsResultActivity;
import com.rackluxury.ferrari.reddit.activities.SelectUserFlairActivity;
import com.rackluxury.ferrari.reddit.activities.SelectedSubredditsAndUsersActivity;
import com.rackluxury.ferrari.reddit.activities.SendPrivateMessageActivity;
import com.rackluxury.ferrari.reddit.activities.RedditSubmitCrosspostActivity;
import com.rackluxury.ferrari.reddit.activities.RedditSubredditSelectionActivity;
import com.rackluxury.ferrari.reddit.activities.RedditSubscribedThingListingActivity;
import com.rackluxury.ferrari.reddit.activities.SuicidePreventionActivity;
import com.rackluxury.ferrari.reddit.activities.ViewImageOrGifActivity;
import com.rackluxury.ferrari.reddit.activities.RedditViewImgurMediaActivity;
import com.rackluxury.ferrari.reddit.activities.RedditViewMultiRedditDetailActivity;
import com.rackluxury.ferrari.reddit.activities.RedditViewSubredditDetailActivity;
import com.rackluxury.ferrari.reddit.activities.RedditViewUserDetailActivity;
import com.rackluxury.ferrari.reddit.activities.RedditWebViewActivity;
import com.rackluxury.ferrari.reddit.bottomsheetfragments.FlairBottomSheetFragment;
import com.rackluxury.ferrari.reddit.fragments.CommentsListingFragment;
import com.rackluxury.ferrari.reddit.fragments.FollowedUsersListingFragment;
import com.rackluxury.ferrari.reddit.fragments.InboxFragment;
import com.rackluxury.ferrari.reddit.fragments.MultiRedditListingFragment;
import com.rackluxury.ferrari.reddit.fragments.PostFragment;
import com.rackluxury.ferrari.reddit.fragments.SidebarFragment;
import com.rackluxury.ferrari.reddit.fragments.SubredditListingFragment;
import com.rackluxury.ferrari.reddit.fragments.SubscribedSubredditsListingFragment;
import com.rackluxury.ferrari.reddit.fragments.UserListingFragment;
import com.rackluxury.ferrari.reddit.fragments.ViewImgurImageFragment;
import com.rackluxury.ferrari.reddit.fragments.ViewImgurVideoFragment;
import com.rackluxury.ferrari.reddit.fragments.ViewPostDetailFragment;
import com.rackluxury.ferrari.reddit.fragments.ViewRPANBroadcastFragment;
import com.rackluxury.ferrari.reddit.fragments.ViewRedditGalleryImageOrGifFragment;
import com.rackluxury.ferrari.reddit.fragments.ViewRedditGalleryVideoFragment;
import com.rackluxury.ferrari.reddit.services.RedditDownloadMediaService;
import com.rackluxury.ferrari.reddit.services.RedditDownloadRedditVideoService;
import com.rackluxury.ferrari.reddit.services.MaterialYouService;
import com.rackluxury.ferrari.reddit.services.SubmitPostService;
import com.rackluxury.ferrari.reddit.settings.AdvancedPreferenceFragment;
import com.rackluxury.ferrari.reddit.settings.CrashReportsFragment;
import com.rackluxury.ferrari.reddit.settings.CustomizeBottomAppBarFragment;
import com.rackluxury.ferrari.reddit.settings.CustomizeMainPageTabsFragment;
import com.rackluxury.ferrari.reddit.settings.DownloadLocationPreferenceFragment;
import com.rackluxury.ferrari.reddit.settings.GesturesAndButtonsPreferenceFragment;
import com.rackluxury.ferrari.reddit.settings.MainPreferenceFragment;
import com.rackluxury.ferrari.reddit.settings.MiscellaneousPreferenceFragment;
import com.rackluxury.ferrari.reddit.settings.NotificationPreferenceFragment;
import com.rackluxury.ferrari.reddit.settings.NsfwAndSpoilerFragment;
import com.rackluxury.ferrari.reddit.settings.PostHistoryFragment;
import com.rackluxury.ferrari.reddit.settings.SecurityPreferenceFragment;
import com.rackluxury.ferrari.reddit.settings.ThemePreferenceFragment;
import com.rackluxury.ferrari.reddit.settings.TranslationFragment;
import com.rackluxury.ferrari.reddit.settings.VideoPreferenceFragment;

@Singleton
@Component(modules = com.rackluxury.ferrari.reddit.AppModule.class)
public interface AppComponent {
    void inject(RedditMainActivity redditMainActivity);

    void inject(RedditLoginActivity redditLoginActivity);

    void inject(PostFragment postFragment);

    void inject(SubredditListingFragment subredditListingFragment);

    void inject(UserListingFragment userListingFragment);

    void inject(RedditViewPostDetailActivity redditViewPostDetailActivity);

    void inject(RedditViewSubredditDetailActivity redditViewSubredditDetailActivity);

    void inject(RedditViewUserDetailActivity redditViewUserDetailActivity);

    void inject(RedditCommentActivity redditCommentActivity);

    void inject(RedditSubscribedThingListingActivity redditSubscribedThingListingActivity);

    void inject(RedditPostTextActivity redditPostTextActivity);

    void inject(SubscribedSubredditsListingFragment subscribedSubredditsListingFragment);

    void inject(RedditPostLinkActivity redditPostLinkActivity);

    void inject(RedditPostImageActivity redditPostImageActivity);

    void inject(RedditPostVideoActivity redditPostVideoActivity);

    void inject(FlairBottomSheetFragment flairBottomSheetFragment);

    void inject(RedditRulesActivity redditRulesActivity);

    void inject(CommentsListingFragment commentsListingFragment);

    void inject(SubmitPostService submitPostService);

    void inject(RedditFilteredPostsActivity redditFilteredPostsActivity);

    void inject(SearchResultActivity searchResultActivity);

    void inject(RedditSearchSubredditsResultActivity redditSearchSubredditsResultActivity);

    void inject(FollowedUsersListingFragment followedUsersListingFragment);

    void inject(RedditSubredditSelectionActivity redditSubredditSelectionActivity);

    void inject(RedditEditPostActivity redditEditPostActivity);

    void inject(RedditEditCommentActivity redditEditCommentActivity);

    void inject(RedditAccountPostsActivity redditAccountPostsActivity);

    void inject(com.rackluxury.ferrari.reddit.PullNotificationWorker pullNotificationWorker);

    void inject(RedditInboxActivity redditInboxActivity);

    void inject(NotificationPreferenceFragment notificationPreferenceFragment);

    void inject(RedditLinkResolverActivity redditLinkResolverActivity);

    void inject(RedditSearchActivity redditSearchActivity);

    void inject(RedditSettingsActivity redditSettingsActivity);

    void inject(MainPreferenceFragment mainPreferenceFragment);

    void inject(AccountSavedThingActivity accountSavedThingActivity);

    void inject(ViewImageOrGifActivity viewGIFActivity);

    void inject(RedditViewMultiRedditDetailActivity redditViewMultiRedditDetailActivity);

    void inject(RedditViewVideoActivity redditViewVideoActivity);

    void inject(GesturesAndButtonsPreferenceFragment gesturesAndButtonsPreferenceFragment);

    void inject(CreateMultiRedditActivity createMultiRedditActivity);

    void inject(RedditSubredditMultiselectionActivity redditSubredditMultiselectionActivity);

    void inject(ThemePreferenceFragment themePreferenceFragment);

    void inject(RedditCustomizeThemeActivity redditCustomizeThemeActivity);

    void inject(CustomThemeListingActivity customThemeListingActivity);

    void inject(SidebarFragment sidebarFragment);

    void inject(AdvancedPreferenceFragment advancedPreferenceFragment);

    void inject(RedditCustomThemePreviewActivity redditCustomThemePreviewActivity);

    void inject(RedditEditMultiRedditActivity redditEditMultiRedditActivity);

    void inject(SelectedSubredditsAndUsersActivity selectedSubredditsAndUsersActivity);

    void inject(ReportActivity reportActivity);

    void inject(RedditViewImgurMediaActivity redditViewImgurMediaActivity);

    void inject(ViewImgurVideoFragment viewImgurVideoFragment);

    void inject(RedditDownloadRedditVideoService redditDownloadRedditVideoService);

    void inject(MultiRedditListingFragment multiRedditListingFragment);

    void inject(InboxFragment inboxFragment);

    void inject(RedditViewPrivateMessagesActivity redditViewPrivateMessagesActivity);

    void inject(SendPrivateMessageActivity sendPrivateMessageActivity);

    void inject(VideoPreferenceFragment videoPreferenceFragment);

    void inject(RedditViewRedditGalleryActivity redditViewRedditGalleryActivity);

    void inject(ViewRedditGalleryVideoFragment viewRedditGalleryVideoFragment);

    void inject(CustomizeMainPageTabsFragment customizeMainPageTabsFragment);

    void inject(RedditDownloadMediaService redditDownloadMediaService);

    void inject(DownloadLocationPreferenceFragment downloadLocationPreferenceFragment);

    void inject(RedditSubmitCrosspostActivity redditSubmitCrosspostActivity);

    void inject(FullMarkdownActivity fullMarkdownActivity);

    void inject(SelectUserFlairActivity selectUserFlairActivity);

    void inject(SecurityPreferenceFragment securityPreferenceFragment);

    void inject(NsfwAndSpoilerFragment nsfwAndSpoilerFragment);

    void inject(CustomizeBottomAppBarFragment customizeBottomAppBarFragment);

    void inject(GiveAwardActivity giveAwardActivity);

    void inject(TranslationFragment translationFragment);

    void inject(FetchRandomSubredditOrPostActivity fetchRandomSubredditOrPostActivity);

    void inject(MiscellaneousPreferenceFragment miscellaneousPreferenceFragment);

    void inject(CustomizePostFilterActivity customizePostFilterActivity);

    void inject(PostHistoryFragment postHistoryFragment);

    void inject(RedditPostFilterPreferenceActivity redditPostFilterPreferenceActivity);

    void inject(PostFilterUsageListingActivity postFilterUsageListingActivity);

    void inject(RedditSearchUsersResultActivity redditSearchUsersResultActivity);

    void inject(MultiredditSelectionActivity multiredditSelectionActivity);

    void inject(ViewImgurImageFragment viewImgurImageFragment);

    void inject(ViewRedditGalleryImageOrGifFragment viewRedditGalleryImageOrGifFragment);

    void inject(ViewPostDetailFragment viewPostDetailFragment);

    void inject(SuicidePreventionActivity suicidePreventionActivity);

    void inject(RedditWebViewActivity redditWebViewActivity);

    void inject(CrashReportsFragment crashReportsFragment);

    void inject(AnonymousSubscriptionsActivity anonymousSubscriptionsActivity);

    void inject(RedditLockScreenActivity redditLockScreenActivity);

    void inject(MaterialYouService materialYouService);

    void inject(RedditRPANActivity redditRpanActivity);

    void inject(ViewRPANBroadcastFragment viewRPANBroadcastFragment);

    void inject(RedditPostGalleryActivity redditPostGalleryActivity);

    void inject(RedditTrendingActivity redditTrendingActivity);
}
