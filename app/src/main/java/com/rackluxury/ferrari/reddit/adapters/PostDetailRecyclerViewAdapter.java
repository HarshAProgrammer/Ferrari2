package com.rackluxury.ferrari.reddit.adapters;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.google.android.exoplayer2.metadata.Metadata;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.text.Cue;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
import com.google.android.exoplayer2.ui.PlayerView;
import com.libRG.CustomTextView;

import org.commonmark.ext.gfm.tables.TableBlock;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Executor;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import im.ene.toro.CacheManager;
import im.ene.toro.ToroPlayer;
import im.ene.toro.ToroUtil;
import im.ene.toro.exoplayer.ExoCreator;
import im.ene.toro.exoplayer.ExoPlayerViewHelper;
import im.ene.toro.exoplayer.Playable;
import im.ene.toro.media.PlaybackInfo;
import im.ene.toro.widget.Container;
import io.noties.markwon.AbstractMarkwonPlugin;
import io.noties.markwon.Markwon;
import io.noties.markwon.MarkwonConfiguration;
import io.noties.markwon.core.MarkwonTheme;
import io.noties.markwon.ext.strikethrough.StrikethroughPlugin;
import io.noties.markwon.html.HtmlPlugin;
import io.noties.markwon.linkify.LinkifyPlugin;
import io.noties.markwon.movement.MovementMethodPlugin;
import io.noties.markwon.recycler.MarkwonAdapter;
import io.noties.markwon.recycler.table.TableEntry;
import io.noties.markwon.recycler.table.TableEntryPlugin;
import jp.wasabeef.glide.transformations.BlurTransformation;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;
import me.saket.bettermovementmethod.BetterLinkMovementMethod;
import com.rackluxury.ferrari.reddit.FetchGfycatOrRedgifsVideoLinks;
import com.rackluxury.ferrari.R;
import com.rackluxury.ferrari.reddit.RedditDataRoomDatabase;
import com.rackluxury.ferrari.reddit.SaveThing;
import com.rackluxury.ferrari.reddit.VoteThing;
import com.rackluxury.ferrari.reddit.activities.RedditCommentActivity;
import com.rackluxury.ferrari.reddit.activities.RedditFilteredPostsActivity;
import com.rackluxury.ferrari.reddit.activities.RedditLinkResolverActivity;
import com.rackluxury.ferrari.reddit.activities.RedditViewPostDetailActivity;
import com.rackluxury.ferrari.reddit.activities.RedditViewRedditGalleryActivity;
import com.rackluxury.ferrari.reddit.activities.RedditViewSubredditDetailActivity;
import com.rackluxury.ferrari.reddit.activities.RedditViewVideoActivity;
import com.rackluxury.ferrari.reddit.activities.ViewImageOrGifActivity;
import com.rackluxury.ferrari.reddit.activities.RedditViewUserDetailActivity;
import com.rackluxury.ferrari.reddit.asynctasks.LoadSubredditIcon;
import com.rackluxury.ferrari.reddit.asynctasks.LoadUserData;
import com.rackluxury.ferrari.reddit.bottomsheetfragments.CopyTextBottomSheetFragment;
import com.rackluxury.ferrari.reddit.bottomsheetfragments.ShareLinkBottomSheetFragment;
import com.rackluxury.ferrari.reddit.bottomsheetfragments.UrlMenuBottomSheetFragment;
import com.rackluxury.ferrari.reddit.comment.Comment;
import com.rackluxury.ferrari.reddit.customtheme.CustomThemeWrapper;
import com.rackluxury.ferrari.reddit.customviews.AspectRatioGifImageView;
import com.rackluxury.ferrari.reddit.customviews.MarkwonLinearLayoutManager;
import com.rackluxury.ferrari.reddit.fragments.ViewPostDetailFragment;
import com.rackluxury.ferrari.reddit.post.Post;
import com.rackluxury.ferrari.reddit.post.PostDataSource;
import com.rackluxury.ferrari.reddit.utils.APIUtils;
import com.rackluxury.ferrari.reddit.utils.SharedPreferencesUtils;
import com.rackluxury.ferrari.reddit.utils.Utils;
import pl.droidsonroids.gif.GifImageView;
import retrofit2.Retrofit;

import static com.rackluxury.ferrari.reddit.activities.RedditCommentActivity.WRITE_COMMENT_REQUEST_CODE;

public class PostDetailRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements CacheManager {
    private static final int VIEW_TYPE_POST_DETAIL_VIDEO_AUTOPLAY = 1;
    private static final int VIEW_TYPE_POST_DETAIL_VIDEO_AND_GIF_PREVIEW = 2;
    private static final int VIEW_TYPE_POST_DETAIL_IMAGE = 3;
    private static final int VIEW_TYPE_POST_DETAIL_GIF_AUTOPLAY = 4;
    private static final int VIEW_TYPE_POST_DETAIL_LINK = 5;
    private static final int VIEW_TYPE_POST_DETAIL_NO_PREVIEW_LINK = 6;
    private static final int VIEW_TYPE_POST_DETAIL_GALLERY = 7;
    private static final int VIEW_TYPE_POST_DETAIL_TEXT_TYPE = 8;

    private AppCompatActivity mActivity;
    private ViewPostDetailFragment mFragment;
    private Executor mExecutor;
    private Retrofit mRetrofit;
    private Retrofit mOauthRetrofit;
    private Retrofit mGfycatRetrofit;
    private Retrofit mRedgifsRetrofit;
    private RedditDataRoomDatabase mRedditDataRoomDatabase;
    private RequestManager mGlide;
    private Markwon mPostDetailMarkwon;
    private final MarkwonAdapter mMarkwonAdapter;
    private int mImageViewWidth;
    private String mAccessToken;
    private String mAccountName;
    private Post mPost;
    private ArrayList<Comment> mVisibleComments;
    private String mSubredditNamePrefixed;
    private Locale mLocale;
    private String mSingleCommentId;
    private boolean mIsSingleCommentThreadMode;
    private boolean mNeedBlurNsfw;
    private boolean mDoNotBlurNsfwInNsfwSubreddits;
    private boolean mNeedBlurSpoiler;
    private boolean mVoteButtonsOnTheRight;
    private boolean mShowElapsedTime;
    private String mTimeFormatPattern;
    private boolean mExpandChildren;
    private boolean mCommentToolbarHidden;
    private boolean mCommentToolbarHideOnClick;
    private boolean mSwapTapAndLong;
    private boolean mShowCommentDivider;
    private boolean mShowAbsoluteNumberOfVotes;
    private boolean mAutoplay = false;
    private boolean mAutoplayNsfwVideos;
    private boolean mMuteAutoplayingVideos;
    private boolean mFullyCollapseComment;
    private double mStartAutoplayVisibleAreaOffset;
    private boolean mMuteNSFWVideo;
    private boolean mAutomaticallyTryRedgifs;
    private boolean mDataSavingMode;
    private boolean mDisableImagePreview;
    private boolean mOnlyDisablePreviewInVideoAndGifPosts;
    private PostDetailRecyclerViewAdapterCallback mPostDetailRecyclerViewAdapterCallback;
    private boolean isInitiallyLoading;
    private boolean isInitiallyLoadingFailed;
    private boolean mHasMoreComments;
    private boolean loadMoreCommentsFailed;

    private int mColorAccent;
    private int mCardViewColor;
    private int mSecondaryTextColor;
    private int mPostTitleColor;
    private int mPrimaryTextColor;
    private int mPostTypeBackgroundColor;
    private int mPostTypeTextColor;
    private int mSubredditColor;
    private int mUsernameColor;
    private int mAuthorFlairTextColor;
    private int mSpoilerBackgroundColor;
    private int mSpoilerTextColor;
    private int mFlairBackgroundColor;
    private int mFlairTextColor;
    private int mNSFWBackgroundColor;
    private int mNSFWTextColor;
    private int mArchivedTintColor;
    private int mLockedTintColor;
    private int mCrosspostTintColor;
    private int mUpvoteRatioTintColor;
    private int mNoPreviewPostTypeBackgroundColor;
    private int mNoPreviewPostTypeIconTint;
    private int mUpvotedColor;
    private int mDownvotedColor;
    private int mVoteAndReplyUnavailableVoteButtonColor;
    private int mPostIconAndInfoColor;
    private int mCommentIconAndInfoColor;

    private Drawable mCommentIcon;
    private float mScale;
    private ExoCreator mExoCreator;

