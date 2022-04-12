package com.project.tape.Fragments;

import static android.app.Activity.RESULT_OK;
import static com.project.tape.Activities.MainActivity.artistNameStr;
import static com.project.tape.Activities.MainActivity.searchOpenedInAlbumFragments;
import static com.project.tape.Activities.MainActivity.songNameStr;
import static com.project.tape.Activities.SongInfoTab.repeatBtnClicked;
import static com.project.tape.Adapters.AlbumsAdapter.mAlbum;
import static com.project.tape.Fragments.ArtistsFragment.fromArtistsFragment;
import static com.project.tape.Fragments.SongsFragment.albumList;
import static com.project.tape.Fragments.SongsFragment.albumName;
import static com.project.tape.Fragments.SongsFragment.previousAlbumName;

import android.app.ActivityOptions;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.project.tape.Activities.AboutFragmentItem;
import com.project.tape.Adapters.AlbumsAdapter;
import com.project.tape.R;
import com.project.tape.SecondaryClasses.Album;
import com.project.tape.SecondaryClasses.HeadsetActionButtonReceiver;
import com.project.tape.SecondaryClasses.MusicLoader;
import com.project.tape.SecondaryClasses.RecyclerItemClickListener;
import com.project.tape.SecondaryClasses.VerticalSpaceItemDecoration;

import java.util.ArrayList;
import java.util.List;


public class AlbumsFragment extends FragmentGeneral implements MediaPlayer.OnCompletionListener, LoaderManager.LoaderCallbacks<List<Album>> {


    TextView album_title_albumFragments;
    ImageView album_cover_albumFragment;
    LinearLayoutManager LLMAlbumFragment = new LinearLayoutManager(getContext());
    private Parcelable listState;
    public static RecyclerView myRecyclerView;

    int positionIndex, topView;
    final int REQUEST_CODE = 1;
    private static final int VERTICAL_ITEM_SPACE = 10;

    public static AlbumsAdapter albumsAdapter;

    private boolean oneTime = false;

    public static boolean fromAlbumsFragment;

    ArrayList<Album> albumLists = new ArrayList<>();


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v;
        v = inflater.inflate(R.layout.albums_fragment, container, false);
        //Booleans
        coverLoaded = false;

        //Init views
        album_title_albumFragments = (TextView) v.findViewById(R.id.album_title_albumFragment);
        myRecyclerView = (RecyclerView) v.findViewById(R.id.albums_recyclerview);
        album_cover_albumFragment = v.findViewById(R.id.album_cover_albumFragment);
        song_title_main = (TextView) getActivity().findViewById(R.id.song_title_main);
        artist_name_main = (TextView) getActivity().findViewById(R.id.artist_name_main);
        //Setting song title and artist name in infoTab
        album_cover_main = (ImageView) getActivity().findViewById(R.id.album_cover_main);
        mainPlayPauseBtn = (ImageButton) getActivity().findViewById(R.id.pause_button);
        song_title_main.setText(songNameStr);
        artist_name_main.setText(artistNameStr);

        //Sets adapter to list and applies settings to recyclerView
        albumsAdapter = new AlbumsAdapter(getContext(), albumLists);

