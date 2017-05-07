package com.tinglabs.silent.party.ui.items;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.mikepenz.fastadapter.items.AbstractItem;
import com.tinglabs.silent.party.model.Party;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import com.tinglabs.silent.party.R;

/**
 * Created by Talal on 1/5/2017.
 */

public class FindPartyItem extends AbstractItem<FindPartyItem, FindPartyItem.ViewHolder> {

    private Party party;

    public FindPartyItem(Party party) {
        this.party = party;
    }

    public Party getParty() {
        return party;
    }

    public void setParty(Party party) {
        this.party = party;
    }

    @Override
    public int getType() {
        return R.id.find_party_item;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.fragment_find_party_item;
    }

    @Override
    public void bindView(FindPartyItem.ViewHolder viewHolder, List<Object> payloads) {
        super.bindView(viewHolder, payloads);
        viewHolder.name.setText(party.getName());
        viewHolder.description.setText("Now " + party.getStatus() + "!");
    }

    //reset the view here (this is an optional method, but recommended)
    @Override
    public void unbindView(FindPartyItem.ViewHolder holder) {
        super.unbindView(holder);
        holder.name.setText(null);
        holder.description.setText(null);
    }

    //The viewHolder used for this item. This viewHolder is always reused by the RecyclerView so scrolling is blazing fast
    public static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.current_party_name)
        public TextView name;

        @BindView(R.id.party_desc)
        public TextView description;

        @BindView(R.id.menu_btn)
        public ImageButton menuBtn;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
