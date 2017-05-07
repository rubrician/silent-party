package com.tinglabs.silent.party.ui.items;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.mikepenz.fastadapter.items.AbstractItem;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import com.tinglabs.silent.party.R;
import com.tinglabs.silent.party.model.User;

/**
 * Created by Talal on 1/5/2017.
 */

public class CurrentPartyMemberItem extends AbstractItem<CurrentPartyMemberItem, CurrentPartyMemberItem.ViewHolder> {

    private User user;
    private Mode mode = Mode.MENU;

    public enum Mode {
        MENU,
        NONE
    }

    public CurrentPartyMemberItem(User user) {
        this.user = user;
    }

    public CurrentPartyMemberItem(User user, Mode mode) {
        this.user = user;
        this.mode = mode;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
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
    public void bindView(CurrentPartyMemberItem.ViewHolder viewHolder, List<Object> payloads) {
        super.bindView(viewHolder, payloads);
        //get the context
        Context ctx = viewHolder.itemView.getContext();

        viewHolder.nick.setText(user.getUserName());
        viewHolder.name.setText(user.getName());

        switch (mode) {
            case MENU:
                viewHolder.menuBtn.setImageDrawable(ctx.getResources().getDrawable(R.drawable.ic_menu_grey_600_24dp));
                break;
            case NONE:
                viewHolder.menuBtn.setVisibility(View.GONE);
        }
    }

    //reset the view here (this is an optional method, but recommended)
    @Override
    public void unbindView(CurrentPartyMemberItem.ViewHolder holder) {
        super.unbindView(holder);
        holder.nick.setText(null);
        holder.name.setText(null);
    }

    //The viewHolder used for this item. This viewHolder is always reused by the RecyclerView so scrolling is blazing fast
    public static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.user_image)
        ImageView img;

        @BindView(R.id.user_nick)
        public TextView nick;

        @BindView(R.id.user_full_name)
        public TextView name;

        @BindView(R.id.menu_btn)
        public ImageButton menuBtn;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}