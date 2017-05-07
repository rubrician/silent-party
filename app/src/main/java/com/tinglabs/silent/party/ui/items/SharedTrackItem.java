package com.tinglabs.silent.party.ui.items;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.mikepenz.fastadapter.items.AbstractItem;
import com.orm.query.Condition;
import com.orm.query.Select;
import com.tinglabs.silent.party.conf.Config;
import com.tinglabs.silent.party.model.Party;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import com.tinglabs.silent.party.R;

import com.tinglabs.silent.party.model.Track;

/**
 * Created by Talal on 1/5/2017.
 */

public class SharedTrackItem extends AbstractItem<SharedTrackItem, SharedTrackItem.ViewHolder> {

    public static final String TAG = "SharedTrackItem";

    private Track track;
    private boolean isAdded = false;
    private Mode mode = Mode.MENU;

    public enum Mode {
        MENU,
        SELECTION,
        VOTE,
        NONE
    }

    public SharedTrackItem(Track track) {
        this.track = track;
    }

    public SharedTrackItem(Track track, Mode mode) {
        this.track = track;
        this.mode = mode;
    }

    public Track getTrack() {
        return track;
    }

    public void setTrack(Track track) {
        this.track = track;
    }

    public Mode getMode() {
        return mode;
    }

    public void setMode(Mode mode) {
        this.mode = mode;
    }

    @Override
    public int getType() {
        return R.id.shared_track_item;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.shared_track_item_view;
    }

    @Override
    public void bindView(ViewHolder viewHolder, List<Object> payloads) {
        super.bindView(viewHolder, payloads);
        //get the context
        Context ctx = viewHolder.itemView.getContext();

        viewHolder.title.setText(track.getTitle());
        viewHolder.description.setText(track.getDescription());
        viewHolder.votes.setText("" + track.getVotes());

        // Load image
        Glide.with(ctx).load(track.getArtworkUrl()).crossFade().placeholder(R.drawable.ic_headset_black_48dp).into(viewHolder.imageView);

        // Set selection drawable
        switch (mode) {
            case MENU:
                viewHolder.imgBtn.setImageDrawable(ctx.getResources().getDrawable(R.drawable.ic_menu_grey_600_24dp));
                viewHolder.votes.setVisibility(View.GONE);
                break;
            case SELECTION:
                viewHolder.imgBtn.setImageDrawable(ctx.getResources().getDrawable(R.drawable.ic_playlist_add_grey_600_24dp));
                viewHolder.votes.setVisibility(View.GONE);
                break;
            case VOTE:
                viewHolder.imgBtn.setImageDrawable(ctx.getResources().getDrawable(R.drawable.ic_thumb_up_blue_grey_200_24dp));
                viewHolder.votes.setVisibility(View.VISIBLE);
                break;
            case NONE:
                viewHolder.imgBtn.setVisibility(View.GONE);
                viewHolder.votes.setVisibility(View.GONE);
        }
    }

    //reset the view here (this is an optional method, but recommended)
    @Override
    public void unbindView(SharedTrackItem.ViewHolder holder) {
        super.unbindView(holder);
        holder.title.setText(null);
        holder.description.setText(null);
        holder.votes.setText(null);
    }

    /**
     * Helper method to add track to party
     *
     * @param btn
     */
    public void addOrRemoveFromPartyPlaylist(ImageButton btn, Party party) {
        isAdded = !isAdded;
        if (isAdded) {
            getTrack().setPlaylistId(Config.DEFAULT_PARTY_PLAYLIST);
            party.addTrack(track);
            btn.setImageDrawable(btn.getContext().getResources().getDrawable(R.drawable.ic_playlist_add_check_grey_600_24dp));
        } else {
            party.removeTrack(track);
            btn.setImageDrawable(btn.getContext().getResources().getDrawable(R.drawable.ic_playlist_add_grey_600_24dp));
        }
    }


    /**
     * Helper method
     */
    public void addOrRemoveFromMyPlaylist() {
        isAdded = !isAdded;
        if (isAdded) {
            addToMyPlaylist();
        } else {
            removeFromMyPlaylist();
        }
    }

    /**
     * Helper method
     */
    public void addToMyPlaylist() {
        isAdded = true;
        Track t = Select.from(Track.class).where(Condition.prop(Track.PROP_TRACK_ID).eq(track.getTrackId())).first();
        if (t == null) {
            track.setPlaylistId(Config.DEFAULT_MY_PLAYLIST);
        }
        track.save();
    }

    /**
     * Helper method
     */
    public void removeFromMyPlaylist() {
        Track.deleteAll(Track.class, Track.PROP_TRACK_ID + "=?", track.getTrackId());
    }

    //The viewHolder used for this item. This viewHolder is always reused by the RecyclerView so scrolling is blazing fast
    public static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.track_title)
        public TextView title;

        @BindView(R.id.track_desc)
        public TextView description;

        @BindView(R.id.track_image)
        public ImageView imageView;

        @BindView(R.id.votes_count)
        public TextView votes;

        @BindView(R.id.menu_btn)
        public ImageButton imgBtn;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
