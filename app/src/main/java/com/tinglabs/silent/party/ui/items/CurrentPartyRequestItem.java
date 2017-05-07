package com.tinglabs.silent.party.ui.items;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.mikepenz.fastadapter.items.AbstractItem;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import com.tinglabs.silent.party.R;
import com.tinglabs.silent.party.model.comm.AddTrackRequest;

/**
 * Created by Talal on 1/5/2017.
 */

public class CurrentPartyRequestItem extends AbstractItem<CurrentPartyRequestItem, CurrentPartyRequestItem.ViewHolder> {

    private AddTrackRequest request;

    public CurrentPartyRequestItem(AddTrackRequest request) {
        this.request = request;
    }

    public AddTrackRequest getRequest() {
        return request;
    }

    public void setRequest(AddTrackRequest request) {
        this.request = request;
    }

    @Override
    public int getType() {
        return R.id.current_party_member_item;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.fragment_current_party_members_tab_item;
    }

    @Override
    public void bindView(CurrentPartyRequestItem.ViewHolder viewHolder, List<Object> payloads) {
        super.bindView(viewHolder, payloads);
        viewHolder.title.setText("Track: " + request.getTrack().getTitle());
        viewHolder.description.setText("Request from: " + request.getUser().getName());
    }

    //reset the view here (this is an optional method, but recommended)
    @Override
    public void unbindView(CurrentPartyRequestItem.ViewHolder holder) {
        super.unbindView(holder);
        holder.title.setText(null);
        holder.description.setText(null);
    }

    //The viewHolder used for this item. This viewHolder is always reused by the RecyclerView so scrolling is blazing fast
    public static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.req_title)
        public TextView title;

        @BindView(R.id.req_description)
        public TextView description;

        @BindView(R.id.menu_btn)
        public ImageButton menuBtn;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