        myRecyclerView.addItemDecoration(new VerticalSpaceItemDecoration(VERTICAL_ITEM_SPACE));
        myRecyclerView.setAdapter(albumsAdapter);
        myRecyclerView.setItemViewCacheSize(300);
        myRecyclerView.setDrawingCacheEnabled(true);
        myRecyclerView.setHasFixedSize(true);
        myRecyclerView.setLayoutManager(new WrapContentLinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));

        myRecyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(getActivity(), myRecyclerView ,new RecyclerItemClickListener.OnItemClickListener() {
                    @Override public void onItemClick(View view, int position) {
                        fromAlbumsFragment = true;
                        fromArtistsFragment = false;



                        Intent intent = new Intent(getActivity(), AboutFragmentItem.class);
                        Bundle bundle = ActivityOptions.makeSceneTransitionAnimation(getActivity()).toBundle();

                        if (searchOpenedInAlbumFragments) {
                            intent.putExtra("albumName", mAlbum.get(position).getAlbumName());
                        } else {
                            intent.putExtra("albumName", albumList.get(position).getAlbum());
                        }
                        getActivity().getSharedPreferences("previousAlbumName", Context.MODE_PRIVATE).edit()
                                .putString("albumName", albumName).commit();

                        if (oneTime) {
                            getActivity().unregisterReceiver(broadcastReceiver);
                        }

                       // getActivity().unregisterReceiver(audioSourceChangedReceiver);

                        startActivityForResult(intent, REQUEST_CODE, bundle);
                        //Sorting albumsFragment
                        //sortAlbumsList();
                    }

                    @Override public void onLongItemClick(View view, int position) {
                        // do whatever
                    }
                })

        );

        final LoaderManager supportLoaderManager = getActivity().getSupportLoaderManager();
        supportLoaderManager.initLoader(1, null, this);

        //Opens place where recyclerView has stopped
        if (listState != null) {
            myRecyclerView.getLayoutManager().onRestoreInstanceState(listState);
            listState = savedInstanceState.getParcelable("ListState");
        }
        return v;
    }

    public class WrapContentLinearLayoutManager extends LinearLayoutManager {

        public WrapContentLinearLayoutManager(Context context, int orientation, boolean reverseLayout) {
            super(context, orientation, reverseLayout);
        }

        @Override
        public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
            try {
                super.onLayoutChildren(recycler, state);
            } catch (IndexOutOfBoundsException e) {

            }
        }
    }


    public Loader<List<Album>> onCreateLoader(int id, Bundle args) {
        return new MusicLoader(getActivity());
    }

    @Override
    public void onLoadFinished(@NonNull Loader<List<Album>> loader, List<Album> data) {
        // Add the newly loaded music to adapter.
        albumsAdapter.addItems(data);
    }

    @Override
    public void onLoaderReset(Loader<List<Album>> loader) {
        // Clear the old music because a new list is going to be coming.
        albumsAdapter.clearItem();
    }

    //Saving place where recyclerView stopped
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable("ListState", myRecyclerView.getLayoutManager().onSaveInstanceState());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_CODE:
                    songNameStr = data.getStringExtra("titleToMain");
                    artistNameStr = data.getStringExtra("ArtistNameToMain");
                    previousAlbumName = data.getStringExtra("previousAlbumName");
                    getActivity().getSharedPreferences("previousAlbumName", Context.MODE_PRIVATE).edit()
                            .putString("previousAlbumName", previousAlbumName);
                    break;
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        createChannel();
        trackAudioSource();

        myRecyclerView.swapAdapter(albumsAdapter, false);

        //Register headphones buttons
        HeadsetActionButtonReceiver.delegate = this;
        HeadsetActionButtonReceiver.register(getActivity());

        song_title_main.setText(songNameStr);
        artist_name_main.setText(artistNameStr);
        if (mediaPlayer != null) {
            if (!coverLoaded) {
                if (uri != null) {
                    metaDataInFragment(uri);
                    coverLoaded = true;
                }
            }

            if (mediaPlayer.isPlaying()) {
                mainPlayPauseBtn.setImageResource(R.drawable.pause_song);
            } else {
                mainPlayPauseBtn.setImageResource(R.drawable.play_song);
            }
        } else {
            song_title_main.setText(" ");
            artist_name_main.setText(" ");
        }
        mediaPlayer.setOnCompletionListener(AlbumsFragment.this);
    }

    @Override
    public void onPause() {
        super.onPause();
        //Checking is screen locked
        KeyguardManager myKM = (KeyguardManager) getActivity().getSystemService(Context.KEYGUARD_SERVICE);
        if (myKM.inKeyguardRestrictedInputMode()) {
            //if locked
        } else {
            getActivity().unregisterReceiver(broadcastReceiver);
        }

        positionIndex = LLMAlbumFragment.findFirstVisibleItemPosition();
        View startView = myRecyclerView.getChildAt(0);
        topView = (startView == null) ? 0 : (startView.getTop() - myRecyclerView.getPaddingTop());

        if (repeatBtnClicked) {
            getContext().getSharedPreferences("repeatBtnClicked", Context.MODE_PRIVATE).edit()
                    .putBoolean("repeatBtnClicked", true).commit();
        } else {
            getContext().getSharedPreferences("repeatBtnClicked", Context.MODE_PRIVATE).edit()
                    .putBoolean("repeatBtnClicked", false).commit();
        }
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        onTrackNext();
        mediaPlayer.setOnCompletionListener(AlbumsFragment.this);
    }


    @Override
    public void onMediaButtonSingleClick() {
        if (isPlaying) {
            onTrackPause();
        } else {
            onTrackPlay();
        }
    }

    @Override
    public void onMediaButtonDoubleClick() {
    }


}