    public PostDetailRecyclerViewAdapter(AppCompatActivity activity, ViewPostDetailFragment fragment, Executor executor, CustomThemeWrapper customThemeWrapper,
                                         Retrofit retrofit, Retrofit oauthRetrofit, Retrofit gfycatRetrofit,
                                         Retrofit redgifsRetrofit,
                                         RedditDataRoomDatabase redditDataRoomDatabase, RequestManager glide,
                                         int imageViewWidth, String accessToken, String accountName,
                                         Post post, Locale locale, String singleCommentId,
                                         boolean isSingleCommentThreadMode,
                                         SharedPreferences sharedPreferences,
                                         SharedPreferences nsfwAndSpoilerSharedPreferences, ExoCreator exoCreator,
                                         PostDetailRecyclerViewAdapterCallback postDetailRecyclerViewAdapterCallback) {
        mActivity = activity;
        mFragment = fragment;
        mExecutor = executor;
        mRetrofit = retrofit;
        mOauthRetrofit = oauthRetrofit;
        mGfycatRetrofit = gfycatRetrofit;
        mRedgifsRetrofit = redgifsRetrofit;
        mRedditDataRoomDatabase = redditDataRoomDatabase;
        mGlide = glide;
        mSecondaryTextColor = customThemeWrapper.getSecondaryTextColor();
        int markdownColor = customThemeWrapper.getPostContentColor();
        int postSpoilerBackgroundColor = markdownColor | 0xFF000000;
        int linkColor = customThemeWrapper.getLinkColor();
        mPostDetailMarkwon = Markwon.builder(mActivity)
                .usePlugin(HtmlPlugin.create())
                .usePlugin(new AbstractMarkwonPlugin() {
                    @NonNull
                    @Override
                    public String processMarkdown(@NonNull String markdown) {
                        StringBuilder markdownStringBuilder = new StringBuilder(markdown);
                        Pattern spoilerPattern = Pattern.compile(">![\\S\\s]*?!<");
                        Matcher matcher = spoilerPattern.matcher(markdownStringBuilder);
                        while (matcher.find()) {
                            markdownStringBuilder.replace(matcher.start(), matcher.start() + 1, "&gt;");
                        }
                        return super.processMarkdown(markdownStringBuilder.toString());
                    }

                    @Override
                    public void afterSetText(@NonNull TextView textView) {
                        textView.setHighlightColor(Color.TRANSPARENT);
                        SpannableStringBuilder markdownStringBuilder = new SpannableStringBuilder(textView.getText().toString());
                        Pattern spoilerPattern = Pattern.compile(">![\\S\\s]*?!<");
                        Matcher matcher = spoilerPattern.matcher(markdownStringBuilder);
                        int start = 0;
                        boolean find = false;
                        while (matcher.find(start)) {
                            if (markdownStringBuilder.length() < 4
                                    || matcher.start() < 0
                                    || matcher.end() > markdownStringBuilder.length()) {
                                break;
                            }
                            find = true;
                            markdownStringBuilder.delete(matcher.end() - 2, matcher.end());
                            markdownStringBuilder.delete(matcher.start(), matcher.start() + 2);
                            ClickableSpan clickableSpan = new ClickableSpan() {
                                private boolean isShowing = false;
                                @Override
                                public void updateDrawState(@NonNull TextPaint ds) {
                                    if (isShowing) {
                                        super.updateDrawState(ds);
                                        ds.setColor(markdownColor);
                                    } else {
                                        ds.bgColor = postSpoilerBackgroundColor;
                                        ds.setColor(markdownColor);
                                    }
                                    ds.setUnderlineText(false);
                                }

                                @Override
                                public void onClick(@NonNull View view) {
                                    isShowing = !isShowing;
                                    view.invalidate();
                                }
                            };
                            markdownStringBuilder.setSpan(clickableSpan, matcher.start(), matcher.end() - 4, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                            start = matcher.end() - 4;
                        }
                        if (find) {
                            textView.setText(markdownStringBuilder);
                        }
                    }

                    @Override
                    public void beforeSetText(@NonNull TextView textView, @NonNull Spanned markdown) {
                        textView.setTextColor(markdownColor);
                        textView.setOnLongClickListener(view -> {
                            if (textView.getSelectionStart() == -1 && textView.getSelectionEnd() == -1) {
                                Bundle bundle = new Bundle();
                                bundle.putString(CopyTextBottomSheetFragment.EXTRA_RAW_TEXT, mPost.getSelfTextPlain());
                                bundle.putString(CopyTextBottomSheetFragment.EXTRA_MARKDOWN, mPost.getSelfText());
                                CopyTextBottomSheetFragment copyTextBottomSheetFragment = new CopyTextBottomSheetFragment();
                                copyTextBottomSheetFragment.setArguments(bundle);
                                copyTextBottomSheetFragment.show(mActivity.getSupportFragmentManager(), copyTextBottomSheetFragment.getTag());
                            }
                            return true;
                        });
                    }

                    @Override
                    public void configureConfiguration(@NonNull MarkwonConfiguration.Builder builder) {
                        builder.linkResolver((view, link) -> {
                            Intent intent = new Intent(mActivity, RedditLinkResolverActivity.class);
                            Uri uri = Uri.parse(link);
                            intent.setData(uri);
                            intent.putExtra(RedditLinkResolverActivity.EXTRA_IS_NSFW, mPost.isNSFW());
                            mActivity.startActivity(intent);
                        });
                    }

                    @Override
                    public void configureTheme(@NonNull MarkwonTheme.Builder builder) {
                        builder.linkColor(linkColor);
                    }
                })
                .usePlugin(StrikethroughPlugin.create())
                .usePlugin(MovementMethodPlugin.create(BetterLinkMovementMethod.linkify(Linkify.WEB_URLS, activity).setOnLinkLongClickListener((textView, url) -> {
                    if (activity != null && !activity.isDestroyed() && !activity.isFinishing()) {
                        UrlMenuBottomSheetFragment urlMenuBottomSheetFragment = new UrlMenuBottomSheetFragment();
                        Bundle bundle = new Bundle();
                        bundle.putString(UrlMenuBottomSheetFragment.EXTRA_URL, url);
                        urlMenuBottomSheetFragment.setArguments(bundle);
                        urlMenuBottomSheetFragment.show(activity.getSupportFragmentManager(), urlMenuBottomSheetFragment.getTag());
                    }
                    return true;
                })))
                .usePlugin(LinkifyPlugin.create(Linkify.WEB_URLS))
                .usePlugin(TableEntryPlugin.create(mActivity))
                .build();
        mMarkwonAdapter = MarkwonAdapter.builder(R.layout.adapter_default_entry, R.id.text)
                .include(TableBlock.class, TableEntry.create(builder -> builder
                        .tableLayout(R.layout.adapter_table_block, R.id.table_layout)
                        .textLayoutIsRoot(R.layout.view_table_entry_cell)))
                .build();
        mImageViewWidth = imageViewWidth;
        mAccessToken = accessToken;
        mAccountName = accountName;
        mPost = post;
        mVisibleComments = new ArrayList<>();
        mSubredditNamePrefixed = post.getSubredditNamePrefixed();
        mLocale = locale;
        mSingleCommentId = singleCommentId;
        mIsSingleCommentThreadMode = isSingleCommentThreadMode;

        mNeedBlurNsfw = nsfwAndSpoilerSharedPreferences.getBoolean((mAccountName == null ? "" : mAccountName) + SharedPreferencesUtils.BLUR_NSFW_BASE, true);
        mDoNotBlurNsfwInNsfwSubreddits = nsfwAndSpoilerSharedPreferences.getBoolean((mAccountName == null ? "" : mAccountName) + SharedPreferencesUtils.DO_NOT_BLUR_NSFW_IN_NSFW_SUBREDDITS, false);
        mNeedBlurSpoiler = nsfwAndSpoilerSharedPreferences.getBoolean((mAccountName == null ? "" : mAccountName) + SharedPreferencesUtils.BLUR_SPOILER_BASE, false);
        mVoteButtonsOnTheRight = sharedPreferences.getBoolean(SharedPreferencesUtils.VOTE_BUTTONS_ON_THE_RIGHT_KEY, false);
        mShowElapsedTime = sharedPreferences.getBoolean(SharedPreferencesUtils.SHOW_ELAPSED_TIME_KEY, false);
        mTimeFormatPattern = sharedPreferences.getString(SharedPreferencesUtils.TIME_FORMAT_KEY, SharedPreferencesUtils.TIME_FORMAT_DEFAULT_VALUE);
        mExpandChildren = !sharedPreferences.getBoolean(SharedPreferencesUtils.SHOW_TOP_LEVEL_COMMENTS_FIRST, false);
        mCommentToolbarHidden = sharedPreferences.getBoolean(SharedPreferencesUtils.COMMENT_TOOLBAR_HIDDEN, false);
        mCommentToolbarHideOnClick = sharedPreferences.getBoolean(SharedPreferencesUtils.COMMENT_TOOLBAR_HIDE_ON_CLICK, true);
        mSwapTapAndLong = sharedPreferences.getBoolean(SharedPreferencesUtils.SWAP_TAP_AND_LONG_COMMENTS, false);
        mShowCommentDivider = sharedPreferences.getBoolean(SharedPreferencesUtils.SHOW_COMMENT_DIVIDER, false);
        mShowAbsoluteNumberOfVotes = sharedPreferences.getBoolean(SharedPreferencesUtils.SHOW_ABSOLUTE_NUMBER_OF_VOTES, true);

        String autoplayString = sharedPreferences.getString(SharedPreferencesUtils.VIDEO_AUTOPLAY, SharedPreferencesUtils.VIDEO_AUTOPLAY_VALUE_NEVER);
        int networkType = Utils.getConnectedNetwork(activity);
        if (autoplayString.equals(SharedPreferencesUtils.VIDEO_AUTOPLAY_VALUE_ALWAYS_ON)) {
            mAutoplay = true;
        } else if (autoplayString.equals(SharedPreferencesUtils.VIDEO_AUTOPLAY_VALUE_ON_WIFI)) {
            mAutoplay = networkType == Utils.NETWORK_TYPE_WIFI;
        }
        mAutoplayNsfwVideos = sharedPreferences.getBoolean(SharedPreferencesUtils.AUTOPLAY_NSFW_VIDEOS, true);
        mMuteAutoplayingVideos = sharedPreferences.getBoolean(SharedPreferencesUtils.MUTE_AUTOPLAYING_VIDEOS, true);
        mFullyCollapseComment = sharedPreferences.getBoolean(SharedPreferencesUtils.FULLY_COLLAPSE_COMMENT, false);

        Resources resources = activity.getResources();
        mStartAutoplayVisibleAreaOffset = resources.getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT ?
                sharedPreferences.getInt(SharedPreferencesUtils.START_AUTOPLAY_VISIBLE_AREA_OFFSET_PORTRAIT, 75) / 100.0 :
                sharedPreferences.getInt(SharedPreferencesUtils.START_AUTOPLAY_VISIBLE_AREA_OFFSET_LANDSCAPE, 50) / 100.0;

        mMuteNSFWVideo = sharedPreferences.getBoolean(SharedPreferencesUtils.MUTE_NSFW_VIDEO, false);
        mAutomaticallyTryRedgifs = sharedPreferences.getBoolean(SharedPreferencesUtils.AUTOMATICALLY_TRY_REDGIFS, true);

        String dataSavingModeString = sharedPreferences.getString(SharedPreferencesUtils.DATA_SAVING_MODE, SharedPreferencesUtils.DATA_SAVING_MODE_OFF);
        if (dataSavingModeString.equals(SharedPreferencesUtils.DATA_SAVING_MODE_ALWAYS)) {
            mDataSavingMode = true;
        } else if (dataSavingModeString.equals(SharedPreferencesUtils.DATA_SAVING_MODE_ONLY_ON_CELLULAR_DATA)) {
            mDataSavingMode = networkType == Utils.NETWORK_TYPE_CELLULAR;
        }
        mDisableImagePreview = sharedPreferences.getBoolean(SharedPreferencesUtils.DISABLE_IMAGE_PREVIEW, false);
        mOnlyDisablePreviewInVideoAndGifPosts = sharedPreferences.getBoolean(SharedPreferencesUtils.ONLY_DISABLE_PREVIEW_IN_VIDEO_AND_GIF_POSTS, false);

        mPostDetailRecyclerViewAdapterCallback = postDetailRecyclerViewAdapterCallback;
        isInitiallyLoading = true;
        isInitiallyLoadingFailed = false;
        mHasMoreComments = false;
        loadMoreCommentsFailed = false;
        mScale = resources.getDisplayMetrics().density;

        mColorAccent = customThemeWrapper.getColorAccent();
        mCardViewColor = customThemeWrapper.getCardViewBackgroundColor();
        mPostTitleColor = customThemeWrapper.getPostTitleColor();
        mPrimaryTextColor = customThemeWrapper.getPrimaryTextColor();
        mPostTypeBackgroundColor = customThemeWrapper.getPostTypeBackgroundColor();
        mPostTypeTextColor = customThemeWrapper.getPostTypeTextColor();
        mAuthorFlairTextColor = customThemeWrapper.getAuthorFlairTextColor();
        mSpoilerBackgroundColor = customThemeWrapper.getSpoilerBackgroundColor();
        mSpoilerTextColor = customThemeWrapper.getSpoilerTextColor();
        mNSFWBackgroundColor = customThemeWrapper.getNsfwBackgroundColor();
        mNSFWTextColor = customThemeWrapper.getNsfwTextColor();
        mArchivedTintColor = customThemeWrapper.getArchivedIconTint();
        mLockedTintColor = customThemeWrapper.getLockedIconTint();
        mCrosspostTintColor = customThemeWrapper.getCrosspostIconTint();
        mUpvoteRatioTintColor = customThemeWrapper.getUpvoteRatioIconTint();
        mNoPreviewPostTypeBackgroundColor = customThemeWrapper.getNoPreviewPostTypeBackgroundColor();
        mNoPreviewPostTypeIconTint = customThemeWrapper.getNoPreviewPostTypeIconTint();
        mFlairBackgroundColor = customThemeWrapper.getFlairBackgroundColor();
        mFlairTextColor = customThemeWrapper.getFlairTextColor();
        mSubredditColor = customThemeWrapper.getSubreddit();
        mUsernameColor = customThemeWrapper.getUsername();
        mUpvotedColor = customThemeWrapper.getUpvoted();
        mDownvotedColor = customThemeWrapper.getDownvoted();
        mVoteAndReplyUnavailableVoteButtonColor = customThemeWrapper.getVoteAndReplyUnavailableButtonColor();
        mPostIconAndInfoColor = customThemeWrapper.getPostIconAndInfoColor();
        mCommentIconAndInfoColor = customThemeWrapper.getCommentIconAndInfoColor();

        mCommentIcon = activity.getDrawable(R.drawable.ic_comment_grey_24dp);
        if (mCommentIcon != null) {
            DrawableCompat.setTint(mCommentIcon, mPostIconAndInfoColor);
        }

        mExoCreator = exoCreator;
    }

    @Override
    public int getItemViewType(int position) {
        switch (mPost.getPostType()) {
            case Post.VIDEO_TYPE:
                if (mAutoplay) {
                    if (!mAutoplayNsfwVideos && mPost.isNSFW()) {
                        return VIEW_TYPE_POST_DETAIL_VIDEO_AND_GIF_PREVIEW;
                    }
                    return VIEW_TYPE_POST_DETAIL_VIDEO_AUTOPLAY;
                } else {
                    return VIEW_TYPE_POST_DETAIL_VIDEO_AND_GIF_PREVIEW;
                }
            case Post.GIF_TYPE:
                if (mAutoplay) {
                    if (!mAutoplayNsfwVideos && mPost.isNSFW()) {
                        return VIEW_TYPE_POST_DETAIL_VIDEO_AND_GIF_PREVIEW;
                    }
                    return VIEW_TYPE_POST_DETAIL_GIF_AUTOPLAY;
                } else {
                    return VIEW_TYPE_POST_DETAIL_VIDEO_AND_GIF_PREVIEW;
                }
            case Post.IMAGE_TYPE:
                return VIEW_TYPE_POST_DETAIL_IMAGE;
            case Post.LINK_TYPE:
                return VIEW_TYPE_POST_DETAIL_LINK;
            case Post.NO_PREVIEW_LINK_TYPE:
                return VIEW_TYPE_POST_DETAIL_NO_PREVIEW_LINK;
            case Post.GALLERY_TYPE:
                return VIEW_TYPE_POST_DETAIL_GALLERY;
            default:
                return VIEW_TYPE_POST_DETAIL_TEXT_TYPE;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case VIEW_TYPE_POST_DETAIL_VIDEO_AUTOPLAY:
                if (mDataSavingMode) {
                    if (mDisableImagePreview || mOnlyDisablePreviewInVideoAndGifPosts) {
                        return new PostDetailNoPreviewViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_post_detail_no_preview, parent, false));
                    }
                    return new PostDetailVideoAndGifPreviewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_post_detail_video_and_gif_preview, parent, false));
                }
                return new PostDetailVideoAutoplayViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_post_detail_video_autoplay, parent, false));
            case VIEW_TYPE_POST_DETAIL_VIDEO_AND_GIF_PREVIEW:
                if (mDataSavingMode && (mDisableImagePreview || mOnlyDisablePreviewInVideoAndGifPosts)) {
                    return new PostDetailNoPreviewViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_post_detail_no_preview, parent, false));
                }
                return new PostDetailVideoAndGifPreviewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_post_detail_video_and_gif_preview, parent, false));
            case VIEW_TYPE_POST_DETAIL_IMAGE:
                if (mDataSavingMode && mDisableImagePreview) {
                    return new PostDetailNoPreviewViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_post_detail_no_preview, parent, false));
                }
                return new PostDetailImageAndGifAutoplayViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_post_detail_image_and_gif_autoplay, parent, false));
            case VIEW_TYPE_POST_DETAIL_GIF_AUTOPLAY:
                if (mDataSavingMode && (mDisableImagePreview || mOnlyDisablePreviewInVideoAndGifPosts)) {
                    return new PostDetailNoPreviewViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_post_detail_no_preview, parent, false));
                }
                return new PostDetailImageAndGifAutoplayViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_post_detail_image_and_gif_autoplay, parent, false));
            case VIEW_TYPE_POST_DETAIL_LINK:
                if (mDataSavingMode && mDisableImagePreview) {
                    return new PostDetailNoPreviewViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_post_detail_no_preview, parent, false));
                }
                return new PostDetailLinkViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_post_detail_link, parent, false));
            case VIEW_TYPE_POST_DETAIL_NO_PREVIEW_LINK:
                return new PostDetailNoPreviewViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_post_detail_no_preview, parent, false));
            case VIEW_TYPE_POST_DETAIL_GALLERY:
                return new PostDetailGalleryViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_post_detail_gallery, parent, false));
            default:
                return new PostDetailTextViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_post_detail_text, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof PostDetailBaseViewHolder) {
            ((PostDetailBaseViewHolder) holder).mTitleTextView.setText(mPost.getTitle());
            if (mPost.getSubredditNamePrefixed().startsWith("u/")) {
                if (mPost.getAuthorIconUrl() == null) {
                    String authorName = mPost.getAuthor().equals("[deleted]") ? mPost.getSubredditNamePrefixed().substring(2) : mPost.getAuthor();
                    LoadUserData.loadUserData(mExecutor, new Handler(), mRedditDataRoomDatabase, authorName, mOauthRetrofit, iconImageUrl -> {
                        if (mActivity != null && getItemCount() > 0) {
                            if (iconImageUrl == null || iconImageUrl.equals("")) {
                                mGlide.load(R.drawable.subreddit_default_icon)
                                        .apply(RequestOptions.bitmapTransform(new RoundedCornersTransformation(72, 0)))
                                        .into(((PostDetailBaseViewHolder) holder).mIconGifImageView);
                            } else {
                                mGlide.load(iconImageUrl)
                                        .apply(RequestOptions.bitmapTransform(new RoundedCornersTransformation(72, 0)))
                                        .error(mGlide.load(R.drawable.subreddit_default_icon)
                                                .apply(RequestOptions.bitmapTransform(new RoundedCornersTransformation(72, 0))))
                                        .into(((PostDetailBaseViewHolder) holder).mIconGifImageView);
                            }

                            if (holder.getBindingAdapterPosition() >= 0) {
                                mPost.setAuthorIconUrl(iconImageUrl);
                            }
                        }
                    });
                } else if (!mPost.getAuthorIconUrl().equals("")) {
                    mGlide.load(mPost.getAuthorIconUrl())
                            .apply(RequestOptions.bitmapTransform(new RoundedCornersTransformation(72, 0)))
                            .error(mGlide.load(R.drawable.subreddit_default_icon)
                                    .apply(RequestOptions.bitmapTransform(new RoundedCornersTransformation(72, 0))))
                            .into(((PostDetailBaseViewHolder) holder).mIconGifImageView);
                } else {
                    mGlide.load(R.drawable.subreddit_default_icon)
                            .apply(RequestOptions.bitmapTransform(new RoundedCornersTransformation(72, 0)))
                            .into(((PostDetailBaseViewHolder) holder).mIconGifImageView);
                }
            } else {
                if (mPost.getSubredditIconUrl() == null) {
                    LoadSubredditIcon.loadSubredditIcon(mExecutor, new Handler(),
                            mRedditDataRoomDatabase, mPost.getSubredditNamePrefixed().substring(2),
                            mRetrofit, iconImageUrl -> {
                                if (iconImageUrl == null || iconImageUrl.equals("")) {
                                    mGlide.load(R.drawable.subreddit_default_icon)
                                            .apply(RequestOptions.bitmapTransform(new RoundedCornersTransformation(72, 0)))
                                            .into(((PostDetailBaseViewHolder) holder).mIconGifImageView);
                                } else {
                                    mGlide.load(iconImageUrl)
                                            .apply(RequestOptions.bitmapTransform(new RoundedCornersTransformation(72, 0)))
                                            .error(mGlide.load(R.drawable.subreddit_default_icon)
                                                    .apply(RequestOptions.bitmapTransform(new RoundedCornersTransformation(72, 0))))
                                            .into(((PostDetailBaseViewHolder) holder).mIconGifImageView);
                                }

                                mPost.setSubredditIconUrl(iconImageUrl);
                            });
                } else if (!mPost.getSubredditIconUrl().equals("")) {
                    mGlide.load(mPost.getSubredditIconUrl())
                            .apply(RequestOptions.bitmapTransform(new RoundedCornersTransformation(72, 0)))
                            .error(mGlide.load(R.drawable.subreddit_default_icon)
                                    .apply(RequestOptions.bitmapTransform(new RoundedCornersTransformation(72, 0))))
                            .into(((PostDetailBaseViewHolder) holder).mIconGifImageView);
                } else {
                    mGlide.load(R.drawable.subreddit_default_icon)
                            .apply(RequestOptions.bitmapTransform(new RoundedCornersTransformation(72, 0)))
                            .into(((PostDetailBaseViewHolder) holder).mIconGifImageView);
                }
            }

            if (mPost.getAuthorFlairHTML() != null && !mPost.getAuthorFlairHTML().equals("")) {
                ((PostDetailBaseViewHolder) holder).mAuthorFlairTextView.setVisibility(View.VISIBLE);
                Utils.setHTMLWithImageToTextView(((PostDetailBaseViewHolder) holder).mAuthorFlairTextView, mPost.getAuthorFlairHTML(), true);
            } else if (mPost.getAuthorFlair() != null && !mPost.getAuthorFlair().equals("")) {
                ((PostDetailBaseViewHolder) holder).mAuthorFlairTextView.setVisibility(View.VISIBLE);
                ((PostDetailBaseViewHolder) holder).mAuthorFlairTextView.setText(mPost.getAuthorFlair());
            }

            switch (mPost.getVoteType()) {
                case 1:
                    //Upvote
                    ((PostDetailBaseViewHolder) holder).mUpvoteButton.setColorFilter(mUpvotedColor, PorterDuff.Mode.SRC_IN);
                    ((PostDetailBaseViewHolder) holder).mScoreTextView.setTextColor(mUpvotedColor);
                    break;
                case -1:
                    //Downvote
                    ((PostDetailBaseViewHolder) holder).mDownvoteButton.setColorFilter(mDownvotedColor, PorterDuff.Mode.SRC_IN);
                    ((PostDetailBaseViewHolder) holder).mScoreTextView.setTextColor(mDownvotedColor);
                    break;
                case 0:
                    ((PostDetailBaseViewHolder) holder).mUpvoteButton.setColorFilter(mPostIconAndInfoColor, PorterDuff.Mode.SRC_IN);
                    ((PostDetailBaseViewHolder) holder).mDownvoteButton.setColorFilter(mPostIconAndInfoColor, PorterDuff.Mode.SRC_IN);
                    ((PostDetailBaseViewHolder) holder).mScoreTextView.setTextColor(mPostIconAndInfoColor);
            }

            if (mPost.isArchived()) {
                ((PostDetailBaseViewHolder) holder).mUpvoteButton
                        .setColorFilter(mVoteAndReplyUnavailableVoteButtonColor, PorterDuff.Mode.SRC_IN);
                ((PostDetailBaseViewHolder) holder).mDownvoteButton
                        .setColorFilter(mVoteAndReplyUnavailableVoteButtonColor, PorterDuff.Mode.SRC_IN);
            }

            if (mPost.isCrosspost()) {
                ((PostDetailBaseViewHolder) holder).mCrosspostImageView.setVisibility(View.VISIBLE);
            }

            ((PostDetailBaseViewHolder) holder).mSubredditTextView.setText(mPost.getSubredditNamePrefixed());
            ((PostDetailBaseViewHolder) holder).mUserTextView.setText(mPost.getAuthorNamePrefixed());

            if (mShowElapsedTime) {
                ((PostDetailBaseViewHolder) holder).mPostTimeTextView.setText(
                        Utils.getElapsedTime(mActivity, mPost.getPostTimeMillis()));
            } else {
                ((PostDetailBaseViewHolder) holder).mPostTimeTextView.setText(Utils.getFormattedTime(mLocale, mPost.getPostTimeMillis(), mTimeFormatPattern));
            }

            if (mPost.isArchived()) {
                ((PostDetailBaseViewHolder) holder).mArchivedImageView.setVisibility(View.VISIBLE);
            }

            if (mPost.isLocked()) {
                ((PostDetailBaseViewHolder) holder).mLockedImageView.setVisibility(View.VISIBLE);
            }

            if (mPost.isSpoiler()) {
                ((PostDetailBaseViewHolder) holder).mSpoilerTextView.setVisibility(View.VISIBLE);
            }

            if (mPost.getFlair() != null && !mPost.getFlair().equals("")) {
                ((PostDetailBaseViewHolder) holder).mFlairTextView.setVisibility(View.VISIBLE);
                Utils.setHTMLWithImageToTextView(((PostDetailBaseViewHolder) holder).mFlairTextView, mPost.getFlair(), false);
            }

            if (mPost.getAwards() != null && !mPost.getAwards().equals("")) {
                ((PostDetailBaseViewHolder) holder).mAwardsTextView.setVisibility(View.VISIBLE);
                Utils.setHTMLWithImageToTextView(((PostDetailBaseViewHolder) holder).mAwardsTextView, mPost.getAwards(), true);
            }

            ((PostDetailBaseViewHolder) holder).mUpvoteRatioTextView.setText(mPost.getUpvoteRatio() + "%");

            if (mPost.isNSFW()) {
                ((PostDetailBaseViewHolder) holder).mNSFWTextView.setVisibility(View.VISIBLE);
            } else {
                ((PostDetailBaseViewHolder) holder).mNSFWTextView.setVisibility(View.GONE);
            }

            ((PostDetailBaseViewHolder) holder).mScoreTextView.setText(Utils.getNVotes(mShowAbsoluteNumberOfVotes, mPost.getScore() + mPost.getVoteType()));

            ((PostDetailBaseViewHolder) holder).commentsCountTextView.setText(Integer.toString(mPost.getNComments()));

            if (mPost.isSaved()) {
                ((PostDetailBaseViewHolder) holder).mSaveButton.setImageResource(R.drawable.ic_bookmark_grey_24dp);
            } else {
                ((PostDetailBaseViewHolder) holder).mSaveButton.setImageResource(R.drawable.ic_bookmark_border_grey_24dp);
            }

            if (holder instanceof PostDetailVideoAutoplayViewHolder) {
                ((PostDetailVideoAutoplayViewHolder) holder).previewImageView.setVisibility(View.VISIBLE);
                Post.Preview preview = getSuitablePreview(mPost.getPreviews());
                if (preview != null) {
                    ((PostDetailVideoAutoplayViewHolder) holder).aspectRatioFrameLayout.setAspectRatio((float) preview.getPreviewWidth() / preview.getPreviewHeight());
                    if (mImageViewWidth > preview.getPreviewWidth()) {
                        mGlide.load(preview.getPreviewUrl()).override(preview.getPreviewWidth(), preview.getPreviewHeight()).into(((PostDetailVideoAutoplayViewHolder) holder).previewImageView);
                    } else {
                        mGlide.load(preview.getPreviewUrl()).into(((PostDetailVideoAutoplayViewHolder) holder).previewImageView);
                    }
                } else {
                    ((PostDetailVideoAutoplayViewHolder) holder).aspectRatioFrameLayout.setAspectRatio(1);
                }
                ((PostDetailVideoAutoplayViewHolder) holder).setVolume(mMuteAutoplayingVideos || (mPost.isNSFW() && mMuteNSFWVideo) ? 0f : 1f);

                if (mPost.isGfycat() || mPost.isRedgifs() && !mPost.isLoadGfyOrRedgifsVideoSuccess()) {
                    ((PostDetailVideoAutoplayViewHolder) holder).fetchGfycatOrRedgifsVideoLinks = new FetchGfycatOrRedgifsVideoLinks(new FetchGfycatOrRedgifsVideoLinks.FetchGfycatOrRedgifsVideoLinksListener() {
                        @Override
                        public void success(String webm, String mp4) {
                            mPost.setVideoDownloadUrl(mp4);
                            mPost.setVideoUrl(webm);
                            mPost.setLoadGfyOrRedgifsVideoSuccess(true);
                            ((PostDetailVideoAutoplayViewHolder) holder).bindVideoUri(Uri.parse(mPost.getVideoUrl()));
                        }

                        @Override
                        public void failed(int errorCode) {
                            ((PostDetailVideoAutoplayViewHolder) holder).mErrorLoadingGfycatImageView.setVisibility(View.VISIBLE);
                        }
                    });
                    ((PostDetailVideoAutoplayViewHolder) holder).fetchGfycatOrRedgifsVideoLinks
                            .fetchGfycatOrRedgifsVideoLinksInRecyclerViewAdapter(mExecutor, new Handler(),
                                    mGfycatRetrofit, mRedgifsRetrofit, mPost.getGfycatId(),
                                    mPost.isGfycat(), mAutomaticallyTryRedgifs);
                } else {
                    ((PostDetailVideoAutoplayViewHolder) holder).bindVideoUri(Uri.parse(mPost.getVideoUrl()));
                }
            } else if (holder instanceof PostDetailVideoAndGifPreviewHolder) {
                if (mPost.getPostType() == Post.GIF_TYPE) {
                    ((PostDetailVideoAndGifPreviewHolder) holder).mTypeTextView.setText(mActivity.getString(R.string.gif));
                } else {
                    ((PostDetailVideoAndGifPreviewHolder) holder).mTypeTextView.setText(mActivity.getString(R.string.video));
                }
                Post.Preview preview = getSuitablePreview(mPost.getPreviews());
                if (preview != null) {
                    ((PostDetailVideoAndGifPreviewHolder) holder).mImageView.setRatio((float) preview.getPreviewHeight() / (float) preview.getPreviewWidth());
                    loadImage((PostDetailVideoAndGifPreviewHolder) holder, preview);
                }
            } else if (holder instanceof PostDetailImageAndGifAutoplayViewHolder) {
                if (mPost.getPostType() == Post.IMAGE_TYPE) {
                    ((PostDetailImageAndGifAutoplayViewHolder) holder).mTypeTextView.setText(R.string.image);
                } else {
                    ((PostDetailImageAndGifAutoplayViewHolder) holder).mTypeTextView.setText(R.string.gif);
                }

                Post.Preview preview = getSuitablePreview(mPost.getPreviews());
                if (preview != null) {
                    if (preview.getPreviewWidth() <= 0 || preview.getPreviewHeight() <= 0) {
                        int height = (int) (400 * mScale);
                        ((PostDetailImageAndGifAutoplayViewHolder) holder).mImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                        ((PostDetailImageAndGifAutoplayViewHolder) holder).mImageView.getLayoutParams().height = height;
                        preview.setPreviewWidth(mImageViewWidth);
                        preview.setPreviewHeight(height);
                    } else {
                        ((PostDetailImageAndGifAutoplayViewHolder) holder).mImageView.setRatio((float) preview.getPreviewHeight() / (float) preview.getPreviewWidth());
                    }
                    loadImage((PostDetailImageAndGifAutoplayViewHolder) holder, preview);
                }
            } else if (holder instanceof PostDetailLinkViewHolder) {
                String domain = Uri.parse(mPost.getUrl()).getHost();
                ((PostDetailLinkViewHolder) holder).mLinkTextView.setText(domain);
                Post.Preview preview = getSuitablePreview(mPost.getPreviews());
                if (preview != null) {
                    ((PostDetailLinkViewHolder) holder).mImageView.setRatio((float) preview.getPreviewHeight() / (float) preview.getPreviewWidth());
                    loadImage((PostDetailLinkViewHolder) holder, preview);
                }

            } else if (holder instanceof PostDetailNoPreviewViewHolder) {
                if (mPost.getPostType() == Post.LINK_TYPE || mPost.getPostType() == Post.NO_PREVIEW_LINK_TYPE) {
                    ((PostDetailNoPreviewViewHolder) holder).mTypeTextView.setText(R.string.link);
                    String noPreviewLinkDomain = Uri.parse(mPost.getUrl()).getHost();
                    ((PostDetailNoPreviewViewHolder) holder).mLinkTextView.setVisibility(View.VISIBLE);
                    ((PostDetailNoPreviewViewHolder) holder).mLinkTextView.setText(noPreviewLinkDomain);
                    ((PostDetailNoPreviewViewHolder) holder).mNoPreviewPostTypeImageView.setImageResource(R.drawable.ic_link);
                } else {
                    ((PostDetailNoPreviewViewHolder) holder).mLinkTextView.setVisibility(View.GONE);
                    switch (mPost.getPostType()) {
                        case Post.VIDEO_TYPE:
                            ((PostDetailNoPreviewViewHolder) holder).mTypeTextView.setText(R.string.video);
                            ((PostDetailNoPreviewViewHolder) holder).mNoPreviewPostTypeImageView.setImageResource(R.drawable.ic_outline_video_24dp);
                            break;
                        case Post.IMAGE_TYPE:
                            ((PostDetailNoPreviewViewHolder) holder).mTypeTextView.setText(R.string.image);
                            ((PostDetailNoPreviewViewHolder) holder).mNoPreviewPostTypeImageView.setImageResource(R.drawable.ic_image_24dp);
                            break;
                        case Post.GIF_TYPE:
                            ((PostDetailNoPreviewViewHolder) holder).mTypeTextView.setText(R.string.gif);
                            ((PostDetailNoPreviewViewHolder) holder).mNoPreviewPostTypeImageView.setImageResource(R.drawable.ic_image_24dp);
                            break;
                        case Post.GALLERY_TYPE:
                            ((PostDetailNoPreviewViewHolder) holder).mTypeTextView.setText(R.string.gallery);
                            ((PostDetailNoPreviewViewHolder) holder).mNoPreviewPostTypeImageView.setImageResource(R.drawable.ic_gallery_reverse_color_24dp);
                            break;
                    }
                }

                if (mPost.getSelfText() != null && !mPost.getSelfText().equals("")) {
                    ((PostDetailNoPreviewViewHolder) holder).mContentMarkdownView.setVisibility(View.VISIBLE);
                    LinearLayoutManager linearLayoutManager = new MarkwonLinearLayoutManager(mActivity, new MarkwonLinearLayoutManager.HorizontalScrollViewScrolledListener() {
                        @Override
                        public void onScrolledLeft() {
                            ((RedditViewPostDetailActivity) mActivity).lockSwipeRightToGoBack();
                        }

                        @Override
                        public void onScrolledRight() {
                            ((RedditViewPostDetailActivity) mActivity).unlockSwipeRightToGoBack();
                        }
                    });
                    ((PostDetailNoPreviewViewHolder) holder).mContentMarkdownView.setLayoutManager(linearLayoutManager);
                    ((PostDetailNoPreviewViewHolder) holder).mContentMarkdownView.setAdapter(mMarkwonAdapter);
                    mMarkwonAdapter.setMarkdown(mPostDetailMarkwon, mPost.getSelfText());
                    mMarkwonAdapter.notifyDataSetChanged();
                }
            } else if (holder instanceof PostDetailGalleryViewHolder) {
                Post.Preview preview = getSuitablePreview(mPost.getPreviews());
                if (preview != null) {
                    ((PostDetailGalleryViewHolder) holder).mRelativeLayout.setVisibility(View.VISIBLE);
                    ((PostDetailGalleryViewHolder) holder).mImageView
                            .setRatio((float) preview.getPreviewHeight() / preview.getPreviewWidth());

                    loadImage((PostDetailGalleryViewHolder) holder, preview);
                } else {
                    ((PostDetailGalleryViewHolder) holder).mNoPreviewPostTypeImageView.setVisibility(View.VISIBLE);
                }
            } else if (holder instanceof PostDetailTextViewHolder) {
                if (mPost.getSelfText() != null && !mPost.getSelfText().equals("")) {
                    ((PostDetailTextViewHolder) holder).mContentMarkdownView.setVisibility(View.VISIBLE);
                    LinearLayoutManager linearLayoutManager = new MarkwonLinearLayoutManager(mActivity, new MarkwonLinearLayoutManager.HorizontalScrollViewScrolledListener() {
                        @Override
                        public void onScrolledLeft() {
                            ((RedditViewPostDetailActivity) mActivity).lockSwipeRightToGoBack();
                        }

                        @Override
                        public void onScrolledRight() {
                            ((RedditViewPostDetailActivity) mActivity).unlockSwipeRightToGoBack();
                        }
                    });
                    ((PostDetailTextViewHolder) holder).mContentMarkdownView.setLayoutManager(linearLayoutManager);
                    ((PostDetailTextViewHolder) holder).mContentMarkdownView.setAdapter(mMarkwonAdapter);
                    mMarkwonAdapter.setMarkdown(mPostDetailMarkwon, mPost.getSelfText());
                    mMarkwonAdapter.notifyDataSetChanged();
                }
            }
        }
    }

    @Nullable
    private Post.Preview getSuitablePreview(ArrayList<Post.Preview> previews) {
        Post.Preview preview;
        if (!previews.isEmpty()) {
            int previewIndex;
            if (mDataSavingMode && previews.size() > 2) {
                previewIndex = previews.size() / 2;
            } else {
                previewIndex = 0;
            }
            preview = previews.get(previewIndex);
            if (preview.getPreviewWidth() * preview.getPreviewHeight() > 10_000_000) {
                for (int i = previews.size() - 1; i >= 1; i--) {
                    preview = previews.get(i);
                    if (mImageViewWidth >= preview.getPreviewWidth()) {
                        if (preview.getPreviewWidth() * preview.getPreviewHeight() <= 10_000_000) {
                            return preview;
                        }
                    } else {
                        int height = mImageViewWidth / preview.getPreviewWidth() * preview.getPreviewHeight();
                        if (mImageViewWidth * height <= 10_000_000) {
                            return preview;
                        }
                    }
                }
            }

            if (preview.getPreviewWidth() * preview.getPreviewHeight() > 10_000_000) {
                int divisor = 2;
                do {
                    preview.setPreviewWidth(preview.getPreviewWidth() / divisor);
                    preview.setPreviewHeight(preview.getPreviewHeight() / divisor);
                    divisor *= 2;
                } while (preview.getPreviewWidth() * preview.getPreviewHeight() > 10_000_000);
            }

            return preview;
        }

        return null;
    }

    private void loadImage(PostDetailBaseViewHolder holder, @NonNull Post.Preview preview) {
        if (holder instanceof PostDetailImageAndGifAutoplayViewHolder) {
            boolean blurImage = (mPost.isNSFW() && mNeedBlurNsfw && !(mDoNotBlurNsfwInNsfwSubreddits && mFragment != null && mFragment.getIsNsfwSubreddit()) && !(mPost.getPostType() == Post.GIF_TYPE && mAutoplayNsfwVideos)) || (mPost.isSpoiler() && mNeedBlurSpoiler);
            String url = mPost.getPostType() == Post.IMAGE_TYPE || blurImage ? preview.getPreviewUrl() : mPost.getUrl();
            RequestBuilder<Drawable> imageRequestBuilder = mGlide.load(url)
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            ((PostDetailImageAndGifAutoplayViewHolder) holder).mLoadImageProgressBar.setVisibility(View.GONE);
                            ((PostDetailImageAndGifAutoplayViewHolder) holder).mLoadImageErrorTextView.setVisibility(View.VISIBLE);
                            ((PostDetailImageAndGifAutoplayViewHolder) holder).mLoadImageErrorTextView.setOnClickListener(view -> {
                                ((PostDetailImageAndGifAutoplayViewHolder) holder).mLoadImageProgressBar.setVisibility(View.VISIBLE);
                                ((PostDetailImageAndGifAutoplayViewHolder) holder).mLoadImageErrorTextView.setVisibility(View.GONE);
                                loadImage(holder, preview);
                            });
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            ((PostDetailImageAndGifAutoplayViewHolder) holder).mLoadWrapper.setVisibility(View.GONE);
                            return false;
                        }
                    });

            if (blurImage) {
                imageRequestBuilder.apply(RequestOptions.bitmapTransform(new BlurTransformation(50, 10))).into(((PostDetailImageAndGifAutoplayViewHolder) holder).mImageView);
            } else {
                if (mImageViewWidth > preview.getPreviewWidth()) {
                    imageRequestBuilder.override(preview.getPreviewWidth(), preview.getPreviewHeight()).into(((PostDetailImageAndGifAutoplayViewHolder) holder).mImageView);
                } else {
                    imageRequestBuilder.into(((PostDetailImageAndGifAutoplayViewHolder) holder).mImageView);
                }
            }
        } else if (holder instanceof PostDetailVideoAndGifPreviewHolder) {
            RequestBuilder<Drawable> imageRequestBuilder = mGlide.load(preview.getPreviewUrl())
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            ((PostDetailVideoAndGifPreviewHolder) holder).mLoadImageProgressBar.setVisibility(View.GONE);
                            ((PostDetailVideoAndGifPreviewHolder) holder).mLoadImageErrorTextView.setVisibility(View.VISIBLE);
                            ((PostDetailVideoAndGifPreviewHolder) holder).mLoadImageErrorTextView.setOnClickListener(view -> {
                                ((PostDetailVideoAndGifPreviewHolder) holder).mLoadImageProgressBar.setVisibility(View.VISIBLE);
                                ((PostDetailVideoAndGifPreviewHolder) holder).mLoadImageErrorTextView.setVisibility(View.GONE);
                                loadImage(holder, preview);
                            });
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            ((PostDetailVideoAndGifPreviewHolder) holder).mLoadWrapper.setVisibility(View.GONE);
                            return false;
                        }
                    });

            if ((mPost.isNSFW() && mNeedBlurNsfw && !(mDoNotBlurNsfwInNsfwSubreddits && mFragment != null && mFragment.getIsNsfwSubreddit())) || (mPost.isSpoiler() && mNeedBlurSpoiler)) {
                imageRequestBuilder.apply(RequestOptions.bitmapTransform(new BlurTransformation(50, 10)))
                        .into(((PostDetailVideoAndGifPreviewHolder) holder).mImageView);
            } else {
                if (mImageViewWidth > preview.getPreviewWidth()) {
                    imageRequestBuilder.override(preview.getPreviewWidth(), preview.getPreviewHeight()).into(((PostDetailVideoAndGifPreviewHolder) holder).mImageView);
                } else {
                    imageRequestBuilder.into(((PostDetailVideoAndGifPreviewHolder) holder).mImageView);
                }
            }
        } else if (holder instanceof PostDetailLinkViewHolder) {
            RequestBuilder<Drawable> imageRequestBuilder = mGlide.load(preview.getPreviewUrl())
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            ((PostDetailLinkViewHolder) holder).mLoadImageProgressBar.setVisibility(View.GONE);
                            ((PostDetailLinkViewHolder) holder).mLoadImageErrorTextView.setVisibility(View.VISIBLE);
                            ((PostDetailLinkViewHolder) holder).mLoadImageErrorTextView.setOnClickListener(view -> {
                                ((PostDetailLinkViewHolder) holder).mLoadImageProgressBar.setVisibility(View.VISIBLE);
                                ((PostDetailLinkViewHolder) holder).mLoadImageErrorTextView.setVisibility(View.GONE);
                                loadImage(holder, preview);
                            });
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            ((PostDetailLinkViewHolder) holder).mLoadWrapper.setVisibility(View.GONE);
                            return false;
                        }
                    });

            if ((mPost.isNSFW() && mNeedBlurNsfw && !(mDoNotBlurNsfwInNsfwSubreddits && mFragment != null && mFragment.getIsNsfwSubreddit())) || (mPost.isSpoiler() && mNeedBlurSpoiler)) {
                imageRequestBuilder.apply(RequestOptions.bitmapTransform(new BlurTransformation(50, 10)))
                        .into(((PostDetailLinkViewHolder) holder).mImageView);
            } else {
                if (mImageViewWidth > preview.getPreviewWidth()) {
                    imageRequestBuilder.override(preview.getPreviewWidth(), preview.getPreviewHeight()).into(((PostDetailLinkViewHolder) holder).mImageView);
                } else {
                    imageRequestBuilder.into(((PostDetailLinkViewHolder) holder).mImageView);
                }
            }
        } else if (holder instanceof PostDetailGalleryViewHolder) {
            RequestBuilder<Drawable> imageRequestBuilder = mGlide.load(preview.getPreviewUrl())
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            ((PostDetailGalleryViewHolder) holder).mLoadImageProgressBar.setVisibility(View.GONE);
                            ((PostDetailGalleryViewHolder) holder).mLoadImageErrorTextView.setVisibility(View.VISIBLE);
                            ((PostDetailGalleryViewHolder) holder).mLoadImageErrorTextView.setOnClickListener(view -> {
                                ((PostDetailGalleryViewHolder) holder).mLoadImageProgressBar.setVisibility(View.VISIBLE);
                                ((PostDetailGalleryViewHolder) holder).mLoadImageErrorTextView.setVisibility(View.GONE);
                                loadImage(holder, preview);
                            });
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            ((PostDetailGalleryViewHolder) holder).mLoadWrapper.setVisibility(View.GONE);
                            return false;
                        }
                    });

            if ((mPost.isNSFW() && mNeedBlurNsfw && !(mDoNotBlurNsfwInNsfwSubreddits && mFragment != null && mFragment.getIsNsfwSubreddit()) && !(mPost.getPostType() == Post.GIF_TYPE && mAutoplayNsfwVideos)) || (mPost.isSpoiler() && mNeedBlurSpoiler)) {
                imageRequestBuilder.apply(RequestOptions.bitmapTransform(new BlurTransformation(50, 10))).into(((PostDetailGalleryViewHolder) holder).mImageView);
            } else {
                if (mImageViewWidth > preview.getPreviewWidth()) {
                    imageRequestBuilder.override(preview.getPreviewWidth(), preview.getPreviewHeight()).into(((PostDetailGalleryViewHolder) holder).mImageView);
                } else {
                    imageRequestBuilder.into(((PostDetailGalleryViewHolder) holder).mImageView);
                }
            }
        }
    }

    public void updatePost(Post post) {
        mPost = post;
        notifyItemChanged(0);
    }

    public void setBlurNsfwAndDoNotBlurNsfwInNsfwSubreddits(boolean needBlurNsfw, boolean doNotBlurNsfwInNsfwSubreddits) {
        mNeedBlurNsfw = needBlurNsfw;
        mDoNotBlurNsfwInNsfwSubreddits = doNotBlurNsfwInNsfwSubreddits;
    }

    public void setBlurSpoiler(boolean needBlurSpoiler) {
        mNeedBlurSpoiler = needBlurSpoiler;
    }

    public void setAutoplay(boolean autoplay) {
        mAutoplay = autoplay;
    }

    public void setDataSavingMode(boolean dataSavingMode) {
        mDataSavingMode = dataSavingMode;
    }

    public void onItemSwipe(RecyclerView.ViewHolder viewHolder, int direction, int swipeLeftAction, int swipeRightAction) {
        if (viewHolder instanceof PostDetailBaseViewHolder) {
            if (direction == ItemTouchHelper.LEFT || direction == ItemTouchHelper.START) {
                if (swipeLeftAction == SharedPreferencesUtils.SWIPE_ACITON_UPVOTE) {
                    ((PostDetailBaseViewHolder) viewHolder).mUpvoteButton.performClick();
                } else if (swipeLeftAction == SharedPreferencesUtils.SWIPE_ACITON_DOWNVOTE) {
                    ((PostDetailBaseViewHolder) viewHolder).mDownvoteButton.performClick();
                }
            } else {
                if (swipeRightAction == SharedPreferencesUtils.SWIPE_ACITON_UPVOTE) {
                    ((PostDetailBaseViewHolder) viewHolder).mUpvoteButton.performClick();
                } else if (swipeRightAction == SharedPreferencesUtils.SWIPE_ACITON_DOWNVOTE) {
                    ((PostDetailBaseViewHolder) viewHolder).mDownvoteButton.performClick();
                }
            }
        }
    }

    public void giveAward(String awardsHTML, int awardCount) {
        if (mPost != null) {
            mPost.addAwards(awardsHTML);
            mPost.addAwards(awardCount);
            notifyItemChanged(0);
        }
    }

    @Override
    public void onViewRecycled(@NonNull RecyclerView.ViewHolder holder) {
        if (holder instanceof PostDetailBaseViewHolder) {
            ((PostDetailBaseViewHolder) holder).mUpvoteButton.setColorFilter(mPostIconAndInfoColor, PorterDuff.Mode.SRC_IN);
            ((PostDetailBaseViewHolder) holder).mScoreTextView.setTextColor(mPostIconAndInfoColor);
            ((PostDetailBaseViewHolder) holder).mDownvoteButton.setColorFilter(mPostIconAndInfoColor, PorterDuff.Mode.SRC_IN);
            ((PostDetailBaseViewHolder) holder).mFlairTextView.setVisibility(View.GONE);
            ((PostDetailBaseViewHolder) holder).mSpoilerTextView.setVisibility(View.GONE);
            ((PostDetailBaseViewHolder) holder).mNSFWTextView.setVisibility(View.GONE);

            if (holder instanceof PostDetailVideoAutoplayViewHolder) {
                if (((PostDetailVideoAutoplayViewHolder) holder).fetchGfycatOrRedgifsVideoLinks != null) {
                    ((PostDetailVideoAutoplayViewHolder) holder).fetchGfycatOrRedgifsVideoLinks.cancel();
                }
                ((PostDetailVideoAutoplayViewHolder) holder).mErrorLoadingGfycatImageView.setVisibility(View.GONE);
                ((PostDetailVideoAutoplayViewHolder) holder).muteButton.setVisibility(View.GONE);
                ((PostDetailVideoAutoplayViewHolder) holder).resetVolume();
                mGlide.clear(((PostDetailVideoAutoplayViewHolder) holder).previewImageView);
                ((PostDetailVideoAutoplayViewHolder) holder).previewImageView.setVisibility(View.GONE);
            } else if (holder instanceof PostDetailVideoAndGifPreviewHolder) {
                mGlide.clear(((PostDetailVideoAndGifPreviewHolder) holder).mImageView);
            } else if (holder instanceof PostDetailImageAndGifAutoplayViewHolder) {
                mGlide.clear(((PostDetailImageAndGifAutoplayViewHolder) holder).mImageView);
            } else if (holder instanceof PostDetailLinkViewHolder) {
                mGlide.clear(((PostDetailLinkViewHolder) holder).mImageView);
            }
        }
    }

    @Override
    public int getItemCount() {
        return 1;
    }

    @Nullable
    @Override
    public Object getKeyForOrder(int order) {
        return mPost;
    }

    @Nullable
    @Override
    public Integer getOrderForKey(@NonNull Object key) {
        return 0;
    }

    public interface PostDetailRecyclerViewAdapterCallback {
        void updatePost(Post post);
    }

    public class PostDetailBaseViewHolder extends RecyclerView.ViewHolder {
        AspectRatioGifImageView mIconGifImageView;
        TextView mSubredditTextView;
        TextView mUserTextView;
        TextView mAuthorFlairTextView;
        TextView mPostTimeTextView;
        TextView mTitleTextView;
        CustomTextView mTypeTextView;
        ImageView mCrosspostImageView;
        ImageView mArchivedImageView;
        ImageView mLockedImageView;
        CustomTextView mNSFWTextView;
        CustomTextView mSpoilerTextView;
        CustomTextView mFlairTextView;
        TextView mAwardsTextView;
        TextView mUpvoteRatioTextView;
        ConstraintLayout mBottomConstraintLayout;
        ImageView mUpvoteButton;
        TextView mScoreTextView;
        ImageView mDownvoteButton;
        TextView commentsCountTextView;
        ImageView mSaveButton;
        ImageView mShareButton;

        PostDetailBaseViewHolder(@NonNull View itemView) {
            super(itemView);
        }

        void setBaseView(AspectRatioGifImageView mIconGifImageView,
                         TextView mSubredditTextView,
                         TextView mUserTextView,
                         TextView mAuthorFlairTextView,
                         TextView mPostTimeTextView,
                         TextView mTitleTextView,
                         CustomTextView mTypeTextView,
                         ImageView mCrosspostImageView,
                         ImageView mArchivedImageView,
                         ImageView mLockedImageView,
                         CustomTextView mNSFWTextView,
                         CustomTextView mSpoilerTextView,
                         CustomTextView mFlairTextView,
                         TextView mAwardsTextView,
                         TextView mUpvoteRatioTextView,
                         ConstraintLayout mBottomConstraintLayout,
                         ImageView mUpvoteButton,
                         TextView mScoreTextView,
                         ImageView mDownvoteButton,
                         TextView commentsCountTextView,
                         ImageView mSaveButton,
                         ImageView mShareButton) {
            this.mIconGifImageView = mIconGifImageView;
            this.mSubredditTextView = mSubredditTextView;
            this.mUserTextView = mUserTextView;
            this.mAuthorFlairTextView = mAuthorFlairTextView;
            this.mPostTimeTextView = mPostTimeTextView;
            this.mTitleTextView = mTitleTextView;
            this.mTypeTextView = mTypeTextView;
            this.mCrosspostImageView = mCrosspostImageView;
            this.mArchivedImageView = mArchivedImageView;
            this.mLockedImageView = mLockedImageView;
            this.mNSFWTextView = mNSFWTextView;
            this.mSpoilerTextView = mSpoilerTextView;
            this.mFlairTextView = mFlairTextView;
            this.mAwardsTextView = mAwardsTextView;
            this.mUpvoteRatioTextView = mUpvoteRatioTextView;
            this.mBottomConstraintLayout = mBottomConstraintLayout;
            this.mUpvoteButton = mUpvoteButton;
            this.mScoreTextView = mScoreTextView;
            this.mDownvoteButton = mDownvoteButton;
            this.commentsCountTextView = commentsCountTextView;
            this.mSaveButton = mSaveButton;
            this.mShareButton = mShareButton;

            mIconGifImageView.setOnClickListener(view -> mSubredditTextView.performClick());

            mSubredditTextView.setOnClickListener(view -> {
                Intent intent;
                if (mPost.getSubredditNamePrefixed().equals("u/" + mPost.getAuthor())) {
                    intent = new Intent(mActivity, RedditViewUserDetailActivity.class);
                    intent.putExtra(RedditViewUserDetailActivity.EXTRA_USER_NAME_KEY, mPost.getAuthor());
                } else {
                    intent = new Intent(mActivity, RedditViewSubredditDetailActivity.class);
                    intent.putExtra(RedditViewSubredditDetailActivity.EXTRA_SUBREDDIT_NAME_KEY,
                            mPost.getSubredditNamePrefixed().substring(2));
                }
                mActivity.startActivity(intent);
            });

            mUserTextView.setOnClickListener(view -> {
                Intent intent = new Intent(mActivity, RedditViewUserDetailActivity.class);
                intent.putExtra(RedditViewUserDetailActivity.EXTRA_USER_NAME_KEY, mPost.getAuthor());
                mActivity.startActivity(intent);
            });

            mAuthorFlairTextView.setOnClickListener(view -> mUserTextView.performClick());

            mCrosspostImageView.setOnClickListener(view -> {
                Intent crosspostIntent = new Intent(mActivity, RedditViewPostDetailActivity.class);
                crosspostIntent.putExtra(RedditViewPostDetailActivity.EXTRA_POST_ID, mPost.getCrosspostParentId());
                mActivity.startActivity(crosspostIntent);
            });

            mTypeTextView.setOnClickListener(view -> {
                Intent intent = new Intent(mActivity, RedditFilteredPostsActivity.class);
                intent.putExtra(RedditFilteredPostsActivity.EXTRA_NAME, mSubredditNamePrefixed.substring(2));
                intent.putExtra(RedditFilteredPostsActivity.EXTRA_POST_TYPE, PostDataSource.TYPE_SUBREDDIT);
                intent.putExtra(RedditFilteredPostsActivity.EXTRA_FILTER, mPost.getPostType());
                mActivity.startActivity(intent);
            });

            mFlairTextView.setOnClickListener(view -> {
                Intent intent = new Intent(mActivity, RedditFilteredPostsActivity.class);
                intent.putExtra(RedditFilteredPostsActivity.EXTRA_NAME, mSubredditNamePrefixed.substring(2));
                intent.putExtra(RedditFilteredPostsActivity.EXTRA_POST_TYPE, PostDataSource.TYPE_SUBREDDIT);
                intent.putExtra(RedditFilteredPostsActivity.EXTRA_CONTAIN_FLAIR, mPost.getFlair());
                mActivity.startActivity(intent);
            });

            mNSFWTextView.setOnClickListener(view -> {
                Intent intent = new Intent(mActivity, RedditFilteredPostsActivity.class);
                intent.putExtra(RedditFilteredPostsActivity.EXTRA_NAME, mSubredditNamePrefixed.substring(2));
                intent.putExtra(RedditFilteredPostsActivity.EXTRA_POST_TYPE, PostDataSource.TYPE_SUBREDDIT);
                intent.putExtra(RedditFilteredPostsActivity.EXTRA_FILTER, Post.NSFW_TYPE);
                mActivity.startActivity(intent);
            });

            mUpvoteButton.setOnClickListener(view -> {
                if (mPost.isArchived()) {
                    Toast.makeText(mActivity, R.string.archived_post_vote_unavailable, Toast.LENGTH_SHORT).show();
                    return;
                }

                if (mAccessToken == null) {
                    Toast.makeText(mActivity, R.string.login_first, Toast.LENGTH_SHORT).show();
                    return;
                }

                ColorFilter previousUpvoteButtonColorFilter = mUpvoteButton.getColorFilter();
                ColorFilter previousDownvoteButtonColorFilter = mDownvoteButton.getColorFilter();
                int previousScoreTextViewColor = mScoreTextView.getCurrentTextColor();

                int previousVoteType = mPost.getVoteType();
                String newVoteType;

                mDownvoteButton.setColorFilter(mPostIconAndInfoColor, PorterDuff.Mode.SRC_IN);

                if (previousVoteType != 1) {
                    //Not upvoted before
                    mPost.setVoteType(1);
                    newVoteType = APIUtils.DIR_UPVOTE;
                    mUpvoteButton.setColorFilter(mUpvotedColor, PorterDuff.Mode.SRC_IN);
                    mScoreTextView.setTextColor(mUpvotedColor);
                } else {
                    //Upvoted before
                    mPost.setVoteType(0);
                    newVoteType = APIUtils.DIR_UNVOTE;
                    mUpvoteButton.setColorFilter(mPostIconAndInfoColor, PorterDuff.Mode.SRC_IN);
                    mScoreTextView.setTextColor(mPostIconAndInfoColor);
                }

                mScoreTextView.setText(Utils.getNVotes(mShowAbsoluteNumberOfVotes,
                        mPost.getScore() + mPost.getVoteType()));

                mPostDetailRecyclerViewAdapterCallback.updatePost(mPost);

                VoteThing.voteThing(mActivity, mOauthRetrofit, mAccessToken, new VoteThing.VoteThingWithoutPositionListener() {
                    @Override
                    public void onVoteThingSuccess() {
                        if (newVoteType.equals(APIUtils.DIR_UPVOTE)) {
                            mPost.setVoteType(1);
                            mUpvoteButton.setColorFilter(mUpvotedColor, PorterDuff.Mode.SRC_IN);
                            mScoreTextView.setTextColor(mUpvotedColor);
                        } else {
                            mPost.setVoteType(0);
                            mUpvoteButton.setColorFilter(mPostIconAndInfoColor, PorterDuff.Mode.SRC_IN);
                            mScoreTextView.setTextColor(mPostIconAndInfoColor);
                        }

                        mDownvoteButton.setColorFilter(mPostIconAndInfoColor, PorterDuff.Mode.SRC_IN);
                        mScoreTextView.setText(Utils.getNVotes(mShowAbsoluteNumberOfVotes,
                                mPost.getScore() + mPost.getVoteType()));

                        mPostDetailRecyclerViewAdapterCallback.updatePost(mPost);
                    }

                    @Override
                    public void onVoteThingFail() {
                        Toast.makeText(mActivity, R.string.vote_failed, Toast.LENGTH_SHORT).show();
                        mPost.setVoteType(previousVoteType);
                        mScoreTextView.setText(Utils.getNVotes(mShowAbsoluteNumberOfVotes,
                                mPost.getScore() + previousVoteType));
                        mUpvoteButton.setColorFilter(previousUpvoteButtonColorFilter);
                        mDownvoteButton.setColorFilter(previousDownvoteButtonColorFilter);
                        mScoreTextView.setTextColor(previousScoreTextViewColor);

                        mPostDetailRecyclerViewAdapterCallback.updatePost(mPost);
                    }
                }, mPost.getFullName(), newVoteType);
            });

            mDownvoteButton.setOnClickListener(view -> {
                if (mPost.isArchived()) {
                    Toast.makeText(mActivity, R.string.archived_post_vote_unavailable, Toast.LENGTH_SHORT).show();
                    return;
                }

                if (mAccessToken == null) {
                    Toast.makeText(mActivity, R.string.login_first, Toast.LENGTH_SHORT).show();
                    return;
                }

                ColorFilter previousUpvoteButtonColorFilter = mUpvoteButton.getColorFilter();
                ColorFilter previousDownvoteButtonColorFilter = mDownvoteButton.getColorFilter();
                int previousScoreTextViewColor = mScoreTextView.getCurrentTextColor();

                int previousVoteType = mPost.getVoteType();
                String newVoteType;

                mUpvoteButton.setColorFilter(mPostIconAndInfoColor, PorterDuff.Mode.SRC_IN);

                if (previousVoteType != -1) {
                    //Not upvoted before
                    mPost.setVoteType(-1);
                    newVoteType = APIUtils.DIR_DOWNVOTE;
                    mDownvoteButton.setColorFilter(mDownvotedColor, PorterDuff.Mode.SRC_IN);
                    mScoreTextView.setTextColor(mDownvotedColor);
                } else {
                    //Upvoted before
                    mPost.setVoteType(0);
                    newVoteType = APIUtils.DIR_UNVOTE;
                    mDownvoteButton.setColorFilter(mPostIconAndInfoColor, PorterDuff.Mode.SRC_IN);
                    mScoreTextView.setTextColor(mPostIconAndInfoColor);
                }

                mScoreTextView.setText(Utils.getNVotes(mShowAbsoluteNumberOfVotes,
                        mPost.getScore() + mPost.getVoteType()));

                mPostDetailRecyclerViewAdapterCallback.updatePost(mPost);

                VoteThing.voteThing(mActivity, mOauthRetrofit, mAccessToken, new VoteThing.VoteThingWithoutPositionListener() {
                    @Override
                    public void onVoteThingSuccess() {
                        if (newVoteType.equals(APIUtils.DIR_DOWNVOTE)) {
                            mPost.setVoteType(-1);
                            mDownvoteButton.setColorFilter(mDownvotedColor, PorterDuff.Mode.SRC_IN);
                            mScoreTextView.setTextColor(mDownvotedColor);
                        } else {
                            mPost.setVoteType(0);
                            mDownvoteButton.setColorFilter(mPostIconAndInfoColor, PorterDuff.Mode.SRC_IN);
                            mScoreTextView.setTextColor(mPostIconAndInfoColor);
                        }

                        mUpvoteButton.setColorFilter(mPostIconAndInfoColor, PorterDuff.Mode.SRC_IN);
                        mScoreTextView.setText(Utils.getNVotes(mShowAbsoluteNumberOfVotes,
                                mPost.getScore() + mPost.getVoteType()));

                        mPostDetailRecyclerViewAdapterCallback.updatePost(mPost);
                    }

                    @Override
                    public void onVoteThingFail() {
                        Toast.makeText(mActivity, R.string.vote_failed, Toast.LENGTH_SHORT).show();
                        mPost.setVoteType(previousVoteType);
                        mScoreTextView.setText(Utils.getNVotes(mShowAbsoluteNumberOfVotes,
                                mPost.getScore() + previousVoteType));
                        mUpvoteButton.setColorFilter(previousUpvoteButtonColorFilter);
                        mDownvoteButton.setColorFilter(previousDownvoteButtonColorFilter);
                        mScoreTextView.setTextColor(previousScoreTextViewColor);

                        mPostDetailRecyclerViewAdapterCallback.updatePost(mPost);
                    }
                }, mPost.getFullName(), newVoteType);
            });

            commentsCountTextView.setOnClickListener(view -> {
                if (mPost.isArchived()) {
                    Toast.makeText(mActivity, R.string.archived_post_comment_unavailable, Toast.LENGTH_SHORT).show();
                    return;
                }

                if (mPost.isLocked()) {
                    Toast.makeText(mActivity, R.string.locked_post_comment_unavailable, Toast.LENGTH_SHORT).show();
                    return;
                }

                if (mAccessToken == null) {
                    Toast.makeText(mActivity, R.string.login_first, Toast.LENGTH_SHORT).show();
                    return;
                }

                Intent intent = new Intent(mActivity, RedditCommentActivity.class);
                intent.putExtra(RedditCommentActivity.EXTRA_PARENT_FULLNAME_KEY, mPost.getFullName());
                intent.putExtra(RedditCommentActivity.EXTRA_COMMENT_PARENT_TEXT_MARKDOWN_KEY, mPost.getTitle());
                intent.putExtra(RedditCommentActivity.EXTRA_COMMENT_PARENT_BODY_MARKDOWN_KEY, mPost.getSelfText());
                intent.putExtra(RedditCommentActivity.EXTRA_COMMENT_PARENT_BODY_KEY, mPost.getSelfTextPlain());
                intent.putExtra(RedditCommentActivity.EXTRA_IS_REPLYING_KEY, false);
                intent.putExtra(RedditCommentActivity.EXTRA_PARENT_DEPTH_KEY, 0);
                mActivity.startActivityForResult(intent, WRITE_COMMENT_REQUEST_CODE);
            });

            mSaveButton.setOnClickListener(view -> {
                if (mAccessToken == null) {
                    Toast.makeText(mActivity, R.string.login_first, Toast.LENGTH_SHORT).show();
                    return;
                }

                if (mPost.isSaved()) {
                    mSaveButton.setImageResource(R.drawable.ic_bookmark_border_grey_24dp);
                    SaveThing.unsaveThing(mOauthRetrofit, mAccessToken, mPost.getFullName(),
                            new SaveThing.SaveThingListener() {
                                @Override
                                public void success() {
                                    mPost.setSaved(false);
                                    mSaveButton.setImageResource(R.drawable.ic_bookmark_border_grey_24dp);
                                    Toast.makeText(mActivity, R.string.post_unsaved_success, Toast.LENGTH_SHORT).show();
                                    mPostDetailRecyclerViewAdapterCallback.updatePost(mPost);
                                }

                                @Override
                                public void failed() {
                                    mPost.setSaved(true);
                                    mSaveButton.setImageResource(R.drawable.ic_bookmark_grey_24dp);
                                    Toast.makeText(mActivity, R.string.post_unsaved_failed, Toast.LENGTH_SHORT).show();
                                    mPostDetailRecyclerViewAdapterCallback.updatePost(mPost);
                                }
                            });
                } else {
                    mSaveButton.setImageResource(R.drawable.ic_bookmark_grey_24dp);
                    SaveThing.saveThing(mOauthRetrofit, mAccessToken, mPost.getFullName(),
                            new SaveThing.SaveThingListener() {
                                @Override
                                public void success() {
                                    mPost.setSaved(true);
                                    mSaveButton.setImageResource(R.drawable.ic_bookmark_grey_24dp);
                                    Toast.makeText(mActivity, R.string.post_saved_success, Toast.LENGTH_SHORT).show();
                                    mPostDetailRecyclerViewAdapterCallback.updatePost(mPost);
                                }

                                @Override
                                public void failed() {
                                    mPost.setSaved(false);
                                    mSaveButton.setImageResource(R.drawable.ic_bookmark_border_grey_24dp);
                                    Toast.makeText(mActivity, R.string.post_saved_failed, Toast.LENGTH_SHORT).show();
                                    mPostDetailRecyclerViewAdapterCallback.updatePost(mPost);
                                }
                            });
                }
            });

            mShareButton.setOnClickListener(view -> {
                Bundle bundle = new Bundle();
                bundle.putString(ShareLinkBottomSheetFragment.EXTRA_POST_LINK, mPost.getPermalink());
                if (mPost.getPostType() != Post.TEXT_TYPE) {
                    bundle.putInt(ShareLinkBottomSheetFragment.EXTRA_MEDIA_TYPE, mPost.getPostType());
                    switch (mPost.getPostType()) {
                        case Post.IMAGE_TYPE:
                        case Post.GIF_TYPE:
                        case Post.LINK_TYPE:
                        case Post.NO_PREVIEW_LINK_TYPE:
                            bundle.putString(ShareLinkBottomSheetFragment.EXTRA_MEDIA_LINK, mPost.getUrl());
                            break;
                        case Post.VIDEO_TYPE:
                            bundle.putString(ShareLinkBottomSheetFragment.EXTRA_MEDIA_LINK, mPost.getVideoDownloadUrl());
                            break;
                    }
                }
                ShareLinkBottomSheetFragment shareLinkBottomSheetFragment = new ShareLinkBottomSheetFragment();
                shareLinkBottomSheetFragment.setArguments(bundle);
                shareLinkBottomSheetFragment.show(mActivity.getSupportFragmentManager(), shareLinkBottomSheetFragment.getTag());
            });

            if (mVoteButtonsOnTheRight) {
                ConstraintSet constraintSet = new ConstraintSet();
                constraintSet.clone(mBottomConstraintLayout);
                constraintSet.clear(mUpvoteButton.getId(), ConstraintSet.START);
                constraintSet.clear(mScoreTextView.getId(), ConstraintSet.START);
                constraintSet.clear(mDownvoteButton.getId(), ConstraintSet.START);
                constraintSet.clear(mSaveButton.getId(), ConstraintSet.END);
                constraintSet.clear(mShareButton.getId(), ConstraintSet.END);
                constraintSet.connect(mUpvoteButton.getId(), ConstraintSet.END, mScoreTextView.getId(), ConstraintSet.START);
                constraintSet.connect(mScoreTextView.getId(), ConstraintSet.END, mDownvoteButton.getId(), ConstraintSet.START);
                constraintSet.connect(mDownvoteButton.getId(), ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END);
                constraintSet.connect(commentsCountTextView.getId(), ConstraintSet.START, mSaveButton.getId(), ConstraintSet.END);
                constraintSet.connect(commentsCountTextView.getId(), ConstraintSet.END, mUpvoteButton.getId(), ConstraintSet.START);
                constraintSet.connect(mSaveButton.getId(), ConstraintSet.START, mShareButton.getId(), ConstraintSet.END);
                constraintSet.connect(mShareButton.getId(), ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START);
                constraintSet.setHorizontalBias(commentsCountTextView.getId(), 0);
                constraintSet.applyTo(mBottomConstraintLayout);
            }

            itemView.setBackgroundColor(mCardViewColor);
            mSubredditTextView.setTextColor(mSubredditColor);
            mUserTextView.setTextColor(mUsernameColor);
            mAuthorFlairTextView.setTextColor(mAuthorFlairTextColor);
            mPostTimeTextView.setTextColor(mSecondaryTextColor);
            mTitleTextView.setTextColor(mPostTitleColor);
            mTypeTextView.setBackgroundColor(mPostTypeBackgroundColor);
            mTypeTextView.setBorderColor(mPostTypeBackgroundColor);
            mTypeTextView.setTextColor(mPostTypeTextColor);
            mSpoilerTextView.setBackgroundColor(mSpoilerBackgroundColor);
            mSpoilerTextView.setBorderColor(mSpoilerBackgroundColor);
            mSpoilerTextView.setTextColor(mSpoilerTextColor);
            mNSFWTextView.setBackgroundColor(mNSFWBackgroundColor);
            mNSFWTextView.setBorderColor(mNSFWBackgroundColor);
            mNSFWTextView.setTextColor(mNSFWTextColor);
            mFlairTextView.setBackgroundColor(mFlairBackgroundColor);
            mFlairTextView.setBorderColor(mFlairBackgroundColor);
            mFlairTextView.setTextColor(mFlairTextColor);
            mArchivedImageView.setColorFilter(mArchivedTintColor, PorterDuff.Mode.SRC_IN);
            mLockedImageView.setColorFilter(mLockedTintColor, PorterDuff.Mode.SRC_IN);
            mCrosspostImageView.setColorFilter(mCrosspostTintColor, PorterDuff.Mode.SRC_IN);
            mAwardsTextView.setTextColor(mSecondaryTextColor);
            Drawable upvoteRatioDrawable = Utils.getTintedDrawable(mActivity, R.drawable.ic_upvote_ratio, mUpvoteRatioTintColor);
            mUpvoteRatioTextView.setCompoundDrawablesWithIntrinsicBounds(
                    upvoteRatioDrawable, null, null, null);
            mUpvoteRatioTextView.setTextColor(mSecondaryTextColor);
            mUpvoteButton.setColorFilter(mPostIconAndInfoColor, PorterDuff.Mode.SRC_IN);
            mScoreTextView.setTextColor(mPostIconAndInfoColor);
            mDownvoteButton.setColorFilter(mPostIconAndInfoColor, PorterDuff.Mode.SRC_IN);
            commentsCountTextView.setTextColor(mPostIconAndInfoColor);
            commentsCountTextView.setCompoundDrawablesWithIntrinsicBounds(mCommentIcon, null, null, null);
            mSaveButton.setColorFilter(mPostIconAndInfoColor, PorterDuff.Mode.SRC_IN);
            mShareButton.setColorFilter(mPostIconAndInfoColor, PorterDuff.Mode.SRC_IN);
        }
    }

    class PostDetailVideoAutoplayViewHolder extends PostDetailBaseViewHolder implements ToroPlayer {
        @BindView(R.id.icon_gif_image_view_item_post_detail_video_autoplay)
        AspectRatioGifImageView mIconGifImageView;
        @BindView(R.id.subreddit_text_view_item_post_detail_video_autoplay)
        TextView mSubredditTextView;
        @BindView(R.id.user_text_view_item_post_detail_video_autoplay)
        TextView mUserTextView;
        @BindView(R.id.author_flair_text_view_item_post_detail_video_autoplay)
        TextView mAuthorFlairTextView;
        @BindView(R.id.post_time_text_view_item_post_detail_video_autoplay)
        TextView mPostTimeTextView;
        @BindView(R.id.title_text_view_item_post_detail_video_autoplay)
        TextView mTitleTextView;
        @BindView(R.id.type_text_view_item_post_detail_video_autoplay)
        CustomTextView mTypeTextView;
        @BindView(R.id.crosspost_image_view_item_post_detail_video_autoplay)
        ImageView mCrosspostImageView;
        @BindView(R.id.archived_image_view_item_post_detail_video_autoplay)
        ImageView mArchivedImageView;
        @BindView(R.id.locked_image_view_item_post_detail_video_autoplay)
        ImageView mLockedImageView;
        @BindView(R.id.nsfw_text_view_item_post_detail_video_autoplay)
        CustomTextView mNSFWTextView;
        @BindView(R.id.spoiler_custom_text_view_item_post_detail_video_autoplay)
        CustomTextView mSpoilerTextView;
        @BindView(R.id.flair_custom_text_view_item_post_detail_video_autoplay)
        CustomTextView mFlairTextView;
        @BindView(R.id.awards_text_view_item_post_detail_video_autoplay)
        TextView mAwardsTextView;
        @BindView(R.id.upvote_ratio_text_view_item_post_detail_video_autoplay)
        TextView mUpvoteRatioTextView;
        @BindView(R.id.aspect_ratio_frame_layout_item_post_detail_video_autoplay)
        AspectRatioFrameLayout aspectRatioFrameLayout;
        @BindView(R.id.player_view_item_post_detail_video_autoplay)
        PlayerView playerView;
        @BindView(R.id.preview_image_view_item_post_detail_video_autoplay)
        GifImageView previewImageView;
        @BindView(R.id.error_loading_gfycat_image_view_item_post_detail_video_autoplay)
        ImageView mErrorLoadingGfycatImageView;
        @BindView(R.id.mute_exo_playback_control_view)
        ImageView muteButton;
        @BindView(R.id.fullscreen_exo_playback_control_view)
        ImageView fullscreenButton;
        @BindView(R.id.bottom_constraint_layout_item_post_detail_video_autoplay)
        ConstraintLayout mBottomConstraintLayout;
        @BindView(R.id.plus_button_item_post_detail_video_autoplay)
        ImageView mUpvoteButton;
        @BindView(R.id.score_text_view_item_post_detail_video_autoplay)
        TextView mScoreTextView;
        @BindView(R.id.minus_button_item_post_detail_video_autoplay)
        ImageView mDownvoteButton;
        @BindView(R.id.comments_count_item_post_detail_video_autoplay)
        TextView commentsCountTextView;
        @BindView(R.id.save_button_item_post_detail_video_autoplay)
        ImageView mSaveButton;
        @BindView(R.id.share_button_item_post_detail_video_autoplay)
        ImageView mShareButton;

        @Nullable
        ExoPlayerViewHelper helper;
        private Uri mediaUri;
        private float volume;
        public FetchGfycatOrRedgifsVideoLinks fetchGfycatOrRedgifsVideoLinks;

        public PostDetailVideoAutoplayViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            setBaseView(mIconGifImageView,
                    mSubredditTextView,
                    mUserTextView,
                    mAuthorFlairTextView,
                    mPostTimeTextView,
                    mTitleTextView,
                    mTypeTextView,
                    mCrosspostImageView,
                    mArchivedImageView,
                    mLockedImageView,
                    mNSFWTextView,
                    mSpoilerTextView,
                    mFlairTextView,
                    mAwardsTextView,
                    mUpvoteRatioTextView,
                    mBottomConstraintLayout,
                    mUpvoteButton,
                    mScoreTextView,
                    mDownvoteButton,
                    commentsCountTextView,
                    mSaveButton,
                    mShareButton);

            aspectRatioFrameLayout.setOnClickListener(null);

            muteButton.setOnClickListener(view -> {
                if (helper != null) {
                    if (helper.getVolume() != 0) {
                        muteButton.setImageDrawable(mActivity.getDrawable(R.drawable.ic_mute_white_rounded_24dp));
                        helper.setVolume(0f);
                        volume = 0f;
                    } else {
                        muteButton.setImageDrawable(mActivity.getDrawable(R.drawable.ic_unmute_white_rounded_24dp));
                        helper.setVolume(1f);
                        volume = 1f;
                    }
                }
            });

            fullscreenButton.setOnClickListener(view -> {
                Intent intent = new Intent(mActivity, RedditViewVideoActivity.class);
                if (mPost.isGfycat()) {
                    intent.putExtra(RedditViewVideoActivity.EXTRA_VIDEO_TYPE, RedditViewVideoActivity.VIDEO_TYPE_GFYCAT);
                    intent.putExtra(RedditViewVideoActivity.EXTRA_GFYCAT_ID, mPost.getGfycatId());
                    if (mPost.isLoadGfyOrRedgifsVideoSuccess()) {
                        intent.setData(Uri.parse(mPost.getVideoUrl()));
                        intent.putExtra(RedditViewVideoActivity.EXTRA_VIDEO_DOWNLOAD_URL, mPost.getVideoDownloadUrl());
                    }
                } else if (mPost.isRedgifs()) {
                    intent.putExtra(RedditViewVideoActivity.EXTRA_VIDEO_TYPE, RedditViewVideoActivity.VIDEO_TYPE_REDGIFS);
                    intent.putExtra(RedditViewVideoActivity.EXTRA_GFYCAT_ID, mPost.getGfycatId());
                    if (mPost.isLoadGfyOrRedgifsVideoSuccess()) {
                        intent.setData(Uri.parse(mPost.getVideoUrl()));
                        intent.putExtra(RedditViewVideoActivity.EXTRA_VIDEO_DOWNLOAD_URL, mPost.getVideoDownloadUrl());
                    }
                } else {
                    intent.setData(Uri.parse(mPost.getVideoUrl()));
                    intent.putExtra(RedditViewVideoActivity.EXTRA_VIDEO_DOWNLOAD_URL, mPost.getVideoDownloadUrl());
                    intent.putExtra(RedditViewVideoActivity.EXTRA_SUBREDDIT, mPost.getSubredditName());
                    intent.putExtra(RedditViewVideoActivity.EXTRA_ID, mPost.getId());
                }
                intent.putExtra(RedditViewVideoActivity.EXTRA_POST_TITLE, mPost.getTitle());
                if (helper != null) {
                    intent.putExtra(RedditViewVideoActivity.EXTRA_PROGRESS_SECONDS, helper.getLatestPlaybackInfo().getResumePosition());
                }
                intent.putExtra(RedditViewVideoActivity.EXTRA_IS_NSFW, mPost.isNSFW());
                mActivity.startActivity(intent);
            });

            previewImageView.setOnLongClickListener(view -> fullscreenButton.performClick());
            playerView.setOnLongClickListener(view -> fullscreenButton.performClick());
        }

        void bindVideoUri(Uri videoUri) {
            mediaUri = videoUri;
        }

        void setVolume(float volume) {
            this.volume = volume;
        }

        void resetVolume() {
            volume = 0f;
        }

        @NonNull
        @Override
        public View getPlayerView() {
            return playerView;
        }

        @NonNull
        @Override
        public PlaybackInfo getCurrentPlaybackInfo() {
            return helper != null && mediaUri != null ? helper.getLatestPlaybackInfo() : new PlaybackInfo();
        }

        @Override
        public void initialize(@NonNull Container container, @NonNull PlaybackInfo playbackInfo) {
            if (mediaUri == null) {
                return;
            }
            if (helper == null) {
                helper = new ExoPlayerViewHelper(this, mediaUri, null, mExoCreator);
                helper.addEventListener(new Playable.EventListener() {
                    @Override
                    public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {
                        if (!trackGroups.isEmpty()) {
                            for (int i = 0; i < trackGroups.length; i++) {
                                String mimeType = trackGroups.get(i).getFormat(0).sampleMimeType;
                                if (mimeType != null && mimeType.contains("audio")) {
                                    helper.setVolume(volume);
                                    muteButton.setVisibility(View.VISIBLE);
                                    if (volume != 0f) {
                                        muteButton.setImageDrawable(mActivity.getDrawable(R.drawable.ic_unmute_white_rounded_24dp));
                                    } else {
                                        muteButton.setImageDrawable(mActivity.getDrawable(R.drawable.ic_mute_white_rounded_24dp));
                                    }
                                    break;
                                }
                            }
                        } else {
                            muteButton.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onMetadata(Metadata metadata) {

                    }

                    @Override
                    public void onCues(List<Cue> cues) {

                    }

                    @Override
                    public void onRenderedFirstFrame() {
                        mGlide.clear(previewImageView);
                        previewImageView.setVisibility(View.GONE);
                    }
                });
            }
            helper.initialize(container, playbackInfo);
        }

        @Override
        public void play() {
            if (helper != null && mediaUri != null) helper.play();
        }

        @Override
        public void pause() {
            if (helper != null) helper.pause();
        }

        @Override
        public boolean isPlaying() {
            return helper != null && helper.isPlaying();
        }

        @Override
        public void release() {
            if (helper != null) {
                helper.release();
                helper = null;
            }
        }

        @Override
        public boolean wantsToPlay() {
            return ToroUtil.visibleAreaOffset(this, itemView.getParent()) >= mStartAutoplayVisibleAreaOffset;
        }

        @Override
        public int getPlayerOrder() {
            return 0;
        }
    }

    class PostDetailVideoAndGifPreviewHolder extends PostDetailBaseViewHolder {
        @BindView(R.id.icon_gif_image_view_item_post_detail_video_and_gif_preview)
        AspectRatioGifImageView mIconGifImageView;
        @BindView(R.id.subreddit_text_view_item_post_detail_video_and_gif_preview)
        TextView mSubredditTextView;
        @BindView(R.id.user_text_view_item_post_detail_video_and_gif_preview)
        TextView mUserTextView;
        @BindView(R.id.author_flair_text_view_item_post_detail_video_and_gif_preview)
        TextView mAuthorFlairTextView;
        @BindView(R.id.post_time_text_view_item_post_detail_video_and_gif_preview)
        TextView mPostTimeTextView;
        @BindView(R.id.title_text_view_item_post_detail_video_and_gif_preview)
        TextView mTitleTextView;
        @BindView(R.id.type_text_view_item_post_detail_video_and_gif_preview)
        CustomTextView mTypeTextView;
        @BindView(R.id.crosspost_image_view_item_post_detail_video_and_gif_preview)
        ImageView mCrosspostImageView;
        @BindView(R.id.archived_image_view_item_post_detail_video_and_gif_preview)
        ImageView mArchivedImageView;
        @BindView(R.id.locked_image_view_item_post_detail_video_and_gif_preview)
        ImageView mLockedImageView;
        @BindView(R.id.nsfw_text_view_item_post_detail_video_and_gif_preview)
        CustomTextView mNSFWTextView;
        @BindView(R.id.spoiler_custom_text_view_item_post_detail_video_and_gif_preview)
        CustomTextView mSpoilerTextView;
        @BindView(R.id.flair_custom_text_view_item_post_detail_video_and_gif_preview)
        CustomTextView mFlairTextView;
        @BindView(R.id.awards_text_view_item_post_detail_video_and_gif_preview)
        TextView mAwardsTextView;
        @BindView(R.id.upvote_ratio_text_view_item_post_detail_video_and_gif_preview)
        TextView mUpvoteRatioTextView;
        @BindView(R.id.load_wrapper_item_post_detail_video_and_gif_preview)
        RelativeLayout mLoadWrapper;
        @BindView(R.id.progress_bar_item_post_detail_video_and_gif_preview)
        ProgressBar mLoadImageProgressBar;
        @BindView(R.id.load_image_error_text_view_item_post_detail_video_and_gif_preview)
        TextView mLoadImageErrorTextView;
        @BindView(R.id.image_view_item_post_detail_video_and_gif_preview)
        AspectRatioGifImageView mImageView;
        @BindView(R.id.bottom_constraint_layout_item_post_detail_video_and_gif_preview)
        ConstraintLayout mBottomConstraintLayout;
        @BindView(R.id.plus_button_item_post_detail_video_and_gif_preview)
        ImageView mUpvoteButton;
        @BindView(R.id.score_text_view_item_post_detail_video_and_gif_preview)
        TextView mScoreTextView;
        @BindView(R.id.minus_button_item_post_detail_video_and_gif_preview)
        ImageView mDownvoteButton;
        @BindView(R.id.comments_count_item_post_detail_video_and_gif_preview)
        TextView commentsCountTextView;
        @BindView(R.id.save_button_item_post_detail_video_and_gif_preview)
        ImageView mSaveButton;
        @BindView(R.id.share_button_item_post_detail_video_and_gif_preview)
        ImageView mShareButton;

        PostDetailVideoAndGifPreviewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            setBaseView(mIconGifImageView,
                    mSubredditTextView,
                    mUserTextView,
                    mAuthorFlairTextView,
                    mPostTimeTextView,
                    mTitleTextView,
                    mTypeTextView,
                    mCrosspostImageView,
                    mArchivedImageView,
                    mLockedImageView,
                    mNSFWTextView,
                    mSpoilerTextView,
                    mFlairTextView,
                    mAwardsTextView,
                    mUpvoteRatioTextView,
                    mBottomConstraintLayout,
                    mUpvoteButton,
                    mScoreTextView,
                    mDownvoteButton,
                    commentsCountTextView,
                    mSaveButton,
                    mShareButton);

            mLoadImageProgressBar.setIndeterminateTintList(ColorStateList.valueOf(mColorAccent));
            mLoadImageErrorTextView.setTextColor(mPrimaryTextColor);

            mImageView.setOnClickListener(view -> {
                if (mPost.getPostType() == Post.VIDEO_TYPE) {
                    Intent intent = new Intent(mActivity, RedditViewVideoActivity.class);
                    if (mPost.isGfycat()) {
                        intent.putExtra(RedditViewVideoActivity.EXTRA_VIDEO_TYPE, RedditViewVideoActivity.VIDEO_TYPE_GFYCAT);
                        intent.putExtra(RedditViewVideoActivity.EXTRA_GFYCAT_ID, mPost.getGfycatId());
                    } else if (mPost.isRedgifs()) {
                        intent.putExtra(RedditViewVideoActivity.EXTRA_VIDEO_TYPE, RedditViewVideoActivity.VIDEO_TYPE_REDGIFS);
                        intent.putExtra(RedditViewVideoActivity.EXTRA_GFYCAT_ID, mPost.getGfycatId());
                    } else {
                        intent.setData(Uri.parse(mPost.getVideoUrl()));
                        intent.putExtra(RedditViewVideoActivity.EXTRA_SUBREDDIT, mPost.getSubredditName());
                        intent.putExtra(RedditViewVideoActivity.EXTRA_ID, mPost.getId());
                        intent.putExtra(RedditViewVideoActivity.EXTRA_VIDEO_DOWNLOAD_URL, mPost.getVideoDownloadUrl());
                    }
                    intent.putExtra(RedditViewVideoActivity.EXTRA_POST_TITLE, mPost.getTitle());
                    intent.putExtra(RedditViewVideoActivity.EXTRA_IS_NSFW, mPost.isNSFW());
                    mActivity.startActivity(intent);
                } else if (mPost.getPostType() == Post.GIF_TYPE) {
                    Intent intent = new Intent(mActivity, ViewImageOrGifActivity.class);
                    intent.putExtra(ViewImageOrGifActivity.EXTRA_FILE_NAME_KEY, mPost.getSubredditName()
                            + "-" + mPost.getId() + ".gif");
                    intent.putExtra(ViewImageOrGifActivity.EXTRA_GIF_URL_KEY, mPost.getVideoUrl());
                    intent.putExtra(ViewImageOrGifActivity.EXTRA_POST_TITLE_KEY, mPost.getTitle());
                    intent.putExtra(ViewImageOrGifActivity.EXTRA_SUBREDDIT_OR_USERNAME_KEY, mPost.getSubredditName());
                    mActivity.startActivity(intent);
                }
            });
        }
    }

    class PostDetailImageAndGifAutoplayViewHolder extends PostDetailBaseViewHolder {
        @BindView(R.id.icon_gif_image_view_item_post_detail_image_and_gif_autoplay)
        AspectRatioGifImageView mIconGifImageView;
        @BindView(R.id.subreddit_text_view_item_post_detail_image_and_gif_autoplay)
        TextView mSubredditTextView;
        @BindView(R.id.user_text_view_item_post_detail_image_and_gif_autoplay)
        TextView mUserTextView;
        @BindView(R.id.author_flair_text_view_item_post_detail_image_and_gif_autoplay)
        TextView mAuthorFlairTextView;
        @BindView(R.id.post_time_text_view_item_post_detail_image_and_gif_autoplay)
        TextView mPostTimeTextView;
        @BindView(R.id.title_text_view_item_post_detail_image_and_gif_autoplay)
        TextView mTitleTextView;
        @BindView(R.id.type_text_view_item_post_detail_image_and_gif_autoplay)
        CustomTextView mTypeTextView;
        @BindView(R.id.crosspost_image_view_item_post_detail_image_and_gif_autoplay)
        ImageView mCrosspostImageView;
        @BindView(R.id.archived_image_view_item_post_detail_image_and_gif_autoplay)
        ImageView mArchivedImageView;
        @BindView(R.id.locked_image_view_item_post_detail_image_and_gif_autoplay)
        ImageView mLockedImageView;
        @BindView(R.id.nsfw_text_view_item_post_detail_image_and_gif_autoplay)
        CustomTextView mNSFWTextView;
        @BindView(R.id.spoiler_custom_text_view_item_post_detail_image_and_gif_autoplay)
        CustomTextView mSpoilerTextView;
        @BindView(R.id.flair_custom_text_view_item_post_detail_image_and_gif_autoplay)
        CustomTextView mFlairTextView;
        @BindView(R.id.awards_text_view_item_post_detail_image_and_gif_autoplay)
        TextView mAwardsTextView;
        @BindView(R.id.upvote_ratio_text_view_item_post_detail_image_and_gif_autoplay)
        TextView mUpvoteRatioTextView;
        @BindView(R.id.image_view_wrapper_item_post_detail_image_and_gif_autoplay)
        RelativeLayout mRelativeLayout;
        @BindView(R.id.load_wrapper_item_post_detail_image_and_gif_autoplay)
        RelativeLayout mLoadWrapper;
        @BindView(R.id.progress_bar_item_post_detail_image_and_gif_autoplay)
        ProgressBar mLoadImageProgressBar;
        @BindView(R.id.load_image_error_text_view_item_post_detail_image_and_gif_autoplay)
        TextView mLoadImageErrorTextView;
        @BindView(R.id.image_view_item_post_detail_image_and_gif_autoplay)
        AspectRatioGifImageView mImageView;
        @BindView(R.id.bottom_constraint_layout_item_post_detail_image_and_gif_autoplay)
        ConstraintLayout mBottomConstraintLayout;
        @BindView(R.id.plus_button_item_post_detail_image_and_gif_autoplay)
        ImageView mUpvoteButton;
        @BindView(R.id.score_text_view_item_post_detail_image_and_gif_autoplay)
        TextView mScoreTextView;
        @BindView(R.id.minus_button_item_post_detail_image_and_gif_autoplay)
        ImageView mDownvoteButton;
        @BindView(R.id.comments_count_item_post_detail_image_and_gif_autoplay)
        TextView commentsCountTextView;
        @BindView(R.id.save_button_item_post_detail_image_and_gif_autoplay)
        ImageView mSaveButton;
        @BindView(R.id.share_button_item_post_detail_image_and_gif_autoplay)
        ImageView mShareButton;

        PostDetailImageAndGifAutoplayViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            setBaseView(mIconGifImageView,
                    mSubredditTextView,
                    mUserTextView,
                    mAuthorFlairTextView,
                    mPostTimeTextView,
                    mTitleTextView,
                    mTypeTextView,
                    mCrosspostImageView,
                    mArchivedImageView,
                    mLockedImageView,
                    mNSFWTextView,
                    mSpoilerTextView,
                    mFlairTextView,
                    mAwardsTextView,
                    mUpvoteRatioTextView,
                    mBottomConstraintLayout,
                    mUpvoteButton,
                    mScoreTextView,
                    mDownvoteButton,
                    commentsCountTextView,
                    mSaveButton,
                    mShareButton);

            mLoadImageProgressBar.setIndeterminateTintList(ColorStateList.valueOf(mColorAccent));
            mLoadImageErrorTextView.setTextColor(mPrimaryTextColor);

            mImageView.setOnClickListener(view -> {
                if (mPost.getPostType() == Post.IMAGE_TYPE) {
                    Intent intent = new Intent(mActivity, ViewImageOrGifActivity.class);
                    intent.putExtra(ViewImageOrGifActivity.EXTRA_IMAGE_URL_KEY, mPost.getUrl());
                    intent.putExtra(ViewImageOrGifActivity.EXTRA_FILE_NAME_KEY, mPost.getSubredditNamePrefixed().substring(2)
                            + "-" + mPost.getId().substring(3) + ".jpg");
                    intent.putExtra(ViewImageOrGifActivity.EXTRA_POST_TITLE_KEY, mPost.getTitle());
                    intent.putExtra(ViewImageOrGifActivity.EXTRA_SUBREDDIT_OR_USERNAME_KEY, mPost.getSubredditName());
                    mActivity.startActivity(intent);
                } else if (mPost.getPostType() == Post.GIF_TYPE) {
                    Intent intent = new Intent(mActivity, ViewImageOrGifActivity.class);
                    intent.putExtra(ViewImageOrGifActivity.EXTRA_FILE_NAME_KEY, mPost.getSubredditName()
                            + "-" + mPost.getId() + ".gif");
                    intent.putExtra(ViewImageOrGifActivity.EXTRA_GIF_URL_KEY, mPost.getVideoUrl());
                    intent.putExtra(ViewImageOrGifActivity.EXTRA_POST_TITLE_KEY, mPost.getTitle());
                    intent.putExtra(ViewImageOrGifActivity.EXTRA_SUBREDDIT_OR_USERNAME_KEY, mPost.getSubredditName());
                    mActivity.startActivity(intent);
                }
            });
        }
    }

    class PostDetailLinkViewHolder extends PostDetailBaseViewHolder {
        @BindView(R.id.icon_gif_image_view_item_post_detail_link)
        AspectRatioGifImageView mIconGifImageView;
        @BindView(R.id.subreddit_text_view_item_post_detail_link)
        TextView mSubredditTextView;
        @BindView(R.id.user_text_view_item_post_detail_link)
        TextView mUserTextView;
        @BindView(R.id.author_flair_text_view_item_post_detail_link)
        TextView mAuthorFlairTextView;
        @BindView(R.id.post_time_text_view_item_post_detail_link)
        TextView mPostTimeTextView;
        @BindView(R.id.title_text_view_item_post_detail_link)
        TextView mTitleTextView;
        @BindView(R.id.type_text_view_item_post_detail_link)
        CustomTextView mTypeTextView;
        @BindView(R.id.crosspost_image_view_item_post_detail_link)
        ImageView mCrosspostImageView;
        @BindView(R.id.archived_image_view_item_post_detail_link)
        ImageView mArchivedImageView;
        @BindView(R.id.locked_image_view_item_post_detail_link)
        ImageView mLockedImageView;
        @BindView(R.id.nsfw_text_view_item_post_detail_link)
        CustomTextView mNSFWTextView;
        @BindView(R.id.spoiler_custom_text_view_item_post_detail_link)
        CustomTextView mSpoilerTextView;
        @BindView(R.id.flair_custom_text_view_item_post_detail_link)
        CustomTextView mFlairTextView;
        @BindView(R.id.awards_text_view_item_post_detail_link)
        TextView mAwardsTextView;
        @BindView(R.id.upvote_ratio_text_view_item_post_detail_link)
        TextView mUpvoteRatioTextView;
        @BindView(R.id.link_text_view_item_post_detail_link)
        TextView mLinkTextView;
        @BindView(R.id.image_view_wrapper_item_post_detail_link)
        RelativeLayout mRelativeLayout;
        @BindView(R.id.load_wrapper_item_post_detail_link)
        RelativeLayout mLoadWrapper;
        @BindView(R.id.progress_bar_item_post_detail_link)
        ProgressBar mLoadImageProgressBar;
        @BindView(R.id.load_image_error_text_view_item_post_detail_link)
        TextView mLoadImageErrorTextView;
        @BindView(R.id.image_view_item_post_detail_link)
        AspectRatioGifImageView mImageView;
        @BindView(R.id.bottom_constraint_layout_item_post_detail_link)
        ConstraintLayout mBottomConstraintLayout;
        @BindView(R.id.plus_button_item_post_detail_link)
        ImageView mUpvoteButton;
        @BindView(R.id.score_text_view_item_post_detail_link)
        TextView mScoreTextView;
        @BindView(R.id.minus_button_item_post_detail_link)
        ImageView mDownvoteButton;
        @BindView(R.id.comments_count_item_post_detail_link)
        TextView commentsCountTextView;
        @BindView(R.id.save_button_item_post_detail_link)
        ImageView mSaveButton;
        @BindView(R.id.share_button_item_post_detail_link)
        ImageView mShareButton;

        PostDetailLinkViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            setBaseView(mIconGifImageView,
                    mSubredditTextView,
                    mUserTextView,
                    mAuthorFlairTextView,
                    mPostTimeTextView,
                    mTitleTextView,
                    mTypeTextView,
                    mCrosspostImageView,
                    mArchivedImageView,
                    mLockedImageView,
                    mNSFWTextView,
                    mSpoilerTextView,
                    mFlairTextView,
                    mAwardsTextView,
                    mUpvoteRatioTextView,
                    mBottomConstraintLayout,
                    mUpvoteButton,
                    mScoreTextView,
                    mDownvoteButton,
                    commentsCountTextView,
                    mSaveButton,
                    mShareButton);

            mLinkTextView.setTextColor(mSecondaryTextColor);
            mLoadImageProgressBar.setIndeterminateTintList(ColorStateList.valueOf(mColorAccent));
            mLoadImageErrorTextView.setTextColor(mPrimaryTextColor);

            mImageView.setOnClickListener(view -> {
                Intent intent = new Intent(mActivity, RedditLinkResolverActivity.class);
                Uri uri = Uri.parse(mPost.getUrl());
                intent.setData(uri);
                intent.putExtra(RedditLinkResolverActivity.EXTRA_IS_NSFW, mPost.isNSFW());
                mActivity.startActivity(intent);
            });
        }
    }

    class PostDetailNoPreviewViewHolder extends PostDetailBaseViewHolder {
        @BindView(R.id.icon_gif_image_view_item_post_detail_no_preview_link)
        AspectRatioGifImageView mIconGifImageView;
        @BindView(R.id.subreddit_text_view_item_post_detail_no_preview_link)
        TextView mSubredditTextView;
        @BindView(R.id.user_text_view_item_post_detail_no_preview_link)
        TextView mUserTextView;
        @BindView(R.id.author_flair_text_view_item_post_detail_no_preview_link)
        TextView mAuthorFlairTextView;
        @BindView(R.id.post_time_text_view_item_post_detail_no_preview_link)
        TextView mPostTimeTextView;
        @BindView(R.id.title_text_view_item_post_detail_no_preview_link)
        TextView mTitleTextView;
        @BindView(R.id.content_markdown_view_item_post_detail_no_preview_link)
        RecyclerView mContentMarkdownView;
        @BindView(R.id.type_text_view_item_post_detail_no_preview_link)
        CustomTextView mTypeTextView;
        @BindView(R.id.crosspost_image_view_item_post_detail_no_preview_link)
        ImageView mCrosspostImageView;
        @BindView(R.id.archived_image_view_item_post_detail_no_preview_link)
        ImageView mArchivedImageView;
        @BindView(R.id.locked_image_view_item_post_detail_no_preview_link)
        ImageView mLockedImageView;
        @BindView(R.id.nsfw_text_view_item_post_detail_no_preview_link)
        CustomTextView mNSFWTextView;
        @BindView(R.id.spoiler_custom_text_view_item_post_detail_no_preview_link)
        CustomTextView mSpoilerTextView;
        @BindView(R.id.flair_custom_text_view_item_post_detail_no_preview_link)
        CustomTextView mFlairTextView;
        @BindView(R.id.awards_text_view_item_post_detail_no_preview_link)
        TextView mAwardsTextView;
        @BindView(R.id.upvote_ratio_text_view_item_post_detail_no_preview_link)
        TextView mUpvoteRatioTextView;
        @BindView(R.id.link_text_view_item_post_detail_no_preview_link)
        TextView mLinkTextView;
        @BindView(R.id.image_view_no_preview_post_type_item_post_detail_no_preview_link)
        ImageView mNoPreviewPostTypeImageView;
        @BindView(R.id.bottom_constraint_layout_item_post_detail_no_preview_link)
        ConstraintLayout mBottomConstraintLayout;
        @BindView(R.id.plus_button_item_post_detail_no_preview_link)
        ImageView mUpvoteButton;
        @BindView(R.id.score_text_view_item_post_detail_no_preview_link)
        TextView mScoreTextView;
        @BindView(R.id.minus_button_item_post_detail_no_preview_link)
        ImageView mDownvoteButton;
        @BindView(R.id.comments_count_item_post_detail_no_preview_link)
        TextView commentsCountTextView;
        @BindView(R.id.save_button_item_post_detail_no_preview_link)
        ImageView mSaveButton;
        @BindView(R.id.share_button_item_post_detail_no_preview_link)
        ImageView mShareButton;

        PostDetailNoPreviewViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            setBaseView(mIconGifImageView,
                    mSubredditTextView,
                    mUserTextView,
                    mAuthorFlairTextView,
                    mPostTimeTextView,
                    mTitleTextView,
                    mTypeTextView,
                    mCrosspostImageView,
                    mArchivedImageView,
                    mLockedImageView,
                    mNSFWTextView,
                    mSpoilerTextView,
                    mFlairTextView,
                    mAwardsTextView,
                    mUpvoteRatioTextView,
                    mBottomConstraintLayout,
                    mUpvoteButton,
                    mScoreTextView,
                    mDownvoteButton,
                    commentsCountTextView,
                    mSaveButton,
                    mShareButton);

            mLinkTextView.setTextColor(mSecondaryTextColor);
            mNoPreviewPostTypeImageView.setBackgroundColor(mNoPreviewPostTypeBackgroundColor);
            mNoPreviewPostTypeImageView.setColorFilter(mNoPreviewPostTypeIconTint, PorterDuff.Mode.SRC_IN);

            mNoPreviewPostTypeImageView.setOnClickListener(view -> {
                if (mPost != null) {
                    if (mPost.getPostType() == Post.VIDEO_TYPE) {
                        Intent intent = new Intent(mActivity, RedditViewVideoActivity.class);
                        if (mPost.isGfycat()) {
                            intent.putExtra(RedditViewVideoActivity.EXTRA_VIDEO_TYPE, RedditViewVideoActivity.VIDEO_TYPE_GFYCAT);
                            intent.putExtra(RedditViewVideoActivity.EXTRA_GFYCAT_ID, mPost.getGfycatId());
                        } else if (mPost.isRedgifs()) {
                            intent.putExtra(RedditViewVideoActivity.EXTRA_VIDEO_TYPE, RedditViewVideoActivity.VIDEO_TYPE_REDGIFS);
                            intent.putExtra(RedditViewVideoActivity.EXTRA_GFYCAT_ID, mPost.getGfycatId());
                        } else {
                            intent.setData(Uri.parse(mPost.getVideoUrl()));
                            intent.putExtra(RedditViewVideoActivity.EXTRA_SUBREDDIT, mPost.getSubredditName());
                            intent.putExtra(RedditViewVideoActivity.EXTRA_ID, mPost.getId());
                            intent.putExtra(RedditViewVideoActivity.EXTRA_VIDEO_DOWNLOAD_URL, mPost.getVideoDownloadUrl());
                        }
                        intent.putExtra(RedditViewVideoActivity.EXTRA_POST_TITLE, mPost.getTitle());
                        intent.putExtra(RedditViewVideoActivity.EXTRA_IS_NSFW, mPost.isNSFW());
                        mActivity.startActivity(intent);
                    } else if (mPost.getPostType() == Post.IMAGE_TYPE) {
                        Intent intent = new Intent(mActivity, ViewImageOrGifActivity.class);
                        intent.putExtra(ViewImageOrGifActivity.EXTRA_IMAGE_URL_KEY, mPost.getUrl());
                        intent.putExtra(ViewImageOrGifActivity.EXTRA_FILE_NAME_KEY, mPost.getSubredditName()
                                + "-" + mPost.getId() + ".jpg");
                        intent.putExtra(ViewImageOrGifActivity.EXTRA_POST_TITLE_KEY, mPost.getTitle());
                        intent.putExtra(ViewImageOrGifActivity.EXTRA_SUBREDDIT_OR_USERNAME_KEY, mPost.getSubredditName());
                        mActivity.startActivity(intent);
                    } else if (mPost.getPostType() == Post.GIF_TYPE){
                        Intent intent = new Intent(mActivity, ViewImageOrGifActivity.class);
                        intent.putExtra(ViewImageOrGifActivity.EXTRA_FILE_NAME_KEY, mPost.getSubredditName()
                                + "-" + mPost.getId() + ".gif");
                        intent.putExtra(ViewImageOrGifActivity.EXTRA_GIF_URL_KEY, mPost.getVideoUrl());
                        intent.putExtra(ViewImageOrGifActivity.EXTRA_POST_TITLE_KEY, mPost.getTitle());
                        intent.putExtra(ViewImageOrGifActivity.EXTRA_SUBREDDIT_OR_USERNAME_KEY, mPost.getSubredditName());
                        mActivity.startActivity(intent);
                    } else if (mPost.getPostType() == Post.LINK_TYPE || mPost.getPostType() == Post.NO_PREVIEW_LINK_TYPE) {
                        Intent intent = new Intent(mActivity, RedditLinkResolverActivity.class);
                        Uri uri = Uri.parse(mPost.getUrl());
                        intent.setData(uri);
                        intent.putExtra(RedditLinkResolverActivity.EXTRA_IS_NSFW, mPost.isNSFW());
                        mActivity.startActivity(intent);
                    } else if (mPost.getPostType() == Post.GALLERY_TYPE) {
                        Intent intent = new Intent(mActivity, RedditViewRedditGalleryActivity.class);
                        intent.putParcelableArrayListExtra(RedditViewRedditGalleryActivity.EXTRA_REDDIT_GALLERY, mPost.getGallery());
                        intent.putExtra(RedditViewRedditGalleryActivity.EXTRA_SUBREDDIT_NAME, mPost.getSubredditName());
                        mActivity.startActivity(intent);
                    }
                }
            });
        }
    }

    class PostDetailGalleryViewHolder extends PostDetailBaseViewHolder {
        @BindView(R.id.icon_gif_image_view_item_post_detail_gallery)
        AspectRatioGifImageView mIconGifImageView;
        @BindView(R.id.subreddit_text_view_item_post_detail_gallery)
        TextView mSubredditTextView;
        @BindView(R.id.user_text_view_item_post_detail_gallery)
        TextView mUserTextView;
        @BindView(R.id.author_flair_text_view_item_post_detail_gallery)
        TextView mAuthorFlairTextView;
        @BindView(R.id.post_time_text_view_item_post_detail_gallery)
        TextView mPostTimeTextView;
        @BindView(R.id.title_text_view_item_post_detail_gallery)
        TextView mTitleTextView;
        @BindView(R.id.type_text_view_item_post_detail_gallery)
        CustomTextView mTypeTextView;
        @BindView(R.id.crosspost_image_view_item_post_detail_gallery)
        ImageView mCrosspostImageView;
        @BindView(R.id.archived_image_view_item_post_detail_gallery)
        ImageView mArchivedImageView;
        @BindView(R.id.locked_image_view_item_post_detail_gallery)
        ImageView mLockedImageView;
        @BindView(R.id.nsfw_text_view_item_post_detail_gallery)
        CustomTextView mNSFWTextView;
        @BindView(R.id.spoiler_custom_text_view_item_post_detail_gallery)
        CustomTextView mSpoilerTextView;
        @BindView(R.id.flair_custom_text_view_item_post_detail_gallery)
        CustomTextView mFlairTextView;
        @BindView(R.id.awards_text_view_item_post_detail_gallery)
        TextView mAwardsTextView;
        @BindView(R.id.upvote_ratio_text_view_item_post_detail_gallery)
        TextView mUpvoteRatioTextView;
        @BindView(R.id.image_view_wrapper_item_post_detail_gallery)
        RelativeLayout mRelativeLayout;
        @BindView(R.id.load_wrapper_item_post_detail_gallery)
        RelativeLayout mLoadWrapper;
        @BindView(R.id.progress_bar_item_post_detail_gallery)
        ProgressBar mLoadImageProgressBar;
        @BindView(R.id.load_image_error_text_view_item_post_detail_gallery)
        TextView mLoadImageErrorTextView;
        @BindView(R.id.image_view_item_post_detail_gallery)
        AspectRatioGifImageView mImageView;
        @BindView(R.id.image_view_no_preview_link_item_post_detail_gallery)
        ImageView mNoPreviewPostTypeImageView;
        @BindView(R.id.bottom_constraint_layout_item_post_detail_gallery)
        ConstraintLayout mBottomConstraintLayout;
        @BindView(R.id.plus_button_item_post_detail_gallery)
        ImageView mUpvoteButton;
        @BindView(R.id.score_text_view_item_post_detail_gallery)
        TextView mScoreTextView;
        @BindView(R.id.minus_button_item_post_detail_gallery)
        ImageView mDownvoteButton;
        @BindView(R.id.comments_count_item_post_detail_gallery)
        TextView commentsCountTextView;
        @BindView(R.id.save_button_item_post_detail_gallery)
        ImageView mSaveButton;
        @BindView(R.id.share_button_item_post_detail_gallery)
        ImageView mShareButton;

        PostDetailGalleryViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            setBaseView(mIconGifImageView,
                    mSubredditTextView,
                    mUserTextView,
                    mAuthorFlairTextView,
                    mPostTimeTextView,
                    mTitleTextView,
                    mTypeTextView,
                    mCrosspostImageView,
                    mArchivedImageView,
                    mLockedImageView,
                    mNSFWTextView,
                    mSpoilerTextView,
                    mFlairTextView,
                    mAwardsTextView,
                    mUpvoteRatioTextView,
                    mBottomConstraintLayout,
                    mUpvoteButton,
                    mScoreTextView,
                    mDownvoteButton,
                    commentsCountTextView,
                    mSaveButton,
                    mShareButton);

            mLoadImageProgressBar.setIndeterminateTintList(ColorStateList.valueOf(mColorAccent));
            mLoadImageErrorTextView.setTextColor(mPrimaryTextColor);
            mNoPreviewPostTypeImageView.setBackgroundColor(mNoPreviewPostTypeBackgroundColor);
            mNoPreviewPostTypeImageView.setColorFilter(mNoPreviewPostTypeIconTint, PorterDuff.Mode.SRC_IN);

            mImageView.setOnClickListener(view -> {
                Intent intent = new Intent(mActivity, RedditViewRedditGalleryActivity.class);
                intent.putParcelableArrayListExtra(RedditViewRedditGalleryActivity.EXTRA_REDDIT_GALLERY, mPost.getGallery());
                intent.putExtra(RedditViewRedditGalleryActivity.EXTRA_SUBREDDIT_NAME, mPost.getSubredditName());
                mActivity.startActivity(intent);
            });

            mNoPreviewPostTypeImageView.setOnClickListener(view -> {
                mImageView.performClick();
            });
        }
    }

    class PostDetailTextViewHolder extends PostDetailBaseViewHolder {
        @BindView(R.id.icon_gif_image_view_item_post_detail_text)
        AspectRatioGifImageView mIconGifImageView;
        @BindView(R.id.subreddit_text_view_item_post_detail_text)
        TextView mSubredditTextView;
        @BindView(R.id.user_text_view_item_post_detail_text)
        TextView mUserTextView;
        @BindView(R.id.author_flair_text_view_item_post_detail_text)
        TextView mAuthorFlairTextView;
        @BindView(R.id.post_time_text_view_item_post_detail_text)
        TextView mPostTimeTextView;
        @BindView(R.id.title_text_view_item_post_detail_text)
        TextView mTitleTextView;
        @BindView(R.id.content_markdown_view_item_post_detail_text)
        RecyclerView mContentMarkdownView;
        @BindView(R.id.type_text_view_item_post_detail_text)
        CustomTextView mTypeTextView;
        @BindView(R.id.crosspost_image_view_item_post_detail_text)
        ImageView mCrosspostImageView;
        @BindView(R.id.archived_image_view_item_post_detail_text)
        ImageView mArchivedImageView;
        @BindView(R.id.locked_image_view_item_post_detail_text)
        ImageView mLockedImageView;
        @BindView(R.id.nsfw_text_view_item_post_detail_text)
        CustomTextView mNSFWTextView;
        @BindView(R.id.spoiler_custom_text_view_item_post_detail_text)
        CustomTextView mSpoilerTextView;
        @BindView(R.id.flair_custom_text_view_item_post_detail_text)
        CustomTextView mFlairTextView;
        @BindView(R.id.awards_text_view_item_post_detail_text)
        TextView mAwardsTextView;
        @BindView(R.id.upvote_ratio_text_view_item_post_detail_text)
        TextView mUpvoteRatioTextView;
        @BindView(R.id.bottom_constraint_layout_item_post_detail_text)
        ConstraintLayout mBottomConstraintLayout;
        @BindView(R.id.plus_button_item_post_detail_text)
        ImageView mUpvoteButton;
        @BindView(R.id.score_text_view_item_post_detail_text)
        TextView mScoreTextView;
        @BindView(R.id.minus_button_item_post_detail_text)
        ImageView mDownvoteButton;
        @BindView(R.id.comments_count_item_post_detail_text)
        TextView commentsCountTextView;
        @BindView(R.id.save_button_item_post_detail_text)
        ImageView mSaveButton;
        @BindView(R.id.share_button_item_post_detail_text)
        ImageView mShareButton;

        PostDetailTextViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            setBaseView(mIconGifImageView,
                    mSubredditTextView,
                    mUserTextView,
                    mAuthorFlairTextView,
                    mPostTimeTextView,
                    mTitleTextView,
                    mTypeTextView,
                    mCrosspostImageView,
                    mArchivedImageView,
                    mLockedImageView,
                    mNSFWTextView,
                    mSpoilerTextView,
                    mFlairTextView,
                    mAwardsTextView,
                    mUpvoteRatioTextView,
                    mBottomConstraintLayout,
                    mUpvoteButton,
                    mScoreTextView,
                    mDownvoteButton,
                    commentsCountTextView,
                    mSaveButton,
                    mShareButton);
        }
    }
}
