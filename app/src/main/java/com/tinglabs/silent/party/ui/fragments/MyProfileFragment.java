package com.tinglabs.silent.party.ui.fragments;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;

import com.tinglabs.silent.party.conf.Config;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import com.tinglabs.silent.party.R;
import com.tinglabs.silent.party.api.UserAPI;
import com.tinglabs.silent.party.event.UserEventListener;
import com.tinglabs.silent.party.model.User;
import com.tinglabs.silent.party.ui.interfaces.ViewController;
import com.tinglabs.silent.party.util.EventManager;


/**
 * Created by Talal on 11/27/2016.
 */

public class MyProfileFragment extends Fragment implements UserEventListener {
    public static final String TAG = "MyProfileFragment";

    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 1888;

    @BindView(R.id.user_profile_name)
    EditText mName;
    @BindView(R.id.user_profile_short_bio)
    EditText mBio;
    @BindView(R.id.user_profile_email)
    EditText mEmail;
    @BindView(R.id.user_profile_nick)
    EditText mNick;
    @BindView(R.id.user_profile_photo)
    CircleImageView mPhoto;

    @BindView(R.id.update_profile_btn)
    Button mUpdateBtn;
    @BindView(R.id.profile_main_layout)
    FrameLayout mLayout;

    private User mUser;

    public MyProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.fragment_my_profile_bar_menu, menu);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_my_profile, container, false);
        ButterKnife.bind(this, view);
        // Don't need player here
        ((ViewController) getActivity()).hidePlayer();
        // Disable edit
        disableEdit();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        EventManager.register(this);

        // Retrieve current user
        ((UserAPI) getActivity()).loadUser();
        ((ViewController) getActivity()).startLoader("Loading profile...");
    }

    @Override
    public void onPause() {
        super.onPause();
        EventManager.unregister(this);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_edit_profile:
                enableEdit();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                // Get data
                Bitmap bmp = (Bitmap) data.getExtras().get("data");
                ByteArrayOutputStream stream = new ByteArrayOutputStream();

                bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byte[] byteArray = stream.toByteArray();

                // convert byte array to Bitmap
                Bitmap bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
                // Set imageview
                mPhoto.setImageBitmap(bitmap);
                // Store
                try {
                    File dir = new File(getActivity().getExternalFilesDir(null) + "/" + Config.APP_DIR_NAME);
                    dir.mkdirs();
                    File file = new File(dir, mUser.getUserName() + ".png");
                    if (file.exists()) file.delete();
                    file.createNewFile();
                    FileOutputStream fo = new FileOutputStream(file);
                    fo.write(byteArray);
                    fo.flush();
                    fo.close();
                    mUser.setPicUrl(Uri.fromFile(file).toString());
                    ((UserAPI) getActivity()).updateUser(mUser);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void userCreated(User user) {
    }

    @Override
    public void userUpdated(User user) {

    }

    @Override
    public void userLoaded(User user) {
        mUser = user;
        if (mUser.getName() != null) mName.setText(mUser.getName());
        if (mUser.getDescription() != null) mBio.setText(mUser.getDescription());
        if (mUser.getEmail() != null) mEmail.setText(mUser.getEmail());
        if (mUser.getUserName() != null) mNick.setText(mUser.getUserName());
        if (mUser.getPicUrl() != null) mPhoto.setImageURI(Uri.parse(mUser.getPicUrl()));
    }

    @Override
    public void onComplete(String action) {
        ((ViewController) getActivity()).stopLoader();
    }

    @Override
    public void onError(String action, String reason) {

    }

    @OnClick(R.id.take_photo_btn)
    public void takePhoto() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
    }

    @OnClick(R.id.update_profile_btn)
    public void update() {
        disableEdit();
        mUser.setName(mName.getText().toString());
        mUser.setDescription(mBio.getText().toString());
        mUser.setEmail(mEmail.getText().toString());
        mUser.setUserName(mNick.getText().toString());
        ((UserAPI) getActivity()).updateUser(mUser);
    }

    private void enableEdit() {
        mName.setEnabled(true);
        mBio.setEnabled(true);
        mEmail.setEnabled(true);
        mNick.setEnabled(true);
        mUpdateBtn.setVisibility(View.VISIBLE);
    }

    private void disableEdit() {
        mName.setEnabled(false);
        mBio.setEnabled(false);
        mEmail.setEnabled(false);
        mNick.setEnabled(false);
        mUpdateBtn.setVisibility(View.GONE);
    }
}
